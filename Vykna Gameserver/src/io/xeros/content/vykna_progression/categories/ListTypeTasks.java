package io.xeros.content.vykna_progression.categories;

import io.xeros.content.vykna_progression.ProgressionEntry;
import io.xeros.content.vykna_progression.ProgressionListDefinition;
import io.xeros.content.vykna_progression.ProgressionListType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public enum ListTypeTasks {
    FIRE_STARTER(1000, "Fire Starter", "Burn 50 logs.", "Exploration",
            "logs_burned", 50, 5, 1),
    TOWN_TOURIST(1001, "Town Tourist", "Visit three major cities.", "Travel",
            "cities_visited", 3, 4, 2),
    EARLY_CHECKIN(1002, "Early Check-in", "Open the Vykna Progression menu.", "General",
            "open_progression", 1, 1, 3);

    private final int entryId;
    private final String name;
    private final String description;
    private final String subcategory;
    private final String requirementKey;
    private final int requirementTarget;
    private final int points;
    private final int spriteIndex;

    ListTypeTasks(int entryId, String name, String description, String subcategory,
                  String requirementKey, int requirementTarget, int points, int spriteIndex) {
        this.entryId = entryId;
        this.name = name;
        this.description = description;
        this.subcategory = subcategory;
        this.requirementKey = requirementKey;
        this.requirementTarget = requirementTarget;
        this.points = points;
        this.spriteIndex = spriteIndex;
    }

    public ProgressionEntry toEntry() {
        return new ProgressionEntry(entryId, name, description, ProgressionListType.TASKS.getId(),
                subcategory, points, requirementKey, requirementTarget, spriteIndex);
    }

    public static ProgressionListDefinition getDefinition() {
        List<ProgressionEntry> entries = new ArrayList<>();
        Set<String> subcategories = new TreeSet<>();
        for (ListTypeTasks entry : values()) {
            entries.add(entry.toEntry());
            subcategories.add(entry.subcategory);
        }
        return new ProgressionListDefinition(
                ProgressionListType.TASKS.getId(),
                ProgressionListType.TASKS.getDisplayName(),
                new ArrayList<>(subcategories),
                entries
        );
    }
}
