package io.xeros.content.skills.woodcutting;

import java.util.Optional;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.SkillcapePerks;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.achievement_diary.impl.DesertDiaryEntry;
import io.xeros.content.achievement_diary.impl.FaladorDiaryEntry;
import io.xeros.content.achievement_diary.impl.FremennikDiaryEntry;
import io.xeros.content.achievement_diary.impl.KandarinDiaryEntry;
import io.xeros.content.achievement_diary.impl.LumbridgeDraynorDiaryEntry;
import io.xeros.content.achievement_diary.impl.VarrockDiaryEntry;
import io.xeros.content.achievement_diary.impl.WildernessDiaryEntry;
import io.xeros.content.bosspoints.BossPoints;
import io.xeros.content.dailytasks.DailyTasks;
import io.xeros.content.event.eventcalendar.EventChallenge;
import io.xeros.content.evil_tree.EvilTree;
import io.xeros.content.hespori.Hespori;
import io.xeros.content.shooting_star.ShootingStar;
import io.xeros.content.skills.Skill;
import io.xeros.content.skills.firemake.Firemaking;
import io.xeros.model.Items;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.cycleevent.Event;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Location3D;
import io.xeros.util.Misc;

import static io.xeros.Server.amountCut;

public class WoodcuttingEvent extends Event<Player> {
	private static boolean woodcuttingTree;
	private final Tree tree;
	private final Hatchet hatchet;
	private final int objectId;
	private final int x;
	private final int y;
	private int chops;
	
	private final int[] lumberjackOutfit = { 10933, 10939, 10940, 10941 };

	public WoodcuttingEvent(Player player, Tree tree, Hatchet hatchet, int objectId, int x, int y) {
		super("skilling", player, 1);
		this.tree = tree;
		this.hatchet = hatchet;
		this.objectId = objectId;
		this.x = x;
		this.y = y;
	}

	@Override
	public void execute() {
		double osrsExperience;
		double experience;
		int pieces = 0;
		pieces=handleOutfit(pieces);
		osrsExperience = tree.getExperience() + tree.getExperience() / 10 * pieces;
		if (canChop()) return;
		if (Misc.random(300) == 0 && attachment.getInterfaceEvent().isExecutable()) {
			attachment.getInterfaceEvent().execute();
			super.stop();
			return;
		}
		chops++;
		int chopChance = 1 + (int) (tree.getChopsRequired() * hatchet.getChopSpeed());
		if (Boundary.isIn(attachment, Boundary.WOODCUTTING_GUILD_BOUNDARY)){
			chopChance *= 1.5;
		}

		if (Misc.random(tree.getChopdownChance()) == 0 || tree.equals(Tree.NORMAL) && Misc.random(chopChance) == 0) {
			int face = 0;
			Optional<WorldObject> worldObject = attachment.getRegionProvider().get(x, y).getWorldObject(objectId, x, y, 0);
			if (worldObject.isPresent()) {
				face = worldObject.get().getFace();
			}
			int stumpId = 0;
			if (tree.equals(Tree.REDWOOD)) {
				face = (attachment.absX < 1568) ? 1 : (attachment.absX > 1573) ? 3 : (attachment.absY < 3480) ? 0 : 2;
				if (objectId == 29668)
					stumpId = 29669;
				else if (objectId == 29670)
					stumpId = 29671;
			}

			Server.getGlobalObjects().add(new GlobalObject(tree.equals(Tree.REDWOOD) ? stumpId : tree.getStumpId(), x, y, attachment.heightLevel, face, 10, tree.getRespawnTime(), objectId));
			attachment.getItems().addItem(tree.getWood(), 1);
			attachment.getEventCalendar().progress(EventChallenge.CUT_DOWN_X_MAGIC_LOGS);
			attachment.getPA().addSkillXPMultiplied((int)osrsExperience, Skill.WOODCUTTING.getId(), true);
			Achievements.increase(attachment, AchievementType.WOODCUT, 1);
			attachment.getPA().sendSound(2734);
			handleRewards();

			super.stop();
			return;
		}
		if (!tree.equals(Tree.NORMAL)) {
			if (Misc.random(chopChance) == 0 || chops >= tree.getChopsRequired()) {
				chops = 0;
				int random = Misc.random(4);
				attachment.getPA().addSkillXPMultiplied((int) osrsExperience, Skill.WOODCUTTING.getId(), true);
				Achievements.increase(attachment, AchievementType.WOODCUT, 1);
				if ((attachment.getItems().isWearingItem(13241) || attachment.getItems().playerHasItem(13241)) && random == 2) {
					Firemaking.lightFire(attachment, tree.getWood(), "infernal_axe");
					return;
				}
				handleDiary(tree);
				foeArtefact(attachment);
				handleWildernessRewards();
				handleRewards();
				switch (tree) {





					case EVIL_TREE:
						//if ((Server.amountCut >= Server.cutAmount)){
						amountCut++;
						if (amountCut >= Server.cutAmount) {
							EvilTree.despawnTree();
							stop();
							return;
						}
						if (Misc.random(8) == 1) {
							stop();
							return;
						}
						if (Misc.random(5) == 2) {
							attachment.getItems().addItem(1512, 20 + Misc.random(30));
							attachment.sendMessage("@dre@ You managed to chop some logs from the crystal tree!");
						}
						if (Misc.random(8) == 2) {
							attachment.getItems().addItem(1522, 20 + Misc.random(30));
							attachment.sendMessage("@dre@ You managed to chop some oak logs from the crystal tree!");
						}
						if (Misc.random(14) == 2) {
							attachment.getItems().addItem(1516, 20 + Misc.random(30));
							attachment.sendMessage("@dre@ You managed to chop some yew logs from the crystal tree!");
						}
						if (Misc.random(14) == 2) {
							attachment.getItems().addItem(1514, 1 + Misc.random(10));
							attachment.sendMessage("@dre@ You managed to chop some magic logs from the crystal tree!");
						}
						if (Misc.random(50) == 2) {
							attachment.getItems().addItem(1514, 1 + Misc.random(10));
							attachment.sendMessage("@dre@ You managed to chop some magic logs from the crystal tree!");
						}
						if (Misc.random(120) == 2) {
							attachment.getItems().addItem(1514, 50 + Misc.random(60));
							attachment.sendMessage("@dre@ You managed to chop some magic logs from the crystal tree!");
						}
						if (Misc.random(77) == 2) {
							attachment.getItems().addItem(1514, 5 + Misc.random(25));
							attachment.sendMessage("@dre@ You managed to chop some magic logs from the crystal tree!");
						}
						if (Misc.random(65) == 2) {
							attachment.getItems().addItem(1516, 20 + Misc.random(30));
							attachment.sendMessage("@dre@ You managed to chop some yew logs from the crystal tree!");
						}
						if (Misc.random(98) == 2) {
							attachment.getItems().addItem(1516, 20 + Misc.random(30));
							attachment.sendMessage("@dre@ You managed to chop some yew logs from the crystal tree!");
						}
						if (Misc.random(200) == 2) {
							attachment.getItems().addItem(4185, 1);
							attachment.sendMessage("@dre@ You managed to chop an nightmare key from the crystal tree!");
							PlayerHandler.executeGlobalMessage("@red@"+attachment.getLoginName()+" @pur@has chopped an @red@nightmare key @pur@from the crystal tree.");
						}
						if (Misc.random(290) == 2) {
							attachment.getItems().addItem(2528, 1);
							attachment.sendMessage("@dre@ You managed to chop an experience lamp from the evil tree!");
							PlayerHandler.executeGlobalMessage("@red@"+attachment.getLoginName()+" @pur@has chopped an @red@experience lamp @pur@from the crystal tree.");
						}
						if (Misc.random(295) == 2) {
							attachment.getItems().addItem(23776, 1);
							attachment.sendMessage("@dre@ You managed to chop an hunllefs key from the evil tree!");
							PlayerHandler.executeGlobalMessage("@red@"+attachment.getLoginName()+" @pur@has chopped an @red@hunllefs key @pur@from the crystal tree.");
						}
						if (Misc.random(360) == 2) {
							attachment.getItems().addItem(21307, 1);
							attachment.sendMessage("@dre@ You managed to chop an rogue's equipment crate from the evil tree!");
							PlayerHandler.executeGlobalMessage("@red@"+attachment.getLoginName()+" @pur@has chopped an @red@rogue's equipment crate @pur@from the crystal tree.");
						}
						if (Misc.random(271) == 2) {
							attachment.getItems().addItem(995, 100000 + Misc.random(200000));
							attachment.sendMessage("@dre@ You managed to chop some gold from the crystal tree!");
						}
						if (Misc.random(200) == 2) {
							attachment.getItems().addItem(995, 100000 + Misc.random(200000));
							attachment.sendMessage("@dre@ You managed to chop some gold from the crystal tree!");
						}
						if (Misc.random(400) == 2) {
							attachment.getItems().addItem(995, 300000 + Misc.random(500000));
							attachment.sendMessage("@dre@ You managed to chop some gold from the crystal tree!");
						}
						if (Misc.random(447) == 2) {
							attachment.getItems().addItem(11230, 50 + Misc.random(150));
							attachment.sendMessage("@dre@ You managed to chop some dragon darts from the crystal tree!");
						}
						if (Misc.random(448) == 2) {
							attachment.getItems().addItem(20906, 1);
							attachment.sendMessage("@dre@ You managed to chop a glopar seed from the crystal tree!");
							PlayerHandler.executeGlobalMessage("@red@"+attachment.getLoginName()+" @pur@has chopped an @red@golpar seed @pur@from the crystal tree.");
						}
						break;




					case AFK_TREE:
						if (!Boundary.isIn(attachment, Boundary.AFK_ZONE)) {
							stop();
							return;
						}
						if (Misc.random(75) == 1) {
							attachment.afkPoints++;
							attachment.sendMessage("@red@You chop some @blu@afk points @red@from the tree.");
						}
						break;

					default:
						break;

				}
				if ((SkillcapePerks.WOODCUTTING.isWearing(attachment) || SkillcapePerks.isWearingMaxCape(attachment)) && attachment.getItems().freeSlots() < 2) {
					attachment.sendMessage("You have run out of free inventory space.");
					super.stop();
					return;
				}
				attachment.getItems().addItem(tree.getWood(), SkillcapePerks.WOODCUTTING.isWearing(attachment) || SkillcapePerks.isWearingMaxCape(attachment) ? 2 : 1);
			}
		}
		if (super.getElapsedTicks() % 4 == 0) {
			attachment.startAnimation(hatchet.getAnimation());
		}
	}

	private int handleOutfit(int pieces) {

		for (int aLumberjackOutfit : lumberjackOutfit) {
			if (attachment.getItems().isWearingItem(aLumberjackOutfit)) {
				pieces+=2;
			}
		}
		return pieces;
	}

	private boolean canChop() {

		if (attachment == null || attachment.isDisconnected() || attachment.getSession() == null) {
			super.stop();
			return true;
		}
		if (!attachment.getItems().playerHasItem(hatchet.getItemId()) && !attachment.getItems().isWearingItem(hatchet.getItemId())) {
			attachment.sendMessage("Your axe has disappeared.");
			super.stop();
			return true;
		}
		if (attachment.playerLevel[Player.playerWoodcutting] < hatchet.getLevelRequired()) {
			attachment.sendMessage("You no longer have the level required to operate this hatchet.");
			super.stop();
			return true;
		}
		if (attachment.getItems().freeSlots() == 0) {
			attachment.sendMessage("You have run out of free inventory space.");
			super.stop();
			return true;
		}
		return false;
	}

	private void handleWildernessRewards() {

		if (Boundary.isIn(attachment, Boundary.RESOURCE_AREA)) {
			if (Misc.random(20) == 5) {
				int randomAmount = 1;
				attachment.sendMessage("You received " + randomAmount + " pkp while woodcutting!");
				attachment.getItems().addItem(2996, randomAmount);
			}
		}
	}

	private void handleDiary(Tree tree) {
		switch (tree) {

			case MAGIC:
				attachment.getEventCalendar().progress(EventChallenge.CUT_DOWN_X_MAGIC_LOGS, 2);
				if (Boundary.isIn(attachment, Boundary.AL_KHARID_BOUNDARY)) {
					attachment.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.CHOP_MAGIC_AL);
				}
				if (Boundary.isIn(attachment, Boundary.RESOURCE_AREA_BOUNDARY)) {
					attachment.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.MAGIC_LOG_WILD);
				}
				if (Boundary.isIn(attachment, Boundary.SEERS_BOUNDARY)) {
					attachment.getDiaryManager().getKandarinDiary().progress(KandarinDiaryEntry.CUT_MAGIC_SEERS);
				}
				DailyTasks.increase(attachment, DailyTasks.PossibleTasks.MAGIC_LOGS);//Daily tasks
				break;
			case MAPLE:
				break;
			case NORMAL:
				break;
			case OAK:
				if (Boundary.isIn(attachment, Boundary.LUMRIDGE_BOUNDARY) || Boundary.isIn(attachment, Boundary.DRAYNOR_BOUNDARY)) {
					attachment.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.CHOP_OAK);
				}
				if (Boundary.isIn(attachment, Boundary.RELLEKKA_BOUNDARY)) {
					attachment.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.CHOP_OAK_FREM);
				}
				break;
			case WILLOW:
				if (Boundary.isIn(attachment, Boundary.FALADOR_BOUNDARY)) {
					attachment.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.CHOP_WILLOW);
				}
				if (Boundary.isIn(attachment, Boundary.DRAYNOR_BOUNDARY)) {
					attachment.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.CHOP_WILLOW_DRAY);
				}
				break;
			case YEW:
				if (Boundary.isIn(attachment, Boundary.FALADOR_BOUNDARY)) {
					attachment.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.CHOP_YEW);
				}
				if (Boundary.isIn(attachment, Boundary.VARROCK_BOUNDARY)) {
					attachment.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.YEWS_AND_BURN);
				}
				DailyTasks.increase(attachment, DailyTasks.PossibleTasks.YEW_LOGS);//Daily tasks
				break;
			case TEAK:
				if (Boundary.isIn(attachment, Boundary.DESERT_BOUNDARY)) {
					attachment.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.CHOP_TEAK);
				}
				break;
			default:
				break;

		}
	}
	private static void foeArtefact(Player player) {
		int chance = 250;
		int artefactRoll = Misc.random(100);
		if (Misc.random(chance) == 1) {
			if (artefactRoll <65) {//1/300
				player.getItems().addItemUnderAnyCircumstance(11180, 1);//ancient coin foe for 200
				player.sendMessage("You found a coin that can be used in the Fire of Exchange!");
			} else if (artefactRoll >= 65 && artefactRoll <= 99) {//1/600
				player.getItems().addItemUnderAnyCircumstance(681, 1);//anicent talisman foe for 300
				player.sendMessage("You found a talisman that can be used in the Fire of Exchange!");
			} else if (artefactRoll > 99){//1/1000
				player.getItems().addItemUnderAnyCircumstance(9034, 1);//golden statuette foe for 500
				PlayerHandler.executeGlobalMessage("@bla@[@red@Woodcutting@bla@]@blu@ " + player.getDisplayName() + " @red@just found a Golden statuette while wcing!");
			}
		}
	}

	private void handleRewards() {
		int dropRate = 10;
		int clueAmount = 1;
		if (attachment.fasterCluesScroll) {
			dropRate = dropRate*2;
		}
		if (Hespori.activeGolparSeed) {
			clueAmount = 2;
		}
		if (Misc.random(tree.getPetChance() / dropRate) == 10) {
			switch (Misc.random(2)) {
			case 0:
				attachment.getItems().addItemUnderAnyCircumstance(19712, clueAmount);
				break;
			case 1:
				attachment.getItems().addItemUnderAnyCircumstance(19714, clueAmount);
				break;
			case 2:
				attachment.getItems().addItemUnderAnyCircumstance(19716, clueAmount);
				break;
			}
			attachment.sendMessage("@blu@You appear to see a clue nest fall from the tree, and pick it up.");
		}

		if (Misc.random(500) == 1) {
			attachment.getItems().addItemUnderAnyCircumstance(lumberjackOutfit[Misc.random(lumberjackOutfit.length - 1)], 1);
			attachment.sendMessage("You notice a lumberjack piece falling from the tree and pick it up.");
		}

		if (Misc.random(175) == 1) {
			attachment.getItems().addItemUnderAnyCircumstance(Items.BIRD_NEST, 1);
			attachment.sendMessage("You notice a Bird's nest falling from the tree and pick it up.");
		}

		if (Misc.hasOneOutOf(34)) {//Woodcutting tomes
			if (Boundary.isIn(attachment, Boundary.AFK_ZONE)) {
				return;
			}
			if (Boundary.isIn(attachment, Boundary.AFK_COOKING_AREA)) {
				return;
			}
			if (Boundary.isIn(attachment, Boundary.AFK_FURNACE_AREA)) {
				return;
			}
			attachment.getItems().addItemUnderAnyCircumstance(7797, 1);
			attachment.sendMessage("@red@You luckily notice an woodcutting tome fall from the tree.");
		}

		int petRate = attachment.skillingPetRateScroll ? (int) (tree.getPetChance() * .75) : tree.getPetChance();
		if (Misc.random(petRate) == 2 && attachment.getItems().getItemCount(13322, false) == 0 && attachment.petSummonId != 13322) {
			PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] @cr20@ <col=255>" + attachment.getDisplayName() + "</col> chopped down a tree for the <col=CC0000>Beaver</col> pet!");
			attachment.getItems().addItemUnderAnyCircumstance(13322, 1);
			attachment.getCollectionLog().handleDrop(attachment, 5, 13322, 1);
		}
	}

	@Override
	public void stop() {
		super.stop();
		if (attachment != null) {
			attachment.startAnimation(65535);
		}
	}

}
