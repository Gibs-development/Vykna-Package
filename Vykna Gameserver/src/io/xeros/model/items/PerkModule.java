package io.xeros.model.items;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class PerkModule {
    public static final class PerkDefinition {
        private final int id;
        private final String name;
        private final String description;

        private PerkDefinition(int id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
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
    }

    private static final Map<Integer, PerkDefinition> PERKS = new LinkedHashMap<>();
    private static final int[] PERK_IDS;

    static {
        register(100, "Drop Rate Boost", "+10% drop rate from PvM encounters.");
        register(101, "Crystal Finder", "Chance to find crystal keys on drops.");
        register(102, "Sharpshooter", "+10% ranged strength in PvM.");
        register(103, "Prayer Leech", "10% chance to restore prayer from hits.");
        register(104, "Major Drop Rate", "+20% drop rate from PvM encounters.");
        register(105, "Life Steal", "10% chance to restore HP from hits.");
        register(106, "Coin Hoarder", "Coin bags have a 25% chance to double.");
        register(107, "Arcane Power", "+10% magic strength in PvM.");
        register(108, "Resourceful", "Resource boxes have a 25% chance to double.");
        register(109, "Brute Force", "+10% melee strength in PvM.");
        register(110, "Clue Finder", "Clue scrolls have a 25% chance to double.");
        PERK_IDS = PERKS.keySet().stream().mapToInt(Integer::intValue).toArray();
    }

    private PerkModule() {
    }

    private static void register(int id, String name, String description) {
        PERKS.put(id, new PerkDefinition(id, name, description));
    }

    public static PerkDefinition forId(int id) {
        return PERKS.get(id);
    }

    public static Map<Integer, PerkDefinition> all() {
        return Collections.unmodifiableMap(PERKS);
    }

    public static int[] perkIds() {
        return Arrays.copyOf(PERK_IDS, PERK_IDS.length);
    }
}
