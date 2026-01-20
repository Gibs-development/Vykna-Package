package com.client.vykna_progression;

public enum ProgressionListType {
    TASKS(1, "Task"),
    SKILLING(2, "Skilling"),
    COMBAT(3, "Combat");

    private final int id;
    private final String displayName;

    ProgressionListType(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ProgressionListType fromId(int id) {
        for (ProgressionListType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return TASKS;
    }
}
