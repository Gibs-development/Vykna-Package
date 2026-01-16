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
public class CommanderZilyanaInstance {

	public static boolean spawned = false;

	public static final int CommanderZilyana_BOSS_ID = 2205;
	private static final int BOSS_DAMAGE = 27;
	private static final int MINION_DAMAGE = 21;

	public static void startCommanderZilyanaInstance(Player player) {
		player.getPA().movePlayer(2893, 5266, 24 + (player.getIndex() * 4));
		Server.getGlobalObjects().add(new GlobalObject(7318, 2890, 5260, player.getHeight()));
		spawned = false;
	}


	public static void respawnAgain(Player player) {
		NPCSpawning.spawnNpc(player, CommanderZilyana_BOSS_ID, 2897, 5266, player.getHeight(), 3, BOSS_DAMAGE, true, false);
		NPCSpawning.spawnNpc(player, 2206, 2893, 5262, player.getHeight(), 4, MINION_DAMAGE, true, false);
		NPCSpawning.spawnNpc(player, 2207, 2893, 5271, player.getHeight(), 4, MINION_DAMAGE, true, false);
		NPCSpawning.spawnNpc(player, 2208, 2903, 5262, player.getHeight(), 4, MINION_DAMAGE, true, false);
		spawned = true;
	}

}
