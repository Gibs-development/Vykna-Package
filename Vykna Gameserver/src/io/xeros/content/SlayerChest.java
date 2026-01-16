package io.xeros.content;


import io.xeros.Server;
import io.xeros.content.skills.hunter.impling.ItemRarity;
import io.xeros.model.Items;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 
 * @author C.T
 *
 */

/*
 * Handling for the Slayer Chest
 */

public class SlayerChest {

	/*
	 * Item Id's & Animations
	 */
	private static final int MYSTERY_BOX = 6199;
	private static final int KEY1 = 13248;// TIER 1 SLAYER KEY
	private static final int KEY2 = 21055;// TIER 2 SLAYER KEY
	private static final int KEY3 = 21054;// TIER 3 SLAYER KEY
	private static final int KEY4 = 21053;// TIER 4 SLAYER KEY

	public final static double MYSTERY_BOX_CHANCE = 65;//M BOX CHANCE

	public final static double SLAYER_SCROLL_CHANCE = 15;//SLAYER SCROLL CHANCE

	private static final int ANIMATION = 881;

	private static final Map<ItemRarity, List<GameItem>> items = new HashMap<>();

	static {
		
		/*
		 * Tier 1
		 */
        items.put(ItemRarity.COMMON, Arrays.asList(
				new GameItem(1618, Misc.random(20, 50)),//uncut diamond
				new GameItem(1620, Misc.random(20, 50)),//uncut ruby
				new GameItem(454, Misc.random(50, 150)),//coal
    			new GameItem(445, Misc.random(50, 150)),//gold
				new GameItem(11237, Misc.random(10, 50)),//dragon arrow tips
                new GameItem(441, Misc.random(50, 85)),//iron ore
        		new GameItem(1164, Misc.random(1)),//rune fullhelm
        		new GameItem(1128, Misc.random(1)),//rune platebody
        		new GameItem(1080, Misc.random(1)),//rune platelegs
        		new GameItem(360, Misc.random(30, 40)),//tuna
        		new GameItem(378, Misc.random(40, 60)),//lobster
        		new GameItem(372, Misc.random(20, 50)),//swordfish
        		new GameItem(384, Misc.random(15, 25)),//shark
        		new GameItem(396, Misc.random(30, 40)),// sea turtle
        		new GameItem(12934, Misc.random(10, 50))));//zulrah scale
        
        /*
         * Tier 2
         */
        items.put(ItemRarity.UNCOMMON, Arrays.asList(
				new GameItem(1618, Misc.random(125, 175)),//uncut diamond
				new GameItem(1620, Misc.random(125, 175)),//uncut ruby
				new GameItem(454, Misc.random(400, 600)),//coal
    			new GameItem(445, Misc.random(200, 250)),//gold
				new GameItem(11237, Misc.random(100, 300)),//dragon arrow tips
                new GameItem(441, Misc.random(350, 500)),//iron ore
        		new GameItem(1164, Misc.random(3, 5)),//rune fullhelm
        		new GameItem(1128, Misc.random(2, 3)),//rune platebody
        		new GameItem(1080, Misc.random(2, 3)),//rune platelegs
        		new GameItem(360, Misc.random(100, 350)),//tuna
        		new GameItem(378, Misc.random(100, 300)),//lobster
        		new GameItem(372, Misc.random(100, 300)),//swordfish
        		new GameItem(7945, Misc.random(100, 300)),//monkfish
        		new GameItem(384, Misc.random(200, 400)),//shark
        		new GameItem(396, Misc.random(100, 250)),// sea turtle
        		new GameItem(390, Misc.random(80, 160)),//manta
        		new GameItem(452, Misc.random(30, 55)),// runite ore
        		new GameItem(2354, Misc.random(300, 500)),//steel bar
        		new GameItem(1514, Misc.random(150, 300)),// magic logs
        		new GameItem(11232, Misc.random(110, 170)),//dragon dart tip
        		new GameItem(5295, Misc.random(15, 25)),//rannar seed
        		new GameItem(5300, Misc.random(15, 25)),//snap seed
        		new GameItem(5304, Misc.random(15, 25)),//torstol seed
        		new GameItem(7937, Misc.random(600, 900)),//pure ess
        		new GameItem(12934, Misc.random(150, 250))));//zulrah scale
        
        /*
         * Tier 3
         */
        items.put(ItemRarity.RARE, Arrays.asList(
				new GameItem(1618, Misc.random(135, 185)),//uncut diamond
				new GameItem(1620, Misc.random(135, 185)),//uncut ruby
				new GameItem(454, Misc.random(400, 600)),//coal
    			new GameItem(445, Misc.random(200, 250)),//gold
				new GameItem(11237, Misc.random(100, 300)),//dragon arrow tips
                new GameItem(441, Misc.random(450, 500)),//iron ore
        		new GameItem(1164, Misc.random(3, 6)),//rune fullhelm
        		new GameItem(1128, Misc.random(2, 4)),//rune platebody
        		new GameItem(1080, Misc.random(2, 4)),//rune platelegs
        		new GameItem(360, Misc.random(100, 350)),//tuna
        		new GameItem(378, Misc.random(100, 300)),//lobster
        		new GameItem(372, Misc.random(100, 300)),//swordfish
        		new GameItem(7945, Misc.random(100, 300)),//monkfish
        		new GameItem(384, Misc.random(300, 500)),//shark
        		new GameItem(396, Misc.random(100, 250)),// sea turtle
        		new GameItem(390, Misc.random(80, 160)),//manta
        		new GameItem(452, Misc.random(40, 60)),// runite ore
        		new GameItem(2354, Misc.random(300, 500)),//steel bar
        		new GameItem(1514, Misc.random(200, 400)),// magic logs
        		new GameItem(11232, Misc.random(150, 200)),//dragon dart tip
        		new GameItem(5295, Misc.random(15, 25)),//rannar seed
        		new GameItem(5300, Misc.random(15, 25)),//snap seed
        		new GameItem(5304, Misc.random(15, 25)),//torstol seed
        		new GameItem(7937, Misc.random(600, 900)),//pure ess
        		new GameItem(12934, Misc.random(200, 600))));//zulrah scale
		
        /*
         * Tier 4
         */
		items.put(ItemRarity.VERY_RARE, Arrays.asList(
				new GameItem(1618, Misc.random(135, 185)),//uncut diamond
				new GameItem(1620, Misc.random(135, 185)),//uncut ruby
				new GameItem(454, Misc.random(400, 600)),//coal
    			new GameItem(445, Misc.random(200, 250)),//gold
				new GameItem(11237, Misc.random(100, 300)),//dragon arrow tips
                new GameItem(441, Misc.random(450, 500)),//iron ore
        		new GameItem(1164, Misc.random(4, 7)),//rune fullhelm
        		new GameItem(1128, Misc.random(3, 5)),//rune platebody
        		new GameItem(1080, Misc.random(3, 5)),//rune platelegs
        		new GameItem(360, Misc.random(100, 350)),//tuna
        		new GameItem(378, Misc.random(100, 300)),//lobster
        		new GameItem(372, Misc.random(100, 300)),//swordfish
        		new GameItem(7945, Misc.random(100, 300)),//monkfish
        		new GameItem(384, Misc.random(350, 600)),//shark
        		new GameItem(396, Misc.random(100, 250)),// sea turtle
        		new GameItem(390, Misc.random(80, 160)),//manta
        		new GameItem(452, Misc.random(50, 70)),// runite ore
        		new GameItem(2354, Misc.random(300, 500)),//steel bar
        		new GameItem(1514, Misc.random(250, 450)),// magic logs
        		new GameItem(11232, Misc.random(170, 250)),//dragon dart tip
        		new GameItem(5295, Misc.random(15, 25)),//rannar seed
        		new GameItem(5300, Misc.random(15, 25)),//snap seed
        		new GameItem(5304, Misc.random(15, 25)),//torstol seed
        		new GameItem(7937, Misc.random(1000, 2000)),//pure ess
        		new GameItem(12934, Misc.random(200, 600))));//zulrah scale
		
        /*
         * Tier 5
         */
		items.put(ItemRarity.ULTRA_RARE, Arrays.asList(
				new GameItem(5304, Misc.random(80, 120)),//torstol seed
				new GameItem(7937, Misc.random(1000, 5000)),//pure ess               // common
				new GameItem(12934, Misc.random(600, 750)),//zulrah scale

				new GameItem(5304, Misc.random(80, 120)),//torstol seed
				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
				new GameItem(12934, Misc.random(600, 750)),//zulrah scale

				new GameItem(5304, Misc.random(80, 120)),//torstol seed
				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                  // common
				new GameItem(12934, Misc.random(600, 750)),//zulrah scale

				new GameItem(5304, Misc.random(80, 120)),//torstol seed
				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
				new GameItem(12934, Misc.random(600, 750)),//zulrah scale

				new GameItem(5304, Misc.random(80, 120)),//torstol seed
				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
				new GameItem(12934, Misc.random(600, 750)),//zulrah scale


				new GameItem(5304, Misc.random(80, 120)),//torstol seed
				new GameItem(7937, Misc.random(1000, 5000)),//pure ess               // common
				new GameItem(12934, Misc.random(600, 750)),//zulrah scale

				new GameItem(5304, Misc.random(80, 120)),//torstol seed
				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
				new GameItem(12934, Misc.random(600, 750)),//zulrah scale

				new GameItem(5304, Misc.random(80, 120)),//torstol seed
				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                  // common
				new GameItem(12934, Misc.random(600, 750)),//zulrah scale

				new GameItem(5304, Misc.random(80, 120)),//torstol seed
				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
				new GameItem(12934, Misc.random(600, 750)),//zulrah scale

				new GameItem(5304, Misc.random(80, 120)),//torstol seed
				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
				new GameItem(12934, Misc.random(600, 750)),//zulrah scale


				new GameItem(5304, Misc.random(80, 120)),//torstol seed
				new GameItem(7937, Misc.random(1000, 5000)),//pure ess               // common
				new GameItem(12934, Misc.random(600, 750)),//zulrah scale

				new GameItem(5304, Misc.random(80, 120)),//torstol seed
				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
				new GameItem(12934, Misc.random(600, 750)),//zulrah scale

				new GameItem(5304, Misc.random(80, 120)),//torstol seed
				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                  // common
				new GameItem(12934, Misc.random(600, 750)),//zulrah scale

				new GameItem(5304, Misc.random(80, 120)),//torstol seed
				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
				new GameItem(12934, Misc.random(600, 750)),//zulrah scale

				new GameItem(5304, Misc.random(80, 120)),//torstol seed
				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
				new GameItem(12934, Misc.random(600, 750)),//zulrah scale


				new GameItem(5304, Misc.random(80, 120)),//torstol seed
				new GameItem(7937, Misc.random(1000, 5000)),//pure ess               // common
				new GameItem(12934, Misc.random(600, 750)),//zulrah scale

				new GameItem(5304, Misc.random(80, 120)),//torstol seed
				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
				new GameItem(12934, Misc.random(600, 750)),//zulrah scale

				new GameItem(5304, Misc.random(80, 120)),//torstol seed
				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                  // common
				new GameItem(12934, Misc.random(600, 750)),//zulrah scale

				new GameItem(5304, Misc.random(80, 120)),//torstol seed
				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
				new GameItem(12934, Misc.random(600, 750)),//zulrah scale

				new GameItem(5304, Misc.random(80, 120)),//torstol seed
				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
				new GameItem(12934, Misc.random(600, 750)),//zulrah scale


				new GameItem(19547, 1),//zenyte
				new GameItem(19553, 1),//torture
				new GameItem(19550, 1),//suffering
				new GameItem(4151, 1),//whip
				new GameItem(12927, 1),//serp vis
				new GameItem(12932, 1),//magic fang
				new GameItem(13227, 1),//boot crystals
				new GameItem(13229, 1),//peg crystal
				new GameItem(13231, 1),//prim crystal
				new GameItem(12004, 1)));//tent
	}

	/*
	 * Handles getting a random item from each list depending on the Tier.
	 */
	private static GameItem randomTier1ChestRewards(int chance) {
		int random = Misc.random(1, 100);
		List<GameItem> itemList = random <= 99 ? items.get(ItemRarity.COMMON) : items.get(ItemRarity.ULTRA_RARE);
		return Misc.getRandomItem(itemList);
	}
	
	private static GameItem randomTier2ChestRewards(int chance) {
		int random = Misc.random(1, 100);
		List<GameItem> itemList = random <= 98 ? items.get(ItemRarity.UNCOMMON) : items.get(ItemRarity.ULTRA_RARE);
		return Misc.getRandomItem(itemList);
	}
	
	private static GameItem randomTier3ChestRewards(int chance) {
		int random = Misc.random(1, 100);
		List<GameItem> itemList = random <= 97 ? items.get(ItemRarity.RARE) : items.get(ItemRarity.ULTRA_RARE);
		return Misc.getRandomItem(itemList);
	}

	private static GameItem randomTier4ChestRewards(int chance) {
		int random = Misc.random(1, 100);
		List<GameItem> itemList = random <= 96 ? items.get(ItemRarity.VERY_RARE) : items.get(ItemRarity.ULTRA_RARE);
		return Misc.getRandomItem(itemList);
	}
	
	public static void searchChest(Player c) {
		//Tier 1
		if (c.getItems().playerHasItem(KEY1)) {
			c.getItems().deleteItem(KEY1, 1);
			c.startAnimation(ANIMATION);
			c.getItems().addItemUnderAnyCircumstance(995, Misc.random(25_000, 150_000));
			GameItem reward = Boundary.isIn(c, Boundary.DONATOR_ZONE) && c.getRights().isOrInherits(Right.DIAMOND_CLUB) ? randomTier1ChestRewards(2) : randomTier1ChestRewards(9);
			if (!c.getItems().addItem(reward.getId(), reward.getAmount())) {
				Server.itemHandler.createGroundItem(c, reward.getId(), c.getX(), c.getY(), c.getHeight(), reward.getAmount());
			}
			c.sendMessage( " You receive a " + ItemAssistant.getItemName(reward.getId()) + " from the Slayer Chest!");
			c.getPA().addSkillXP((5), Player.playerSlayer, true);
		}
		//Tier 2
		else if (c.getItems().playerHasItem(KEY2)) {
			c.getItems().deleteItem(KEY2, 1);
			c.startAnimation(ANIMATION);
			c.getItems().addItemUnderAnyCircumstance(995, Misc.random(50_000, 300_000));
			GameItem reward = Boundary.isIn(c, Boundary.DONATOR_ZONE) && c.getRights().isOrInherits(Right.DIAMOND_CLUB) ? randomTier2ChestRewards(2) : randomTier2ChestRewards(9);
			if (!c.getItems().addItem(reward.getId(), reward.getAmount())) {
				Server.itemHandler.createGroundItem(c, reward.getId(), c.getX(), c.getY(), c.getHeight(), reward.getAmount());
			}
			c.sendMessage( " You receive a " + ItemAssistant.getItemName(reward.getId()) + " from the Slayer Chest!");
			c.getPA().addSkillXP((10), Player.playerSlayer, true);
		}
		//Tier 3
		else if (c.getItems().playerHasItem(KEY3)) {
			c.getItems().deleteItem(KEY3, 1);
			c.startAnimation(ANIMATION);
			c.getItems().addItemUnderAnyCircumstance(995, Misc.random(150_000, 400_000));
			GameItem reward = Boundary.isIn(c, Boundary.DONATOR_ZONE) && c.getRights().isOrInherits(Right.DIAMOND_CLUB) ? randomTier3ChestRewards(2) : randomTier3ChestRewards(9);
			if (!c.getItems().addItem(reward.getId(), reward.getAmount())) {
				Server.itemHandler.createGroundItem(c, reward.getId(), c.getX(), c.getY(), c.getHeight(), reward.getAmount());
			}
			c.sendMessage( " You receive a " + ItemAssistant.getItemName(reward.getId()) + " from the Slayer Chest!");
			PlayerHandler.executeGlobalMessage("@red@" + c.getLoginName() + " @pur@has received @red@" + ItemAssistant.getItemName(reward.getId()) + " @pur@from the @red@Slayer chest!");
			c.getPA().addSkillXP((15), Player.playerSlayer, true);
		} 
		//Tier 4
		else if (c.getItems().playerHasItem(KEY4)) {
			c.getItems().deleteItem(KEY4, 1);
			c.startAnimation(ANIMATION);
			c.getItems().addItemUnderAnyCircumstance(995, Misc.random(250_000, 500_000));
			GameItem reward = Boundary.isIn(c, Boundary.DONATOR_ZONE) && c.getRights().isOrInherits(Right.DIAMOND_CLUB) ? randomTier4ChestRewards(2) : randomTier4ChestRewards(9);
			if (!c.getItems().addItem(reward.getId(), reward.getAmount())) {
				Server.itemHandler.createGroundItem(c, reward.getId(), c.getX(), c.getY(), c.getHeight(), reward.getAmount());
			}
			if (Misc.hasOneOutOf(MYSTERY_BOX_CHANCE)) {
				c.getItems().addItemUnderAnyCircumstance(MYSTERY_BOX, 1);
				c.sendMessage("You got lucky and got an mystery box!");
				PlayerHandler.executeGlobalMessage("@red@" + c.getLoginName() + " @pur@has received an @red@mystery box @pur@from the @red@Slayer chest!");
			}
			if (Misc.hasOneOutOf(SLAYER_SCROLL_CHANCE)) {
				c.getItems().addItemUnderAnyCircumstance(7629, 1);
				c.sendMessage("You got lucky and receive two x2 slayer point scrolls!");
				PlayerHandler.executeGlobalMessage("@red@" + c.getLoginName() + " @pur@has received @red@x2 slayer point scroll @pur@from the @red@Slayer chest!");
			}
			c.sendMessage( " You receive a " + ItemAssistant.getItemName(reward.getId()) + " from the Slayer Chest!");
			PlayerHandler.executeGlobalMessage("@red@" + c.getLoginName() + " @pur@has received @red@" + ItemAssistant.getItemName(reward.getId()) + " @pur@from the @red@Slayer chest!");
			c.getPA().addSkillXP((30), Player.playerSlayer, true);
		}
		
		else {
			c.getDH().sendStatement("You will need a Slayer key Tier 1-4 to open this.",
					"All Slayer monsters drop Keys. Higher level monsters = higher tier keys!");
		}
	} 
}