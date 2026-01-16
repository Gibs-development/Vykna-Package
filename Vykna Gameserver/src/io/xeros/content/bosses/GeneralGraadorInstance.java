package io.xeros.content.bosses;

import io.xeros.Server;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;

/**
 * 
 * @author C.T for koranes
 *
 */
public class GeneralGraadorInstance {

	public static boolean spawned = false;

	public static final int GeneralGraador_BOSS_ID = 2215;
	private static final int BOSS_DAMAGE = 60;
	private static final int MINION_DAMAGE = 21;

	public static void startGeneralGraadorInstance(Player player) {
		player.getPA().movePlayer(2893, 5266, 24 + (player.getIndex() * 4));
		Server.getGlobalObjects().add(new GlobalObject(7319, 2890, 5260, player.getHeight()));
		spawned = false;
	}


	public static void respawnAgain(Player player) {
		NPCSpawning.spawnNpc(player, GeneralGraador_BOSS_ID, 2897, 5266, player.getHeight(), 3, BOSS_DAMAGE, true, false);
		NPCSpawning.spawnNpc(player, 2216, 2893, 5262, player.getHeight(), 4, MINION_DAMAGE, true, false);
		NPCSpawning.spawnNpc(player, 2217, 2893, 5271, player.getHeight(), 4, MINION_DAMAGE, true, false);
		NPCSpawning.spawnNpc(player, 2217, 2903, 5262, player.getHeight(), 4, MINION_DAMAGE, true, false);
		spawned = true;
	}

}
