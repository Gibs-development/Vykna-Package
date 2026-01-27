package io.xeros.content.vykna_teleports.data;

import io.xeros.content.vykna_teleports.model.TeleportCategory;
import io.xeros.content.vykna_teleports.model.TeleportDefinition;
import io.xeros.content.vykna_teleports.model.TeleportDestination;
import io.xeros.content.vykna_teleports.model.TeleportRequirement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Monsters category teleport definitions.
 * Keep these grouped so editing/expanding is painless.
 */
public final class TeleportMonsters {

    private static final List<TeleportDefinition> LIST;

    static {
        List<TeleportDefinition> defs = new ArrayList<>();

        // NOTE: destination coords are placeholders - set to your actual teleport locations.
        defs.add(new TeleportDefinition(
                1, TeleportCategory.MONSTERS,
                "Goblins",
                "A small green nuisance found across the world.",
                TeleportRequirement.combatLevel(2),
                null,
                100, // npcId (client preview)
                2,
                50,
                false,
                0, // headIconIndex (RowHeads tile index)
                new TeleportDestination(3242, 3242, 0)
        ));

        defs.add(new TeleportDefinition(
                2, TeleportCategory.MONSTERS,
                "Hill Giants",
                "Large humanoids that hit harder than they look.",
                TeleportRequirement.combatLevel(20),
                null,
                117, // npcId
                28,
                35,
                true,
                1,
                new TeleportDestination(3116, 9830, 0)
        ));

        defs.add(new TeleportDefinition(
                3, TeleportCategory.MONSTERS,
                "Green Dragons",
                "Deadly dragons often hunted for their hides.",
                TeleportRequirement.combatLevel(40),
                null,
                260, // npcId
                79,
                75,
                true,
                2,
                new TeleportDestination(2980, 3614, 0)
        ));

        LIST = Collections.unmodifiableList(defs);
    }

    private TeleportMonsters() {}

    public static List<TeleportDefinition> list() {
        return LIST;
    }
}
