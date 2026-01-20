package io.xeros.content.vykna_achievements;

import io.xeros.model.entity.player.Player;

public final class AchievementHandler {

    public static final int ACHIEVEMENTS_INTERFACE_ID = 35000;
    private final Player player;
    public AchievementHandler(Player player) {
        this.player = player;
    }

    /**
     * Opens the achievements interface. This should be the only entry-point for opening it.
     */
    public static void open(Player player) {
        if (player == null) return;

        // Adjust this call to whatever your server uses:
        // player.getPA().showInterface(ACHIEVEMENTS_INTERFACE_ID);
        player.getPA().showInterface(ACHIEVEMENTS_INTERFACE_ID);
    }
    public static boolean handleButton(Player player, int buttonId) {
        if (player == null) return false;

        switch (buttonId) {
            case 136204: // Tasks
                openList(player, ListType.TASKS);
                return true;
            case 136214: // Skills
                openList(player, ListType.SKILLS);
                return true;
            case 136224: // Combat
                openList(player, ListType.COMBAT);
                return true;
            default:
                return false;
        }
    }

    /**
     * Central click handler for the achievements interface.
     * Wire this from your Button/Interface action listener.
     *
     * @return true if this handler consumed the click.
     */
    public static boolean handleClick(Player player, int interfaceId, int componentId, int opcode) {
        if (player == null) return false;
        if (interfaceId != ACHIEVEMENTS_INTERFACE_ID) return false;

        // TODO: Hook your actual component ids here once you finalize the client interface.
        // Keep all achievements UI clicking routed through this method.

        switch (componentId) {
            // Example placeholders:
            // case 35010: openTasks(player); return true;
            // case 35011: openCombat(player); return true;
            // case 35012: openSkilling(player); return true;

            default:
                return false;
        }
    }
}
