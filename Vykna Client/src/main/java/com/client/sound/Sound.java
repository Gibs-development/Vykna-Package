package com.client.sound;

import com.client.sign.Signlink;

import javax.sound.sampled.*;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Sound {

    // TODO
    // https://pastebin.com/vFkVrAGZ
    // store sound files as they come in (~100 max)

    private static final Sound SINGLETON = new Sound();
    // Cached pool avoids queueing long-running sound playback.
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    // Basic debug (set to false once you're happy)
    private static final boolean DEBUG = true;

    public static Sound getSound() {
        return SINGLETON;
    }

    private static File getSound(int id) {
        return new File(Signlink.getCacheDirectory() + "sounds/game/" + id + ".wav");
    }


    public Sound() {
    }

    public void playSound(int id, SoundType soundType, double distanceFromOrigin) {
        if (executor.isShutdown()) {
            return;
        }

        File f = getSound(id);
        if (!f.exists()) {
            if (DEBUG) {
                System.out.println("[Sound] Missing wav for id=" + id + " at " + f.getAbsolutePath());
            }
            return;
        }

        // Clamp distance to avoid negative/huge values
        final double distance = Math.max(0, distanceFromOrigin);
        executor.submit(() -> {
            try {
                if (DEBUG) {
                    System.out.println("[Sound] Play id=" + id + " type=" + soundType + " dist=" + distance + " file=" + f.getName());
                }
                playClip(f, soundType, distance);
            } catch (Throwable t) {
                // Don't kill the executor; just log.
                System.err.println("[Sound] Failed playing id=" + id + " file=" + f.getAbsolutePath());
                t.printStackTrace();
            }
        });
    }

    public float calculateVolume(SoundType soundType, double distanceFromOrigin) {
        double distanceVolume = (12d - distanceFromOrigin) / 12d; // 0.0-1.0
        if (distanceVolume < 0d) {
            distanceVolume = 0d;
        } else if (distanceVolume > 1d) {
            distanceVolume = 1d;
        }
        double soundVolume = (soundType.getVolume() / 10d);
        if (soundVolume < 0d) {
            soundVolume = 0d;
        } else if (soundVolume > 1d) {
            soundVolume = 1d;
        }
        return (float) (soundVolume * distanceVolume);
    }

    private void playClip(File soundFile, SoundType soundType, double distanceFromOrigin) throws Exception {
        try (AudioInputStream in = AudioSystem.getAudioInputStream(soundFile)) {
            AudioFormat baseFormat = in.getFormat();
            AudioFormat decoded = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false
            );
            try (AudioInputStream din = AudioSystem.getAudioInputStream(decoded, in)) {
                DataLine.Info info = new DataLine.Info(Clip.class, decoded);
                Clip clip = (Clip) AudioSystem.getLine(info);
                clip.open(din);
                applyVolume(clip, soundType, distanceFromOrigin);
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP || event.getType() == LineEvent.Type.CLOSE) {
                        clip.close();
                    }
                });
                clip.start();
            }
        }
    }

    private void applyVolume(Clip clip, SoundType soundType, double distanceFromOrigin) {
        float volume = calculateVolume(soundType, distanceFromOrigin);
        if (volume <= 0f) {
            clip.stop();
            clip.close();
            return;
        }
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log10(Math.max(0.0001f, volume)) * 20.0);
            dB = Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), dB));
            gain.setValue(dB);
        }
        if (clip.isControlSupported(BooleanControl.Type.MUTE)) {
            BooleanControl mute = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
            mute.setValue(volume <= 0.001f);
        }
    }
}
