package io.xeros.content.achievement;

import io.xeros.model.entity.player.Player;
import io.xeros.content.achievement.Achievements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CombatAchievementsHandler {

	private static final int INTERFACE_ID = 35000;
	private static final int BUTTON_ID_START = 35003;
	private static final int BUTTON_ID_END = 35234;
	private static final int CLOSE_BUTTON = 35003;
	private static final int TAB_HOME = 35036;
	private static final int TAB_COMBAT = 35037;
	private static final int TAB_MASTERY = 35038;
	private static final int NAV_BUTTON_START = 35081;
	private static final int NAV_BUTTON_END = 35115;
	private static final int MASTERY_BUTTON_START = 35170;
	private static final int MASTERY_BUTTON_END = 35175;
	private static final int MONSTER_BUTTON_START = 35200;
	private static final int MONSTER_BUTTON_END = 35206;
	private static final int REWARD_BUTTON_START = 35230;
	private static final int REWARD_BUTTON_END = 35234;

	private static final int FEED_TITLE_ID = 35031;
	private static final int SELECTED_TITLE_ID = 35016;
	private static final int SELECTED_DESC_ID = 35017;
	private static final int SELECTED_REWARD_ID = 35019;
	private static final int SELECTED_POINTS_ID = 35021;

	private static final int[] ENTRY_TEXT_IDS = {35111, 35116, 35121, 35126, 35131, 35136};
	private static final int[] ENTRY_PERCENT_IDS = {35113, 35118, 35123, 35128, 35133, 35138};

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
			openHome(player);
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
			setSelected(player, MASTERY_ITEMS.get(buttonId - MASTERY_BUTTON_START),
					"Track mastery progress for " + MASTERY_ITEMS.get(buttonId - MASTERY_BUTTON_START) + ".",
					"Rewards: Coming soon", "Points: -");
			return true;
		}

		if (buttonId >= MONSTER_BUTTON_START && buttonId <= MONSTER_BUTTON_END) {
			openCombat(player);
			String monster = getCombatMonster(buttonId - MONSTER_BUTTON_START);
			setSelected(player, monster + " Achievements",
					"Combat achievements for " + monster + " (placeholder list).",
					"Rewards: Coming soon", "Points: -");
			return true;
		}

		if (buttonId >= REWARD_BUTTON_START && buttonId <= REWARD_BUTTON_END) {
			openCombat(player);
			String tier = getRewardTier(buttonId - REWARD_BUTTON_START);
			setSelected(player, tier + " Rewards",
					tier + " combat achievements rewards (placeholder).",
					"Rewards: Coming soon", "Points: -");
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
		if (label.startsWith("Combat")) {
			openCombat(player);
			return;
		}
		if (label.startsWith("Mastery") || label.contains("Cape") || label.contains("200M")) {
			openMastery(player);
			return;
		}
		openHome(player);
	}

	private static void openHome(Player player) {
		List<String> entries = new ArrayList<>();
		for (Achievements.Achievement achievement : Achievements.Achievement.values()) {
			int current = player.getAchievements().getAmountRemaining(achievement.getTier().getId(), achievement.getId());
			String progress = current + "/" + achievement.getAmount();
			entries.add(achievement.getFormattedName() + " (" + progress + ")");
		}
		updateEntries(player, entries, "General Achievements");
		if (!Achievements.Achievement.ACHIEVEMENTS.isEmpty()) {
			Achievements.Achievement first = Achievements.Achievement.values()[0];
			setSelected(player, first.getFormattedName(),
					first.getDescription().replace("%d", String.valueOf(first.getAmount())),
					"Rewards: " + first.getRewards().length + " items",
					"Points: " + first.getPoints());
		}
	}

	private static void openCombat(Player player) {
		updateEntries(player, COMBAT_PLACEHOLDERS, "Combat Achievements");
		setSelected(player, "Combat Achievements",
				"Boss challenges and difficulty rewards will appear here.",
				"Rewards: Coming soon", "Points: -");
	}

	private static void openMastery(Player player) {
		updateEntries(player, MASTERY_PLACEHOLDERS, "Mastery Achievements");
		setSelected(player, "Mastery Achievements",
				"Track cape milestones and 200M goals here.",
				"Rewards: Coming soon", "Points: -");
	}

	private static void updateEntries(Player player, List<String> entries, String feedTitle) {
		player.getPA().sendFrame126(feedTitle, FEED_TITLE_ID);
		for (int i = 0; i < ENTRY_TEXT_IDS.length; i++) {
			String text = i < entries.size() ? entries.get(i) : "";
			player.getPA().sendFrame126(text, ENTRY_TEXT_IDS[i]);
			player.getPA().sendFrame126("", ENTRY_PERCENT_IDS[i]);
		}
	}

	private static void setSelected(Player player, String title, String description, String rewards, String points) {
		player.getPA().sendFrame126(title, SELECTED_TITLE_ID);
		player.getPA().sendFrame126(description, SELECTED_DESC_ID);
		player.getPA().sendFrame126(rewards, SELECTED_REWARD_ID);
		player.getPA().sendFrame126(points, SELECTED_POINTS_ID);
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
}
