package io.xeros.content.bosses;

import io.xeros.Server;
import io.xeros.achievements.AchievementHandler;
import io.xeros.achievements.AchievementList;
import io.xeros.content.bosses.hydra.CombatProjectile;
import io.xeros.content.combat.Hitmark;
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
import io.xeros.model.entity.npc.actions.LoadSpell;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.xeros.Server.npcHandler;
import static io.xeros.model.entity.player.PlayerHandler.getPlayers;

/**
 *
 * @author C.T for runerogue
 *
 */

public class SeaSnake {

	public static boolean activated = false;

	public static final int SNAKE_ID = 1101;

	public static NPC SNAKE;

	public static void spawnSnake() {
		activated = false;
		LoadSpell.minions = false;
		int health = 0;

		NPCHandler.despawn(SNAKE_ID, 0);//Sea snake
		despawn();
		if (Boundary.getPlayersInBoundary(Boundary.SEA_SNAKE_DECK) >= 0 && Boundary.getPlayersInBoundary(Boundary.SEA_SNAKE_DECK) <= 1) {
			health = 2650;
		} else if (Boundary.getPlayersInBoundary(Boundary.SEA_SNAKE_DECK) >= 2 && Boundary.getPlayersInBoundary(Boundary.SEA_SNAKE_DECK) <= 10) {
			health = 3275;
		} else if (Boundary.getPlayersInBoundary(Boundary.SEA_SNAKE_DECK) >= 11 && Boundary.getPlayersInBoundary(Boundary.SEA_SNAKE_DECK) <= 20) {
			health = 5325;
		} else if (Boundary.getPlayersInBoundary(Boundary.SEA_SNAKE_DECK) >= 21 && Boundary.getPlayersInBoundary(Boundary.SEA_SNAKE_DECK) <= 80) {
			health = 8500;
		}
		NPCSpawning.spawnNpcOld(SNAKE_ID, 1670, 5928, 0, 1, health, 35, 1400, 250);

	}

	public static void despawn() {
		NPCHandler.despawn(1097, 0);//Sea snake minion
	}


	public static boolean seaSnakeMob(int id) {
		return id == SNAKE_ID;
	}


	public static void death(Player player, final int id) {
		Server.getEventHandler().submit(new DelayEvent(60) {

			@Override
			public void onExecute() {
				spawnSnake();
				stop();
			}
		});
	}

	public static void handleDeath() {
		NPC snake = NPCHandler.getNpc(SNAKE_ID);
		activated = false;
		snake.forceChat("I shall defeat you next time warriors!");
		PlayerHandler.executeGlobalMessage("@red@The powerful sea snake as now been defeated.");

		Server.getEventHandler().submit(new DelayEvent(4) {

			@Override
			public void onExecute() {
				for (Player p : getPlayers()) {
					final int x = p.getPosition().getX(), y = p.getPosition().getY();
					if (!Boundary.isIn(p, Boundary.SEA_SNAKE_DECK)) {
						return;
					}
					if (p == null)
						continue;
					if (p.distanceToPoint(x, y) <= 3) {
						p.appendDamage(25 + Misc.getRandom(15), Hitmark.HIT);
					}
					if (p.distanceToPoint(x, y) <= 20) {

						for (int x_ = x - 2; x_ < x + 2; x_++) {
							for (int y_ = y - 2; y_ < y + 2; y_++) {
								p.getPA().createPlayersStillGfx(456, x_, y_, 0, 5);//456
							}
						}
					}
				}
				this.stop();
			}
		});
	}



	public static int[] rarerewards = {6769, 6769, 6769, 6769, 692, 693, 691, 6828, 6199, 24119, 2425, 6769, 6769, 6769,};//$5 scroll, 25k foe, 50k foe, 10k foe, super box, m box, black dragon hunter crossbow, Vorkath head
	static int[] veryrarerewards = {6769, 6769, 6769, 6769, 6769, 6769, 2403, 21752, 8167, 13346, 6769, 6769, 6769,};//$10 scroll, Runerogue ring, FoE Mystery Chest, ultra box
	static int[] commonrewards1 = {560,565,566,892,208, 3052, 5300, 220, 11937, 3050, 5296, 13442, 11935, 13440, 1616, 824, 11232, 1516, 1514, 2362, 3025, 19484, 2364, 20849, 11212, 452, 2722, 11230}; //{item, maxAmount}
	static int[] commonrewards2 = {560,565,566,892,208, 3052, 5300, 220, 11937, 3050, 5296, 11937, 13442, 11935, 13440, 1616, 824, 11232, 1516, 1514, 2362, 3025, 19484, 2364, 20849, 11212, 452, 2722, 11230};
	static int[] commonrewards3 = {560,565,566,892,208, 3052, 5300, 220, 11937, 3050, 5296, 11937, 13442, 11935, 13440, 1616, 824, 11232, 1516, 1514, 2362, 3025, 19484, 2364, 20849, 11212, 452, 2722, 11230};


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
					PlayerHandler.executeGlobalMessage("@red@"+p.getLoginName()+" @pur@just received: @red@" + ItemDef.forId(p.nexVeryRareReward[0][0]).getName() + "@pur@ from @red@Powerful sea snake!" +
							" @pur@Kc: @red@" + p.seaSnakeKills + ".");
		            Discord.writeDropsSyncMessage("News: "+p.getLoginName()+" just received: " + ItemDef.forId(p.nexVeryRareReward[0][0]).getName() + " from Powerful sea snake!" +
							" Sea snake count: " + p.seaSnakeKills + ".");
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
					PlayerHandler.executeGlobalMessage("@red@"+player.getLoginName()+" @pur@just received: @red@" + ItemDef.forId(player.nexRareReward[0][0]).getName() + "@pur@ from @red@Powerful sea snake!" +
							" @pur@Kc: @red@" + player.seaSnakeKills + ".");
		            Discord.writeDropsSyncMessage("News: "+player.getLoginName()+" just received: " + ItemDef.forId(player.nexRareReward[0][0]).getName() + " from Powerful sea snake!" +
							" Sea snake count: " + player.seaSnakeKills + ".");
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
		PlayerHandler.nonNullStream().filter(p -> Boundary.isIn(p, Boundary.SEA_SNAKE_DECK))
				.forEach(p -> {
					p.sendMessage("@red@"+p.getLoginName()+" @blu@You managed to defeat the powerful sea snake take these rewards.");
					p.seaSnakeKills+=1;
					int rewardChance = Misc.random(500);
					if (rewardChance >= 497) {
						int petChance = Misc.random(120);
						if (petChance == 1) {
						//	if (p.getItems().getItemCount(26348, false) == 0 && p.petSummonId != 11276) {//Add sea snake here
							//	p.getItems().addItemUnderAnyCircumstance(26348, 1);
						//		PlayerHandler.executeGlobalMessage("@red@" + p.getLoginName() + " @pur@just received: @red@Sea snake@pur@ from @red@The powerful sea snake!");
						//		Discord.writeDropsSyncMessage("News: "+ p.getLoginName() + "  has received: the sea snake pet! "+
						//				" Sea snake count: " + p.seaSnakeKills + ".");
						//		//p.getCollectionLog().handleDrop(p, 11278, 26348, 1);//Nexling collection log drop
						//	}
						}
						giveVeryRareReward(p);
						giveCommonReward1(p);
						giveCommonReward2(p);
						giveCommonReward3(p);
						p.getItems().addItemUnderAnyCircumstance(p.nexVeryRareReward[0][0], p.nexVeryRareReward[0][1]);
						p.getItems().addItemUnderAnyCircumstance(p.nexCommonReward1[0][0], p.nexCommonReward1[0][1]);
						p.getItems().addItemUnderAnyCircumstance(p.nexCommonReward2[0][0], p.nexCommonReward2[0][1]);
						p.getItems().addItemUnderAnyCircumstance(p.nexCommonReward3[0][0], p.nexCommonReward3[0][1]);
					} else if (rewardChance >= 484 && rewardChance < 497) {
						giveRareReward(p);
						giveCommonReward1(p);
						giveCommonReward2(p);
						giveCommonReward3(p);
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


	//Checks weather there is any players
	// inside the sea snake if not then respawn the instance.
	public static void respawnCheck() {
		if (Boundary.getPlayersInBoundary(Boundary.SEA_SNAKE_DECK) == 0) {
			spawnSnake();
		}
	}


	private static final Location[] FIRE_SPAWNS = {
			new Location(1669, 5919),
	};

	private static final int LIGHTNING_GFX = 1666;
	private static final CombatProjectile LIGHTNING_PROJECTILE = new CombatProjectile(1665, 50, 0, 0, 100, 0, 50);
	public static void sendFire(Player player) {
		NPC npc = NPCHandler.getNpc(SNAKE_ID);
		List<Location> steps = Stream
				.of(FIRE_SPAWNS)
				.map(loc -> new Location(loc.getX(), loc.getY(), 0))
				.collect(Collectors.toList());

		CycleEventHandler.getSingleton().addEvent(npc, new CycleEvent() {

			int tick;

			@Override
			public void execute(CycleEventContainer container) {
				if (npc.isDead() || player.isDead) {
					container.stop();
					return;
				}

				if (tick == 0) {
				} else if (tick >= 2 && tick <= 15) {
					steps.stream().forEach(loc -> {
						Location nextStep = npc.getRegionProvider().get(loc.getX(), loc.getY()).getNextStepLocation(loc.getX(), loc.getY(), player.getX(), player.getY(), 0, 0, 0);
						loc.setX(nextStep.getX());
						loc.setY(nextStep.getY());
						player.getPA().stillGfx(1668, loc.getX(), loc.getY(), 0, 0);//FIRE_GFX

						int index = 0;
						if (player.isPlayer() && steps.get(index).equals(player.getLocation())) {
							player.appendDamage(npc, Misc.random(3, 8), Hitmark.HIT);
						}
					});

				} else if (tick >= 15) {
					container.stop();
				}

				tick++;
			}

		}, 1);
	}

	public static void sendProjectileToTile(Player player, Location target, CombatProjectile projectile) {
		NPC npc = NPCHandler.getNpc(SNAKE_ID);
		int size = (int) Math.ceil((double) npc.getSize() / 2.0);

		int centerX = npc.getX() + size;
		int centerY = npc.getY() + size;
		int offsetX = (centerY - target.getY()) * -1;
		int offsetY = (centerX - target.getX()) * -1;
		player.getPA().createPlayersProjectile(centerX, centerY, offsetX, offsetY, projectile.getAngle(), projectile.getSpeed(), projectile.getGfx(), projectile.getStartHeight(), projectile.getEndHeight(), -1, 65, projectile.getDelay());
	}


}
