package io.xeros.content.bosses;

import io.xeros.Server;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;

import static io.xeros.model.entity.npc.data.NpcMaxHit.getMaxHit;

/**
 * 
 * @author C.T for koranes
 *
 */
public class KreeArraInstance {

	public static boolean spawned = false;

	public static final int KreeArra_BOSS_ID = 3162;
	private static final int BOSS_DAMAGE = 69;
	private static final int MINION_DAMAGE = 21;

	public static void startKreeArraInstance(Player player) {
		player.getPA().movePlayer(2893, 5266, 24 + (player.getIndex() * 4));
		Server.getGlobalObjects().add(new GlobalObject(7321, 2890, 5260, player.getHeight()));
		spawned = false;
	}


	public static void respawnAgain(Player player) {

		NPCSpawning.spawnNpc(player, KreeArra_BOSS_ID, 2897, 5266, player.getHeight(), 3, BOSS_DAMAGE, true, false);
		NPCSpawning.spawnNpc(player, 3164, 2893, 5262, player.getHeight(),3, MINION_DAMAGE, true, false);
		NPCSpawning.spawnNpc(player, 3165, 2893, 5271, player.getHeight(),3, MINION_DAMAGE, true, false);
		NPCSpawning.spawnNpc(player, 3163, 2903, 5262, player.getHeight(), 3, MINION_DAMAGE, true, false);
		spawned = true;
	}

}
