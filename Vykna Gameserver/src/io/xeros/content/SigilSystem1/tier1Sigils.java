package io.xeros.content.SigilSystem1;


import io.xeros.model.entity.player.Player;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 *
 * @author C.T for RuneRogue
 *
 */

public class tier1Sigils {

	static int ALLGODWARSKC = 500; // 10% Drop rate perk kc
	static int HUNLLEF = 25; // Crystal key perk kc
	static int KRAKEN = 500; // Magic strength perk kc
	static int NIGHTMARE = 150; // Half Hit perk kc
	static int ALLGODWARSKC1 = 2000; // 20% Drop rate perk kc
	static int NEX = 100; // Restore hp perk kc
	static int BARRELCHEST = 250; // Coin bags perk kc
	static int KALPHITE = 350; // Range strength perk kc
	static int SARACHNIS = 50; // Resource perk kc
	static int CORPBEAST = 100; // Melee strength perk kc
	static int LIZARDSHAWMAN = 150; // Clue scroll perk kc

	public static List<Integer> getLines() {
		return IntStream.range(60562, 60562 + 70).boxed().collect(Collectors.toList());
	}

	
	public static void openInterface(Player player) {
		List<Integer> lines = getLines();
		int index = 0;

		if (player.getDropRateBonus() == true) {
			player.getPA().sendFrame126("+ 10% Drop-rate Bonus: @gre@Active", lines.get(index++));
		} else {
			player.getPA().sendFrame126("+ 10% Drop-rate Bonus: @red@Inactive", lines.get(index++));
		}

		player.getPA().sendString("", lines.get(index++));

		if (player.getCrystalKeyPerk() == true) {
			player.getPA().sendFrame126("Collect CKeys & 25% Chance to Double: @gre@Active", lines.get(index++));
		} else {
			player.getPA().sendFrame126("Collect CKeys & 25% Chance to Double: @red@Inactive", lines.get(index++));
		}

		player.getPA().sendString("", lines.get(index++));

		if (player.getRangeStrengthPerk() == true) {
			player.getPA().sendFrame126("+ 10% Range Strength Bonus in Pvm: @gre@Active", lines.get(index++));
		} else {
			player.getPA().sendFrame126("+ 10% Range Strength Bonus in Pvm: @red@Inactive", lines.get(index++));
		}

		player.getPA().sendString("", lines.get(index++));

		if (player.getHalfHitPerk() == true) {
			player.getPA().sendFrame126("10% Chance half your hit is rest into pray: @gre@Active", lines.get(index++));
		} else {
			player.getPA().sendFrame126("10% Chance half your hit is rest into pray: @red@Inactive", lines.get(index++));
		}

		player.getPA().sendString("", lines.get(index++));

		if (player.getDropRateBonus20() == true) {
			player.getPA().sendFrame126("+ 20% Drop-rate Bonus: @gre@Active", lines.get(index++));
		} else {
			player.getPA().sendFrame126("+ 20% Drop-rate Bonus: @red@Inactive", lines.get(index++));
		}

		player.getPA().sendString("", lines.get(index++));

		if (player.getDamageTakenPerk() == true) {
			player.getPA().sendFrame126("10% Chance your hit will Restore HP: @gre@Active", lines.get(index++));
		} else {
			player.getPA().sendFrame126("10% Chance your hit will Restore HP: @red@Inactive", lines.get(index++));
		}

		player.getPA().sendString("", lines.get(index++));

		if (player.getCoinBagPerk() == true) {
			player.getPA().sendFrame126("Collect Coin Bags % 25% Chance to Double: @gre@Active", lines.get(index++));
		} else {
			player.getPA().sendFrame126("Collect Coin Bags % 25% Chance to Double: @red@Inactive", lines.get(index++));
		}

		player.getPA().sendString("", lines.get(index++));

		if (player.getMagicStrengthPerk() == true) {
			player.getPA().sendFrame126("+ 10% Magic Strength Bonus in Pvm: @gre@Active", lines.get(index++));
		} else {
			player.getPA().sendFrame126("+ 10% Magic Strength Bonus in Pvm: @red@Inactive", lines.get(index++));
		}

		player.getPA().sendString("", lines.get(index++));

		if (player.getResourceBoxPerk() == true) {
			player.getPA().sendFrame126("Collect Resource Box & 25% Chance to Double: @gre@Active", lines.get(index++));
		} else {
			player.getPA().sendFrame126("Collect Resource Box & 25% Chance to Double: @red@Inactive", lines.get(index++));
		}

		player.getPA().sendString("", lines.get(index++));

		if (player.getMeleeStrengthPerk() == true) {
			player.getPA().sendFrame126("+ 10% Melee Strength Bonus in Pvm: @gre@Active", lines.get(index++));
		} else {
			player.getPA().sendFrame126("+ 10% Melee Strength Bonus in Pvm: @red@Inactive", lines.get(index++));
		}

		player.getPA().sendString("", lines.get(index++));

		if (player.getClueScrollPerk() == true) {
			player.getPA().sendFrame126("Collect Clue Scrolls & 25% Chance to Double: @gre@Active", lines.get(index++));
		} else {
			player.getPA().sendFrame126("Collect Clue Scrolls & 25% Chance to Double: @red@Inactive", lines.get(index++));
		}

		player.getPA().sendString("", lines.get(index++));

		player.getPA().sendFrame126("        -> Reset Advanced Perks: <-", lines.get(index++));

		player.getPA().sendString("", lines.get(index++));

		while (index < lines.size()) {
			player.getPA().sendString("", lines.get(index++));
		}

	}

	public static boolean handleButton(Player player, int actionButtonId) {

			switch (actionButtonId) {

				case 236146://10% DROP RATE BONUS UNLOCK
					if (player.getDropRateBonus20() == true) {
						player.sendMessage("@red@You can not activate this perk with + 20 Drop-rate boost active!");
						return false;
					}

					if (player.getDropRateBonus() == true) {
						player.sendMessage("@red@You already have this perk activated!");
						return false;
					}
					if (player.getNpcDeathTracker().getKc("general graardor") >= ALLGODWARSKC
						|| player.getNpcDeathTracker().getKc("k'ril tsutsaroth") >= ALLGODWARSKC
						|| player.getNpcDeathTracker().getKc("kree'arra") >= ALLGODWARSKC
						|| player.getNpcDeathTracker().getKc("commander zilyana") >= ALLGODWARSKC) {
						player.sendMessage("@blu@You have activated the + 10% drop-rate bonus perk!");
						player.getPA().sendFrame126("+ 10% Drop-rate Bonus: @gre@Active", 60562);
						player.setDropRateBonus(true);
						return true;
					}
					player.sendMessage("@red@You will need "+ALLGODWARSKC+" kill count of any of the Godwars Bosses to activate the 10%");
					player.sendMessage("@red@drop-rate bonus perk.");
					player.setDropRateBonus(false);
					return false;



				case 236148://Crystal key UNLOCK
					if (player.getCrystalKeyPerk() == true) {
						player.sendMessage("@red@You already have this perk activated!");
						return false;
					}
					if (player.getNpcDeathTracker().getKc("crystalline hunllef") >= HUNLLEF) {
						player.sendMessage("@blu@You have activated the crystal key perk!");
						player.getPA().sendFrame126("Collect Crystal Keys & 25% Chance to Double: @gre@Active", 60564);
						player.setCrystalKeyPerk(true);
						return true;
					}
					player.sendMessage("@red@You will need to kill "+HUNLLEF+"+ Hunllef to activate this perk.");
					player.setCrystalKeyPerk(false);
					return false;


				case 236150://Range strength perk UNLOCK
					if (player.getRangeStrengthPerk() == true) {
						player.sendMessage("@red@You already have this perk activated!");
						return false;
					}
					if (player.getNpcDeathTracker().getKc("kalphite queen") >= KALPHITE) {
						player.sendMessage("@blu@You have activated the + 10% Range Strength Bonus in Pvm perk!");
						player.getPA().sendFrame126("+ 10% Range Strength Bonus in Pvm: @gre@Active", 60566);
						player.setRangeStrengthPerk(true);
						return true;
					}
					player.sendMessage("@red@You will need to kill "+KALPHITE+"+ Kalphite queen to activate this perk.");
					player.setRangeStrengthPerk(false);
					return false;


				case 236152://Half hit perk UNLOCK
					if (player.getHalfHitPerk() == true) {
						player.sendMessage("@red@You already have this perk activated!");
						return false;
					}
					if (player.getNpcDeathTracker().getKc("the nightmare") >= NIGHTMARE) {
						player.sendMessage("@blu@You have activated the 10% Chance half your hit is restored into pray perk!");
						player.getPA().sendFrame126("10% Chance half your hit is restored into pray: @gre@Active", 60568);
						player.setHalfHitPerk(true);
						return true;
					}
					player.sendMessage("@red@You will need to kill "+NIGHTMARE+"+ Nightmare to activate this perk.");
					player.setHalfHitPerk(false);
					return false;

				case 236154://20% DROP RATE BONUS UNLOCK
					if (player.getDropRateBonus() == true) {
						player.sendMessage("@red@You can not activate this perk with + 10 Drop-rate boost active!");
						return false;
					}

					if (player.getDropRateBonus20() == true) {
						player.sendMessage("@red@You already have this perk activated!");
						return false;
					}
					if (player.getNpcDeathTracker().getKc("general graardor") >= ALLGODWARSKC1
							|| player.getNpcDeathTracker().getKc("k'ril tsutsaroth") >= ALLGODWARSKC1
							|| player.getNpcDeathTracker().getKc("kree'arra") >= ALLGODWARSKC1
							|| player.getNpcDeathTracker().getKc("commander zilyana") >= ALLGODWARSKC1) {
						player.sendMessage("@blu@You have activated the + 20% drop-rate bonus perk!");
						player.getPA().sendFrame126("+ 20% Drop-rate Bonus: @gre@Active", 60570);
						player.setDropRateBonus20(true);
						return true;
					}
					player.sendMessage("@red@You will need "+ALLGODWARSKC1+"+ kill count of any of the Godwars Bosses to activate the 20%");
					player.sendMessage("@red@droprate bonus perk.");
					player.setDropRateBonus20(false);
					return false;

				case 236156://Damage taken UNLOCK
					if (player.getDamageTakenPerk() == true) {
						player.sendMessage("@red@You already have this perk activated!");
						return false;
					}
					//if (player.getNpcDeathTracker().getKc("NAME HERE") >= 4000) {
					if (player.nexKills >= NEX) {
						player.sendMessage("@blu@You have activated the 10% Chance your hit will Restore HP perk!");
						player.getPA().sendFrame126("10% Chance your hit will Restore HP: @gre@Active", 60572);
						player.setDamageTakenPerk(true);
						return true;
					}
					player.sendMessage("@red@You will need "+NEX+"+ kill count of Nex to activate the 10%");
					player.sendMessage("@red@Chance your hit will Restore HP perk.");
					player.setDamageTakenPerk(false);
					return false;

				case 236158://Coin bag UNLOCK
					if (player.getCoinBagPerk() == true) {
						player.sendMessage("@red@You already have this perk activated!");
						return false;
					}
					if (player.getNpcDeathTracker().getKc("barrelchest") >= BARRELCHEST) {
						player.sendMessage("@blu@You have activated the Collect Coin Bags % 25% Chance to Double perk!");
						player.getPA().sendFrame126("Collect Coin Bags % 25% Chance to Double: @gre@Active", 60574);
						player.setCoinBagPerk(true);
						return true;
					}
					player.sendMessage("@red@You will need "+BARRELCHEST+"+ kill count of Barrel Chest to activate the Collect");
					player.sendMessage("@red@Coin Bags % 25% Chance to Double perk.");
					player.setCoinBagPerk(false);
					return false;

				case 236160://Magic strength UNLOCK
					if (player.getMagicStrengthPerk() == true) {
						player.sendMessage("@red@You already have this perk activated!");
						return false;
					}
					if (player.getNpcDeathTracker().getKc("kraken") >= KRAKEN) {
						player.sendMessage("@blu@You have activated the + 10% Magic Strength Bonus in Pvm perk!");
						player.getPA().sendFrame126("+ 10% Magic Strength Bonus in Pvm: @gre@Active", 60576);
						player.setMagicStrengthPerk(true);
						return true;
					}
					player.sendMessage("@red@You will need "+KRAKEN+"+ kill count of Kraken to activate the + 10");
					player.sendMessage("@red@Magic Strength Bonus in Pvm perk.");
					player.setMagicStrengthPerk(false);
					return false;

				case 236162://Resourcebox UNLOCK
					if (player.getResourceBoxPerk() == true) {
						player.sendMessage("@red@You already have this perk activated!");
						return false;
					}
					if (player.getNpcDeathTracker().getKc("sarachnis") >= SARACHNIS) {
						player.sendMessage("@blu@You have activated the Collect Resource Box & 25% Chance to Double perk!");
						player.getPA().sendFrame126("Collect Resource Box & 25% Chance to Double: @gre@Active", 60578);
						player.setResourceBoxPerk(true);
						return true;
					}
					player.sendMessage("@red@You will need "+SARACHNIS+"+ kill count of Sarachnis to activate the collect");
					player.sendMessage("@red@Resource Box & 25% Chance to Double perk.");
					player.setResourceBoxPerk(false);
					return false;

				case 236164://Melee strength UNLOCK
					if (player.getMeleeStrengthPerk() == true) {
						player.sendMessage("@red@You already have this perk activated!");
						return false;
					}
					if (player.getNpcDeathTracker().getKc("corporeal beast") >= CORPBEAST) {
						player.sendMessage("@blu@You have activated the + 10% Melee Strength Bonus in Pvm perk!");
						player.getPA().sendFrame126("+ 10% Melee Strength Bonus in Pvm: @gre@Active", 60580);
						player.setMeleeStrengthPerk(true);
						return true;
					}
					player.sendMessage("@red@You will need "+CORPBEAST+"+ kill count of Corporeal Beast to activate the + 10%");
					player.sendMessage("@red@Melee Strength Bonus in Pvm perk.");
					player.setMeleeStrengthPerk(false);
					return false;

				case 236166://Clue scroll UNLOCK
					if (player.getClueScrollPerk() == true) {
						player.sendMessage("@red@You already have this perk activated!");
						return false;
					}
					if (player.getNpcDeathTracker().getKc("lizardman shaman") >= LIZARDSHAWMAN) {
						player.sendMessage("@blu@You have activated the Collect Clue Scrolls & 25% Chance to Double perk!");
						player.getPA().sendFrame126("Collect Clue Scrolls & 25% Chance to Double: @gre@Active", 60582);
						player.setClueScrollPerk(true);
						return true;
					}
					player.sendMessage("@red@You will need "+LIZARDSHAWMAN+"+ kill count of Lizard Shamans to activate the Collect");
					player.sendMessage("@red@Clue Scrolls & 25% Chance to Double perk.");
					player.setClueScrollPerk(false);
					return false;

				case 236168://Reset all perks to not active
					player.sendMessage("@blu@You have reset all of the perks to inactive.");
					player.setClueScrollPerk(false);
					player.setMeleeStrengthPerk(false);
					player.setResourceBoxPerk(false);
					player.setMagicStrengthPerk(false);
					player.setCoinBagPerk(false);
					player.setDamageTakenPerk(false);
					player.setDropRateBonus20(false);
					player.setHalfHitPerk(false);
					player.setRangeStrengthPerk(false);
					player.setCrystalKeyPerk(false);
					player.setDropRateBonus(false);
					player.getPA().sendFrame126("Collect Clue Scrolls & 25% Chance to Double: @red@Inactive", 60582);
					player.getPA().sendFrame126("+ 10% Melee Strength Bonus in Pvm: @red@Inactive", 60580);
					player.getPA().sendFrame126("Collect Resource Box & 25% Chance to Double: @red@Inactive", 60578);
					player.getPA().sendFrame126("+ 10% Magic Strength Bonus in Pvm: @red@Inactive", 60576);
					player.getPA().sendFrame126("Collect Coin Bags % 25% Chance to Double: @red@Inactive", 60574);
					player.getPA().sendFrame126("10% Chance your hit will Restore HP: @red@Inactive", 60572);
					player.getPA().sendFrame126("+ 20% Drop-rate Bonus: @red@Inactive", 60570);
					player.getPA().sendFrame126("10% Chance half your hit is restored into pray: @red@Inactive", 60568);
					player.getPA().sendFrame126("+ 10% Range Strength Bonus in Pvm: @red@Inactive", 60566);
					player.getPA().sendFrame126("Collect Crystal Keys & 25% Chance to Double: @red@Inactive", 60564);
					player.getPA().sendFrame126("+ 10% Drop-rate Bonus: @red@Inactive", 60562);
					return true;




				}
		return false;
	}


}


