package io.xeros.content;

import io.xeros.Server;
import io.xeros.content.event.eventcalendar.EventChallenge;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The glod chest.
 *
 * @author C.T for koranes
 *
 */


public class GlodChest  {

	public static int KEY = 4589;
	public static final int ANIMATION = 881;

	private static Map<Rarity, List<GameItem>> items = new HashMap<>();

	static {
		items.put(Rarity.COMMON, Arrays.asList(
				new GameItem(21046, 4),//15% chest rate tomb

				new GameItem(995, 240000), //coins
				new GameItem(2996, 35),//pkp tickets
				new GameItem(11230, 12),//dragon darts
				new GameItem(20849, 32),//dragon thrownaxe
				new GameItem(22804, 35),//dragon knife
				new GameItem(537, 5 + Misc.random(30)),//dragon bones
				new GameItem(1306, 3),//dragon longsword
				new GameItem(1080, 6),//rune platelegs
				new GameItem(1128, 6),//rune platebody
				new GameItem(4087, 1),//dragon platelegs
				new GameItem(4585, 1),//dragon plateskirt
				new GameItem(11232, 50 + Misc.random(10)),//dragon darts
				new GameItem(11840),//dragon boots
				new GameItem(6889),//mages book
				new GameItem(2364, 100),//runite bar
				new GameItem(1514, 100),// magic logs
				new GameItem(1632, 50),//uncut dragonstone

				new GameItem(995, 240000), //coins
				new GameItem(2996, 35),//pkp tickets
				new GameItem(11230, 12),//dragon darts
				new GameItem(20849, 32),//dragon thrownaxe
				new GameItem(22804, 35),//dragon knife
				new GameItem(537, 5 + Misc.random(30)),//dragon bones
				new GameItem(1306, 3),//dragon longsword
				new GameItem(1080, 6),//rune platelegs
				new GameItem(1128, 6),//rune platebody
				new GameItem(4087, 1),//dragon platelegs
				new GameItem(4585, 1),//dragon plateskirt
				new GameItem(11232, 50 + Misc.random(10)),//dragon darts
				new GameItem(11840),//dragon boots
				new GameItem(6889),//mages book
				new GameItem(2364, 100),//runite bar
				new GameItem(1514, 100),// magic logs
				new GameItem(1632, 50),//uncut dragonstone


				new GameItem(11230, 250 + Misc.random(500)),
				new GameItem(12696, 15 + Misc.random(25)),
				new GameItem(21880, 600 + Misc.random(700)),
				new GameItem(995, 50000 + Misc.random(150000)),
				new GameItem(21820, 500 + Misc.random(1100)),
				new GameItem(13442, 150 + Misc.random(250)),
				new GameItem(11937, 200 + Misc.random(250))));
		
		items.put(Rarity.UNCOMMON, Arrays.asList(
				new GameItem(23804, 1),//imbuedifier
				new GameItem(995, 100000 + Misc.random(200000)),
				new GameItem(1249, 1),
				new GameItem(4587, 1),
				new GameItem(9426, 30 + Misc.random(90)),
				new GameItem(3204, 1)));
		
		items.put(Rarity.RARE, Arrays.asList(
				new GameItem(12748, 1),
				new GameItem(12749, 1),
				new GameItem(12746, 1),
				new GameItem(12753, 1),
				new GameItem(12754, 1),
				new GameItem(12755, 1),
				new GameItem(6199, 1),
				new GameItem(6914, 1),
				new GameItem(22547, 1),  //COMMON
				new GameItem(22552, 1),
				new GameItem(22542, 1),
				new GameItem(11791, 1),
				new GameItem(11773, 1),
				new GameItem(11770, 1),
				new GameItem(11772, 1),
				new GameItem(11771, 1),
				new GameItem(13237, 1),
				new GameItem(12748, 1),
				new GameItem(12749, 1),
				new GameItem(12746, 1),
				new GameItem(12753, 1),
				new GameItem(12754, 1),
				new GameItem(12755, 1),
				new GameItem(6199, 1),
				new GameItem(6914, 1),
				new GameItem(22547, 1),  //COMMON
				new GameItem(22552, 1),
				new GameItem(22542, 1),
				new GameItem(11791, 1),
				new GameItem(11773, 1),
				new GameItem(11770, 1),
				new GameItem(11772, 1),
				new GameItem(11771, 1),
				new GameItem(13237, 1),


				new GameItem(24291),//dagon hai top
				new GameItem(24294),//dagon hai bottom
				new GameItem(24288),//dagon hai hat
				new GameItem(19707),//eternal glory
				new GameItem(4151, 1),//whip
				new GameItem(20784, 1), //dragon claws
				new GameItem(21034, 1), //dex scroll
				new GameItem(22622, 1), //statius warhammer          //RARE
				new GameItem(22610, 1), //vesta spear
				new GameItem(22613, 1), //vesta longsword


				new GameItem(12877, 1)));

	}

	public static GameItem randomChestRewards(int chance) {
		int random = Misc.random(chance);
		List<GameItem> itemList = random < chance ? items.get(Rarity.COMMON) : items.get(Rarity.UNCOMMON);
		return Misc.getRandomItem(itemList);
	}

	public static void searchChest(Player c) {
		int pkpbonus = Misc.random(19) + 10;
		if (c.getItems().playerHasItem(KEY)) {
			c.pkp += pkpbonus;
			c.getItems().deleteItem(KEY, 1);
			c.startAnimation(ANIMATION);
			int random = Misc.random(100);
			List<GameItem> itemList = random < 55 ? items.get(Rarity.COMMON) : random >= 55 && random <= 94 ? items.get(Rarity.UNCOMMON) : items.get(Rarity.RARE);
			GameItem item = Misc.getRandomItem(itemList);
			int rareChance = 997;
			if (c.getItems().playerHasItem(21046)) {
				rareChance = 996;
				c.getItems().deleteItem(21046, 1);
				c.sendMessage("@red@You sacrifice your @cya@tablet @red@for an increased drop rate." );
				c.getEventCalendar().progress(EventChallenge.USE_X_CHEST_RATE_INCREASE_TABLETS, 1);
			}
			if (c.getMode().isUltimateIronman()) {
			if (!c.getItems().addItem(item.getId(), item.getAmount())) {
				Server.itemHandler.createGroundItem(c, item.getId(), c.getX(), c.getY(), c.getHeight(), item.getAmount());
			}
			} else {
				c.getItems().addItemUnderAnyCircumstance(item.getId(), item.getAmount());
				PlayerHandler.executeGlobalMessage("@blu@"+c.getLoginName() + " has received: @red@"+item.getAmount()+"x "+ ItemAssistant.getItemName(item.getId()) + " @blu@from the glod chest!");
			}
			c.sendMessage("@dre@You unlock the chest with your glod key and pull an item out of it...");
			c.sendMessage("@blu@You also receive @red@" + pkpbonus + " @blu@pkp as a bonus for killing glod.");
			Right primary = c.getRights().getPrimary();
			int rights = c.getRights().getPrimary().getValue() - 1;
			if (random > 94) {
				Discord.writeDropsSyncMessage(""+ c.getLoginName() + "  has received: "+item.getAmount()+"x "+ ItemAssistant.getItemName(item.getId()) + " from the glod chest!");
			}
		} else {
			c.sendMessage("@dre@I think I need an glod key to open this chest...");
			return;
		}
	}




	enum Rarity {
		UNCOMMON, COMMON, RARE
	}

}