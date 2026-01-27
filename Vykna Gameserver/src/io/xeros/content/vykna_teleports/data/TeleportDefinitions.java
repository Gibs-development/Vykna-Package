package io.xeros.content.vykna_teleports.data;

import io.xeros.content.vykna_teleports.model.TeleportCategory;
import io.xeros.content.vykna_teleports.model.TeleportDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Aggregates teleports across category-specific classes.
 *
 * Keeps interface code stable while allowing you to maintain definitions cleanly.
 */
public final class TeleportDefinitions {

    private static final List<TeleportDefinition> ALL = buildAll();

    private TeleportDefinitions() {}

    private static List<TeleportDefinition> buildAll() {
        List<TeleportDefinition> defs = new ArrayList<>();
        defs.addAll(TeleportMonsters.list());
        defs.addAll(TeleportBosses.list());
        defs.addAll(TeleportActivities.list());
        defs.addAll(TeleportQuests.list());
        return Collections.unmodifiableList(defs);
    }

    public static List<TeleportDefinition> all() {
        return ALL;
    }

    public static List<TeleportDefinition> byCategory(TeleportCategory category) {
        return ALL.stream().filter(d -> d.getCategory() == category).collect(Collectors.toList());
    }

    public static TeleportDefinition byId(int id) {
        for (TeleportDefinition def : ALL) {
            if (def.getId() == id) return def;
        }
        return null;
    }
}
