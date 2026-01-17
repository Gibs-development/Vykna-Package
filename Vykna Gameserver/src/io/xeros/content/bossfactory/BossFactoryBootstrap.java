package io.xeros.content.bossfactory;

import io.xeros.annotate.Init;
import io.xeros.content.bossfactory.bosses.MotherMadera;

public class BossFactoryBootstrap {

    @Init
    public static void init() {
        BossFactoryRegistry.register(MotherMadera.NPC_ID, MotherMadera::new);
    }
}
