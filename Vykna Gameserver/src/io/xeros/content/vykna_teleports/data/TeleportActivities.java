package io.xeros.content.vykna_teleports.data;

import io.xeros.content.vykna_teleports.model.TeleportDefinition;

import java.util.Collections;
import java.util.List;

/**
 * Activities category teleport definitions.
 * Empty for now - add your skilling/minigames here.
 */
public final class TeleportActivities {

    private static final List<TeleportDefinition> LIST = Collections.emptyList();

    private TeleportActivities() {}

    public static List<TeleportDefinition> list() {
        return LIST;
    }
}
