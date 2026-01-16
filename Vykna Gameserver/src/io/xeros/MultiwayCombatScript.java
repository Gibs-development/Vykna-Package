package io.xeros;

import java.util.ArrayList;
import java.util.List;

import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;


/**
 * Multiway NPC combat script.
 * 
 * @author C.T
 *
 */
public interface MultiwayCombatScript {

	/**
	 * @return the boss.
	 */
	static NPC getBoss() {
		return null;
	}

	/**
	 * @return a collection of applicable targets.
	 */
	static List<Entity> getPossibleTargets() {
		return getPossibleTargets(14, true, false);
	}

	/**
	 * Returns a collection of applicable targets.
	 * 
	 * @param ratio
	 * 			The distance cap.
	 * @param players
	 * 			{@code True} if players to be checked.
	 * @param npcs
	 * 			{@code True} if npcs to be checked.
	 * @return a collection of applicable targets.
	 */
	static List<Entity> getPossibleTargets(int ratio, boolean players, boolean npcs) {
		ArrayList<Entity> possibleTargets = new ArrayList<>();
		if (players) {
			for (Player player : PlayerHandler.players) {
				if (player == null) { 
					continue;
				}
				if (player == null || player.isDead() || Misc.getDistance(player.getLocation(), getBoss().getLocation()) > ratio) {
					continue;
				}
				possibleTargets.add(player);
			}
		}
		if (npcs) {
			for (NPC npc : Server.npcHandler.npcs) {
				if (npc == null || npc == getBoss() || npc.isDead || Misc.getDistance(npc.getLocation(), getBoss().getLocation()) > ratio) { 
					continue;
				}
				possibleTargets.add(npc);
			}
		}
		return possibleTargets;
	}
	
	void hit(Entity attacker, Entity victim);
	
	/**
	 * @return a melee target if possible.
	 */
	default Player getMeleeTarget() {
		List<Entity> possible = getPossibleTargets();
		for (Entity entity : possible) {
			if (entity == null || !(entity instanceof Player)) continue;
			Player player = (Player) entity;
			if (Misc.getDistance(player.getLocation(), getBoss().getLocation()) <= getBoss().getSize()) {
				return player;
			}
		}
		return null;
	}
	
	/**
	 * @return the player target that has not been attacked in the longest time.
	 */
	default Player getBestPossibleTarget() {
		List<Entity> possible = getPossibleTargets();
		ArrayList<Player> plausable = new ArrayList<>();
		for (Entity entity : possible) {
			if (entity == null || !(entity instanceof Player)) continue;
			Player player = (Player) entity;
			if (player.getAttributes().getLong(getBoss().getDefinition().getName()) < System.currentTimeMillis()) {
				plausable.add(player);
			}
		}
		if (plausable.size() == 1) {
			return plausable.get(0);
		}
		return plausable.get(Misc.random(plausable.size() - 1));
	}
	
}