package io.xeros.content.vykna_achievements;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class VyknaAchievements {
    private static final Map<Integer, VyknaAchievementDefinition> BY_ID;

    static {
        Map<Integer, VyknaAchievementDefinition> defs = new HashMap<>();
        defs.put(1001, new VyknaAchievementDefinition(1001, "combat", "starter", 5, 10));
        defs.put(1002, new VyknaAchievementDefinition(1002, "combat", "starter", 25, 20));
        defs.put(1101, new VyknaAchievementDefinition(1101, "combat", "bossing", 1, 25));
        defs.put(1102, new VyknaAchievementDefinition(1102, "combat", "bossing", 10, 50));
        defs.put(2001, new VyknaAchievementDefinition(2001, "skilling", "gathering", 50, 10));
        defs.put(2002, new VyknaAchievementDefinition(2002, "skilling", "gathering", 250, 25));
        defs.put(2101, new VyknaAchievementDefinition(2101, "skilling", "artisan", 10, 15));
        defs.put(2102, new VyknaAchievementDefinition(2102, "skilling", "artisan", 100, 30));
        defs.put(3001, new VyknaAchievementDefinition(3001, "exploration", "regions", 1, 5));
        defs.put(3002, new VyknaAchievementDefinition(3002, "exploration", "regions", 5, 15));
        defs.put(3101, new VyknaAchievementDefinition(3101, "exploration", "oddities", 1, 10));
        defs.put(3102, new VyknaAchievementDefinition(3102, "exploration", "oddities", 3, 20));
        BY_ID = Collections.unmodifiableMap(defs);
    }

    private VyknaAchievements() {
    }

    public static VyknaAchievementDefinition getDefinition(int id) {
        return BY_ID.get(id);
    }

    public static List<VyknaAchievementDefinition> getByTypeGroup(String type, String group) {
        return BY_ID.values().stream()
                .filter(def -> def.getType().equalsIgnoreCase(type))
                .filter(def -> def.getGroup().equalsIgnoreCase(group))
                .sorted((a, b) -> Integer.compare(a.getId(), b.getId()))
                .collect(Collectors.toList());
    }

    public static List<VyknaAchievementDefinition> getAll() {
        return BY_ID.values().stream()
                .sorted((a, b) -> Integer.compare(a.getId(), b.getId()))
                .collect(Collectors.toList());
    }
}
