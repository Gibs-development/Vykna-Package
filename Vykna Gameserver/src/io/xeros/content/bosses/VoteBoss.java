package io.xeros.content.bosses;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Coordinate;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

/**
 * 
 * @author C.T for koranes
 *
 */
public class VoteBoss {

	public static final int VOTE_BOSS_ID = 4804;
	private static final int MAX_DAMAGE = 30;

	public static void startVoteBoss(Player player) {
		VoteBossInstance instance = new VoteBossInstance(player, Boundary.VOTE_BOSS_ROOM);
		player.getPA().movePlayer(new Coordinate(2466, 5042, instance.getHeight()));
		NPC npc = NPCSpawning.spawnNpc(player, VOTE_BOSS_ID, 2455, 5028, instance.getHeight(), -1, MAX_DAMAGE, true, false);
		npc.getBehaviour().setRespawnWhenPlayerOwned(true);
		instance.add(npc);
		instance.add(player);
	}

}
