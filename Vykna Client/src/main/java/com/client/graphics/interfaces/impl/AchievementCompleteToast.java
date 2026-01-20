package com.client.graphics.interfaces.impl;

import com.client.Sprite;
import com.client.TextDrawingArea;
import com.client.graphics.interfaces.RSInterface;

/**
 * Small walkable overlay "toast" shown when an redundant_achievement completes.
 */
public final class AchievementCompleteToast extends RSInterface {

    private static final String SPRITE_ROOT = "interfaces/vykna_progression/";
    private static final String RECENT_ATLAS = SPRITE_ROOT + "AchievementRecentAtlas";

    // Atlas tiles
    private static final int TILE_SIZE = 32;
    private static final int DEFAULT_ICON_INDEX = 0;

    // Panel size
    private static final int PANEL_W = 240;
    private static final int PANEL_H = 64;

    // Position (centered on fixed 512px layout)
    private static final int X = (512 - PANEL_W) / 2;
    private static final int Y = 20;

    public static final int INTERFACE_ID = 64650;

    private static final int BG_ID = INTERFACE_ID + 1;
    private static final int ICON_ID = INTERFACE_ID + 2;

    private static final int TEXT_TITLE = INTERFACE_ID + 10;
    private static final int TEXT_NAME  = INTERFACE_ID + 11;
    private static final int TEXT_EXTRA = INTERFACE_ID + 12;

    // cache cropped tiles so we don’t crop every time
    private static Sprite[] atlasTileCache;

    private AchievementCompleteToast() {}

    public static void build(TextDrawingArea[] tda) {
        RSInterface r = addInterface(INTERFACE_ID);

        // Background placeholder (we override with generated panel sprite)
        addSprite(BG_ID, 0, SPRITE_ROOT + "LeftTabSelected");
        RSInterface bg = RSInterface.interfaceCache[BG_ID];
        if (bg != null) {
            Sprite panel = buildPanelSprite(PANEL_W, PANEL_H);
            bg.sprite1 = panel;
            bg.sprite2 = panel;
            bg.width = PANEL_W;
            bg.height = PANEL_H;

            // Only if your RSInterface supports it; harmless if unused
            bg.spriteOpacity = 210; // 0..255 (lower = more transparent)
        }

        // Icon placeholder (we override with cropped atlas tile)
        addSprite(ICON_ID, 0, SPRITE_ROOT + "LeftTabSelected");
        RSInterface icon = RSInterface.interfaceCache[ICON_ID];
        if (icon != null) {
            Sprite tile = getAtlasTile(DEFAULT_ICON_INDEX);
            if (tile != null) {
                icon.sprite1 = tile;
                icon.sprite2 = tile;
                icon.width = tile.myWidth;
                icon.height = tile.myHeight;
            } else {
                icon.width = TILE_SIZE;
                icon.height = TILE_SIZE;
            }
        }

        // Left aligned text looks much cleaner in a toast
        addText(TEXT_TITLE, "Achievement complete!", tda, 2, 0xE6C35A, false, true);
        addText(TEXT_NAME,  "", tda, 1, 0xFFFFFF, false, true);
        addText(TEXT_EXTRA, "", tda, 0, 0xC8C8C8, false, true);

        r.totalChildren(6);

        int c = 0;
        r.child(c++, BG_ID,   X,      Y);
        r.child(c++, ICON_ID, X + 10, Y + 16);

        int textX = X + 52; // icon (32) + padding
        r.child(c++, TEXT_TITLE, textX, Y + 12);
        r.child(c++, TEXT_NAME,  textX, Y + 28);
        r.child(c++, TEXT_EXTRA, textX, Y + 44);
    }

    public static void setToastText(String name, String extraLine) {
        RSInterface n = RSInterface.interfaceCache[TEXT_NAME];
        if (n != null) n.message = name;

        RSInterface e = RSInterface.interfaceCache[TEXT_EXTRA];
        if (e != null) e.message = (extraLine == null) ? "" : extraLine;
    }

    /** Sets the icon by atlas tile index (32x32). */
    public static void setToastIconIndex(int atlasIndex) {
        RSInterface icon = RSInterface.interfaceCache[ICON_ID];
        if (icon == null) return;

        Sprite tile = getAtlasTile(atlasIndex);
        if (tile == null) return;

        icon.sprite1 = tile;
        icon.sprite2 = tile;
        icon.width = tile.myWidth;
        icon.height = tile.myHeight;
    }

    /** Loads atlas sheet and crops a tile. */
    private static Sprite getAtlasTile(int index) {
        if (index < 0) index = 0;

        // grow cache if needed
        if (atlasTileCache == null || index >= atlasTileCache.length) {
            int newSize = Math.max(index + 1, atlasTileCache == null ? 64 : atlasTileCache.length * 2);
            Sprite[] newCache = new Sprite[newSize];
            if (atlasTileCache != null) {
                System.arraycopy(atlasTileCache, 0, newCache, 0, atlasTileCache.length);
            }
            atlasTileCache = newCache;
        }

        if (atlasTileCache[index] != null) {
            return atlasTileCache[index];
        }

        // Load the atlas SHEET (must exist as "AchievementRecentAtlas 0")
        Sprite sheet = imageLoader(0, RECENT_ATLAS);
        if (sheet == null || sheet.myPixels == null) {
            return null;
        }

        int cols = sheet.myWidth / TILE_SIZE;
        if (cols <= 0) return null;

        int sx = (index % cols) * TILE_SIZE;
        int sy = (index / cols) * TILE_SIZE;

        // bounds check
        if (sx + TILE_SIZE > sheet.myWidth || sy + TILE_SIZE > sheet.myHeight) {
            return null;
        }

        // Crop pixels
        Sprite out = new Sprite(TILE_SIZE, TILE_SIZE);
        for (int y = 0; y < TILE_SIZE; y++) {
            int srcOff = (sy + y) * sheet.myWidth + sx;
            int dstOff = y * TILE_SIZE;
            System.arraycopy(sheet.myPixels, srcOff, out.myPixels, dstOff, TILE_SIZE);
        }

        atlasTileCache[index] = out;
        return out;
    }

    /** Generates a simple dark panel with a gold border (no external sprites required). */
    private static Sprite buildPanelSprite(int w, int h) {
        Sprite s = new Sprite(w, h);

        final int fill = 0x151515;     // dark fill
        final int border = 0x8A6A2F;   // gold-ish border
        final int border2 = 0x2B2011;  // inner border for depth

        // Fill
        for (int i = 0; i < s.myPixels.length; i++) {
            s.myPixels[i] = fill;
        }

        // Outer border
        for (int x = 0; x < w; x++) {
            s.myPixels[x] = border;                       // top
            s.myPixels[(h - 1) * w + x] = border;        // bottom
        }
        for (int y = 0; y < h; y++) {
            s.myPixels[y * w] = border;                  // left
            s.myPixels[y * w + (w - 1)] = border;        // right
        }

        // Inner border (1px inset)
        if (w > 4 && h > 4) {
            for (int x = 1; x < w - 1; x++) {
                s.myPixels[1 * w + x] = border2;
                s.myPixels[(h - 2) * w + x] = border2;
            }
            for (int y = 1; y < h - 1; y++) {
                s.myPixels[y * w + 1] = border2;
                s.myPixels[y * w + (w - 2)] = border2;
            }
        }

        return s;
    }
}
