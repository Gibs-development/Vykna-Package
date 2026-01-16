package io.xeros.content;

import java.util.*;
import java.util.stream.Collectors;

import com.everythingrs.marketplace.Item;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.content.item.lootable.LootableInterface;
import io.xeros.content.skills.hunter.impling.ItemRarity;
import io.xeros.model.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.GameItemVariableAmount;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;


/**
 * The Koranian chest.
 *
 * @author C.T for koranes
 *
 */


public class KoranianChest implements Lootable {

	private static final int[] KEY = {4185};
	private static final int ANIMATION = 881;

	private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

	static {
		items.put(LootRarity.COMMON, Arrays.asList(
				new GameItemVariableAmount(995, 100000, 400000), //coins
				new GameItemVariableAmount(11212, 10, 100), //dragon arrow
				new GameItemVariableAmount(3050, 10, 20), //grimy toadflax
				new GameItemVariableAmount(210, 10, 15), //grimy irit
				new GameItemVariableAmount(212, 10, 20), //grimy avantoe
				new GameItemVariableAmount(214, 10, 20), //grimy kwuarm
				new GameItemVariableAmount(216, 10, 21), //grimy candatine
				new GameItemVariableAmount(2486, 10, 23), //grimy landatyme           //commmon
				new GameItemVariableAmount(218, 10, 22), //dwarf weed
				new GameItemVariableAmount(220, 10, 10), //torstol
				new GameItemVariableAmount(454, 20, 150), //coal
				new GameItemVariableAmount(13440, 10, 30), //anglerfish
				new GameItemVariableAmount(448, 10, 45), //mith ore
				new GameItemVariableAmount(450, 10, 28), //addy ore
				new GameItemVariableAmount(19484, 10, 60), //dragon javelin
				new GameItemVariableAmount(12934, 100, 500), //zul scales
				new GameItemVariableAmount(2, 50, 100), //cannon balls
				new GameItemVariableAmount(9245, 100, 150), //onyx bolts e


				new GameItemVariableAmount(995, 100000, 400000), //coins
				new GameItemVariableAmount(11212, 10, 100), //dragon arrow
				new GameItemVariableAmount(3050, 10, 20), //grimy toadflax
				new GameItemVariableAmount(210, 10, 15), //grimy irit
				new GameItemVariableAmount(212, 10, 20), //grimy avantoe
				new GameItemVariableAmount(214, 10, 20), //grimy kwuarm
				new GameItemVariableAmount(216, 10, 21), //grimy candatine
				new GameItemVariableAmount(2486, 10, 23), //grimy landatyme           //commmon
				new GameItemVariableAmount(218, 10, 22), //dwarf weed
				new GameItemVariableAmount(220, 10, 10), //torstol
				new GameItemVariableAmount(454, 20, 150), //coal
				new GameItemVariableAmount(13440, 10, 30), //anglerfish
				new GameItemVariableAmount(448, 10, 45), //mith ore
				new GameItemVariableAmount(450, 10, 28), //addy ore
				new GameItemVariableAmount(19484, 10, 60), //dragon javelin
				new GameItemVariableAmount(12934, 100, 500), //zul scales
				new GameItemVariableAmount(2, 50, 100), //cannon balls
				new GameItemVariableAmount(9245, 100, 150), //onyx bolts e


				new GameItemVariableAmount(995, 100000, 400000), //coins
				new GameItemVariableAmount(11212, 10, 100), //dragon arrow
				new GameItemVariableAmount(3050, 10, 20), //grimy toadflax
				new GameItemVariableAmount(210, 10, 15), //grimy irit
				new GameItemVariableAmount(212, 10, 20), //grimy avantoe
				new GameItemVariableAmount(214, 10, 20), //grimy kwuarm
				new GameItemVariableAmount(216, 10, 21), //grimy candatine
				new GameItemVariableAmount(2486, 10, 23), //grimy landatyme           //commmon
				new GameItemVariableAmount(218, 10, 22), //dwarf weed
				new GameItemVariableAmount(220, 10, 10), //torstol
				new GameItemVariableAmount(454, 20, 150), //coal
				new GameItemVariableAmount(13440, 10, 30), //anglerfish
				new GameItemVariableAmount(448, 10, 45), //mith ore
				new GameItemVariableAmount(450, 10, 28), //addy ore
				new GameItemVariableAmount(19484, 10, 60), //dragon javelin
				new GameItemVariableAmount(12934, 100, 500), //zul scales
				new GameItemVariableAmount(2, 50, 100), //cannon balls
				new GameItemVariableAmount(9245, 100, 150), //onyx bolts e

				new GameItem(21046, 1),//chest rate bonus
				new GameItem(6889),//mages book
				new GameItem(2364, 150),//runite bar
				new GameItem(1514, 200),// magic logs
				new GameItem(1632, 50),//uncut dragonstone
		        new GameItem(2996, 35),//pkp tickets
				new GameItem(11230, 30)//dragon darts

		));

		items.put(LootRarity.UNCOMMON, Arrays.asList(
				new GameItemVariableAmount(11212, 50, 140), //dragon arrow
				new GameItemVariableAmount(995, 500000, 1000000), //coins
				new GameItem(1249, 1),
				new GameItem(4587, 1),
				new GameItem(9425, 30 + Misc.random(90)),
				new GameItem(3204, 1),
				new GameItem(11230, 250 + Misc.random(500)),                   //uncommon
				new GameItem(12696, 15 + Misc.random(25)),
				new GameItem(21880, 600 + Misc.random(700)),
				new GameItem(995, 50000 + Misc.random(150000)),
				new GameItem(21820, 500 + Misc.random(1100)),
				new GameItem(13442, 150 + Misc.random(250)),
				new GameItem(11937, 200 + Misc.random(250)),

				new GameItemVariableAmount(11212, 50, 140), //dragon arrow
				new GameItemVariableAmount(995, 500000, 1000000), //coins
				new GameItem(1249, 1),
				new GameItem(4587, 1),
				new GameItem(9425, 30 + Misc.random(90)),
				new GameItem(3204, 1),
				new GameItem(11230, 250 + Misc.random(500)),                   //uncommon
				new GameItem(12696, 15 + Misc.random(25)),
				new GameItem(21880, 600 + Misc.random(700)),
				new GameItem(995, 50000 + Misc.random(150000)),
				new GameItem(21820, 500 + Misc.random(1100)),
				new GameItem(13442, 150 + Misc.random(250)),
				new GameItem(11937, 200 + Misc.random(250)),

				new GameItemVariableAmount(11212, 50, 140), //dragon arrow
				new GameItemVariableAmount(995, 500000, 1000000), //coins
				new GameItem(1249, 1),
				new GameItem(4587, 1),
				new GameItem(9425, 30 + Misc.random(90)),
				new GameItem(3204, 1),
				new GameItem(11230, 250 + Misc.random(500)),                   //uncommon
				new GameItem(12696, 15 + Misc.random(25)),
				new GameItem(21880, 600 + Misc.random(700)),
				new GameItem(995, 50000 + Misc.random(150000)),
				new GameItem(21820, 500 + Misc.random(1100)),
				new GameItem(13442, 150 + Misc.random(250)),
				new GameItem(11937, 200 + Misc.random(250)),

				new GameItemVariableAmount(11212, 50, 140), //dragon arrow
				new GameItemVariableAmount(995, 500000, 1000000), //coins
				new GameItem(1249, 1),
				new GameItem(4587, 1),
				new GameItem(9425, 30 + Misc.random(90)),
				new GameItem(3204, 1),
				new GameItem(11230, 250 + Misc.random(500)),                   //uncommon
				new GameItem(12696, 15 + Misc.random(25)),
				new GameItem(21880, 600 + Misc.random(700)),
				new GameItem(995, 50000 + Misc.random(150000)),
				new GameItem(21820, 500 + Misc.random(1100)),
				new GameItem(13442, 150 + Misc.random(250)),
				new GameItem(11937, 200 + Misc.random(250)),


				new GameItemVariableAmount(2, 100, 150), //cannon balls
				new GameItemVariableAmount(9245, 130, 190), //onyx bolts e
				new GameItem(21547, 1),//small foe bone
				new GameItem(21547, 1),//small foe bone
				new GameItem(21547, 1),//small foe bone
				new GameItem(21547, 1),//small foe bone
				new GameItem(21549, 1),//medium foe bone
				new GameItem(21549, 1),//medium foe bone
				new GameItem(21549, 1),//medium foe bone
				new GameItem(21551, 1),//large foe bone
				new GameItem(21551, 1),//large foe bone
				new GameItem(21553, 1),//rare foe bone

		        new GameItem(11840),//dragon boots
		        new GameItem(2996, 38),//pkp tickets
				new GameItem(11230, 60),//dragon darts
				new GameItem(21046, 3),//chest rate bonus
				new GameItem(7629, 1),//x2 slayer scroll
				new GameItem(23804, 1),//imbuedifier
		        new GameItemVariableAmount(12934, 100, 100) //zul scales

		));

		items.put(LootRarity.RARE, Arrays.asList(
				new GameItemVariableAmount(995, 2000000, 3000000), //coins
				new GameItemVariableAmount(11212, 300, 500), //dragon arrow
				new GameItemVariableAmount(454, 20, 150), //coal
				new GameItemVariableAmount(13440, 10, 30), //anglerfish
				new GameItemVariableAmount(448, 10, 45), //mith ore
				new GameItemVariableAmount(450, 10, 28), //addy ore              //common
				new GameItemVariableAmount(19484, 10, 18), //dragon javelin
				new GameItemVariableAmount(2, 300, 600), //cannon balls
				new GameItemVariableAmount(9245, 250, 350), //onyx bolts e
		        new GameItem(2996, 50),//pkp tickets
				new GameItem(11230, 150),//dragon darts

				new GameItemVariableAmount(995, 2000000, 3000000), //coins
				new GameItemVariableAmount(11212, 300, 500), //dragon arrow
				new GameItemVariableAmount(454, 20, 150), //coal
				new GameItemVariableAmount(13440, 10, 30), //anglerfish
				new GameItemVariableAmount(448, 10, 45), //mith ore
				new GameItemVariableAmount(450, 10, 28), //addy ore              //common
				new GameItemVariableAmount(19484, 10, 18), //dragon javelin
				new GameItemVariableAmount(2, 300, 600), //cannon balls
				new GameItemVariableAmount(9245, 250, 350), //onyx bolts e
				new GameItem(2996, 50),//pkp tickets
				new GameItem(11230, 150),//dragon darts

				new GameItemVariableAmount(995, 2000000, 3000000), //coins
				new GameItemVariableAmount(11212, 300, 500), //dragon arrow
				new GameItemVariableAmount(454, 20, 150), //coal
				new GameItemVariableAmount(13440, 10, 30), //anglerfish
				new GameItemVariableAmount(448, 10, 45), //mith ore
				new GameItemVariableAmount(450, 10, 28), //addy ore              //common
				new GameItemVariableAmount(19484, 10, 18), //dragon javelin
				new GameItemVariableAmount(2, 300, 600), //cannon balls
				new GameItemVariableAmount(9245, 250, 350), //onyx bolts e
				new GameItem(2996, 50),//pkp tickets
				new GameItem(11230, 150),//dragon darts

				new GameItemVariableAmount(995, 2000000, 3000000), //coins
				new GameItemVariableAmount(11212, 300, 500), //dragon arrow
				new GameItemVariableAmount(454, 20, 150), //coal
				new GameItemVariableAmount(13440, 10, 30), //anglerfish
				new GameItemVariableAmount(448, 10, 45), //mith ore
				new GameItemVariableAmount(450, 10, 28), //addy ore              //common
				new GameItemVariableAmount(19484, 10, 18), //dragon javelin
				new GameItemVariableAmount(2, 300, 600), //cannon balls
				new GameItemVariableAmount(9245, 250, 350), //onyx bolts e
				new GameItem(2996, 50),//pkp tickets
				new GameItem(11230, 150),//dragon darts






		        new GameItem(Items.ZURIELS_HOOD),
				new GameItem(Items.ZURIELS_ROBE_BOTTOM),
				new GameItem(Items.ZURIELS_ROBE_TOP),
				new GameItem(Items.ZURIELS_STAFF),
				new GameItem(Items.MORRIGANS_COIF),
				new GameItem(Items.MORRIGANS_LEATHER_BODY),
				new GameItem(22647, 1), //zuriel staff
				new GameItem(Items.MORRIGANS_LEATHER_CHAPS),
				new GameItem(22638, 1),
				new GameItem(22641, 1),
				new GameItem(22644, 1),
				new GameItem(22656, 1),   //rare morgians keep here
				new GameItem(24419, 1),
				new GameItem(24417, 1),
				new GameItem(24420, 1)

		));

	}


	public static void searchChest(Player c) {
		for (int key : KEY) {
			if (c.getItems().playerHasItem(key)) {
				c.getItems().deleteItem(key, 1);
				c.startAnimation(ANIMATION);

				int random = Misc.random(100);
				List<GameItem> itemList = random < 55 ? items.get(LootRarity.COMMON) : random >= 55 && random <= 94 ? items.get(LootRarity.UNCOMMON) : items.get(LootRarity.RARE);
				GameItem item = Misc.getRandomItem(itemList);
				c.getItems().addItemUnderAnyCircumstance(item.getId(), item.getAmount());
				c.sendMessage("@blu@You stick your hand in the chest and pull an item out of the chest.");
				PlayerHandler.executeGlobalMessage("@blu@"+c.getLoginName() + " has received: @red@"+ ItemAssistant.getItemName(item.getId()) + " @blu@from the nightmare chest!");
				if (random > 94) {
					Discord.writeDropsSyncMessage(""+ c.getLoginName() + "  has received: "+ ItemAssistant.getItemName(item.getId()) + " from the nightmare chest!");
				}
			} else {
				c.getDH().sendStatement("You will need a nightmare key",
						"you can receive it from nightmare.");
			}
		}
	}

	@Override
	public Map<LootRarity, List<GameItem>> getLoot() {
		return items;
	}

	@Override
	public void roll(Player player) {

	}

	enum Rarity {
		UNCOMMON, COMMON, RARE
	}






}