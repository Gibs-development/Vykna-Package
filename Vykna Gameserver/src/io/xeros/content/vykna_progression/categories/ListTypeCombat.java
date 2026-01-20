package io.xeros.content.vykna_progression.categories;

import io.xeros.content.vykna_progression.ProgressionEntry;
import io.xeros.content.vykna_progression.ProgressionListDefinition;
import io.xeros.content.vykna_progression.ProgressionListType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public enum ListTypeCombat {
    SLAYER_APPRENTICE(2000, "Slayer Apprentice", "Complete 5 Slayer tasks.", "Slayer",
            "slayer_tasks", 5, 5, 1),
    GIANT_MOLE_HUNTER(2001, "Giant Mole Hunter", "Defeat the Giant Mole 3 times.", "Giant Mole",
            "giant_mole_kills", 3, 8, 2),
    BANDOS_BREAKER(2002, "Bandos Breaker", "Defeat General Graardor 5 times.", "General Graardor",
            "graardor_kills", 5, 10, 3);

    private final int entryId;
    private final String name;
    private final String description;
    private final String subcategory;
    private final String requirementKey;
    private final int requirementTarget;
    private final int points;
    private final int spriteIndex;

    ListTypeCombat(int entryId, String name, String description, String subcategory,
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
        return new ProgressionEntry(entryId, name, description, ProgressionListType.COMBAT.getId(),
                subcategory, points, requirementKey, requirementTarget, spriteIndex);
    }

    public static ProgressionListDefinition getDefinition() {
        List<ProgressionEntry> entries = new ArrayList<>();
        Set<String> subcategories = new TreeSet<>();
        for (ListTypeCombat entry : values()) {
            entries.add(entry.toEntry());
            subcategories.add(entry.subcategory);
        }
        return new ProgressionListDefinition(
                ProgressionListType.COMBAT.getId(),
                ProgressionListType.COMBAT.getDisplayName(),
                new ArrayList<>(subcategories),
                entries
        );
    }
}
