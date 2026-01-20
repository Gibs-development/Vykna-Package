package io.xeros.content.vykna_progression;

import java.util.List;

public class ProgressionListDefinition {
    private final int id;
    private final String displayName;
    private final List<String> subcategories;
    private final List<ProgressionEntry> entries;

    public ProgressionListDefinition(int id, String displayName, List<String> subcategories,
                                     List<ProgressionEntry> entries) {
        this.id = id;
        this.displayName = displayName;
        this.subcategories = subcategories;
        this.entries = entries;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getSubcategories() {
        return subcategories;
    }

    public List<ProgressionEntry> getEntries() {
        return entries;
    }
}
