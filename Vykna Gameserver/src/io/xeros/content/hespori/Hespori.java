package io.xeros.content.hespori;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.event.eventcalendar.EventChallenge;
import io.xeros.content.leaderboards.LeaderboardType;
import io.xeros.content.leaderboards.LeaderboardUtils;
import io.xeros.content.skills.Skill;
import io.xeros.model.Items;
import io.xeros.model.definitions.NpcStats;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;

import static io.xeros.content.combat.Hitmark.HIT;


public class Hespori {

	public static final int WELL_OF_GOODWILL = 6097;

	private static final List<HesporiBonus> bonuses = Lists.newArrayList(new AttasBonus(), new IasorBonus(), new KronosBonus(),
			new BuchuBonus(), new CelastrusBonus(), new GolparBonus(), new KeldaBonus(), new NoxiferBonus(), new ConsecrationBonus());

	public static final int[] HESPORI_RARE_SEEDS = {
					HesporiBonusPlant.KRONOS.getItemId(),
					HesporiBonusPlant.IASOR.getItemId(),
					HesporiBonusPlant.ATTAS.getItemId(),
					HesporiBonusPlant.KELDA.getItemId(),
					HesporiBonusPlant.NOXIFER.getItemId(),
					HesporiBonusPlant.BUCHU.getItemId(),
					HesporiBonusPlant.CELASTRUS.getItemId(),
					HesporiBonusPlant.GOLPAR.getItemId(),
					HesporiBonusPlant.CONSECRATION.getItemId()
	};



	private static void plant(Player player, HesporiBonus hesporiBonus) {
		if (!player.getItems().playerHasItem(hesporiBonus.getPlant().getItemId())) {
			player.sendMessage("You don't have any seeds to donate.");
			player.getPA().removeAllWindows();
			return;
		}

		if (hesporiBonus.canPlant(player)) {
			player.getItems().deleteItem(hesporiBonus.getPlant().getItemId(), 1);
			hesporiBonus.activate(player);
			hesporiBonus.updateObject(true);
			player.donationSeed+=1;
			if (!player.getMode().isOsrs() && !player.getMode().is5x()) {
				player.getPA().addSkillXP(250000 , Skill.FARMING.getId(), true);
			} else {
				player.getPA().addSkillXP(50000 , Skill.FARMING.getId(), true);
			}

			if (Misc.random(10) == 1) {
				player.getItems().addItemUnderAnyCircumstance(4589, 1);
				PlayerHandler.executeGlobalMessage("@red@ "+player.getLoginName()+" @pur@has received an @red@Glod Key @pur@from @red@donating @pur@an Seed to the @red@Well");

			}
			if (Misc.random(12) == 1) {
				player.getItems().addItemUnderAnyCircumstance(22428, 1);
				PlayerHandler.executeGlobalMessage("@red@ "+player.getLoginName()+" @pur@has received an @red@Solak Key @pur@from @red@donating @pur@an Seed to the @red@Well");

			}
			if (Misc.random(30) == 1) {
				player.getItems().addItemUnderAnyCircumstance(990, 5);
				PlayerHandler.executeGlobalMessage("@red@ "+player.getLoginName()+" @pur@has received an @red@5x Crystal Key @pur@from @red@donating @pur@an Seed to the @red@Well");

			}
			if (Misc.random(25) == 1) {
				player.getItems().addItemUnderAnyCircumstance(995, 600000);
				PlayerHandler.executeGlobalMessage("@red@ "+player.getLoginName()+" @pur@has received @red@600k Coins @pur@from @red@donating @pur@an Seed to the @red@Well");

			}
			if (Misc.random(27) == 1) {
				player.getItems().addItemUnderAnyCircumstance(21046, 3);
				PlayerHandler.executeGlobalMessage("@red@ "+player.getLoginName()+" @pur@has received @red@3x Chest Rate Bonus @pur@from @red@donating @pur@an Seed to the @red@Well");

			}
			if (Misc.random(31) == 1) {
				player.getItems().addItemUnderAnyCircumstance(995, 1000000);
				PlayerHandler.executeGlobalMessage("@red@ "+player.getLoginName()+" @pur@has received @red@1m Coins @pur@from @red@donating @pur@an Seed to the @red@Well");

			}
			player.getItems().addItemUnderAnyCircumstance(4185, 1);
			player.getItems().addItemUnderAnyCircumstance(21046, 1);

		}
	}






	public static boolean clickObject(Player player, int objectId) {
		if (objectId == WELL_OF_GOODWILL) {
			List<HesporiBonus> list = bonuses.stream().filter(bonus -> player.getItems().playerHasItem(bonus.getPlant().getItemId())).collect(Collectors.toList());

			if (list.isEmpty()) {
				player.sendMessage("You need a bonus seed to use here.");
			} else if (list.size() > 1) {
				player.sendMessage("You have too many @gre@bonus seeds@bla@ please bring only 1 seed type.");
			} else {
				plant(player, list.get(0));
			}
		}

		return false;
	}



	/**
	 * Hespori Seeds Bonus Time Handling
	 */

	public static long ATTAS_TIMER, KRONOS_TIMER, IASOR_TIMER, GOLPAR_TIMER, BUCHU_TIMER, NOXIFER_TIMER, KELDA_TIMER, CELASTRUS_TIMER, CONSECRATION_TIMER;
	public static boolean activeAttasSeed = false;
	public static boolean activeKronosSeed = false;
	public static boolean activeIasorSeed = false;
	public static boolean activeBuchuSeed = false;
	public static boolean activeNoxiferSeed = false;
	public static boolean activeGolparSeed = false;
	public static boolean activeKeldaSeed = false;
	public static boolean activeCelastrusSeed = false;
	public static boolean activeConsecrationSeed = false;
	public static int chosenSkillid;

	public static String getSaveFile() {
		return Server.getSaveDirectory() + "hespori_seed_bonuses.txt";
	}

	public static void init() {
		try {
			File f = new File(getSaveFile());
			if (!f.exists()) {
				Preconditions.checkState(f.createNewFile());
			}
			Scanner sc = new Scanner(f);
			int i = 0;
			while(sc.hasNextLine()){
				i++;
				String line = sc.nextLine();
				String[] details = line.split("=");
				String amount = details[1];

				switch (i) {
					case 1:
						ATTAS_TIMER = (int) Long.parseLong(amount);
						break;
					case 2:
						KRONOS_TIMER = (int) Long.parseLong(amount);
						break;
					case 3:
						IASOR_TIMER = (int) Long.parseLong(amount);
						break;
					case 4:
						KELDA_TIMER = (int) Long.parseLong(amount);
						break;
					case 5:
						CELASTRUS_TIMER = (int) Long.parseLong(amount);
						break;
					case 6:
						NOXIFER_TIMER = (int) Long.parseLong(amount);
						break;
					case 7:
						BUCHU_TIMER = (int) Long.parseLong(amount);
						break;
					case 8:
						GOLPAR_TIMER = (int) Long.parseLong(amount);
						break;
					case 9:
						CONSECRATION_TIMER = (int) Long.parseLong(amount);
						break;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String activeAnimaBonus() {
		if (Hespori.ATTAS_TIMER > 0) {
			return "Anima: @gre@Attas [Bonus XP]";
		}
		if (Hespori.KRONOS_TIMER > 0) {
			return "Anima: @gre@Kronos [x2 Raids 1 Keys]";
		}
		if (Hespori.IASOR_TIMER > 0) {
			return "Anima: @gre@Iasor [+10% DR]";
		}
		return "Anima: @red@None";
	}

}

