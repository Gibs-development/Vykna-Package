package com.client.achievements;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Dummy achievement definitions for client-side testing.
 *
 * Server will eventually send a "definition id" (defId) for each recent completion slot.
 * Client will look up this definition and render spriteIndex from the atlas.
 */
public final class AchievementDefinitions {

    public static final class AchievementDefinition {
        public final int id;
        public final String key;
        public final String name;
        public final String description;
        /**
         * Index inside the sprite atlas (row-major).
         * For a 2x2 atlas: 0..3.
         */
        public final int spriteIndex;

        public AchievementDefinition(int id, String key, String name, String description, int spriteIndex) {
            this.id = id;
            this.key = key;
            this.name = name;
            this.description = description;
            this.spriteIndex = spriteIndex;
        }
    }

    private static final Map<Integer, AchievementDefinition> BY_ID;

    static {
        Map<Integer, AchievementDefinition> m = new HashMap<>();

        // --- Dummy data (IDs 1..4) ---
        m.put(1, new AchievementDefinition(
                1,
                "boss_graardor_5",
                "Bandos Brawler",
                "Defeat General Graardor five times.",
                0
        ));

        m.put(2, new AchievementDefinition(
                2,
                "skill_woodcut_50",
                "Lumberjack",
                "Reach 50 Woodcutting.",
                1
        ));

        m.put(3, new AchievementDefinition(
                3,
                "quest_first_steps",
                "First Steps",
                "Complete your first quest.",
                2
        ));

        m.put(4, new AchievementDefinition(
                4,
                "pvp_first_blood",
                "First Blood",
                "Win your first PvP fight.",
                3
        ));

        BY_ID = Collections.unmodifiableMap(m);
    }

    private AchievementDefinitions() {}

    public static AchievementDefinition getById(int id) {
        AchievementDefinition def = BY_ID.get(id);
        if (def == null) {
            // id=0 or unknown -> return a safe fallback to avoid NPEs
            return new AchievementDefinition(
                    0,
                    "unknown",
                    "Unknown",
                    "No description.",
                    0
            );
        }
        return def;
    }
}
