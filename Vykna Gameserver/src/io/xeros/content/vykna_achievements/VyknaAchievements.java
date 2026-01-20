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
        defs.put(1001, new VyknaAchievementDefinition(1001, "combat", "Starter", 5, 10));
        defs.put(1002, new VyknaAchievementDefinition(1002, "combat", "Starter", 25, 20));
        defs.put(1101, new VyknaAchievementDefinition(1101, "combat", "Slayer", 5, 15));
        defs.put(1102, new VyknaAchievementDefinition(1102, "combat", "Slayer", 50, 30));
        defs.put(1201, new VyknaAchievementDefinition(1201, "combat", "Bandos", 1, 25));
        defs.put(1202, new VyknaAchievementDefinition(1202, "combat", "Bandos", 10, 50));
        defs.put(2001, new VyknaAchievementDefinition(2001, "skilling", "Woodcutting", 50, 10));
        defs.put(2002, new VyknaAchievementDefinition(2002, "skilling", "Woodcutting", 250, 25));
        defs.put(2101, new VyknaAchievementDefinition(2101, "skilling", "Fletching", 20, 15));
        defs.put(2102, new VyknaAchievementDefinition(2102, "skilling", "Fletching", 100, 30));
        defs.put(2201, new VyknaAchievementDefinition(2201, "skilling", "Firemaking", 10, 10));
        defs.put(2202, new VyknaAchievementDefinition(2202, "skilling", "Firemaking", 100, 25));
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

    public static List<String> getTypes() {
        return BY_ID.values().stream()
                .map(VyknaAchievementDefinition::getType)
                .distinct()
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
    }

    public static List<String> getGroupsByType(String type) {
        return BY_ID.values().stream()
                .filter(def -> def.getType().equalsIgnoreCase(type))
                .map(VyknaAchievementDefinition::getGroup)
                .distinct()
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
    }
}
