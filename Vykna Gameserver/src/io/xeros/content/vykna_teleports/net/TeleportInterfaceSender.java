package io.xeros.content.vykna_teleports.net;

import io.xeros.content.vykna_teleports.data.TeleportDefinitions;
import io.xeros.content.vykna_teleports.model.TeleportCategory;
import io.xeros.content.vykna_teleports.model.TeleportDefinition;

import java.util.List;

/**
 * Sends interface frames for TeleportHomePage (client: 31000).
 *
 * You said you'll add:
 * - open hook (wherever you open the UI)
 * - button click hook (routes button ids to TeleportButtonHandler)
 *
 * This class only formats/sends data.
 */
public final class TeleportInterfaceSender {

    // ---- Client interface ids ----
    public static final int INTERFACE_ID = 31000;

    // Text targets (from your TeleportHomePage class)
    private static final int PREVIEW_NAME_ID = INTERFACE_ID + 1201;

    private static final int STATS_LINE1_ID = INTERFACE_ID + 1216;
    private static final int STATS_LINE2_ID = INTERFACE_ID + 1217;

    private static final int DESC_LINE1_ID = INTERFACE_ID + 1401;
    private static final int DESC_LINE2_ID = INTERFACE_ID + 1402;

    private static final int REQ_LINE1_ID = INTERFACE_ID + 1421;
    private static final int REQ_LINE2_ID = INTERFACE_ID + 1422;

    private static final int QUEST_LINE1_ID = INTERFACE_ID + 1441;

    // NPC model widget id (client)
    private static final int PREVIEW_NPC_ID = INTERFACE_ID + 1202;

    // Scroll row text IDs (client row base + 1)
    private static final int ROW_START_ID = INTERFACE_ID + 700;
    private static final int ROW_STRIDE = 10;

    private TeleportInterfaceSender() {}

    public static void open(io.xeros.model.entity.player.Player player) {
        // Standard Xeros: player.getPA().showInterface(int)
        player.getPA().showInterface(INTERFACE_ID);

        // Default category for first open
        sendCategory(player, TeleportCategory.MONSTERS);
    }

    public static void sendCategory(io.xeros.model.entity.player.Player player, TeleportCategory category) {
        List<TeleportDefinition> defs = TeleportDefinitions.byCategory(category);

        // Clear ALL 100 rows client-side (avoid stale data)
        for (int i = 0; i < 100; i++) {
            int textId = (ROW_START_ID + (i * ROW_STRIDE)) + 1;
            player.getPA().sendFrame126("", textId);
        }

        // Populate visible rows
        int max = Math.min(defs.size(), 100);
        for (int i = 0; i < max; i++) {
            TeleportDefinition def = defs.get(i);
            int textId = (ROW_START_ID + (i * ROW_STRIDE)) + 1;
            player.getPA().sendFrame126(def.getName(), textId);

            // Row head icon index (client-side type=17 uses valueIndex)
            // You will need a small packet/frame to set that per row later.
            // For now: client dummy mapping will show something.
        }

        // Push first preview by default
        if (!defs.isEmpty()) {
            sendPreview(player, defs.get(0));
            player.getAttributes().setInt("vykna_tp_selected_id", defs.get(0).getId());
        } else {
            // Empty state
            player.getPA().sendFrame126("No teleports", PREVIEW_NAME_ID);
            player.getPA().sendFrame126("", DESC_LINE1_ID);
            player.getPA().sendFrame126("", DESC_LINE2_ID);
            player.getPA().sendFrame126("", REQ_LINE1_ID);
            player.getPA().sendFrame126("", REQ_LINE2_ID);
            player.getPA().sendFrame126("None", QUEST_LINE1_ID);
        }
    }

    public static void sendPreview(io.xeros.model.entity.player.Player player, TeleportDefinition def) {
        player.getPA().sendFrame126(def.getName(), PREVIEW_NAME_ID);

        // Stats
        player.getPA().sendFrame126("Combat: " + def.getCombatLevel() + "   |   HP: " + def.getHitpoints(), STATS_LINE1_ID);
        player.getPA().sendFrame126("Aggressive: " + (def.isAggressive() ? "Yes" : "No"), STATS_LINE2_ID);

        // Description (2-line simple split)
        String d = def.getDescription() == null ? "" : def.getDescription();
        String line1 = d.length() > 32 ? d.substring(0, 32) : d;
        String line2 = d.length() > 32 ? d.substring(32) : "";
        player.getPA().sendFrame126(line1, DESC_LINE1_ID);
        player.getPA().sendFrame126(line2, DESC_LINE2_ID);

        // Requirements (simple)
        if (def.getRequirements() != null && def.getRequirements().getCombatLevel() != null) {
            player.getPA().sendFrame126("Combat level: " + def.getRequirements().getCombatLevel(), REQ_LINE1_ID);
        } else {
            player.getPA().sendFrame126("None", REQ_LINE1_ID);
        }
        player.getPA().sendFrame126("", REQ_LINE2_ID);

        // Quest
        player.getPA().sendFrame126(def.getQuestName() == null ? "None" : def.getQuestName(), QUEST_LINE1_ID);

        // NPC model preview
        // You already have sendNpcHeadOnInterface(npcId, interfaceId) for chat heads.
        // If you upgraded it to full NPC model: call it here.
        player.getPA().sendNpcHeadOnInterface(def.getNpcId(), PREVIEW_NPC_ID);
    }
}
