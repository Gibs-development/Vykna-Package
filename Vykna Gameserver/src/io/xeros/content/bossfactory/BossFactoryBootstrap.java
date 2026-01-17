package io.xeros.content.bossfactory;

import io.xeros.annotate.Init;
import io.xeros.content.bossfactory.bosses.GoblinNecromancerBoss;

public class BossFactoryBootstrap {

    @Init
    public static void init() {
        BossFactoryRegistry.register(GoblinNecromancerBoss.NPC_ID, GoblinNecromancerBoss::new);
    }
}
