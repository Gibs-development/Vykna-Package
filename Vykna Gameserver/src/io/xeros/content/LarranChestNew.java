package io.xeros.content;

import io.xeros.Server;
import io.xeros.content.event.eventcalendar.EventChallenge;
import io.xeros.model.Items;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The new larrans chest.
 *
 * @author C.T for koranes
 *
 */


public class LarranChestNew {

	public static int KEY = Items.LARRANS_KEY;//larrans big key
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

				new GameItem(995, 5_000_000),
				new GameItem(21046, 5),//15% chest rate tomb
				new GameItem(Items.STEEL_BAR_NOTED, 1000),
				new GameItem(Items.MAGIC_LOGS_NOTED, 400),
				new GameItem(Items.RAW_ANGLERFISH_NOTED, 300),
				new GameItem(Items.IRON_ORE_NOTED, 1000),
				new GameItem(Items.COAL_NOTED, 1000),
				new GameItem(Items.PURE_ESSENCE_NOTED, 1500),
				new GameItem(Items.BLOOD_RUNE, 1500),
				new GameItem(Items.DEATH_RUNE, 1500),
				new GameItem(Items.GRIMY_TORSTOL_NOTED, 75),
				new GameItem(Items.GRIMY_TOADFLAX_NOTED, 75),
				new GameItem(Items.GRIMY_SNAPDRAGON_NOTED, 75),
				new GameItem(Items.OVERLOAD_4, 5),
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

				new GameItem(995, 5_000_000),
				new GameItem(21046, 5),//15% chest rate tomb
				new GameItem(Items.STEEL_BAR_NOTED, 1000),
				new GameItem(Items.MAGIC_LOGS_NOTED, 400),
				new GameItem(Items.RAW_ANGLERFISH_NOTED, 300),
				new GameItem(Items.IRON_ORE_NOTED, 1000),
				new GameItem(Items.COAL_NOTED, 1000),
				new GameItem(Items.PURE_ESSENCE_NOTED, 1500),
				new GameItem(Items.BLOOD_RUNE, 1500),
				new GameItem(Items.DEATH_RUNE, 1500),
				new GameItem(Items.GRIMY_TORSTOL_NOTED, 75),
				new GameItem(Items.GRIMY_TOADFLAX_NOTED, 75),
				new GameItem(Items.GRIMY_SNAPDRAGON_NOTED, 75),
				new GameItem(Items.OVERLOAD_4, 5),


				new GameItem(9426, 30 + Misc.random(90)),

				new GameItem(22093, 1),// vote streak key

				new GameItem(3204, 1)));
		
		items.put(Rarity.RARE, Arrays.asList(
				new GameItem(995, 5_000_000),
				new GameItem(21046, 5),//15% chest rate tomb
				new GameItem(Items.STEEL_BAR_NOTED, 1000),
				new GameItem(Items.MAGIC_LOGS_NOTED, 400),
				new GameItem(Items.RAW_ANGLERFISH_NOTED, 300),
				new GameItem(Items.IRON_ORE_NOTED, 1000),
				new GameItem(Items.COAL_NOTED, 1000),
				new GameItem(Items.PURE_ESSENCE_NOTED, 1500),
				new GameItem(Items.BLOOD_RUNE, 1500),
				new GameItem(Items.DEATH_RUNE, 1500),
				new GameItem(Items.GRIMY_TORSTOL_NOTED, 75),
				new GameItem(Items.GRIMY_TOADFLAX_NOTED, 75),
				new GameItem(Items.GRIMY_SNAPDRAGON_NOTED, 75),
				new GameItem(Items.OVERLOAD_4, 5),

				new GameItem(Items.STEEL_BAR_NOTED, 1000),
				new GameItem(Items.MAGIC_LOGS_NOTED, 400),
				new GameItem(Items.RAW_ANGLERFISH_NOTED, 300),
				new GameItem(Items.IRON_ORE_NOTED, 1000),
				new GameItem(Items.COAL_NOTED, 1000),
				new GameItem(Items.PURE_ESSENCE_NOTED, 1500),
				new GameItem(Items.BLOOD_RUNE, 1500),
				new GameItem(Items.DEATH_RUNE, 1500),
				new GameItem(Items.GRIMY_TORSTOL_NOTED, 75),
				new GameItem(Items.GRIMY_TOADFLAX_NOTED, 75),
				new GameItem(Items.GRIMY_SNAPDRAGON_NOTED, 75),
				new GameItem(Items.OVERLOAD_4, 5),

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

				new GameItem(Items.VESTAS_LONGSWORD),
				new GameItem(Items.VESTAS_SPEAR),
				new GameItem(Items.VESTAS_CHAINBODY),
				new GameItem(Items.VESTAS_PLATESKIRT),
				new GameItem(Items.STATIUSS_WARHAMMER),
				new GameItem(Items.STATIUSS_FULL_HELM),
				new GameItem(Items.STATIUSS_PLATEBODY),
				new GameItem(Items.STATIUSS_PLATELEGS),
				new GameItem(Items.ZURIELS_HOOD),
				new GameItem(Items.ZURIELS_ROBE_BOTTOM),
				new GameItem(Items.ZURIELS_ROBE_TOP),
				new GameItem(Items.ZURIELS_STAFF),
				new GameItem(Items.MORRIGANS_COIF),
				new GameItem(Items.MORRIGANS_LEATHER_BODY),
				new GameItem(Items.MORRIGANS_LEATHER_CHAPS),

				new GameItem(22093, 5)));//VOTE STREAK KEY

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
				c.getItems().addItemUnderAnyCircumstance(995, 500000);
			//Remember to add any rares you add to the chest here to be announced
			if(item.getId() == Items.VESTAS_LONGSWORD || item.getId() == Items.VESTAS_SPEAR || item.getId() == Items.VESTAS_CHAINBODY || item.getId() == Items.VESTAS_PLATESKIRT || item.getId() == Items.STATIUSS_WARHAMMER
					|| item.getId() == Items.STATIUSS_FULL_HELM || item.getId() == Items.STATIUSS_PLATEBODY || item.getId() == Items.STATIUSS_PLATELEGS || item.getId() == Items.ZURIELS_HOOD
					|| item.getId() == Items.ZURIELS_ROBE_BOTTOM || item.getId() == Items.ZURIELS_ROBE_TOP || item.getId() == Items.ZURIELS_STAFF
					|| item.getId() == Items.MORRIGANS_COIF || item.getId() == Items.MORRIGANS_LEATHER_BODY || item.getId() == Items.MORRIGANS_LEATHER_CHAPS){
				PlayerHandler.executeGlobalMessage("@pur@"+c.getLoginName() + " @pur@has received an rare: @red@"+item.getAmount()+"x @red@"+ ItemAssistant.getItemName(item.getId()) + " @pur@from the @pur@Larran's Chest!");
				Discord.writeDropsSyncMessage(""+ c.getLoginName() + "  has received an rare: "+item.getAmount()+"x "+ ItemAssistant.getItemName(item.getId()) + " from the Larran's Chest!");
			}
			}
			c.sendMessage("@pur@You manage to unlock the chest with your larran's key and pull some loot from it...");

		} else {
			c.sendMessage("@red@You need an larran's key to open this chest!");
			return;
		}
	}




	enum Rarity {
		UNCOMMON, COMMON, RARE
	}

}