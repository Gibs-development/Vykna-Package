package com.client.graphics.interfaces.impl;

import com.client.TextDrawingArea;
import com.client.graphics.interfaces.RSInterface;

/**
 * Tasks page (mockup) built from AchievementHomePage base.
 *
 * Fixes:
 *  1) ID COLLISION PROOFING:
 *     Your original ids used small offsets (+20/+23/+30/+33 etc). Many interface helper methods
 *     (especially addHoverButtonNew) internally create extra interfaces using id+1, id+2, ...
 *     That was overwriting your icon/sprite ids and even the dropdown, causing "two dropdowns",
 *     icons teleporting, and random sprites appearing near the dropdown.
 *
 *     This version allocates SAFE "blocks" of ids for each button, header controls, scroll rows, etc.
 *
 *  2) Scroll row components are explicitly parented to SCROLL_ID, preventing any row background/box
 *     from rendering at (0,0) or under the left tabs.
 *
 * IMPORTANT:
 *  - INTERFACE_ID remains 36000 so your existing open-interface code keeps working.
 */
public final class AchievementListPage extends RSInterface {

    private static final String SPRITE_ROOT = "interfaces/vykna_progression/";
    private static final String RECENT_ATLAS = SPRITE_ROOT + "AchievementRecentAtlas";

    // Keep same interface id so openInterface packets/etc still work
    public static final int INTERFACE_ID = 64504;

    // ----- SAFE ID RANGES (avoid helper-method collisions) -----

    // Background
    private static final int BG_ID = INTERFACE_ID + 1;

    // Left nav sprites/buttons (put them in a high block)
    private static final int NAV_SELECTED_BG = INTERFACE_ID + 1100;
    private static final int NAV_SELECTED_ICON = INTERFACE_ID + 1101;

    private static final int NAV_HOME_BTN = INTERFACE_ID + 1200;
    private static final int NAV_HOME_ICON = INTERFACE_ID + 1210;

    private static final int NAV_SKILL_BTN = INTERFACE_ID + 1240;
    private static final int NAV_SKILL_ICON = INTERFACE_ID + 1250;

    private static final int NAV_COMBAT_BTN = INTERFACE_ID + 1260;
    private static final int NAV_COMBAT_ICON = INTERFACE_ID + 1270;

    private static final int NAV_MASTERY_BTN = INTERFACE_ID + 1280;
    private static final int NAV_MASTERY_ICON = INTERFACE_ID + 1290;

    // Header / top controls (separate block)
    private static final int TEXT_TITLE = INTERFACE_ID + 2000;
    private static final int CLOSE_BTN_ID = INTERFACE_ID + 2005;
    private static final int DROPDOWN_ID = INTERFACE_ID + 2010;
    private static final int TEXT_SHOW_COMPLETED = INTERFACE_ID + 2020;

    // Scroll + rows (separate block)
    private static final int SCROLL_ID = INTERFACE_ID + 3000;

    private static final int ROW_START_ID = INTERFACE_ID + 3100;
    private static final int ROW_STRIDE = 30;

    // Progress bar (separate block)
    private static final int TEXT_PROGRESS = INTERFACE_ID + 4000;
    private static final int PROGRESS_BAR_ID = INTERFACE_ID + 4010;

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

        // Optional per-task progress (e.g. 21/50). If target <= 0, bar is hidden.
        private final int progressCurrent;
        private final int progressTarget;

        private TaskRow(String title, String description, int points, boolean completed) {
            this.title = title;
            this.description = description;
            this.points = points;
            this.completed = completed;
            this.progressCurrent = 0;
            this.progressTarget = 0;
        }

        private TaskRow(String title, String description, int points, boolean completed, int progressCurrent, int progressTarget) {
            this.title = title;
            this.description = description;
            this.points = points;
            this.completed = completed;
            this.progressCurrent = progressCurrent;
            this.progressTarget = progressTarget;
        }
    }

    private static final TaskRow[][] TASKS_BY_CATEGORY = {
            {
                    new TaskRow("Lumbridge: Chop normal logs", "Chop logs from any tree in Lumbridge.", 5, true, 21, 50),
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

    // Scroll layout (shared by build + refresh)
    private static final int ROW_H = 48;
    private static final int ICON_SIZE = 36;
    private static final int ROWS_HIDDEN_Y = 2000; // push unused rows out of the scroll viewport

    // Per-row progress bar
    private static final int ROW_BAR_W = 120;
    private static final int ROW_BAR_H = 12;

    /**
     * The interface is built once, so we allocate enough rows for the largest category.
     * On refresh we reposition/hide unused rows and shrink scrollMax so the scrollbar matches.
     */
    private static int maxTaskCount() {
        int max = 0;
        for (TaskRow[] rows : TASKS_BY_CATEGORY) {
            if (rows != null && rows.length > max) {
                max = rows.length;
            }
        }
        return max;
    }

    private AchievementListPage() {}

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
            if (task.completed) completed++;
        }
        int total = tasks.length;
        int percent = total == 0 ? 0 : (int) ((completed * 100.0) / total);

        if (RSInterface.interfaceCache[TEXT_PROGRESS] != null) {
            RSInterface.interfaceCache[TEXT_PROGRESS].message =
                    "Completion: " + completed + "/" + total + " (" + percent + "%)";
        }

        // Resize the scrollbar to match the number of tasks in this category.
        // Note: scrollMax should never be smaller than the scroll height.
        RSInterface scroll = RSInterface.interfaceCache[SCROLL_ID];
        if (scroll != null) {
            scroll.scrollMax = Math.max(tasks.length * ROW_H, scroll.height);
        }

        // Fill rows we need, and push unused rows out of view so we don't render empty boxes.
        final int rowCount = maxTaskCount();
        for (int i = 0; i < rowCount; i++) {
            int base = ROW_START_ID + (i * ROW_STRIDE);
            int boxId = base + 5;

            boolean active = i < tasks.length;

            if (RSInterface.interfaceCache[base + 1] != null) {
                RSInterface.interfaceCache[base + 1].message = active ? tasks[i].title : "";
            }
            if (RSInterface.interfaceCache[base + 2] != null) {
                RSInterface.interfaceCache[base + 2].message = active ? tasks[i].description : "";
            }
            if (RSInterface.interfaceCache[base + 3] != null) {
                RSInterface.interfaceCache[base + 3].message = active ? "<icon=0>" + tasks[i].points : "";
            }

            // Per-task progress bar + [x/y] in the middle
            final int barBgId = base + 6;
            final int barFillId = base + 7;
            final int barTextId = base + 8;

            if (RSInterface.interfaceCache[barTextId] != null) {
                if (active && tasks[i].progressTarget > 0) {
                    RSInterface.interfaceCache[barTextId].message = "[" + tasks[i].progressCurrent + "/" + tasks[i].progressTarget + "]";
                } else {
                    RSInterface.interfaceCache[barTextId].message = "";
                }
            }

            if (RSInterface.interfaceCache[barFillId] != null) {
                if (active && tasks[i].progressTarget > 0) {
                    int pct = (int) ((tasks[i].progressCurrent * 100L) / Math.max(1, tasks[i].progressTarget));
                    if (pct < 0) pct = 0;
                    if (pct > 100) pct = 100;
                    int w = (ROW_BAR_W * pct) / 100;
                    RSInterface.interfaceCache[barFillId].width = w;
                } else {
                    RSInterface.interfaceCache[barFillId].width = 0;
                }
            }

            // Reposition the row children (9 per row) so unused rows don't draw.
            if (scroll != null && scroll.childY != null) {
                int childBase = i * 9;
                int y = active ? (i * ROW_H) : ROWS_HIDDEN_Y;

                // box
                if (childBase + 0 < scroll.childY.length) scroll.childY[childBase + 0] = y + 3;
                // icon
                if (childBase + 1 < scroll.childY.length) scroll.childY[childBase + 1] = y + 6;
                // title
                if (childBase + 2 < scroll.childY.length) scroll.childY[childBase + 2] = y + 4;
                // desc
                if (childBase + 3 < scroll.childY.length) scroll.childY[childBase + 3] = y + 20;
                // points
                if (childBase + 4 < scroll.childY.length) scroll.childY[childBase + 4] = y + 10;
                // hover
                if (childBase + 5 < scroll.childY.length) scroll.childY[childBase + 5] = y;

                // progress bg
                if (childBase + 6 < scroll.childY.length) scroll.childY[childBase + 6] = y + 30;
                // progress fill
                if (childBase + 7 < scroll.childY.length) scroll.childY[childBase + 7] = y + 31;
                // progress text
                if (childBase + 8 < scroll.childY.length) scroll.childY[childBase + 8] = y + 31;
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

        // ---- Background ----
        addSprite(BG_ID, 0, SPRITE_ROOT + "/Tasks/Background");

        // ---- Left Tabs ----
        // Selected = Tasks tab
        addSprite(NAV_SELECTED_BG, 0, SPRITE_ROOT + "LeftTabSelected");
        addSprite(NAV_SELECTED_ICON, 0, SPRITE_ROOT + "TasksIcon");

        // Home / Achievements / Skilling / Combat / Mastery buttons
        addHoverButtonNew(NAV_HOME_BTN, SPRITE_ROOT + "LeftTabStandard", SPRITE_ROOT + "LeftTabHover",
                36, 36, "Overview", 0, 1);
        addSprite(NAV_HOME_ICON, 0, SPRITE_ROOT + "OverviewIcon");

        addHoverButtonNew(NAV_SKILL_BTN, SPRITE_ROOT + "LeftTabStandard", SPRITE_ROOT + "LeftTabHover",
                36, 36, "Skilling", 0, 1);
        addSprite(NAV_SKILL_ICON, 0, SPRITE_ROOT + "SkillingIcon");

        addHoverButtonNew(NAV_COMBAT_BTN, SPRITE_ROOT + "LeftTabStandard", SPRITE_ROOT + "LeftTabHover",
                36, 36, "Combat Achievements", 0, 1);
        addSprite(NAV_COMBAT_ICON, 0, SPRITE_ROOT + "CombatIcon");

        addHoverButtonNew(NAV_MASTERY_BTN, SPRITE_ROOT + "LeftTabStandard", SPRITE_ROOT + "LeftTabHover",
                36, 36, "Mastery", 0, 1);
        addSprite(NAV_MASTERY_ICON, 0, SPRITE_ROOT + "MasteryIcon");

        // ---- Title ----
        addText(TEXT_TITLE, "Task System", tda, 2, 0xE3AE19, false, true);

        // ---- Close button (top-right) ----
        // Uses the same sprite path style as the rest of the interface. If your client uses cacheSprite3-based
        // close buttons, swap this to RSInterface.closeButton(...) and point at the correct sprite indices.
        addHoverButtonNew(CLOSE_BTN_ID,
                SPRITE_ROOT + "Close",
                SPRITE_ROOT + "CloseHover",
                16, 16, "Close", 0, 1);

        // ---- Top row controls ----
        dropdownMenu(
                DROPDOWN_ID, 166, 0, TASK_CATEGORIES,
                (optionSelected, rsInterface) -> AchievementListPage.refreshList(optionSelected),
                DARK_DROPDOWN_COLORS, false, tda, 1
        );

        addHoverText(TEXT_SHOW_COMPLETED, "Show Completed?", "Toggle showing completed tasks",
                tda, 0, 0xE3AE19, false, true, 110, 16);

        // ---- Scroll container ----
        RSInterface scroll = addTabInterface(SCROLL_ID);
        scroll.width = 390;
        scroll.height = 210;

        final int ROW_COUNT = maxTaskCount();

        // Default to max category size; refreshList() will shrink this per selected category.
        scroll.scrollMax = Math.max(ROW_COUNT * ROW_H, scroll.height);
        scroll.totalChildren(ROW_COUNT * 9);

        for (int i = 0; i < ROW_COUNT; i++) {
            final int base = ROW_START_ID + (i * ROW_STRIDE);
            final int boxId = base + 5;
            final int barBgId = base + 6;
            final int barFillId = base + 7;
            final int barTextId = base + 8;

            int valueIndex = (i % 4) + 1;
            addBox(boxId, 0x3a3228, 0x2c261f, 120, scroll.width - 12, ROW_H - 6);
            addGridSpriteValueIndex(base + 0, RECENT_ATLAS, 2, 2, ICON_SIZE, valueIndex, "");

            addText(base + 1, "", tda, 0, 0xE3AE19, false, true);
            addText(base + 2, "", tda, 0, 0xFFFAE5, false, true);
            addText(base + 3, "", tda, 0, 0xFFFAE5, true, true); // points, right aligned
            addHoverText(base + 4, "", "View task", tda, 0, 0xFFFFFF, false, true, scroll.width, ROW_H);

            // Per-row progress (hidden if target <= 0)
            addBox(barBgId, 0x2c261f, 0x1f1a15, 120, ROW_BAR_W, ROW_BAR_H);
            addBox(barFillId, 0x6f5a2e, 0x6f5a2e, 120, ROW_BAR_W, ROW_BAR_H - 2);
            addText(barTextId, "", tda, 0, 0xFFFAE5, true, true);

            // Force scroll parent so nothing draws outside the scroll at (0,0)
            if (RSInterface.interfaceCache[boxId] != null) RSInterface.interfaceCache[boxId].parentID = SCROLL_ID;
            if (RSInterface.interfaceCache[base + 0] != null) RSInterface.interfaceCache[base + 0].parentID = SCROLL_ID;
            if (RSInterface.interfaceCache[base + 1] != null) RSInterface.interfaceCache[base + 1].parentID = SCROLL_ID;
            if (RSInterface.interfaceCache[base + 2] != null) RSInterface.interfaceCache[base + 2].parentID = SCROLL_ID;
            if (RSInterface.interfaceCache[base + 3] != null) RSInterface.interfaceCache[base + 3].parentID = SCROLL_ID;
            if (RSInterface.interfaceCache[base + 4] != null) RSInterface.interfaceCache[base + 4].parentID = SCROLL_ID;
            if (RSInterface.interfaceCache[barBgId] != null) RSInterface.interfaceCache[barBgId].parentID = SCROLL_ID;
            if (RSInterface.interfaceCache[barFillId] != null) RSInterface.interfaceCache[barFillId].parentID = SCROLL_ID;
            if (RSInterface.interfaceCache[barTextId] != null) RSInterface.interfaceCache[barTextId].parentID = SCROLL_ID;

            int y = i * ROW_H;
            scroll.child(i * 9 + 0, boxId, 2, y + 3);
            scroll.child(i * 9 + 1, base + 0, 6, y + 6);
            scroll.child(i * 9 + 2, base + 1, 50, y + 4);
            scroll.child(i * 9 + 3, base + 2, 50, y + 20);
            scroll.child(i * 9 + 4, base + 3, scroll.width - 30, y + 10);
            scroll.child(i * 9 + 5, base + 4, 0, y);

            // progress bar sits at bottom-right of each row
            final int barX = scroll.width - (ROW_BAR_W + 16);
            scroll.child(i * 9 + 6, barBgId, barX, y + 30);
            scroll.child(i * 9 + 7, barFillId, barX + 1, y + 31);
            scroll.child(i * 9 + 8, barTextId, barX + (ROW_BAR_W / 2), y + 31);
        }

        // ---- Bottom completion progress ----
        addText(TEXT_PROGRESS, "", tda, 0, 0xFFFAE5, false, true);
        addSkinProgressBar2021(PROGRESS_BAR_ID, 400, 11);

        // ---- Children ----
        rsi.totalChildren(
                1   // bg
                        + 2 // selected tab + icon
                        + 2 // home btn+icon
                        + 2 // skill btn+icon
                        + 2 // combat btn+icon
                        + 2 // mastery btn+icon
                        + 1 // title
                        + 1 // close
                        + 1 // dropdown
                        + 1 // show completed
                        + 1 // scroll
                        + 1 // progress text
                        + 4 // progress bar parts
        );

        int c = 0;

        // BG
        rsi.child(c++, BG_ID, BG_X, BG_Y);

        // Selected tab (Tasks) - position at index 1 like your original
        rsi.child(c++, NAV_SELECTED_BG, NAV_X, NAV_Y + (1 * TAB_GAP));
        rsi.child(c++, NAV_SELECTED_ICON, NAV_X + ICON_INSET, NAV_Y + (1 * TAB_GAP) + ICON_INSET);

        // Home tab
        rsi.child(c++, NAV_HOME_BTN, NAV_X, NAV_Y);
        rsi.child(c++, NAV_HOME_ICON, NAV_X + ICON_INSET, NAV_Y + ICON_INSET);

        // Skilling tab (moved up after removing Achievements)
        rsi.child(c++, NAV_SKILL_BTN, NAV_X, NAV_Y + (2 * TAB_GAP));
        rsi.child(c++, NAV_SKILL_ICON, NAV_X + ICON_INSET, NAV_Y + (2 * TAB_GAP) + ICON_INSET);

        // Combat tab
        rsi.child(c++, NAV_COMBAT_BTN, NAV_X, NAV_Y + (3 * TAB_GAP));
        rsi.child(c++, NAV_COMBAT_ICON, NAV_X + ICON_INSET, NAV_Y + (3 * TAB_GAP) + ICON_INSET);

        // Mastery tab
        rsi.child(c++, NAV_MASTERY_BTN, NAV_X, NAV_Y + (4 * TAB_GAP));
        rsi.child(c++, NAV_MASTERY_ICON, NAV_X + ICON_INSET, NAV_Y + (4 * TAB_GAP) + ICON_INSET);

        // Title
        rsi.child(c++, TEXT_TITLE, MAIN_X + 175, MAIN_Y - 16-5-4);

        // Close (top-right of the frame)
        rsi.child(c++, CLOSE_BTN_ID, BG_X + 470, BG_Y + 14);

        // Scroll
        rsi.child(c++, SCROLL_ID, MAIN_X, MAIN_Y + 30);

        // Bottom progress
        rsi.child(c++, TEXT_PROGRESS, MAIN_X + 6, MAIN_Y + 270-20-4);

        final int BAR_X = MAIN_X + 2;
        final int BAR_Y = MAIN_Y + 286-30+1;
        rsi.child(c++, PROGRESS_BAR_ID, BAR_X, BAR_Y);
        rsi.child(c++, PROGRESS_BAR_ID + 1, BAR_X + 1, BAR_Y + 1);
        rsi.child(c++, PROGRESS_BAR_ID + 2, BAR_X + 2, BAR_Y + 2);
        rsi.child(c++, PROGRESS_BAR_ID + 3, BAR_X + 2, BAR_Y + 2);

        // Top row (draw last so the dropdown popup renders above everything)
        rsi.child(c++, DROPDOWN_ID, MAIN_X, MAIN_Y + 2);
        rsi.child(c++, TEXT_SHOW_COMPLETED, MAIN_X + 180, MAIN_Y + 2);

        // Populate initial list

        refreshList(0);
    }
}
