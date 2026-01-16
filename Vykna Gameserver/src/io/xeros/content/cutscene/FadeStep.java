package io.xeros.content.cutscene;

import io.xeros.model.entity.player.Player;

public final class FadeStep implements CutsceneStep {
    private boolean sent;
    private final String text;
    private final int state;
    private final int seconds;
    private int waitTicks;

    /**
     * @param waitTicks how many server ticks to wait after sending fade
     */
    public FadeStep(String text, int state, int seconds, int waitTicks) {
        this.text = text;
        this.state = state;
        this.seconds = Math.max(1, seconds); // <-- FIX: never allow 0
        this.waitTicks = Math.max(0, waitTicks);
    }


    @Override
    public boolean tick(Player p) {
        if (!sent) {
            p.getPA().sendScreenFade(text, state, seconds);
            sent = true;
        }
        return waitTicks-- <= 0;
    }
}
