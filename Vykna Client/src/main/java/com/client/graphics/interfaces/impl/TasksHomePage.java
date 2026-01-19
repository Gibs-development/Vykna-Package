package com.client.graphics.interfaces.impl;

import com.client.TextDrawingArea;
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

    // Header
    private static final int TEXT_TITLE = 36009;
    private static final int DROPDOWN_ID = 36010;
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

    // Task categories
    private static final String[] TASK_CATEGORIES = {
            "Lumbridge",
            "Varrock",
            "Falador",
            "Ardougne",
            "Wilderness",
            "Misc"
    };

    private static final class TaskRow {
        private final String title;
        private final String description;
        private final int points;
        private final boolean completed;

        private TaskRow(String title, String description, int points, boolean completed) {
            this.title = title;
            this.description = description;
            this.points = points;
            this.completed = completed;
        }
    }

    private static final TaskRow[][] TASKS_BY_CATEGORY = {
            {
                    new TaskRow("Lumbridge: Chop normal logs", "Chop logs from any tree in Lumbridge.", 5, true),
                    new TaskRow("Lumbridge: Catch shrimp", "Net shrimp in the river south of Lumbridge.", 5, false),
                    new TaskRow("Lumbridge: Bake bread", "Bake a loaf of bread in Lumbridge Castle.", 10, false),
                    new TaskRow("Lumbridge: Talk to the Duke", "Speak with the Duke in the castle.", 5, true),
                    new TaskRow("Lumbridge: Mine copper", "Mine copper ore in the mine south-east.", 5, false),
            },
            {
                    new TaskRow("Varrock: Visit the museum", "Enter the Varrock Museum and speak to the curator.", 5, true),
                    new TaskRow("Varrock: Buy a rune", "Purchase a rune from Aubury in the rune shop.", 10, false),
                    new TaskRow("Varrock: Mine iron", "Mine iron ore in the south-east mine.", 10, false),
                    new TaskRow("Varrock: Use the GE", "Place a buy offer on the Grand Exchange.", 15, false),
                    new TaskRow("Varrock: Talk to Zaff", "Speak to Zaff in the staff shop.", 5, true),
            },
            {
                    new TaskRow("Falador: Pray at altar", "Pray at the altar in Falador Castle.", 5, true),
                    new TaskRow("Falador: Mine tin", "Mine tin ore in the Dwarven Mine.", 10, false),
                    new TaskRow("Falador: Visit the park", "Walk through Falador Park.", 5, true),
                    new TaskRow("Falador: Take a charter", "Board a charter ship from the docks.", 15, false),
            },
            {
                    new TaskRow("Ardougne: Pickpocket a guard", "Pickpocket an Ardougne guard.", 15, false),
                    new TaskRow("Ardougne: Steal a cake", "Steal a cake from the market stall.", 10, true),
                    new TaskRow("Ardougne: Visit the zoo", "Walk through the Ardougne Zoo.", 5, true),
                    new TaskRow("Ardougne: Use the bank", "Deposit items at the Ardougne bank.", 5, false),
            },
            {
                    new TaskRow("Wilderness: Enter the wild", "Cross the ditch into the Wilderness.", 5, true),
                    new TaskRow("Wilderness: Kill a skeleton", "Defeat a skeleton in level 5 wilderness.", 10, false),
                    new TaskRow("Wilderness: Collect bones", "Pick up bones in the wilderness.", 5, false),
                    new TaskRow("Wilderness: Visit the obelisk", "Touch a wilderness obelisk.", 15, false),
            },
            {
                    new TaskRow("Misc: Check your tasks", "Open the tasks home screen.", 5, true),
                    new TaskRow("Misc: Talk to a banker", "Speak to any banker in the world.", 5, false),
                    new TaskRow("Misc: Equip armor", "Equip any piece of armor.", 5, false),
                    new TaskRow("Misc: Use a teleport", "Use any teleport to move around.", 10, false),
            }
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

        TaskRow[] tasks = TASKS_BY_CATEGORY[categoryIndex];
        int completed = 0;
        for (TaskRow task : tasks) {
            if (task.completed) {
                completed++;
            }
        }
        int total = tasks.length;
        int percent = total == 0 ? 0 : (int) ((completed * 100.0) / total);

        // Update progress label
        if (RSInterface.interfaceCache[TEXT_PROGRESS] != null) {
            RSInterface.interfaceCache[TEXT_PROGRESS].message =
                    "Completion: " + completed + "/" + total + " (" + percent + "%)";
        }

        // Update rows (dummy content for now)
        for (int i = 0; i < 50; i++) {
            int base = ROW_START_ID + (i * ROW_STRIDE);

            if (RSInterface.interfaceCache[base + 1] != null) {
                RSInterface.interfaceCache[base + 1].message =
                        i < tasks.length ? tasks[i].title : "";
            }
            if (RSInterface.interfaceCache[base + 2] != null) {
                RSInterface.interfaceCache[base + 2].message =
                        i < tasks.length ? tasks[i].description : "";
            }
            if (RSInterface.interfaceCache[base + 3] != null) {
                RSInterface.interfaceCache[base + 3].message =
                        i < tasks.length ? String.valueOf(tasks[i].points) : "";
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
        addSprite(INTERFACE_ID + 1, 0, SPRITE_ROOT + "/Tasks/Background");

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

        // ---- Title ----
        addText(TEXT_TITLE, "Tasks", tda, 2, 0xE3AE19, false, true);

        // ---- Top row controls ----
        dropdownMenu(
                DROPDOWN_ID, 166, 0, TASK_CATEGORIES,
                (optionSelected, rsInterface) -> TasksHomePage.refreshList(optionSelected),
                DARK_DROPDOWN_COLORS, false, tda, 1
        );

        addHoverText(TEXT_SHOW_COMPLETED, "show completed", "Toggle showing completed tasks",
                tda, 0, 0xE3AE19, false, true, 110, 16);

        // ---- Scroll container ----
        RSInterface scroll = addTabInterface(SCROLL_ID);
        scroll.width = 410;
        scroll.height = 234;

        final int ROW_H = 48;
        final int ICON_SIZE = 36;
        final int ROW_COUNT = 50;

        scroll.scrollMax = ROW_COUNT * ROW_H;
        scroll.totalChildren(ROW_COUNT * 6);

        for (int i = 0; i < ROW_COUNT; i++) {
            final int base = ROW_START_ID + (i * ROW_STRIDE);
            final int boxId = base + 5;

            int valueIndex = (i % 4) + 1;
            addBox(boxId, 0x3a3228, 0x2c261f, 120, scroll.width - 12, ROW_H - 6);
            addGridSpriteValueIndex(base + 0, RECENT_ATLAS, 2, 2, ICON_SIZE, valueIndex, "");

            // Start blank; refreshList() will populate
            addText(base + 1, "", tda, 0, 0xE3AE19, false, true);
            addText(base + 2, "", tda, 0, 0xFFFAE5, false, true);
            addText(base + 3, "", tda, 0, 0xFFFAE5, true, true); // points, right aligned

            addHoverText(base + 4, "", "View task", tda, 0, 0xFFFFFF, false, true, scroll.width, ROW_H);

            int y = i * ROW_H;
            scroll.child(i * 6 + 0, boxId, 2, y + 3);
            scroll.child(i * 6 + 1, base + 0, 6, y + 6);
            scroll.child(i * 6 + 2, base + 1, 50, y + 4);
            scroll.child(i * 6 + 3, base + 2, 50, y + 20);
            scroll.child(i * 6 + 4, base + 3, scroll.width - 10, y + 4);
            scroll.child(i * 6 + 5, base + 4, 0, y);
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
                + 1 /*title*/
                + 1 /*dropdown*/
                + 1 /*show completed*/
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

        // Title
        rsi.child(c++, TEXT_TITLE, MAIN_X + 4, MAIN_Y - 16);

        // Top row
        rsi.child(c++, DROPDOWN_ID, MAIN_X + 4, MAIN_Y + 6);
        rsi.child(c++, TEXT_SHOW_COMPLETED, MAIN_X + 180, MAIN_Y + 10);

        // Populate initial list (Misc)
        refreshList(0);
    }
}
