package io.xeros.content.skills.mining;

import io.xeros.Server;
import io.xeros.content.SkillcapePerks;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.achievement_diary.impl.DesertDiaryEntry;
import io.xeros.content.achievement_diary.impl.FaladorDiaryEntry;
import io.xeros.content.achievement_diary.impl.FremennikDiaryEntry;
import io.xeros.content.achievement_diary.impl.KaramjaDiaryEntry;
import io.xeros.content.achievement_diary.impl.LumbridgeDraynorDiaryEntry;
import io.xeros.content.achievement_diary.impl.VarrockDiaryEntry;
import io.xeros.content.achievement_diary.impl.WildernessDiaryEntry;
import io.xeros.content.bosspoints.BossPoints;
import io.xeros.content.dailytasks.DailyTasks;
import io.xeros.content.hespori.Hespori;
import io.xeros.content.shooting_star.ShootingStar;
import io.xeros.content.skills.Skill;
import io.xeros.content.skills.smithing.Smelting;
import io.xeros.model.cycleevent.Event;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Location3D;
import io.xeros.util.Misc;
import org.apache.commons.lang3.RandomUtils;

/**
 * Represents a singular event that is executed when a player attempts to mine.
 * 
 * @author Jason MacKeigan
 * @date Feb 18, 2015, 6:17:11 PM
 */
public class MiningEvent extends Event<Player> {
	
	public static int[] prospectorOutfit = { 12013, 12014, 12015, 12016 };

	/**
	 * The amount of cycles that must pass before the animation is updated
	 */
	private final int ANIMATION_CYCLE_DELAY = 3;

	/**
	 * The value in cycles of the last animation
	 */
	private int lastAnimation;

	/**
	 * The pickaxe being used to mine
	 */
	private final Pickaxe pickaxe;

	/**
	 * The mineral being mined
	 */
	private final Mineral mineral;

	/**
	 * The object that we are mning
	 */
	private int objectId;

	/**
	 * The location of the object we're mining
	 */
	private final Location3D location;

	/**
	 * The npc the player is mining, if any
	 */
	private NPC npc;

	/**
	 * Constructs a new {@link MiningEvent} for a single player
	 * 
	 * @param attachment the player this is created for
	 * @param objectId the id value of the object being mined from
	 * @param location the location of the object being mined from
	 * @param mineral the mineral being mined
	 * @param pickaxe the pickaxe being used to mine
	 */
	public MiningEvent(Player attachment, int objectId, Location3D location, Mineral mineral, Pickaxe pickaxe, int time) {
		super("skilling", attachment, time);
		this.objectId = objectId;
		this.location = location;
		this.mineral = mineral;
		this.pickaxe = pickaxe;
	}

	/**
	 * Constructs a new {@link MiningEvent} for a single player
	 * 
	 * @param attachment the player this is created for
	 * @param npc the npc being from from
	 * @param location the location of the npc
	 * @param mineral the mineral being mined
	 * @param pickaxe the pickaxe being used to mine
	 */
	public MiningEvent(Player attachment, NPC npc, Location3D location, Mineral mineral, Pickaxe pickaxe, int time) {
		super("skilling", attachment, time);
		this.npc = npc;
		this.location = location;
		this.mineral = mineral;
		this.pickaxe = pickaxe;
	}

	@Override
	public void update() {
		if (attachment == null || attachment.isDisconnected() || attachment.getSession() == null) {
			stop();
			return;
		}
		if (!attachment.getItems().playerHasItem(pickaxe.getItemId()) && !attachment.getItems().isWearingItem(pickaxe.getItemId())) {
			attachment.sendMessage("That is strange! The pickaxe could not be found.");
			stop();
			return;
		}
		if (attachment.getItems().freeSlots() == 0) {
			attachment.getDH().sendStatement("You have no more free slots.");
			stop();
			return;
		}
		if (RandomUtils.nextInt(1, 300) == 1 && attachment.getInterfaceEvent().isExecutable()) {
			attachment.getInterfaceEvent().execute();
			stop();
			return;
		}
		if (objectId > 0) {
			if (Server.getGlobalObjects().exists(Mineral.EMPTY_VEIN, location.getX(), location.getY(), location.getZ()) && mineral.isDepletable()) {
				attachment.sendMessage("This vein contains no more minerals.");
				stop();
				return;
			}
		} else {
			if (npc == null || npc.isDead()) {
				attachment.sendMessage("This vein contains no more minerals.");
				stop();
				return;
			}
		}
		if (super.getElapsedTicks() - lastAnimation > ANIMATION_CYCLE_DELAY) {
			attachment.startAnimation(pickaxe.getAnimation());
			lastAnimation = super.getElapsedTicks();
		}
	}
	private static void foeArtefact(Player player) {
		int chance = 250;
		int artefactRoll = Misc.random(100);
		if (Misc.random(chance) == 1) {
			if (artefactRoll <65) {//1/300
				if (player.getItems().isWearingItem(21126)) { // Hazelmere signut author C.T
					player.sendMessage("<col=ff7000>The power of Hazelmere discovers an coin for the foe and sends it to the bank!</col>");
					player.getItems().addItemToBankOrDrop(11180, 1);
					return;
				}
				player.getItems().addItemUnderAnyCircumstance(11180, 1);//ancient coin foe for 200
				player.sendMessage("You found a coin that can be used in the Fire of Exchange!");
			} else if (artefactRoll >= 65 && artefactRoll < 99) {//1/600
				if (player.getItems().isWearingItem(21126)) { // Hazelmere signut author C.T
					player.sendMessage("<col=ff7000>The power of Hazelmere discovers an talisman for the foe and sends it to the bank!</col>");
					player.getItems().addItemToBankOrDrop(681, 1);
					return;
				}
				player.getItems().addItemUnderAnyCircumstance(681, 1);//anicent talisman foe for 300
				player.sendMessage("You found a talisman that can be used in the Fire of Exchange!");
			} else if (artefactRoll > 99){//1/1000
				if (player.getItems().isWearingItem(21126)) { // Hazelmere signut author C.T
					player.sendMessage("<col=ff7000>The power of Hazelmere discovers an Golden statuette for the foe and sends it to the bank!</col>");
					player.getItems().addItemToBankOrDrop(9034, 1);
					return;
				}
				player.getItems().addItemUnderAnyCircumstance(9034, 1);//golden statuette foe for 500
				PlayerHandler.executeGlobalMessage("@bla@[@red@Mining@bla@]@blu@ " + player.getDisplayName() + " @red@just found a Golden statuette while mining!");
			}
		}
	}
	@Override
	public void execute() {
		double osrsExperience = 0;
		int pieces = 0;
		for (int i = 0; i < prospectorOutfit.length; i++) {
			if (attachment.getItems().isWearingItem(prospectorOutfit[i])) {
				pieces += 6;
			}
		}
		if (attachment == null || attachment.isDisconnected() || attachment.getSession() == null) {
			stop();
			return;
		}
		if (mineral.isDepletable()) {
			if (RandomUtils.nextInt(0, mineral.getDepletionProbability()) == 0
					|| mineral.getDepletionProbability() == 0) {
				if (objectId > 0) {
					//if (objectId == 17196) {//Shooting star
					//	if (Server.amountMined >= Server.miningAmount)
					//	ShootingStar.spawned = false;
					//	Server.getGlobalObjects().add(new GlobalObject(-1, location.getX(), location.getY(), location.getZ(), 0, 10, -1, -1));
					//	ShootingStar.setCurrentLocation(null);
					//	attachment.sendMessage("@dre@The shooting star has vanished. It's expected to spawn again within the hour!");
					//	PlayerHandler.executeGlobalMessage("@cr17@ @red@The shooting star has vanished. It's expected to spawn again within the hour!");
					//	stop();
					//	return;
				//}
					Server.getGlobalObjects().add(new GlobalObject(Mineral.EMPTY_VEIN, location.getX(), location.getY(),
							location.getZ(), 0, 10, mineral.getRespawnRate(), objectId));
				} else {
					npc.setDead(true);
					npc.actionTimer = 0;
					npc.needRespawn = false;
				}
			}
		}
		attachment.facePosition(location.getX(), location.getY());
		Achievements.increase(attachment, AchievementType.MINE, 1);
		foeArtefact(attachment);
		if (Boundary.isIn(attachment, Boundary.RESOURCE_AREA)) {
			if (Misc.random(20) == 5) {
				int randomAmount = 1;
				attachment.sendMessage("You received " + randomAmount + " pkp while mining!");
				attachment.getItems().addItem(2996, randomAmount);
			}
		}
		
		/**
		 * Experience calculation
		 */
		osrsExperience = mineral.getExperience() + mineral.getExperience() / 10 * pieces;
		attachment.getPA().addSkillXPMultiplied((int) osrsExperience, Skill.MINING.getId(), true);
		switch (mineral) {
			case ADAMANT:
				break;
			case COAL:
				if (Boundary.isIn(attachment, Boundary.RELLEKKA_BOUNDARY)) {
					attachment.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.MINE_COAL_FREM);
				}
				break;
			case COPPER:
				break;
			case ESSENCE:
				attachment.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.MINE_ESSENCE);
				break;
			case GEM:
				attachment.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.MINE_GEM_FAL);
				DailyTasks.increase(attachment, DailyTasks.PossibleTasks.GEM_ROCK);
				break;
			case GOLD:
				if (Boundary.isIn(attachment, Boundary.TZHAAR_CITY_BOUNDARY)) {
					attachment.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.MINE_GOLD_KAR);
				}
				if (Boundary.isIn(attachment, Boundary.RELLEKKA_BOUNDARY)) {
				}
				break;
			case IRON:
				if (attachment.getPosition().inWild()) {
					attachment.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.MINE_IRON_WILD);
				}
				if (Boundary.isIn(attachment, Boundary.VARROCK_BOUNDARY)) {
					attachment.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.MINE_IRON);
				}
				if (Boundary.isIn(attachment, Boundary.AL_KHARID_BOUNDARY)) {
					attachment.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.MINE_IRON_LUM);
				}
				break;
			case MITHRIL:
				if (attachment.getPosition().inWild()) {
					attachment.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.MINE_MITHRIL_WILD);
				}
				DailyTasks.increase(attachment, DailyTasks.PossibleTasks.MITHRIL_ORES);//daily tasks
				break;
			case TIN:
				break;
			case CLAY:
				attachment.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.MINE_CLAY);
				break;

			case SHOOTING_STAR:
				if (Server.amountMined >= Server.miningAmount){

				ShootingStar.spawned = false;
				Server.getGlobalObjects().add(new GlobalObject(-1, location.getX(), location.getY(), location.getZ(), 0, 10, -1, -1));
				ShootingStar.setCurrentLocation(null);
				if(ShootingStar.spawned = false) {
					attachment.sendMessage("@dre@The shooting star has vanished. It's expected to spawn again within the hour!");
					PlayerHandler.executeGlobalMessage("@cr17@ @red@The shooting star has vanished. It's expected to spawn again within the hour!");
					return;
					}

				stop();
				return;
		        }
				if (Misc.random(8) == 1) {
					stop();
					return;
				}
				if (Misc.random(5) == 2) {
					attachment.getItems().addItem(452, 1 + Misc.random(2));
					attachment.sendMessage("@dre@ You found some runite ores from inside the star!");
				}
				if (Misc.random(8) == 2) {
					attachment.getItems().addItem(2364, 1 + Misc.random(5));
					attachment.sendMessage("@dre@ You found some runite bars from inside the star!");
				}
				if (Misc.random(14) == 2) {
					attachment.getItems().addItem(9144, 1 + Misc.random(20));
					attachment.sendMessage("@dre@ You found some runite bolts from inside the star!");
				}
				if (Misc.random(14) == 2) {
					attachment.getItems().addItem(9144, 1 + Misc.random(20));
					attachment.sendMessage("@dre@ You found some runite bolts from inside the star!");
				}
				if (Misc.random(50) == 2) {
					attachment.getItems().addItem(1127, 1);
					attachment.sendMessage("@dre@ You found an rune body from inside the star!");
				}
				if (Misc.random(120) == 2) {
					attachment.getItems().addItem(537, 10);
					attachment.sendMessage("@dre@ You found 10 dragon bones from inside the star!");
				}
				if (Misc.random(77) == 2) {
					attachment.getItems().addItem(533, 30);
					attachment.sendMessage("@dre@ You found 30 big bones from inside the star!");
				}
				if (Misc.random(65) == 2) {
					attachment.getItems().addItem(11937, 12);
					attachment.sendMessage("@dre@ You found 12 dark crab from inside the star!");
				}
				if (Misc.random(98) == 2) {
					attachment.getItems().addItem(11730, 1);
					attachment.sendMessage("@dre@ You found an overload from inside the star!");
				}
				if (Misc.random(200) == 2) {
					attachment.getItems().addItem(4589, 1);
					attachment.sendMessage("@dre@ You found an glod key from inside the star!");
					PlayerHandler.executeGlobalMessage("@cr17@@blu@"+attachment.getLoginName()+" @red@Has received an glod key from the shooting star.");
				}
				if (Misc.random(290) == 2) {
					BossPoints.addPoints(attachment, 1, false);
					attachment.sendMessage("@dre@ You found some boss points from inside the star!");
					PlayerHandler.executeGlobalMessage("@cr17@@blu@"+attachment.getLoginName()+" @red@Has received 1 boss point from the shooting star.");
				}
				if (Misc.random(360) == 2) {
					BossPoints.addPoints(attachment, 3, false);
					attachment.sendMessage("@dre@ You found some boss points from inside the star!");
					PlayerHandler.executeGlobalMessage("@cr17@@blu@"+attachment.getLoginName()+" @red@Has received 3 boss points from the shooting star.");
				}
				if (Misc.random(370) == 2) {
					attachment.getItems().addItem(20903, 1);
					attachment.sendMessage("@dre@ You found an noxifer seed from inside the star!");
					PlayerHandler.executeGlobalMessage("@cr17@@blu@"+attachment.getLoginName()+" @red@Has received an noxifer seed from the shooting star.");
				}
				if (Misc.random(373) == 2) {
					attachment.getItems().addItem(23776, 1);
					attachment.sendMessage("@dre@ You found an hunllef key from inside the star!");
					PlayerHandler.executeGlobalMessage("@cr17@@blu@"+attachment.getLoginName()+" @red@Has received an hunllef key from the shooting star.");
				}
				if (Misc.random(375) == 2) {
					attachment.getItems().addItem(4185, 1);
					attachment.sendMessage("@dre@ You found an nightmare key from inside the star!");
					PlayerHandler.executeGlobalMessage("@cr17@@blu@"+attachment.getLoginName()+" @red@Has received an nightmare key from the shooting star.");
				}
				if (Misc.random(401) == 2) {
					attachment.getItems().addItem(11040, 1);
					attachment.sendMessage("@dre@ You found an tarn entry key from inside the star!");
					PlayerHandler.executeGlobalMessage("@cr17@@blu@"+attachment.getLoginName()+" @red@Has received an tarn entry key from the shooting star.");
				}
				if (Misc.random(420) == 2) {
					attachment.getItems().addItem(4447, 1);
					attachment.sendMessage("@dre@ You found an antique lamp from inside the star!");
					PlayerHandler.executeGlobalMessage("@cr17@@blu@"+attachment.getLoginName()+" @red@Has received an antique lamp from the shooting star.");
				}
				if (Misc.random(271) == 2) {
					attachment.getItems().addItem(21046, 1);
					attachment.sendMessage("@dre@ You found an chest rate bonus from inside the star!");
					PlayerHandler.executeGlobalMessage("@cr17@@blu@"+attachment.getLoginName()+" @red@Has received an chest rate bonus from the shooting star.");
				}
				if (Misc.random(200) == 2) {
					attachment.getItems().addItem(995, 100000 + Misc.random(200000));
					attachment.sendMessage("@dre@ You found some gold from inside the star!");
				}
				if (Misc.random(400) == 2) {
					attachment.getItems().addItem(995, 300000 + Misc.random(700000));
					attachment.sendMessage("@dre@ You found some gold from inside the star!");
				}
				if (Misc.random(447) == 2) {
					attachment.getItems().addItem(7629, 1);
					attachment.sendMessage("@dre@ You found an x2 slayer point scroll from inside the star!");
					PlayerHandler.executeGlobalMessage("@cr17@@blu@"+attachment.getLoginName()+" @red@Has received an x2 slayer point scroll from the shooting star.");
				}
				break;

			case AFK_ROCK:
				if (!Boundary.isIn(attachment, Boundary.AFK_ZONE)) {
					stop();
					return;
				}
				if (Misc.random(10) == 1) {
					attachment.afkPoints++;
					attachment.sendMessage("@red@You mine some @blu@Afk points @red@from the rock.");
				}
				break;


		default:
			break;
		
		}
		int amount = SkillcapePerks.MINING.isWearing(attachment) || SkillcapePerks.isWearingMaxCape(attachment) ? 2 :
					attachment.getRechargeItems().hasItem(13104) && Misc.random(9) == 2 ? 2 :
					attachment.getRechargeItems().hasItem(13105) && Misc.random(8) == 2 ? 2 :
					attachment.getRechargeItems().hasItem(13106) && Misc.random(6) == 2 ? 2 :
					attachment.getRechargeItems().hasItem(13107) && Misc.random(4) == 2 ? 2 : 1;

		if (mineral.getMineralReturn().generate() == -1) {
			Server.amountMined++;
		}


		int itemId = mineral.getMineralReturn().generate();
		if ((SkillcapePerks.MINING.isWearing(attachment) || SkillcapePerks.isWearingMaxCape(attachment)) && attachment.getItems().freeSlots() < 2) {
			attachment.sendMessage("You have run out of inventory space.");
			stop();
			return;
		}

		attachment.getItems().addItem(itemId, amount);

		if (mineral == Mineral.GEM) {
			if (itemId == 6571) {
				PlayerHandler.executeGlobalMessage("@pur@" + attachment.getDisplayNameFormatted() + " received a drop: " +
						"" + ItemDef.forId(itemId).getName() + " x " + amount + " from a <col=E9362B>Gem Rock</col>@pur@.");
			}
		}

		if (!mineral.getBarName().equalsIgnoreCase("none")) {
			if (Misc.random(2) == 0) {
				if (attachment.getItems().isWearingItem(13243) || attachment.getItems().playerHasItem(13243)) {
					Smelting.startSmelting(attachment, mineral.getBarName(), "ONE", "INFERNAL");
					return;
				}
			}
		}
		int dropRate = 20;
		switch (mineral) {
			case ADAMANT:
			case COAL:
			case GEM:
			case GOLD:
			case MITHRIL:
			case RUNE:
			case AMETHYST:
				dropRate = 60;
				break;
			case COPPER:
			case IRON:
			case TIN:
			case CLAY:
			case ESSENCE:
				dropRate = 10;
				break;
			default:
				break;
		}
		if (attachment.fasterCluesScroll) {
			dropRate = dropRate*2;
		}
		int clueAmount = 1;
		if (Hespori.activeGolparSeed) {
			clueAmount = 2;
		}
		if (Misc.random(mineral.getPetChance() / dropRate) == 10 ) {
			switch (Misc.random(2)) {
			case 0:
				attachment.getItems().addItemUnderAnyCircumstance(20358, clueAmount);
				break;
				
			case 1:
				attachment.getItems().addItemUnderAnyCircumstance(20360, clueAmount);
				break;
			case 2:
				attachment.getItems().addItemUnderAnyCircumstance(20362, clueAmount);
				break;

			}
			attachment.sendMessage("@blu@You appear to see a clue geode fall within the rock, and pick it up.");
		}

			if (Misc.random(mineral.getPetChance()) / dropRate == 10) {
				attachment.getItems().addItemUnderAnyCircumstance(20362, clueAmount);
				attachment.sendMessage("@blu@You appear to see a clue geode fall within the rock, and pick it up.");
			}

		if (Misc.hasOneOutOf(38)) {//Mining tomes
			if (Boundary.isIn(attachment, Boundary.AFK_ZONE)) {
				return;
			}
			if (Boundary.isIn(attachment, Boundary.AFK_COOKING_AREA)) {
				return;
			}
			if (Boundary.isIn(attachment, Boundary.AFK_FURNACE_AREA)) {
				return;
			}
			attachment.getItems().addItemUnderAnyCircumstance(7791, 1);
			attachment.sendMessage("@red@You luckily notice an mining tome beneath the rock.");
		}

		int petRate = attachment.skillingPetRateScroll ? (int) (mineral.getPetChance() * .75) : mineral.getPetChance();
		if (Misc.random(petRate) == 2 && attachment.getItems().getItemCount(13321, false) == 0
				&& attachment.petSummonId != 7439) {
			PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] @cr20@ <col=255>" + attachment.getDisplayName()
					+ "</col> mined a rock and formed the <col=CC0000>Rock golem</col> pet!");
			attachment.getCollectionLog().handleDrop(attachment, 5, 13321, 1);
			attachment.getItems().addItemUnderAnyCircumstance(13321, 1);
		}
	}

	@Override
	public void stop() {
		super.stop();
		if (attachment == null) {
			return;
		}
		attachment.stopAnimation();
	}
}
