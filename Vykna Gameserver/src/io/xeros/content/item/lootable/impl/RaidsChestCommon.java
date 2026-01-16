package io.xeros.content.item.lootable.impl;

import java.util.List;
import java.util.Map;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.content.minigames.raids.Raids;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;

public class RaidsChestCommon implements Lootable {

    private static final int ANIMATION = 881;

    private static final int KEY = Raids.COMMON_KEY;
    public final static double RARE_KEY_CHANCE = 42;

    public final static double KRONOS_SEED_CHANCE = 220;
    private static final int SEED = 22885;

    private static final int TWISTED_HORNS = 24466;
    public final static double TWISTED_HORNS_CHANCE = 150;


    private static GameItem randomChestRewards() {
        List<GameItem> itemList = RaidsChestItems.getItems().get(LootRarity.COMMON);
        return Misc.getRandomItem(itemList);
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return RaidsChestItems.getItems();
    }

    @Override
    public void roll(Player c) {

        if (Misc.hasOneOutOf(TWISTED_HORNS_CHANCE)) {
            c.getItems().addItem(TWISTED_HORNS, 1);
            PlayerHandler.executeGlobalMessage("@pur@"+ Misc.capitalize(c.getLoginName()) + " has received some @red@twisted horns @pur@ from the chambers of xeric.");
            Discord.writeDropsSyncMessage(""+ c.getLoginName() + " has received: Twisted Horns from the Chambers of Xeric.");
        }

        if (Misc.hasOneOutOf(KRONOS_SEED_CHANCE)) {
            c.getItems().addItem(SEED, 1);
            PlayerHandler.executeGlobalMessage("@pur@"+ Misc.capitalize(c.getLoginName()) + " has received an @red@kronos seed @pur@ from the chambers of xeric.");
            Discord.writeDropsSyncMessage(""+ c.getLoginName() + " has received: Kronos Seed from the Chambers of Xeric.");
        }

        if (Misc.hasOneOutOf(RARE_KEY_CHANCE)) {
            c.getItems().addItem(Raids.RARE_KEY, 1);
            PlayerHandler.executeGlobalMessage("@pur@"+ Misc.capitalize(c.getLoginName()) + " has received an @red@rare key @pur@ from the chambers of xeric.");
            Discord.writeDropsSyncMessage(""+ c.getLoginName() + " has received: Rare Key from the Chambers of Xeric.");
            c.rarekeysCollected+=1;
        }


        if (c.getItems().playerHasItem(KEY)) {
            c.getItems().deleteItem(KEY, 1);
            c.startAnimation(ANIMATION);
            GameItem reward =  randomChestRewards();
            GameItem reward2 = randomChestRewards();
            GameItem reward3 = randomChestRewards();
            c.getItems().addItem(reward.getId(), reward.getAmount() * 1); //potentially gives the loot 3 times.
            c.getItems().addItem(reward2.getId(), reward2.getAmount() * 1); //potentially gives the loot 3 times.
            c.getItems().addItem(reward3.getId(), reward3.getAmount()* 1); //potentially gives the loot 3 times.
            c.chestsOpen+=1;
            c.sendMessage("@blu@You received a common item out of the storage unit.");
        } else {
            c.sendMessage("@blu@The chest is locked, it won't budge!");
        }
    }
}
