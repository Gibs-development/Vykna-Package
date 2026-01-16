package io.xeros.content;

import com.google.common.collect.Lists;
import io.xeros.content.skills.hunter.impling.ItemRarity;
import io.xeros.model.CombatType;
import io.xeros.model.entity.HealthStatus;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Tarn razorlor.
 *
 * @author C.T for RuneRogue
 *
 */

public class Tarn {

    private static int TARN_KEY = 9722;



    public static void rewardPlayers() {
        List<String> givenToIP = Lists.newArrayList();
        PlayerHandler.nonNullStream().filter(p -> Boundary.isIn(p, Boundary.TARN_AREA))
                .forEach(p -> {
                    if (!givenToIP.contains(p.connectedFrom)) {
                            p.sendMessage("@blu@You receive an @red@Tarn Reward Key@blu@ for defeating @red@Tarn Razorlor.");
                            p.sendMessage("@blu@use it on the chest for your rewards.");
                            p.getItems().addItemUnderAnyCircumstance(TARN_KEY, 1);
                            givenToIP.add(p.connectedFrom);
                            p.getPA().movePlayer(3186, 4632, 0);//Chest area

                        } else {
                            p.sendMessage("You can only receive 1 drop per @red@ IP ADDRESS!");
                        }
                });
       }




    /*
     * An array containing all items rewarded from the tarn chest.
     */
    private static Map<ItemRarity, List<GameItem>> items = new HashMap<>();

    static {
        items.put(ItemRarity.COMMON,
                Arrays.asList(
                        new GameItem(995, 100000),//cash 100k
                        new GameItem(11040, 1),//tarn entry key
                        new GameItem(995, 500_000 + Misc.random(1_000_000)),//gold
                        new GameItem(990, 2 + Misc.random(3)),//ckeys
                        new GameItem(11232, 150 + Misc.random(75)),//dragon dart tips
                        new GameItem(20849, 50)//dragon thrown axe
                ));

        items.put(ItemRarity.UNCOMMON, Arrays.asList(
                new GameItem(995, 200000),//cash 200k
                new GameItem(22125, 25 + Misc.random(50)),//Superior Dragon bones
                new GameItem(2528, 1),// XP lamp
                new GameItem(11212, 100 + Misc.random(75)),//dragon arrows
                new GameItem(11230, 150 + Misc.random(75)),//dragon darts
                new GameItem(20849, 50 + Misc.random(50))//dragon thrown axe

        ));

        items.put(ItemRarity.RARE, Arrays.asList(
                new GameItem(22296, 1),//staff of light
                new GameItem(19841, 1)//Master clue casket


        ));

        items.put(ItemRarity.VERY_RARE, Arrays.asList(
                new GameItem(22125, 50 + Misc.random(100)),//Superior Dragon bones
                new GameItem(2528, 1),// XP lamp
                new GameItem(11212, 100 + Misc.random(75)),//dragon arrows                 //common
                new GameItem(11230, 150 + Misc.random(75)),//dragon darts
                new GameItem(20849, 50 + Misc.random(50)),//dragon thrown axe
                new GameItem(995, 600000),//cash 100k


                new GameItem(22125, 50 + Misc.random(100)),//Superior Dragon bones
                new GameItem(2528, 1),// XP lamp
                new GameItem(11212, 100 + Misc.random(75)),//dragon arrows                 //common
                new GameItem(11230, 150 + Misc.random(75)),//dragon darts
                new GameItem(20849, 50 + Misc.random(50)),//dragon thrown axe
                new GameItem(995, 600000),//cash 100k



                new GameItem(22125, 50 + Misc.random(100)),//Superior Dragon bones
                new GameItem(2528, 1),// XP lamp
                new GameItem(11212, 100 + Misc.random(75)),//dragon arrows                 //common
                new GameItem(11230, 150 + Misc.random(75)),//dragon darts
                new GameItem(20849, 50 + Misc.random(50)),//dragon thrown axe
                new GameItem(995, 600000),//cash 100k



                new GameItem(6572, 1),//uncut onyx
                new GameItem(6731, 1),//seers ring
                new GameItem(6733, 1),//archer ring
                new GameItem(6735, 1),//warrior ring
                new GameItem(6737, 1),//berserker ring


                new GameItem(12004, 1),//kraken tent
                new GameItem(11040, 2),// 2x tarn entry key
                new GameItem(4151, 1),//whip
                new GameItem(2572, 1),//ring of wealth
                new GameItem(6199, 1),//Mystery box
                new GameItem(4081, 1)//Salve ammy
        ));
    }

    /**
     * Gets a random reward from the array & rewards it to the player
     * @param
     * @return
     */

    public static void execute(Player player) {
        int rewardRoll = Misc.random(1000);// 10 = 1%
        List<GameItem> itemList = rewardRoll <= 500 ? items.get(ItemRarity.COMMON) : // 50%
                rewardRoll > 500 && rewardRoll <= 930 ? items.get(ItemRarity.UNCOMMON) : // 44.0%
                        rewardRoll > 930 && rewardRoll <= 950 ? items.get(ItemRarity.RARE) : // 5%
                                items.get(ItemRarity.VERY_RARE); // 1%
        GameItem reward = Misc.getRandomItem(itemList);

        if (player.getItems().playerHasItem(TARN_KEY)) {
            player.getItems().deleteItem(TARN_KEY, 1);
            player.getItems().addItem(reward.getId(), reward.getAmount());
            player.getItems().addItemUnderAnyCircumstance(10476, 10 + Misc.random(10));
            player.sendMessage("You reach into the chest and pull out a @red@" + ItemAssistant.getItemName(reward.getId()));
            for (Map.Entry<ItemRarity, List<GameItem>> gift : items.entrySet())
                for (GameItem gift_item : gift.getValue())
                    if (gift_item == reward)
                        if (gift.getKey() == ItemRarity.RARE || gift.getKey() == ItemRarity.VERY_RARE)
                            PlayerHandler.executeGlobalMessage("@red@"+Misc.capitalize(player.getLoginName()) + " @blu@received a @red@rare item: "
                                    + (reward.getAmount() > 1 ? (reward.getAmount() + "x ")
                                    : ItemAssistant.getItemName(reward.getId()) + "@blu@ from the @red@Tarn Razorlor Chest."));
        } else {
            player.sendMessage("@red@You need an @pur@Tarn Reward Key @red@to open this @pur@chest!");
        }
    }












}
