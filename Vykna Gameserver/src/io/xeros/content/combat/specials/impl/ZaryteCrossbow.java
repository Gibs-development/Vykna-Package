package io.xeros.content.combat.specials.impl;

import io.xeros.content.combat.Damage;
import io.xeros.content.combat.range.RangeData;
import io.xeros.content.combat.specials.Special;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

/**
 * 
 * @author Jason MacKeigan
 * @date Apr 4, 2015, 2015, 11:44:39 PM
 */
public class ZaryteCrossbow extends Special {

	public ZaryteCrossbow() {
		super(4.0, 3.0, 2.0, new int[] { 26374 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.usingBow = true;
		player.startAnimation(4230);
		if (player.playerAttackingIndex > 0 && target instanceof Player) {
			RangeData.fireProjectilePlayer(player, (Player) target, 50, 70, 1995, 43, 31, 37, 10);
		} else if (player.npcAttackingIndex > 0 && target instanceof NPC) {
			RangeData.fireProjectileNpc(player, (NPC) target, 50, 70, 1995, 43, 31, 37, 10);
		}
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {

	}

}
