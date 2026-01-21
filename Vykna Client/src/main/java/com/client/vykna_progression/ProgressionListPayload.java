package com.client.vykna_progression;

import java.util.List;

public class ProgressionListPayload {
    private int listTypeId;
    private List<String> subcategories;
    private List<ProgressionEntryDefinition> entries;
    private int pageIndex;
    private int pageSize;
    private int totalEntries;
    private int totalPages;

    public int getListTypeId() {
        return listTypeId;
    }

    public List<String> getSubcategories() {
        return subcategories;
    }

    public List<ProgressionEntryDefinition> getEntries() {
        return entries;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalEntries() {
        return totalEntries;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
