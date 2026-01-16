package io.xeros.content.combat.wrath;

import io.xeros.content.sound.Sfx;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

/**
 * Minimal + stable Wrath implementation.
 *
 * Goals:
 *  - Always clamp Wrath to 0..100
 *  - Never block combat flow
 *  - Never spam chat
 *  - Send client updates only when value changes
 *
 * NOTE:
 *  Finishers and exotic effects can be layered on once the bar is stable.
 */
public final class WrathSystem {

    private WrathSystem() {}

    public static final int MAX_WRATH = 100;
    public static final int READY_AT  = 80;

    private static final long[] LAST_COMBAT_MS = new long[2048];
    private static final long[] LAST_DECAY_MS = new long[2048];
    // Attribute keys
    private static final String ATTR_WRATH_VALUE = "wrath.value";
    private static final String ATTR_READY_STATE = "wrath.readyState"; // 0/1
    private static final String ATTR_READY_MSG_TS = "wrath.readyMsgTs"; // millis

    /** Read current Wrath (0..100). */
    public static int getWrath(Player p) {
        if (p == null) return 0;
        int w = p.getAttributes().getInt(ATTR_WRATH_VALUE, 0);
        return clamp(w);
    }

    /** Set Wrath (clamped) and push to client if changed. */
    public static void setWrath(Player p, int newWrath) {
        if (p == null) return;

        int old = getWrath(p);
        int w = clamp(newWrath);

        if (old == w) {
            // Still make sure the client isn't starved if lastSent got reset
     //       WrathPacketSender.sendSelfIfChanged(p, w);
            return;
        }

        p.getAttributes().setInt(ATTR_WRATH_VALUE, w);
     //   WrathPacketSender.sendSelfIfChanged(p, w);

        handleReadyState(p, old, w);
    }

    public static void markInCombat(Player p) {
        int idx = p.getIndex();
        if (idx < 0 || idx >= LAST_COMBAT_MS.length) return;
        LAST_COMBAT_MS[idx] = System.currentTimeMillis();
    }

//    public static void tick(Player p) {
//        if (p == null) return;
//        int idx = p.getIndex();
//        if (idx < 0 || idx >= LAST_COMBAT_MS.length) return;
//
//        int w = getWrath(p);
//        if (w <= 0) return;
//
//        long now = System.currentTimeMillis();
//        long sinceCombat = now - LAST_COMBAT_MS[idx];
//
//        // Start decaying after 8 seconds out of combat
//        if (sinceCombat < 8000) return;
//
//        // Decay 2 wrath per second
//        if (now - LAST_DECAY_MS[idx] < 500) return;
//        LAST_DECAY_MS[idx] = now;
//
//        subtractWrath(p, 2);
//    }



    /** Add Wrath (positive or negative). */
    public static void addWrath(Player p, int delta) {
        if (p == null || delta == 0) return;
        setWrath(p, getWrath(p) + delta);
    }

    /**
     * Call when a player deals damage.
     * Works for NPC or Player targets, but we mainly tune it for PvM.
     */
    public static void onPlayerDealtDamage(Player attacker, Entity target, int damage) {
        if (attacker == null || target == null) return;

        // No wrath gain when not in combat contexts you want? Keep simple for now.
        int gain;
        if (damage <= 0) {
            gain = 1;
        } else {
            // Smooth, bounded scaling: 2..12
            gain = 2 + (damage / 8);
            if (gain > 12) gain = 12;
        }

        // Optional: slightly less gain when attacking players (PvP tuning)
        if (target instanceof Player) {
            gain = Math.max(1, gain - 1);
        }
        attacker.getPA().sendSound(Sfx.BLADE_HIT);
        addWrath(attacker, gain);
    }

    /** Call when a player takes damage from an NPC. */
    public static void onPlayerTookNpcDamage(Player victim, NPC npc, int damage) {
        if (victim == null || npc == null) return;
        victim.getPA().sendSound(Sfx.GOBLIN_HIT);
        int gain;
        if (damage <= 0) {
            gain = 0; // avoid noisy gain on misses
        } else {
            // 1..8
            gain = 1 + (damage / 15);
            if (gain > 8) gain = 8;
        }

        if (gain > 0) {
            addWrath(victim, gain);
        }
    }

    /** Clears Wrath (eg after a finisher). */
    public static void reset(Player p) {
        setWrath(p, 0);
    }

    private static void handleReadyState(Player p, int oldWrath, int newWrath) {
        boolean wasReady = oldWrath >= READY_AT;
        boolean isReady  = newWrath >= READY_AT;

        // Track state so we can message only on edge transitions.
        int prevState = p.getAttributes().getInt(ATTR_READY_STATE, 0);
        int nextState = isReady ? 1 : 0;

        if (prevState != nextState) {
            p.getAttributes().setInt(ATTR_READY_STATE, nextState);
        }

        if (!wasReady && isReady) {
            long now = System.currentTimeMillis();
            long last = p.getAttributes().getLong(ATTR_READY_MSG_TS, 0L);

            // Rate limit: 3 seconds (prevents spam if you oscillate around READY_AT)
            if (now - last >= 3000) {
                p.getAttributes().setLong(ATTR_READY_MSG_TS, now);
                p.sendMessage("@red@Your Wrath surges. @bla@A finisher is ready!");
            }
        }
    }

    private static int clamp(int w) {
        if (w < 0) return 0;
        if (w > MAX_WRATH) return MAX_WRATH;
        return w;
    }
}
