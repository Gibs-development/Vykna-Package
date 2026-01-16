package io.xeros.content;

import java.util.Arrays;

import io.xeros.achievements.AchievementHandler;
import io.xeros.achievements.AchievementList;
import io.xeros.chance.Chance;
import io.xeros.chance.WeightedChance;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;

/**
 * Fishing casket
 * @author C.T for RuneRogue
 *
 */
public class FishingCasket {
	/**
	 * casket Identification
	 */
	private final static GameItem CASKET = new GameItem(7956);

	/**
	 * All possible loot from the fishing casket
	 */
	public static Chance<GameItem> FISHINGCASKET = new Chance<>(Arrays.asList(

			new WeightedChance<>(WeightedChance.COMMON, new GameItem(995, 25000)),//coins
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(995, 50000)),//coins
			new WeightedChance<>(WeightedChance.RARE, new GameItem(995, 100000)),//coins
			new WeightedChance<>(WeightedChance.VERY_RARE, new GameItem(995, 200000)),//coins
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(390, 50)),//manta
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(360, 50)),//tuna
			new WeightedChance<>(WeightedChance.COMMON, new GameItem(378, 50)),//lobster
			new WeightedChance<>(WeightedChance.COMMON, new GameItem(372, 50)),//swordfish
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(396, 50)),//sea turtle
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(7945, 50)),//monkfish
			new WeightedChance<>(WeightedChance.RARE, new GameItem(3143, 50)),//karambwan
			new WeightedChance<>(WeightedChance.RARE, new GameItem(11935, 50)), //dark crab
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(384, 30)),//shark
			new WeightedChance<>(WeightedChance.VERY_RARE, new GameItem(13320, 1)),//heron
			new WeightedChance<>(WeightedChance.RARE, new GameItem(13258, 1)), //wrangler hat
			new WeightedChance<>(WeightedChance.RARE, new GameItem(13259, 1)), //wrangler top
			new WeightedChance<>(WeightedChance.RARE, new GameItem(13260, 1)), //wrangler bottoms
			new WeightedChance<>(WeightedChance.RARE, new GameItem(13261, 1)) //wrangler booties


	));
	
	
	/**
	 * Handles opening the casket
	 * @param player
	 */
	public static void openFishingCasket(Player player) {
		GameItem reward = FISHINGCASKET.nextObject().get();
		String name = reward.getDef().getName();
		String formatted_name = Misc.getAOrAn(name) + " " + name;
		player.getItems().deleteItem(CASKET.getId(), 1);
		player.getItems().addItemUnderAnyCircumstance(reward.getId(), reward.getAmount());
		player.sendMessage("@red@You manage to pull a " + formatted_name + " from the fishing casket.");

		if (Misc.hasOneOutOf(250)) {//Pet shark
			player.getItems().addItemUnderAnyCircumstance(7993, 1);//pet shark
			player.sendMessage("@red@Congratulations you have received the pet shark.");
			Announcement.announce("@red@"+player.getLoginName()+" @org@has received the @red@Pet Shark @org@from an @red@Fishing Casket.");
			Discord.writeDropsSyncMessage(""+player.getLoginName()+" has received the Pet Shark from an Fishing Casket.");
			AchievementHandler.activate(player, AchievementList.PET_SHARK, 1);
		}

	}
}
