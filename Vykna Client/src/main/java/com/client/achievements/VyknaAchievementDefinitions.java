package com.client.achievements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class VyknaAchievementDefinitions {

    public static final class Definition {
        private final int id;
        private final String name;
        private final String description;
        private final String icon;
        private final int points;
        private final String type;
        private final String group;
        private final int order;

        public Definition(int id, String name, String description, String icon, int points, String type, String group, int order) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.icon = icon;
            this.points = points;
            this.type = type;
            this.group = group;
            this.order = order;
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

        public String getIcon() {
            return icon;
        }

        public int getPoints() {
            return points;
        }

        public String getType() {
            return type;
        }

        public String getGroup() {
            return group;
        }

        public int getOrder() {
            return order;
        }
    }

    private static final Map<Integer, Definition> BY_ID;

    static {
        Map<Integer, Definition> defs = new HashMap<>();
        defs.put(1001, new Definition(1001, "First Blood", "Defeat 5 enemies.", "combat_sword", 10, "combat", "starter", 1));
        defs.put(1002, new Definition(1002, "Rampage", "Defeat 25 enemies.", "combat_sword", 20, "combat", "starter", 2));
        defs.put(1101, new Definition(1101, "Boss Spotter", "Defeat 1 boss.", "combat_boss", 25, "combat", "bossing", 1));
        defs.put(1102, new Definition(1102, "Boss Hunter", "Defeat 10 bosses.", "combat_boss", 50, "combat", "bossing", 2));
        defs.put(2001, new Definition(2001, "Gathering Basics", "Collect 50 resources.", "skill_gather", 10, "skilling", "gathering", 1));
        defs.put(2002, new Definition(2002, "Gathering Focus", "Collect 250 resources.", "skill_gather", 25, "skilling", "gathering", 2));
        defs.put(2101, new Definition(2101, "First Craft", "Craft 10 items.", "skill_craft", 15, "skilling", "artisan", 1));
        defs.put(2102, new Definition(2102, "Master Crafter", "Craft 100 items.", "skill_craft", 30, "skilling", "artisan", 2));
        defs.put(3001, new Definition(3001, "New Horizons", "Discover 1 region.", "explore_region", 5, "exploration", "regions", 1));
        defs.put(3002, new Definition(3002, "World Traveler", "Discover 5 regions.", "explore_region", 15, "exploration", "regions", 2));
        defs.put(3101, new Definition(3101, "Oddity Found", "Find 1 oddity.", "explore_oddity", 10, "exploration", "oddities", 1));
        defs.put(3102, new Definition(3102, "Oddity Hunter", "Find 3 oddities.", "explore_oddity", 20, "exploration", "oddities", 2));
        BY_ID = Collections.unmodifiableMap(defs);
    }

    private VyknaAchievementDefinitions() {
    }

    public static Definition get(int id) {
        return BY_ID.get(id);
    }

    public static List<Definition> byTypeGroup(String type, String group) {
        List<Definition> defs = new ArrayList<>();
        for (Definition def : BY_ID.values()) {
            if (def.getType().equalsIgnoreCase(type) && def.getGroup().equalsIgnoreCase(group)) {
                defs.add(def);
            }
        }
        defs.sort(Comparator.comparingInt(Definition::getOrder));
        return defs;
    }

    public static List<Definition> all() {
        return BY_ID.values().stream()
                .sorted(Comparator.comparingInt(Definition::getOrder))
                .collect(Collectors.toList());
    }
}
