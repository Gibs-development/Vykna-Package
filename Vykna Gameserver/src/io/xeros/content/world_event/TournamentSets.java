package io.xeros.content.world_event;

import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

/**
 * Tournament quick set up layouts
 *
 * @author C.T,
 */
public class TournamentSets {

	public static int[][] pureTribridInventory =
			{
					{9185, 1},
					{10499, 1},
					{11936, 1},
					{2440, 1},
					{2497, 1},
					{5698, 1},
					{11936, 1},
					{2436, 1},
					{4587, 1},
					{3024, 1},
					{11936, 1},
					{2444, 1},
					{6685, 1},
					{3024, 1},
					{6685, 1}, // Saradomin brew(4)
					{11936, 1},
					{11936, 1},
					{11936, 1},
					{11936, 1},
					{11936, 1},
					{11936, 1},
					{11936, 1},
					{11936, 1},
					{11936, 1},
					{11936, 1},
					{555, 3000}, // water runes
					{560, 2000}, // death rune
					{565, 1000}, // blood rune
			};

	public static int[][] tournamentInventory(Player player , String type) {
		//{"Pure tribrid", "Berserker hybrid", "Main hybrid welfare", "Main hybrid barrows"};


		if (type.contains("Nh")) {

			int[][] inventory =
					{
							{12695, 1}, // Super combat potion(4)
							{10499, 1}, // Ava's accumulator
							{2497, 1}, // Black d'hide chaps
							{12006, 1}, // Abyssal tentacle
							{2444, 1}, // Ranging potion(4)
							{9185, 1}, // Rune crossbow
							{2503, 1}, // Black d'hide body
							{12954, 1}, // Dragon defender
							{11936, 1}, // Dark crab
							{11936, 1}, // Dark crab
							{11936, 1}, // Dark crab
							{Misc.hasPercentageChance(50) ? 20784 : 11802, 1}, // Armadyl godsword
							{3024, 1}, // Super restore(4)
							{3024, 1}, // Super restore(4)
							{3024, 1}, // Super restore(4)
							{3024, 1}, // Super restore(4)
							{6685, 1}, // Saradomin brew(4)
							{6685, 1}, // Saradomin brew(4)
							{6685, 1}, // Saradomin brew(4)
							{6685, 1}, // Saradomin brew(4)
							{11936, 1}, // Dark crab
							{11936, 1}, // Dark crab
							{11936, 1}, // Dark crab
							{11936, 1}, // Dark crab
							{11936, 1}, // Dark crab
							{555, 3000}, // water runes
							{560, 2000}, // death rune
							{565, 1000}, // blood rune
					};
			return inventory;
		} else {
			int[][] inventory =
					{
							{12695, 1}, // Super combat potion(4)
							{type.contains("barrows") ? 11936 : 2497, 1}, // Black d'hide chaps
							{type.startsWith("Berserker") ? 4587 : type.equals("Main hybrid barrows") ? 12006 : 4151, 1}, // Abyssal whip
							{type.contains("barrows") ? getRandomBarrowsMeleeBottom() : 1079, 1}, // Rune platelegs
							{3040, 1}, // Magic potion(4)
							{type.contains("barrows") ? 4736 : 2503, 1}, // Black d'hide body
							{type.startsWith("Berserker") ? 8850 : 12954, 1}, // Dragon defender
							{type.contains("barrows") ? getRandomBarrowsMeleeTop() : 10551, 1}, // Fighter torso
							{3024, 1}, // Super restore(4)
							{3024, 1}, // Super restore(4)
							{5698, 1}, // Dragon dagger p++
							{11936, 1}, // Dark crab
							{6685, 1}, // Saradomin brew(4)
							{6685, 1}, // Saradomin brew(4)
							{11936, 1}, // Dark crab
							{11936, 1}, // Dark crab
							{11936, 1}, // Dark crab
							{11936, 1}, // Dark crab
							{11936, 1}, // Dark crab
							{11936, 1}, // Dark crab
							{11936, 1}, // Dark crab
							{11936, 1}, // Dark crab
							{11936, 1}, // Dark crab
							{11936, 1}, // Dark crab
							{11936, 1}, // Dark crab
							{555, 3000}, // water runes
							{560, 2000}, // death rune
							{565, 1000}, // blood rune
					};
			return inventory;
		}
	}

	public static int[][] tournamentInventoryDharok(Player player , String type) {
		int[][] inventory =
				{
						{12695, 1}, // Super combat potion(4)
						{3024, 1}, // Super restore(4)
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{6685, 1}, // Saradomin brew(4)
						{3024, 1}, // Super restore(4)
						{6685, 1}, // Saradomin brew(4)
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{4718, 1}, // Dharok's greataxe
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{1187, 1}, // Dragon sq shield
						{5698, 1}, // Dragon dagger (p++)
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{557, 1000}, // earth runes
						{9075, 400}, // astral rune
						{560, 200}, // death rune
				};


		return inventory;
	}

	public static int[][] tournamentInventoryBarrowsBrid(Player player, String type ) {
		int[][] inventory =
				{
						{4718, 1}, // Dh axe
						{4720, 1}, // Dh body
						{4710, 1}, // Ahrim's staff
						{4712, 1}, // Ahrim's top
						{4716, 1}, // Dh helm
						{4722, 1}, // Dh legs
						{4708, 1}, // Ahrim's hood
						{4714, 1}, // Ahrim's skirt
						{11840, 1}, // Dragon boots
						{6737, 1}, // Berserker ring
						{6920, 1}, // Infinity boots
						{6731, 1}, // Seer's ring
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{12695, 1}, // Super combat potion(4)
						{2444, 1}, // Ranging potion(4)
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{6685, 1}, // Saradomin brew(4)
						{6685, 1}, // Saradomin brew(4)
						{20784, 1}, // Dragon claws
						{11936, 1}, // Dark crab
						{3024, 1}, // Super restore(4)
						{3024, 1}, // Super restore(4)
						{11936, 1}, // Dark crab
						{555, 3000}, // water runes
						{560, 2000}, // death rune
						{565, 1000}, // blood rune
				};


		return inventory;
	}

	public static int[][] tournamentInventoryF2p(Player player, String type ) {
		int[][] inventory =
				{
						{113, 1},
						{1319, 1},
						{1333, 1},
						{1725, 1},
						{2297, 1},
						{2297, 1},
						{2297, 1},
						{2297, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1},
						{373, 1}
				};
		return inventory;
	}


	public static int getRandomBarrowsMeleeTop() {
		return barrowsMeleeTop[Misc.random(barrowsMeleeTop.length - 1)];
	}

	public static int getRandomBarrowsMeleeBottom() {
		return barrowsMeleeBottom[Misc.random(barrowsMeleeBottom.length - 1)];
	}

	public static int getRandomSerpHelm() {
		return serpHelm[Misc.random(serpHelm.length - 1)];
	}

	private final static int[] serpHelm =
			{13197, 13199, 12931,};

	private final static int[] barrowsMeleeTop =
			{
					4720, // Dharok's platebody
					4728, // Guthan's platebody
					4749, // Torag's platebody
			};

	private final static int[] barrowsMeleeBottom =
			{
					4722, // Dharok's platelegs
					4730, // Guthan's chainskirt
					4751, // Torag's platelegs
					4759, // Verac's plateskirt
			};


	public static int[][] tournamentInventoryMaxBrid(Player player, String type) {
		int[][] inventory =
				{
						{12695, 1}, // Super combat potion(4)
						{19553, 1}, // Amulet of torture
						{12006, 1}, // Abyssal tentacle
						{11834, 1}, // Bandos tassets
						{3040, 1}, // Magic potion(4)
						{13239, 1}, // Primordial boots
						{12954, 1}, // Dragon defender
						{11832, 1}, // Bandos chestplate
						{3024, 1}, // Super restore(4)
						{3024, 1}, // Super restore(4)
						{Misc.hasOneOutOf(2) ? 11802 : 20784, 1}, // Armadyl godsword
						{4736, 1}, // Karil's leathertop
						{6685, 1}, // Saradomin brew(4)
						{6685, 1}, // Saradomin brew(4)
						{6685, 1}, // Saradomin brew(4)
						{3024, 1}, // Super restore(4)
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{555, 3000}, // water runes
						{560, 2000}, // death rune
						{565, 1000}, // blood rune
				};


		return inventory;
	}


	public static int[][] tournamentInventoryVestaMelee(Player player , String type) {
		int[][] inventory =
				{
						{12695, 1}, // Super combat potion(4)
						{3024, 1}, // Super restore(4)
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{6685, 1}, // Saradomin brew(4)
						{3024, 1}, // Super restore(4)
						{6685, 1}, // Saradomin brew(4)
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{12006, 1}, // tentacle whip
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{22322, 1}, // Avernic defender
						{5698, 1}, // Dragon dagger (p++)
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{11936, 1}, // Dark crab
						{557, 1000}, // earth runes
						{9075, 400}, // astral rune
						{560, 200}, // death rune
				};


		return inventory;
	}




}
