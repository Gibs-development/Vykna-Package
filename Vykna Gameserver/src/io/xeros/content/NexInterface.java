package io.xeros.content;

import io.xeros.content.bosses.Nex;
import io.xeros.content.bosses.nightmare.Nightmare;
import io.xeros.content.bosses.nightmare.totem.Totem;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

import java.util.List;

public class NexInterface {

    public static final int NEX_HEALTH_INTERFACE_ID = 57302;
    public static final int NEX_HEALTH_STATUS = 1360;
    public static final int NEX_HEALTH_AMOUNT = 1361;
    public static final int NEX_MAX_HEALTH_AMOUNT = 1362;

    public static void update(Player player) {
        NPC nex = NPCHandler.getNpc(11278);

        if (!Boundary.isIn(player, Boundary.NEX_BOSS_ROOM)) {
            return;
        }

        if (Nex.isAlive()) {
            player.getPA().walkableInterface(-1);
        } else {
            player.getPA().walkableInterface(NEX_HEALTH_INTERFACE_ID);
            player.getPA().sendConfig(NEX_HEALTH_AMOUNT, nex.getHealth().getCurrentHealth());
            player.getPA().sendConfig(NEX_MAX_HEALTH_AMOUNT, nex.getHealth().getMaximumHealth());
        }
    }

}
