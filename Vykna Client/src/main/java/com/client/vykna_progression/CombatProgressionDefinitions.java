package com.client.vykna_progression;

import java.util.ArrayList;
import java.util.List;

public class CombatProgressionDefinitions {
    private List<String> subcategories = new ArrayList<>();
    private List<ProgressionEntryDefinition> entries = new ArrayList<>();

    public void update(ProgressionListPayload payload, boolean replace) {
        if (replace) {
            subcategories = payload.getSubcategories();
            entries = new ArrayList<>(payload.getEntries());
        } else {
            if (payload.getEntries() != null) {
                entries.addAll(payload.getEntries());
            }
        }
    }

    public List<String> getSubcategories() {
        return subcategories;
    }

    public List<ProgressionEntryDefinition> getEntries() {
        return entries;
    }
}
