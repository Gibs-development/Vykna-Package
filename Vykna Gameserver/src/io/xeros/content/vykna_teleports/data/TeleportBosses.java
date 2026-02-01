package io.xeros.content.vykna_teleports.data;

import io.xeros.content.vykna_teleports.model.TeleportCategory;
import io.xeros.content.vykna_teleports.model.TeleportDefinition;
import io.xeros.content.vykna_teleports.model.TeleportDestination;
import io.xeros.content.vykna_teleports.model.TeleportRequirement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Bosses category teleport definitions.
 */
public final class TeleportBosses {

    private static final List<TeleportDefinition> LIST;

    static {
        List<TeleportDefinition> defs = new ArrayList<>();

        defs.add(new TeleportDefinition(
                1, TeleportCategory.BOSSES,
                "Dragith Nurn",
                "A giant subterranean creature lurking beneath Falador Park.",
                TeleportRequirement.combatLevel(50),
                null,
                1121, // npcId
                230,
                200,
                true,
                3,
                new TeleportDestination(1752, 5237, 0)
        ));

        defs.add(new TeleportDefinition(
                5, TeleportCategory.BOSSES,
                "Barrows",
                "Defeat the Barrows brothers to earn ancient armour.",
                TeleportRequirement.combatLevel(60),
                "Priest in Peril", // placeholder quest requirement
                1675, // npcId (Ahrim etc - preview placeholder)
                98,
                100,
                true,
                4,
                new TeleportDestination(3565, 3315, 0)
        ));

        LIST = Collections.unmodifiableList(defs);
    }

    private TeleportBosses() {}

    public static List<TeleportDefinition> list() {
        return LIST;
    }
}
