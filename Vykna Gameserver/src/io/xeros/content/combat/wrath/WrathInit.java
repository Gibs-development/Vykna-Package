package io.xeros.content.combat.wrath;

import io.xeros.annotate.Init;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

/**
 * Wires Wrath into the server tick.
 */
public final class WrathInit {

    private WrathInit() {}

    @Init
    public static void init() {
        Object key = new Object();

        // Tick every game cycle.
        CycleEventHandler.getSingleton().addEvent(key, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                // Process Wrath for online players: decay + one-time initial sync.
                for (int i = 0; i < PlayerHandler.players.length; i++) {
                    Player p = PlayerHandler.players[i];
                    if (p == null) continue;
                    //WrathSystem.process(p);
                }
            }

            @Override
            public void onStopped() {
            }
        }, 1);
    }
}
