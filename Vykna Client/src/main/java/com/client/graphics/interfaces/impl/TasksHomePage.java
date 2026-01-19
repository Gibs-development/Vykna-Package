package com.client.graphics.interfaces.impl;

import com.client.TextDrawingArea;
import com.client.graphics.interfaces.MenuItem;
import com.client.graphics.interfaces.RSInterface;

/**
 * Tasks page (mockup) built from AchievementHomePage base.
 * Minimal visuals: reuses achievement frame + atlas icons, no new sprites required yet.
 */
public final class TasksHomePage extends RSInterface {

    private static final String SPRITE_ROOT = "interfaces/vykna_achievements/";
    private static final String RECENT_ATLAS = SPRITE_ROOT + "AchievementRecentAtlas";

    // Interface ids (keep separate from 35000 range)
    public static final int INTERFACE_ID = 36000;

    // Dropdown + headers
    private static final int DROPDOWN_ID = 36010;
    private static final int TEXT_POINTS_HEADER = 36011;
    private static final int TEXT_SHOW_COMPLETED = 36012;

    // Scroll
    private static final int SCROLL_ID = 36050;

    // Progress bar
    private static final int TEXT_PROGRESS = 36080;
    private static final int PROGRESS_BAR_ID = 36090;

    // Dummy task rows
    private static final int ROW_START_ID = 36100;
    private static final int ROW_STRIDE = 20;

    // Dropdown colors (dark)
    private static final int[] DARK_DROPDOWN_COLORS = { 0x1a1a1a, 0x2a2a2a, 0x202224, 0x2b2e32, 0x34383d };

    // Categories shown in dropdown
    private static final String[] TASK_CATEGORIES = {
            "Misc", "Varrock", "Lumbridge", "Falador", "Ardougne", "Wilderness"
    };

    private static int currentCategory = 0;

    private TasksHomePage() {
    }

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

        r.sprite1 = imageLoader(0, atlasSpritePath);
        r.sprite2 = r.sprite1;

        r.width = cellSize;
        r.height = cellSize;

        r.gridCols = gridCols;
        r.gridRows = gridRows;
        r.gridCellSize = cellSize;

        r.valueIndex = valueIndex; // dummy
        r.configId = -1;

        r.tooltip = tooltip;
    }

    public static void refreshList(int categoryIndex) {
        if (categoryIndex < 0 || categoryIndex >= TASK_CATEGORIES.length) {
            categoryIndex = 0;
        }
        currentCategory = categoryIndex;

        String categoryName = TASK_CATEGORIES[categoryIndex];

        // Dummy progress values for now
        int completed = (categoryIndex * 3) % 25;
        int total = 25;
        int percent = (int) ((completed * 100.0) / total);

        // Update progress label
        if (RSInterface.interfaceCache[TEXT_PROGRESS] != null) {
            RSInterface.interfaceCache[TEXT_PROGRESS].message =
                    "Completion: " + completed + "/" + total + " (" + percent + "%)";
        }

        // Update rows (dummy content for now)
        for (int i = 0; i < 50; i++) {
            int base = ROW_START_ID + (i * ROW_STRIDE);

            if (RSInterface.interfaceCache[base + 1] != null) {
                RSInterface.interfaceCache[base + 1].message = categoryName + " Task " + (i + 1);
            }
            if (RSInterface.interfaceCache[base + 2] != null) {
                RSInterface.interfaceCache[base + 2].message = "Do something in " + categoryName + " to earn rewards.";
            }
            if (RSInterface.interfaceCache[base + 3] != null) {
                RSInterface.interfaceCache[base + 3].message = String.valueOf(5 + (i % 15));
            }
        }
    }

    public static void build(TextDrawingArea[] tda) {
        RSInterface rsi = addTabInterface(INTERFACE_ID);

        // ---- Layout constants (mirrors AchievementHomePage frame) ----
        final int BG_X = 8, BG_Y = 8;
        final int NAV_X = BG_X + 16;
        final int NAV_Y = BG_Y + 65;
        final int TAB_GAP = 46;
        final int ICON_INSET = 7;

        // Content anchor (main area to the right of tabs)
        final int MAIN_X = BG_X + 70;
        final int MAIN_Y = BG_Y + 40;

        // ---- Background (reuse) ----
        addSprite(INTERFACE_ID + 1, 0, SPRITE_ROOT + "Background");

        // ---- Left Tabs (reuse) ----
        // Selected = Tasks tab
        addSprite(INTERFACE_ID + 10, 0, SPRITE_ROOT + "LeftTabSelected");
        addSprite(INTERFACE_ID + 11, 0, SPRITE_ROOT + "TasksIcon");

        // Home / Achievements / Skilling / Combat / Mastery buttons
        addHoverButtonNew(INTERFACE_ID + 20, SPRITE_ROOT + "LeftTabStandard", SPRITE_ROOT + "LeftTabHover",
                36, 36, "Overview", 0, 1);
        addSprite(INTERFACE_ID + 23, 0, SPRITE_ROOT + "OverviewIcon");

        addHoverButtonNew(INTERFACE_ID + 30, SPRITE_ROOT + "LeftTabStandard", SPRITE_ROOT + "LeftTabHover",
                36, 36, "Achievements", 0, 1);
        // If you don't have AchievementsIcon, swap to any existing icon
        addSprite(INTERFACE_ID + 33, 0, SPRITE_ROOT + "AchievementsIcon");

        addHoverButtonNew(INTERFACE_ID + 40, SPRITE_ROOT + "LeftTabStandard", SPRITE_ROOT + "LeftTabHover",
                36, 36, "Skilling", 0, 1);
        addSprite(INTERFACE_ID + 43, 0, SPRITE_ROOT + "SkillingIcon");

        addHoverButtonNew(INTERFACE_ID + 60, SPRITE_ROOT + "LeftTabStandard", SPRITE_ROOT + "LeftTabHover",
                36, 36, "Combat Achievements", 0, 1);
        addSprite(INTERFACE_ID + 63, 0, SPRITE_ROOT + "CombatIcon");

        addHoverButtonNew(INTERFACE_ID + 70, SPRITE_ROOT + "LeftTabStandard", SPRITE_ROOT + "LeftTabHover",
                36, 36, "Mastery", 0, 1);
        addSprite(INTERFACE_ID + 73, 0, SPRITE_ROOT + "MasteryIcon");

        // ---- Top row controls ----
        dropdownMenu(
                DROPDOWN_ID, 166, 0, TASK_CATEGORIES,
                new MenuItem("Select task set") {
                    @Override
                    public void select(int optionSelected, RSInterface rsInterface) {
                        TasksHomePage.refreshList(optionSelected);
                    }

                    @Override
                    public void execute() {
                        // no-op for dropdowns in this interface
                    }
                },
                DARK_DROPDOWN_COLORS, false, Interfaces.defaultTextDrawingAreas, 1
        );

        addHoverText(TEXT_SHOW_COMPLETED, "show completed", "Toggle showing completed tasks",
                tda, 0, 0xE3AE19, false, true, 110, 16);

        addText(TEXT_POINTS_HEADER, "points", tda, 0, 0xE3AE19, false, true);

        // ---- Scroll container ----
        RSInterface scroll = addTabInterface(SCROLL_ID);
        scroll.width = 410;
        scroll.height = 234;

        final int ROW_H = 48;
        final int ICON_SIZE = 36;
        final int ROW_COUNT = 50;

        scroll.scrollMax = ROW_COUNT * ROW_H;
        scroll.totalChildren(ROW_COUNT * 5);

        for (int i = 0; i < ROW_COUNT; i++) {
            final int base = ROW_START_ID + (i * ROW_STRIDE);

            int valueIndex = (i % 4) + 1;
            addGridSpriteValueIndex(base + 0, RECENT_ATLAS, 2, 2, ICON_SIZE, valueIndex, "");

            // Start blank; refreshList() will populate
            addText(base + 1, "", tda, 0, 0xE3AE19, false, true);
            addText(base + 2, "", tda, 0, 0xFFFAE5, false, true);
            addText(base + 3, "", tda, 0, 0xFFFAE5, true, true); // points, right aligned

            addHoverText(base + 4, "", "View task", tda, 0, 0xFFFFFF, false, true, scroll.width, ROW_H);

            int y = i * ROW_H;
            scroll.child(i * 5 + 0, base + 0, 6, y + 6);
            scroll.child(i * 5 + 1, base + 1, 50, y + 4);
            scroll.child(i * 5 + 2, base + 2, 50, y + 20);
            scroll.child(i * 5 + 3, base + 3, scroll.width - 10, y + 4);
            scroll.child(i * 5 + 4, base + 4, 0, y);
        }

        // ---- Bottom completion progress ----
        addText(TEXT_PROGRESS, "", tda, 0, 0xFFFAE5, false, true);
        addSkinProgressBar2021(PROGRESS_BAR_ID, 406, 11);

        // ---- Children ----
        rsi.totalChildren(1 /*bg*/
                + 2 /*selected tab*/
                + 2 /*home btn+icon*/
                + 2 /*ach btn+icon*/
                + 2 /*skill btn+icon*/
                + 2 /*combat btn+icon*/
                + 2 /*mastery btn+icon*/
                + 1 /*dropdown*/
                + 1 /*show completed*/
                + 1 /*points*/
                + 1 /*scroll*/
                + 1 /*progress text*/
                + 4 /*progress bar parts*/);

        int c = 0;

        // BG
        rsi.child(c++, INTERFACE_ID + 1, BG_X, BG_Y);

        // Selected tab (Tasks)
        rsi.child(c++, INTERFACE_ID + 10, NAV_X, NAV_Y + (1 * TAB_GAP));
        rsi.child(c++, INTERFACE_ID + 11, NAV_X + ICON_INSET, NAV_Y + (1 * TAB_GAP) + ICON_INSET);

        // Home tab
        rsi.child(c++, INTERFACE_ID + 20, NAV_X, NAV_Y);
        rsi.child(c++, INTERFACE_ID + 23, NAV_X + ICON_INSET, NAV_Y + ICON_INSET);

        // Achievements tab
        rsi.child(c++, INTERFACE_ID + 30, NAV_X, NAV_Y + (2 * TAB_GAP));
        rsi.child(c++, INTERFACE_ID + 33, NAV_X + ICON_INSET, NAV_Y + (2 * TAB_GAP) + ICON_INSET);

        // Skilling tab
        rsi.child(c++, INTERFACE_ID + 40, NAV_X, NAV_Y + (3 * TAB_GAP));
        rsi.child(c++, INTERFACE_ID + 43, NAV_X + ICON_INSET, NAV_Y + (3 * TAB_GAP) + ICON_INSET);

        // Combat tab
        rsi.child(c++, INTERFACE_ID + 60, NAV_X, NAV_Y + (4 * TAB_GAP));
        rsi.child(c++, INTERFACE_ID + 63, NAV_X + ICON_INSET, NAV_Y + (4 * TAB_GAP) + ICON_INSET);

        // Mastery tab
        rsi.child(c++, INTERFACE_ID + 70, NAV_X, NAV_Y + (5 * TAB_GAP));
        rsi.child(c++, INTERFACE_ID + 73, NAV_X + ICON_INSET, NAV_Y + (5 * TAB_GAP) + ICON_INSET);

        // Top row
        rsi.child(c++, DROPDOWN_ID, MAIN_X + 4, MAIN_Y + 6);
        rsi.child(c++, TEXT_SHOW_COMPLETED, MAIN_X + 180, MAIN_Y + 10);
        rsi.child(c++, TEXT_POINTS_HEADER, MAIN_X + 360, MAIN_Y + 10);

        // Scroll
        rsi.child(c++, SCROLL_ID, MAIN_X + 4, MAIN_Y + 30);

        // Bottom progress
        rsi.child(c++, TEXT_PROGRESS, MAIN_X + 6, MAIN_Y + 270);

        final int BAR_X = MAIN_X + 4;
        final int BAR_Y = MAIN_Y + 286;
        rsi.child(c++, PROGRESS_BAR_ID, BAR_X, BAR_Y);
        rsi.child(c++, PROGRESS_BAR_ID + 1, BAR_X + 1, BAR_Y + 1);
        rsi.child(c++, PROGRESS_BAR_ID + 2, BAR_X + 2, BAR_Y + 2);
        rsi.child(c++, PROGRESS_BAR_ID + 3, BAR_X + 2, BAR_Y + 2);

        // Populate initial list (Misc)
        refreshList(0);
    }
}
