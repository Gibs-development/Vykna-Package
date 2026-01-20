package com.client.vykna_progression;

import java.util.List;

public class ProgressionListPayload {
    private int listTypeId;
    private List<String> subcategories;
    private List<ProgressionEntryDefinition> entries;

    public int getListTypeId() {
        return listTypeId;
    }

    public List<String> getSubcategories() {
        return subcategories;
    }

    public List<ProgressionEntryDefinition> getEntries() {
        return entries;
    }
}
