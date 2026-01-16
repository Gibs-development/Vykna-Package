package io.xeros.content.combat.wrath;

import io.xeros.model.entity.player.Player;

public final class ChannelBarSender {

    private ChannelBarSender() {}

    public static final int OPCODE_CHANNEL_BAR = 252;

    public static void start(Player p, int type, int durationMs) {
        if (p == null || p.getOutStream() == null) return;

        // clamp to u16 (0..65535)
        if (durationMs < 0) durationMs = 0;
        if (durationMs > 65535) durationMs = 65535;

        p.getOutStream().createFrame(OPCODE_CHANNEL_BAR);
        p.getOutStream().writeByte(type);
        p.getOutStream().writeWord(durationMs);
    }

    public static void clear(Player p) {
        start(p, 0, 0);
    }
}
