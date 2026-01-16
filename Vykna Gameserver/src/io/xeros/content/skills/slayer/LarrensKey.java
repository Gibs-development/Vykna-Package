package io.xeros.content.skills.slayer;

import io.xeros.Server;
import io.xeros.content.Announcement;
import io.xeros.content.LarranChestNew;
import io.xeros.content.event.eventcalendar.EventChallenge;
import io.xeros.content.hespori.Hespori;
import io.xeros.content.item.lootable.impl.LarransChest;
import io.xeros.model.Items;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;

public class LarrensKey {

    private static final int LARGE_CHEST_OBJECT = 34_832;
    private static final int SMALL_CHEST_OBJECT = 34_831;

//if (amount >=  0 && amount <= 0) {
    public static void roll(Player player, NPC npc) {
        int rewardAmount = 1;
        if (Hespori.activeKeldaSeed) {
            rewardAmount = 2;
        }
         if (Misc.hasOneOutOf(100) && npc.getDefinition().getCombatLevel() >= 10 && npc.getDefinition().getCombatLevel() <= 50) {
            player.getEventCalendar().progress(EventChallenge.OBTAIN_X_LARRENS_KEYS, 1);
            Server.itemHandler.createGroundItem(player, Items.LARRANS_KEY, npc.getX(), npc.getY(), npc.getHeight(), rewardAmount);
            player.sendMessage("@pur@You notice an" + ItemDef.forId(Items.LARRANS_KEY).getName() + " on the ground.");
            Announcement.announce("@cr21@ @pur@" + player.getLoginName() + " received a drop: Larren's key from wilderness slayer.");
            return;
        }

        if (Misc.hasOneOutOf(60) && npc.getDefinition().getCombatLevel() >= 50 && npc.getDefinition().getCombatLevel() <= 85) {
            player.getEventCalendar().progress(EventChallenge.OBTAIN_X_LARRENS_KEYS, 1);
            Server.itemHandler.createGroundItem(player, Items.LARRANS_KEY, npc.getX(), npc.getY(), npc.getHeight(), rewardAmount);
            player.sendMessage("@pur@You notice an" + ItemDef.forId(Items.LARRANS_KEY).getName() + " on the ground.");
            Announcement.announce("@cr21@ @pur@" + player.getLoginName() + " received a drop: Larren's key from wilderness slayer.");
            return;
        }

        if (Misc.hasOneOutOf(25) && npc.getDefinition().getCombatLevel() >= 85 && npc.getDefinition().getCombatLevel() <= 800) {
            player.getEventCalendar().progress(EventChallenge.OBTAIN_X_LARRENS_KEYS, 1);
            Server.itemHandler.createGroundItem(player, Items.LARRANS_KEY, npc.getX(), npc.getY(), npc.getHeight(), rewardAmount);
            player.sendMessage("@pur@You notice an" + ItemDef.forId(Items.LARRANS_KEY).getName() + " on the ground.");
            Announcement.announce("@cr21@ @pur@" + player.getLoginName() + " received a drop: Larren's key from wilderness slayer.");
            return;
        }
    }

    public static boolean clickObject(Player player, WorldObject object) {
        if (object.getId() == LARGE_CHEST_OBJECT) {
           // new LarransChest().roll(player);
            LarranChestNew.searchChest(player);
            return true;
        } else if (object.getId() == SMALL_CHEST_OBJECT) {
            player.sendMessage("Larran's small chest wasn't added, if you feel it should be suggest it on ::discord!");
            return true;
        }

        return false;
    }
}
