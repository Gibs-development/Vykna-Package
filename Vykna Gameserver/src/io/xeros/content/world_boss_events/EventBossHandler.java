package io.xeros.content.world_boss_events;

import com.google.common.collect.Lists;
import io.xeros.Configuration;
import io.xeros.content.combat.Hitmark;
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
 * @author C.T, koranse
 */
public class EventBossHandler {
	
	private static int GLOD_KEY = 4589;
	
	public static boolean activated = false;
	
	public enum EventBosses {

		BOSS1(5129, "True power of glod", 3000, 36, 600, 300);

		private int npcId;

		private  String bossName;

		private  int hp;

		private  int maxHit;

		private  int attack;

		private int defence;

		EventBosses(final int npcId, final String bossName, final int hp, final int maxHit, final int attack, final int defence) {
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

		public static EventBosses getRandom() {
			return EventBosses.values()[Misc.random(0, EventBosses.values().length - 1)];
		}

		public int getNpcId() {
			return npcId;
		}

		public void setNpcId(int id) {
			this.npcId = id;
		}
	}

	
	private static EventBossSpawns currentLocation;


	private static EventBosses activeBoss;
	
	
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
					container.setTick(Misc.toCycles(50, TimeUnit.MINUTES));
					activated = false;
					setCurrentLocation(null);
					Configuration.GLOD_EVENT_TIMER = EventBossHandler.generateTime();
					return;
				}
				container.setTick(generateTime());
				spawnBoss();
				Configuration.GLOD_EVENT_TIMER = EventBossHandler.generateTime();
				activated = true;
			    }

		    @Override
		    public void onStopped() {
				
			   }

		};
		setCycleEventContainer(CycleEventHandler.getSingleton().addEvent(EventBossHandler.class, cycleEvent, generateTime()));
	}    

	public static void destroyBoss() {
		if(getActiveBoss() == null)
			return;
		NPCHandler.despawn(getActiveBoss().npcId, 0);
		setCurrentLocation(null);
		setActiveBoss(null);
		activated = false;
	}


	public static void spawnBoss() {
		setCurrentLocation(EventBossSpawns.generateLocation());
		setActiveBoss(EventBosses.getRandom());
		NPCSpawning.spawnNpcOld(getActiveBoss().getNpcId(), getCurrentLocation().getX(), getCurrentLocation().getY(), 0, 1, getActiveBoss().getHp(), getActiveBoss().getMaxHit(), getActiveBoss().getAttack(), getActiveBoss().getDefence()/*, false*/);
		new Broadcast(getActiveBoss().getBossName() + " has spawned " + getCurrentLocation().getLocationName() + " use ::glod to fight.").copyMessageToChatbox().submit();
		Discord.writeEventMessage(getActiveBoss().getBossName() + " has spawned " + getCurrentLocation().getLocationName() + " use ::glod to fight.");
	}



	/**
	 * Rewards all players in the area at the time of the fight
	 */
	public static void rewardPlayers() {
		List<String> givenToIP = Lists.newArrayList();
		PlayerHandler.nonNullStream().filter(p -> Boundary.isIn(p, Boundary.Level_54_WILD))
				.forEach(p -> {
					if(!givenToIP.contains(p.connectedFrom)) {
					if (p.getGlodDamageCounter() >= 150) {
						p.sendMessage("@blu@You have participated in the true power of glod event and have been rewarded!");
						PlayerHandler.executeGlobalMessage("@red@"+p.getLoginName() + " has retrieved the glod key at level " + p.wildLevel + " wild!");
						p.getItems().addItemUnderAnyCircumstance(GLOD_KEY, 1);
						givenToIP.add(p.connectedFrom);
						if (p.hasFollower && (p.petSummonId == 30123)) {
							if (Misc.random(100) < 25) {
								p.getItems().addItemUnderAnyCircumstance(GLOD_KEY, 2);
								p.sendMessage("Your pet provided 2 extra keys!");
								//PlayerHandler.executeGlobalMessage("@red@"+p.getLoginName() + " has retrieved 2 glod keys at level " + p.wildLevel + " wild!");
							}
						}
						if ((Configuration.DOUBLE_DROPS_TIMER > 0 || Configuration.DOUBLE_DROPS)) {
							p.getItems().addItemUnderAnyCircumstance(GLOD_KEY, 2);
							p.sendMessage("@gre@[WOGW] Double drops is activated and you received 2 extra keys!");
							//PlayerHandler.executeGlobalMessage("@red@"+p.getLoginName() + " has retrieved 2 glod keys at level " + p.wildLevel + " wild!");
						}
					} else if (p.getGlodDamageCounter() < 150) {
						p.sendMessage("You must deal @red@250+</col> damage to receive a key!");
					}
					} else {
						p.sendMessage("You can only receive 1 drop per @red@IP ADDRESS!");
					}
					p.setGlodDamageCounter(0);

				});
	}





	public static int specialAmount = 0;

	public static void glodSpecial(Player player) {
		NPC GLOD = NPCHandler.getNpc(5129);

		if (GLOD.isDead) {
			return;
		}

		if (GLOD.getHealth().getCurrentHealth() < 1400 && specialAmount == 0 ||
			GLOD.getHealth().getCurrentHealth() < 1100 && specialAmount == 1 ||
			GLOD.getHealth().getCurrentHealth() < 900 && specialAmount == 2 ||
			GLOD.getHealth().getCurrentHealth() < 700 && specialAmount == 3 ||
			GLOD.getHealth().getCurrentHealth() < 400 && specialAmount == 4 ||
			GLOD.getHealth().getCurrentHealth() < 100 && specialAmount == 5) {
			NPCHandler.npcs[GLOD.getIndex()].forceChat("Glod Smash!");
			GLOD.startAnimation(6501);
			GLOD.underAttackBy = -1;
			GLOD.underAttack = false;
			NPCHandler.glodAttack = "SPECIAL";
			specialAmount++;
			PlayerHandler.nonNullStream().filter(p -> Boundary.isIn(p, Boundary.Level_54_WILD))
					.forEach(p -> {
						p.appendDamage(Misc.random(25) + 13, Hitmark.HIT);
						p.sendMessage("@red@Glod's hit trembles through your body.");
					});
		}
	}


	public static int generateTime(){
		return Misc.toCycles(50, TimeUnit.MINUTES);
	}//50
	
	public static CycleEventContainer getCycleEventContainer() {
		return cycleEventContainer;
	}

	public static void setCycleEventContainer(CycleEventContainer cycleEventContainer) {
		EventBossHandler.cycleEventContainer = cycleEventContainer;
	}

	public static Boundary getActiveBoundary() {
		return getCurrentLocation().getBoundary();
	}

	public static NPC getActiveNPC() {
		return activeNPC;
	}

	public static void setActiveNPC(NPC activeNPC) {
		EventBossHandler.activeNPC = activeNPC;
	}

	public static boolean isDidSend() {
		return didSend;
	}

	public static void setDidSend(boolean didSend) {
		EventBossHandler.didSend = didSend;
	}
	
	private static String name;
	public static String getName() {
		return name;
	}

	public static void setName(String name) {
		EventBossHandler.name = name;
	}

	public static EventBossSpawns getCurrentLocation() {
		return currentLocation;
	}

	public static void setCurrentLocation(EventBossSpawns currentLocation) {
		EventBossHandler.currentLocation = currentLocation;
	}

	public static EventBosses getActiveBoss() {
		return activeBoss;
	}

	public static void setActiveBoss(EventBosses activeBoss) {
		EventBossHandler.activeBoss = activeBoss;
	}

	

}