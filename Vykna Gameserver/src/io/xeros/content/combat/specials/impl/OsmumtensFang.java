package io.xeros.content.combat.specials.impl;

import io.xeros.content.combat.Damage;
import io.xeros.content.combat.melee.CombatPrayer;
import io.xeros.content.combat.specials.Special;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

public class OsmumtensFang extends Special {

    public OsmumtensFang() {
        super(2.5, 5.15, 2.00, new int[] { 26_219,27_246 });
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {
        player.startAnimation(1378);
        player.gfx100(2128);
    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {
        int heal = Misc.random((damage.getAmount() / 2) + (Misc.random(damage.getAmount() / 2)));
        player.getHealth().increase(heal);
        player.getPA().refreshSkill(3);
        if (target instanceof Player) {
            if (damage.getAmount() > 0) {
                CombatPrayer.resetOverHeads((Player) target);
            }
        }

    }

}
