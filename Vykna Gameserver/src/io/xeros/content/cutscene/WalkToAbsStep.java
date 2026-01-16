package io.xeros.content.cutscene;

import io.xeros.model.entity.player.Player;

public class WalkToAbsStep implements CutsceneStep {

    private final int destX, destY;
    private int timeoutTicks;
    private boolean started;

    // Give the pathfinder 1-2 ticks to populate the walking queue before we start checking "stuck"
    private int settleTicks = 2;

    public WalkToAbsStep(int destX, int destY, int timeoutTicks) {
        this.destX = destX;
        this.destY = destY;
        this.timeoutTicks = timeoutTicks;
    }

    @Override
    public boolean tick(Player p) {
        if (!started) {
            started = true;

            // IMPORTANT: use PathFinder-based walking (works even though cutscene ticks run after player processing)
            p.getPA().playerWalk(destX, destY);

            return false;
        }

        if (p.getX() == destX && p.getY() == destY) {
            return true;
        }

        if (settleTicks > 0) {
            settleTicks--;
            return false;
        }

        // If the queue is empty but we're not at destination, route likely failed (door/clipping/etc)
        if (p.isWalkingQueueEmpty()) {
            return true;
        }

        return --timeoutTicks <= 0;
    }
}
