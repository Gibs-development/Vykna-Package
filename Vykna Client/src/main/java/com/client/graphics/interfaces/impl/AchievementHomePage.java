package com.client.graphics.interfaces.impl;

import com.client.TextDrawingArea;
import com.client.graphics.interfaces.RSInterface;

/**
 * Achievement home page (interface 35000).
 *
 * Adds 4 "recent completion" grid sprites (type 17) using a single atlas:
 *   interfaces/vykna_achievements/AchievementRecentAtlas.png
 *
 * Dummy client-side data:
 *   valueIndex is set to 1..4 for the 4 recent slots.
 *   When server-side is ready, swap valueIndex usage to configId and have server send defIds.
 */
public final class AchievementHomePage extends RSInterface {

	private static final String SPRITE_ROOT = "interfaces/vykna_achievements/";
	@SuppressWarnings("unused")
	private static final int LIST_SIZE = 200;

	private AchievementHomePage() {
	}

	/**
	 * Creates a "grid sprite" interface component (type 17).
	 *
	 * IMPORTANT:
	 * This requires these fields to exist on RSInterface:
	 *   public int gridCols, gridRows, gridCellSize;
	 *   public int valueIndex;      // used for dummy data now
	 *   public int configId = -1;   // used later for variousSettings[configId]
	 *
	 * And you need Sprite.getSubSprite(...) implemented (see NOTES.txt).
	 */
	private static void addGridSpriteValueIndex(int id, String atlasSpritePath,
												int gridCols, int gridRows, int cellSize,
												int valueIndex, String tooltip) {
		RSInterface r = addInterface(id);
		r.id = id;
		r.parentID = id;
		r.type = 17;
		r.atActionType = 0;
		r.contentType = 0;
		r.aByte254 = 0;

		r.sprite1 = imageLoader(0, atlasSpritePath); // atlas
		r.sprite2 = r.sprite1;

		r.width = cellSize;
		r.height = cellSize;

		r.gridCols = gridCols;
		r.gridRows = gridRows;
		r.gridCellSize = cellSize;

		r.valueIndex = valueIndex; // dummy defId (1..4)
		r.configId = -1;          // later: set configId and ignore valueIndex

		r.tooltip = tooltip;
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
		addText(35100, "Overview", tda, 1, 0xE3AE19, false, true);
		addText(35101, "Achievements Completed", tda, 0, 0xFFFAE5, false, true);
		addText(35102, "0/0", tda, 0, 0xFFFAE5, true, true);

		// main completion bar 203x11
		addSkinProgressBar2021(35200, 203, 11);

		addText(35110, "Recently completed", tda, 0, 0xFFFAE5, false, true);

		// ---- Recent completion grid sprites (4 icons @ 36x36) ----
		// Atlas layout: 2 cols x 2 rows, each cell 36x36.
		// Dummy IDs 1..4 correspond to dummy AchievementDefinitions entries.
		final String RECENT_ATLAS = SPRITE_ROOT + "AchievementRecentAtlas";
		addGridSpriteValueIndex(35300, RECENT_ATLAS, 2, 2, 36, 1, "Recent #1");
		addGridSpriteValueIndex(35301, RECENT_ATLAS, 2, 2, 36, 2, "Recent #2");
		addGridSpriteValueIndex(35302, RECENT_ATLAS, 2, 2, 36, 3, "Recent #3");
		addGridSpriteValueIndex(35303, RECENT_ATLAS, 2, 2, 36, 4, "Recent #4");

		addText(35120, "You're almost finished...", tda, 0, 0xFFFAE5, false, true);
		addSkinProgressBar2021(35210, 186, 11);
		addSkinProgressBar2021(35220, 186, 11);

		addText(35130, "Categories", tda, 0, 0xE3AE19, false, true);

		// 6 category mini bars (69x7)
		addSkinProgressBar2021(35230, 69, 7);
		addSkinProgressBar2021(35240, 69, 7);
		addSkinProgressBar2021(35250, 69, 7);
		addSkinProgressBar2021(35260, 69, 7);
		addSkinProgressBar2021(35270, 69, 7);
		addSkinProgressBar2021(35280, 69, 7);

		// ---- Children ----
		rsi.totalChildren(220);
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
		rsi.child(c++, 35100, MAIN_X + 4, MAIN_Y + 6);
		rsi.child(c++, 35101, MAIN_X + 4, MAIN_Y + 24);
		rsi.child(c++, 35102, MAIN_X + 210, MAIN_Y + 24);

		// main skinned bar at x,y
		final int MAIN_BAR_X = 86;
		final int MAIN_BAR_Y = 90;
		rsi.child(c++, 35200, MAIN_BAR_X, MAIN_BAR_Y);
		rsi.child(c++, 35201, MAIN_BAR_X + 1, MAIN_BAR_Y + 1);
		rsi.child(c++, 35202, MAIN_BAR_X + 2, MAIN_BAR_Y + 2);
		rsi.child(c++, 35203, MAIN_BAR_X + 2, MAIN_BAR_Y + 2);

		// ---- Recently completed ----
		rsi.child(c++, 35110, MAIN_X + 4, MAIN_Y + 65);

		// 4 recent icons (adjust positions later)
		final int RECENT_X = MAIN_X + 4;
		final int RECENT_Y = MAIN_Y + 82;
		final int RECENT_GAP = 40; // 36 + 4 spacing
		rsi.child(c++, 35300, RECENT_X + (0 * RECENT_GAP), RECENT_Y);
		rsi.child(c++, 35301, RECENT_X + (1 * RECENT_GAP), RECENT_Y);
		rsi.child(c++, 35302, RECENT_X + (2 * RECENT_GAP), RECENT_Y);
		rsi.child(c++, 35303, RECENT_X + (3 * RECENT_GAP), RECENT_Y);

		// ---- Almost finished ----
		rsi.child(c++, 35120, MAIN_X + 18, MAIN_Y + 140);

		// bar 1
		final int AF1_X = MAIN_X + 60;
		final int AF1_Y = MAIN_Y + 162;
		rsi.child(c++, 35210, AF1_X, AF1_Y);
		rsi.child(c++, 35211, AF1_X + 1, AF1_Y + 1);
		rsi.child(c++, 35212, AF1_X + 2, AF1_Y + 2);
		rsi.child(c++, 35213, AF1_X + 2, AF1_Y + 2);

		// bar 2
		final int AF2_X = MAIN_X + 60;
		final int AF2_Y = MAIN_Y + 202;
		rsi.child(c++, 35220, AF2_X, AF2_Y);
		rsi.child(c++, 35221, AF2_X + 1, AF2_Y + 1);
		rsi.child(c++, 35222, AF2_X + 2, AF2_Y + 2);
		rsi.child(c++, 35223, AF2_X + 2, AF2_Y + 2);

		// ---- Categories ----
		rsi.child(c++, 35130, MAIN_X + 18, MAIN_Y + 235);

		final int CAT_X = MAIN_X + 18;
		final int CAT_Y = MAIN_Y + 265;
		final int CAT_GAP_X = 86;
		final int CAT_GAP_Y = 36;

		int x0 = CAT_X + (0 * CAT_GAP_X), x1 = CAT_X + (1 * CAT_GAP_X), x2 = CAT_X + (2 * CAT_GAP_X);
		int y0 = CAT_Y + (0 * CAT_GAP_Y), y1 = CAT_Y + (1 * CAT_GAP_Y);

		// row 1
		placeSkinnedBar(rsi, 35230, x0, y0, c); c += 4;
		placeSkinnedBar(rsi, 35240, x1, y0, c); c += 4;
		placeSkinnedBar(rsi, 35250, x2, y0, c); c += 4;

		// row 2
		placeSkinnedBar(rsi, 35260, x0, y1, c); c += 4;
		placeSkinnedBar(rsi, 35270, x1, y1, c); c += 4;
		placeSkinnedBar(rsi, 35280, x2, y1, c); c += 4;

		// Dummy % values (client-side visual testing)
		RSInterface.interfaceCache[35202].progressBar2021Percentage = 0.65;
		RSInterface.interfaceCache[35212].progressBar2021Percentage = 0.42;
		RSInterface.interfaceCache[35222].progressBar2021Percentage = 0.18;

		RSInterface.interfaceCache[35232].progressBar2021Percentage = 0.90;
		RSInterface.interfaceCache[35242].progressBar2021Percentage = 0.60;
		RSInterface.interfaceCache[35252].progressBar2021Percentage = 0.25;
		RSInterface.interfaceCache[35262].progressBar2021Percentage = 0.80;
		RSInterface.interfaceCache[35272].progressBar2021Percentage = 0.10;
		RSInterface.interfaceCache[35282].progressBar2021Percentage = 0.45;

		System.out.println("AchievementHomePage final child count = " + c);
	}
}
