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

		// ---- Sprite Root ----
		// sprites/interfaces/vykna_achievements/<name>.png
		// NOTE: no ".png" in the string
		// private static final String SPRITE_ROOT = "interfaces/vykna_achievements/";

		// ---- Layout constants ----
		final int BG_X = 8, BG_Y = 8;
// Background is 497x322, keep it anchored at 8,8 like other interfaces
		final int NAV_X = BG_X + 16;
		final int NAV_Y = BG_Y + 50;
		final int TAB_GAP = 46;

// icon inset inside 36x36 (tweak later if needed)
		final int ICON_INSET = 7;


		// ---- Background ----
		addSprite(35001, 0, SPRITE_ROOT + "Background");

		/*
		 * Left tabs:
		 * - Selected tab is drawn as a sprite (LeftTabSelected) + icon sprite
		 * - Other tabs are hover buttons: LeftTabStandard -> LeftTabHover + icon sprite
		 *
		 * IMPORTANT: These buttons do nothing until you handle their button IDs server/client side.
		 */

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


		// ---- Children ----
		rsi.totalChildren(11);
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
	}



}
