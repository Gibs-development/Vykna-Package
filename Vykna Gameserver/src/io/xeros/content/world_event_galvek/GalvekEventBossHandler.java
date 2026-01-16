package io.xeros.content.world_event_galvek;


import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.achievements.AchievementHandler;
import io.xeros.achievements.AchievementList;
import io.xeros.content.Announcement;
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

import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * @author C.T.
 *
 * Handles the galvek event system.
 */
public class GalvekEventBossHandler {

	public final static double GALVEK_PET_CHANCE = 300;//galvek pet chance

	public static int[] ALWAYSLOOT = {535, 537, 11944, 527, 11212, 11230, 386, 392};//Resources can leave those unless want to add more resources
	public static int[] COMMONLOOT = {535, 537, 11944, 527, 11212, 11230, 386, 392, 200, 1514, 21880, 6816};//Resources can leave those unless want to add more resources
	public static int[] RARELOOT = {10589, 10589, 6809, 6809, 10564, 10564, 21643, 21643, 3122, 3122, 4153, 4153, 12848, 21298, 21298, 21301, 21301, 21304, 21304, 6568, 6568, 4151, 4151, 21009, 21009, 1249, 1249, 7158, 7158, 1434, 1434, 12875, 12877, 12873, 12881, 12883, 12879};//granite set, obsidian set, obsidian cape, abyssal whip, dragon mace, dragon 2h sword, dragon spear, dragon sowrd, verac, dharok, guthan, ahrim, karils, torag sets
	public static int[] SUPERRARELOOT = {11335, 11335, 21892, 21892, 3140, 3140, 3140, 21895, 21895, 21895, 1187, 1187, 1187, 6828, 6199, 6199, 6199, 691, 691, 691, 692, 692, 21028};//Dragon full helm, platebody, chainbody, kiteshield (old), sq shield, super m box, m box, 10k foe, 25k foe, dragon harpoon (not in order)
	public static int[] ULTRA = {21226, 13346, 21633, 21633, 21637, 21637, 13652, 13652, 13576, 13576, 6769, 6769, 693, 693, 6828, 6828, 12417, 12417, 12417, 22242, 22242, 22242, 12416, 12416, 12416, 12415, 12415, 12415, 12414, 12414, 12414, 22234, 22234, 22234, 22244, 22244, 22244, 20002, 20002, 20143, 20143, 12771, 12771};//Signet, ultra box, Ancient wyvern shield, Wyvern Visage, Dragon (g), $5 scroll, 50k foe (not in order)


	public static boolean activated = false;

	public enum GalvekEventBosses {

		GALVEK(8095, "Galvek", 3000, 50, 650, 450);


		private int npcId;

		private String bossName;

		private int hp;

		private int maxHit;

		private int attack;

		private int defence;

		GalvekEventBosses(final int npcId, final String bossName, final int hp, final int maxHit, final int attack, final int defence) {
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

		public static GalvekEventBosses getRandom() {
			return GalvekEventBosses.values()[Misc.random(0, GalvekEventBosses.values().length - 1)];
		}

		public int getNpcId() {
			return npcId;
		}

		public void setNpcId(int id) {
			this.npcId = id;
		}
	}


	private static GalvekEventBossSpawns currentLocation;


	private static GalvekEventBosses activeBoss;


	private static NPC activeNPC;


	private static CycleEvent cycleEvent;


	private static CycleEventContainer cycleEventContainer;
	private static boolean didSend;


	public static List<Player> participants = new ArrayList<Player>();//all players participating in event | added into list by attacking npc


	public static void spawnNPC() {
		activated = true;
		cycleEvent = new CycleEvent() {

			@Override
			public void execute(CycleEventContainer container) {

				if (getActiveBoss() != null) {
					destroyBoss();
					container.setTick(Misc.toCycles(167, TimeUnit.MINUTES));
					activated = false;
					setCurrentLocation(null);
					Configuration.GALVEK_EVENT_TIMER = GalvekEventBossHandler.generateTime();
					return;
				}
				container.setTick(generateTime());
				spawnBoss();
				Configuration.GALVEK_EVENT_TIMER = GalvekEventBossHandler.generateTime();
				activated = true;
			}

			@Override
			public void onStopped() {

			}

		};
		setCycleEventContainer(CycleEventHandler.getSingleton().addEvent(GalvekEventBossHandler.class, cycleEvent, generateTime()));
	}

	public static void destroyBoss() {
		if (getActiveBoss() == null)
			return;
		NPCHandler.despawn(getActiveBoss().npcId, 0);
		setActiveBoss(null);
		setCurrentLocation(null);
		activated = false;
	}


	public static void spawnBoss() {
		setCurrentLocation(GalvekEventBossSpawns.generateLocation());
		setActiveBoss(GalvekEventBosses.getRandom());
		NPCSpawning.spawnNpcOld(getActiveBoss().getNpcId(), getCurrentLocation().getX(), getCurrentLocation().getY(), 0, 1, getActiveBoss().getHp(), getActiveBoss().getMaxHit(), getActiveBoss().getAttack(), getActiveBoss().getDefence()/*, false*/);
		new Broadcast(getActiveBoss().getBossName() + " has spawned " + getCurrentLocation().getLocationName() + " use ::galvek to fight.").copyMessageToChatbox().submit();
		Discord.writeEventMessage(getActiveBoss().getBossName() + " has spawned " + getCurrentLocation().getLocationName() + " use ::galvek to fight.");
	}


	/**
	 * Rewards all players in the area at the time of the fight
	 */
	public static void rewardPlayers() {
		PlayerHandler.nonNullStream().filter(p -> Boundary.isIn(p, Boundary.GALVEK_AREA))
				.forEach(p -> {
					defeated(p);
					PlayerHandler.executeGlobalMessage("<img=20> <col=dbffba><shad=1>" + p.getLoginName() + " has defeated the Mighty Galvek!");
					setCurrentLocation(null);
				});
	}

	public static void defeated(Player player) {
		activated = false;
		PlayerHandler.nonNullStream().filter(p -> Boundary.isIn(p, Boundary.GALVEK_AREA))
				.forEach(p -> {

					Server.itemHandler.createGroundItem(p, 995,
							2989, 3949, 0, 250_000 + Misc.getRandom(800_000));
					p.sendMessage("Well done you managed to defeat the mighty galvek.");
					AchievementHandler.activate(p, AchievementList.KILL_GALVEK, 1);//NEW ACHIEVEMNTS
					if (Misc.hasOneOutOf(GALVEK_PET_CHANCE)) {
						p.getItems().addItem(16017, 1);
						PlayerHandler.executeGlobalMessage("@red@" + Misc.capitalize(p.getLoginName()) + " @blu@has received the @red@galvek pet @blu@from the @red@mighty galvek.");
					}
					int random = Misc.random(15);
					if (random > 14) {
						int rareitem1 = 0;
						rareitem1 = Misc.random(ULTRA.length - 1);
						p.galvekUltraRareReward[0][0] = ULTRA[rareitem1];
						Server.itemHandler.createGroundItem(p, p.galvekUltraRareReward[0][0],
								2976, 3950, 0, 1);

						Server.itemHandler.createGroundItem(p, COMMONLOOT[Misc.random(COMMONLOOT.length - 1)],
								2981, 3950, 0, 50 + Misc.getRandom(120));

						Server.itemHandler.createGroundItem(p, COMMONLOOT[Misc.random(COMMONLOOT.length - 1)],
								2986, 3950, 0, 50 + Misc.getRandom(120));

						//Announcement.announce("<img=21>@red@" + p.getLoginName() + " @blu@galvek has dropped hes @red@Heavy Loot package!");
						Announcement.announce("<img=21>@red@" + p.getLoginName() + " @blu@galvek has dropped: @red@"+ ItemDef.forId(p.galvekUltraRareReward[0][0]).getName() +"!");
						return;
					}

					if (random > 10) {
						int rareitem = 0;
						rareitem = Misc.random(SUPERRARELOOT.length - 1);
						p.galvekSuperRareReward[0][0] = SUPERRARELOOT[rareitem];
						Server.itemHandler.createGroundItem(p, p.galvekSuperRareReward[0][0],
								2986, 3955, 0, 1);

						Server.itemHandler.createGroundItem(p, COMMONLOOT[Misc.random(COMMONLOOT.length - 1)],
								2982, 3955, 0, 30 + Misc.getRandom(100));

						Server.itemHandler.createGroundItem(p, COMMONLOOT[Misc.random(COMMONLOOT.length - 1)],
								2978, 3955, 0, 30 + Misc.getRandom(100));
						//Announcement.announce("<img=21> @red@" + p.getLoginName() + " @blu@galvek has dropped hes @red@Heavy Loot package!");
						Announcement.announce("<img=21>@red@" + p.getLoginName() + " @blu@galvek has dropped: @red@"+ ItemDef.forId(p.galvekSuperRareReward[0][0]).getName() +"!");
						return;
					}

					if (random > 8) {
						int rareitem2 = 0;
						rareitem2 = Misc.random(RARELOOT.length - 1);
						p.galvekRareReward[0][0] = RARELOOT[rareitem2];
						Server.itemHandler.createGroundItem(p, p.galvekRareReward[0][0],
								2978, 3959, 0, 1);


						Server.itemHandler.createGroundItem(p, ALWAYSLOOT[Misc.random(ALWAYSLOOT.length - 1)],
								2982, 3959, 0, 10 + Misc.getRandom(80));

						Server.itemHandler.createGroundItem(p, ALWAYSLOOT[Misc.random(ALWAYSLOOT.length - 1)],
								2986, 3959, 0, 10 + Misc.getRandom(80));

						//Announcement.announce("<img=21> @red@" + p.getLoginName() + " @blu@galvek has dropped hes @red@Heavy Loot package!");
						Announcement.announce("<img=21>@red@" + p.getLoginName() + " @blu@galvek has dropped: @red@"+ ItemDef.forId(p.galvekRareReward[0][0]).getName() +"!");
						return;
					}

					if (random > 4) {
						Server.itemHandler.createGroundItem(p, COMMONLOOT[Misc.random(COMMONLOOT.length - 1)],
								2986, 3962, 0, 10 + Misc.getRandom(35));

						Server.itemHandler.createGroundItem(p, COMMONLOOT[Misc.random(COMMONLOOT.length - 1)],
								2982, 3962, 0, 10 + Misc.getRandom(35));

						Server.itemHandler.createGroundItem(p, COMMONLOOT[Misc.random(COMMONLOOT.length - 1)],
								2979, 3962, 0, 10 + Misc.getRandom(35));

						Server.itemHandler.createGroundItem(p, ALWAYSLOOT[Misc.random(ALWAYSLOOT.length - 1)],
								2976, 3960, 0, 10 + Misc.getRandom(20));
						Announcement.announce("<img=21> @red@" + p.getLoginName() + " @blu@galvek has dropped hes @red@Common Loot package!");
						//Announcement.announce("<img=21>@red@" + p.getLoginName() + " @blu@galvek has dropped: @red@"+ ItemDef.forId(COMMONLOOT.length).getName() +"!");
						return;
					}
					//	} else {
					if (random > 2) {
						Server.itemHandler.createGroundItem(p, ALWAYSLOOT[Misc.random(ALWAYSLOOT.length - 1)],
								2976, 3956, 0, 10 + Misc.getRandom(20));

						Server.itemHandler.createGroundItem(p, ALWAYSLOOT[Misc.random(ALWAYSLOOT.length - 1)],
								2976, 3953, 0, 10 + Misc.getRandom(20));

						Server.itemHandler.createGroundItem(p, ALWAYSLOOT[Misc.random(ALWAYSLOOT.length - 1)],
								2976, 3948, 0, 10 + Misc.getRandom(20));
						Announcement.announce("<img=21> @red@" + p.getLoginName() + " @blu@galvek has dropped hes @red@Common Loot package!");
						//Announcement.announce("<img=21>@red@" + p.getLoginName() + " @blu@galvek has dropped: @red@"+ ItemDef.forId(COMMONLOOT.length).getName() +"!");
						return;
					}

				});
	}




	public static int generateTime(){
		return Misc.toCycles(167, TimeUnit.MINUTES);
	}
	
	public static CycleEventContainer getCycleEventContainer() {
		return cycleEventContainer;
	}

	public static void setCycleEventContainer(CycleEventContainer cycleEventContainer) {
		GalvekEventBossHandler.cycleEventContainer = cycleEventContainer;
	}

	public static Boundary getActiveBoundary() {
		return getCurrentLocation().getBoundary();
	}

	public static NPC getActiveNPC() {
		return activeNPC;
	}

	public static void setActiveNPC(NPC activeNPC) {
		GalvekEventBossHandler.activeNPC = activeNPC;
	}

	public static boolean isDidSend() {
		return didSend;
	}

	public static void setDidSend(boolean didSend) {
		GalvekEventBossHandler.didSend = didSend;
	}
	
	private static String name;
	public static String getName() {
		return name;
	}

	public static void setName(String name) {
		GalvekEventBossHandler.name = name;
	}

	public static GalvekEventBossSpawns getCurrentLocation() {
		return currentLocation;
	}

	public static void setCurrentLocation(GalvekEventBossSpawns currentLocation) {
		GalvekEventBossHandler.currentLocation = currentLocation;
	}

	public static GalvekEventBosses getActiveBoss() {
		return activeBoss;
	}

	public static void setActiveBoss(GalvekEventBosses activeBoss) {
		GalvekEventBossHandler.activeBoss = activeBoss;
	}

	

}