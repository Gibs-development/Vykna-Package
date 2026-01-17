package io.xeros.content.achievement;

import io.xeros.model.entity.player.Player;

public final class CombatAchievementsHandler {

	private static final int BUTTON_ID_START = 35003;
	private static final int BUTTON_ID_END = 35234;

	private CombatAchievementsHandler() {
	}

	public static boolean handle(Player player, int buttonId) {
		if (buttonId < BUTTON_ID_START || buttonId > BUTTON_ID_END) {
			return false;
		}
		player.sendMessage("Combat achievements coming soon...");
		return true;
	}
}
