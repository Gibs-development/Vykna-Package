package com.client.graphics.interfaces.impl;

import com.client.TextDrawingArea;
import com.client.definitions.NpcDefinition;
import com.client.graphics.interfaces.RSInterface;
import com.client.vykna_teleports.TeleportListPayload;
import com.client.vykna_teleports.TeleportRowIconPayload;

import java.util.List;

/**
 * Teleport interface (client-side layout only; dummy data for now).
 *
 * Additions:
 * - Top tab icons (Monsters/Bosses/Activities/Quests) drawn to the left of the tab text.
 * - Teleport list head icon on the far-right of each row using the SAME grid-sprite atlas system as Achievements:
 *   RSI type=17 + valueIndex.
 *
 * Sprites expected in: interfaces/vykna_teleports/
 * - TabIconMonsters 0.png
 * - TabIconBosses 0.png
 * - TabIconActivities 0.png
 * - TabIconQuests 0.png
 * - RowHeads 0.png  (atlas; 8x8 grid of 16x16 tiles => 128x128)
 *
 * NOTE: Keep interface id < 65535 due to opcode 97 (2-byte interface id).
 */
public final class TeleportHomePage extends RSInterface {

    private static final String SPRITE_ROOT = "interfaces/vykna_teleports/";

    public static final int INTERFACE_ID = 31000;

    // ---- SAFE ID BLOCKS ----
    private static final int BG_ID = INTERFACE_ID + 1;

    // Header / tabs
    private static final int TITLE_ID = INTERFACE_ID + 50;
    private static final int CLOSE_UI_ID = INTERFACE_ID + 51;

    private static final int TAB_MONSTERS_BTN = INTERFACE_ID + 200;
    private static final int TAB_BOSSES_BTN   = INTERFACE_ID + 220;
    private static final int TAB_ACTS_BTN     = INTERFACE_ID + 240;
    private static final int TAB_QUESTS_BTN   = INTERFACE_ID + 260;

    private static final int TAB_MONSTERS_TXT = INTERFACE_ID + 201;
    private static final int TAB_BOSSES_TXT   = INTERFACE_ID + 221;
    private static final int TAB_ACTS_TXT     = INTERFACE_ID + 241;
    private static final int TAB_QUESTS_TXT   = INTERFACE_ID + 261;

    // Tab icons
    private static final int TAB_MONSTERS_ICON = INTERFACE_ID + 202;
    private static final int TAB_BOSSES_ICON   = INTERFACE_ID + 222;
    private static final int TAB_ACTS_ICON     = INTERFACE_ID + 242;
    private static final int TAB_QUESTS_ICON   = INTERFACE_ID + 262;

    // Search
    private static final int SEARCH_BG_ID    = INTERFACE_ID + 400;
    private static final int SEARCH_TEXT_ID  = INTERFACE_ID + 401;
    private static final int SEARCH_CLEAR_ID = INTERFACE_ID + 402;

    // Left list
    private static final int SCROLL_ID = INTERFACE_ID + 600;
    private static final int ROW_START_ID = INTERFACE_ID + 700;
    private static final int ROW_STRIDE = 10;
    private static final int MAX_ROWS = 100;

    // Center preview
    private static final int PREVIEW_BOX_ID  = INTERFACE_ID + 1200;
    private static final int PREVIEW_NAME_ID = INTERFACE_ID + 1201;
    private static final int PREVIEW_NPC_ID  = INTERFACE_ID + 1202;

    private static final int STATS_BOX_ID    = INTERFACE_ID + 1215;
    private static final int STATS_LINE1_ID  = INTERFACE_ID + 1216;
    private static final int STATS_LINE2_ID  = INTERFACE_ID + 1217;

    private static final int TELEPORT_BTN_ID = INTERFACE_ID + 1220;

    // Loot panel (bottom-right)
    private static final int LOOT_BOX_ID   = INTERFACE_ID + 1300;
    private static final int LOOT_TITLE_ID = INTERFACE_ID + 1301;
    private static final int LOOT_GRID_ID  = INTERFACE_ID + 1310;

    // Right column info (top-right)
    private static final int DESC_TITLE_ID = INTERFACE_ID + 1400;
    private static final int DESC_LINE1_ID = INTERFACE_ID + 1401;
    private static final int DESC_LINE2_ID = INTERFACE_ID + 1402;

    private static final int REQ_TITLE_ID  = INTERFACE_ID + 1420;
    private static final int REQ_LINE1_ID  = INTERFACE_ID + 1421;
    private static final int REQ_LINE2_ID  = INTERFACE_ID + 1422;

    private static final int QUEST_TITLE_ID = INTERFACE_ID + 1440;
    private static final int QUEST_LINE1_ID = INTERFACE_ID + 1441;

    // --- Layout constants (matches your paste) ---
    private static final int BG_X = 0, BG_Y = 10;

    @SuppressWarnings("unused")
    private static final int BG_W = 497;
    @SuppressWarnings("unused")
    private static final int BG_H = 322;

    private static final int SHIFT_X = 10;

    private static final int TITLE_X = BG_X + 18;
    private static final int TITLE_Y = BG_Y + 10;

    private static final int TABS_Y = BG_Y + 28 + 20-8;
    private static final int TAB_START_X = BG_X + 10;
    private static final int TAB_GAP = 85;
    private static final int TAB_W = 80;
    private static final int TAB_H = 22;

    private static final int SEARCH_W = 100;
    private static final int SEARCH_X = BG_X + 338 + 15;
    private static final int SEARCH_Y = BG_Y + 26 + 20-5;
    private static final int SEARCH_CLEAR_X = SEARCH_X + SEARCH_W + 4;

    private static final int CLOSE_X = BG_X + 470;
    private static final int CLOSE_Y = BG_Y + 10;

    private static final int LIST_X = BG_X + 10;
    private static final int LIST_Y = BG_Y + 58 + 20;
    private static final int LIST_W = 150;
    private static final int LIST_H = 232;

    private static final int PREVIEW_X = BG_X + 190 - 10;
    private static final int PREVIEW_Y = BG_Y + 58 + 20;
    private static final int PREVIEW_W = 160;
    private static final int PREVIEW_H = 145;

    private static final int STATS_W = 146;
    private static final int STATS_H = 34;

    private static final int INFO_X = BG_X + 350;
    private static final int INFO_Y = BG_Y + 58 + 20;

    private static final int LOOT_X = BG_X + 350;
    private static final int LOOT_Y = BG_Y + 196 + 20;
    private static final int LOOT_W = 138;
    private static final int LOOT_H = 102;

    // Row head atlas constants
    private static final int HEAD_COLS = 4;
    private static final int HEAD_ROWS = 4;
    private static final int HEAD_CELL = 16;

    private TeleportHomePage() {}

    public static int getSearchClearId() {
        return SEARCH_CLEAR_ID;
    }

    public static void build(TextDrawingArea[] tda) {
        RSInterface rsi = addTabInterface(INTERFACE_ID);

        addSprite(BG_ID, 0, SPRITE_ROOT + "Background");
        addText(TITLE_ID, "Vykna Teleports", tda, 2, 0xE3AE19, false, true);

        // Top-right close
        addHoverButtonNew(CLOSE_UI_ID, SPRITE_ROOT + "Close", SPRITE_ROOT + "CloseHover",
                16, 16, "Close", 0, 1);

        // Tabs
        addHoverButtonNew(TAB_MONSTERS_BTN, SPRITE_ROOT + "TopTab", SPRITE_ROOT + "TopTabHover",
                TAB_W, TAB_H, "Monsters", 0, 1);
        addHoverButtonNew(TAB_BOSSES_BTN, SPRITE_ROOT + "TopTab", SPRITE_ROOT + "TopTabHover",
                TAB_W, TAB_H, "Bosses", 0, 1);
        addHoverButtonNew(TAB_ACTS_BTN, SPRITE_ROOT + "TopTab", SPRITE_ROOT + "TopTabHover",
                TAB_W, TAB_H, "Activities", 0, 1);
        addHoverButtonNew(TAB_QUESTS_BTN, SPRITE_ROOT + "TopTab", SPRITE_ROOT + "TopTabHover",
                TAB_W, TAB_H, "Quests", 0, 1);

        // Tab icons
        addSprite(TAB_MONSTERS_ICON, 0, SPRITE_ROOT + "TabIconMonsters");
        addSprite(TAB_BOSSES_ICON,   0, SPRITE_ROOT + "TabIconBosses");
        addSprite(TAB_ACTS_ICON,     0, SPRITE_ROOT + "TabIconActivities");
        addSprite(TAB_QUESTS_ICON,   0, SPRITE_ROOT + "TabIconQuests");

        // Tab text (nudged right)
        addText(TAB_MONSTERS_TXT, "Monsters", tda, 1, 0xE3AE19, true, true);
        addText(TAB_BOSSES_TXT, "Bosses", tda, 1, 0xE3AE19, true, true);
        addText(TAB_ACTS_TXT, "Activities", tda, 1, 0xE3AE19, true, true);
        addText(TAB_QUESTS_TXT, "Quests", tda, 1, 0xE3AE19, true, true);

        // Search
        addBox(SEARCH_BG_ID, 0x2b2118, 0x1f1812, 120, SEARCH_W, 18);
        addHoverText(SEARCH_TEXT_ID, "Search...", "Search teleports", tda, 0, 0x9a8b7a, false, true, SEARCH_W, 16);
        addHoverButtonNew(SEARCH_CLEAR_ID, SPRITE_ROOT + "Close", SPRITE_ROOT + "CloseHover",
                16, 16, "Clear", 0, 1);

        // Scroll list
        RSInterface scroll = addTabInterface(SCROLL_ID);
        scroll.width = LIST_W;
        scroll.height = LIST_H;
        scroll.scrollMax = Math.max(MAX_ROWS * 22, scroll.height);

        // Each row = (hover bg) + (label) + (right head icon)
        scroll.totalChildren(MAX_ROWS * 3);

        final int rowW = LIST_W - 6;
        final int rowH = 20;

        for (int i = 0; i < MAX_ROWS; i++) {
            final int base = ROW_START_ID + (i * ROW_STRIDE);

            if (i % 2 == 0) {
                addHoverButtonNew(base + 0, SPRITE_ROOT + "RowLight", SPRITE_ROOT + "RowLightHover",
                        rowW, rowH, "Select teleport", 0, 1);
            } else {
                addHoverButtonNew(base + 0, SPRITE_ROOT + "RowDark", SPRITE_ROOT + "RowDarkHover",
                        rowW, rowH, "Select teleport", 0, 1);
            }

            addText(base + 1, "", tda, 0, 0xE3AE19, false, true);

            // Right-side head icon (grid atlas like achievements)
            // IMPORTANT: This is NOT a type=5 sprite. It uses type=17 and valueIndex.
            int valueIndex = 0; // dummy default (use setRowHeadIndex(...) for per-row icons)
            addGridSpriteValueIndex(base + 2, SPRITE_ROOT + "RowHeads", HEAD_COLS, HEAD_ROWS, HEAD_CELL, valueIndex, "");

            if (RSInterface.interfaceCache[base + 0] != null) RSInterface.interfaceCache[base + 0].parentID = SCROLL_ID;
            if (RSInterface.interfaceCache[base + 1] != null) RSInterface.interfaceCache[base + 1].parentID = SCROLL_ID;
            if (RSInterface.interfaceCache[base + 2] != null) RSInterface.interfaceCache[base + 2].parentID = SCROLL_ID;

            int y = i * 22;
            scroll.child(i * 3,     base + 0, 3, y);
            scroll.child(i * 3 + 1, base + 1, 10, y + 4);
            // place head icon at far right
            scroll.child(i * 3 + 2, base + 2, rowW - 18, y + 2);
        }

        // Dummy row labels
        setRowText(0, "Goblins");
        setRowText(1, "Hill Giants");
        setRowText(2, "Green Dragons");
        setRowText(3, "Giant Mole");
        setRowText(4, "Barrows");

        // Dummy head icons (indexes into RowHeads atlas)
        setRowHeadIndex(0, 0);
        setRowHeadIndex(1, 1);
        setRowHeadIndex(2, 2);
        setRowHeadIndex(3, 3);
        setRowHeadIndex(4, 4);

        // Preview panel
        addBox(PREVIEW_BOX_ID, 0x2b241d, 0x1f1812, 90, PREVIEW_W, PREVIEW_H);
        addText(PREVIEW_NAME_ID, "Goblins", tda, 1, 0xE3AE19, true, true);

        final int DUMMY_NPC_ID = findPreviewNpcId(100, 0);
        addNpcModel(PREVIEW_NPC_ID, DUMMY_NPC_ID, 900, 1, 1);

        RSInterface npcWidget = RSInterface.interfaceCache[PREVIEW_NPC_ID];
        if (npcWidget != null) {
            npcWidget.type = 6;
            npcWidget.anInt233 = 2;
            npcWidget.mediaID = DUMMY_NPC_ID;

            npcWidget.width = 100;
            npcWidget.height = 90;
            npcWidget.autoNpcZoom = true;
            npcWidget.modelZoom = RSInterface.autoZoomForNpc(
                    DUMMY_NPC_ID,
                    npcWidget.width,
                    npcWidget.height
            );

            npcWidget.modelRotation1 = 180;
            npcWidget.modelRotation2 = 40;

            npcWidget.anInt257 = -1;
            npcWidget.anInt258 = -1;
            npcWidget.anInt246 = 0;
            npcWidget.useNpcFullModel = true;
            npcWidget.useNpcStandAnim = true;
        }

        // Stats
        addBox(STATS_BOX_ID, 0x231d16, 0x1f1812, 110, STATS_W, STATS_H);
        addText(STATS_LINE1_ID, "Combat: 2   |   HP: 50", tda, 0, 0xFFFAE5, true, true);
        addText(STATS_LINE2_ID, "Aggressive: No", tda, 0, 0xFFFAE5, true, true);

        // Teleport button
        addHoverButtonNew(TELEPORT_BTN_ID, SPRITE_ROOT + "TeleportBtn", SPRITE_ROOT + "TeleportBtnHover",
                100, 22, "Teleport", 0, 1);

        // Right info
        addText(DESC_TITLE_ID, "Description", tda, 0, 0xE3AE19, false, true);
        addText(DESC_LINE1_ID, "A small green nuisance found", tda, 0, 0xFFFAE5, false, true);
        addText(DESC_LINE2_ID, "across the world.", tda, 0, 0xFFFAE5, false, true);

        addText(REQ_TITLE_ID, "Requirements", tda, 0, 0xE3AE19, false, true);
        addText(REQ_LINE1_ID, "Combat level: 2", tda, 0, 0xFFFAE5, false, true);
        addText(REQ_LINE2_ID, "HP: 50", tda, 0, 0xFFFAE5, false, true);

        addText(QUEST_TITLE_ID, "Quest", tda, 0, 0xE3AE19, false, true);
        addText(QUEST_LINE1_ID, "None", tda, 0, 0xFFFAE5, false, true);

        // Loot
        addBox(LOOT_BOX_ID, 0x2b241d, 0x1f1812, 90, LOOT_W, LOOT_H);
        addText(LOOT_TITLE_ID, "Drops", tda, 1, 0xE3AE19, true, true);
        addBox(LOOT_GRID_ID, 0x000000, 0x000000, 0, LOOT_W - 18, LOOT_H - 28);

        // ---- Children ----
        // Previous count 37; +4 tab icons => 41
        rsi.totalChildren(41);
        int c = 0;

        rsi.child(c++, BG_ID, BG_X + SHIFT_X, BG_Y);
        rsi.child(c++, TITLE_ID, TITLE_X + SHIFT_X, TITLE_Y);
        rsi.child(c++, CLOSE_UI_ID, CLOSE_X + SHIFT_X, CLOSE_Y);

        int tabY = TABS_Y;
        int tab0 = TAB_START_X + SHIFT_X;

        rsi.child(c++, TAB_MONSTERS_BTN, tab0 + (TAB_GAP * 0), tabY);
        rsi.child(c++, TAB_BOSSES_BTN,   tab0 + (TAB_GAP * 1), tabY);
        rsi.child(c++, TAB_ACTS_BTN,     tab0 + (TAB_GAP * 2), tabY);
        rsi.child(c++, TAB_QUESTS_BTN,   tab0 + (TAB_GAP * 3), tabY);

        // Icons inside each tab
        rsi.child(c++, TAB_MONSTERS_ICON, tab0 + (TAB_GAP * 0) + 6, tabY + 2);
        rsi.child(c++, TAB_BOSSES_ICON,   tab0 + (TAB_GAP * 1) + 6, tabY + 2);
        rsi.child(c++, TAB_ACTS_ICON,     tab0 + (TAB_GAP * 2) + 6, tabY + 2);
        rsi.child(c++, TAB_QUESTS_ICON,   tab0 + (TAB_GAP * 3) + 6, tabY + 2);

        // Tab text slightly right
        rsi.child(c++, TAB_MONSTERS_TXT, tab0 + (TAB_GAP * 0) + (TAB_W / 2) + 8, tabY + 4);
        rsi.child(c++, TAB_BOSSES_TXT,   tab0 + (TAB_GAP * 1) + (TAB_W / 2) + 8, tabY + 4);
        rsi.child(c++, TAB_ACTS_TXT,     tab0 + (TAB_GAP * 2) + (TAB_W / 2) + 8, tabY + 4);
        rsi.child(c++, TAB_QUESTS_TXT,   tab0 + (TAB_GAP * 3) + (TAB_W / 2) + 8, tabY + 4);

        rsi.child(c++, SEARCH_BG_ID, SEARCH_X + SHIFT_X, SEARCH_Y);
        rsi.child(c++, SEARCH_TEXT_ID, SEARCH_X + 5 + SHIFT_X, SEARCH_Y + 3);
        rsi.child(c++, SEARCH_CLEAR_ID, SEARCH_CLEAR_X + SHIFT_X, SEARCH_Y + 1);

        rsi.child(c++, SCROLL_ID, LIST_X + SHIFT_X, LIST_Y-6);

        rsi.child(c++, PREVIEW_BOX_ID, PREVIEW_X + SHIFT_X, PREVIEW_Y-600);
        rsi.child(c++, PREVIEW_NAME_ID, PREVIEW_X + (PREVIEW_W / 2) + SHIFT_X, PREVIEW_Y + 8-6 +80+60);
        rsi.child(c++, PREVIEW_NPC_ID, PREVIEW_X + 30 + SHIFT_X, PREVIEW_Y + 26+50-6+8);

        int statsX = PREVIEW_X + (PREVIEW_W / 2) + SHIFT_X;
        int statsBoxX = PREVIEW_X + (PREVIEW_W - STATS_W) / 2 + SHIFT_X;
        int statsBoxY = PREVIEW_Y + 160+12;
        rsi.child(c++, STATS_BOX_ID, statsBoxX, statsBoxY);
        rsi.child(c++, STATS_LINE1_ID, statsX-2, statsBoxY + 8);
        rsi.child(c++, STATS_LINE2_ID, statsX-2, statsBoxY + 20);

        rsi.child(c++, TELEPORT_BTN_ID, PREVIEW_X + (PREVIEW_W / 2) + SHIFT_X - 50, PREVIEW_Y + 205+7-1);

        rsi.child(c++, DESC_TITLE_ID, INFO_X + SHIFT_X, INFO_Y);
        rsi.child(c++, DESC_LINE1_ID, INFO_X + SHIFT_X, INFO_Y + 14);
        rsi.child(c++, DESC_LINE2_ID, INFO_X + SHIFT_X, INFO_Y + 28);

        rsi.child(c++, REQ_TITLE_ID, INFO_X + SHIFT_X, INFO_Y + 52);
        rsi.child(c++, REQ_LINE1_ID, INFO_X + SHIFT_X, INFO_Y + 66);
        rsi.child(c++, REQ_LINE2_ID, INFO_X + SHIFT_X, INFO_Y + 80);

        rsi.child(c++, QUEST_TITLE_ID, INFO_X + SHIFT_X, INFO_Y + 104);
        rsi.child(c++, QUEST_LINE1_ID, INFO_X + SHIFT_X, INFO_Y + 118);

        rsi.child(c++, LOOT_BOX_ID, LOOT_X + SHIFT_X - 5, LOOT_Y - 8);
        rsi.child(c++, LOOT_TITLE_ID, LOOT_X + (LOOT_W / 2) + SHIFT_X - 5, LOOT_Y);
        rsi.child(c++, LOOT_GRID_ID, LOOT_X + 9 + SHIFT_X - 5, LOOT_Y + 14);
    }

    /**
     * EXACT copy of the achievement grid-sprite helper (type=17),
     * but kept local to this class to avoid touching other files.
     *
     * It draws ONE tile from the atlas based on valueIndex/configId.
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

        r.valueIndex = valueIndex;
        r.configId = -1;

        r.tooltip = tooltip;
    }

    private static int findPreviewNpcId(int preferredId, int fallbackId) {
        int total = NpcDefinition.totalAmount;
        if (total <= 0) {
            System.out.println("[TeleportHomePage] npc defs not loaded; fallback npc=" + fallbackId);
            return fallbackId;
        }
        if (preferredId >= 0 && preferredId < total) {
            try {
                NpcDefinition preferred = NpcDefinition.forID(preferredId);
                if (preferred != null && preferred.models != null && preferred.models.length > 0) {
                    System.out.println("[TeleportHomePage] preview npc id=" + preferredId + " (preferred)");
                    return preferredId;
                }
            } catch (Exception e) {
                // fall back
            }
        }
        int searchMax = Math.min(total, 2000);
        for (int i = 0; i < searchMax; i++) {
            try {
                NpcDefinition def = NpcDefinition.forID(i);
                if (def != null && def.models != null && def.models.length > 0) {
                    return i;
                }
            } catch (Exception e) {
                // keep scanning
            }
        }
        return fallbackId;
    }


    private static void setRowHeadIndex(int row, int valueIndex) {
        int base = ROW_START_ID + (row * ROW_STRIDE);
        RSInterface head = RSInterface.interfaceCache[base + 2];
        if (head != null) {
            head.gridUseValueIndex = true;
            head.valueIndex = valueIndex;
            head.configId = -1;
        }
    }

    public static void applyRowHeadPayload(TeleportListPayload payload) {
        if (payload == null) {
            return;
        }
        List<TeleportRowIconPayload> entries = payload.getEntries();
        if (entries == null) {
            return;
        }
        for (TeleportRowIconPayload entry : entries) {
            if (entry == null) {
                continue;
            }
            setRowHeadIndex(entry.getRowIndex(), entry.getHeadIconIndex());
        }
    }

    private static void setRowText(int row, String text) {
        int base = ROW_START_ID + (row * ROW_STRIDE);
        if (RSInterface.interfaceCache[base + 1] != null) {
            RSInterface.interfaceCache[base + 1].message = text;
        }
    }
}
