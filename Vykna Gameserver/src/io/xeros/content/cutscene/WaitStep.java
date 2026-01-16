package io.xeros.content.cutscene;

import io.xeros.model.entity.player.Player;

public final class WaitStep implements CutsceneStep {
    private int ticks;

    public WaitStep(int ticks) {
        this.ticks = Math.max(0, ticks);
    }

    @Override
    public boolean tick(Player p) {
        return ticks-- <= 0;
    }
}
