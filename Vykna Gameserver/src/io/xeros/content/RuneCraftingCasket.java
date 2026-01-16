package io.xeros.content;

import io.xeros.achievements.AchievementHandler;
import io.xeros.achievements.AchievementList;
import io.xeros.chance.Chance;
import io.xeros.chance.WeightedChance;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;

import java.util.Arrays;

/**
 * Runecrafting Casket
 * @author C.T for RuneRogue
 *
 */
public class RuneCraftingCasket {
	/**
	 * casket Identification
	 */
	private final static GameItem CASKET = new GameItem(25590);

	/**
	 * All possible loot from the fishing casket
	 */
	public static Chance<GameItem> RUNECRAFTINGCASKET = new Chance<>(Arrays.asList(

			new WeightedChance<>(WeightedChance.COMMON, new GameItem(995, 25000)),//coins
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(995, 50000)),//coins
			new WeightedChance<>(WeightedChance.RARE, new GameItem(995, 100000)),//coins
			new WeightedChance<>(WeightedChance.VERY_RARE, new GameItem(995, 200000)),//coins
			new WeightedChance<>(WeightedChance.COMMON, new GameItem(554, 100)),//Fire Rune
			new WeightedChance<>(WeightedChance.COMMON, new GameItem(555, 100)),//Water Rune
			new WeightedChance<>(WeightedChance.COMMON, new GameItem(556, 100)),//Air Rune
			new WeightedChance<>(WeightedChance.COMMON, new GameItem(557, 100)),//earth Rune
			new WeightedChance<>(WeightedChance.COMMON, new GameItem(558, 100)),//mind Rune
			new WeightedChance<>(WeightedChance.COMMON, new GameItem(559, 100)),//body Rune
			new WeightedChance<>(WeightedChance.COMMON, new GameItem(560, 100)),//death Rune
			new WeightedChance<>(WeightedChance.COMMON, new GameItem(561, 100)),//nature Rune
			new WeightedChance<>(WeightedChance.COMMON, new GameItem(562, 100)),//chaos Rune
			new WeightedChance<>(WeightedChance.COMMON, new GameItem(563, 100)),//law Rune
			new WeightedChance<>(WeightedChance.COMMON, new GameItem(564, 100)),//cosmic Rune
			new WeightedChance<>(WeightedChance.COMMON, new GameItem(565, 100)),//blood Rune
			new WeightedChance<>(WeightedChance.COMMON, new GameItem(566, 100)),//Soul Rune
			new WeightedChance<>(WeightedChance.COMMON, new GameItem(9075, 100)),//Astral Rune
			new WeightedChance<>(WeightedChance.COMMON, new GameItem(21880, 100)),//Wrath Rune
			new WeightedChance<>(WeightedChance.COMMON, new GameItem(7936, 50)),//Pure Essence
			new WeightedChance<>(WeightedChance.COMMON, new GameItem(1436, 50)),//rune essence
			new WeightedChance<>(WeightedChance.COMMON, new GameItem(7936, 100)),//Pure Essence
			new WeightedChance<>(WeightedChance.COMMON, new GameItem(1436, 100)),//rune essence
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(7936, 250)),//Pure Essence
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(1436, 250)),//rune essence
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(7936, 500)),//Pure Essence
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(1436, 500)),//rune essence
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(7936, 50)),//Pure Essence
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(1436, 50)),//rune essence
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(7936, 100)),//Pure Essence
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(1436, 100)),//rune essence
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(554, 500)),//Fire Rune
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(555, 500)),//Water Rune
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(556, 500)),//Air Rune
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(557, 500)),//earth Rune
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(558, 500)),//mind Rune
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(559, 500)),//body Rune
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(560, 500)),//death Rune
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(561, 500)),//nature Rune
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(562, 500)),//chaos Rune
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(563, 500)),//law Rune
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(564, 500)),//cosmic Rune
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(565, 500)),//blood Rune
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(566, 500)),//Soul Rune
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(9075, 500)),//Astral Rune
			new WeightedChance<>(WeightedChance.UNCOMMON, new GameItem(21880, 500)),//Wrath Rune
			new WeightedChance<>(WeightedChance.RARE, new GameItem(26807, 1)),// Abyssal green dye
			new WeightedChance<>(WeightedChance.RARE, new GameItem(26809, 1)),// Abyssal blue dye
			new WeightedChance<>(WeightedChance.RARE, new GameItem(26811, 1)),// Abyssal red dye
			new WeightedChance<>(WeightedChance.RARE, new GameItem(26850, 1)), //hat of the eye
			new WeightedChance<>(WeightedChance.RARE, new GameItem(26852, 1)), //robe top of the eye
			new WeightedChance<>(WeightedChance.RARE, new GameItem(26854, 1)), //robe bottoms of the eye
			new WeightedChance<>(WeightedChance.RARE, new GameItem(26856, 1)),//boots of the eye
            new WeightedChance<>(WeightedChance.VERY_RARE, new GameItem(20665, 1))//Rift guardian


	));
	
	
	/**
	 * Handles opening the casket
	 * @param player
	 */
	public static void openRuneCraftingCasket(Player player) {
		GameItem reward = RUNECRAFTINGCASKET.nextObject().get();
		String name = reward.getDef().getName();
		String formatted_name = Misc.getAOrAn(name) + " " + name;
		player.getItems().deleteItem(CASKET.getId(), 1);
		player.getItems().addItemUnderAnyCircumstance(reward.getId(), reward.getAmount());
		player.sendMessage("@red@You manage to pull a " + formatted_name + " from the runecrafting casket.");

		if (Misc.hasOneOutOf(85)) {//Mystery box
			player.getItems().addItemUnderAnyCircumstance(6199, 1);//Mystery box
			player.sendMessage("@red@Congratulations you have received an mystery box.");
			Announcement.announce("@red@"+player.getLoginName()+" @blu@has received an @red@Mystery Box @blu@from an @red@Runecrafting Casket.");
			Discord.writeDropsSyncMessage(""+player.getLoginName()+" has received an Mystery box from an Runecrating Casket.");
		}

		if (Misc.hasOneOutOf(50)) {//Colossal pouch
			if (player.getItems().getItemCount(26784, false) == 0) {
				//player.sendMessage("You receive a {}.", ItemDef.forId(26784).getName());
				player.getItems().addItemUnderAnyCircumstance(26784, 1);
				player.sendMessage("@red@You get lucky and receive an Colossal Pouch.");
				Announcement.announce("@red@"+player.getLoginName()+" @blu@has received an @red@Colossal Pouch @blu@from an @red@Runecrafting Casket.");
				Discord.writeDropsSyncMessage(""+player.getLoginName()+" has received an Colossal Pouch from an Runecrating Casket.");
			}
		}

		if (Misc.hasOneOutOf(200)) {//Abyssal protector
				player.getItems().addItemUnderAnyCircumstance(26901, 1);
				player.sendMessage("@red@You get lucky and receive the Abyssal protector pet.");
				Announcement.announce("@red@"+player.getLoginName()+" @org@has received the @red@Abyssal Protector @org@from an @red@Runecrafting Casket.");
				Discord.writeDropsSyncMessage(""+player.getLoginName()+" has received the Abyssal Protector from an Runecrating Casket.");
		}
	}

}
