package io.xeros.content.cutscene;

import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.entity.player.Player;

import java.util.List;

public final class CutsceneRunner extends CycleEvent {

    public static final int EVENT_ID = 98765;

    private final Player player;
    private final List<CutsceneStep> steps;
    private int index = 0;

    public CutsceneRunner(Player player, List<CutsceneStep> steps) {
        this.player = player;
        this.steps = steps;
    }

    @Override
    public void execute(CycleEventContainer container) {
        if (player == null || player.isDisconnected()) {
            container.stop();
            return;
        }

        if (index >= steps.size()) {
            end(container);
            return;
        }

        boolean done = steps.get(index).tick(player);
        if (done) index++;
    }

    @Override
    public void onStopped() {
        // Safety cleanup (if event stops early)
        if (player != null) {
            player.getPA().resetCamera();
            player.unlock();
        }
    }

    private void end(CycleEventContainer container) {
        player.getPA().resetCamera();
        player.unlock();
        container.stop();
    }
}
