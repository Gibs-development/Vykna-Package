package io.xeros.model.items;

public final class ItemAttributes {

    /** 0 = common, 1 = uncommon, ... */
    public byte rarityId;

    /** perks are future, keep fields now so save/packets don’t change later */
    public short perk1;
    public byte perk1Rank;
    public short perk2;
    public byte perk2Rank;

    public int computeHash() {
        int h = 17;
        h = 31 * h + rarityId;
        h = 31 * h + perk1;
        h = 31 * h + perk1Rank;
        h = 31 * h + perk2;
        h = 31 * h + perk2Rank;
        return h;
    }

    public ItemAttributes copy() {
        ItemAttributes c = new ItemAttributes();
        c.rarityId = rarityId;
        c.perk1 = perk1;
        c.perk1Rank = perk1Rank;
        c.perk2 = perk2;
        c.perk2Rank = perk2Rank;
        return c;
    }

    public boolean equalsAttrs(ItemAttributes o) {
        if (o == null) return false;
        return rarityId == o.rarityId
                && perk1 == o.perk1 && perk1Rank == o.perk1Rank
                && perk2 == o.perk2 && perk2Rank == o.perk2Rank;
    }

    public static String rarityName(int rarityId) {
        switch (rarityId) {
            case 0:
                return "Common";
            case 1:
                return "Uncommon";
            case 2:
                return "Rare";
            case 3:
                return "Epic";
            case 4:
                return "Legendary";
            case 5:
                return "Mythic";
            default:
                return "Unknown";
        }
    }

    public static String rarityLabel(ItemAttributes attrs) {
        if (attrs == null || attrs.rarityId <= 0) {
            return "";
        }
        return " [" + rarityName(attrs.rarityId).toUpperCase() + "]";
    }
}
