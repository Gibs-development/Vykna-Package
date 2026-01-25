package com.client.graphics.interfaces.impl;

import com.client.TextDrawingArea;
import com.client.graphics.interfaces.RSInterface;

/**
 * Teleport interface (client-side layout only; dummy data for now).
 *
 * Polished pass:
 * - Uses sprites for: background, top tabs, teleport button, alternating row backgrounds, search bg, close (X)
 * - Alternating selection rows now vary the MAIN fill (via sprites), not just the stroke.
 *
 * NPC Preview:
 * - Uses existing RSInterface#addNpcModel(...) (you already have this in RSInterface.java).
 * - Dummy NPC id is set to 100 so you see something immediately.
 * - Later, server can update the NPC shown via opcode 75 (sendNpcHeadOnInterface), targeting PREVIEW_NPC_ID.
 *
 * NOTE: Keep interface id < 65535 due to opcode 97 (2-byte interface id).
 */
public final class TeleportHomePage extends RSInterface {

    private static final String SPRITE_ROOT = "interfaces/vykna_teleports/";

    /**
     * Free range below 65535.
     * Recommended: 30478 - 32648.
     */
    public static final int INTERFACE_ID = 31000;

    // ---- SAFE ID BLOCKS ----
    private static final int BG_ID = INTERFACE_ID + 1;

    // Header / tabs
    private static final int TITLE_ID = INTERFACE_ID + 50;

    private static final int TAB_MONSTERS_BTN = INTERFACE_ID + 200;
    private static final int TAB_BOSSES_BTN   = INTERFACE_ID + 220;
    private static final int TAB_ACTS_BTN     = INTERFACE_ID + 240;
    private static final int TAB_QUESTS_BTN   = INTERFACE_ID + 260;

    private static final int TAB_MONSTERS_TXT = INTERFACE_ID + 201;
    private static final int TAB_BOSSES_TXT   = INTERFACE_ID + 221;
    private static final int TAB_ACTS_TXT     = INTERFACE_ID + 241;
    private static final int TAB_QUESTS_TXT   = INTERFACE_ID + 261;

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

    // --- Layout constants ---
    private static final int BG_X = 8, BG_Y = 8;

    // Final background art size: 497x322.
    @SuppressWarnings("unused")
    private static final int BG_W = 497;
    @SuppressWarnings("unused")
    private static final int BG_H = 322;

    // Global nudge (tune visually)
    private static final int SHIFT_X = 12;

    // Header
    private static final int TITLE_X = BG_X + 18;
    private static final int TITLE_Y = BG_Y + 10;

    private static final int TABS_Y = BG_Y + 28;
    private static final int TAB_START_X = BG_X + 54;
    private static final int TAB_GAP = 85;
    private static final int TAB_W = 80;
    private static final int TAB_H = 22;

    // Search
    private static final int SEARCH_W = 142;
    private static final int SEARCH_X = BG_X + 318;
    private static final int SEARCH_Y = BG_Y + 26;
    private static final int SEARCH_CLEAR_X = SEARCH_X + SEARCH_W + 4;

    // Left list
    private static final int LIST_X = BG_X + 18;
    private static final int LIST_Y = BG_Y + 58;
    private static final int LIST_W = 150;
    private static final int LIST_H = 232;

    // Center preview
    private static final int PREVIEW_X = BG_X + 190;
    private static final int PREVIEW_Y = BG_Y + 58;
    private static final int PREVIEW_W = 160;
    private static final int PREVIEW_H = 145;

    // Stats box
    private static final int STATS_W = 146;
    private static final int STATS_H = 34;

    // Right column info
    private static final int INFO_X = BG_X + 350;
    private static final int INFO_Y = BG_Y + 58;

    // Loot panel
    private static final int LOOT_X = BG_X + 350;
    private static final int LOOT_Y = BG_Y + 196;
    private static final int LOOT_W = 138;
    private static final int LOOT_H = 102;

    private TeleportHomePage() {}

    public static void build(TextDrawingArea[] tda) {
        RSInterface rsi = addTabInterface(INTERFACE_ID);

        // Background: expects "Background 0.png"
        addSprite(BG_ID, 0, SPRITE_ROOT + "Background");

        // Title
        addText(TITLE_ID, "Vykna Teleports", tda, 2, 0xE3AE19, false, true);

        // ---- Tabs (sprite buttons + centered text) ----
        addHoverButtonNew(TAB_MONSTERS_BTN, SPRITE_ROOT + "TopTab", SPRITE_ROOT + "TopTabHover",
                TAB_W, TAB_H, "Monsters", 0, 1);
        addHoverButtonNew(TAB_BOSSES_BTN, SPRITE_ROOT + "TopTab", SPRITE_ROOT + "TopTabHover",
                TAB_W, TAB_H, "Bosses", 0, 1);
        addHoverButtonNew(TAB_ACTS_BTN, SPRITE_ROOT + "TopTab", SPRITE_ROOT + "TopTabHover",
                TAB_W, TAB_H, "Activities", 0, 1);
        addHoverButtonNew(TAB_QUESTS_BTN, SPRITE_ROOT + "TopTab", SPRITE_ROOT + "TopTabHover",
                TAB_W, TAB_H, "Quests", 0, 1);

        addText(TAB_MONSTERS_TXT, "Monsters", tda, 1, 0xE3AE19, true, true);
        addText(TAB_BOSSES_TXT, "Bosses", tda, 1, 0xE3AE19, true, true);
        addText(TAB_ACTS_TXT, "Activities", tda, 1, 0xE3AE19, true, true);
        addText(TAB_QUESTS_TXT, "Quests", tda, 1, 0xE3AE19, true, true);

        // ---- Search ----
        addBox(SEARCH_BG_ID, 0x2b2118, 0x1f1812, 120, SEARCH_W, 18);
        addHoverText(SEARCH_TEXT_ID, "Search...", "Search teleports", tda, 0, 0x9a8b7a, false, true, SEARCH_W, 16);

        // Clear X: expects "Close 0.png" and "CloseHover 0.png"
        addHoverButtonNew(SEARCH_CLEAR_ID, SPRITE_ROOT + "Close", SPRITE_ROOT + "CloseHover",
                16, 16, "Clear", 0, 1);

        // ---- Left scroll list ----
        RSInterface scroll = addTabInterface(SCROLL_ID);
        scroll.width = LIST_W;
        scroll.height = LIST_H;
        scroll.scrollMax = Math.max(MAX_ROWS * 22, scroll.height);
        scroll.totalChildren(MAX_ROWS * 3);

        final int rowW = LIST_W - 6;
        final int rowH = 20;

        for (int i = 0; i < MAX_ROWS; i++) {
            final int base = ROW_START_ID + (i * ROW_STRIDE);

            if (i % 2 == 0) {
                addSprite(base + 0, 0, SPRITE_ROOT + "RowLight");
            } else {
                addSprite(base + 0, 0, SPRITE_ROOT + "RowDark");
            }
            if (RSInterface.interfaceCache[base + 0] != null) {
                RSInterface.interfaceCache[base + 0].width = rowW;
                RSInterface.interfaceCache[base + 0].height = rowH;
            }

            addText(base + 1, "", tda, 0, 0xE3AE19, false, true);

            // Click zone overlay (empty; prevents duplicate text rendering)
            addHoverText(base + 2, "", "Select teleport", tda, 0, 0xFFFFFF, false, true, LIST_W, rowH);

            if (RSInterface.interfaceCache[base + 0] != null) RSInterface.interfaceCache[base + 0].parentID = SCROLL_ID;
            if (RSInterface.interfaceCache[base + 1] != null) RSInterface.interfaceCache[base + 1].parentID = SCROLL_ID;
            if (RSInterface.interfaceCache[base + 2] != null) RSInterface.interfaceCache[base + 2].parentID = SCROLL_ID;

            int y = i * 22;
            scroll.child(i * 3,     base + 0, 3, y);
            scroll.child(i * 3 + 1, base + 1, 10, y + 4);
            scroll.child(i * 3 + 2, base + 2, 0, y);
        }

        // Dummy row labels
        setRowText(0, "Goblins");
        setRowText(1, "Hill Giants");
        setRowText(2, "Green Dragons");
        setRowText(3, "Giant Mole");
        setRowText(4, "Barrows");

        // ---- Center preview ----
        addBox(PREVIEW_BOX_ID, 0x2b241d, 0x1f1812, 90, PREVIEW_W, PREVIEW_H);
        addText(PREVIEW_NAME_ID, "Goblins", tda, 1, 0xE3AE19, true, true);

        // NPC model display (DUMMY)
        // IMPORTANT: Your renderer uses:
        // - modelRotation1 for sin/cos + pitch
        // - anInt257/anInt258 for animation ids (must be -1 if you don't want anim lookup)
        // - method209(...) uses anInt233/mediaID to pick NPC definition
        //
        // This dummy setup renders immediately client-side without any server packet.
        final int DUMMY_NPC_ID = 1; // change if your cache uses different ids
        addNpcModel(PREVIEW_NPC_ID, DUMMY_NPC_ID, 900, 1, 1);

        RSInterface npcWidget = RSInterface.interfaceCache[PREVIEW_NPC_ID];
        if (npcWidget != null) {
            npcWidget.type = 6;
            npcWidget.anInt233 = 2; // NPC
            npcWidget.mediaID = DUMMY_NPC_ID;

            npcWidget.modelZoom = 900;
            npcWidget.modelRotation1 = 180; // pitch (avoid 0)
            npcWidget.modelRotation2 = 40;  // yaw

            // Ensure we don't try to pull an animation (prevents null/odd rendering)
            npcWidget.anInt257 = -1;
            npcWidget.anInt258 = -1;
            npcWidget.anInt246 = 0;

            npcWidget.width = 100;
            npcWidget.height = 90;
        }

        // Stats box
        addBox(STATS_BOX_ID, 0x231d16, 0x1f1812, 110, STATS_W, STATS_H);
        addText(STATS_LINE1_ID, "Combat: 2   |   HP: 50", tda, 0, 0xFFFAE5, true, true);
        addText(STATS_LINE2_ID, "Aggressive: No", tda, 0, 0xFFFAE5, true, true);

        // Teleport button (sprite)
        addHoverButtonNew(TELEPORT_BTN_ID, SPRITE_ROOT + "TeleportBtn", SPRITE_ROOT + "TeleportBtnHover",
                100, 22, "Teleport", 0, 1);

        // ---- Right column info ----
        addText(DESC_TITLE_ID, "Description", tda, 0, 0xE3AE19, false, true);
        addText(DESC_LINE1_ID, "A small green nuisance found", tda, 0, 0xFFFAE5, false, true);
        addText(DESC_LINE2_ID, "across the world.", tda, 0, 0xFFFAE5, false, true);

        addText(REQ_TITLE_ID, "Requirements", tda, 0, 0xE3AE19, false, true);
        addText(REQ_LINE1_ID, "Combat level: 2", tda, 0, 0xFFFAE5, false, true);
        addText(REQ_LINE2_ID, "HP: 50", tda, 0, 0xFFFAE5, false, true);

        addText(QUEST_TITLE_ID, "Quest", tda, 0, 0xE3AE19, false, true);
        addText(QUEST_LINE1_ID, "None", tda, 0, 0xFFFAE5, false, true);

        // ---- Loot panel ----
        addBox(LOOT_BOX_ID, 0x2b241d, 0x1f1812, 90, LOOT_W, LOOT_H);
        addText(LOOT_TITLE_ID, "Drops", tda, 1, 0xE3AE19, true, true);
        addBox(LOOT_GRID_ID, 0x000000, 0x000000, 0, LOOT_W - 18, LOOT_H - 28);

        // ---- Children ----
        // Final child count = 36
        rsi.totalChildren(36);
        int c = 0;

        rsi.child(c++, BG_ID, BG_X + SHIFT_X, BG_Y);

        rsi.child(c++, TITLE_ID, TITLE_X + SHIFT_X, TITLE_Y);

        int tabY = TABS_Y;
        int tab0 = TAB_START_X + SHIFT_X;

        rsi.child(c++, TAB_MONSTERS_BTN, tab0 + (TAB_GAP * 0), tabY);
        rsi.child(c++, TAB_BOSSES_BTN,   tab0 + (TAB_GAP * 1), tabY);
        rsi.child(c++, TAB_ACTS_BTN,     tab0 + (TAB_GAP * 2), tabY);
        rsi.child(c++, TAB_QUESTS_BTN,   tab0 + (TAB_GAP * 3), tabY);

        rsi.child(c++, TAB_MONSTERS_TXT, tab0 + (TAB_GAP * 0) + (TAB_W / 2), tabY + 6);
        rsi.child(c++, TAB_BOSSES_TXT,   tab0 + (TAB_GAP * 1) + (TAB_W / 2), tabY + 6);
        rsi.child(c++, TAB_ACTS_TXT,     tab0 + (TAB_GAP * 2) + (TAB_W / 2), tabY + 6);
        rsi.child(c++, TAB_QUESTS_TXT,   tab0 + (TAB_GAP * 3) + (TAB_W / 2), tabY + 6);

        rsi.child(c++, SEARCH_BG_ID, SEARCH_X + SHIFT_X, SEARCH_Y);
        rsi.child(c++, SEARCH_TEXT_ID, SEARCH_X + 5 + SHIFT_X, SEARCH_Y + 3);
        rsi.child(c++, SEARCH_CLEAR_ID, SEARCH_CLEAR_X + SHIFT_X, SEARCH_Y + 1);

        rsi.child(c++, SCROLL_ID, LIST_X + SHIFT_X, LIST_Y);

        rsi.child(c++, PREVIEW_BOX_ID, PREVIEW_X + SHIFT_X, PREVIEW_Y);
        rsi.child(c++, PREVIEW_NAME_ID, PREVIEW_X + (PREVIEW_W / 2) + SHIFT_X, PREVIEW_Y + 8);
        rsi.child(c++, PREVIEW_NPC_ID, PREVIEW_X + 30 + SHIFT_X, PREVIEW_Y + 26);

        int statsX = PREVIEW_X + (PREVIEW_W / 2) + SHIFT_X;
        int statsBoxX = PREVIEW_X + (PREVIEW_W - STATS_W) / 2 + SHIFT_X;
        int statsBoxY = PREVIEW_Y + 108;
        rsi.child(c++, STATS_BOX_ID, statsBoxX, statsBoxY);
        rsi.child(c++, STATS_LINE1_ID, statsX, statsBoxY + 8);
        rsi.child(c++, STATS_LINE2_ID, statsX, statsBoxY + 20);

        rsi.child(c++, TELEPORT_BTN_ID, PREVIEW_X + (PREVIEW_W / 2) + SHIFT_X - 50, PREVIEW_Y + 145);

        rsi.child(c++, DESC_TITLE_ID, INFO_X + SHIFT_X, INFO_Y);
        rsi.child(c++, DESC_LINE1_ID, INFO_X + SHIFT_X, INFO_Y + 14);
        rsi.child(c++, DESC_LINE2_ID, INFO_X + SHIFT_X, INFO_Y + 28);

        rsi.child(c++, REQ_TITLE_ID, INFO_X + SHIFT_X, INFO_Y + 52);
        rsi.child(c++, REQ_LINE1_ID, INFO_X + SHIFT_X, INFO_Y + 66);
        rsi.child(c++, REQ_LINE2_ID, INFO_X + SHIFT_X, INFO_Y + 80);

        rsi.child(c++, QUEST_TITLE_ID, INFO_X + SHIFT_X, INFO_Y + 104);
        rsi.child(c++, QUEST_LINE1_ID, INFO_X + SHIFT_X, INFO_Y + 118);

        rsi.child(c++, LOOT_BOX_ID, LOOT_X + SHIFT_X, LOOT_Y);
        rsi.child(c++, LOOT_TITLE_ID, LOOT_X + (LOOT_W / 2) + SHIFT_X, LOOT_Y + 8);
        rsi.child(c++, LOOT_GRID_ID, LOOT_X + 9 + SHIFT_X, LOOT_Y + 22);
    }

    private static void setRowText(int row, String text) {
        int base = ROW_START_ID + (row * ROW_STRIDE);
        if (RSInterface.interfaceCache[base + 1] != null) {
            RSInterface.interfaceCache[base + 1].message = text;
        }
        if (RSInterface.interfaceCache[base + 2] != null) {
            RSInterface.interfaceCache[base + 2].hoverText = text;
        }
    }
}
