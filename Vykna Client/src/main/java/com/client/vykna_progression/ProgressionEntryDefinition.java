package com.client.vykna_progression;

public class ProgressionEntryDefinition {
    private int entryId;
    private String name;
    private String description;
    private int listTypeId;
    private String subcategory;
    private int points;
    private String requirementKey;
    private int requirementTarget;
    private int spriteIndex;
    private boolean completed;
    private int progressCurrent;

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

    public boolean isCompleted() {
        return completed;
    }

    public int getProgressCurrent() {
        return progressCurrent;
    }
}
