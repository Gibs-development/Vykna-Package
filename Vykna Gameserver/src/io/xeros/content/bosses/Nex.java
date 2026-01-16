package io.xeros.content.bosses;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.achievements.AchievementHandler;
import io.xeros.achievements.AchievementList;
import io.xeros.content.NexInterface;
import io.xeros.content.collection_log.CollectionLog;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.death.NPCDeath;
import io.xeros.content.combat.melee.CombatPrayer;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.model.CombatType;
import io.xeros.model.collisionmap.doors.Location;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.cycleevent.DelayEvent;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.entity.HealthStatus;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;

import java.util.*;
import java.util.stream.Collectors;

import static io.xeros.Server.npcHandler;
import static io.xeros.content.bosses.nightmare.NightmareConstants.HEALTH;
import static io.xeros.model.entity.player.PlayerHandler.getPlayers;
import static io.xeros.model.entity.player.PlayerHandler.players;

/**
 *
 * @author C.T for runerogue
 *
 */

public class Nex {

	public static final int HEALTH = 3400;

	public static boolean activated = false;

	public static final int NEX_ID = 11278;

	public static NPC NEX;
	public static NPC FUMUS;
	public static NPC UMBRA;
	public static NPC CRUOR;
	public static NPC GLACIES;

	private static boolean[] magesKilled = new boolean[4];
	private static boolean[] magesAttackable = new boolean[4];
	private static boolean[] attacks = new boolean[18];
	private static boolean zarosStage;
	private static int nexHealth;

	public static int phase;

	public static void spawn() {
		phase = 0;
		activated = false;
		zarosStage = false;

		nexHealth = HEALTH;

		for (int i = 0; i < 4; i++) {
			magesKilled[i] = magesAttackable[i] = false;
		}

		NPCHandler.despawn(11278, 8);//Nex
		despawn();

		NEX = NPC.of(11278, new Position(2924, 5203, 8));

		FUMUS = NPC.of(11283, new Position(2913, 5215, 8));
		UMBRA = NPC.of(11284, new Position(2937, 5215, 8));
		CRUOR = NPC.of(11285, new Position(2937, 5191, 8));
		GLACIES = NPC.of(11286, new Position(2913, 5191, 8));

		FUMUS.facePosition(2925, 5203);
		UMBRA.facePosition(2925, 5203);
		CRUOR.facePosition(2925, 5203);
		GLACIES.facePosition(2925, 5203);
		NEX.getBehaviour().setAggressive(true);


	}

	public static void despawn() {

		NPCHandler.despawn(11283, 8);

		NPCHandler.despawn(11284, 8);

		NPCHandler.despawn(11285, 8);

		NPCHandler.despawn(11286, 8);
	}


	public void handleAttackTest(Player player) {
		NPC nex = NPCHandler.getNpc(11278);
		NexInterface.update(player);
		if (nex.getHealth().getCurrentHealth() <= 0) {
			return;
		}
		if (!Boundary.isIn(nex, Boundary.NEX_BOSS_ROOM)) {
			return;
		}
		if (!Boundary.isIn(player, Boundary.NEX_BOSS_ROOM)) {
			return;
		}
		if (phase == 0) {
			if (Misc.random(40) == 1) {
				if (!Boundary.isIn(player, Boundary.NEX_BOSS_ROOM)) {
					return;
				}
				nex.forceChat("There is... NO ESCAPE!");
				nex.startAnimation(9189);
				walkToCenter(player);
				if (Boundary.isIn(player, Boundary.NEX_JUMP_ATTACK)) {
					player.appendDamage(Misc.random(30, 50), Hitmark.HIT);
				}

			}
			int distance2 = player.distanceToPoint(nex.getX(), nex.getY());
			if (distance2 > 2) {
				nex.setAttackType(CombatType.MAGE);
				fireTargetedProjectile(2009);
				nex.endGfx = -1;
				nex.maxHit = 15;
				int poisonChance = Misc.random(20);
				if (poisonChance == 0) {
					player.getHealth().proposeStatus(HealthStatus.POISON, 6 + Misc.random(10), Optional.of(nex));
					player.sendMessage("@pur@Nex has sent an poison venom through your body.");
				}
				return;
			} else if (distance2 <= 2) {
				nex.setAttackType(CombatType.SPECIAL);
				nex.projectileId = -1;//Smoke phase
				nex.endGfx = -1;
				nex.maxHit = 20;
				return;
			}
		}
		if (phase == 1) {
			int distance2 = player.distanceToPoint(nex.getX(), nex.getY());
			if (distance2 > 2) {
				nex.setAttackType(CombatType.RANGE);
				fireTargetedProjectile(1997);
				nex.endGfx = -1;
				nex.maxHit = 15;
				if (Misc.random(25) == 1) {
					if (!Boundary.isIn(player, Boundary.NEX_BOSS_ROOM)) {
						return;
					}
					for (int i = 0; i < player.prayerActive.length - 1; i++) {
						if (!player.prayerActive[i])
							continue;
						player.prayerActive[i] = false;
						player.getPA().sendFrame36(CombatPrayer.PRAYER_GLOW[i], 0);
					}
					player.sendMessage("@red@Nex has managed to disable your prayers.");
					player.headIcon = -1;
					player.getPA().requestUpdates();
					applyRandomDamage(player, 3);
				}
				return;
			} else if (distance2 <= 2) {
				nex.setAttackType(CombatType.SPECIAL);
				nex.projectileId = -1;
				nex.endGfx = -1;
				nex.maxHit = 20;
				return;
			}
		}
		if (phase == 2) {
			int distance2 = player.distanceToPoint(nex.getX(), nex.getY());
			if (distance2 > 2) {
				nex.setAttackType(CombatType.MAGE);
				fireTargetedProjectile(2002);
				nex.endGfx = -1;
				nex.maxHit = 20;
				return;
			} else if (distance2 <= 2) {
				nex.setAttackType(CombatType.SPECIAL);
				nex.projectileId = -1;
				nex.endGfx = -1;
				nex.maxHit = 25;
				return;
			}

		}
		if (phase == 4) {
			int distance2 = player.distanceToPoint(nex.getX(), nex.getY());
			if (distance2 > 2) {
				nex.setAttackType(CombatType.MAGE);
				fireTargetedProjectile(2007);
				nex.endGfx = -1;
				nex.maxHit = 25;
				return;
			} else if (distance2 <= 2) {
				nex.setAttackType(CombatType.SPECIAL);
				nex.projectileId = -1;
				nex.endGfx = -1;
				nex.maxHit = 25;
				return;
			}
		}
		if (phase == 3) {
			int distance2 = player.distanceToPoint(nex.getX(), nex.getY());
			if (distance2 > 2) {
				nex.setAttackType(CombatType.MAGE);
				fireTargetedProjectile(2004);
				nex.endGfx = -1;
				nex.maxHit = 20;
				PlayerHandler.nonNullStream().filter(p -> Boundary.isIn(p, Boundary.NEX_BOSS_ROOM)).forEach(p -> {
					if (p.prayerActive[16]) {
						return;
					} else {
						p.gfx0(2005);//ice barrage
						p.sendMessage("@red@Nex has frozen you to the ground!");
						p.freezeTimer = 10;
					}
				});
				return;
			} else if (distance2 <= 2) {
				nex.setAttackType(CombatType.SPECIAL);
				nex.projectileId = -1;
				nex.endGfx = -1;
				nex.maxHit = 25;
				return;
			}
		}
	}


	public static void takeDamage(Player from, int damage) {
		NPC nex = NPCHandler.getNpc(11278);
		if (!Boundary.isIn(from, Boundary.NEX_BOSS_ROOM)) {
			return;
		}

		nexHealth = nex.getHealth().getCurrentHealth();

		if (phase == 3) {
			if (nex.getHealth().getCurrentHealth() <= 680) {
				if (magesKilled[3]) {
					phase = 4;
					nex.forceChat("NOW, THE POWER OF ZAROS!");
					zarosStage = true;
				}
				if (!magesAttackable[3]) {
					nex.forceChat("Don't fail me, Glacies!");
					sendGlobalMsg(from, "@red@Glacies is now attackable! You need to defeat him to weaken Nex!");
					nex.getHealth().setCurrentHealth(680);
					magesAttackable[3] = true;
					GLACIES.getBehaviour().setAggressive(true);
				}
				if (magesAttackable[3] && !magesKilled[3]) {
					nex.getHealth().setCurrentHealth(680);
					from.sendMessage("You need to kill Glacies before being able to damage Nex further!");
				}
			}
		}
		if (phase == 2) {
			if (nex.getHealth().getCurrentHealth() <= 1360) {
				if (magesKilled[2])
					phase = 3;
				if (!magesAttackable[2]) {
					nex.forceChat("Don't fail me, Cruor!");
					sendGlobalMsg(from, "@red@Cruor is now attackable! You need to defeat him to weaken Nex!");
					nex.getHealth().setCurrentHealth(1360);
					magesAttackable[2] = true;
					CRUOR.getBehaviour().setAggressive(true);
				}
				if (magesAttackable[2] && !magesKilled[2]) {
					nex.getHealth().setCurrentHealth(1360);
					from.sendMessage("You need to kill Cruor before being able to damage Nex further!");
				}
			}
		}
		if (phase == 1) {
			if (nex.getHealth().getCurrentHealth() <= 2040) {
				if (magesKilled[1])
					phase = 2;
				if (!magesAttackable[1]) {
					nex.forceChat("Don't fail me, Umbra!");
					sendGlobalMsg(from, "@red@Umbra is now attackable! You need to defeat him to weaken Nex!");
					magesAttackable[1] = true;
					UMBRA.getBehaviour().setAggressive(true);
				}
				if (magesAttackable[1] && !magesKilled[1]) {
					nex.getHealth().setCurrentHealth(2040);
					from.sendMessage("You need to kill Umbra before being able to damage Nex further!");
				}
			}
		}
		if (phase == 0) {
			if (nex.getHealth().getCurrentHealth() <= 2720) {
				if (magesKilled[0])
					phase = 1;
				if (!magesAttackable[0]) {
					nex.forceChat("Don't fail me, Fumus!");
					sendGlobalMsg(from, "@red@Fumus is now attackable! You need to defeat her to weaken Nex!");
					magesAttackable[0] = true;
					FUMUS.getBehaviour().setAggressive(true);
					activated = true;
				}
				if (magesAttackable[0] && !magesKilled[0]) {
					nex.getHealth().setCurrentHealth(2720);
					from.sendMessage("You need to kill Fumus before being able to damage Nex further!");
				}
			}
		}
	}

	public static boolean nexMinion(int id) {
		return id >= 11283 && id <= 11286;
	}

	public static void death(Player player, final int id) {
		if (nexMinion(id)) {
			int index = id - 11283;
			if (index >= 0) {
				magesKilled[index] = true;
			}
			return;
		} else {
			if (player.getPosition().getHeight() > 8) {
				player.sendMessage("Re-enter the instance to spawn the Nex boss again.");
				return;
			}
		}
		Server.getEventHandler().submit(new DelayEvent(60) {

			@Override
			public void onExecute() {
				spawn();
				stop();
			}
		});
	}

	public static void handleDeath(Player player) {
		NPC nex = NPCHandler.getNpc(11278);
		phase = 0;
		activated = false;
		nex.forceChat("Taste my wrath!");

		//final int x = nex.getPosition().getX(), y = nex.getPosition().getY();

		final int x = player.getX(), y = player.getY();
		Server.getEventHandler().submit(new DelayEvent(4) {

			@Override
			public void onExecute() {
				PlayerHandler.nonNullStream().filter(p -> Boundary.isIn(p, Boundary.NEX_BOSS_ROOM))
						.forEach(p -> {
							if (!Boundary.isIn(p, Boundary.NEX_BOSS_ROOM)) {
								return;
							}
							if (p.distanceToPoint(x, y) <= 3) {
								p.appendDamage(25 + Misc.getRandom(15), Hitmark.HIT);
							}
							p.getPA().createPlayersStillGfx(2013, x, y, 8, 5);//1247
						});
				this.stop();
			}
		});
	}

	public static boolean isAlive() {
		NPC nex = NPCHandler.getNpc(11278);
		return nex.isDead;
	}


	public static int[] rarerewards = {11798, 26370, 26372, 21053};//hilts etc
	static int[] veryrarerewards = {26235, 26382, 26384, 26386};//torva
	static int[] commonrewards1 = {560, 565, 566, 892, 208, 3052, 5300, 220, 11937, 3050, 5296, 13442, 11935, 13440, 1616, 824, 11232, 1516, 1514, 2362, 3025, 19484, 2364, 20849, 11212, 452, 2722, 11230}; //{item, maxAmount}
	static int[] commonrewards2 = {560, 565, 566, 892, 208, 3052, 5300, 220, 11937, 3050, 5296, 11937, 13442, 11935, 13440, 1616, 824, 11232, 1516, 1514, 2362, 3025, 19484, 2364, 20849, 11212, 452, 2722, 11230};
	static int[] commonrewards3 = {560, 565, 566, 892, 208, 3052, 5300, 220, 11937, 3050, 5296, 11937, 13442, 11935, 13440, 1616, 824, 11232, 1516, 1514, 2362, 3025, 19484, 2364, 20849, 11212, 452, 2722, 11230};


	public static void giveCommonReward1(Player p) {

		int commonitem1 = 0;
		commonitem1 = Misc.random(commonrewards1.length - 1);
		p.nexCommonReward1[0][0] = commonrewards1[commonitem1];

		switch (p.nexCommonReward1[0][0]) {
			case 560://death rune
				p.nexCommonReward1[0][1] = Misc.random(450);
				break;
			case 565://blood rune
				p.nexCommonReward1[0][1] = Misc.random(250);
				break;
			case 566://soul rune
				p.nexCommonReward1[0][1] = Misc.random(250);
				break;
			case 892://rune arrow
				p.nexCommonReward1[0][1] = Misc.random(500);
				break;
			case 11212://dragon arrow
				p.nexCommonReward1[0][1] = Misc.random(300) + 150;
				break;
			case 452://runite ore
				p.nexCommonReward1[0][1] = Misc.random(50) + 15;
				break;
			case 2364://runite bar
				p.nexCommonReward1[0][1] = Misc.random(50) + 15;
				break;
			case 20849://dragon throwing axe
				p.nexCommonReward1[0][1] = Misc.random(50) + 25;
				break;
			case 19484://dragon jav
				p.nexCommonReward1[0][1] = Misc.random(150) + 50;
				break;
			case 11230://dragon dart
				p.nexCommonReward1[0][1] = Misc.random(250) + 50;
				break;
			case 2435://prayer pot
				p.nexCommonReward1[0][1] = Misc.random(25) + 5;
				break;
			case 3025://restore pot
				p.nexCommonReward1[0][1] = Misc.random(15) + 5;
				break;
			case 2362://adamant bar
				p.nexCommonReward1[0][1] = Misc.random(75) + 20;
				break;
			case 1514://magic log
				p.nexCommonReward1[0][1] = Misc.random(45) + 15;
				break;
			case 1516://yew log
				p.nexCommonReward1[0][1] = Misc.random(45) + 20;
				break;
			case 11232://dragon dart tip
				p.nexCommonReward1[0][1] = Misc.random(250) + 75;
				break;
			case 824://rune dart tip
				p.nexCommonReward1[0][1] = Misc.random(250) + 50;
				break;
			case 1616://dragonstone
				p.nexCommonReward1[0][1] = Misc.random(45) + 25;
				break;
			case 13440://raw anglerfish
				p.nexCommonReward1[0][1] = Misc.random(40) + 80;
				break;
			case 11935://raw dark crab
				p.nexCommonReward1[0][1] = Misc.random(45) + 85;
				break;
			case 13442://anglerfish
				p.nexCommonReward1[0][1] = Misc.random(40) + 65;
				break;
			case 11937://dark crab
				p.nexCommonReward1[0][1] = Misc.random(45) + 75;
				break;
			case 208:
				p.nexCommonReward1[0][1] = Misc.random(25) + 30;
				break;
			case 220:
				p.nexCommonReward1[0][1] = Misc.random(25) + 30;
				break;
			case 3052:
				p.nexCommonReward1[0][1] = Misc.random(25) + 30;
				break;
			case 5300:
				p.nexCommonReward1[0][1] = Misc.random(25) + 30;
				break;
			case 5296:
				p.nexCommonReward1[0][1] = Misc.random(25) + 30;
				break;
			case 3050:
				p.nexCommonReward1[0][1] = Misc.random(25) + 30;
				break;
			default:
				p.nexCommonReward1[0][1] = 1;
				break;

		}
	}

	public static void giveVeryRareReward(Player p) {

		int veryrareitem = 0;
		veryrareitem = Misc.random(veryrarerewards.length - 1);
		p.nexVeryRareReward[0][0] = veryrarerewards[veryrareitem];
		PlayerHandler.executeGlobalMessage("@red@" + p.getLoginName() + " @pur@just received: @red@" + ItemDef.forId(p.nexVeryRareReward[0][0]).getName() + "@pur@ from @red@Nex!" +
				" @pur@Nex count: @red@" + p.nexKills + ".");
		Discord.writeDropsSyncMessage("News: " + p.getLoginName() + " just received: " + ItemDef.forId(p.nexVeryRareReward[0][0]).getName() + " from Nex!" +
				" Nex count: " + p.nexKills + ".");
		if (p.nexVeryRareReward[0][0] == 20849) {
			p.nexVeryRareReward[0][1] = 500;
		} else {
			p.nexVeryRareReward[0][1] = 1;
		}
	}

	public static void giveRareReward(Player player) {

		int rareitem = 0;
		rareitem = Misc.random(rarerewards.length - 1);
		player.nexRareReward[0][0] = rarerewards[rareitem];
		PlayerHandler.executeGlobalMessage("@red@" + player.getLoginName() + " @pur@just received: @red@" + ItemDef.forId(player.nexRareReward[0][0]).getName() + "@pur@ from @red@Nex!" +
				" @pur@Nex count: @red@" + player.nexKills + ".");
		Discord.writeDropsSyncMessage("News: " + player.getLoginName() + " just received: " + ItemDef.forId(player.nexRareReward[0][0]).getName() + " from Nex!" +
				" Nex count: " + player.nexKills + ".");
		if (player.nexRareReward[0][0] == 20849) {
			player.nexRareReward[0][1] = 500;
		} else {
			player.nexRareReward[0][1] = 1;
		}

	}

	public static void giveCommonReward2(Player p) {

		int commonitem2 = 0;
		commonitem2 = Misc.random(commonrewards2.length - 1);
		p.nexCommonReward2[0][0] = commonrewards2[commonitem2];

		switch (p.nexCommonReward2[0][0]) {
			case 560://death rune
				p.nexCommonReward2[0][1] = Misc.random(500);
				break;
			case 565://blood rune
				p.nexCommonReward2[0][1] = Misc.random(500);
				break;
			case 566://soul rune
				p.nexCommonReward2[0][1] = Misc.random(500);
				break;
			case 892://rune arrow
				p.nexCommonReward2[0][1] = Misc.random(250);
				break;
			case 11212://dragon arrow
				p.nexCommonReward2[0][1] = Misc.random(300) + 150;
				break;
			case 452://runite ore
				p.nexCommonReward2[0][1] = Misc.random(50) + 15;
				break;
			case 2364://runite bar
				p.nexCommonReward2[0][1] = Misc.random(50) + 15;
				break;
			case 20849://dragon throwing axe
				p.nexCommonReward2[0][1] = Misc.random(50) + 25;
				break;
			case 19484://dragon jav
				p.nexCommonReward2[0][1] = Misc.random(150) + 50;
				break;
			case 11230://dragon dart
				p.nexCommonReward2[0][1] = Misc.random(250) + 50;
				break;
			case 2435://prayer pot
				p.nexCommonReward2[0][1] = Misc.random(25) + 5;
				break;
			case 3025://restore pot
				p.nexCommonReward2[0][1] = Misc.random(15) + 5;
				break;
			case 2362://adamant bar
				p.nexCommonReward2[0][1] = Misc.random(75) + 20;
				break;
			case 1514://magic log
				p.nexCommonReward2[0][1] = Misc.random(45) + 15;
				break;
			case 1516://yew log
				p.nexCommonReward2[0][1] = Misc.random(45) + 20;
				break;
			case 11232://dragon dart tip
				p.nexCommonReward2[0][1] = Misc.random(250) + 75;
				break;
			case 824://rune dart tip
				p.nexCommonReward2[0][1] = Misc.random(250) + 50;
				break;
			case 1616://dragonstone
				p.nexCommonReward2[0][1] = Misc.random(45) + 25;
				break;
			case 13440://raw anglerfish
				p.nexCommonReward2[0][1] = Misc.random(40) + 80;
				break;
			case 11935://raw dark crab
				p.nexCommonReward2[0][1] = Misc.random(45) + 85;
				break;
			case 13442://anglerfish
				p.nexCommonReward2[0][1] = Misc.random(40) + 65;
				break;
			case 11937://dark crab
				p.nexCommonReward2[0][1] = Misc.random(45) + 75;
				break;
			case 208:
				p.nexCommonReward2[0][1] = Misc.random(25) + 30;
				break;
			case 220:
				p.nexCommonReward2[0][1] = Misc.random(25) + 30;
				break;
			case 3052:
				p.nexCommonReward2[0][1] = Misc.random(25) + 30;
				break;
			case 5300:
				p.nexCommonReward2[0][1] = Misc.random(25) + 30;
				break;
			case 5296:
				p.nexCommonReward2[0][1] = Misc.random(25) + 30;
				break;
			case 3050:
				p.nexCommonReward2[0][1] = Misc.random(25) + 30;
				break;
			default:
				p.nexCommonReward2[0][1] = 1;
				break;

		}

	}

	public static void giveCommonReward3(Player p) {

		int commonitem3 = 0;
		commonitem3 = Misc.random(commonrewards3.length - 1);
		p.nexCommonReward3[0][0] = commonrewards3[commonitem3];

		switch (p.nexCommonReward3[0][0]) {
			case 560://death rune
				p.nexCommonReward3[0][1] = Misc.random(500);
				break;
			case 565://blood rune
				p.nexCommonReward3[0][1] = Misc.random(500);
				break;
			case 566://soul rune
				p.nexCommonReward3[0][1] = Misc.random(500);
				break;
			case 892://rune arrow
				p.nexCommonReward3[0][1] = Misc.random(250);
				break;
			case 11212://dragon arrow
				p.nexCommonReward3[0][1] = Misc.random(300) + 150;
				break;
			case 452://runite ore
				p.nexCommonReward3[0][1] = Misc.random(50) + 15;
				break;
			case 2364://runite bar
				p.nexCommonReward3[0][1] = Misc.random(50) + 15;
				break;
			case 20849://dragon throwing axe
				p.nexCommonReward3[0][1] = Misc.random(50) + 25;
				break;
			case 19484://dragon jav
				p.nexCommonReward3[0][1] = Misc.random(150) + 50;
				break;
			case 11230://dragon dart
				p.nexCommonReward3[0][1] = Misc.random(250) + 50;
				break;
			case 2435://prayer pot
				p.nexCommonReward3[0][1] = Misc.random(25) + 5;
				break;
			case 3025://restore pot
				p.nexCommonReward3[0][1] = Misc.random(15) + 5;
				break;
			case 2362://adamant bar
				p.nexCommonReward3[0][1] = Misc.random(75) + 20;
				break;
			case 1514://magic log
				p.nexCommonReward3[0][1] = Misc.random(45) + 15;
				break;
			case 1516://yew log
				p.nexCommonReward3[0][1] = Misc.random(45) + 20;
				break;
			case 11232://dragon dart tip
				p.nexCommonReward3[0][1] = Misc.random(250) + 75;
				break;
			case 824://rune dart tip
				p.nexCommonReward3[0][1] = Misc.random(250) + 50;
				break;
			case 1616://dragonstone
				p.nexCommonReward3[0][1] = Misc.random(45) + 25;
				break;
			case 13440://raw anglerfish
				p.nexCommonReward3[0][1] = Misc.random(40) + 80;
				break;
			case 11935://raw dark crab
				p.nexCommonReward3[0][1] = Misc.random(45) + 85;
				break;
			case 13442://anglerfish
				p.nexCommonReward3[0][1] = Misc.random(40) + 65;
				break;
			case 11937://dark crab
				p.nexCommonReward3[0][1] = Misc.random(45) + 75;
				break;
			case 208:
				p.nexCommonReward3[0][1] = Misc.random(25) + 30;
				break;
			case 220:
				p.nexCommonReward3[0][1] = Misc.random(25) + 30;
				break;
			case 3052:
				p.nexCommonReward3[0][1] = Misc.random(25) + 30;
				break;
			case 5300:
				p.nexCommonReward3[0][1] = Misc.random(25) + 30;
				break;
			case 5296:
				p.nexCommonReward3[0][1] = Misc.random(25) + 30;
				break;
			case 3050:
				p.nexCommonReward3[0][1] = Misc.random(25) + 30;
				break;
			default:
				p.nexCommonReward3[0][1] = 1;
				break;

		}
	}

	public static void giveReward() {
		PlayerHandler.nonNullStream().filter(p -> Boundary.isIn(p, Boundary.NEX_BOSS_ROOM))
				.forEach(p -> {
					p.sendMessage("" + p.getLoginName() + " You managed to defeat nex take these rewards.");
					p.nexKills += 1;
					AchievementHandler.activate(p, AchievementList.NEX_I, 1);//NEW ACHIEVEMNTS
					AchievementHandler.activate(p, AchievementList.NEX_II, 1);//NEW ACHIEVEMNTS
					AchievementHandler.activate(p, AchievementList.NEX_III, 1);//NEW ACHIEVEMNTS
					int rewardChance = Misc.random(500);
					if (rewardChance >= 497) {
						int petChance = Misc.random(25);
						if (petChance == 1) {
							if (p.getItems().getItemCount(26348, false) == 0 && p.petSummonId != 11276) {//Add nexling here
								p.getItems().addItemUnderAnyCircumstance(26348, 1);
								PlayerHandler.executeGlobalMessage("@red@" + p.getLoginName() + " @pur@just received: @red@Nexling@pur@ from @red@Nex!");
								Discord.writeDropsSyncMessage("News: " + p.getLoginName() + "  has received: Nexling from Nex! " +
										" Nex count: " + p.nexKills + ".");
								p.getCollectionLog().handleDrop(p, 11278, 26348, 1);//Nexling collection log drop
							}
						}
						giveVeryRareReward(p);
						giveCommonReward1(p);
						giveCommonReward2(p);
						giveCommonReward3(p);
						p.getCollectionLog().handleDrop(p, 11278, p.nexVeryRareReward[0][0], p.nexVeryRareReward[0][1]);
						p.getItems().addItemUnderAnyCircumstance(p.nexVeryRareReward[0][0], p.nexVeryRareReward[0][1]);
						p.getItems().addItemUnderAnyCircumstance(p.nexCommonReward1[0][0], p.nexCommonReward1[0][1]);
						p.getItems().addItemUnderAnyCircumstance(p.nexCommonReward2[0][0], p.nexCommonReward2[0][1]);
						p.getItems().addItemUnderAnyCircumstance(p.nexCommonReward3[0][0], p.nexCommonReward3[0][1]);
					} else if (rewardChance >= 484 && rewardChance < 497) {
						giveRareReward(p);
						giveCommonReward1(p);
						giveCommonReward2(p);
						giveCommonReward3(p);
						p.getCollectionLog().handleDrop(p, 11278, p.nexRareReward[0][0], p.nexRareReward[0][1]);//
						p.getItems().addItemUnderAnyCircumstance(p.nexRareReward[0][0], p.nexRareReward[0][1]);
						p.getItems().addItemUnderAnyCircumstance(p.nexCommonReward1[0][0], p.nexCommonReward1[0][1]);
						p.getItems().addItemUnderAnyCircumstance(p.nexCommonReward2[0][0], p.nexCommonReward2[0][1]);
						p.getItems().addItemUnderAnyCircumstance(p.nexCommonReward3[0][0], p.nexCommonReward3[0][1]);
					} else {
						giveCommonReward1(p);
						giveCommonReward2(p);
						giveCommonReward3(p);
						p.getItems().addItemUnderAnyCircumstance(p.nexCommonReward1[0][0], p.nexCommonReward1[0][1]);
						p.getItems().addItemUnderAnyCircumstance(p.nexCommonReward2[0][0], p.nexCommonReward2[0][1]);
						p.getItems().addItemUnderAnyCircumstance(p.nexCommonReward3[0][0], p.nexCommonReward3[0][1]);
					}
				});
	}


	public static void sendGlobalMsg(Player original, String message) {
		for (Player p : Misc.getCombinedPlayerList(original)) {
			if (p != null) {
				p.sendMessage(message);
			}
		}
	}


	public boolean zarosStage() {
		return zarosStage;
	}

	public static boolean nexMob(int id) {
		return id == 11278 || nexMinion(id);
	}

	public static boolean checkAttack(Player p, int npc) {
		if (npc == 11278) {//Nex id
			for (int i = 0; i < magesAttackable.length; i++) {
				if (magesAttackable[i] && !magesKilled[i]) {
					int index = 11283 + i;
					p.sendMessage("You need to kill " + NpcDef.forId(index).getName()
							+ " before being able to damage Nex further!");
					return false;
				}
			}
			return true;
		}
		int index = npc - 11283;
		if (!magesAttackable[index] && !magesKilled[index]) {
			p.sendMessage("" + NpcDef.forId(npc).getName() + " is currently being protected by Nex!");
			return false;
		}
		return true;
	}


	private static NPCHandler handler() {
		return npcHandler;
	}

	private void applyRandomDamage(Player player, int amount) {
		player.appendDamage(Misc.random(amount) + 1, Hitmark.HIT);
	}


	private static final Location NORTH = new Location(2925, 5192);

	private void walkToCenter(Player player) {
		if (!Boundary.isIn(player, Boundary.NEX_BOSS_ROOM)) {
			return;
		}
		NPC nex = NPCHandler.getNpc(11278);
		CycleEventHandler.getSingleton().addEvent(0, nex, new CycleEvent() {

			int cycle;

			@Override
			public void execute(CycleEventContainer container) {
				if (nex.isDead() || player.isDead) {
					container.stop();
					return;
				}

				if (cycle == 0) {
					nex.setPlayerAttackingIndex(0);
					nex.facePlayer(0);
					nex.underAttack = false;
					nex.lastRandomlySelectedPlayer = 0;
					nex.facePosition(NORTH.getX(), NORTH.getY());
				}
				if (cycle == 2) {
					nex.teleport(NORTH.getX(), NORTH.getY(), 8);
					nex.facePlayer(1);
					nex.setPlayerAttackingIndex(1);
				}
				if (cycle == 8) {
					nex.facePosition(2925, 5203);
					nex.facePlayer(1);
					nex.setPlayerAttackingIndex(1);
				}
				if (cycle >= 10) {
					nex.teleport(2925, 5203, 8);
					nex.facePlayer(1);
					nex.setPlayerAttackingIndex(1);
					container.stop();
				}
				cycle++;
			}

		}, 1);
	}

	//Checks weather there is any players
	// inside the chamber if not then respawn the instance.
	public static void respawnCheck() {
		if (Boundary.getPlayersInBoundary(Boundary.NEX_BOSS_ROOM) == 0) {
			spawn();
		}
	}


	private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

	// Collection log items had to be handled this way.
	static {
		items.put(LootRarity.RARE, Arrays.asList(
				new GameItem(26235, 1),
				new GameItem(26382, 1),
				new GameItem(26384, 1),
				new GameItem(26386, 1),

				new GameItem(11798, 1),
				new GameItem(26370, 1),
				new GameItem(26372, 1)

		));
	}


	public static ArrayList<GameItem> getRareDrops() {
		ArrayList<GameItem> drops = new ArrayList<>();
		List<GameItem> found = items.get(LootRarity.RARE);
		for (GameItem f : found) {
			boolean foundItem = false;
			for (GameItem drop : drops) {
				if (drop.getId() == f.getId()) {
					foundItem = true;
					break;
				}
			}
			if (!foundItem) {
				drops.add(f);
			}
		}
		return drops;
	}


	private void fireTargetedProjectile(int projectileId) {
		PlayerHandler.nonNullStream().filter(p -> Boundary.isIn(p, Boundary.NEX_BOSS_ROOM))
				.forEach(p -> {
					NPC nex = NPCHandler.getNpc(11278);
					int offY = (nex.getX() - p.getX()) * -1;
					int offX = (nex.getY() - p.getY()) * -1;
					int delay = 3;//hit delay timer
					p.getPA().createPlayersProjectile(nex.getX(), nex.getY(), offX, offY, 50, 110, projectileId, 40, 28, -p.getIndex() - 1, 50, delay);//65
				});
	}


}
