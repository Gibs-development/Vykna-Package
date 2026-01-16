package io.xeros.content.koranian_event;

import com.google.common.collect.Lists;
import io.xeros.Configuration;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
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
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @author C.T, for koranse.
 */
public class KoranianEventBossHandler {
	
	private static int KORANIAN_KEY = 4185;
	
	public static boolean activated = false;
	
	public enum KoranianEventBosses {

		KORANIAN(3833, "The nightmare", 3500, 35, 650, 450);




		private int npcId;

		private  String bossName;

		private  int hp;

		private  int maxHit;

		private  int attack;

		private int defence;

		KoranianEventBosses(final int npcId, final String bossName, final int hp, final int maxHit, final int attack, final int defence) {
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

		public static KoranianEventBosses getRandom() {
			return KoranianEventBosses.values()[Misc.random(0, KoranianEventBosses.values().length - 1)];
		}

		public int getNpcId() {
			return npcId;
		}

		public void setNpcId(int id) {
			this.npcId = id;
		}
	}

	
	private static KoranianEventBossSpawns currentLocation;


	private static KoranianEventBosses activeBoss;
	
	
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
				
				if(getActiveBoss() != null) {
					destroyBoss();
					container.setTick(Misc.toCycles(145, TimeUnit.MINUTES));
					activated = false;
					Configuration.NIGHTMARE_EVENT_TIMER = KoranianEventBossHandler.generateTime();
					return;
				}
				container.setTick(generateTime());
				spawnBoss();
				Configuration.NIGHTMARE_EVENT_TIMER = KoranianEventBossHandler.generateTime();
				activated = true;
			    }

		    @Override
		    public void onStopped() {
				
			   }

		};
		setCycleEventContainer(CycleEventHandler.getSingleton().addEvent(KoranianEventBossHandler.class, cycleEvent, generateTime()));
	}    

	public static void destroyBoss() {
		if(getActiveBoss() == null)
			return;
		NPCHandler.despawn(getActiveBoss().npcId, 0);
		setActiveBoss(null);
		setCurrentLocation(null);
		activated = false;
	}


	public static void spawnBoss() {
		setCurrentLocation(KoranianEventBossSpawns.generateLocation());
		setActiveBoss(KoranianEventBosses.getRandom());
		NPCSpawning.spawnNpcOld(getActiveBoss().getNpcId(), getCurrentLocation().getX(), getCurrentLocation().getY(), 0, 1, getActiveBoss().getHp(), getActiveBoss().getMaxHit(), getActiveBoss().getAttack(), getActiveBoss().getDefence()/*, false*/);
		new Broadcast(getActiveBoss().getBossName() + " has been seen near " + getCurrentLocation().getLocationName() + ".").copyMessageToChatbox().submit();
		Discord.writeEventMessage(getActiveBoss().getBossName() + " has been seen near " + getCurrentLocation().getLocationName() + ".");
	}



	/**
	 * Rewards all players in the area at the time of the fight
	 */
	public static void rewardPlayers() {
		PlayerHandler.executeGlobalMessage("@blu@The nightmare has now been killed.");
		List<String> givenToIP = Lists.newArrayList();
		PlayerHandler.nonNullStream().filter(p -> Boundary.isIn(p, Boundary.KORANIAN_AREA))
				.forEach(p -> {
					if(!givenToIP.contains(p.connectedFrom)) {
					if (p.getKoraneDamageCounter() >= 150) {
						p.sendMessage("@red@You receive an Nightmare key for defeating the nightmare.");
						p.getItems().addItemUnderAnyCircumstance(KORANIAN_KEY, 1);
						givenToIP.add(p.connectedFrom);
						if (p.hasFollower && (p.petSummonId == 30123)) {
							if (Misc.random(100) < 25) {
								p.getItems().addItemUnderAnyCircumstance(KORANIAN_KEY, 2);
								p.sendMessage("Your pet provided 2 extra keys!");
							}
						}
						if ((Configuration.DOUBLE_DROPS_TIMER > 0 || Configuration.DOUBLE_DROPS)) {
							p.getItems().addItemUnderAnyCircumstance(KORANIAN_KEY, 2);
							p.sendMessage("@gre@[WOGW] Double drops is activated and you received 2 extra keys!");
						}
					} else if (p.getKoraneDamageCounter() < 150) {
						p.sendMessage("@red@You must deal @red@150+</col> damage to receive a key!");
					}
					} else {
						p.sendMessage("You can only receive 1 drop per @redIP ADDRESS!");
					}
					p.setKoraneDamageCounter(0);
				});
	}



	public static int generateTime(){
		return Misc.toCycles(145, TimeUnit.MINUTES);
	}
	
	public static CycleEventContainer getCycleEventContainer() {
		return cycleEventContainer;
	}

	public static void setCycleEventContainer(CycleEventContainer cycleEventContainer) {
		KoranianEventBossHandler.cycleEventContainer = cycleEventContainer;
	}

	public static Boundary getActiveBoundary() {
		return getCurrentLocation().getBoundary();
	}

	public static NPC getActiveNPC() {
		return activeNPC;
	}

	public static void setActiveNPC(NPC activeNPC) {
		KoranianEventBossHandler.activeNPC = activeNPC;
	}

	public static boolean isDidSend() {
		return didSend;
	}

	public static void setDidSend(boolean didSend) {
		KoranianEventBossHandler.didSend = didSend;
	}
	
	private static String name;
	public static String getName() {
		return name;
	}

	public static void setName(String name) {
		KoranianEventBossHandler.name = name;
	}

	public static KoranianEventBossSpawns getCurrentLocation() {
		return currentLocation;
	}

	public static void setCurrentLocation(KoranianEventBossSpawns currentLocation) {
		KoranianEventBossHandler.currentLocation = currentLocation;
	}

	public static KoranianEventBosses getActiveBoss() {
		return activeBoss;
	}

	public static void setActiveBoss(KoranianEventBosses activeBoss) {
		KoranianEventBossHandler.activeBoss = activeBoss;
	}

	

}