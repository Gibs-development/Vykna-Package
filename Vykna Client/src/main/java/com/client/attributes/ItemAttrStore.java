package com.client.attributes;

import com.client.DrawingArea;
import com.client.Sprite;

import java.util.HashMap;
import java.util.Map;

public final class ItemAttrStore {
    private static final Map<Integer, Integer> lastItemId = new HashMap<>();
    public static final class Attr {
        public int hash;
        public int rarityId;
        public int perk1, perk1Rank;
        public int perk2, perk2Rank;
    }
    /**
     * Canonical rarity mapping (icons + text colors):
     * Common   -> items/itemcommon.png   -> white/grey text
     * Uncommon -> items/itemuncommon.png -> green text
     * Rare     -> items/itemrare.png     -> blue text
     * Epic     -> items/itemepic.png     -> purple text
     * Mythic   -> items/itemmythic.png   -> orange/gold text
     */
    public static int rarityToColor(int rarityId) {
        switch (rarityId) {
            case 0: return 0xE0E0E0; // common
            case 1: return 0x2ECC71; // uncommon
            case 2: return 0x3B82F6; // rare
            case 3: return 0xA855F7; // epic
            case 4: return 0xF5A623; // mythic
            case 5: return 0xF5A623; // mythic (legacy)
            default: return -1;
        }
    }
    private static final Sprite COMMON   = new Sprite("items/itemcommon");
    private static final Sprite UNCOMMON = new Sprite("items/itemuncommon");
    private static final Sprite RARE     = new Sprite("items/itemrare");
    private static final Sprite EPIC     = new Sprite("items/itemepic");
    private static final Sprite MYTHIC   = new Sprite("items/itemmythic");
    public static Sprite spriteForRarity(int rarityId) {
        switch (rarityId) {
            case 0: return COMMON;
            case 1: return UNCOMMON;
            case 2: return RARE;
            case 3: return EPIC;
            case 4: return MYTHIC;
            case 5: return MYTHIC;
            default: return null;
        }
    }
    public static void clearAll() {
        map.clear();
        lastItemId.clear();
    }

    private static void drawOutline1px(int x, int y, int w, int h, int rgb) {
        // top
        DrawingArea.drawPixels(1, y, x, rgb, w);
        // bottom
        DrawingArea.drawPixels(1, y + h - 1, x, rgb, w);
        // left
        DrawingArea.drawPixels(h, y, x, rgb, 1);
        // right
        DrawingArea.drawPixels(h, y, x + w - 1, rgb, 1);
    }



    // key = (interfaceId << 16) | slot
    private static final Map<Integer, Attr> map = new HashMap<>();

    private static int key(int interfaceId, int slot) {
        return (interfaceId << 16) | (slot & 0xFFFF);
    }

    public static void put(int interfaceId, int slot, Attr a) {
        int k = key(interfaceId, slot);
        if (a == null) map.remove(k);
        else map.put(k, a);
    }

    public static void clear(int interfaceId, int slot) {
        map.remove(key(interfaceId, slot));
    }
    public static void onSlotItemSet(int interfaceId, int slot, int newItemId) {
        int k = key(interfaceId, slot);
        Integer old = lastItemId.get(k);

        // Normalize: empty slot is -1 or 0 depending on interface code; treat <=0 as empty
        if (newItemId <= 0) {
            // slot cleared -> clear attrs too
            lastItemId.remove(k);
            map.remove(k);
            return;
        }

        if (old != null && old != newItemId) {
            // item changed in-place -> attrs no longer valid
            map.remove(k);
        }

        lastItemId.put(k, newItemId);
    }

    public static void clearAllForInterface(int interfaceId, int size) {
        for (int slot = 0; slot < size; slot++) {
            int k = key(interfaceId, slot);
            lastItemId.remove(k);
            map.remove(k);
        }
    }

    public static void swap(int interfaceId, int a, int b) {
        if (a == b) return;

        int ka = key(interfaceId, a);
        int kb = key(interfaceId, b);

        Attr aa = map.get(ka);
        Attr bb = map.get(kb);

        if (bb == null) map.remove(ka);
        else map.put(ka, bb);

        if (aa == null) map.remove(kb);
        else map.put(kb, aa);
    }

    public static void move(int interfaceId, int from, int to) {
        if (from == to) return;

        int kFrom = key(interfaceId, from);
        int kTo = key(interfaceId, to);

        Attr a = map.get(kFrom);

        if (a == null) map.remove(kTo);
        else map.put(kTo, a);

        map.remove(kFrom);
    }


    public static Attr get(int interfaceId, int slot) {
        return map.get(key(interfaceId, slot));
    }
}
