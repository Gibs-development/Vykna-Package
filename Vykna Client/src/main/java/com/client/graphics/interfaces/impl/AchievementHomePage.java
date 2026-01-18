package com.client.graphics.interfaces.impl;

import com.client.Configuration;
import com.client.TextDrawingArea;
import com.client.graphics.interfaces.RSInterface;

public final class AchievementHomePage extends RSInterface {

	private static final String SPRITE_ROOT = "interfaces/vykna_achievements/";
	private static final int LIST_SIZE = 200;

	private AchievementHomePage() {
	}
	public static void build(TextDrawingArea[] tda) {
		RSInterface rsi = addTabInterface(35000);

		// ---- Layout constants ----
		final int BG_X = 8, BG_Y = 8;
		final int NAV_X = BG_X + 16;
		final int NAV_Y = BG_Y + 50;
		final int TAB_GAP = 46;
		final int ICON_INSET = 7;

		// Content anchor (main area to the right of tabs)
		final int MAIN_X = BG_X + 70;
		final int MAIN_Y = BG_Y + 40;

		// ---- Background ----
		addSprite(35001, 0, SPRITE_ROOT + "Background");

		// ---- Left Tabs ----
		addSprite(35010, 0, SPRITE_ROOT + "LeftTabSelected");
		addSprite(35011, 0, SPRITE_ROOT + "OverviewIcon");

		addHoverButtonNew(35020, SPRITE_ROOT + "LeftTabStandard", SPRITE_ROOT + "LeftTabHover",
				36, 36, "Achievements", 0, 1);
		addSprite(35023, 0, SPRITE_ROOT + "TasksIcon");

		addHoverButtonNew(35030, SPRITE_ROOT + "LeftTabStandard", SPRITE_ROOT + "LeftTabHover",
				36, 36, "Skilling", 0, 1);
		addSprite(35033, 0, SPRITE_ROOT + "SkillingIcon");

		addHoverButtonNew(35040, SPRITE_ROOT + "LeftTabStandard", SPRITE_ROOT + "LeftTabHover",
				36, 36, "Combat Achievements", 0, 1);
		addSprite(35043, 0, SPRITE_ROOT + "CombatIcon");

		addHoverButtonNew(35050, SPRITE_ROOT + "LeftTabStandard", SPRITE_ROOT + "LeftTabHover",
				36, 36, "Mastery", 0, 1);
		addSprite(35053, 0, SPRITE_ROOT + "MasteryIcon");

		// ---- Home Page Text + Progress Bars ----
		addText(35100, "Overview", tda, 1, 0x1E1E1E, false, true);
		addText(35101, "Achievements Completed", tda, 0, 0x1E1E1E, false, true);
		addText(35102, "0/0", tda, 0, 0x1E1E1E, true, true);

		// main completion bar 203x11
		addProgressBar2021(35103, 203, 11, 0x2A2A2A);

		addText(35110, "Recently completed", tda, 0, 0x1E1E1E, false, true);

		addText(35120, "You're almost finished...", tda, 0, 0x1E1E1E, false, true);
		addProgressBar2021(35121, 186, 11, 0x2A2A2A);
		addProgressBar2021(35122, 186, 11, 0x2A2A2A);

		addText(35130, "Categories", tda, 0, 0x1E1E1E, false, true);

		// 6 category mini bars (69x7)
		addProgressBar2021(35131, 69, 7, 0x2A2A2A);
		addProgressBar2021(35132, 69, 7, 0x2A2A2A);
		addProgressBar2021(35133, 69, 7, 0x2A2A2A);
		addProgressBar2021(35134, 69, 7, 0x2A2A2A);
		addProgressBar2021(35135, 69, 7, 0x2A2A2A);
		addProgressBar2021(35136, 69, 7, 0x2A2A2A);

		// ---- Children ----
		// 11 existing + 1+1+1+1 + 1 + 1+2 + 1 + 6 = 25 total
		rsi.totalChildren(26);
		int c = 0;

		// BG
		rsi.child(c++, 35001, BG_X, BG_Y);

		// Selected tab (Home)
		rsi.child(c++, 35010, NAV_X, NAV_Y);
		rsi.child(c++, 35011, NAV_X + ICON_INSET, NAV_Y + ICON_INSET);

		// Achievements tab (button + icon)
		rsi.child(c++, 35020, NAV_X, NAV_Y + (1 * TAB_GAP));
		rsi.child(c++, 35023, NAV_X + ICON_INSET, NAV_Y + (1 * TAB_GAP) + ICON_INSET);

		// Skilling tab
		rsi.child(c++, 35030, NAV_X, NAV_Y + (2 * TAB_GAP));
		rsi.child(c++, 35033, NAV_X + ICON_INSET, NAV_Y + (2 * TAB_GAP) + ICON_INSET);

		// Combat tab
		rsi.child(c++, 35040, NAV_X, NAV_Y + (3 * TAB_GAP));
		rsi.child(c++, 35043, NAV_X + ICON_INSET, NAV_Y + (3 * TAB_GAP) + ICON_INSET);

		// Mastery tab
		rsi.child(c++, 35050, NAV_X, NAV_Y + (4 * TAB_GAP));
		rsi.child(c++, 35053, NAV_X + ICON_INSET, NAV_Y + (4 * TAB_GAP) + ICON_INSET);

		// ---- Overview block ----
		rsi.child(c++, 35100, MAIN_X + 18, MAIN_Y + 6);            // "Overview"
		rsi.child(c++, 35101, MAIN_X + 18, MAIN_Y + 26);           // "Achievements Completed"
		rsi.child(c++, 35102, MAIN_X + 250, MAIN_Y + 26);          // "0/0" right aligned-ish
		rsi.child(c++, 35103, MAIN_X + 90, MAIN_Y + 44);           // main progress bar (203x11)

		// ---- Recently completed ----
		rsi.child(c++, 35110, MAIN_X + 18, MAIN_Y + 70);

		// ---- Almost finished ----
		rsi.child(c++, 35120, MAIN_X + 18, MAIN_Y + 140);
		rsi.child(c++, 35121, MAIN_X + 60, MAIN_Y + 162);          // prog 186x11
		rsi.child(c++, 35122, MAIN_X + 60, MAIN_Y + 202);          // prog 186x11

		// ---- Categories ----
		rsi.child(c++, 35130, MAIN_X + 18, MAIN_Y + 235);

		// 3x2 grid of 69x7 bars
		final int CAT_X = MAIN_X + 18;
		final int CAT_Y = MAIN_Y + 265;
		final int CAT_GAP_X = 86; // 69 width + ~17 spacing
		final int CAT_GAP_Y = 36;

		rsi.child(c++, 35131, CAT_X + (0 * CAT_GAP_X), CAT_Y + (0 * CAT_GAP_Y));
		rsi.child(c++, 35132, CAT_X + (1 * CAT_GAP_X), CAT_Y + (0 * CAT_GAP_Y));
		rsi.child(c++, 35133, CAT_X + (2 * CAT_GAP_X), CAT_Y + (0 * CAT_GAP_Y));

		rsi.child(c++, 35134, CAT_X + (0 * CAT_GAP_X), CAT_Y + (1 * CAT_GAP_Y));
		rsi.child(c++, 35135, CAT_X + (1 * CAT_GAP_X), CAT_Y + (1 * CAT_GAP_Y));
		rsi.child(c++, 35136, CAT_X + (2 * CAT_GAP_X), CAT_Y + (1 * CAT_GAP_Y));
	}




}
