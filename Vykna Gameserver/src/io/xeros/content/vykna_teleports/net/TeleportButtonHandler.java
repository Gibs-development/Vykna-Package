package io.xeros.content.vykna_teleports.net;

import io.xeros.content.vykna_teleports.data.TeleportDefinitions;
import io.xeros.content.vykna_teleports.model.TeleportCategory;
import io.xeros.content.vykna_teleports.model.TeleportDefinition;
import io.xeros.model.entity.player.Player;

import java.util.List;

/**
 * Button / row click logic for TeleportHomePage.
 *
 * You will hook this from your server's ClickButton packet.
 */
public final class TeleportButtonHandler {

    // ---- Client ids (must match TeleportHomePage) ----
    private static final int INTERFACE_ID = TeleportInterfaceSender.INTERFACE_ID;

    private static final int TAB_MONSTERS_BTN = INTERFACE_ID + 200;
    private static final int TAB_BOSSES_BTN   = INTERFACE_ID + 220;
    private static final int TAB_ACTS_BTN     = INTERFACE_ID + 240;
    private static final int TAB_QUESTS_BTN   = INTERFACE_ID + 260;

    private static final int CLOSE_UI_ID      = INTERFACE_ID + 51;
    private static final int SEARCH_CLEAR_ID  = INTERFACE_ID + 402;

    private static final int TELEPORT_BTN_ID  = INTERFACE_ID + 1220;

    // Row click IDs:
    // Your rows are hover buttons at (ROW_START_ID + i*ROW_STRIDE) + 0
    private static final int ROW_START_ID = INTERFACE_ID + 700;
    private static final int ROW_STRIDE = 10;
    private static final int MAX_ROWS = 100;

    private TeleportButtonHandler() {}

    public static boolean handle(Player player, int buttonId) {
        if (buttonId == CLOSE_UI_ID) {
            player.getPA().closeAllWindows();
            return true;
        }

        if (buttonId == SEARCH_CLEAR_ID) {
            // Client-side will clear search UI; server-side can ignore for now.
            return true;
        }

        if (buttonId == TAB_MONSTERS_BTN) {
            TeleportInterfaceSender.sendCategory(player, TeleportCategory.MONSTERS);
            return true;
        }
        if (buttonId == TAB_BOSSES_BTN) {
            TeleportInterfaceSender.sendCategory(player, TeleportCategory.BOSSES);
            return true;
        }
        if (buttonId == TAB_ACTS_BTN) {
            TeleportInterfaceSender.sendCategory(player, TeleportCategory.ACTIVITIES);
            return true;
        }
        if (buttonId == TAB_QUESTS_BTN) {
            TeleportInterfaceSender.sendCategory(player, TeleportCategory.QUESTS);
            return true;
        }

        // Row click
        for (int i = 0; i < MAX_ROWS; i++) {
            int rowBtn = (ROW_START_ID + (i * ROW_STRIDE)) + 0;
            if (buttonId == rowBtn) {
                // Determine current category - simplest approach: store it on player attributes when tabs clicked.
                TeleportCategory cat = getOrDefaultCategory(player, TeleportCategory.MONSTERS);
                List<TeleportDefinition> defs = TeleportDefinitions.byCategory(cat);
                if (i < defs.size()) {
                    TeleportDefinition def = defs.get(i);
                    player.getAttributes().setInt("vykna_tp_selected_id", def.getId());
                    TeleportInterfaceSender.sendPreview(player, def);
                }
                return true;
            }
        }

        if (buttonId == TELEPORT_BTN_ID) {
            int selected = player.getAttributes().getInt("vykna_tp_selected_id");
            TeleportDefinition def = TeleportDefinitions.byId(selected);
            if (def != null) {
                // TODO: real requirement + quest checking here.
                player.getPA().startTeleport(def.getDestination().getX(), def.getDestination().getY(), def.getDestination().getHeight(), "modern", false);
            }
            return true;
        }

        return false;
    }

    private static TeleportCategory getOrDefaultCategory(Player player, TeleportCategory def) {
        String key = "vykna_tp_category";
        Object v = player.getAttributes().get(key);
        if (v instanceof TeleportCategory) {
            return (TeleportCategory) v;
        }
        player.getAttributes().set(key, def);
        return def;
    }
}
