package io.xeros.content.cutscene;

import io.xeros.model.entity.player.Player;

public interface CutsceneStep {
    /**
     * Return true when the step is finished.
     */
    boolean tick(Player p);
}
