package io.xeros.content.vykna_progression;

public class ProgressionEntry {
    private final int entryId;
    private final String name;
    private final String description;
    private final int listTypeId;
    private final String subcategory;
    private final int points;
    private final String requirementKey;
    private final int requirementTarget;
    private final int spriteIndex;

    public ProgressionEntry(int entryId, String name, String description, int listTypeId, String subcategory,
                            int points, String requirementKey, int requirementTarget, int spriteIndex) {
        this.entryId = entryId;
        this.name = name;
        this.description = description;
        this.listTypeId = listTypeId;
        this.subcategory = subcategory;
        this.points = points;
        this.requirementKey = requirementKey;
        this.requirementTarget = requirementTarget;
        this.spriteIndex = spriteIndex;
    }

    public int getEntryId() {
        return entryId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getListTypeId() {
        return listTypeId;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public int getPoints() {
        return points;
    }

    public String getRequirementKey() {
        return requirementKey;
    }

    public int getRequirementTarget() {
        return requirementTarget;
    }

    public int getSpriteIndex() {
        return spriteIndex;
    }
}
