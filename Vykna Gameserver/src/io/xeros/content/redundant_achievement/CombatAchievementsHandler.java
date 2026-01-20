package io.xeros.content.redundant_achievement;

import io.xeros.content.achievement.vykna.AchievementDefinition;
import io.xeros.content.achievement.vykna.AchievementListType;
import io.xeros.content.achievement.vykna.AchievementSubcategory;
import io.xeros.content.achievement.vykna.VyknaAchievementCatalog;
import io.xeros.model.entity.player.Player;

import java.util.List;

public final class CombatAchievementsHandler {

	private static final int HOME_INTERFACE_ID = 35000;
	private static final int LIST_INTERFACE_ID = 64504;

	private static final int HOME_TASKS_BUTTON = 35020;
	private static final int HOME_SKILLING_BUTTON = 35030;
	private static final int HOME_COMBAT_BUTTON = 35040;
	private static final int HOME_MASTERY_BUTTON = 35050;
	private static final int HOME_TASKS_TEXT = 35171;
	private static final int HOME_SKILLING_TEXT = 35172;
	private static final int HOME_COMBAT_TEXT = 35173;

	private static final int LIST_HOME_BUTTON = LIST_INTERFACE_ID + 1200;
	private static final int LIST_SKILLING_BUTTON = LIST_INTERFACE_ID + 1240;
	private static final int LIST_COMBAT_BUTTON = LIST_INTERFACE_ID + 1260;
	private static final int LIST_MASTERY_BUTTON = LIST_INTERFACE_ID + 1280;
	private static final int LIST_CLOSE_BUTTON = LIST_INTERFACE_ID + 2005;
	private static final int LIST_DROPDOWN_ID = LIST_INTERFACE_ID + 2010;
	private static final int LIST_TITLE_ID = LIST_INTERFACE_ID + 2000;
	private static final int LIST_PROGRESS_TEXT_ID = LIST_INTERFACE_ID + 4000;
	private static final int LIST_ROW_START = LIST_INTERFACE_ID + 3100;
	private static final int LIST_ROW_STRIDE = 30;
	private static final int LIST_ROW_COUNT = 200;

	private CombatAchievementsHandler() {
	}

	public static void openInterface(Player player) {
		player.getPA().showInterface(HOME_INTERFACE_ID);
		player.setAchievementListType(AchievementListType.TASKS);
		player.setAchievementSubcategoryIndex(0);
	}

	public static boolean handle(Player player, int buttonId) {
		if (handleHomeButtons(player, buttonId)) {
			return true;
		}
		if (handleListButtons(player, buttonId)) {
			return true;
		}
		return false;
	}

	private static boolean handleHomeButtons(Player player, int buttonId) {
		switch (buttonId) {
			case HOME_TASKS_BUTTON:
			case HOME_TASKS_TEXT:
				openList(player, AchievementListType.TASKS);
				return true;
			case HOME_SKILLING_BUTTON:
			case HOME_SKILLING_TEXT:
				openList(player, AchievementListType.SKILLING);
				return true;
			case HOME_COMBAT_BUTTON:
			case HOME_COMBAT_TEXT:
				openList(player, AchievementListType.COMBAT);
				return true;
			case HOME_MASTERY_BUTTON:
				player.sendMessage("Mastery achievements coming soon.");
				return true;
			default:
				return false;
		}
	}

	private static boolean handleListButtons(Player player, int buttonId) {
		if (buttonId == LIST_CLOSE_BUTTON) {
			player.getPA().removeAllWindows();
			return true;
		}
		if (buttonId == LIST_HOME_BUTTON) {
			player.getPA().showInterface(HOME_INTERFACE_ID);
			return true;
		}
		if (buttonId == LIST_SKILLING_BUTTON) {
			openList(player, AchievementListType.SKILLING);
			return true;
		}
		if (buttonId == LIST_COMBAT_BUTTON) {
			openList(player, AchievementListType.COMBAT);
			return true;
		}
		if (buttonId == LIST_MASTERY_BUTTON) {
			player.sendMessage("Mastery achievements coming soon.");
			return true;
		}
		if (buttonId >= LIST_DROPDOWN_ID && buttonId < LIST_DROPDOWN_ID + 200) {
			int dropdownIndex = buttonId - LIST_DROPDOWN_ID;
			handleDropdownSelection(player, dropdownIndex);
			return true;
		}
		return false;
	}

	public static void handleDropdownSelection(Player player, int dropdownIndex) {
		AchievementListType listType = player.getAchievementListType();
		List<? extends AchievementSubcategory> subcategories = listType.getSubcategories();
		int clampedIndex = Math.max(0, Math.min(dropdownIndex, subcategories.size() - 1));
		player.setAchievementSubcategoryIndex(clampedIndex);
		sendAchievementList(player, listType, subcategories.get(clampedIndex));
	}

	private static void openList(Player player, AchievementListType listType) {
		player.setAchievementListType(listType);
		player.setAchievementSubcategoryIndex(0);
		player.getPA().showInterface(LIST_INTERFACE_ID);
		player.getPA().sendFrame126(listType.getDisplayName(), LIST_TITLE_ID);
		sendDropdownOptions(player, listType);
		handleDropdownSelection(player, 0);
	}

	private static void sendDropdownOptions(Player player, AchievementListType listType) {
		String options = listType.getSubcategories().stream()
				.map(AchievementSubcategory::getDisplayName)
				.reduce((left, right) -> left + "," + right)
				.orElse("");
		player.getPA().sendFrame126(options, LIST_DROPDOWN_ID);
	}

	private static void sendAchievementList(Player player, AchievementListType listType, AchievementSubcategory subcategory) {
		List<AchievementDefinition> achievements = VyknaAchievementCatalog.getAchievements(listType, subcategory);
		int completed = 0;
		for (int i = 0; i < achievements.size(); i++) {
			AchievementDefinition achievement = achievements.get(i);
			int base = LIST_ROW_START + (i * LIST_ROW_STRIDE);
			player.getPA().sendFrame126(achievement.getName(), base + 1);
			player.getPA().sendFrame126(achievement.getDescription(), base + 2);
			player.getPA().sendFrame126("<icon=0>" + achievement.getPoints(), base + 3);
			player.getPA().sendFrame126(achievement.getProgressText(player), base + 8);
			if (achievement.isComplete(player)) {
				completed++;
			}
		}
		for (int i = achievements.size(); i < LIST_ROW_COUNT; i++) {
			int base = LIST_ROW_START + (i * LIST_ROW_STRIDE);
			player.getPA().sendFrame126("", base + 1);
			player.getPA().sendFrame126("", base + 2);
			player.getPA().sendFrame126("", base + 3);
			player.getPA().sendFrame126("", base + 8);
		}
		player.getPA().sendFrame126(buildCompletionText(completed, achievements.size()), LIST_PROGRESS_TEXT_ID);
	}

	private static String buildCompletionText(int completed, int total) {
		int percent = total == 0 ? 0 : (int) Math.round((completed * 100.0) / total);
		return "Completion: " + completed + "/" + total + " (" + percent + "%)";
	}
}
