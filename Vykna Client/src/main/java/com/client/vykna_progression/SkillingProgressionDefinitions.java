package com.client.vykna_progression;

import java.util.ArrayList;
import java.util.List;

public class SkillingProgressionDefinitions {
    private List<String> subcategories = new ArrayList<>();
    private List<ProgressionEntryDefinition> entries = new ArrayList<>();

    public void update(ProgressionListPayload payload) {
        subcategories = payload.getSubcategories();
        entries = payload.getEntries();
    }

    public List<String> getSubcategories() {
        return subcategories;
    }

    public List<ProgressionEntryDefinition> getEntries() {
        return entries;
    }
}
