package com.client.graphics.interfaces.impl;

import com.client.Sprite;
import com.client.TextDrawingArea;
import com.client.graphics.interfaces.RSInterface;

/**
 * Small walkable overlay "toast" shown when an achievement completes.
 */
public final class AchievementCompleteToast extends RSInterface {

    private static final String SPRITE_ROOT = "interfaces/vykna_achievements/";
    private static final String RECENT_ATLAS = SPRITE_ROOT + "AchievementRecentAtlas";

    // IMPORTANT:
    // This assumes the atlas sheet is stored as "AchievementRecentAtlas 0"
    // and contains a grid of 32x32 icons.
    private static final int TILE_SIZE = 32;
    private static final int DEFAULT_ICON_INDEX = 0;

    public static final int INTERFACE_ID = 64650;

    private static final int BG_ID = INTERFACE_ID + 1;
    private static final int ICON_ID = INTERFACE_ID + 2;

    private static final int TEXT_TITLE = INTERFACE_ID + 10;
    private static final int TEXT_NAME  = INTERFACE_ID + 11;
    private static final int TEXT_EXTRA = INTERFACE_ID + 12;

    private static final int X = 166;
    private static final int Y = 20;

    // cache cropped tiles so we don’t crop every time
    private static Sprite[] atlasTileCache;

    private AchievementCompleteToast() {}

    public static void build(TextDrawingArea[] tda) {
        RSInterface r = addInterface(INTERFACE_ID);

        addSprite(BG_ID, 0, SPRITE_ROOT + "LeftTabSelected");

        // Create icon component (we'll override sprite1/sprite2 with cropped tile)
        addSprite(ICON_ID, 0, SPRITE_ROOT + "LeftTabSelected"); // safe placeholder that definitely loads
        RSInterface icon = RSInterface.interfaceCache[ICON_ID];
        if (icon != null) {
            Sprite tile = getAtlasTile(DEFAULT_ICON_INDEX);
            if (tile != null) {
                icon.sprite1 = tile;
                icon.sprite2 = tile;
                icon.width = tile.myWidth;
                icon.height = tile.myHeight;
            } else {
                // fallback size so it doesn't explode layout
                icon.width = TILE_SIZE;
                icon.height = TILE_SIZE;
            }
        }

        addText(TEXT_TITLE, "Achievement complete!", tda, 2, 0xFFFFFF, true, true);
        addText(TEXT_NAME,  "", tda, 1, 0xFFD200, true, true);
        addText(TEXT_EXTRA, "", tda, 0, 0xFFFFFF, true, true);

        r.totalChildren(6);

        int c = 0;
        r.child(c++, BG_ID,   X,      Y);
        r.child(c++, ICON_ID, X + 12, Y + 8);

        r.child(c++, TEXT_TITLE, X + 95, Y + 10);
        r.child(c++, TEXT_NAME,  X + 95, Y + 26);
        r.child(c++, TEXT_EXTRA, X + 95, Y + 40);
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
            if (atlasTileCache != null) System.arraycopy(atlasTileCache, 0, newCache, 0, atlasTileCache.length);
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
}
