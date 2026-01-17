package io.xeros.content.bossfactory;

import io.xeros.content.combat.npc.NPCAutoAttack;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

public interface BossController {
    void bind(NPC npc);

    NPCAutoAttack selectAutoAttack(Entity target);

    void ensureScaledStats(Player primaryTarget);

    double getDropMultiplier(Player killer);

    void cleanup(BossCleanupReason reason);
}
