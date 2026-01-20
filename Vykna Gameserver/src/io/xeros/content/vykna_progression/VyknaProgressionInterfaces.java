package io.xeros.content.vykna_progression;

public final class VyknaProgressionInterfaces {

    private VyknaProgressionInterfaces() {}

    // Home page opens to 35000 (you already have this working)
    public static final int HOME_INTERFACE_ID = 35000;

    // List page interface (client: AchievementListPage.INTERFACE_ID)
    public static final int LIST_INTERFACE_ID = 64504;

    // Client ids (from the class you pasted)
    public static final int TEXT_TITLE = LIST_INTERFACE_ID + 2000;     // 66504
    public static final int TEXT_PROGRESS = LIST_INTERFACE_ID + 4000;  // 68504

    // Row layout
    public static final int ROW_START_ID = LIST_INTERFACE_ID + 3100;   // 67604
    public static final int ROW_STRIDE = 30;

    // What server needs to set per row:
    // base+1 = title text
    // base+2 = description text
    // base+3 = points text (right aligned)
    // base+8 = progress text "[x/y]" (optional)
}
