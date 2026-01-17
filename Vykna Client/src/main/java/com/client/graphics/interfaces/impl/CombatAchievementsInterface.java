package com.client.graphics.interfaces.impl;

import com.client.Configuration;
import com.client.TextDrawingArea;
import com.client.graphics.interfaces.RSInterface;

public final class CombatAchievementsInterface extends RSInterface {

	private static final String SPRITE_ROOT = "Interfaces/vykna_achievements/";
	private static final int LIST_SIZE = 200;

	private CombatAchievementsInterface() {
	}

	public static void build(TextDrawingArea[] tda) {
		RSInterface rsi = addTabInterface(35000);
		addSprite(35001, 0, SPRITE_ROOT + "combat_bg");
		addHoverButton(35003, SPRITE_ROOT + "combat_close", 0, 15, 15, "Close", 250, 35004, 3);
		addHoveredButton(35004, SPRITE_ROOT + "combat_close", 0, 15, 15, 35005);

		addText(35301, Configuration.CLIENT_TITLE + " Achievements", tda, 2, 0xF7AA25, true, true);
		addHoverButton(35360, SPRITE_ROOT + "combat_tab_home", 0, 120, 16, "Home", 0, 35361, 1);
		addHoveredButton(35361, SPRITE_ROOT + "combat_tab_home", 0, 120, 16, 35362);
		addHoverButton(35362, SPRITE_ROOT + "combat_tab_combat", 0, 150, 16, "Combat Achievements", 0, 35363, 1);
		addHoveredButton(35363, SPRITE_ROOT + "combat_tab_combat", 0, 150, 16, 35364);
		addHoverButton(35364, SPRITE_ROOT + "combat_tab_mastery", 0, 120, 16, "Mastery", 0, 35365, 1);
		addHoveredButton(35365, SPRITE_ROOT + "combat_tab_mastery", 0, 120, 16, 35366);

		addText(35007, "Overall Completion", tda, 1, 0xE6E0D0, false, true);
		addSprite(35310, 0, SPRITE_ROOT + "combat_progress_bar");
		addText(35009, "", tda, 0, 0xB7F0FF, false, true);

		addSprite(35320, 0, SPRITE_ROOT + "combat_section_panel");
		addText(35040, "Home", tda, 1, 0xF7AA25, true, true);
		addText(35041, "Highlights", tda, 0, 0x9FB3C8, true, true);
		addText(35042, "Top 3 closest achievements", tda, 0, 0xC9C2B4, true, true);
		addText(35043, "", tda, 0, 0xC9C2B4, true, true);
		addText(35044, "", tda, 0, 0xC9C2B4, true, true);
		addText(35045, "", tda, 0, 0xC9C2B4, true, true);

		addSprite(35330, 0, SPRITE_ROOT + "combat_section_panel");
		addSprite(35331, 0, SPRITE_ROOT + "combat_section_panel");
		addSprite(35332, 0, SPRITE_ROOT + "combat_section_panel");
		addSprite(35333, 0, SPRITE_ROOT + "combat_section_panel");

		addText(35006, "", tda, 1, 0xF7AA25, false, true);
		addText(35008, "", tda, 0, 0xC9C2B4, false, true);
		addText(35010, "", tda, 0, 0xC9C2B4, false, true);
		addText(35012, "", tda, 0, 0xC9C2B4, false, true);
		addText(35311, "Rewards", tda, 1, 0xE6E0D0, false, true);
		addText(35014, "", tda, 0, 0xB7F0FF, false, true);
		addText(35015, "", tda, 0, 0xBF7D0A, true, true);
		addText(35016, "", tda, 0, 0xBF7D0A, true, true);

		addText(35300, "", tda, 1, 0xE6E0D0, false, true);
		addText(35302, "Selected Achievement", tda, 1, 0xE6E0D0, false, true);
		addText(35303, "Filters", tda, 0, 0x9FB3C8, false, true);
		addText(35304, "Monsters", tda, 0, 0x9FB3C8, false, true);
		addText(35305, "Difficulty Rewards", tda, 0, 0x9FB3C8, false, true);
		addText(35306, "Mastery Sections", tda, 0, 0x9FB3C8, false, true);
		addSprite(35312, 0, SPRITE_ROOT + "combat_filter_header");
		addSprite(35313, 0, SPRITE_ROOT + "combat_monster_entry");
		addSprite(35314, 0, SPRITE_ROOT + "combat_reward_tier");
		addSprite(35315, 0, SPRITE_ROOT + "combat_entry_row");

		addItemContainer(36002, 2, 10, 0, 0, false);
		addConfigSpriteNew(35307, SPRITE_ROOT + "combat_badge_slot", 0, 0, 0, 694);

		rsi.totalChildren(49);
		rsi.child(0, 35001, 8, 8);
		rsi.child(1, 35003, 474, 18);
		rsi.child(2, 35004, 474, 18);
		rsi.child(3, 35301, 255, 20);
		rsi.child(4, 35360, 26, 34);
		rsi.child(5, 35361, 26, 34);
		rsi.child(6, 35362, 170, 34);
		rsi.child(7, 35363, 170, 34);
		rsi.child(8, 35364, 360, 34);
		rsi.child(9, 35365, 360, 34);
		rsi.child(10, 35007, 20, 66);
		rsi.child(11, 35310, 150, 68);
		rsi.child(12, 35009, 420, 66);
		rsi.child(13, 35320, 168, 88);
		rsi.child(14, 35040, 244, 94);
		rsi.child(15, 35041, 244, 108);
		rsi.child(16, 35042, 244, 120);
		rsi.child(17, 35043, 244, 132);
		rsi.child(18, 35044, 244, 144);
		rsi.child(19, 35045, 244, 156);
		rsi.child(20, 35330, 12, 126);
		rsi.child(21, 35331, 168, 126);
		rsi.child(22, 35332, 176, 136);
		rsi.child(23, 35333, 176, 266);
		rsi.child(24, 35307, 182, 272);
		rsi.child(25, 35006, 238, 268);
		rsi.child(26, 35008, 238, 284);
		rsi.child(27, 35010, 238, 304);
		rsi.child(28, 35012, 238, 318);
		rsi.child(29, 35311, 238, 332);
		rsi.child(30, 35014, 238, 346);
		rsi.child(31, 35015, 260, 88);
		rsi.child(32, 35016, 420, 88);
		rsi.child(33, 35300, 182, 138);
		rsi.child(34, 35302, 182, 252);
		rsi.child(35, 35303, 22, 196);
		rsi.child(36, 35304, 22, 250);
		rsi.child(37, 35305, 182, 196);
		rsi.child(38, 35306, 22, 318);
		rsi.child(39, 35312, 16, 192);
		rsi.child(40, 35313, 16, 246);
		rsi.child(41, 35314, 182, 200);
		rsi.child(42, 35315, 182, 150);
		rsi.child(43, 35030, 182, 154);
		rsi.child(44, 35400, 16, 176);
		rsi.child(45, 35500, 16, 334);
		rsi.child(46, 35520, 16, 260);
		rsi.child(47, 35540, 182, 204);
		rsi.child(48, 36002, 408, 300);

		RSInterface listScroll = addTabInterface(35030);
		listScroll.width = 304;
		listScroll.height = 90;
		listScroll.scrollMax = LIST_SIZE * 16;
		listScroll.totalChildren(LIST_SIZE);
		int y = 2;
		for (int i = 0; i < LIST_SIZE; i++) {
			int id = 35031 + i;
			addHoverText(id, "", "View achievement", tda, 0, 0xE6E0D0, false, true, 280, 16);
			listScroll.child(i, id, 4, y);
			y += 16;
		}

		RSInterface navScroll = addTabInterface(35400);
		navScroll.width = 138;
		navScroll.height = 170;
		navScroll.scrollMax = 560;
		int navChild = 0;
		navScroll.totalChildren(35);
		int navY = 2;
		String[] navItems = {
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
		};
		for (int i = 0; i < navItems.length; i++) {
			int id = 35401 + i;
			String label = navItems[i];
			boolean isSpacer = label.trim().isEmpty();
			boolean isHeading = label.contains(":") || label.equals("Mastery Milestones") || label.equals("Filters");
			int color = isHeading ? 0x9FB3C8 : 0xC9C2B4;
			String tooltip = isSpacer ? "" : "Select " + label.trim();
			addHoverText(id, label, tooltip, tda, 0, color, false, true, 130, 16);
			navScroll.child(navChild++, id, 4, navY);
			navY += 16;
		}

		RSInterface masteryScroll = addTabInterface(35500);
		masteryScroll.width = 138;
		masteryScroll.height = 70;
		masteryScroll.scrollMax = 120;
		masteryScroll.totalChildren(6);
		String[] masteryItems = {
				"Master Quest Cape",
				"Max Cape",
				"Completionist Cape",
				"Completionist (t)",
				"200M All Skills",
				"200M All Skills (t)"
		};
		for (int i = 0; i < masteryItems.length; i++) {
			int id = 35501 + i;
			addHoverText(id, masteryItems[i], "View " + masteryItems[i], tda, 0, 0xC9C2B4, false, true, 130, 16);
			masteryScroll.child(i, id, 4, 2 + (i * 16));
		}

		RSInterface combatMonsters = addTabInterface(35520);
		combatMonsters.width = 138;
		combatMonsters.height = 70;
		combatMonsters.scrollMax = 140;
		combatMonsters.totalChildren(7);
		String[] combatMonstersList = {
				"Araxxor",
				"Kerapac",
				"Zamorak",
				"Raksha",
				"Telos",
				"Zuk",
				"Vorago"
		};
		for (int i = 0; i < combatMonstersList.length; i++) {
			int id = 35521 + i;
			addHoverText(id, combatMonstersList[i], "View " + combatMonstersList[i], tda, 0, 0xC9C2B4, false, true, 130, 16);
			combatMonsters.child(i, id, 4, 2 + (i * 16));
		}

		RSInterface combatRewards = addTabInterface(35540);
		combatRewards.width = 304;
		combatRewards.height = 34;
		combatRewards.scrollMax = 34;
		combatRewards.totalChildren(5);
		String[] rewardTiers = {"Easy", "Medium", "Hard", "Master", "Grandmaster"};
		for (int i = 0; i < rewardTiers.length; i++) {
			int id = 35541 + i;
			addHoverText(id, rewardTiers[i], "View " + rewardTiers[i] + " rewards", tda, 0, 0xF7AA25, false, true, 90, 16);
			combatRewards.child(i, id, 4 + (i * 58), 8);
		}
	}
}
