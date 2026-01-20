package com.client.graphics.interfaces.impl;

import com.client.TextDrawingArea;
import com.client.achievements.VyknaAchievementDefinitions;
import com.client.achievements.VyknaAchievementProgressStore;
import com.client.graphics.interfaces.RSInterface;
import com.client.graphics.interfaces.MenuItem;

import java.util.Map;

/**
 * Achievement list page (combat/skilling).
 */
public final class AchievementListPage extends RSInterface {

    private static final String SPRITE_ROOT = "interfaces/vykna_achievements/";
    private static final String RECENT_ATLAS = SPRITE_ROOT + "AchievementRecentAtlas";

    public static final int COMBAT_INTERFACE_ID = 35150;
    public static final int SKILLING_INTERFACE_ID = 35250;

    private static final int COMBAT_DROPDOWN_ID = 35160;
    private static final int SKILLING_DROPDOWN_ID = 35260;

    private static final int COMBAT_SCROLL_ID = 35170;
    private static final int SKILLING_SCROLL_ID = 35270;

    private static final int COMBAT_ROW_START = 35300;
    private static final int SKILLING_ROW_START = 35400;
    private static final int ROW_STRIDE = 4;
    private static final int MAX_ROWS = 24;

    private static final int BTN_HOME_COMBAT = 35120;
    private static final int BTN_HOME_SKILLING = 35220;

    private static final String[] COMBAT_GROUPS = { "Starter", "Slayer", "Bandos" };
    private static final String[] SKILLING_GROUPS = { "Woodcutting", "Fletching", "Firemaking" };

    private AchievementListPage() {
    }

    public static void build(TextDrawingArea[] tda) {
        buildCombat(tda);
        buildSkilling(tda);
    }

    private static void buildCombat(TextDrawingArea[] tda) {
        buildPage(COMBAT_INTERFACE_ID, "Combat Achievements", COMBAT_DROPDOWN_ID, COMBAT_SCROLL_ID,
                COMBAT_ROW_START, COMBAT_GROUPS, new CombatGroupMenuItem(), tda);
    }

    private static void buildSkilling(TextDrawingArea[] tda) {
        buildPage(SKILLING_INTERFACE_ID, "Skilling Achievements", SKILLING_DROPDOWN_ID, SKILLING_SCROLL_ID,
                SKILLING_ROW_START, SKILLING_GROUPS, new SkillingGroupMenuItem(), tda);
    }

    private static void buildPage(int interfaceId, String title, int dropdownId, int scrollId, int rowStartId,
                                  String[] groupOptions, MenuItem menuItem, TextDrawingArea[] tda) {
        RSInterface rsi = addTabInterface(interfaceId);

        final int BG_X = 8, BG_Y = 8;
        final int MAIN_X = BG_X + 70;
        final int MAIN_Y = BG_Y + 40;

        addSprite(interfaceId + 1, 0, SPRITE_ROOT + "Background");
        addText(interfaceId + 2, title, tda, 1, 0xE3AE19, false, true);

        dropdownMenu(dropdownId, 160, 0, groupOptions, menuItem, tda, 0);

        RSInterface scroll = addInterfaceContainer(scrollId, 300, 222, 500);
        scroll.totalChildren(MAX_ROWS * ROW_STRIDE);

        int childId = rowStartId;
        int childIndex = 0;
        for (int i = 0; i < MAX_ROWS; i++) {
            addGridSpriteValueIndex(childId, RECENT_ATLAS, 2, 2, 36, 0, "Achievement");
            addText(childId + 1, "", tda, 0, 0xFFFAE5, false, true);
            addText(childId + 2, "", tda, 0, 0xC9C4A0, false, true);
            addText(childId + 3, "", tda, 0, 0xFFFAE5, true, true);

            scroll.children[childIndex++] = childId;
            scroll.childX[childIndex - 1] = MAIN_X + 4;
            scroll.childY[childIndex - 1] = MAIN_Y + (i * 38);

            scroll.children[childIndex++] = childId + 1;
            scroll.childX[childIndex - 1] = MAIN_X + 46;
            scroll.childY[childIndex - 1] = MAIN_Y + (i * 38);

            scroll.children[childIndex++] = childId + 2;
            scroll.childX[childIndex - 1] = MAIN_X + 46;
            scroll.childY[childIndex - 1] = MAIN_Y + (i * 38) + 14;

            scroll.children[childIndex++] = childId + 3;
            scroll.childX[childIndex - 1] = MAIN_X + 250;
            scroll.childY[childIndex - 1] = MAIN_Y + (i * 38);

            childId += ROW_STRIDE;
        }
        scroll.scrollMax = MAX_ROWS * 38;
        int homeButtonId = interfaceId == COMBAT_INTERFACE_ID ? BTN_HOME_COMBAT : BTN_HOME_SKILLING;
        addHoverButtonNew(homeButtonId, SPRITE_ROOT + "LeftTabStandard", SPRITE_ROOT + "LeftTabHover",
                36, 36, "Overview", 0, 1);
        addSprite(homeButtonId + 1, 0, SPRITE_ROOT + "OverviewIcon");

        rsi.totalChildren(6);
        rsi.children[0] = interfaceId + 1;
        rsi.childX[0] = BG_X;
        rsi.childY[0] = BG_Y;
        rsi.children[1] = interfaceId + 2;
        rsi.childX[1] = MAIN_X;
        rsi.childY[1] = MAIN_Y - 22;
        rsi.children[2] = dropdownId;
        rsi.childX[2] = MAIN_X;
        rsi.childY[2] = MAIN_Y - 2;
        rsi.children[3] = scrollId;
        rsi.childX[3] = 0;
        rsi.childY[3] = 0;
        rsi.children[4] = homeButtonId;
        rsi.childX[4] = MAIN_X - 50;
        rsi.childY[4] = MAIN_Y - 32;
        rsi.children[5] = homeButtonId + 1;
        rsi.childX[5] = MAIN_X - 43;
        rsi.childY[5] = MAIN_Y - 25;
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

        r.valueIndex = valueIndex;
        r.configId = -1;
        r.tooltip = tooltip;
    }

    public static void updateEntries(String type, String group, Map<Integer, VyknaAchievementProgressStore.Progress> progressMap) {
        if ("combat".equalsIgnoreCase(type)) {
            updateEntriesFor(COMBAT_ROW_START, COMBAT_GROUPS, group, progressMap, "combat");
        } else if ("skilling".equalsIgnoreCase(type)) {
            updateEntriesFor(SKILLING_ROW_START, SKILLING_GROUPS, group, progressMap, "skilling");
        }
    }

    private static void updateEntriesFor(int rowStartId, String[] groupOptions, String group,
                                         Map<Integer, VyknaAchievementProgressStore.Progress> progressMap, String type) {
        if (!matchesGroup(groupOptions, group)) {
            return;
        }
        int row = 0;
        for (VyknaAchievementDefinitions.Definition def : VyknaAchievementDefinitions.byTypeGroup(type, group)) {
            if (row >= MAX_ROWS) {
                break;
            }
            int base = rowStartId + (row * ROW_STRIDE);
            VyknaAchievementProgressStore.Progress progress = progressMap.get(def.getId());
            int current = progress == null ? 0 : progress.getCurrent();
            int target = progress == null ? 1 : progress.getTarget();

            RSInterface icon = RSInterface.interfaceCache[base];
            RSInterface title = RSInterface.interfaceCache[base + 1];
            RSInterface desc = RSInterface.interfaceCache[base + 2];
            RSInterface prog = RSInterface.interfaceCache[base + 3];

            if (icon != null) {
                icon.valueIndex = def.getId();
            }
            if (title != null) {
                title.message = def.getName();
            }
            if (desc != null) {
                desc.message = def.getDescription();
            }
            if (prog != null) {
                prog.message = current + "/" + Math.max(1, target);
            }
            row++;
        }

        for (; row < MAX_ROWS; row++) {
            int base = rowStartId + (row * ROW_STRIDE);
            RSInterface icon = RSInterface.interfaceCache[base];
            RSInterface title = RSInterface.interfaceCache[base + 1];
            RSInterface desc = RSInterface.interfaceCache[base + 2];
            RSInterface prog = RSInterface.interfaceCache[base + 3];
            if (icon != null) {
                icon.valueIndex = 0;
            }
            if (title != null) {
                title.message = "";
            }
            if (desc != null) {
                desc.message = "";
            }
            if (prog != null) {
                prog.message = "";
            }
        }
    }

    private static boolean matchesGroup(String[] groups, String group) {
        for (String option : groups) {
            if (option.equalsIgnoreCase(group)) {
                return true;
            }
        }
        return false;
    }

    public static int combatHomeButtonId() {
        return BTN_HOME_COMBAT;
    }

    public static int skillingHomeButtonId() {
        return BTN_HOME_SKILLING;
    }

    private static final class CombatGroupMenuItem implements MenuItem {
        @Override
        public void select(int optionSelected, RSInterface rsInterface) {
            if (optionSelected < 0 || optionSelected >= COMBAT_GROUPS.length) {
                return;
            }
            VyknaAchievementProgressStore.showList("combat", COMBAT_GROUPS[optionSelected]);
        }
    }

    private static final class SkillingGroupMenuItem implements MenuItem {
        @Override
        public void select(int optionSelected, RSInterface rsInterface) {
            if (optionSelected < 0 || optionSelected >= SKILLING_GROUPS.length) {
                return;
            }
            VyknaAchievementProgressStore.showList("skilling", SKILLING_GROUPS[optionSelected]);
        }
    }
}
