package com.client.graphics.interfaces.impl;

import com.client.TextDrawingArea;
import com.client.graphics.interfaces.RSInterface;

/**
 * Small walkable overlay "toast" shown when an achievement completes.
 * Uses walkableInterfaceId so it doesn't replace main interfaces.
 */
public final class AchievementCompleteToast extends RSInterface {

    private static final String SPRITE_ROOT = "interfaces/vykna_achievements/";

    // Pick a safe range that won't collide with your 64504 page
    public static final int INTERFACE_ID = 64650;

    private static final int BG_ID = INTERFACE_ID + 1;
    private static final int ICON_ID = INTERFACE_ID + 2;

    private static final int TEXT_TITLE = INTERFACE_ID + 10;
    private static final int TEXT_NAME  = INTERFACE_ID + 11;
    private static final int TEXT_EXTRA = INTERFACE_ID + 12; // points/progress line

    // Position (tweak if you want)
    private static final int X = 166; // ~centered on 512px wide frame for a ~180px panel
    private static final int Y = 20;

    private AchievementCompleteToast() {}

    public static void build(TextDrawingArea[] tda) {
        RSInterface r = addInterface(INTERFACE_ID);

        // Background panel sprite (reuse something you already have)
        // If LeftTabSelected is too small/large, swap to another existing sprite.
        addSprite(BG_ID, 0, SPRITE_ROOT + "LeftTabSelected");

        // Icon (reuse any existing icon sprite you have; you can swap to an atlas later)
        // We'll just point at a default icon initially; client will swap sprite via setToastIcon(...)
        addSprite(ICON_ID, 0, SPRITE_ROOT + "TasksIcon");

        addText(TEXT_TITLE, "Achievement complete!", tda, 2, 0xFFFFFF, true, true);
        addText(TEXT_NAME,  "", tda, 1, 0xFFD200, true, true);
        addText(TEXT_EXTRA, "", tda, 0, 0xFFFFFF, true, true);

        r.totalChildren(6);

        int c = 0;
        r.child(c++, BG_ID,   X,     Y);
        r.child(c++, ICON_ID, X + 12, Y + 8);

        // Text centered in the panel area
        r.child(c++, TEXT_TITLE, X + 95, Y + 10);
        r.child(c++, TEXT_NAME,  X + 95, Y + 26);
        r.child(c++, TEXT_EXTRA, X + 95, Y + 40);

        // Start hidden: client sets walkableInterfaceId when showing
    }

    /**
     * Called by Client when showing a toast.
     * This avoids needing to rebuild the interface.
     */
    public static void setToastText(String name, String extraLine) {
        if (RSInterface.interfaceCache[TEXT_NAME] != null) {
            RSInterface.interfaceCache[TEXT_NAME].message = name;
        }
        if (RSInterface.interfaceCache[TEXT_EXTRA] != null) {
            RSInterface.interfaceCache[TEXT_EXTRA].message = extraLine == null ? "" : extraLine;
        }
    }

    /**
     * Set the toast icon to an existing sprite path.
     * You can later switch this to atlas grid icon (valueIndex) if you prefer.
     */
    public static void setToastIcon(String spritePath) {
        if (RSInterface.interfaceCache[ICON_ID] != null) {
            RSInterface.interfaceCache[ICON_ID].sprite1 = imageLoader(0, spritePath);
            RSInterface.interfaceCache[ICON_ID].sprite2 = RSInterface.interfaceCache[ICON_ID].sprite1;
        }
    }
}
