package com.client.vykna_progression;

import java.util.ArrayList;
import java.util.List;

public final class VyknaProgressionDefinitions {
    private static final CombatProgressionDefinitions COMBAT = new CombatProgressionDefinitions();
    private static final SkillingProgressionDefinitions SKILLING = new SkillingProgressionDefinitions();
    private static final TaskProgressionDefinitions TASKS = new TaskProgressionDefinitions();

    private static List<ProgressionListTypePayload> listTypes = new ArrayList<>();

    private VyknaProgressionDefinitions() {
    }

    public static void setListTypes(List<ProgressionListTypePayload> payloads) {
        listTypes = payloads;
    }

    public static List<ProgressionListTypePayload> getListTypes() {
        return listTypes;
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
}
