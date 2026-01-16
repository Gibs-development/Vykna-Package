package io.xeros.content;

import io.xeros.Server;
import io.xeros.content.event.eventcalendar.EventChallenge;
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
 * The Kratos chest.
 *
 * @author C.T for koranes
 *
 */


public class KratosChest {

	public static int KEY = 7302;//Kratos key
	public static final int ANIMATION = 881;

	private static Map<Rarity, List<GameItem>> items = new HashMap<>();

	static {
		items.put(Rarity.COMMON, Arrays.asList(
				new GameItem(21046, 1),//15% chest rate tomb
				new GameItem(995, 500000), //coins
				new GameItem(11230, 40),//dragon darts
				new GameItem(20849, 50),//dragon thrownaxe
				new GameItem(22804, 45),//dragon knife
				new GameItem(537, 5 + Misc.random(30)),//dragon bones
				new GameItem(1306, 3),//dragon longsword
				new GameItem(4087, 1),//dragon platelegs
				new GameItem(4585, 1),//dragon plateskirt
				new GameItem(11232, 50 + Misc.random(10)),//dragon darts
				new GameItem(11840),//dragon boots
				new GameItem(6889),//mages book
				new GameItem(2364, 100),//runite bar
				new GameItem(1514, 100),// magic logs
				new GameItem(1632, 50),//uncut dragonstone

				new GameItem(995, 500000), //coins
				new GameItem(11230, 12),//dragon darts
				new GameItem(20849, 32),//dragon thrownaxe
				new GameItem(22804, 35),//dragon knife
				new GameItem(537, 5 + Misc.random(30)),//dragon bones
				new GameItem(1306, 3),//dragon longsword
				new GameItem(4087, 1),//dragon platelegs
				new GameItem(4585, 1),//dragon plateskirt
				new GameItem(11232, 50 + Misc.random(10)),//dragon darts
				new GameItem(11840),//dragon boots
				new GameItem(6889),//mages book
				new GameItem(2364, 100),//runite bar
				new GameItem(1514, 100),// magic logs
				new GameItem(1632, 50)));//uncut dragonstone

		
		items.put(Rarity.UNCOMMON, Arrays.asList(
				new GameItem(995, 600000), //coins
				new GameItem(11230, 12),//dragon darts
				new GameItem(20849, 32),//dragon thrownaxe
				new GameItem(22804, 35),//dragon knife
				new GameItem(537, 5 + Misc.random(30)),//dragon bones
				new GameItem(1306, 3),//dragon longsword
				new GameItem(4087, 1),//dragon platelegs
				new GameItem(4585, 1),//dragon plateskirt
				new GameItem(11232, 50 + Misc.random(10)),//dragon darts
				new GameItem(11840),//dragon boots
				new GameItem(6889),//mages book
				new GameItem(2364, 100),//runite bar
				new GameItem(1514, 100),// magic logs

				new GameItem(995, 600000), //coins
				new GameItem(11230, 12),//dragon darts
				new GameItem(20849, 32),//dragon thrownaxe
				new GameItem(22804, 35),//dragon knife
				new GameItem(537, 5 + Misc.random(30)),//dragon bones
				new GameItem(1306, 3),//dragon longsword
				new GameItem(4087, 1),//dragon platelegs
				new GameItem(4585, 1),//dragon plateskirt
				new GameItem(11232, 50 + Misc.random(10)),//dragon darts
				new GameItem(11840),//dragon boots
				new GameItem(6889),//mages book
				new GameItem(2364, 100),//runite bar
				new GameItem(1514, 100),// magic logs


				new GameItem(9426, 30 + Misc.random(90)),

				new GameItem(22093, 1),// vote streak key

				new GameItem(3204, 1)));
		
		items.put(Rarity.RARE, Arrays.asList(
				new GameItem(11230, 180),//dragon darts
				new GameItem(20849, 132),//dragon thrownaxe
				new GameItem(22804, 135),//dragon knife
				new GameItem(537, 5 + Misc.random(30)),//dragon bones                 //Common
				new GameItem(1306, 3),//dragon longsword
				new GameItem(4087, 1),//dragon platelegs
				new GameItem(4585, 1),//dragon plateskirt
				new GameItem(11232, 100 + Misc.random(10)),//dragon darts
				new GameItem(11840),//dragon boots
				new GameItem(6889),//mages book
				new GameItem(2364, 100),//runite bar
				new GameItem(1514, 100),// magic logs

				new GameItem(11230, 180),//dragon darts
				new GameItem(20849, 132),//dragon thrownaxe
				new GameItem(22804, 135),//dragon knife
				new GameItem(537, 5 + Misc.random(30)),//dragon bones                 //Common
				new GameItem(1306, 3),//dragon longsword
				new GameItem(4087, 1),//dragon platelegs
				new GameItem(4585, 1),//dragon plateskirt
				new GameItem(11232, 100 + Misc.random(10)),//dragon darts
				new GameItem(11840),//dragon boots
				new GameItem(6889),//mages book
				new GameItem(2364, 100),//runite bar
				new GameItem(1514, 100),// magic logs

				new GameItem(11230, 180),//dragon darts
				new GameItem(20849, 132),//dragon thrownaxe
				new GameItem(22804, 135),//dragon knife
				new GameItem(537, 5 + Misc.random(30)),//dragon bones                 //Common
				new GameItem(1306, 3),//dragon longsword
				new GameItem(4087, 1),//dragon platelegs
				new GameItem(4585, 1),//dragon plateskirt
				new GameItem(11232, 100 + Misc.random(10)),//dragon darts
				new GameItem(11840),//dragon boots
				new GameItem(6889),//mages book
				new GameItem(2364, 100),//runite bar
				new GameItem(1514, 100),// magic logs

				new GameItem(11230, 180),//dragon darts
				new GameItem(20849, 132),//dragon thrownaxe
				new GameItem(22804, 135),//dragon knife
				new GameItem(537, 5 + Misc.random(30)),//dragon bones                 //Common
				new GameItem(1306, 3),//dragon longsword
				new GameItem(4087, 1),//dragon platelegs
				new GameItem(4585, 1),//dragon plateskirt
				new GameItem(11232, 100 + Misc.random(10)),//dragon darts
				new GameItem(11840),//dragon boots
				new GameItem(6889),//mages book
				new GameItem(2364, 100),//runite bar
				new GameItem(1514, 100),// magic logs

				new GameItem(4151, 1),//whip
				new GameItem(4151, 1),//whip
				new GameItem(4151, 1),//whip
				new GameItem(4151, 1),//whip
				new GameItem(4151, 1),//whip
				new GameItem(4151, 1),//whip
				new GameItem(4151, 1),//whip
				new GameItem(4151, 1),//whip
				new GameItem(4151, 1),//whip
				new GameItem(4151, 1),//whip
				new GameItem(4151, 1),//whip
				new GameItem(4151, 1),//whip
				new GameItem(4151, 1),//whip
				new GameItem(4151, 1),//whip
				new GameItem(4151, 1),//whip
				new GameItem(4151, 1),//whip
				new GameItem(4151, 1),//whip
				new GameItem(4151, 1),//whip
				new GameItem(4151, 1),//whip
				new GameItem(4151, 1),//whip
				new GameItem(4151, 1),//whip
				new GameItem(22093, 3),// vote streak key
				new GameItem(22622, 1), //statius warhammer
				new GameItem(22610, 1), //vesta spear                  //RARE
				new GameItem(22613, 1), //vesta longsword

				//new GameItem(34037, 1), //death cape
				new GameItem(12468, 1), //New dragon kite
				new GameItem(30022, 1), //kratos pet
				new GameItem(30122, 1), //kratos pet                   //RARE

				new GameItem(12877, 1)));

	}

	public static GameItem randomChestRewards(int chance) {
		int random = Misc.random(chance);
		List<GameItem> itemList = random < chance ? items.get(Rarity.COMMON) : items.get(Rarity.UNCOMMON);
		return Misc.getRandomItem(itemList);
	}

	public static void searchChest(Player c) {
		if (c.getItems().playerHasItem(KEY)) {
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
				c.getItems().addItemUnderAnyCircumstance(995, 8000000);
			//Remember to add any rares you add to the chest here to be announced
			if(item.getId() == 22622 || item.getId() == 22610 || item.getId() == 22613 || item.getId() == 12468 || item.getId() == 30022 || item.getId() == 30122 || item.getId() == 12877){
				PlayerHandler.executeGlobalMessage("@red@"+c.getLoginName() + " @blu@has received: @red@"+item.getAmount()+"x @red@"+ ItemAssistant.getItemName(item.getId()) + " @blu@from the @red@Kratos Chest!");
				Discord.writeDropsSyncMessage(""+ c.getLoginName() + "  has received: "+item.getAmount()+"x "+ ItemAssistant.getItemName(item.getId()) + " from the Kratos Chest!");
			}
			}
			c.sendMessage("@pur@You manage to unlock the chest with your kratos key and pull some loot from it...");

		} else {
			c.sendMessage("@red@You need an total of 5 keys to open this chest");
			c.sendMessage("@blu@Nightmare key, Seren key, Solak key, Glod key, Vote key.");
			c.sendMessage("@pur@Faster option head to the vote store for the @red@Kratos entry key.");
			return;
		}
	}




	enum Rarity {
		UNCOMMON, COMMON, RARE
	}

}