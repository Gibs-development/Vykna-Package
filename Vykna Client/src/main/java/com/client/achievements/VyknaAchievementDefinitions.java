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
        private final int iconId;
        private final int points;
        private final String type;
        private final String group;
        private final int order;

        public Definition(int id, String name, String description, int iconId, int points, String type, String group, int order) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.iconId = iconId;
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

        public int getIconId() {
            return iconId;
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
        defs.put(1001, new Definition(1001, "First Blood", "Defeat 5 enemies.", 0, 10, "combat", "Starter", 1));
        defs.put(1002, new Definition(1002, "Rampage", "Defeat 25 enemies.", 1, 20, "combat", "Starter", 2));
        defs.put(1101, new Definition(1101, "Slayer Initiate", "Finish 5 slayer tasks.", 2, 15, "combat", "Slayer", 1));
        defs.put(1102, new Definition(1102, "Slayer Veteran", "Finish 50 slayer tasks.", 3, 30, "combat", "Slayer", 2));
        defs.put(1201, new Definition(1201, "Bandos Scout", "Defeat Bandos 1 time.", 1, 25, "combat", "Bandos", 1));
        defs.put(1202, new Definition(1202, "Bandos Commander", "Defeat Bandos 10 times.", 2, 50, "combat", "Bandos", 2));
        defs.put(2001, new Definition(2001, "Chop Chop", "Chop 50 logs.", 0, 10, "skilling", "Woodcutting", 1));
        defs.put(2002, new Definition(2002, "Timber!", "Chop 250 logs.", 1, 25, "skilling", "Woodcutting", 2));
        defs.put(2101, new Definition(2101, "String It", "Fletch 20 bows.", 2, 15, "skilling", "Fletching", 1));
        defs.put(2102, new Definition(2102, "Bowyer", "Fletch 100 bows.", 3, 30, "skilling", "Fletching", 2));
        defs.put(2201, new Definition(2201, "First Flame", "Burn 10 logs.", 0, 10, "skilling", "Firemaking", 1));
        defs.put(2202, new Definition(2202, "Blazing Trail", "Burn 100 logs.", 1, 25, "skilling", "Firemaking", 2));
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
