package com.client.sound;

import javax.sound.midi.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;

/**
 * Very small MIDI player for the 317-style cache index3 music tracks.
 *
 * Goal for v1:
 *  - Play the decompressed MIDI bytes the moment they arrive from the on-demand fetcher
 *  - Never crash the game if the host machine has no MIDI device
 *  - Allow changing volume at runtime
 *
 * This intentionally does NOT try to perfectly emulate RuneScape's original MIDI synth yet.
 * It's a solid foundation so you can later add:
 *  - SoundFont (SF2) support for authentic instruments
 *  - Fade in/out and crossfades (songChanging)
 *  - Jingles + queued tracks
 */
public final class MidiPlayer {

    private static final MidiPlayer INSTANCE = new MidiPlayer();

    public static MidiPlayer get() {
        return INSTANCE;
    }

    private Sequencer sequencer;
    private Synthesizer synth;
    private SourceDataLine audioLine;
    private Receiver synthReceiver;
    private AudioInputStream synthStream;
    private Thread audioPumpThread;
    private int currentTrackId = -1;
    private int volume0To255 = 255;
    private boolean deviceInfoLogged = false;
    private long lastPlayDebugAt = 0L;
    private int lastPlayDebugTrackId = -1;
    private int noteOnCount = 0;
    private int loggedNoteOns = 0;

    private MidiPlayer() {}

    private void ensureOpen() throws MidiUnavailableException {
        boolean synthReady = false;
        try {
            if (synth == null || !synth.isOpen()) {
                synth = MidiSystem.getSynthesizer();
                boolean openedWithStream = openSynthWithStreamIfPossible(synth);
                if (!openedWithStream) {
                    boolean openedWithLine = openSynthWithLineIfPossible(synth);
                    if (!openedWithLine) {
                        openSynthWithGainIfSupported(synth);
                    }
                }
                if (!synth.isOpen()) {
                    openSynthWithGainIfSupported(synth);
                }
                loadDefaultSoundbankIfMissing(synth);
            }
            synthReady = synth != null && synth.isOpen();
        } catch (Throwable t) {
            System.out.println("[MidiPlayer] Synth unavailable, will try sequencer-only path. err=" + t);
            synth = null;
        }

        if (sequencer == null || !sequencer.isOpen()) {
            if (synthReady) {
                // false = don't auto-connect to default synth, we wire it ourselves
                sequencer = MidiSystem.getSequencer(false);
                sequencer.open();

                // Route sequencer -> synth
                Transmitter transmitter = sequencer.getTransmitter();
                synthReceiver = synth.getReceiver();
                transmitter.setReceiver(new CountingReceiver(synthReceiver));
                System.out.println("[MidiPlayer] Sequencer routed to synth receiver=" + synthReceiver.getClass().getSimpleName());
            } else {
                // Fallback: let the system pick whatever default device is available.
                sequencer = MidiSystem.getSequencer(true);
                sequencer.open();
            }
        }

        if (!deviceInfoLogged) {
            deviceInfoLogged = true;
            try {
                String synthName = synth != null ? synth.getDeviceInfo().getName() : "none";
                String seqName = sequencer != null ? sequencer.getDeviceInfo().getName() : "none";
                int instruments = synth != null ? synth.getAvailableInstruments().length : 0;
                System.out.println("[MidiPlayer] Devices: synth=" + synthName + " instruments=" + instruments + " sequencer=" + seqName);
            } catch (Throwable ignored) {}
        }
    }

    /**
     * Play a MIDI track from bytes.
     *
     * @param trackId track id (cache file id) purely for "current track" bookkeeping
     * @param midiBytes decompressed .mid bytes (usually starts with "MThd")
     * @param loop true to loop forever
     * @param volume0To255 client music volume (0..255)
     */
    public synchronized void play(int trackId, byte[] midiBytes, boolean loop, int volume0To255) {
        if (midiBytes == null || midiBytes.length == 0) {
            return;
        }

        try {
            ensureOpen();

            Sequence sequence = MidiSystem.getSequence(new ByteArrayInputStream(midiBytes));
            try {
                int trackCount = sequence.getTracks() != null ? sequence.getTracks().length : 0;
                long tickLen = sequence.getTickLength();
                long microLen = sequence.getMicrosecondLength();
                if (trackCount == 0 || tickLen == 0) {
                    System.out.println("[MidiPlayer] Sequence loaded but empty? trackId=" + trackId
                            + " tracks=" + trackCount + " ticks=" + tickLen + " micros=" + microLen);
                }
            } catch (Throwable ignored) {}
            sequencer.stop();
            sequencer.setSequence(sequence);

            this.currentTrackId = trackId;
            this.noteOnCount = 0;
            this.loggedNoteOns = 0;
            resetSynthChannels();
            setVolume(volume0To255);

            sequencer.setLoopCount(loop ? Sequencer.LOOP_CONTINUOUSLY : 0);
            sequencer.setTickPosition(0);
            try {
                sequencer.setTempoFactor(1.0f);
                if (sequence.getTickLength() > 0) {
                    sequencer.setLoopStartPoint(0);
                    sequencer.setLoopEndPoint(sequence.getTickLength());
                }
            } catch (Throwable ignored) {}
            sequencer.start();
            // Re-apply volume shortly after start in case the sequence sets volume to 0.
            scheduleVolumeBump(trackId, 250L);
            scheduleVolumeBump(trackId, 1000L);
            debugPlayState(trackId);
        } catch (Throwable t) {
            // Never let audio break the client.
            // Common reasons:
            //  - No MIDI synthesizer available
            //  - Headless / minimal JRE
            //  - Broken OS audio device
            System.out.println("[MidiPlayer] play failed trackId=" + trackId + " err=" + t);
            stop();
        }
    }

    public synchronized void stop() {
        try {
            if (sequencer != null) {
                sequencer.stop();
            }
        } catch (Throwable ignored) {}
        try {
            if (audioLine != null) {
                audioLine.stop();
                audioLine.close();
            }
        } catch (Throwable ignored) {}
        try {
            if (synthStream != null) {
                synthStream.close();
            }
        } catch (Throwable ignored) {}
        synthStream = null;
        audioLine = null;
        if (audioPumpThread != null) {
            audioPumpThread.interrupt();
        }
        audioPumpThread = null;
        currentTrackId = -1;
    }

    /**
     * Set volume at runtime.
     * 0..255 will be mapped to MIDI CC 7 range 0..127.
     */
    public synchronized void setVolume(int volume0To255) {
        this.volume0To255 = Math.max(0, Math.min(255, volume0To255));
        int vol = (this.volume0To255 * 127) / 255;

        try {
            if (synth != null && synth.isOpen()) {
                MidiChannel[] channels = synth.getChannels();
                if (channels != null) {
                    for (MidiChannel ch : channels) {
                        if (ch != null) {
                            ch.setMute(false);
                            ch.setSolo(false);
                            ch.controlChange(7, vol); // Channel volume
                            ch.controlChange(11, vol); // Expression (helps on some synths)
                        }
                    }
                }
            }
        } catch (Throwable ignored) {}
    }

    public synchronized int getCurrentTrackId() {
        return currentTrackId;
    }

    private void debugPlayState(int trackId) {
        long now = System.currentTimeMillis();
        if (trackId == lastPlayDebugTrackId && now - lastPlayDebugAt < 5000L) {
            return;
        }
        lastPlayDebugTrackId = trackId;
        lastPlayDebugAt = now;
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException ignored) {
                return;
            }
            synchronized (MidiPlayer.this) {
                if (currentTrackId != trackId) {
                    return;
                }
                try {
                    boolean running = sequencer != null && sequencer.isRunning();
                    long pos = sequencer != null ? sequencer.getMicrosecondPosition() : -1L;
                    long len = sequencer != null ? sequencer.getMicrosecondLength() : -1L;
                    System.out.println("[MidiPlayer] debug trackId=" + trackId + " running=" + running
                            + " pos=" + pos + " len=" + len + " noteOn=" + noteOnCount);
                } catch (Throwable ignored) {}
            }
        }, "MidiDebug");
        t.setDaemon(true);
        t.start();
    }

    private void scheduleVolumeBump(int trackId, long delayMs) {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException ignored) {
                return;
            }
            synchronized (MidiPlayer.this) {
                if (currentTrackId != trackId) {
                    return;
                }
                setVolume(volume0To255);
                System.out.println("[MidiPlayer] Volume bump at +" + delayMs + "ms for trackId=" + trackId);
            }
        }, "MidiVolBump");
        t.setDaemon(true);
        t.start();
    }

    private void resetSynthChannels() {
        try {
            if (synthReceiver == null) {
                return;
            }
            // GM Reset SysEx: F0 7E 7F 09 01 F7
            byte[] gmReset = new byte[] { (byte) 0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte) 0xF7 };
            synthReceiver.send(new SysexMessage(gmReset, gmReset.length), -1);

            // Reset all controllers + all notes off on each channel.
            for (int ch = 0; ch < 16; ch++) {
                ShortMessage reset = new ShortMessage();
                reset.setMessage(ShortMessage.CONTROL_CHANGE, ch, 121, 0);
                synthReceiver.send(reset, -1);

                ShortMessage allOff = new ShortMessage();
                allOff.setMessage(ShortMessage.CONTROL_CHANGE, ch, 123, 0);
                synthReceiver.send(allOff, -1);
            }
        } catch (Throwable t) {
            System.out.println("[MidiPlayer] resetSynthChannels failed: " + t);
        }
    }

    private void openSynthWithGainIfSupported(Synthesizer synth) throws MidiUnavailableException {
        try {
            // SoftSynthesizer supports open(Map) with gain settings (Java 8).
            java.lang.reflect.Method openWithMap = synth.getClass().getMethod("open", java.util.Map.class);
            java.util.Map<String, Object> props = new java.util.HashMap<>();
            props.put("javax.sound.midi.softsynth.gain", 0.8f);
            openWithMap.invoke(synth, props);
            return;
        } catch (NoSuchMethodException ignored) {
            // Fall through to default open().
        } catch (Throwable t) {
            System.out.println("[MidiPlayer] open(Map) failed, falling back. err=" + t);
        }
        synth.open();
    }

    private boolean openSynthWithLineIfPossible(Synthesizer synth) {
        try {
            Class<?> audioSynthClass = Class.forName("com.sun.media.sound.AudioSynthesizer");
            if (!audioSynthClass.isInstance(synth)) {
                return false;
            }
            AudioFormat format = new AudioFormat(44100f, 16, 2, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format, 16384);
            line.start();

            java.lang.reflect.Method openWithLine = audioSynthClass.getMethod("open", SourceDataLine.class, java.util.Map.class);
            java.util.Map<String, Object> props = new java.util.HashMap<>();
            props.put("javax.sound.midi.softsynth.gain", 1.0f);
            openWithLine.invoke(synth, line, props);

            audioLine = line;
            System.out.println("[MidiPlayer] AudioSynthesizer opened with SourceDataLine.");
            return true;
        } catch (Throwable t) {
            System.out.println("[MidiPlayer] AudioSynthesizer line open failed: " + t);
            return false;
        }
    }

    private boolean openSynthWithStreamIfPossible(Synthesizer synth) {
        try {
            Class<?> audioSynthClass = Class.forName("com.sun.media.sound.AudioSynthesizer");
            if (!audioSynthClass.isInstance(synth)) {
                return false;
            }
            AudioFormat format = new AudioFormat(44100f, 16, 2, true, false);
            java.lang.reflect.Method openStream = audioSynthClass.getMethod("openStream", AudioFormat.class, java.util.Map.class);
            java.util.Map<String, Object> props = new java.util.HashMap<>();
            props.put("javax.sound.midi.softsynth.gain", 1.0f);
            Object streamObj = openStream.invoke(synth, format, props);
            if (!(streamObj instanceof AudioInputStream)) {
                return false;
            }
            synthStream = (AudioInputStream) streamObj;

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format, 16384);
            line.start();
            audioLine = line;

            startAudioPump();
            System.out.println("[MidiPlayer] AudioSynthesizer opened with stream pump.");
            return true;
        } catch (Throwable t) {
            System.out.println("[MidiPlayer] AudioSynthesizer stream open failed: " + t);
            return false;
        }
    }

    private void startAudioPump() {
        if (synthStream == null || audioLine == null || (audioPumpThread != null && audioPumpThread.isAlive())) {
            return;
        }
        audioPumpThread = new Thread(() -> {
            byte[] buf = new byte[4096];
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    int read = synthStream.read(buf, 0, buf.length);
                    if (read == -1) {
                        break;
                    }
                    if (read > 0) {
                        audioLine.write(buf, 0, read);
                    }
                }
            } catch (Throwable ignored) {
            }
        }, "MidiAudioPump");
        audioPumpThread.setDaemon(true);
        audioPumpThread.start();
    }

    private void playTestToneOnce() {
        // test tone removed
    }

    private final class CountingReceiver implements Receiver {
        private final Receiver delegate;

        private CountingReceiver(Receiver delegate) {
            this.delegate = delegate;
        }

        @Override
        public void send(MidiMessage message, long timeStamp) {
            if (message instanceof ShortMessage) {
                ShortMessage sm = (ShortMessage) message;
                int cmd = sm.getCommand();
                if (cmd == ShortMessage.NOTE_ON && sm.getData2() > 0) {
                    noteOnCount++;
                    if (loggedNoteOns < 5) {
                        loggedNoteOns++;
                        System.out.println("[MidiPlayer] NOTE_ON ch=" + sm.getChannel()
                                + " note=" + sm.getData1() + " vel=" + sm.getData2());
                    }
                } else if (cmd == ShortMessage.CONTROL_CHANGE) {
                    int ctrl = sm.getData1();
                    if (ctrl == 7 || ctrl == 11) {
                        int desired = (volume0To255 * 127) / 255;
                        int val = sm.getData2();
                        if (val == 0 && desired > 0) {
                            try {
                                sm.setMessage(ShortMessage.CONTROL_CHANGE, sm.getChannel(), ctrl, desired);
                            } catch (Throwable ignored) {}
                        }
                    }
                }
            }
            if (delegate != null) {
                delegate.send(message, timeStamp);
            }
        }

        @Override
        public void close() {
            if (delegate != null) {
                delegate.close();
            }
        }
    }

    private void loadDefaultSoundbankIfMissing(Synthesizer synth) {
        try {
            if (synth == null || !synth.isOpen()) {
                return;
            }
            boolean hasInstruments = synth.getAvailableInstruments() != null
                    && synth.getAvailableInstruments().length > 0;
            if (hasInstruments) {
                return;
            }
            Soundbank defaultBank = synth.getDefaultSoundbank();
            if (defaultBank != null) {
                boolean loaded = synth.loadAllInstruments(defaultBank);
                System.out.println("[MidiPlayer] Loaded default soundbank: " + loaded);
                return;
            }

            String javaHome = System.getProperty("java.home");
            if (javaHome == null) {
                return;
            }
            File gm = new File(javaHome, "lib/audio/soundbank.gm");
            File gm2 = new File(javaHome, "lib/audio/soundbank-deluxe.gm");
            File bankFile = gm.exists() ? gm : (gm2.exists() ? gm2 : null);
            if (bankFile != null) {
                Soundbank fileBank = MidiSystem.getSoundbank(bankFile);
                boolean loaded = synth.loadAllInstruments(fileBank);
                System.out.println("[MidiPlayer] Loaded soundbank from " + bankFile.getName() + " ok=" + loaded);
            } else {
                System.out.println("[MidiPlayer] No default soundbank available (java.home=" + javaHome + ").");
            }
        } catch (Throwable t) {
            System.out.println("[MidiPlayer] Soundbank load failed: " + t);
        }
    }
}
