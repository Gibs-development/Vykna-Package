package io.xeros.content.achievement;

import io.xeros.achievements.AchievementInterface;
import io.xeros.achievements.AchievementList;
import io.xeros.model.entity.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CombatAchievementsHandler {

	private static final int INTERFACE_ID = 35000;
	private static final int BUTTON_ID_START = 35003;
	private static final int BUTTON_ID_END = 35545;
	private static final int CLOSE_BUTTON = 35003;
	private static final int TAB_HOME = 35360;
	private static final int TAB_COMBAT = 35362;
	private static final int TAB_MASTERY = 35364;
	private static final int NAV_BUTTON_START = 35401;
	private static final int NAV_BUTTON_END = 35435;
	private static final int MASTERY_BUTTON_START = 35501;
	private static final int MASTERY_BUTTON_END = 35506;
	private static final int MONSTER_BUTTON_START = 35521;
	private static final int MONSTER_BUTTON_END = 35527;
	private static final int REWARD_BUTTON_START = 35541;
	private static final int REWARD_BUTTON_END = 35545;

	private static final int FEED_TITLE_ID = 35300;
	private static final int SELECTED_TITLE_ID = 35006;
	private static final int SELECTED_DESC_ID = 35008;
	private static final int SELECTED_DIFFICULTY_ID = 35010;
	private static final int SELECTED_PROGRESS_ID = 35012;
	private static final int SELECTED_REWARD_ID = 35014;
	private static final int COMPLETED_TEXT_ID = 35015;
	private static final int POINTS_TEXT_ID = 35016;

	private static final int LIST_ENTRY_START = 35031;
	private static final int LIST_ENTRY_COUNT = 200;

	private static final List<String> NAV_ITEMS = Arrays.asList(
			"General Overview",
			"Combat Overview",
			"Mastery Overview",
			" ",
			"Filters",
			"Skills",
			"Skilling",
			"Exploration",
			"Minigames",
			"Social",
			"Quests",
			" ",
			"Combat: Difficulties",
			"Easy (0/120)",
			"Medium (0/90)",
			"Hard (0/60)",
			"Master (0/40)",
			"Grandmaster (0/20)",
			" ",
			"Combat: Bosses",
			"Araxxor",
			"Kerapac",
			"Zamorak",
			"Raksha",
			"Telos",
			"Zuk",
			" ",
			"Mastery Milestones",
			"Master Quest Cape",
			"Max Cape",
			"Completionist Cape",
			"Completionist (t)",
			"Master of Masters",
			"200M All Skills",
			"200M All Skills (t)"
	);

	private static final List<String> MASTERY_ITEMS = Arrays.asList(
			"Master Quest Cape",
			"Max Cape",
			"Completionist Cape",
			"Completionist (t)",
			"200M All Skills",
			"200M All Skills (t)"
	);

	private static final List<String> COMBAT_PLACEHOLDERS = Arrays.asList(
			"Kill boss under 1 minute",
			"Kill boss 5 times",
			"Kill boss 25 times",
			"Kill boss 100 times",
			"Kill boss without taking damage",
			"Perfect kill streak"
	);

	private static final List<String> MASTERY_PLACEHOLDERS = Arrays.asList(
			"Master Quest Cape requirements",
			"Max Cape requirements",
			"Completionist Cape requirements",
			"Completionist (t) requirements",
			"200M All Skills requirements",
			"200M All Skills (t) requirements"
	);

	private CombatAchievementsHandler() {
	}

	public static void openInterface(Player player) {
		player.getPA().showInterface(INTERFACE_ID);
		openHome(player);
	}

	public static boolean handle(Player player, int buttonId) {
		if (buttonId < BUTTON_ID_START || buttonId > BUTTON_ID_END) {
			return false;
		}
		if (buttonId == CLOSE_BUTTON) {
			player.getPA().removeAllWindows();
			return true;
		}

		if (buttonId == TAB_HOME) {
			openAchievements(player);
			return true;
		}

		if (buttonId == TAB_COMBAT) {
			openCombat(player);
			return true;
		}

		if (buttonId == TAB_MASTERY) {
			openMastery(player);
			return true;
		}

		if (buttonId >= NAV_BUTTON_START && buttonId <= NAV_BUTTON_END) {
			handleNavigation(player, buttonId);
			return true;
		}

		if (buttonId >= MASTERY_BUTTON_START && buttonId <= MASTERY_BUTTON_END) {
			openMastery(player);
			setPlaceholderSelected(player, MASTERY_ITEMS.get(buttonId - MASTERY_BUTTON_START));
			return true;
		}

		if (buttonId >= MONSTER_BUTTON_START && buttonId <= MONSTER_BUTTON_END) {
			openCombat(player);
			setPlaceholderSelected(player, getCombatMonster(buttonId - MONSTER_BUTTON_START));
			return true;
		}

		if (buttonId >= REWARD_BUTTON_START && buttonId <= REWARD_BUTTON_END) {
			openCombat(player);
			setPlaceholderSelected(player, getRewardTier(buttonId - REWARD_BUTTON_START) + " Rewards");
			return true;
		}

		if (buttonId >= LIST_ENTRY_START && buttonId < LIST_ENTRY_START + AchievementList.values().length) {
			openAchievements(player);
			AchievementList achievement = AchievementList.values()[buttonId - LIST_ENTRY_START];
			AchievementInterface.sendInterfaceForAchievement(player, achievement);
			return true;
		}

		player.sendMessage("Combat achievements coming soon...");
		return true;
	}

	private static void handleNavigation(Player player, int buttonId) {
		int index = buttonId - NAV_BUTTON_START;
		if (index < 0 || index >= NAV_ITEMS.size()) {
			openHome(player);
			return;
		}
		String label = NAV_ITEMS.get(index);
		if (label.startsWith("General")) {
			openHome(player);
			return;
		}
		if (label.startsWith("Combat")) {
			openCombat(player);
			return;
		}
		if (label.startsWith("Mastery") || label.contains("Cape") || label.contains("200M")) {
			openMastery(player);
			return;
		}
		openAchievements(player);
	}

	private static void openHome(Player player) {
		player.getPA().sendFrame126("Achievements Home", FEED_TITLE_ID);
		updateOverview(player);
		updateHighlights(player);
		clearList(player);
		setPlaceholderSelected(player, "Welcome");
	}

	private static void openAchievements(Player player) {
		player.getPA().sendFrame126("Achievements", FEED_TITLE_ID);
		updateOverview(player);
		updateAchievementsList(player);
		AchievementInterface.sendInterfaceForAchievement(player, AchievementList.values()[0]);
	}

	private static void openMastery(Player player) {
		player.getPA().sendFrame126("Mastery (Placeholder)", FEED_TITLE_ID);
		updateOverview(player);
		updateEntries(player, MASTERY_PLACEHOLDERS);
		setPlaceholderSelected(player, "Mastery");
	}

	private static void openCombat(Player player) {
		player.getPA().sendFrame126("Combat Achievements (Placeholder)", FEED_TITLE_ID);
		updateOverview(player);
		updateEntries(player, COMBAT_PLACEHOLDERS);
		setPlaceholderSelected(player, "Combat Achievements");
	}

	private static void updateAchievementsList(Player player) {
		AchievementList[] achievements = AchievementList.values();
		for (int i = 0; i < achievements.length; i++) {
			AchievementList achievement = achievements[i];
			int current = player.getPlayerAchievements().getOrDefault(achievement, 0);
			int total = achievement.getCompleteAmount();
			String progress = current + "/" + total;
			String color = getProgressColor(current, total);
			player.getPA().sendFrame126(color + achievement.getName() + " (" + progress + ")", LIST_ENTRY_START + i);
		}
		for (int i = achievements.length; i < LIST_ENTRY_COUNT; i++) {
			player.getPA().sendFrame126("", LIST_ENTRY_START + i);
		}
	}

	private static void updateEntries(Player player, List<String> entries) {
		for (int i = 0; i < entries.size(); i++) {
			player.getPA().sendFrame126(entries.get(i), LIST_ENTRY_START + i);
		}
		for (int i = entries.size(); i < LIST_ENTRY_COUNT; i++) {
			player.getPA().sendFrame126("", LIST_ENTRY_START + i);
		}
	}

	private static void clearList(Player player) {
		for (int i = 0; i < LIST_ENTRY_COUNT; i++) {
			player.getPA().sendFrame126("", LIST_ENTRY_START + i);
		}
	}

	private static void setPlaceholderSelected(Player player, String title) {
		player.getPA().sendFrame126(title, SELECTED_TITLE_ID);
		player.getPA().sendFrame126("Details coming soon.", SELECTED_DESC_ID);
		player.getPA().sendFrame126("-", SELECTED_DIFFICULTY_ID);
		player.getPA().sendFrame126("-", SELECTED_PROGRESS_ID);
		player.getPA().sendFrame126("-", SELECTED_REWARD_ID);
	}

	private static void updateOverview(Player player) {
		int total = AchievementList.values().length;
		int completed = player.getPA().achievementCompleted();
		player.getPA().sendFrame126("</col>Completed: <col=65280>" + completed + "</col>/" + total, COMPLETED_TEXT_ID);
		player.getPA().sendFrame126("</col>Points: <col=65280>" + player.getAchievementsPoints(), POINTS_TEXT_ID);
		int percent = total == 0 ? 0 : (int) Math.round((completed * 100.0) / total);
		player.getPA().sendFrame126(percent + \"%\", 35009);
	}

	private static void updateHighlights(Player player) {
		List<HighlightEntry> highlights = new ArrayList<>();
		for (AchievementList achievement : AchievementList.values()) {
			int current = player.getPlayerAchievements().getOrDefault(achievement, 0);
			int total = achievement.getCompleteAmount();
			if (current >= total) {
				continue;
			}
			double percent = total == 0 ? 0 : (double) current / total;
			highlights.add(new HighlightEntry(achievement, percent));
		}
		highlights.sort((a, b) -> Double.compare(b.percent, a.percent));
		updateHighlightLine(player, 0, highlights, 35043);
		updateHighlightLine(player, 1, highlights, 35044);
		updateHighlightLine(player, 2, highlights, 35045);
	}

	private static void updateHighlightLine(Player player, int index, List<HighlightEntry> highlights, int lineId) {
		if (index >= highlights.size()) {
			player.getPA().sendFrame126("", lineId);
			return;
		}
		HighlightEntry entry = highlights.get(index);
		int current = player.getPlayerAchievements().getOrDefault(entry.achievement, 0);
		int total = entry.achievement.getCompleteAmount();
		int percent = total == 0 ? 0 : (int) Math.round((current * 100.0) / total);
		player.getPA().sendFrame126((index + 1) + ") " + entry.achievement.getName() + " (" + percent + "%)", lineId);
	}

	private static String getProgressColor(int current, int total) {
		if (current <= 0) {
			return "<col=FF0000>";
		}
		if (current >= total) {
			return "<col=00FF00>";
		}
		return "<col=FFFF00>";
	}

	private static String getCombatMonster(int index) {
		switch (index) {
			case 0:
				return "Araxxor";
			case 1:
				return "Kerapac";
			case 2:
				return "Zamorak";
			case 3:
				return "Raksha";
			case 4:
				return "Telos";
			case 5:
				return "Zuk";
			default:
				return "Vorago";
		}
	}

	private static String getRewardTier(int index) {
		switch (index) {
			case 0:
				return "Easy";
			case 1:
				return "Medium";
			case 2:
				return "Hard";
			case 3:
				return "Master";
			default:
				return "Grandmaster";
		}
	}

	private static final class HighlightEntry {
		private final AchievementList achievement;
		private final double percent;

		private HighlightEntry(AchievementList achievement, double percent) {
			this.achievement = achievement;
			this.percent = percent;
		}
	}
}
