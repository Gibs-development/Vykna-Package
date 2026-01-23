package com.client.vykna_progression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public final class VyknaProgressionDefinitions {
    private static final CombatProgressionDefinitions COMBAT = new CombatProgressionDefinitions();
    private static final SkillingProgressionDefinitions SKILLING = new SkillingProgressionDefinitions();
    private static final TaskProgressionDefinitions TASKS = new TaskProgressionDefinitions();
    private static final List<ProgressionListType> INCLUDED_TYPES = Arrays.asList(
            ProgressionListType.TASKS,
            ProgressionListType.SKILLING,
            ProgressionListType.COMBAT
    );

    private static List<ProgressionListTypePayload> listTypes = new ArrayList<>();
    private static ProgressionSummaryPayload summaryPayload;

    private VyknaProgressionDefinitions() {
    }

    public static void setListTypes(List<ProgressionListTypePayload> payloads) {
        listTypes = payloads;
    }

    public static List<ProgressionListTypePayload> getListTypes() {
        return listTypes;
    }

    public static void setSummaryPayload(ProgressionSummaryPayload payload) {
        summaryPayload = payload;
    }

    public static ProgressionSummaryPayload getSummaryPayload() {
        return summaryPayload;
    }

    public static void applyListPayload(ProgressionListPayload payload) {
        ProgressionListType type = ProgressionListType.fromId(payload.getListTypeId());
        boolean replace = payload.getPageIndex() == 0;
        switch (type) {
            case COMBAT:
                COMBAT.update(payload, replace);
                break;
            case SKILLING:
                SKILLING.update(payload, replace);
                break;
            case TASKS:
            default:
                TASKS.update(payload, replace);
                break;
        }
    }

    public static List<String> getSubcategories(ProgressionListType type) {
        switch (type) {
            case COMBAT:
                return COMBAT.getSubcategories();
            case SKILLING:
                return SKILLING.getSubcategories();
            case TASKS:
            default:
                return TASKS.getSubcategories();
        }
    }

    public static List<ProgressionEntryDefinition> getEntries(ProgressionListType type) {
        switch (type) {
            case COMBAT:
                return COMBAT.getEntries();
            case SKILLING:
                return SKILLING.getEntries();
            case TASKS:
            default:
                return TASKS.getEntries();
        }
    }

    public static ProgressionEntryDefinition getEntryById(int listTypeId, int entryId) {
        ProgressionListType type = ProgressionListType.fromId(listTypeId);
        for (ProgressionEntryDefinition entry : getEntries(type)) {
            if (entry.getEntryId() == entryId) {
                return entry;
            }
        }
        return null;
    }

    public static CompletionStats getOverallStats() {
        List<ProgressionEntryDefinition> entries = new ArrayList<>();
        for (ProgressionListType type : INCLUDED_TYPES) {
            entries.addAll(getEntries(type));
        }
        return computeStats(entries);
    }

    public static CompletionStats getStatsForType(ProgressionListType type) {
        return computeStats(getEntries(type));
    }

    public static CompletionStats getStatsForSubcategory(ProgressionListType type, String subcategory) {
        String normalized = normalizeFilter(subcategory);
        List<ProgressionEntryDefinition> filtered = new ArrayList<>();
        for (ProgressionEntryDefinition entry : getEntries(type)) {
            if (normalized.equalsIgnoreCase(normalizeFilter(entry.getSubcategory()))) {
                filtered.add(entry);
            }
        }
        return computeStats(filtered);
    }

    public static List<ProgressionEntryDefinition> getClosestIncomplete(int count) {
        List<ProgressionEntryDefinition> candidates = new ArrayList<>();
        for (ProgressionListType type : INCLUDED_TYPES) {
            for (ProgressionEntryDefinition entry : getEntries(type)) {
                if (entry.isCompleted() || entry.getRequirementTarget() <= 0) {
                    continue;
                }
                candidates.add(entry);
            }
        }
        candidates.sort(Comparator.comparingDouble(VyknaProgressionDefinitions::progressRatio).reversed());
        return candidates.subList(0, Math.min(count, candidates.size()));
    }

    private static CompletionStats computeStats(List<ProgressionEntryDefinition> entries) {
        int completed = 0;
        for (ProgressionEntryDefinition entry : entries) {
            if (entry.isCompleted()) {
                completed++;
            }
        }
        int total = entries.size();
        return new CompletionStats(completed, total);
    }

    private static double progressRatio(ProgressionEntryDefinition entry) {
        int target = entry.getRequirementTarget();
        if (target <= 0) {
            return 0.0;
        }
        int current = Math.max(0, Math.min(entry.getProgressCurrent(), target));
        return current / (double) target;
    }

    private static String normalizeFilter(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    public static final class CompletionStats {
        private final int completed;
        private final int total;
        private final double ratio;

        private CompletionStats(int completed, int total) {
            this.completed = completed;
            this.total = total;
            this.ratio = total <= 0 ? 0.0 : Math.min(1.0, completed / (double) total);
        }

        public int getCompleted() {
            return completed;
        }

        public int getTotal() {
            return total;
        }

        public double getRatio() {
            return ratio;
        }
    }
}
