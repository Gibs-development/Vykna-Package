package io.xeros.content.combat.specials.impl;

import io.xeros.content.combat.Damage;
import io.xeros.content.combat.specials.Special;
import io.xeros.content.skills.Skill;
import io.xeros.model.definitions.AnimationLength;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

public class AncientGodsword extends Special {

	public AncientGodsword() {
		super(5.0, 2.0, 1.375, new int[] { 26233 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		int animationDelay = AnimationLength.getFrameLength(7640 ) + 2; // 9171 is correct animation 7640
		player.getAnimationTimer().setDuration(animationDelay);
		player.startAnimation(7640, 2);
		player.gfx0(1996);
		if (target instanceof NPC) {
			((NPC) target).gfx100(2003);
		} else if (target instanceof Player) {
			((Player) target).gfx100(2003);
		}
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {
		if (damage.getAmount() < 1) {
			return;
		}
		if (damage.getAmount() < 21) {
			player.getHealth().increase(10);
		} else {
			player.getHealth().increase(damage.getAmount() / 1);
		}
	}

}
