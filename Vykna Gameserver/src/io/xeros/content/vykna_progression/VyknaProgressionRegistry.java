package io.xeros.content.vykna_progression;

import io.xeros.content.vykna_progression.categories.ListTypeCombat;
import io.xeros.content.vykna_progression.categories.ListTypeSkilling;
import io.xeros.content.vykna_progression.categories.ListTypeTasks;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class VyknaProgressionRegistry {
    private static final Map<Integer, ProgressionListDefinition> BY_LIST_TYPE;

    static {
        Map<Integer, ProgressionListDefinition> map = new HashMap<>();
        register(map, ListTypeTasks.getDefinition());
        register(map, ListTypeSkilling.getDefinition());
        register(map, ListTypeCombat.getDefinition());
        BY_LIST_TYPE = Collections.unmodifiableMap(map);
    }

    private VyknaProgressionRegistry() {
    }

    private static void register(Map<Integer, ProgressionListDefinition> map, ProgressionListDefinition def) {
        if (map.put(def.getId(), def) != null) {
            throw new IllegalStateException("Duplicate list type id: " + def.getId());
        }
    }

    public static ProgressionListDefinition getByListTypeId(int listTypeId) {
        return BY_LIST_TYPE.get(listTypeId);
    }

    public static Map<Integer, ProgressionListDefinition> getAll() {
        return BY_LIST_TYPE;
    }
}
