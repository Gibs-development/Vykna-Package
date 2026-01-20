package io.xeros.content.vykna_progression.categories;

import io.xeros.content.vykna_progression.ProgressionEntry;
import io.xeros.content.vykna_progression.ProgressionListDefinition;
import io.xeros.content.vykna_progression.ProgressionListType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public enum ListTypeSkilling {
    LOG_CHOPPER(3000, "Log Chopper", "Chop 100 logs.", "Woodcutting",
            "logs_chopped", 100, 5, 1),
    SHRIMP_FISHER(3001, "Shrimp Fisher", "Catch 50 shrimp.", "Fishing",
            "shrimp_caught", 50, 3, 2),
    ORE_SEEKER(3002, "Ore Seeker", "Mine 25 ore rocks.", "Mining",
            "ore_mined", 25, 4, 3),
    BONE_BURIER(3003, "Bone Burier", "Bury 10 bones.", "Prayer",
            "bones_buried", 10, 3, 4);

    private final int entryId;
    private final String name;
    private final String description;
    private final String subcategory;
    private final String requirementKey;
    private final int requirementTarget;
    private final int points;
    private final int spriteIndex;

    ListTypeSkilling(int entryId, String name, String description, String subcategory,
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
        return new ProgressionEntry(entryId, name, description, ProgressionListType.SKILLS.getId(),
                subcategory, points, requirementKey, requirementTarget, spriteIndex);
    }

    public static ProgressionListDefinition getDefinition() {
        List<ProgressionEntry> entries = new ArrayList<>();
        Set<String> subcategories = new TreeSet<>();
        for (ListTypeSkilling entry : values()) {
            entries.add(entry.toEntry());
            subcategories.add(entry.subcategory);
        }
        return new ProgressionListDefinition(
                ProgressionListType.SKILLS.getId(),
                ProgressionListType.SKILLS.getDisplayName(),
                new ArrayList<>(subcategories),
                entries
        );
    }
}
