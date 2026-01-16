package io.xeros.content.minigames.toa;

import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.toa.instance.ToaInstance;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Position;

public class ToaBoss extends NPC {

    public ToaBoss(int npcId, Position position, InstancedArea instancedArea) {
        super(npcId, position);
        instancedArea.add(this);
        getBehaviour().setRespawn(false);
        getBehaviour().setAggressive(true);
    }

    public void onDeath() {
        Entity killer = calculateKiller();
        if (getInstance() != null) {
            getInstance().getPlayers().forEach(plr -> {
                int points = 4;
                if (killer != null && killer.equals(plr)) {
                    points += 2;
                }
                ((ToaInstance) plr.getInstance()).getMvpPoints().award(plr, points);
                ((ToaInstance) plr.getInstance()).getFoodRewards().award(plr, points);
            });
        }
    }

}
