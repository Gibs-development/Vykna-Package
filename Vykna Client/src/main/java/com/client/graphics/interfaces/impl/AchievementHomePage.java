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

		// ---- Background & Close ----
		addSprite(35001, 0, SPRITE_ROOT + "rs3_bg");
		addHoverButton(35003, SPRITE_ROOT + "rs3_close", 0, 15, 15, "Close", 250, 35004, 3);
		addHoveredButton(35004, SPRITE_ROOT + "rs3_close", 0, 15, 15, 35005);

		// ---- Title ----
		addText(35010, Configuration.CLIENT_TITLE + " Achievements", tda, 2, 0xFFD966, true, true);

		// ---- Side Navigation ----
		addHoverButton(35020, SPRITE_ROOT + "rs3_nav_home", 0, 32, 32, "Home", 0, 35021, 1);
		addHoveredButton(35021, SPRITE_ROOT + "rs3_nav_home", 0, 32, 32, 35022);

		addHoverButton(35023, SPRITE_ROOT + "rs3_nav_achievements", 0, 32, 32, "Achievements", 0, 35024, 1);
		addHoveredButton(35024, SPRITE_ROOT + "rs3_nav_achievements", 0, 32, 32, 35025);

		addHoverButton(35026, SPRITE_ROOT + "rs3_nav_combat", 0, 32, 32, "Combat Achievements", 0, 35027, 1);
		addHoveredButton(35027, SPRITE_ROOT + "rs3_nav_combat", 0, 32, 32, 35028);

		addHoverButton(35029, SPRITE_ROOT + "rs3_nav_mastery", 0, 32, 32, "Mastery", 0, 35030, 1);
		addHoveredButton(35030, SPRITE_ROOT + "rs3_nav_mastery", 0, 32, 32, 35031);

		// ---- Main Panels ----
		addSprite(35040, 0, SPRITE_ROOT + "rs3_panel_medium"); // Overview
		addSprite(35050, 0, SPRITE_ROOT + "rs3_panel_medium"); // Recently completed
		addSprite(35060, 0, SPRITE_ROOT + "rs3_panel_medium"); // Almost finished
		addSprite(35070, 0, SPRITE_ROOT + "rs3_panel_large");  // Categories

		addSprite(35044, 0, SPRITE_ROOT + "rs3_progress_bar_frame");
		addSprite(35063, 0, SPRITE_ROOT + "rs3_progress_bar_frame");

		addText(35041, "Overview", tda, 1, 0xEED28A, false, true);
		addText(35042, "Achievements Completed", tda, 0, 0xC9C2B4, false, true);
		addText(35043, "0 / 0", tda, 0, 0xC9C2B4, true, true);
		addText(35045, "0%", tda, 0, 0x8FD3FF, false, true);

		addText(35051, "Recently completed", tda, 0, 0xC9C2B4, false, true);
		addItemContainer(35052, 4, 1, 10, 0, false);

		addText(35061, "You're almost finished", tda, 0, 0xC9C2B4, false, true);
		addText(35062, "Rank: None", tda, 0, 0xFFD966, false, true);
		addText(35064, "0%", tda, 0, 0x8FD3FF, false, true);

		addText(35071, "Categories", tda, 0, 0xC9C2B4, false, true);
		addText(35072, "Skills", tda, 0, 0x8FD3FF, false, true);
		addText(35073, "Exploration", tda, 0, 0x8FD3FF, false, true);
		addText(35074, "Combat", tda, 0, 0x8FD3FF, false, true);
		addText(35075, "Activities", tda, 0, 0x8FD3FF, false, true);

		// ---- Positions ----
		final int BG_X = 8, BG_Y = 8, BG_W = 512, BG_H = 334;
		final int NAV_X = BG_X + 18;
		final int NAV_Y = BG_Y + 60;
		final int MAIN_X = BG_X + 80;
		final int MAIN_Y = BG_Y + 40;

		rsi.totalChildren(32);
		int c = 0;

		rsi.child(c++, 35001, BG_X, BG_Y);
		rsi.child(c++, 35003, BG_X + BG_W - 26, BG_Y + 12);
		rsi.child(c++, 35004, BG_X + BG_W - 26, BG_Y + 12);
		rsi.child(c++, 35010, BG_X + (BG_W / 2), BG_Y + 14);

		rsi.child(c++, 35020, NAV_X, NAV_Y);
		rsi.child(c++, 35021, NAV_X, NAV_Y);
		rsi.child(c++, 35023, NAV_X, NAV_Y + 44);
		rsi.child(c++, 35024, NAV_X, NAV_Y + 44);
		rsi.child(c++, 35026, NAV_X, NAV_Y + 88);
		rsi.child(c++, 35027, NAV_X, NAV_Y + 88);
		rsi.child(c++, 35029, NAV_X, NAV_Y + 132);
		rsi.child(c++, 35030, NAV_X, NAV_Y + 132);

		rsi.child(c++, 35040, MAIN_X, MAIN_Y);
		rsi.child(c++, 35041, MAIN_X + 12, MAIN_Y + 10);
		rsi.child(c++, 35042, MAIN_X + 12, MAIN_Y + 32);
		rsi.child(c++, 35043, MAIN_X + 220, MAIN_Y + 32);
		rsi.child(c++, 35044, MAIN_X + 12, MAIN_Y + 50);
		rsi.child(c++, 35045, MAIN_X + 250, MAIN_Y + 48);

		rsi.child(c++, 35050, MAIN_X + 300, MAIN_Y);
		rsi.child(c++, 35051, MAIN_X + 312, MAIN_Y + 32);
		rsi.child(c++, 35052, MAIN_X + 312, MAIN_Y + 50);

		rsi.child(c++, 35060, MAIN_X, MAIN_Y + 130);
		rsi.child(c++, 35061, MAIN_X + 12, MAIN_Y + 148);
		rsi.child(c++, 35062, MAIN_X + 12, MAIN_Y + 166);
		rsi.child(c++, 35063, MAIN_X + 12, MAIN_Y + 184);
		rsi.child(c++, 35064, MAIN_X + 250, MAIN_Y + 182);

		rsi.child(c++, 35070, MAIN_X, MAIN_Y + 250);
		rsi.child(c++, 35071, MAIN_X + 12, MAIN_Y + 268);
		rsi.child(c++, 35072, MAIN_X + 12, MAIN_Y + 288);
		rsi.child(c++, 35073, MAIN_X + 100, MAIN_Y + 288);
		rsi.child(c++, 35074, MAIN_X + 200, MAIN_Y + 288);
		rsi.child(c++, 35075, MAIN_X + 300, MAIN_Y + 288);
	}


}
