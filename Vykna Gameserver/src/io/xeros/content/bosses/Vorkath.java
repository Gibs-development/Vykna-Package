package io.xeros.content.bosses;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.melee.CombatPrayer;
import io.xeros.content.instances.InstancedArea;
import io.xeros.model.collisionmap.doors.Location;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.cycleevent.DelayEvent;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.HealthStatus;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCDumbPathFinder;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.model.items.GameItem;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;

public class Vorkath {

	/**
	 *
	 * @author C.T for koranes
	 *
	 */

	private final Player player;

	private AttackType specialAttackType;

	private AttackType attackType;

	private final Object eventLock;

	private int attacks;


	public boolean zombieSpawned;

	public Vorkath(Player player) {
		this.player = player;
		this.specialAttackType = Arrays.stream(AttackType.values()).filter(type -> type.name().toLowerCase().contains("special")).collect(Collectors.toList()).get(Misc.random(1));//only 0 or 1
		this.eventLock = new Object();
	}


	public static int attackStyle;

	public static int[] lootCoordinates = { 2268, 4061 };

	public static final int[] NPC_IDS = {8026, 8027, 8028};

	public static final Position SPAWN = new Position(2268, 4061);


    public static ArrayList<GameItem> getVeryRareDrops() {
		return Lists.newArrayList(new GameItem(11286, 1), new GameItem(22006, 1));
	}

	public static boolean inVorkath(Player player) {
		return (player.absX > 2255 && player.absX < 2288 && player.absY > 4053 && player.absY < 4083);
	}

	public static void poke(Player player, NPC npc) {
		if(player.heightLevel == 0) {
			player.sendMessage("Vorkath isn't interested in fighting right now... try rejoining the instance.");
			return;
		}
		npc.requestTransform(8027);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				npc.requestTransform(8028);
			}

			@Override
			public void onStopped() {
			}
		}, 7);

	}

	public static void spawn(Player player) {
		NPCSpawning.spawnNpc(player, 8026, SPAWN.getX(), SPAWN.getY(), player.getIndex() * 4, 0,
				player.antifireDelay > 0 ? 0 : 61, true, false);
	}

	public static void enterInstance(Player player, int instance) {
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (player.absY == 4052 && player.absX != 2272) {
					player.setForceMovement(2272, 4054, 10, 10, "NORTH", 1660);
				}
				if (player.absY == 4052 && player.absX == 2272) {
					player.setForceMovement(player.absX, 4054, 10, 10, "NORTH", 839);
					player.getPA().movePlayer(player.absX, player.absY, player.getIndex() * 4);
					spawn(player);
					container.stop();
				}
			}

			@Override
			public void onStopped() {
			}
		}, 1);
	}

	public static void exit(Player player) {
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (player.absY == 4054 && player.absX != 2272) {
					player.setForceMovement(2272, 4052, 10, 10, "SOUTH", 1660);
				}
				if (player.absY == 4054 && player.absX == 2272) {
					player.setForceMovement(player.absX, 4052, 10, 10, "SOUTH", 839);
					player.getPA().movePlayer(player.absX, player.absY, 0);
					container.stop();
				}
			}

			@Override
			public void onStopped() {
			}
		}, 1);

	}


	public boolean canSpecial() {
		return attacks % 7 == 0 && attacks > 0;
	}

	public void handleAttack() {
		NPC vorkath = NPCHandler.getNpc(8028);
		if (player != null && vorkath != null && !vorkath.isDead && !player.isDead && !zombieSpawned) {


			int distance = player.distanceToPoint(vorkath.getX(), vorkath.getY());
			if (vorkath.actionTimer > 0) {
				return;
			}
			if(distance >= 20 || player.getY() < 4054) {
				return;
			}
			vorkath.actionTimer += 5;
			attacks += 1;
			if (canSpecial()) { //Every 7 attacks
				vorkath.setFacePlayer(true);
				specialAttackType = specialAttackType == AttackType.SPECIAL_1 ? AttackType.SPECIAL_2 : AttackType.SPECIAL_1;
				vorkath.startAnimation(specialAttackType.getAnimationId());
				if (specialAttackType == AttackType.SPECIAL_1) { //acid
					vorkath.setFacePlayer(true);
					CycleEventHandler.getSingleton().addEvent(eventLock, handleAcidSpecial(), 1);
					vorkath.actionTimer += 22;
				} else if (specialAttackType == AttackType.SPECIAL_2) { //jihad
					vorkath.setFacePlayer(true);
					fireTargetedProjectile(specialAttackType.getProjectileId(), vorkath);
					CycleEventHandler.getSingleton().addEvent(eventLock, handleJihadSpecial(), 1);
					player.stopMovement();
					vorkath.actionTimer += 7;
				}
			} else {
				attackType = Arrays.stream(AttackType.values()).filter(type ->
								!type.name().toLowerCase().contains("special")).
						collect(Collectors.toList()).get(Misc.random(5));
				vorkath.actionTimer += 1;
				if (attackType != AttackType.ONE_HIT) {
					vorkath.setFacePlayer(true);
					CycleEventHandler.getSingleton().addEvent(eventLock, handleAttackType(), 1);
					fireTargetedProjectile(attackType.getProjectileId(), vorkath);
				} else {
					vorkath.setFacePlayer(true);
					CycleEventHandler.getSingleton().addEvent(eventLock, handleOneHit(), 1);
					fireOneshit(attackType.getProjectileId(), 150, player.getX(), player.getY(),//150
							50, 50); //50 -> current angle, 50 -> current start height

				}
			}
		}
	}




	private CycleEvent handleJihadSpecial() {
		return new CycleEvent() {
			int SPAWN_X;
			int SPAWN_Y;
			NPC spawn;

			private void killSpawn(boolean explode) {
				spawn.gfx0(542);
				zombieSpawned = false;
				spawn.needRespawn = false;
				spawn.isDead = true;
				if(explode) {
					player.appendDamage(Misc.random(60) + 10, Hitmark.HIT);
				}
				player.freezeTimer = 0;
			}
			@Override
			public void execute(CycleEventContainer container) {
			//	if (container.getOwner() == null || vorkath == null || player == null || player.isDead || vorkathInstance == null) {
				NPC vorkath = NPCHandler.getNpc(8028);
				if (container.getOwner() == null || vorkath == null || player == null || player.isDead) {
					container.stop();
					return;
				}
				int cycle = container.getTotalTicks();
				if (cycle == 4) {
					player.gfx0(specialAttackType.getEndGfx());
					player.freezeTimer = 30;
					player.sendMessage("You've been frozen.");
				}
				if (cycle == 5) {
					if (player.getX() <= 2272 && player.getY() < 4065) {
						SPAWN_X = 2263;
						SPAWN_Y = 4071;
					}
					if (player.getX() > 2272 && player.getY() < 4065) {
						SPAWN_X = 2280;
						SPAWN_Y = 4071;
					}
					if (player.getX() <= 2272 && player.getY() >= 4065) {
						SPAWN_X = 2263;
						SPAWN_Y = 4059;
					}
					if (player.getX() > 2272 && player.getY() >= 4065) {
						SPAWN_X = 2280;
						SPAWN_Y = 4059;
					}
					player.sendMessage("@blu@The dragon throws a creature towards you..");
					zombieSpawned = true;
					fireProjectileToLocation(1484, 130, SPAWN_X, SPAWN_Y, 50);
				}
				if (cycle == 9) {
					spawn = NPCSpawning.spawnNpcOld(player, VorkathConstants.ZOMBIE_SPAWN, SPAWN_X, SPAWN_Y, player.getHeight(), 1, VorkathConstants.ZOMBIE_SPAWN_LIFE_POINTS, 1, 1, 1, false, false);
				}
				if (cycle >= 15) {
					boolean distance = player.getX() == spawn.getX() + 1 || player.getX() == spawn.getX() - 1 || player.getY() == spawn.getY() + 1 || player.getY() == spawn.getY() - 1;
					if(zombieSpawned && spawn.isDead) {
						killSpawn(false);
						container.stop();
					}
					if (distance && zombieSpawned) {
						killSpawn(true);
						container.stop();
					}
				}
				if (zombieSpawned && cycle >= 10) {
					if (spawn.getX() != player.getX() - 1 && spawn.getY() != player.getY() - 1) {
						NPCDumbPathFinder.walkTowards(spawn, player.getX(), player.getY());
					}
				}

				if (!zombieSpawned && cycle >= 20) {
					container.stop();
				}


				if (zombieSpawned && cycle >= 20 && player.distanceToPoint(spawn.getX(), spawn.getY()) >= 5) {
					if (player.distanceToPoint(spawn.getX(), spawn.getY()) < 40) {
						killSpawn(false);
						player.sendMessage("The spawn lost its orientation and blew up..");
					}
				}
			}
		};
	}

	private CycleEvent handleAcidSpecial() {
		return new CycleEvent() {
			int x;
			int y;

			@Override
			public void execute(CycleEventContainer container) {

			//	if (container.getOwner() == null || vorkath == null || player == null || player.isDead || vorkathInstance == null) {
				NPC vorkath = NPCHandler.getNpc(8028);
				if (container.getOwner() == null || vorkath == null || player == null || player.isDead) {
					container.stop();
					return;
				}
				int cycle = container.getTotalTicks();
				if(Server.getGlobalObjects().exists(32000, player.getX(), player.getY(), player.getHeight())) {
					int randomDamage = Misc.random(10) + 7;
					vorkath.getHealth().increase(randomDamage);
					vorkath.setUpdateRequired(true);
					player.appendDamage(randomDamage, Hitmark.POISON);
					player.sendMessage("@gre@You step on the acid and take some damage");
				}
				if (vorkath.getHealth().getMaximumHealth() == 0) {
					container.stop();
					return;
				}
				if (cycle == 1) {
					int minX = VorkathConstants.VORKATH_BOUNDARY.getMinimumX();
					int maxX = VorkathConstants.VORKATH_BOUNDARY.getMaximumX();
					int minY = 4054; //it's bugged in the boundaries
					int maxY = VorkathConstants.VORKATH_BOUNDARY.getMaximumY();
					for (int i = 0; i < 40; i++) {
						int randomX = minX + Misc.random(maxX - minX);
						int randomY = minY + Misc.random(maxY - minY);
						if ((randomX <= 2276 && randomX >= 2268 && randomY <= 4069 && randomY >= 4061)) {
							continue;
						}
						fireProjectileToLocation(1486, 100,
								randomX,
								randomY, 60);
						Server.getEventHandler().submit(new DelayEvent(5) {
							@Override
							public void onExecute() {
								Server.getGlobalObjects().add(new GlobalObject(32000, randomX, randomY, vorkath.getHeight(), Misc.random(3) + 1, 10, 120, -1));
							}
						});
					}
				}
				if (cycle >= 3 && cycle <= 25) {
					if (cycle >= 5) {
						if (x == player.getX() && y == player.getY()) {
							player.appendDamage(30, Hitmark.HIT);
						}
					}
					x = player.getX();
					y = player.getY();
					fireProjectileToLocation(1482, 85, x, y, 35);//95
					player.getPA().stillGfx(131, x, y, 15, 95);
					fireProjectileToLocation(1482, 65, x, y, 35);//70
					player.getPA().stillGfx(131, x, y, 15, 70);
				}
				if (cycle == 30) {

					Server.getGlobalObjects().remove(32000, player.getHeight());  // come back to this cam
					container.stop();
				}
			}
		};
	}



	private CycleEvent handleOneHit() {
		return new CycleEvent() {
			Location arrival = new Location(player.getX(), player.getY(), player.getHeight());

			@Override
			public void execute(CycleEventContainer container) {
			//	if (container.getOwner() == null || vorkath == null || player == null || player.isDead || vorkathInstance == null) {
				NPC vorkath = NPCHandler.getNpc(8028);
				if (container.getOwner() == null || vorkath == null || player == null || player.isDead ) {
					container.stop();
					return;
				}
				int cycle = container.getTotalTicks();
				if (cycle == 1) {
					arrival = new Location(player.getX(), player.getY(), player.getHeight());
				}
				if (cycle == 8) {
					int arrivalX = arrival.getX();
					int arrivalY = arrival.getY();
					int playerX = player.getX();
					int playerY = player.getY();
					if (playerX == arrivalX && playerY == arrivalY) {
						applyRandomDamage(player.getHealth().getMaximumHealth());
						player.getPA().stillGfx(attackType.getEndGfx(), arrival.getX(), arrival.getY(), 100, 0);
					} if(playerX == (arrivalX + 1)
							|| playerX == (arrivalX - 1)
							|| playerY == (arrivalY + 1)
							|| playerY == (arrivalY - 1)) {
						applyRandomDamage(15);
						player.getPA().stillGfx(attackType.getEndGfx(), arrivalX, arrivalY, 100, 0);
					} else {
						player.getPA().stillGfx(attackType.getEndGfx(), arrivalX, arrivalY, 20, 0);
					}
					container.stop();
				}
			}
		};
	}

	private void fireTargetedProjectile(int projectileId ,NPC vorkath) {
		int offY = (vorkath.getX() - player.getX()) * -1;
		int offX = (vorkath.getY() - player.getY()) * -1;
		int delay = 0;
		vorkath.startAnimation(attackType.getAnimationId());
		//player.getPA().createPlayersProjectile(vorkath.getX() + 3, vorkath.getY(), offX, offY, 50, 110, projectileId, 35, 31, -player.getIndex() - 1, 65, delay);

		player.getPA().createPlayersProjectile(vorkath, player, 50, 100, projectileId, 50, 40, -player.getIndex() - 1, 45, 0);
	}

	private void fireProjectileToLocation(int projectileId, int projectileSpeed, int targetX, int targetY, int startHeight) {
		NPC vorkath = NPCHandler.getNpc(8028);
		int offY = (vorkath.getX() - targetX) * -1;
		int offX = (vorkath.getY() - targetY) * -1;
		int delay = 0;
		//player.getPA().createPlayersProjectile(vorkath.getX() + 3, vorkath.getY(), offX, offY - 3, 50, projectileSpeed, projectileId, startHeight, 31, 0, 65, delay);
		player.getPA().createPlayersProjectile(vorkath, player, 50, 100, projectileId, 50, 40, -player.getIndex() - 1, 45, 0);
	}

	private void fireOneshit(int projectileId, int projectileSpeed, int targetX, int targetY, int angle, int startHeight) {
		NPC vorkath = NPCHandler.getNpc(8028);
		int offY = (vorkath.getX() - targetX) * -1;
		int offX = (vorkath.getY() - targetY) * -1;
		int delay = 0;
		vorkath.startAnimation(attackType.getAnimationId());
		//player.getPA().createPlayersProjectile(vorkath.getX() + 3, vorkath.getY(), offX, offY - 3, 50, projectileSpeed, projectileId, startHeight, 31, 0, 65, delay);
		player.getPA().createPlayersProjectile(vorkath, player, 50, 100, projectileId, 50, 40, -player.getIndex() - 1, 45, 0);
	}


	private CycleEvent handleAttackType() {
		return new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				NPC vorkath = NPCHandler.getNpc(8028);
				//if (container.getOwner() == null || vorkath == null || player == null || player.isDead || vorkathInstance == null) {
					if (container.getOwner() == null || vorkath == null || player == null || player.isDead) {
					container.stop();
					return;
				}
				int cycle = container.getTotalTicks();
				//player.getPA().stillGfx(attackType.getEndGfx(), player.getX(), player.getY(), 100, 110);
				if (cycle == 4) {
					handleEffect();
				}
				if (cycle == 5) {
					player.getPA().stillGfx(attackType.getEndGfx(), player.getX(), player.getY(), 100, 0);
					container.stop();
				}
			}
		};
	}

	private void applyRandomDamage(int amount) {
		player.appendDamage(Misc.random(amount) + 1, Hitmark.HIT);
	}

	private void handleEffect() {
		switch (attackType) {
			case MAGIC:
				if (player.prayerActive[16]) {
				//	player.appendDamage(0, Hitmark.MISS);
					return;
				} else {
				//	applyRandomDamage(35);
				}
				break;
			case POISON:
			//	applyRandomDamage(3);
				NPC vorkath = NPCHandler.getNpc(8028);
				player.getHealth().proposeStatus(HealthStatus.POISON, Misc.random(12), Optional.of(vorkath));
				break;
			case RANGED:
				if (player.prayerActive[17]) {
				//	player.appendDamage(0, Hitmark.MISS);
					return;
				} else {
				//	applyRandomDamage(30);
				}
				break;
			case DRAGON_FIRE:
				boolean isResistent =
						player.getItems().isWearingItem(1540)
								|| player.getItems().isWearingItem(11283)
								|| player.getItems().isWearingItem(11284)
								|| player.getItems().isWearingItem(24120) && player.getItems().isWearingItem(24121) && player.getItems().isWearingItem(24122)
						//		|| (System.currentTimeMillis() - player.lastSuperAntifirePotion < player.SuperantifireDelay)
								|| (System.currentTimeMillis() - player.lastAntifirePotion < player.antifireDelay);
				if (isResistent) {
					player.sendMessage("@or1@Your resistance reflects the dragons fire!");
				//	player.appendDamage(0, Hitmark.MISS);
					return;
				} else {
				//	applyRandomDamage(35);
					player.sendMessage("@or1@You got horribly burned by the dragons fire.");
				}
				break;
			case PRAYER_SNIPE:
				for (int i = 0; i < player.prayerActive.length - 1; i++) {
					if (!player.prayerActive[i])
						continue;
					player.prayerActive[i] = false;
				//	player.getPA().sendFrame36(player.PRAYER_GLOW[i], 0);
					player.getPA().sendFrame36(CombatPrayer.PRAYER_GLOW[i], 0);
				}
				player.sendMessage("@red@Your prayers were disabled.");
				player.headIcon = -1;
				player.getPA().requestUpdates();
			//	applyRandomDamage(3);
				break;
			default:
				break;
		}
	}







}
