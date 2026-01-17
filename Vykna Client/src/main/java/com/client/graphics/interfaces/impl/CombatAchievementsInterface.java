package com.client.graphics.interfaces.impl;

import com.client.Configuration;
import com.client.graphics.interfaces.RSInterface;
import com.client.TextDrawingArea;

public final class CombatAchievementsInterface extends RSInterface {

	private CombatAchievementsInterface() {
	}

	public static void build(TextDrawingArea[] tda) {
		RSInterface rsi = addTabInterface(35000);
		addBox(35001, 0x0F141C, 0x0F141C, 110, 494, 317);
		addBox(35002, 0x1B232D, 0x1B232D, 70, 486, 40);
		addHoverButton(35003, "Interfaces/newachievements/IMAGE", 17, 15, 15, "Close", 250, 35004, 3);
		addHoveredButton(35004, "Interfaces/newachievements/IMAGE", 18, 15, 15, 35005);
		addText(35006, Configuration.CLIENT_TITLE + " Achievements", tda, 2, 0xF7AA25, true, true);
		addHoverText(35036, "Achievements", "View achievement home", tda, 1, 0xE6E0D0, false, true, 120, 16);
		addHoverText(35037, "Combat Achievements", "View combat achievements", tda, 1, 0xE6E0D0, false, true, 150, 16);
		addHoverText(35038, "Mastery", "View mastery achievements", tda, 1, 0xE6E0D0, false, true, 120, 16);
		addBox(35039, 0x16202A, 0x16202A, 120, 150, 58);
		addText(35040, "Home", tda, 1, 0xF7AA25, true, true);
		addText(35041, "Highlights", tda, 0, 0x9FB3C8, true, true);
		addText(35042, "Top 3 closest achievements", tda, 0, 0xC9C2B4, true, true);
		addText(35043, "1) Explore the Advanced potions (75%)", tda, 0, 0xC9C2B4, true, true);
		addText(35044, "2) Kill boss 5 under 1 minute (70%)", tda, 0, 0xC9C2B4, true, true);
		addText(35045, "3) Craft 500 runes (60%)", tda, 0, 0xC9C2B4, true, true);
		addText(35007, "Overall Completion", tda, 1, 0xE6E0D0, false, true);
		RSInterface.addProgressBar2021(35008, 260, 12, 0x1B232D);
		RSInterface.get(35008).progressBar2021Percentage = 0.62;
		addText(35009, "62% (1,240 pts)", tda, 0, 0xB7F0FF, false, true);
		addBox(35010, 0x18212B, 0x18212B, 120, 150, 225);
		addBox(35011, 0x18212B, 0x18212B, 110, 330, 225);
		addBox(35012, 0x141A22, 0x141A22, 130, 314, 132);
		addBox(35013, 0x141A22, 0x141A22, 130, 314, 84);
		addBox(35014, 0x1E2833, 0x1E2833, 80, 48, 48);
		addText(35015, "SPRITE", tda, 0, 0x7A8AA1, true, true);
		addText(35016, "Mastery of the Gods", tda, 1, 0xF7AA25, false, true);
		addText(35017, "Complete 100 boss achievements without taking unavoidable damage.", tda, 0, 0xC9C2B4, false, true);
		addText(35018, "Rewards", tda, 1, 0xE6E0D0, false, true);
		addText(35019, "Master of Masters cape, 250 pts, title: \"Godslayer\"", tda, 0, 0xB7F0FF, false, true);
		addText(35020, "Points", tda, 1, 0xE6E0D0, false, true);
		addText(35021, "Mastery: 250 pts", tda, 0, 0xB7F0FF, false, true);
		addHoverText(35022, "General Achievements", "View general achievements", tda, 1, 0xF7AA25, false, true, 130, 16);
		addHoverText(35023, "Combat Achievements", "View combat achievements", tda, 1, 0xF7AA25, false, true, 130, 16);
		addHoverText(35024, "Mastery Achievements", "View mastery achievements", tda, 1, 0xF7AA25, false, true, 130, 16);
		addText(35025, "General", tda, 0, 0xC9C2B4, false, true);
		RSInterface.addProgressBar2021(35026, 120, 10, 0x1B232D);
		RSInterface.get(35026).progressBar2021Percentage = 0.78;
		addText(35027, "Combat", tda, 0, 0xC9C2B4, false, true);
		RSInterface.addProgressBar2021(35028, 120, 10, 0x1B232D);
		RSInterface.get(35028).progressBar2021Percentage = 0.55;
		addText(35029, "Mastery", tda, 0, 0xC9C2B4, false, true);
		RSInterface.addProgressBar2021(35030, 120, 10, 0x1B232D);
		RSInterface.get(35030).progressBar2021Percentage = 0.34;
		addText(35031, "Achievement Feed", tda, 1, 0xE6E0D0, false, true);
		addText(35032, "Selected Achievement", tda, 1, 0xE6E0D0, false, true);
		addText(35046, "Filters", tda, 0, 0x9FB3C8, false, true);
		addText(35047, "Monsters", tda, 0, 0x9FB3C8, false, true);
		addText(35048, "Difficulty Rewards", tda, 0, 0x9FB3C8, false, true);
		addText(35049, "Mastery Sections", tda, 0, 0x9FB3C8, false, true);
		rsi.totalChildren(50);
		rsi.child(0, 35001, 8, 8);
		rsi.child(1, 35002, 12, 12);
		rsi.child(2, 35003, 474, 18);
		rsi.child(3, 35004, 474, 18);
		rsi.child(4, 35006, 255, 20);
		rsi.child(5, 35036, 26, 34);
		rsi.child(6, 35037, 170, 34);
		rsi.child(7, 35038, 360, 34);
		rsi.child(8, 35007, 20, 66);
		rsi.child(9, 35008, 150, 68);
		rsi.child(10, 35009, 420, 66);
		rsi.child(11, 35039, 168, 88);
		rsi.child(12, 35040, 244, 94);
		rsi.child(13, 35041, 244, 108);
		rsi.child(14, 35042, 244, 120);
		rsi.child(15, 35043, 244, 132);
		rsi.child(16, 35044, 244, 144);
		rsi.child(17, 35045, 244, 156);
		rsi.child(18, 35010, 12, 126);
		rsi.child(19, 35011, 168, 126);
		rsi.child(20, 35012, 176, 136);
		rsi.child(21, 35013, 176, 266);
		rsi.child(22, 35014, 182, 272);
		rsi.child(23, 35015, 206, 288);
		rsi.child(24, 35016, 238, 268);
		rsi.child(25, 35017, 238, 284);
		rsi.child(26, 35018, 238, 304);
		rsi.child(27, 35019, 238, 318);
		rsi.child(28, 35020, 238, 328);
		rsi.child(29, 35021, 238, 336);
		rsi.child(30, 35022, 22, 132);
		rsi.child(31, 35023, 22, 150);
		rsi.child(32, 35024, 22, 168);
		rsi.child(33, 35025, 22, 88);
		rsi.child(34, 35026, 104, 90);
		rsi.child(35, 35027, 240, 88);
		rsi.child(36, 35028, 304, 90);
		rsi.child(37, 35029, 372, 88);
		rsi.child(38, 35030, 436, 90);
		rsi.child(39, 35031, 182, 138);
		rsi.child(40, 35032, 182, 252);
		rsi.child(41, 35046, 22, 196);
		rsi.child(42, 35047, 22, 250);
		rsi.child(43, 35048, 182, 196);
		rsi.child(44, 35049, 22, 318);
		rsi.child(45, 35080, 16, 176);
		rsi.child(46, 35100, 182, 138);
		rsi.child(47, 35160, 16, 334);
		rsi.child(48, 35190, 16, 260);
		rsi.child(49, 35220, 182, 204);

		RSInterface leftScroll = addTabInterface(35080);
		leftScroll.width = 138;
		leftScroll.height = 170;
		leftScroll.scrollMax = 560;
		int leftChild = 0;
		leftScroll.totalChildren(35);
		int leftY = 2;
		String[] leftItems = {
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
		for (int i = 0; i < leftItems.length; i++) {
			int id = 35081 + i;
			String label = leftItems[i];
			boolean isSpacer = label.trim().isEmpty();
			boolean isHeading = label.contains(":") || label.equals("Mastery Milestones") || label.equals("Filters");
			int color = isHeading ? 0x9FB3C8 : 0xC9C2B4;
			String tooltip = isSpacer ? "" : "Select " + label.trim();
			addHoverText(id, label, tooltip, tda, 0, color, false, true, 130, 16);
			leftScroll.child(leftChild++, id, 4, leftY);
			leftY += 16;
		}

		RSInterface rightScroll = addTabInterface(35100);
		rightScroll.width = 304;
		rightScroll.height = 104;
		rightScroll.scrollMax = 180;
		int entryCount = 6;
		rightScroll.totalChildren(entryCount * 5);
		for (int i = 0; i < entryCount; i++) {
			int baseId = 35110 + (i * 5);
			addBox(baseId, 0x1B232D, 0x1B232D, 90, 300, 24);
			addText(baseId + 1, "Kill boss " + (i + 1) + " under 1 minute", tda, 0, 0xE6E0D0, false, true);
			RSInterface.addProgressBar2021(baseId + 2, 120, 8, 0x11161D);
			RSInterface.get(baseId + 2).progressBar2021Percentage = Math.min(0.2 + (i * 0.12), 1.0);
			addText(baseId + 3, (10 + i * 15) + "%", tda, 0, 0x9FB3C8, false, true);
			addBox(baseId + 4, 0x29333F, 0x29333F, 60, 18, 18);
			int baseY = 2 + (i * 28);
			rightScroll.child(i * 5, baseId, 2, baseY);
			rightScroll.child(i * 5 + 1, baseId + 1, 8, baseY + 5);
			rightScroll.child(i * 5 + 2, baseId + 2, 160, baseY + 9);
			rightScroll.child(i * 5 + 3, baseId + 3, 262, baseY + 4);
			rightScroll.child(i * 5 + 4, baseId + 4, 276, baseY + 3);
		}

		RSInterface masteryScroll = addTabInterface(35160);
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
			int id = 35170 + i;
			addHoverText(id, masteryItems[i], "View " + masteryItems[i], tda, 0, 0xC9C2B4, false, true, 130, 16);
			masteryScroll.child(i, id, 4, 2 + (i * 16));
		}

		RSInterface combatMonsters = addTabInterface(35190);
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
			int id = 35200 + i;
			addHoverText(id, combatMonstersList[i], "View " + combatMonstersList[i], tda, 0, 0xC9C2B4, false, true, 130, 16);
			combatMonsters.child(i, id, 4, 2 + (i * 16));
		}

		RSInterface combatRewards = addTabInterface(35220);
		combatRewards.width = 304;
		combatRewards.height = 34;
		combatRewards.scrollMax = 34;
		combatRewards.totalChildren(5);
		String[] rewardTiers = {"Easy", "Medium", "Hard", "Master", "Grandmaster"};
		for (int i = 0; i < rewardTiers.length; i++) {
			int id = 35230 + i;
			addHoverText(id, rewardTiers[i], "View " + rewardTiers[i] + " rewards", tda, 0, 0xF7AA25, false, true, 90, 16);
			combatRewards.child(i, id, 4 + (i * 58), 8);
		}
	}
}
