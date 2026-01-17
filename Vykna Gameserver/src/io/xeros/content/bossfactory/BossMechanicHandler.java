package io.xeros.content.bossfactory;

import io.xeros.content.combat.npc.NPCAutoAttack;
import io.xeros.model.entity.Entity;

public interface BossMechanicHandler {
    BossMechanic getMechanic();

    NPCAutoAttack buildAttack(Entity target);

    default boolean isAvailable() {
        return true;
    }

    void cleanup(BossCleanupReason reason);
}
