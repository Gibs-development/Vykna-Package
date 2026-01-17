package io.xeros.content.bossfactory.prefab;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.List;

import io.xeros.content.bossfactory.BossCleanupReason;
import io.xeros.model.Graphic;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;

/**
 * QBD-style moving wall:
 * - Spans the entire arena across one axis (X or Y)
 * - Leaves a gap the player can walk through
 * - Moves toward the other side over time
 */
public class MovingGapWallAttack {

    public enum Orientation {
        // Wall is a vertical line (same X), spans Y; it moves across X
        VERTICAL_MOVES_X,

        // Wall is a horizontal line (same Y), spans X; it moves across Y
        HORIZONTAL_MOVES_Y
    }

    public static class ArenaBounds {
        public final int minX, minY, maxX, maxY, height;

        public ArenaBounds(int minX, int minY, int maxX, int maxY, int height) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
            this.height = height;
        }
    }

    public static class Config {
        private int wallGfx;                 // stillGfx id used for wall tiles (0 disables)
        private int hitGfx;                  // optional: gfx played on a player when hit (0 disables)
        private int tickRate = 2;            // how often to apply damage/effects
        private int stepRate = 1;            // how often the wall moves (in ticks)
        private int wallThickness = 1;       // thickness of the wall (1 = single line)
        private int gapWidth = 2;            // gap size along the spanning axis
        private boolean randomGap = true;    // if false, gap centered on a chosen player at start (if any)
        private Consumer<Player> onPlayerHit;

        public Config setWallGfx(int wallGfx) { this.wallGfx = wallGfx; return this; }
        public Config setHitGfx(int hitGfx) { this.hitGfx = hitGfx; return this; }
        public Config setTickRate(int tickRate) { this.tickRate = tickRate; return this; }
        public Config setStepRate(int stepRate) { this.stepRate = stepRate; return this; }
        public Config setWallThickness(int wallThickness) { this.wallThickness = wallThickness; return this; }
        public Config setGapWidth(int gapWidth) { this.gapWidth = gapWidth; return this; }
        public Config setRandomGap(boolean randomGap) { this.randomGap = randomGap; return this; }
        public Config setOnPlayerHit(Consumer<Player> onPlayerHit) { this.onPlayerHit = onPlayerHit; return this; }
    }

    private final NPC npc;
    private final Config config;

    private final Set<Position> currentWallTiles = new HashSet<>();

    public MovingGapWallAttack(NPC npc, Config config) {
        this.npc = npc;
        this.config = config;
    }

    /**
     * Starts a moving wall pass from one side of the arena to the other.
     *
     * @param bounds arena bounds (inclusive)
     * @param orientation choose which direction the wall spans/moves
     * @param startStep where to start on the movement axis (usually bounds.minX or bounds.minY)
     * @param endStep where to finish on the movement axis (usually bounds.maxX or bounds.maxY)
     * @param stepDir +1 or -1
     */
    public void execute(ArenaBounds bounds, Orientation orientation, int startStep, int endStep, int stepDir) {
        cleanup(BossCleanupReason.GENERIC);

        // Decide the gap location ONCE for this whole wall pass (QBD-style).
        int gapStart = pickGapStart(bounds, orientation);

        CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
            private int tick;
            private int step = startStep;

            @Override
            public void execute(CycleEventContainer container) {
                // stop if we reached/passed the end
                if ((stepDir > 0 && step > endStep) || (stepDir < 0 && step < endStep)) {
                    container.stop();
                    return;
                }

                // Move wall
                if (tick % Math.max(1, config.stepRate) == 0) {
                    rebuildWallTiles(bounds, orientation, step, stepDir, gapStart);
                    broadcastWallGfx();
                    step += stepDir;
                }

                // Apply hits
                if (tick % Math.max(1, config.tickRate) == 0) {
                    for (Player p : getPlayersInInstance()) {
                        if (currentWallTiles.contains(p.getPosition())) {
                            if (config.onPlayerHit != null) {
                                config.onPlayerHit.accept(p);
                            }
                            if (config.hitGfx > 0) {
                                p.startGraphic(new Graphic(config.hitGfx));
                            }
                        }
                    }
                }

                tick++;
            }
        }, 1);
    }

    public void cleanup(BossCleanupReason reason) {
        CycleEventHandler.getSingleton().stopEvents(this);
        currentWallTiles.clear();
    }

    private void rebuildWallTiles(ArenaBounds b, Orientation o, int step, int stepDir, int gapStart) {
        currentWallTiles.clear();

        int thickness = Math.max(1, config.wallThickness);
        int gapWidth = Math.max(1, config.gapWidth);

        if (o == Orientation.VERTICAL_MOVES_X) {
            // Wall is at X = step, spans Y, leaves a gap in Y
            for (int t = 0; t < thickness; t++) {
                int x = step + (t * stepDir); // thickness trails/extends in move direction
                for (int y = b.minY; y <= b.maxY; y++) {
                    if (y >= gapStart && y < gapStart + gapWidth) continue;
                    currentWallTiles.add(new Position(x, y, b.height));
                }
            }
        } else {
            // HORIZONTAL_MOVES_Y: Wall at Y = step, spans X, gap in X
            for (int t = 0; t < thickness; t++) {
                int y = step + (t * stepDir);
                for (int x = b.minX; x <= b.maxX; x++) {
                    if (x >= gapStart && x < gapStart + gapWidth) continue;
                    currentWallTiles.add(new Position(x, y, b.height));
                }
            }
        }
    }

    private void broadcastWallGfx() {
        if (config.wallGfx <= 0) return;

        for (Player player : getPlayersInInstance()) {
            for (Position pos : currentWallTiles) {
                player.getPA().stillGfx(config.wallGfx, pos.getX(), pos.getY(), pos.getHeight(), 0);
            }
        }
    }

    private int pickGapStart(ArenaBounds b, Orientation o) {
        int spanMin = (o == Orientation.VERTICAL_MOVES_X) ? b.minY : b.minX;
        int spanMax = (o == Orientation.VERTICAL_MOVES_X) ? b.maxY : b.maxX;
        int spanLen = (spanMax - spanMin) + 1;

        int gapWidth = Math.max(1, Math.min(config.gapWidth, spanLen));
        int latestStart = spanMax - gapWidth + 1;

        // Option A: random gap (default)
        if (config.randomGap) {
            return ThreadLocalRandom.current().nextInt(spanMin, latestStart + 1);
        }

        // Option B: gap aligned to a player's position at start (if any)
        List<Player> players = getPlayersInInstance();
        if (!players.isEmpty()) {
            Player p = players.get(0);
            int target = (o == Orientation.VERTICAL_MOVES_X) ? p.getY() : p.getX();
            int start = target - (gapWidth / 2);
            if (start < spanMin) start = spanMin;
            if (start > latestStart) start = latestStart;
            return start;
        }

        return ThreadLocalRandom.current().nextInt(spanMin, latestStart + 1);
    }

    private List<Player> getPlayersInInstance() {
        if (npc.getInstance() != null) {
            return npc.getInstance().getPlayers();
        }
        return PlayerHandler.nonNullStream()
                .filter(npc::sameInstance)
                .collect(Collectors.toList());
    }
}
