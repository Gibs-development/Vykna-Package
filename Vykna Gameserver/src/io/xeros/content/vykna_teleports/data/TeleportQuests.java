package io.xeros.content.vykna_teleports.data;

import io.xeros.content.vykna_teleports.model.TeleportDefinition;

import java.util.Collections;
import java.util.List;

/**
 * Quests category teleport definitions.
 * Empty for now - add quest-related locations here.
 */
public final class TeleportQuests {

    private static final List<TeleportDefinition> LIST = Collections.emptyList();

    private TeleportQuests() {}

    public static List<TeleportDefinition> list() {
        return LIST;
    }
}
