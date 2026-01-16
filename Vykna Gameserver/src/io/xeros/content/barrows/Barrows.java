package io.xeros.content.barrows;

import io.xeros.content.collection_log.CollectionLog;
import io.xeros.content.dailytasks.DailyTasks;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.model.Items;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.npc.data.NpcMaxHit;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;

import java.util.*;

/**
 * Barrows new system
 *
 * @author C.T for koranes.
 */

public class Barrows {

	public final static double BARROWS_CHANCE = 10;//Barrows piece chance
	public final static double AMULET_CHANCE = 100;//Amulet of the damned chance
	public final static double DHAROK_PET_CHANCE = 500;//Dharoks pet chance

	public final static double SLAYER_TIER4_CHANCE = 30;//Tier 4 slayer key chance



	public static void updateBarrowsInterface(Player player) {
		player.getPA().sendFrame126((player.barrowsNpcs[4][1] == 2 ? "@gr3@" : "@red@") + "Dharok", 22046);
		player.getPA().sendFrame126((player.barrowsNpcs[3][1] == 2 ? "@gr3@" : "@red@") + "Guthan", 22047);
		player.getPA().sendFrame126((player.barrowsNpcs[5][1] == 2 ? "@gr3@" : "@red@") + "Ahrim", 22048);
		player.getPA().sendFrame126((player.barrowsNpcs[1][1] == 2 ? "@gr3@" : "@red@") + "Torag", 22049);
		player.getPA().sendFrame126((player.barrowsNpcs[0][1] == 2 ? "@gr3@" : "@red@") + "Verac", 22050);
		player.getPA().sendFrame126((player.barrowsNpcs[2][1] == 2 ? "@gr3@" : "@red@") + "Karil", 22051);
	}

	public static void killedBarrows(Player player, NPC npc) {
		if (npc == null) {
			return;
		}
		if (player == null) {
			return;
		}
		for (int o = 0; o < player.barrowsNpcs.length; o++) {
			if (npc.getNpcId() == player.barrowsNpcs[o][0]) {
				player.barrowsNpcs[o][1] = 2; // 2 for dead
				player.barrowsKillCount++;
				updateBarrowsInterface(player);
				player.barrowsBrothersKilled[o] = true;
			}
		}
	}

	public static void updateSavedBarrowsProgress(Player player) {
		for (int index = 0; index < player.barrowsBrothersKilled.length; index++) {
			if (player.barrowsBrothersKilled[index]) {
				player.barrowsNpcs[index][1] = 2;
				player.barrowsKillCount++;
			}
		}
	}

	/**
	 * Starts the digging emote and action for the Barrows minigame
	 */
	public static void startDigging(final Player player) {
		if (Boundary.isWithInArea(player, 3553, 3561, 3294, 3301)) {
			player.getPA().movePlayer(3578, 9706, 3);
		} else if (Boundary.isWithInArea(player, 3550, 3557, 3278, 3287)) {
			player.getPA().movePlayer(3568, 9683, 3);
		} else if (Boundary.isWithInArea(player, 3561, 3568, 3285, 3292)) {
			player.getPA().movePlayer(3557, 9703, 3);
		} else if (Boundary.isWithInArea(player, 3570, 3579, 3293, 3302)) {
			player.getPA().movePlayer(3556, 9718, 3);
		} else if (Boundary.isWithInArea(player, 3571, 3582, 3278, 3285)) {
			player.getPA().movePlayer(3534, 9704, 3);
		} else if (Boundary.isWithInArea(player, 3562, 3569, 3273, 3279)) {
			player.getPA().movePlayer(3546, 9684, 3);
		}
	}

	/**
	 * Coffin identity, Barrows brother identitiy.
	 */
	public static final int[][] COFFIN_AND_BROTHERS =
			{
					{20772, 1677}, //veracs
					{20721, 1676}, //torags
					{20771, 1675}, //karils
					{20722, 1674}, //guthans
					{20720, 1673}, //dh
					{20770, 1672}, //ahrim
			};

	/**
	 * Picking the random coffin
	 **/
	public static int getRandomCoffin() {
		return Misc.random(COFFIN_AND_BROTHERS.length - 1);
	}

	/**
	 * Selects the coffin and shows the interface if coffin id matches random coffin
	 **/
	public static boolean selectCoffin(Player player, int coffinId) {
		if (player.randomCoffin == 0) {
			player.randomCoffin = getRandomCoffin();
		}

		if (COFFIN_AND_BROTHERS[player.randomCoffin][0] == coffinId) {
			player.getDH().sendDialogues(163, -1);
			return true;
		}
		return false;
	}

	public static void resetBarrows(Player player) {
		for (int index = 0; index < player.barrowsBrothersKilled.length; index++) {
			player.barrowsBrothersKilled[index] = false;
			player.barrowsNpcs[index][1] = 0;
		}
		player.barrowsKillCount = 0;
		player.randomCoffin = Misc.random(3) + 1;
	}

	public final static int[] BARROWS_ITEMS =
			{4708, 4710, 4712, 4714, 4716, 4718, 4720, 4722, 4724, 4726, 4728, 4730, 4732, 4734, 4736, 4738, 4745, 4747, 4749, 4751, 4753, 4755, 4757, 4759};

	public static int Runes[] =
			{4740, 558, 560, 565, 4740};

	public static int Resources[] =
			{2364, 452, 13442, 537};

	public static int randomBarrows() {
		return BARROWS_ITEMS[(int) (Math.random() * BARROWS_ITEMS.length)];
	}

	public static int randomRunes() {
		return Runes[(int) (Math.random() * Runes.length)];
	}

	public static int randomResources() {
		return Resources[(int) (Math.random() * Resources.length)];
	}

	/**
	 * Perform actions of the objects in Barrows.
	 */
	public static boolean isBarrowsObject(final Player player, int objectType) {
		switch (objectType) {

			//Chest
			case 20973:
				barrowsChest(player);
				return true;

			//Verac
			case 20672: //steps id
				player.getPA().movePlayer(3556, 3298, 0);
				if (player.barrowsNpcs[0][1] != 2) {
					player.barrowsNpcs[0][1] = 0;
				}
				return true;
			case 20772:

				if (selectCoffin(player, objectType)) {
					return true;
				}
				if (player.barrowsNpcs[0][1] == 0) {
					spawnBarrowsNpcOnCorrectTile(player, 1677);
					player.barrowsNpcs[0][1] = 1;
				} else {
					player.sendMessage("You have already searched in this sarcophagus.");
				}
				return true;

			//Torag
			case 20671: //steps id
				player.getPA().movePlayer(3553, 3283, 0);
				if (player.barrowsNpcs[1][1] != 2) {
					player.barrowsNpcs[1][1] = 0;
				}
				return true;
			case 20721:

				if (selectCoffin(player, objectType)) {
					return true;
				}
				if (player.barrowsNpcs[1][1] == 0) {
					spawnBarrowsNpcOnCorrectTile(player, 1676);
					player.barrowsNpcs[1][1] = 1;
				} else {
					player.sendMessage("You have already searched in this sarcophagus.");
				}
				return true;

			//Karil
			case 20670: //steps id
				player.getPA().movePlayer(3565, 3276, 0);
				if (player.barrowsNpcs[2][1] != 2) {
					player.barrowsNpcs[2][1] = 0;
				}
				return true;

			case 20771:

				if (selectCoffin(player, objectType)) {
					return true;
				}
				if (player.barrowsNpcs[2][1] == 0) {
					spawnBarrowsNpcOnCorrectTile(player, 1675);
					player.barrowsNpcs[2][1] = 1;
				} else {
					player.sendMessage("You have already searched in this sarcophagus.");
				}
				return true;

			//Guthan
			case 20669: //steps id
				player.getPA().movePlayer(3578, 3284, 0);
				if (player.barrowsNpcs[3][1] != 2) {
					player.barrowsNpcs[3][1] = 0;
				}
				return true;
			case 20722:
				if (selectCoffin(player, objectType)) {
					return true;
				}
				if (player.barrowsNpcs[3][1] == 0) {
					spawnBarrowsNpcOnCorrectTile(player, 1674);
					player.barrowsNpcs[3][1] = 1;
				} else {
					player.sendMessage("You have already searched in this sarcophagus.");
				}
				return true;

			//Dharok
			case 20668: //steps id
				player.getPA().movePlayer(3574, 3298, 0);
				if (player.barrowsNpcs[4][1] != 2) {
					player.barrowsNpcs[4][1] = 0;
				}
				return true;
			case 20720:

				if (selectCoffin(player, objectType)) {
					return true;
				}
				if (player.barrowsNpcs[4][1] == 0) {
					spawnBarrowsNpcOnCorrectTile(player, 1673);
					player.barrowsNpcs[4][1] = 1;
				} else {
					player.sendMessage("You have already searched in this sarcophagus.");
				}
				return true;

			//Ahrim
			case 20667: //steps id
				player.getPA().movePlayer(3565, 3290, 0);
				if (player.barrowsNpcs[5][1] != 2) {
					player.barrowsNpcs[5][1] = 0;
				}
				return true;
			case 20770:
				if (selectCoffin(player, objectType)) {
					return true;
				}
				if (player.barrowsNpcs[5][1] == 0) {
					spawnBarrowsNpcOnCorrectTile(player, 1672);
					player.barrowsNpcs[5][1] = 1;
				} else {
					player.sendMessage("You have already searched in this sarcophagus.");
				}
				return true;

		}
		return false;
	}

	/**
	 * Spawn the given npcType on a tile next to the player that the player can walk to.
	 */
	private static void spawnBarrowsNpcOnCorrectTile(Player player, int npcType) {
		NPCSpawning.spawnBarrows(player, npcType, player.getX(), player.getY(), player.getHeight(), true, true, NpcMaxHit.getMaxHit(npcType));


	}

	private static void barrowsChest(Player player) {
		if (player.barrowsKillCount < 5) {
			player.sendMessage("You haven't killed all the brothers.");
			return;
		} else if (player.barrowsKillCount == 5 && player.barrowsNpcs[player.randomCoffin][1] == 1) {
			player.sendMessage("You have already summoned this npc.");
			return;
		}
		if (player.barrowsNpcs[player.randomCoffin][1] == 0 && player.barrowsKillCount >= 5) {
			spawnBarrowsNpcOnCorrectTile(player, player.barrowsNpcs[player.randomCoffin][0]);
			player.barrowsNpcs[player.randomCoffin][1] = 1;
			return;
		}
		if (player.getInventory().freeInventorySlots() < 2) {
			player.sendMessage("You need at least 2 inventory slots empty.");
			return;
		}
		if ((player.barrowsKillCount > 5 || player.barrowsNpcs[player.randomCoffin][1] == 2)) {
			player.setBarrowsRunCompleted(player.getBarrowsRunCompleted() + 1);
			player.sendMessage("Your barrows run count is: @red@"+ player.getBarrowsRunCompleted() + ".");
			ArrayList<String> list = new ArrayList<>();
			resetBarrows(player);
			int runeId = randomRunes();
			int runeAmount = Misc.random(10) + 50;
			int resourceId = randomResources();
			int resourceAmount = Misc.random(10) + 50;
			player.getItems().addItem(runeId, runeAmount);
			player.getItems().addItem(resourceId, resourceAmount);
			list.add(runeId + " " + runeAmount);
			list.add(resourceId + " " + resourceAmount);

			if (player.getBarrowsRunCompleted() == 10) {
				player.getItems().addItem(995, 350000);
				PlayerHandler.executeGlobalMessage("@blu@"+ Misc.capitalize(player.getLoginName()) + " has received @red@350k @blu@for completing @red@10 barrows runs.");
			}
			if (player.getBarrowsRunCompleted() == 25) {
				player.getItems().addItem(995, 800000);
				PlayerHandler.executeGlobalMessage("@blu@"+ Misc.capitalize(player.getLoginName()) + " has received @red@800k @blu@for completing @red@25 barrows runs.");
			}
			if (player.getBarrowsRunCompleted() == 50) {
				player.getItems().addItem(995, 2000000);
				PlayerHandler.executeGlobalMessage("@blu@"+ Misc.capitalize(player.getLoginName()) + " has received @red@2m @blu@ for completing @red@50 barrows runs.");
			}
			if (player.getBarrowsRunCompleted() == 100) {
				player.getItems().addItem(995, 3000000);
				PlayerHandler.executeGlobalMessage("@blu@"+ Misc.capitalize(player.getLoginName()) + " has received @red@3m @blu@for completing @red@100 barrows runs.");
			}
			if (player.getBarrowsRunCompleted() == 250) {
				player.getItems().addItem(995, 5000000);
				PlayerHandler.executeGlobalMessage("@blu@"+ Misc.capitalize(player.getLoginName()) + " has received @red@5m @blu@for completing @red@250 barrows runs.");
			}
			if (player.getBarrowsRunCompleted() == 500) {
				player.getItems().addItem(995, 10000000);
				PlayerHandler.executeGlobalMessage("@blu@"+ Misc.capitalize(player.getLoginName()) + " has received @red@10m @blu@for completing @red@500 barrows runs.");
			}
			if (player.getBarrowsRunCompleted() == 1000) {
				player.getItems().addItem(995, 20000000);
				PlayerHandler.executeGlobalMessage("@blu@"+ Misc.capitalize(player.getLoginName()) + " has received @red@20m @blu@for completing @red@1000 barrows runs.");
				Discord.writeDropsSyncMessage(""+ player.getLoginName() + " has received: 20m for completing 1000 Barrows Runs.");
			}
			if (Misc.hasOneOutOf(AMULET_CHANCE)) {
				player.getItems().addItem(12853, 1);
				PlayerHandler.executeGlobalMessage("@blu@"+ Misc.capitalize(player.getLoginName()) + " has received the @red@amulet of the damned @blu@from barrows.");
				player.getCollectionLog().handleDrop(player, CollectionLog.BARROWS_ID, 12853, 1);
			}
			if (Misc.hasOneOutOf(DHAROK_PET_CHANCE)) {
				player.getItems().addItem(16015, 1);
				PlayerHandler.executeGlobalMessage("@blu@"+ Misc.capitalize(player.getLoginName()) + " has received the @red@dharok pet @blu@from barrows");
				Discord.writeDropsSyncMessage(""+ player.getLoginName() + " has received: the Dharok Pet from Barrows.");
			}
			if (Misc.hasOneOutOf(SLAYER_TIER4_CHANCE)) {
				player.getItems().addItem(21053, 1);
				PlayerHandler.executeGlobalMessage("@blu@"+ Misc.capitalize(player.getLoginName()) + " has received the @red@Slayer key (tier4) @blu@from barrows");
			}
			if (Misc.hasOneOutOf(BARROWS_CHANCE)) {
				list.add(giveBarrowsItemReward(player));
			}
			player.getPA().startTeleport(3564, 3288, 0, "MODERN", false);
			DailyTasks.increase(player, DailyTasks.PossibleTasks.BARROWS_CHESTS);
			updateBarrowsInterface(player);
			Barrows.displayReward(player, list);
			saveBarrowsRunTime(player);

		}

	}

	private static String giveBarrowsItemReward(Player player) {
		int randomBarrows = randomBarrows();
		player.getItems().addItem(randomBarrows, 1);
		player.sendMessage("You have received " + Misc.getAorAn(ItemAssistant.getItemName(randomBarrows)) + "!");
		PlayerHandler.executeGlobalMessage("@blu@"+ Misc.capitalize(player.getLoginName()) + " has received @red@" + ItemAssistant.getItemName(randomBarrows) + " @blu@from Barrows.");
		Discord.writeDropsSyncMessage(""+ player.getLoginName() + " has received:" + ItemAssistant.getItemName(randomBarrows) + " from Barrows.");
		player.getCollectionLog().handleDrop(player, CollectionLog.BARROWS_ID, randomBarrows(), 1);
		return randomBarrows + " 1";
	}

	public static void saveBarrowsRunTime(Player player) {
		if (player.barrowsTimer == 0) {
			return;
		}
		long barrowsRunTime = (System.currentTimeMillis() - player.barrowsTimer) / 1000;
		int oldTime = player.barrowsPersonalRecord;
		boolean broken = false;
		if ((int) barrowsRunTime < player.barrowsPersonalRecord || oldTime == 0) {
			player.barrowsPersonalRecord = (int) barrowsRunTime;
			broken = true;
		}
		if (broken) {
			if (oldTime != 0) {
				player.sendMessage("<col=005f00>You have broken your previous record of " + oldTime + " seconds with " + player.barrowsPersonalRecord + " seconds.");
				player.getItems().addItem(995, 150000);//150k for beating old record.
				player.sendMessage("You have received 150k for beating your previous record!");
			} else {
				player.sendMessage("You have completed the Barrows run in @red@" + player.barrowsPersonalRecord + " seconds.");
			}
		} else {
			player.sendMessage("You have completed the Barrows run in @red@" + barrowsRunTime + " seconds.");
		}

	}

	public static void startBarrowsTimer(Player attacker, NPC npc) {
		if (attacker.barrowsKillCount > 0) {
			return;
		}
		boolean brotherFound = false;
		for (int index = 0; index < COFFIN_AND_BROTHERS.length; index++) {
			if (npc.getNpcId() == COFFIN_AND_BROTHERS[index][1]) {
				brotherFound = true;
				break;
			}
		}

		if (!brotherFound) {
			return;
		}
		if (npc.getHealth().getCurrentHealth() < npc.getHealth().getMaximumHealth()) {
			return;
		}
		attacker.barrowsTimer = System.currentTimeMillis();
	}

	public static void resetCoffinStatus(Player player) {
		for (int index = 0; index < player.barrowsNpcs.length; index++) {
			if (player.barrowsNpcs[index][1] == 1) {
				player.barrowsNpcs[index][1] = 0;
			}
		}
	}

















	public static void displayReward(Player player, ArrayList<?> list) {
		player.getOutStream().createFrameVarSizeWord(53);
		player.getOutStream().writeWord(6963);
		player.getOutStream().writeWord(list.size());
		for (int i = 0; i < list.size(); i++) {
			String[] args = ((String) list.get(i)).split(" ");
			int item = Integer.parseInt(args[0]);
			int amount = Integer.parseInt(args[1]);
			if (amount > 254) {
				player.getOutStream().writeByte(255);
				player.getOutStream().writeDWord_v2(amount);
			} else {
				player.getOutStream().writeByte(amount);
			}
			player.getOutStream().writeWordBigEndianA(item + 1);
		}
		player.getOutStream().endFrameVarSizeWord();
		player.flushOutStream();
		player.getPA().showInterface(6960);
	}












	private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();



	// Collection log items had to be handled this way.
	static {
		items.put(LootRarity.RARE, Arrays.asList(
				new GameItem(4716, 1),//dh helm
				new GameItem(4720, 1),//dh body
				new GameItem(4722, 1),//dh legs
				new GameItem(4718, 1),//dh axe

				new GameItem(4724, 1),//guthan helm
				new GameItem(4728, 1),//guthan body
				new GameItem(4730, 1),//guthan skirt
				new GameItem(4726, 1),//guthan spear

				new GameItem(4753, 1),//verac helm
				new GameItem(4757, 1),//verac body
				new GameItem(4759, 1),//verac skirt
				new GameItem(4755, 1),//verac flail

				new GameItem(4745, 1),//torag helm
				new GameItem(4749, 1),//torag body
				new GameItem(4751, 1),//torag legs
				new GameItem(4747, 1),//torag hammers

				new GameItem(4732, 1),//karil coif
				new GameItem(4736, 1),//karil body
				new GameItem(4738, 1),//karil skirt
				new GameItem(4734, 1),//karil crossbow

				new GameItem(4708, 1),//ahrim hood
				new GameItem(4712, 1),//ahrim top
				new GameItem(4714, 1),//ahrim bottom
				new GameItem(4710, 1),//ahrim staff

				new GameItem(12853, 1)//Amulet of the damned

		));
	}



	public static ArrayList<GameItem> getRareDrops() {
		ArrayList<GameItem> drops = new ArrayList<>();
		List<GameItem> found = items.get(LootRarity.RARE);
		for(GameItem f : found) {
			boolean foundItem = false;
			for(GameItem drop : drops) {
				if (drop.getId() == f.getId()) {
					foundItem = true;
					break;
				}
			}
			if (!foundItem) {
				drops.add(f);
			}
		}
		return drops;
	}








}
