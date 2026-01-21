package com.client.graphics.interfaces.impl;

import com.client.TextDrawingArea;
import com.client.graphics.interfaces.RSInterface;
import com.client.vykna_progression.ProgressionEntryDefinition;
import com.client.vykna_progression.ProgressionListType;
import com.client.vykna_progression.VyknaProgressionDefinitions;

import java.util.List;

/**
 * Achievement home page (interface 35000).
 *
 * Home page layout notes:
 * - Left nav tabs on the left.
 * - Overview / progress blocks on the left content area.
 * - "Most recent redundant_achievement" replaces the old 4-icon "recently completed" grid.
 *
 * Recent redundant_achievement icon uses a single atlas (type 17):
 *   interfaces/vykna_progression/AchievementRecentAtlas.png
 *
 * Dummy client-side data:
 *   valueIndex is set to 1 for the recent redundant_achievement icon.
 *   When server-side is ready, swap valueIndex usage to configId and have server send defIds.
 */
public final class AchievementHomePage extends RSInterface {

	private static final String SPRITE_ROOT = "interfaces/vykna_progression/";
	@SuppressWarnings("unused")
	private static final int LIST_SIZE = 200;
	private static final int CLOSE_BTN_ID = 35005;

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
		r.gridUseValueIndex = true;

		r.valueIndex = valueIndex; // dummy defId (1..4)
		r.configId = -1;          // later: set configId and ignore valueIndex

		r.tooltip = tooltip;
	}

	public static void build(TextDrawingArea[] tda) {
		RSInterface rsi = addTabInterface(35000);

		// ---- Layout constants ----
		final int BG_X = 8, BG_Y = 8;
		final int NAV_X = BG_X + 16;
		final int NAV_Y = BG_Y + 65;
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
				36, 36, "Tasks", 0, 1);
		addSprite(35023, 0, SPRITE_ROOT + "TasksIcon");

		addHoverButtonNew(35030, SPRITE_ROOT + "LeftTabStandard", SPRITE_ROOT + "LeftTabHover",
				36, 36, "Skilling", 0, 1);
		addSprite(35033, 0, SPRITE_ROOT + "SkillingIcon");

		addHoverButtonNew(35040, SPRITE_ROOT + "LeftTabStandard", SPRITE_ROOT + "LeftTabHover",
				36, 36, "Combat Progressions", 0, 1);
		addSprite(35043, 0, SPRITE_ROOT + "CombatIcon");

		addHoverButtonNew(35050, SPRITE_ROOT + "LeftTabStandard", SPRITE_ROOT + "LeftTabHover",
				36, 36, "Mastery", 0, 1);
		addSprite(35053, 0, SPRITE_ROOT + "MasteryIcon");

		// ---- Home Page Text + Progress Bars ----
		addText(35100, "Overview", tda, 1, 0xE3AE19, false, true);
		addText(35101, "Progressions Completed", tda, 0, 0xFFFAE5, false, true);
		addText(35102, "0/0", tda, 0, 0xFFFAE5, true, true);

		// main completion bar 203x11
		addSkinProgressBar2021(35200, 203, 11);

		// ---- Close button (top-right of the frame) ----
		addHoverButtonNew(CLOSE_BTN_ID, SPRITE_ROOT + "Close", SPRITE_ROOT + "CloseHover",
				16, 16, "Close", 0, 1);

		// ---- Most recent progression (replaces old 4-icon grid) ----
		addText(35110, "Most recent progression", tda, 0, 0xFFFAE5, false, true);
		addText(35111, "A FRIEND IN NEED", tda, 0, 0xE3AE19, false, true);
		addText(35112, "Complete a quest for a local.", tda, 0, 0xFFFAE5, false, true);

		// 1 recent icon (36x36) pulled from the atlas.
		// Atlas layout: 6 cols x 6 rows, each cell 36x36.
		final String RECENT_ATLAS = SPRITE_ROOT + "AchievementRecentAtlas";
		addGridSpriteValueIndex(35300, RECENT_ATLAS, 6, 6, 36, 1, "");

		addText(35120, "You're almost finished...", tda, 0, 0xFFFAE5, false, true);
		// Two "close to finish" achievements (dummy client-side)
		addText(35121, "DIARY OF OREB", tda, 0, 0xE3AE19, false, true);
		addText(35122, "Complete the diary tasks.", tda, 0, 0xFFFAE5, false, true);
		addText(35123, "RANK: MASTER FINIX", tda, 0, 0xE3AE19, false, true);
		addText(35124, "Finish the remaining objectives.", tda, 0, 0xFFFAE5, false, true);

		// Icons for those two achievements (pulled from the same 6x6 atlas)
		addGridSpriteValueIndex(35301, RECENT_ATLAS, 6, 6, 36, 2, "");
		addGridSpriteValueIndex(35302, RECENT_ATLAS, 6, 6, 36, 3, "");
		addSkinProgressBar2021(35210, 186, 11);
		addSkinProgressBar2021(35220, 186, 11);

		addText(35130, "Progress by category", tda, 1, 0xE3AE19, false, true);

		// 6 category mini bars (69x7)
		addSkinProgressBar2021(35230, 69, 7);
		addSkinProgressBar2021(35240, 69, 7);
		addSkinProgressBar2021(35250, 69, 7);
		addSkinProgressBar2021(35260, 69, 7);
		addSkinProgressBar2021(35270, 69, 7);
		addSkinProgressBar2021(35280, 69, 7);

		// ---- NEW: Achievement Summary + Ranks + Scores + Clickable list ----
		addText(35160, "Progression Summary", tda, 1, 0xE3AE19, false, true);

		addText(35161, "Rank 1: Player", tda, 0, 0xFFFAE5, false, true);
		addText(35162, "Score:", tda, 0, 0xFFFAE5, false, true);

		addText(35163, "Rank 2: Player", tda, 0, 0xFFFAE5, false, true);
		addText(35164, "Score:", tda, 0, 0xFFFAE5, false, true);

		addText(35165, "Rank 3: Player", tda, 0, 0xFFFAE5, false, true);
		addText(35166, "Score:", tda, 0, 0xFFFAE5, false, true);
// Clickable text (6 lines) - MUST be hover text like your example
// width/height are just sensible defaults; tweak if needed
		final int CLICK_W = 82;
		final int CLICK_H = 16;

		addHoverText(35170, "Quests", "Quests", tda, 0, 0xE3AE19, false, true, CLICK_W, CLICK_H);
		addHoverText(35171, "Tasks", "Tasks", tda, 0, 0xE3AE19, false, true, CLICK_W, CLICK_H);
		addHoverText(35172, "Skilling", "Skilling", tda, 0, 0xE3AE19, false, true, CLICK_W, CLICK_H);
		addHoverText(35173, "Combat", "Combat", tda, 0, 0xE3AE19, false, true, CLICK_W, CLICK_H);
		addHoverText(35174, "Collection", "Collections", tda, 0, 0xE3AE19, false, true, CLICK_W, CLICK_H);
		addHoverText(35175, "Mastery", "Mastery", tda, 0, 0xE3AE19, false, true, CLICK_W, CLICK_H);

		// ---- Children ----
		// Keep this count exact; otherwise you'll hit "Null child of index" errors.
		rsi.totalChildren(76);
		int c = 0;

		// BG
		rsi.child(c++, 35001, BG_X, BG_Y);

		// Close (top-right of the frame)
		rsi.child(c++, CLOSE_BTN_ID, BG_X + 470, BG_Y + 14);

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
		rsi.child(c++, 35102, MAIN_X + 200, MAIN_Y + 24);

		// main skinned bar at x,y
		final int MAIN_BAR_X = 86;
		final int MAIN_BAR_Y = 90;
		rsi.child(c++, 35200, MAIN_BAR_X, MAIN_BAR_Y);
		rsi.child(c++, 35201, MAIN_BAR_X + 1, MAIN_BAR_Y + 1);
		rsi.child(c++, 35202, MAIN_BAR_X + 2, MAIN_BAR_Y + 2);
		rsi.child(c++, 35203, MAIN_BAR_X + 2, MAIN_BAR_Y + 2);

		// ---- Most recent redundant_achievement ----
		// You said you'll add the rectangle background, so this is just icon + 2 lines.
		final int RECENT_BLOCK_X = MAIN_X + 4;
		final int RECENT_BLOCK_Y = MAIN_Y + 65;
		rsi.child(c++, 35110, RECENT_BLOCK_X, RECENT_BLOCK_Y);
		// Icon + title + description
		final int RECENT_ICON_X = RECENT_BLOCK_X;
		final int RECENT_ICON_Y = RECENT_BLOCK_Y + 18;
		rsi.child(c++, 35300, RECENT_ICON_X, RECENT_ICON_Y);
		rsi.child(c++, 35111, RECENT_ICON_X + 42, RECENT_ICON_Y + 2);
		rsi.child(c++, 35112, RECENT_ICON_X + 42, RECENT_ICON_Y + 18);

		// ---- Almost finished ----
		rsi.child(c++, 35120, MAIN_X + 4, MAIN_Y + 128);

		// 2 "close to finish" achievements (icon + 2 lines)
		final int AF_BLOCK_X = MAIN_X + 4;
		final int AF1_ICON_Y = MAIN_Y + 144;
		final int AF2_ICON_Y = MAIN_Y + 204;
		// entry 1
		rsi.child(c++, 35301, AF_BLOCK_X, AF1_ICON_Y);
		rsi.child(c++, 35121, AF_BLOCK_X + 42, AF1_ICON_Y + 2);
		rsi.child(c++, 35122, AF_BLOCK_X + 42, AF1_ICON_Y + 18);
		// entry 2
		rsi.child(c++, 35302, AF_BLOCK_X, AF2_ICON_Y);
		rsi.child(c++, 35123, AF_BLOCK_X + 42, AF2_ICON_Y + 2);
		rsi.child(c++, 35124, AF_BLOCK_X + 42, AF2_ICON_Y + 18);

		// bar 1
		final int AF1_X = 82;
		final int AF1_Y = MAIN_Y + 182;
		rsi.child(c++, 35210, AF1_X, AF1_Y);
		rsi.child(c++, 35211, AF1_X + 1, AF1_Y + 1);
		rsi.child(c++, 35212, AF1_X + 2, AF1_Y + 2);
		rsi.child(c++, 35213, AF1_X + 2, AF1_Y + 2);

		// bar 2
		final int AF2_X = 82;
		final int AF2_Y = MAIN_Y + 242;
		rsi.child(c++, 35220, AF2_X, AF2_Y);
		rsi.child(c++, 35221, AF2_X + 1, AF2_Y + 1);
		rsi.child(c++, 35222, AF2_X + 2, AF2_Y + 2);
		rsi.child(c++, 35223, AF2_X + 2, AF2_Y + 2);

		// ---- Categories ----
		rsi.child(c++, 35130, MAIN_X + 270, MAIN_Y + 6);

		final int CAT_X = MAIN_X + 249;
		final int CAT_Y = MAIN_Y + 49;
		final int CAT_GAP_X = 86;
		final int CAT_GAP_Y = 36;

		// 2 columns, 3 rows
		int x0 = CAT_X;
		int x1 = CAT_X + CAT_GAP_X;

		int y0 = CAT_Y;
		int y1 = CAT_Y + CAT_GAP_Y;
		int y2 = CAT_Y + (2 * CAT_GAP_Y);

		// row 1
		placeSkinnedBar(rsi, 35230, x0, y0, c); c += 4;
		placeSkinnedBar(rsi, 35240, x1, y0, c); c += 4;

		// row 2
		placeSkinnedBar(rsi, 35250, x0, y1, c); c += 4;
		placeSkinnedBar(rsi, 35260, x1, y1, c); c += 4;

		// row 3
		placeSkinnedBar(rsi, 35270, x0, y2, c); c += 4;
		placeSkinnedBar(rsi, 35280, x1, y2, c); c += 4;

		// ---- NEW: Summary block placement (rough positions; tweak freely) ----
		final int SUMMARY_X = MAIN_X + 270 + 25-2;
		final int SUMMARY_Y = MAIN_Y + 170 - 70+ 30;

		rsi.child(c++, 35160, 194, 19);           // "Achievement Summary"

		// Rank list (each with Score under it)
		rsi.child(c++, 35161, SUMMARY_X, SUMMARY_Y + 18+6);      // Rank 1
		rsi.child(c++, 35162, SUMMARY_X, SUMMARY_Y + 32+6);      // Score 1

		rsi.child(c++, 35163, SUMMARY_X, SUMMARY_Y + 52+10-1);      // Rank 2
		rsi.child(c++, 35164, SUMMARY_X, SUMMARY_Y + 66+10-1);      // Score 2

		rsi.child(c++, 35165, SUMMARY_X, SUMMARY_Y + 86+12);      // Rank 3
		rsi.child(c++, 35166, SUMMARY_X, SUMMARY_Y + 100+12);     // Score 3


		rsi.child(c++, 35170, 322+23, 82); // Quests
		rsi.child(c++, 35171, 410+23, 82); // Tasks
		rsi.child(c++, 35172,  322+23, 119); // Skilling
		rsi.child(c++, 35173, 410+23, 119); // Combat
		rsi.child(c++, 35174,322+23, 155); // Collections
		rsi.child(c++, 35175, 410+23, 155); // Mastery

		refreshProgressions();
		System.out.println("ProgressionHomePage final child count = " + c);
	}

	public static void refreshProgressions() {
		VyknaProgressionDefinitions.CompletionStats overall = VyknaProgressionDefinitions.getOverallStats();
		if (RSInterface.interfaceCache[35102] != null) {
			RSInterface.interfaceCache[35102].message = overall.getCompleted() + "/" + overall.getTotal();
		}
		setSkinnedBarPercentage(35200, overall.getRatio());

		VyknaProgressionDefinitions.CompletionStats tasks = VyknaProgressionDefinitions.getStatsForType(ProgressionListType.TASKS);
		VyknaProgressionDefinitions.CompletionStats skilling = VyknaProgressionDefinitions.getStatsForType(ProgressionListType.SKILLING);
		VyknaProgressionDefinitions.CompletionStats combat = VyknaProgressionDefinitions.getStatsForType(ProgressionListType.COMBAT);

		setSkinnedBarPercentage(35240, tasks.getRatio());
		setSkinnedBarPercentage(35250, skilling.getRatio());
		setSkinnedBarPercentage(35260, combat.getRatio());

		setSkinnedBarPercentage(35230, 0.0);
		setSkinnedBarPercentage(35270, 0.0);
		setSkinnedBarPercentage(35280, 0.0);

		List<ProgressionEntryDefinition> closest = VyknaProgressionDefinitions.getClosestIncomplete(2);
		updateAlmostFinishedEntry(0, closest);
		updateAlmostFinishedEntry(1, closest);
	}

	private static void updateAlmostFinishedEntry(int index, List<ProgressionEntryDefinition> closest) {
		int titleId = (index == 0) ? 35121 : 35123;
		int descId = (index == 0) ? 35122 : 35124;
		int iconId = (index == 0) ? 35301 : 35302;
		int barBaseId = (index == 0) ? 35210 : 35220;

		if (index >= closest.size()) {
			if (RSInterface.interfaceCache[titleId] != null) RSInterface.interfaceCache[titleId].message = "";
			if (RSInterface.interfaceCache[descId] != null) RSInterface.interfaceCache[descId].message = "";
			if (RSInterface.interfaceCache[iconId] != null) RSInterface.interfaceCache[iconId].interfaceHidden = true;
			setSkinnedBarVisible(barBaseId, false);
			return;
		}

		ProgressionEntryDefinition entry = closest.get(index);
		ProgressionListType type = ProgressionListType.fromId(entry.getListTypeId());
		String category = type.getDisplayName();
		int target = entry.getRequirementTarget();
		int current = Math.max(0, Math.min(entry.getProgressCurrent(), target));
		double ratio = target <= 0 ? 0.0 : current / (double) target;

		if (RSInterface.interfaceCache[titleId] != null) {
			RSInterface.interfaceCache[titleId].message = entry.getName() + " (" + category + ")";
		}
		if (RSInterface.interfaceCache[descId] != null) {
			RSInterface.interfaceCache[descId].message = current + "/" + target;
		}
		if (RSInterface.interfaceCache[iconId] != null) {
			RSInterface.interfaceCache[iconId].valueIndex = entry.getSpriteIndex();
			RSInterface.interfaceCache[iconId].interfaceHidden = false;
		}
		setSkinnedBarVisible(barBaseId, true);
		setSkinnedBarPercentage(barBaseId, ratio);
	}

	private static void setSkinnedBarPercentage(int baseId, double ratio) {
		double clamped = Math.max(0.0, Math.min(1.0, ratio));
		RSInterface fill = RSInterface.interfaceCache[baseId + 2];
		if (fill != null) {
			fill.progressBar2021Percentage = clamped;
		}
	}

	private static void setSkinnedBarVisible(int baseId, boolean visible) {
		for (int i = 0; i < 4; i++) {
			RSInterface bar = RSInterface.interfaceCache[baseId + i];
			if (bar != null) {
				bar.interfaceHidden = !visible;
			}
		}
	}
}
