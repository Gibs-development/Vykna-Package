package io.xeros.content.cutscene;

import io.xeros.model.entity.player.Player;

public final class CutscenePackets {
    private CutscenePackets() {}

    public static void moveCameraAbs(Player p, int absX, int absY, int height, int speed, int accelOrInstant) {
        int lx = clamp(CutsceneTiles.localX(p, absX));
        int ly = clamp(CutsceneTiles.localY(p, absY));

        var out = p.getOutStream();
        out.createFrame(166);
        out.writeByte(lx);
        out.writeByte(ly);
        out.writeWord(height);
        out.writeByte(speed);
        out.writeByte(accelOrInstant);
        p.flushOutStream();
    }

    public static void lookAtAbs(Player p, int absX, int absY, int height, int speed, int accel) {
        int lx = clamp(CutsceneTiles.localX(p, absX));
        int ly = clamp(CutsceneTiles.localY(p, absY));

        // stillCamera divides by 64, so give it tile*64
        p.getPA().stillCamera(lx * 64, ly * 64, height, speed, accel);
    }

    public static void resetCamera(Player p) {
        var out = p.getOutStream();
        out.createFrame(107);
        p.flushOutStream();
    }

    private static int clamp(int v) {
        if (v < 0) return 0;
        if (v > 103) return 103;
        return v;
    }
}
