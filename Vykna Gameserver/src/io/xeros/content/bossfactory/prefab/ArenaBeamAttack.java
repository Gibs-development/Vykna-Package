package io.xeros.content.bossfactory.prefab;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.xeros.content.bossfactory.BossCleanupReason;
import io.xeros.model.Graphic;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;

public class ArenaBeamAttack {

    public static class Config {
        private int beamGfx;
        private int beamWidth = 1;
        private int tickRate = 2;
        private int durationTicks = 10;
        private Consumer<Player> onPlayerHit;
        private Consumer<NPC> onNpcHit;

        public Config setBeamGfx(int beamGfx) {
            this.beamGfx = beamGfx;
            return this;
        }

        public Config setBeamWidth(int beamWidth) {
            this.beamWidth = beamWidth;
            return this;
        }

        public Config setTickRate(int tickRate) {
            this.tickRate = tickRate;
            return this;
        }

        public Config setDurationTicks(int durationTicks) {
            this.durationTicks = durationTicks;
            return this;
        }

        public Config setOnPlayerHit(Consumer<Player> onPlayerHit) {
            this.onPlayerHit = onPlayerHit;
            return this;
        }

        public Config setOnNpcHit(Consumer<NPC> onNpcHit) {
            this.onNpcHit = onNpcHit;
            return this;
        }
    }

    private final NPC npc;
    private final Config config;
    private final Set<Position> beamTiles = new HashSet<>();

    public ArenaBeamAttack(NPC npc, Config config) {
        this.npc = npc;
        this.config = config;
    }

    public void execute(Position start, Position end) {
        cleanup(BossCleanupReason.GENERIC);
        beamTiles.addAll(expandWidth(buildLine(start, end)));
        broadcastGfx();

        CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
            private int elapsed;

            @Override
            public void execute(CycleEventContainer container) {
                if (elapsed >= config.durationTicks) {
                    container.stop();
                    return;
                }
                if (elapsed % config.tickRate == 0) {
                    for (Player player : getPlayersInInstance()) {
                        if (beamTiles.contains(player.getPosition()) && config.onPlayerHit != null) {
                            config.onPlayerHit.accept(player);
                            if (config.beamGfx > 0) {
                                player.startGraphic(new Graphic(config.beamGfx));
                            }
                        }
                    }
                    if (config.onNpcHit != null && beamTiles.contains(npc.getPosition())) {
                        config.onNpcHit.accept(npc);
                    }
                }
                elapsed++;
            }
        }, 1);
    }

    public void cleanup(BossCleanupReason reason) {
        CycleEventHandler.getSingleton().stopEvents(this);
        beamTiles.clear();
    }

    private void broadcastGfx() {
        if (config.beamGfx <= 0) {
            return;
        }
        for (Player player : getPlayersInInstance()) {
            for (Position position : beamTiles) {
                player.getPA().stillGfx(config.beamGfx, position.getX(), position.getY(), position.getHeight(), 0);
            }
        }
    }

    private List<Player> getPlayersInInstance() {
        if (npc.getInstance() != null) {
            return npc.getInstance().getPlayers();
        }
        return PlayerHandler.nonNullStream()
                .filter(npc::sameInstance)
                .collect(Collectors.toList());
    }

    private List<Position> buildLine(Position start, Position end) {
        List<Position> points = new ArrayList<>();
        int x1 = start.getX();
        int y1 = start.getY();
        int x2 = end.getX();
        int y2 = end.getY();
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;
        int x = x1;
        int y = y1;
        while (true) {
            points.add(new Position(x, y, start.getHeight()));
            if (x == x2 && y == y2) {
                break;
            }
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
        return points;
    }

    private Set<Position> expandWidth(List<Position> line) {
        if (config.beamWidth <= 1) {
            return new HashSet<>(line);
        }
        Set<Position> expanded = new HashSet<>();
        int radius = config.beamWidth - 1;
        for (Position position : line) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    expanded.add(new Position(position.getX() + dx, position.getY() + dy, position.getHeight()));
                }
            }
        }
        return expanded;
    }
}
