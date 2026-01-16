package io.xeros.content.combat.wrath;

import io.xeros.model.entity.player.Player;

/**
 * Sends Wrath updates to the client.
 *
 * Client opcode: 250
 * Payload: [wrath:u8]  (self-only)
 *
 * IMPORTANT:
 *  - We intentionally keep this self-only to avoid player index desync issues.
 *  - Other players won't see your Wrath bar until we implement a safe indexed update.
 */
public final class WrathPacketSender {

    private WrathPacketSender() {}

    public static final int OPCODE_WRATH_UPDATE = 2500; // MUST match client

    // Attribute keys
    private static final String ATTR_LAST_SENT = "wrath.lastSent";

    /**
     * Sends current Wrath to the same player, ONLY if it changed since the last send.
     */
    public static void sendSelfIfChanged(Player player, int wrath0to100) {
        if (player == null || player.getOutStream() == null) return;

        int w = clamp(wrath0to100);

        int last = player.getAttributes().getInt(ATTR_LAST_SENT, -1);
        if (last == w) {
            return; // don't spam packets
        }
        player.getAttributes().setInt(ATTR_LAST_SENT, w);

        player.getOutStream().createFrame(OPCODE_WRATH_UPDATE);
        player.getOutStream().writeByte(w);
        // Do NOT flush here; the normal update loop flushes.
    }

    public static int clamp(int w) {
        if (w < 0) return 0;
        if (w > WrathSystem.MAX_WRATH) return WrathSystem.MAX_WRATH;
        return w;
    }
}
