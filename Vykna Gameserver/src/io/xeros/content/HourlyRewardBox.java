package io.xeros.content;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import io.xeros.content.item.lootable.LootRarity;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;



/*
 * @author C.T
 */

public class HourlyRewardBox {

    /**
     * A map containing a List of {@link GameItem}'s that contain items relevant to their rarity.
     */
    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    public static final int ANIMATION = 881;

    /**
     * Stores an array of items into each map with the corresponding rarity to the list
     */
    static {
        items.put(LootRarity.COMMON,
                Arrays.asList(
                        new GameItem(995, 50000 + Misc.random(75000)),
                        new GameItem(11937, 5 + Misc.random(8)),
                        new GameItem(2354, 20 + Misc.random(15)),
                        new GameItem(441, 50 + Misc.random(25)),         //common
                        new GameItem(208, 10),
                        new GameItem(5295, 20),
                        new GameItem(1516, 30),
                        new GameItem(5300, 20),
                        new GameItem(384, 100),
                        new GameItem(537, 20),
                        new GameItem(2362, 10),

                        new GameItem(995, 50000 + Misc.random(75000)),
                        new GameItem(11937, 5 + Misc.random(8)),
                        new GameItem(2354, 20 + Misc.random(15)),            //common
                        new GameItem(441, 50 + Misc.random(25)),
                        new GameItem(208, 10),
                        new GameItem(5295, 20),
                        new GameItem(1516, 30),
                        new GameItem(5300, 20),
                        new GameItem(384, 100),
                        new GameItem(537, 20),
                        new GameItem(2362, 10),




                        new GameItem(995, 50000 + Misc.random(75000)),
                        new GameItem(11937, 5 + Misc.random(8)),
                        new GameItem(2354, 20 + Misc.random(15)),
                        new GameItem(441, 50 + Misc.random(25)),
                        new GameItem(208, 10),
                        new GameItem(5295, 20),                                //common
                        new GameItem(1516, 30),
                        new GameItem(5300, 20),
                        new GameItem(384, 100),
                        new GameItem(537, 20),
                        new GameItem(2362, 10),



                        new GameItem(21880, 500 + Misc.random(150)),
                        new GameItem(454, 50 + Misc.random(25)),             //uncommon
                        new GameItem(995, 50000 + Misc.random(99000)))
        );

        items.put(LootRarity.UNCOMMON,
                Arrays.asList(
                        new GameItem(995, 100000 + Misc.random(80000)),
                        new GameItem(12696, 3),
                        new GameItem(3052, 10),
                        new GameItem(208, 10),
                        new GameItem(5295, 20),
                        new GameItem(3050, 25),                       //common
                        new GameItem(5296, 25),
                        new GameItem(5300, 20),
                        new GameItem(1514, 40),
                        new GameItem(2362, 10),
                        new GameItem(2, 100 + Misc.random(250)),
                        new GameItem(448, 50 + Misc.random(25)),
                        new GameItem(9244, 50 + Misc.random(25)),

                        new GameItem(995, 100000 + Misc.random(80000)),
                        new GameItem(12696, 3),
                        new GameItem(3052, 10),
                        new GameItem(208, 10),
                        new GameItem(5295, 20),
                        new GameItem(3050, 25),                       //common
                        new GameItem(5296, 25),
                        new GameItem(5300, 20),
                        new GameItem(1514, 40),
                        new GameItem(2362, 10),
                        new GameItem(2, 100 + Misc.random(250)),
                        new GameItem(448, 50 + Misc.random(25)),
                        new GameItem(9244, 50 + Misc.random(25)),


                        new GameItem(995, 100000 + Misc.random(80000)),
                        new GameItem(12696, 3),
                        new GameItem(3052, 10),
                        new GameItem(208, 10),
                        new GameItem(5295, 20),
                        new GameItem(3050, 25),                       //common
                        new GameItem(5296, 25),
                        new GameItem(5300, 20),
                        new GameItem(1514, 40),
                        new GameItem(2362, 10),
                        new GameItem(2, 100 + Misc.random(250)),
                        new GameItem(448, 50 + Misc.random(25)),
                        new GameItem(9244, 50 + Misc.random(25)),

                        new GameItem(995, 100000 + Misc.random(80000)),
                        new GameItem(12696, 3),
                        new GameItem(3052, 10),
                        new GameItem(208, 10),
                        new GameItem(5295, 20),
                        new GameItem(3050, 25),                       //common
                        new GameItem(5296, 25),
                        new GameItem(5300, 20),
                        new GameItem(4447, 1),
                        new GameItem(1514, 40),
                        new GameItem(2362, 10),
                        new GameItem(2, 100 + Misc.random(250)),
                        new GameItem(448, 50 + Misc.random(25)),
                        new GameItem(9244, 50 + Misc.random(25)),

                        new GameItem(995, 100000 + Misc.random(80000)),
                        new GameItem(12696, 3),
                        new GameItem(3052, 10),
                        new GameItem(208, 10),
                        new GameItem(5295, 20),
                        new GameItem(3050, 25),                       //common
                        new GameItem(5296, 25),
                        new GameItem(5300, 20),
                        new GameItem(4447, 1),
                        new GameItem(1514, 40),
                        new GameItem(2362, 10),
                        new GameItem(2, 100 + Misc.random(250)),
                        new GameItem(448, 50 + Misc.random(25)),
                        new GameItem(9244, 50 + Misc.random(25)),

                        new GameItem(2360, 10),
                        new GameItem(995, 500000),
                        new GameItem(4587, 1),
                        new GameItem(4153, 1),
                        new GameItem(5698, 1),
                        new GameItem(11941, 1),
                        new GameItem(384, 25 + Misc.random(25)),
                        new GameItem(3122, 1),
                        new GameItem(9075, 150 + Misc.random(250)),
                        new GameItem(2364, 15),
                        new GameItem(452, 15),
                        new GameItem(6730, 30),
                        new GameItem(12696, 5),
                        new GameItem(11230, 20 + Misc.random(20)),
                        new GameItem(6542, 1),
                        new GameItem(9242, 40 + Misc.random(15)),
                        new GameItem(13442, 15 + Misc.random(15)))

        );

        items.put(LootRarity.RARE,
                Arrays.asList(
                        new GameItem(2364, 15),
                        new GameItem(452, 15),  // common
                        new GameItem(6730, 45),
                        new GameItem(12696, 20),
                        new GameItem(19841, 1),

                        new GameItem(2364, 15),
                        new GameItem(452, 15),  // common
                        new GameItem(6730, 45),
                        new GameItem(12696, 20),
                        new GameItem(19841, 1),

                        new GameItem(2364, 15),
                        new GameItem(452, 15),  // common
                        new GameItem(6730, 45),
                        new GameItem(12696, 20),
                        new GameItem(19841, 1),

                        new GameItem(2364, 15),
                        new GameItem(452, 15),  // common
                        new GameItem(6730, 45),
                        new GameItem(12696, 20),
                        new GameItem(19841, 1),

                        new GameItem(6, 1),
                        new GameItem(8, 1),
                        new GameItem(10, 1),   //rare
                        new GameItem(12, 1),
                        new GameItem(995, 200000 + Misc.random(500000)),
                        new GameItem(6199, 1)
                ));
    }

    /**
     * The player object that will be triggering this event
     */
    private Player player;

    /**
     * @param player the player
     */
    public HourlyRewardBox(Player player) {
        this.player = player;
    }


    public static void searchChest(Player c) {

        c.startAnimation(ANIMATION);
        int random = Misc.random(100);
        List<GameItem> itemList = random < 55 ? items.get(LootRarity.COMMON) : random >= 55 && random <= 94 ? items.get(LootRarity.UNCOMMON) : items.get(LootRarity.RARE);
        GameItem item = Misc.getRandomItem(itemList);
        c.getItems().addItemUnderAnyCircumstance(item.getId(), item.getAmount());
        c.sendMessage("@blu@You stick your hand in the chest and pull an item out of the chest.");
        PlayerHandler.executeGlobalMessage("@red@"+c.getLoginName() + " @blu@has received: @red@"+ ItemAssistant.getItemName(item.getId()) + " @blu@from the @red@loyalty chest!");
        Discord.writeDropsSyncMessage("News: "+ c.getLoginName() + "  has received: "+ ItemAssistant.getItemName(item.getId()) + " from the loyalty chest!");
        c.loyaltyChestClaimable = false;
    }
}
