package com.client.graphics.interfaces.impl;

import com.client.TextDrawingArea;
import com.client.graphics.interfaces.MenuItem;
import com.client.graphics.interfaces.RSInterface;
import com.client.graphics.interfaces.impl.DropdownMenu;
import com.client.vykna_progression.ProgressionEntryDefinition;
import com.client.vykna_progression.ProgressionListType;
import com.client.vykna_progression.VyknaProgressionDefinitions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
 *  - INTERFACE_ID is assigned to a free range to avoid interface id collisions.
 */
public final class AchievementListPage extends RSInterface {

    private static final String SPRITE_ROOT = "interfaces/vykna_progression/";
    private static final String RECENT_ATLAS = SPRITE_ROOT + "AchievementRecentAtlas";

    // Assigned to a free range to avoid interface id collisions.
    public static final int INTERFACE_ID = 55281;

    // ----- SAFE ID RANGES (avoid helper-method collisions) -----

    // Background
    private static final int BG_ID = INTERFACE_ID + 1;

    // Left nav sprites/buttons (put them in a high block)
    private static final int NAV_SELECTED_BG = INTERFACE_ID + 1100;
    private static final int NAV_SELECTED_ICON = INTERFACE_ID + 1101;

    private static final int NAV_HOME_BTN = INTERFACE_ID + 1200;
    private static final int NAV_HOME_ICON = INTERFACE_ID + 1210;

    private static final int NAV_TASKS_BTN = 220181;

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

    private static final String ALL_FILTER = "All";
    private static final int DROPDOWN_WIDTH = 166;
    private static final MenuItem DROPDOWN_ACTION =
            (optionSelected, rsInterface) -> AchievementListPage.refreshList(getDropdownOption(optionSelected, rsInterface));

    private static final class TaskRow {
        private final String title;
        private final String description;
        private final int points;
        private final boolean completed;
        private final int spriteIndex;

        // Optional per-task progress (e.g. 21/50). If target <= 0, bar is hidden.
        private final int progressCurrent;
        private final int progressTarget;

        private TaskRow(String title, String description, int points, boolean completed, int spriteIndex) {
            this.title = title;
            this.description = description;
            this.points = points;
            this.completed = completed;
            this.spriteIndex = spriteIndex;
            this.progressCurrent = 0;
            this.progressTarget = 0;
        }

        private TaskRow(String title, String description, int points, boolean completed, int spriteIndex,
                        int progressCurrent, int progressTarget) {
            this.title = title;
            this.description = description;
            this.points = points;
            this.completed = completed;
            this.spriteIndex = spriteIndex;
            this.progressCurrent = progressCurrent;
            this.progressTarget = progressTarget;
        }
    }

    private static ProgressionListType currentListType = ProgressionListType.TASKS;
    private static String currentFilter = ALL_FILTER;

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
    private static final int MAX_ROWS = 200;

    private static int maxTaskCount() {
        return MAX_ROWS;
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

    public static void refreshList(String selectedOption) {
        String resolvedFilter = normalizeFilter(selectedOption);
        currentFilter = resolvedFilter;

        List<ProgressionEntryDefinition> entries = VyknaProgressionDefinitions.getEntries(currentListType);
        List<TaskRow> rows = new ArrayList<>();
        for (ProgressionEntryDefinition entry : entries) {
            if (!ALL_FILTER.equalsIgnoreCase(currentFilter)
                    && !normalizeFilter(entry.getSubcategory()).equalsIgnoreCase(currentFilter)) {
                continue;
            }
            String status = entry.isCompleted() ? "Completed" : "Incomplete";
            rows.add(new TaskRow(entry.getName(), entry.getDescription() + " (" + status + ")",
                    entry.getPoints(), entry.isCompleted(), entry.getSpriteIndex(),
                    entry.getProgressCurrent(), entry.getRequirementTarget()));
        }

        if (rows.isEmpty() && !ALL_FILTER.equalsIgnoreCase(currentFilter)) {
            currentFilter = ALL_FILTER;
            rows = new ArrayList<>();
            for (ProgressionEntryDefinition entry : entries) {
                String status = entry.isCompleted() ? "Completed" : "Incomplete";
                rows.add(new TaskRow(entry.getName(), entry.getDescription() + " (" + status + ")",
                        entry.getPoints(), entry.isCompleted(), entry.getSpriteIndex(),
                        entry.getProgressCurrent(), entry.getRequirementTarget()));
            }
        }

        int completed = 0;
        for (TaskRow task : rows) {
            if (task.completed) completed++;
        }
        int total = rows.size();

        if (RSInterface.interfaceCache[TEXT_PROGRESS] != null) {
            RSInterface.interfaceCache[TEXT_PROGRESS].message =
                    "Completed: " + completed + "/" + total;
        }

        // Hide the bottom progress bar for now (no progress bars in this stage).
        for (int i = 0; i < 4; i++) {
            RSInterface bar = RSInterface.interfaceCache[PROGRESS_BAR_ID + i];
            if (bar != null) {
                bar.interfaceHidden = true;
            }
        }

        RSInterface scroll = RSInterface.interfaceCache[SCROLL_ID];
        if (scroll != null) {
            scroll.scrollMax = Math.max(rows.size() * ROW_H, scroll.height);
        }

        final int rowCount = maxTaskCount();
        for (int i = 0; i < rowCount; i++) {
            int base = ROW_START_ID + (i * ROW_STRIDE);
            boolean active = i < rows.size();

            if (RSInterface.interfaceCache[base + 1] != null) {
                RSInterface.interfaceCache[base + 1].message = active ? rows.get(i).title : "";
            }
            if (RSInterface.interfaceCache[base + 2] != null) {
                RSInterface.interfaceCache[base + 2].message = active ? rows.get(i).description : "";
            }
            if (RSInterface.interfaceCache[base + 3] != null) {
                RSInterface.interfaceCache[base + 3].message = active ? "<icon=0>" + rows.get(i).points : "";
            }
            if (RSInterface.interfaceCache[base + 0] != null) {
                RSInterface.interfaceCache[base + 0].valueIndex = active ? rows.get(i).spriteIndex : 1;
            }

            final int barBgId = base + 6;
            final int barFillId = base + 7;
            final int barTextId = base + 8;
            boolean showProgress = active && rows.get(i).progressTarget > 0;
            if (RSInterface.interfaceCache[barBgId] != null) {
                RSInterface.interfaceCache[barBgId].interfaceHidden = !showProgress;
            }
            if (RSInterface.interfaceCache[barFillId] != null) {
                RSInterface.interfaceCache[barFillId].interfaceHidden = !showProgress;
            }
            if (RSInterface.interfaceCache[barTextId] != null) {
                RSInterface.interfaceCache[barTextId].interfaceHidden = !showProgress;
            }
            if (showProgress && RSInterface.interfaceCache[barFillId] != null
                    && RSInterface.interfaceCache[barTextId] != null) {
                int current = Math.min(rows.get(i).progressCurrent, rows.get(i).progressTarget);
                int target = Math.max(rows.get(i).progressTarget, 1);
                int filledWidth = Math.max(1, (int) Math.floor((current / (double) target) * ROW_BAR_W));
                RSInterface.interfaceCache[barFillId].width = filledWidth;
                RSInterface.interfaceCache[barTextId].message = current + "/" + target;
            }

            if (scroll != null && scroll.childY != null) {
                int childBase = i * 9;
                int y = active ? (i * ROW_H) : ROWS_HIDDEN_Y;

                if (childBase + 0 < scroll.childY.length) scroll.childY[childBase + 0] = y + 3;
                if (childBase + 1 < scroll.childY.length) scroll.childY[childBase + 1] = y + 6;
                if (childBase + 2 < scroll.childY.length) scroll.childY[childBase + 2] = y + 4;
                if (childBase + 3 < scroll.childY.length) scroll.childY[childBase + 3] = y + 20;
                if (childBase + 4 < scroll.childY.length) scroll.childY[childBase + 4] = y + 10;
                if (childBase + 5 < scroll.childY.length) scroll.childY[childBase + 5] = y;

                if (childBase + 6 < scroll.childY.length) scroll.childY[childBase + 6] = y + 30;
                if (childBase + 7 < scroll.childY.length) scroll.childY[childBase + 7] = y + 31;
                if (childBase + 8 < scroll.childY.length) scroll.childY[childBase + 8] = y + 31;
            }
        }
    }

    public static void applyServerPayload(int listTypeId) {
        currentListType = ProgressionListType.fromId(listTypeId);
        updateDropdownOptions(VyknaProgressionDefinitions.getSubcategories(currentListType));
        if (RSInterface.interfaceCache[TEXT_TITLE] != null) {
            RSInterface.interfaceCache[TEXT_TITLE].message = currentListType.getDisplayName() + " Progression";
        }
        refreshList(ALL_FILTER);
    }

    private static void updateDropdownOptions(List<String> subcategories) {
        List<String> options = new ArrayList<>();
        options.add(ALL_FILTER);
        if (subcategories != null && !subcategories.isEmpty()) {
            List<String> sorted = new ArrayList<>(subcategories);
            sorted.sort(String.CASE_INSENSITIVE_ORDER);
            options.addAll(sorted);
        }
        RSInterface dropdown = RSInterface.interfaceCache[DROPDOWN_ID];
        if (dropdown != null) {
            dropdown.dropdown = new DropdownMenu(
                    DROPDOWN_WIDTH, false, 0, options.toArray(new String[0]), DROPDOWN_ACTION);
            dropdown.dropdown.setSelected(ALL_FILTER);
        }
        currentFilter = ALL_FILTER;
    }

    private static String getDropdownOption(int optionSelected, RSInterface rsInterface) {
        if (rsInterface != null && rsInterface.dropdown != null) {
            String selected = normalizeFilter(rsInterface.dropdown.getSelected());
            if (!"Select an option".equalsIgnoreCase(selected)) {
                return selected;
            }
        }
        String[] options = null;
        if (rsInterface != null && rsInterface.dropdown != null) {
            options = rsInterface.dropdown.getOptions();
        }
        RSInterface cached = RSInterface.interfaceCache[DROPDOWN_ID];
        if ((options == null || options.length == 0) && cached != null && cached.dropdown != null) {
            options = cached.dropdown.getOptions();
        }
        if (options != null && optionSelected >= 0 && optionSelected < options.length) {
            return options[optionSelected];
        }
        return ALL_FILTER;
    }

    private static String normalizeFilter(String value) {
        if (value == null) {
            return ALL_FILTER;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? ALL_FILTER : trimmed;
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
        addHoverButtonNew(NAV_TASKS_BTN, SPRITE_ROOT + "LeftTabStandard", SPRITE_ROOT + "LeftTabHover",
                36, 36, "Tasks", 0, 1);
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
                DROPDOWN_ID, DROPDOWN_WIDTH, 0, new String[] { ALL_FILTER },
                DROPDOWN_ACTION,
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
                        + 1 // tasks btn (selected)
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

        // Tasks tab button (selected state is overlaid)
        rsi.child(c++, NAV_TASKS_BTN, NAV_X, NAV_Y + (1 * TAB_GAP));

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

        refreshList(ALL_FILTER);
    }
}
