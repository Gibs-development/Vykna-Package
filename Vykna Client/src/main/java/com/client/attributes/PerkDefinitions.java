package com.client.attributes;

import com.client.Sprite;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class PerkDefinitions {
    public static final class PerkDefinition {
        private final int id;
        private final String name;
        private final String description;
        private final String iconKey;

        private PerkDefinition(int id, String name, String description, String iconKey) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.iconKey = iconKey;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Sprite getIcon(int rank) {
            return new Sprite("sprites/perks/" + iconKey + "+" + rank);
        }
    }

    private static final Map<Integer, PerkDefinition> PERKS = new HashMap<>();

    static {
        register(100, "Drop Rate Boost", "+10% drop rate from PvM encounters.", "drop_rate");
        register(101, "Crystal Finder", "Chance to find crystal keys on drops.", "crystal_key");
        register(102, "Sharpshooter", "+10% ranged strength in PvM.", "ranged_strength");
        register(103, "Prayer Leech", "10% chance to restore prayer from hits.", "prayer_leech");
        register(104, "Major Drop Rate", "+20% drop rate from PvM encounters.", "drop_rate_major");
        register(105, "Life Steal", "10% chance to restore HP from hits.", "life_steal");
        register(106, "Coin Hoarder", "Coin bags have a 25% chance to double.", "coin_bag");
        register(107, "Arcane Power", "+10% magic strength in PvM.", "magic_strength");
        register(108, "Resourceful", "Resource boxes have a 25% chance to double.", "resource_box");
        register(109, "Brute Force", "+10% melee strength in PvM.", "melee_strength");
        register(110, "Clue Finder", "Clue scrolls have a 25% chance to double.", "clue_scroll");
    }

    private PerkDefinitions() {
    }

    private static void register(int id, String name, String description, String iconKey) {
        PERKS.put(id, new PerkDefinition(id, name, description, iconKey));
    }

    public static PerkDefinition forId(int id) {
        return PERKS.get(id);
    }

    public static Map<Integer, PerkDefinition> all() {
        return Collections.unmodifiableMap(PERKS);
    }
}
