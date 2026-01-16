package io.xeros.content.bosses;

import io.xeros.Server;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Coordinate;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;

/**
 * 
 * @author C.T for koranes
 *
 */
public class KrilTsutsarothInstance {

	public static boolean spawned = false;

	public static final int KRIL_BOSS_ID = 3129;
	private static final int BOSS_DAMAGE = 49;
	private static final int MINION_DAMAGE = 21;


	public static void startKrilInstance(Player player) {
		player.getPA().movePlayer(2893, 5266, 24 + (player.getIndex() * 4));
		Server.getGlobalObjects().add(new GlobalObject(7315, 2890, 5260, player.getHeight()));
		spawned = false;
	}


	public static void respawnAgain(Player player) {
		NPCSpawning.spawnNpc(player, KRIL_BOSS_ID, 2897, 5266, player.getHeight(), 3, BOSS_DAMAGE, true, false);
		NPCSpawning.spawnNpc(player, 3130, 2893, 5262, player.getHeight(), 4, MINION_DAMAGE, true, false);
		NPCSpawning.spawnNpc(player, 3132, 2893, 5271, player.getHeight(), 4, MINION_DAMAGE, true, false);
		NPCSpawning.spawnNpc(player, 3131, 2903, 5262, player.getHeight(), 4, MINION_DAMAGE, true, false);
		spawned = true;
	}

}
