package io.xeros.content.revenant_event;

import com.google.common.collect.Lists;
import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.achievements.AchievementHandler;
import io.xeros.achievements.AchievementList;
import io.xeros.content.Announcement;
import io.xeros.content.evil_tree.EvilTree;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.broadcasts.Broadcast;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.xeros.content.combat.death.NPCDeath.superiorRevenantSpawned;


/**
 * @author C.T, for RuneRogue.
 */
public class RevenantEventBossHandler {

	public static boolean spawned = false;//used to stop the revenant from despawning when the event is active

	public static boolean activated = false;

	public final static double ENRANGED_REVENANT_PET_CHANCE = 500;//EVENT revenant pet chance
	public final static double REVENANT_PET_CHANCE = 1000;//revenant pet chance

	public static int[] ALWAYSLOOT = {7947, 3025, 537, 892, 21820, 7947, 3025, 537, 892, 21820};// resources. Monkfish, Super Restores, Dragon Bones, Rune Arrows, Revenant Ether
	public static int[] COMMONLOOT = {2364, 452, 454, 2362, 1516, 1514, 1632, 9245, 212, 214, 216, 218, 220,384, 13440, 7937, 1748, 44, 824, 2996, 3050, 3052, 11935, 2364, 452, 454, 2362, 1516, 1514, 1632, 9245, 212, 214, 216, 218, 220,384, 13440, 7937, 1748, 44, 824, 2996, 3050, 3052, 11935};// resources. Runite bar, Runite ore, Coal, Adamantite bar, Yew Logs, Uncut Dragonstone, Magic Logs, Onyx bolts (e), Grimy Avantoe, Grimy Kwuarm, Grimy Cadantine, Grimy Dwarf Weeds, Grimy Torstol, Raw Shark, Raw Anglerfish, Pure Essence, Black Dragon hide, Rune Arrowtips, Rune Dart Tips, PkP ticket, Grimy Toadflax, Grimy Snapdragon, Raw dark crab
	public static int[] RARELOOT = {4716, 4718, 4720, 4722, 4708, 4710, 4712, 4714, 4724, 4726, 4728, 4730, 4753, 4755, 4757, 4759, 4732, 4734, 4736, 4738, 4716, 4718, 4720, 4722, 4708, 4710, 4712, 4714, 4724, 4726, 4728, 4730, 4753, 4755, 4757, 4759, 4732, 4734, 4736, 4738, 12875, 12877, 12873, 12883, 12879, 12881};//Dharoks, Ahrims, Guthans, Verac, Karil
	public static int[] SUPERRARELOOT = {6828, 6199, 6199, 6199, 22557, 22557, 692, 692, 691, 691, 691, 19707, 19707, 22542, 22542, 22552, 22552, 22547, 22547, 22885, 22885, 22885, 22883, 22883, 22883, 22881, 22881, 22881, 6112, 6112, 6112, 20903, 20903, 20903, 20909, 20909, 20909, 22869, 22869, 22869, 20906, 20906, 20906, 4205, 4205, 4205};// Super mbox, mbox, avarice, 25k foe, 10k foe, eternal glory, viggora, thammaron, craw, Seeds
	public static int[] ULTRA = {22613, 22610, 22610, 22616, 22619, 22619, 22647, 22647, 22650, 22650, 22650, 22653, 22656, 22656, 22622, 22622, 22622, 22625, 22625, 22628, 22628, 22631, 22631, 22638, 22638, 22641, 22641, 22644, 22644, 13652, 13652, 2403, 6769, 6769, 13346, 6828, 693, 693, 692, 692, 692, 13576, 13576};//Zuriel, Statius, Morrigan, Vesta, Ultra Mystery Box, Super m box, $5 Scroll, $10 scroll, Dragon Claws, Dragon warhammer, 50k foe, 25k foe (not in order)

	public enum RevenantEventBosses {

		REVENANT_MALEDICTUS(11246, "Enranged rev maledictus", 2000, 35, 650, 450);



		private int npcId;

		private  String bossName;

		private  int hp;

		private  int maxHit;

		private  int attack;

		private int defence;

		RevenantEventBosses(final int npcId, final String bossName, final int hp, final int maxHit, final int attack, final int defence) {
			this.npcId = npcId;
			this.bossName = bossName;
			this.hp = hp;
			this.maxHit = maxHit;
			this.attack = attack;
			this.defence = defence;
		}


		public String getBossName() {
			return bossName;
		}

		public int getHp() {
			return hp;
		}

		public int getMaxHit() {
			return maxHit;
		}

		public int getAttack() {
			return attack;
		}

		public int getDefence() {
			return defence;
		}

		public static RevenantEventBosses getRandom() {
			return RevenantEventBosses.values()[Misc.random(0, RevenantEventBosses.values().length - 1)];
		}

		public int getNpcId() {
			return npcId;
		}

		public void setNpcId(int id) {
			this.npcId = id;
		}
	}

	
	private static RevenantEventBossSpawns currentLocation;


	private static RevenantEventBosses activeBoss;
	
	
	private static NPC activeNPC;
	
	

	private static CycleEvent cycleEvent;
	
	

	private static CycleEventContainer cycleEventContainer;
	private static boolean didSend;


	public static List <Player> participants = new ArrayList<Player>();//all players participating in event | added into list by attacking npc


	public static boolean isAttackable;
	public static void spawnNPC() {
		activated = true;
		cycleEvent = new CycleEvent() {

			@Override
			public void execute(CycleEventContainer container) {

				if(spawned == true) {
					return;
				}
				container.setTick(Misc.toCycles(76, TimeUnit.MINUTES));// 77
				if(getActiveBoss() != null) {
					destroyBoss();
					activated = false;
					Configuration.REV_EVENT_TIMER = RevenantEventBossHandler.generateTime();
					return;
				}
				container.setTick(generateTime());
				spawnBoss();
				Configuration.REV_EVENT_TIMER = RevenantEventBossHandler.generateTime();
				activated = true;
			    }

		    @Override
		    public void onStopped() {

			   }

		};
		setCycleEventContainer(CycleEventHandler.getSingleton().addEvent(RevenantEventBossHandler.class, cycleEvent, generateTime()));
	}    

	public static void destroyBoss() {
		if(getActiveBoss() == null)
			return;
		NPCHandler.despawn(getActiveBoss().npcId, 0);
		setActiveBoss(null);
		setCurrentLocation(null);
		activated = false;
		spawned = false;
	}


	public static void spawnBoss() {
		setCurrentLocation(RevenantEventBossSpawns.generateLocation());
		setActiveBoss(RevenantEventBosses.getRandom());
		NPCSpawning.spawnNpcOld(getActiveBoss().getNpcId(), getCurrentLocation().getX(), getCurrentLocation().getY(), 0, 1, getActiveBoss().getHp(), getActiveBoss().getMaxHit(), getActiveBoss().getAttack(), getActiveBoss().getDefence()/*, false*/);
		new Broadcast(getActiveBoss().getBossName() + " has been seen around the " + getCurrentLocation().getLocationName() + ".").copyMessageToChatbox().submit();
		Discord.writeEventMessage(getActiveBoss().getBossName() + " has been seen around the " + getCurrentLocation().getLocationName() + ".");
		spawned = true;
	}



	/**
	 * Rewards all players in the area at the time of the fight
	 */
	public static void rewardPlayers() { //enranged rewards
		activated = false;
		spawned = false;
		PlayerHandler.executeGlobalMessage("@pur@Enranged Revenant maledictus has now been killed.");
		PlayerHandler.nonNullStream().filter(p -> Boundary.isIn(p, Boundary.REV_MALEDICTUS_AREA))
				.forEach(p -> {
					Server.itemHandler.createGroundItem(p, 995,
							3138, 3841, 0, 250_000 + Misc.getRandom(2_000_000));
					p.sendMessage("Congratulations you destroyed the enranged revenant maledictus.");
					AchievementHandler.activate(p, AchievementList.REV_MALEDICTUS_I, 1);//NEW ACHIEVEMNTS
					AchievementHandler.activate(p, AchievementList.REV_MALEDICTUS_II, 1);//NEW ACHIEVEMNTS
					AchievementHandler.activate(p, AchievementList.REV_MALEDICTUS_III, 1);//NEW ACHIEVEMNTS
					if (Misc.hasOneOutOf(ENRANGED_REVENANT_PET_CHANCE)) {
						p.getItems().addItem(16012, 1);//Revenant maledictus pet
						PlayerHandler.executeGlobalMessage("@pur@" + Misc.capitalize(p.getLoginName()) + " @red@has received the @pur@Revenent maledictus pet.");
					}
					int random = Misc.random(15);
					if (random > 14) {
						int rareitem1 = 0;
						rareitem1 = Misc.random(ULTRA.length - 1);
						p.snakeUltraRareReward[0][0] = ULTRA[rareitem1];
						Server.itemHandler.createGroundItem(p, p.snakeUltraRareReward[0][0],
								3131, 3838, 0, 1);

						Server.itemHandler.createGroundItem(p, COMMONLOOT[Misc.random(COMMONLOOT.length - 1)],
								3135, 3840, 0, 50 + Misc.getRandom(120));

						Server.itemHandler.createGroundItem(p, COMMONLOOT[Misc.random(COMMONLOOT.length - 1)],
								3139, 3837, 0, 50 + Misc.getRandom(120));

						//Announcement.announce("<img=25>@pur@" + p.getLoginName() + " @red@Enranged Revenant Maledictus@pur@ has dropped hes @red@Special Loot package!");
						Announcement.announce("<img=25>@pur@" + p.getLoginName() + " @red@Enranged Revenant Maledictus@pur@ has dropped: @red@"+ ItemDef.forId(p.snakeUltraRareReward[0][0]).getName() +"!");
						Discord.writeDropsSyncMessage("News: "+p.getLoginName()+" just received: " + ItemDef.forId(p.snakeUltraRareReward[0][0]).getName() + " from enranged revenant maledictus!");
						return;
					}

					if (random > 10) {
						int rareitem = 0;
						rareitem = Misc.random(SUPERRARELOOT.length - 1);
						p.snakeSuperRareReward[0][0] = SUPERRARELOOT[rareitem];
						Server.itemHandler.createGroundItem(p, p.snakeSuperRareReward[0][0],
								3143, 3841, 0, 1);

						Server.itemHandler.createGroundItem(p, COMMONLOOT[Misc.random(COMMONLOOT.length - 1)],
								3143, 3845, 0, 30 + Misc.getRandom(100));

						Server.itemHandler.createGroundItem(p, COMMONLOOT[Misc.random(COMMONLOOT.length - 1)],
								3145, 3848, 0, 30 + Misc.getRandom(100));
						//Announcement.announce("<img=25> @pur@" + p.getLoginName() + " @red@Enranged Revenant Maledictus @pur@has dropped hes @red@Special Loot package!");
						Announcement.announce("<img=25>@pur@" + p.getLoginName() + " @red@Enranged Revenant Maledictus@pur@ has dropped: @red@"+ ItemDef.forId(p.snakeSuperRareReward[0][0]).getName() +"!");
						Discord.writeDropsSyncMessage("News: "+p.getLoginName()+" just received: " + ItemDef.forId(p.snakeSuperRareReward[0][0]).getName() + " from enranged revenant maledictus!");
						return;
					}

					if (random > 8) {
						int rareitem2 = 0;
						rareitem2 = Misc.random(RARELOOT.length - 1);
						p.snakeRareReward[0][0] = RARELOOT[rareitem2];
						Server.itemHandler.createGroundItem(p, p.snakeRareReward[0][0],
								3138, 3854, 0, 1);


						Server.itemHandler.createGroundItem(p, ALWAYSLOOT[Misc.random(ALWAYSLOOT.length - 1)],
								3138, 3851, 0, 10 + Misc.getRandom(80));

						Server.itemHandler.createGroundItem(p, ALWAYSLOOT[Misc.random(ALWAYSLOOT.length - 1)],
								3135, 3851, 0, 10 + Misc.getRandom(80));

						//Announcement.announce("<img=25> @pur@" + p.getLoginName() + " @red@Enranged Revenant Maledictus @pur@has dropped hes @red@Special Loot package!");
						Announcement.announce("<img=25>@pur@" + p.getLoginName() + " @red@Enranged Revenant Maledictus@pur@ has dropped: @red@"+ ItemDef.forId(p.snakeRareReward[0][0]).getName() +"!");
						Discord.writeDropsSyncMessage("News: "+p.getLoginName()+" just received: " + ItemDef.forId(p.snakeRareReward[0][0]).getName() + " from enranged revenant maledictus!");
						return;
					}

					if (random > 4) {
						Server.itemHandler.createGroundItem(p, COMMONLOOT[Misc.random(COMMONLOOT.length - 1)],
								3132, 3849, 0, 10 + Misc.getRandom(35));

						Server.itemHandler.createGroundItem(p, COMMONLOOT[Misc.random(COMMONLOOT.length - 1)],
								3130, 3848, 0, 10 + Misc.getRandom(35));

						Server.itemHandler.createGroundItem(p, COMMONLOOT[Misc.random(COMMONLOOT.length - 1)],
								3132, 3845, 0, 10 + Misc.getRandom(35));

						Server.itemHandler.createGroundItem(p, ALWAYSLOOT[Misc.random(ALWAYSLOOT.length - 1)],
								3130, 3842, 0, 10 + Misc.getRandom(20));
						Announcement.announce("<img=25> @pur@" + p.getLoginName() + " @red@Enranged Revenant Maledictus @pur@has dropped hes @red@Common Loot package!");
						//Announcement.announce("<img=25>@pur@" + p.getLoginName() + " @red@Enranged Revenant Maledictus@pur@ has dropped: @red@"+ ItemDef.forId(COMMONLOOT.length).getName() +"!");
						return;
					}
					if (random > 2) {
						Server.itemHandler.createGroundItem(p, ALWAYSLOOT[Misc.random(ALWAYSLOOT.length - 1)],
								3128, 3842, 0, 10 + Misc.getRandom(20));

						Server.itemHandler.createGroundItem(p, ALWAYSLOOT[Misc.random(ALWAYSLOOT.length - 1)],
								3127, 3840, 0, 10 + Misc.getRandom(20));

						Server.itemHandler.createGroundItem(p, ALWAYSLOOT[Misc.random(ALWAYSLOOT.length - 1)],
								3127, 3838, 0, 10 + Misc.getRandom(20));
						Announcement.announce("<img=25> @pur@" + p.getLoginName() + " @red@Enranged Revenant maledictus @pur@has dropped hes @red@Common Loot package!");
						//Announcement.announce("<img=25>@pur@" + p.getLoginName() + " @red@Enranged Revenant Maledictus@pur@ has dropped: @red@"+ ItemDef.forId(COMMONLOOT.length).getName() +"!");
						return;
					}

				});
	}



	public static void rewardPlayersSuperior() { //normal version rewards

		PlayerHandler.nonNullStream().filter(p -> Boundary.isIn(p, Boundary.REV_CAVE))
				.forEach(p -> {
					Server.itemHandler.createGroundItem(p, 995,
							p.getX(), p.getY(), 0, 250_000 + Misc.getRandom(300_000));
					p.sendMessage("@blu@Congratulations you defeated the superior revenant maledictus.");

					if (Misc.hasOneOutOf(REVENANT_PET_CHANCE)) {
						p.getItems().addItem(16012, 1);//Revenant maledictus pet
						PlayerHandler.executeGlobalMessage("@pur@" + Misc.capitalize(p.getLoginName()) + " @red@has received the @pur@Revenent maledictus pet.");
					}

					int random = Misc.random(25);//15
					if (random > 24) {
						int rareitem1 = 0;
						rareitem1 = Misc.random(ULTRA.length - 1);
						p.snakeUltraRareReward[0][0] = ULTRA[rareitem1];
						Server.itemHandler.createGroundItem(p, p.snakeUltraRareReward[0][0],
								p.getX(), p.getY(), 0, 1);

						Server.itemHandler.createGroundItem(p, COMMONLOOT[Misc.random(COMMONLOOT.length - 1)],
								p.getX(), p.getY(), 0, 50 + Misc.getRandom(120));

						Server.itemHandler.createGroundItem(p, COMMONLOOT[Misc.random(COMMONLOOT.length - 1)],
								p.getX(), p.getY(), 0, 50 + Misc.getRandom(120));

						Announcement.announce("<img=17>@blu@" + p.getLoginName() + " @red@Revenant Maledictus@blu@ has dropped: @red@"+ ItemDef.forId(p.snakeUltraRareReward[0][0]).getName() +"!");
						Discord.writeDropsSyncMessage("News: "+p.getLoginName()+" just received: " + ItemDef.forId(p.snakeUltraRareReward[0][0]).getName() + " from the revenant maledictus!");
						return;
					}

					if (random > 20) {
						int rareitem = 0;
						rareitem = Misc.random(SUPERRARELOOT.length - 1);
						p.snakeSuperRareReward[0][0] = SUPERRARELOOT[rareitem];
						Server.itemHandler.createGroundItem(p, p.snakeSuperRareReward[0][0],
								p.getX(), p.getY(), 0, 1);

						Server.itemHandler.createGroundItem(p, COMMONLOOT[Misc.random(COMMONLOOT.length - 1)],
								p.getX(), p.getY(), 0, 30 + Misc.getRandom(100));

						Server.itemHandler.createGroundItem(p, COMMONLOOT[Misc.random(COMMONLOOT.length - 1)],
								p.getX(), p.getY(), 0, 30 + Misc.getRandom(100));
						Announcement.announce("<img=17> @blu@" + p.getLoginName() + " @red@Revenant Maledictus @blu@has dropped: @red@"+ ItemDef.forId(p.snakeSuperRareReward[0][0]).getName() +"!");
						Discord.writeDropsSyncMessage("News: "+p.getLoginName()+" just received: " + ItemDef.forId(p.snakeSuperRareReward[0][0]).getName() + " from the revenant maledictus!");
						return;
					}

					if (random > 15) {
						int rareitem2 = 0;
						rareitem2 = Misc.random(RARELOOT.length - 1);
						p.snakeRareReward[0][0] = RARELOOT[rareitem2];
						Server.itemHandler.createGroundItem(p, p.snakeRareReward[0][0],
								p.getX(), p.getY(), 0, 1);


						Server.itemHandler.createGroundItem(p, ALWAYSLOOT[Misc.random(ALWAYSLOOT.length - 1)],
								p.getX(), p.getY(), 0, 10 + Misc.getRandom(80));

						Server.itemHandler.createGroundItem(p, ALWAYSLOOT[Misc.random(ALWAYSLOOT.length - 1)],
								p.getX(), p.getY(), 0, 10 + Misc.getRandom(80));

						Announcement.announce("<img=17> @blu@" + p.getLoginName() + " @red@Revenant Maledictus @blu@has dropped: @red@"+ ItemDef.forId(p.snakeRareReward[0][0]).getName() +"!");
						Discord.writeDropsSyncMessage("News: "+p.getLoginName()+" just received: " + ItemDef.forId(p.snakeRareReward[0][0]).getName() + " from the revenant maledictus!");
						return;
					}

					if (random > 7) {
						Server.itemHandler.createGroundItem(p, COMMONLOOT[Misc.random(COMMONLOOT.length - 1)],
								p.getX(), p.getY(), 0, 10 + Misc.getRandom(35));

						Server.itemHandler.createGroundItem(p, COMMONLOOT[Misc.random(COMMONLOOT.length - 1)],
								p.getX(), p.getY(), 0, 10 + Misc.getRandom(35));

						Server.itemHandler.createGroundItem(p, COMMONLOOT[Misc.random(COMMONLOOT.length - 1)],
								p.getX(), p.getY(), 0, 10 + Misc.getRandom(35));

						Server.itemHandler.createGroundItem(p, ALWAYSLOOT[Misc.random(ALWAYSLOOT.length - 1)],
								p.getX(), p.getY(), 0, 10 + Misc.getRandom(20));
						Announcement.announce("<img=17> @blu@" + p.getLoginName() + " @red@Revenant Maledictus @blu@has dropped: @red@Common Loot package!");

						return;
					}
					if (random > 5) {
						Server.itemHandler.createGroundItem(p, ALWAYSLOOT[Misc.random(ALWAYSLOOT.length - 1)],
								p.getX(), p.getY(), 0, 10 + Misc.getRandom(20));

						Server.itemHandler.createGroundItem(p, ALWAYSLOOT[Misc.random(ALWAYSLOOT.length - 1)],
								p.getX(), p.getY(), 0, 10 + Misc.getRandom(20));

						Server.itemHandler.createGroundItem(p, ALWAYSLOOT[Misc.random(ALWAYSLOOT.length - 1)],
								p.getX(), p.getY(), 0, 10 + Misc.getRandom(20));
						Announcement.announce("<img=17> @blu@" + p.getLoginName() + " @red@Revenant maledictus @blu@has dropped hes @red@Common Loot package!");
						return;
					}

				});
	}



	public static int generateTime(){
		return Misc.toCycles(76, TimeUnit.MINUTES);
	}//77
	
	public static CycleEventContainer getCycleEventContainer() {
		return cycleEventContainer;
	}

	public static void setCycleEventContainer(CycleEventContainer cycleEventContainer) {
		RevenantEventBossHandler.cycleEventContainer = cycleEventContainer;
	}

	public static Boundary getActiveBoundary() {
		return getCurrentLocation().getBoundary();
	}

	public static NPC getActiveNPC() {
		return activeNPC;
	}

	public static void setActiveNPC(NPC activeNPC) {
		RevenantEventBossHandler.activeNPC = activeNPC;
	}

	public static boolean isDidSend() {
		return didSend;
	}

	public static void setDidSend(boolean didSend) {
		RevenantEventBossHandler.didSend = didSend;
	}
	
	private static String name;
	public static String getName() {
		return name;
	}

	public static void setName(String name) {
		RevenantEventBossHandler.name = name;
	}

	public static RevenantEventBossSpawns getCurrentLocation() {
		return currentLocation;
	}

	public static void setCurrentLocation(RevenantEventBossSpawns currentLocation) {
		RevenantEventBossHandler.currentLocation = currentLocation;
	}

	public static RevenantEventBosses getActiveBoss() {
		return activeBoss;
	}

	public static void setActiveBoss(RevenantEventBosses activeBoss) {
		RevenantEventBossHandler.activeBoss = activeBoss;
	}

	

}