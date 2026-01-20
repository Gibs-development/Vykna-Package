package io.xeros.content.vykna_achievements;

import io.xeros.model.entity.player.Player;

public final class VyknaAchievementsInterfaceHandler {

    private static final int HOME_INTERFACE_ID = 35000;
    private static final int COMBAT_LIST_INTERFACE_ID = 35150;
    private static final int SKILLING_LIST_INTERFACE_ID = 35250;

    private static final int HOME_BUTTON_ACHIEVEMENTS = 35020;
    private static final int HOME_BUTTON_SKILLING = 35030;
    private static final int HOME_BUTTON_COMBAT = 35040;
    private static final int HOME_BUTTON_MASTERY = 35050;

    private static final int LIST_HOME_BUTTON_COMBAT = 35120;
    private static final int LIST_HOME_BUTTON_SKILLING = 35220;

    private VyknaAchievementsInterfaceHandler() {
    }

    public static void openHome(Player player) {
        player.getPA().showInterface(HOME_INTERFACE_ID);
        player.getVyknaAchievements().sendAllLists();
    }

    public static void openCombatList(Player player) {
        player.getPA().showInterface(COMBAT_LIST_INTERFACE_ID);
        player.getVyknaAchievements().sendAllLists();
    }

    public static void openSkillingList(Player player) {
        player.getPA().showInterface(SKILLING_LIST_INTERFACE_ID);
        player.getVyknaAchievements().sendAllLists();
    }

    public static boolean handleButton(Player player, int buttonId) {
        if (buttonId == HOME_BUTTON_ACHIEVEMENTS || buttonId == HOME_BUTTON_COMBAT) {
            openCombatList(player);
            return true;
        }
        if (buttonId == HOME_BUTTON_SKILLING) {
            openSkillingList(player);
            return true;
        }
        if (buttonId == HOME_BUTTON_MASTERY) {
            openHome(player);
            return true;
        }
        if (buttonId == LIST_HOME_BUTTON_COMBAT || buttonId == LIST_HOME_BUTTON_SKILLING) {
            openHome(player);
            return true;
        }
        return false;
    }
}
