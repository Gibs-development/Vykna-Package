package io.xeros.content.cutscene;

import java.util.function.Consumer;

import io.xeros.model.entity.player.Player;

public final class ActionStep implements CutsceneStep {
    private final Consumer<Player> action;
    private boolean ran;

    public ActionStep(Consumer<Player> action) {
        this.action = action;
    }

    @Override
    public boolean tick(Player p) {
        if (!ran) {
            ran = true;
            action.accept(p);
        }
        return true;
    }
}
