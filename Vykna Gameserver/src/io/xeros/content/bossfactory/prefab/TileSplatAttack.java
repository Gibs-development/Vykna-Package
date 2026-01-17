package io.xeros.content.bossfactory.prefab;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import io.xeros.content.combat.Hitmark;

public class TileSplatAttack {

    public enum TargetingStyle {
        AROUND_PLAYER,
        AROUND_NPC,
        FIXED_POINT
    }

    public static class Config {
        private int telegraphGfx;
        private int groundSplatGfx;
        private int hitGfx;
        private int animationId;
        private int soundId;
        private int durationTicks;
        private int tickInterval;
        private int damagePerTick;
        private int sizeX = 4;
        private int sizeY = 4;
        private TargetingStyle targetingStyle = TargetingStyle.AROUND_PLAYER;
        private Position fixedCenter;

        public Config setTelegraphGfx(int telegraphGfx) {
            this.telegraphGfx = telegraphGfx;
            return this;
        }

        public Config setGroundSplatGfx(int groundSplatGfx) {
            this.groundSplatGfx = groundSplatGfx;
            return this;
        }

        public Config setHitGfx(int hitGfx) {
            this.hitGfx = hitGfx;
            return this;
        }

        public Config setAnimationId(int animationId) {
            this.animationId = animationId;
            return this;
        }

        public Config setSoundId(int soundId) {
            this.soundId = soundId;
            return this;
        }

        public Config setDurationTicks(int durationTicks) {
            this.durationTicks = durationTicks;
            return this;
        }

        public Config setTickInterval(int tickInterval) {
            this.tickInterval = tickInterval;
            return this;
        }

        public Config setDamagePerTick(int damagePerTick) {
            this.damagePerTick = damagePerTick;
            return this;
        }

        public Config setSize(int sizeX, int sizeY) {
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            return this;
        }

        public Config setTargetingStyle(TargetingStyle targetingStyle) {
            this.targetingStyle = targetingStyle;
            return this;
        }

        public Config setFixedCenter(Position fixedCenter) {
            this.fixedCenter = fixedCenter;
            return this;
        }
    }

    private final NPC npc;
    private final Config config;
    private final Set<Position> activeTiles = new HashSet<>();

    public TileSplatAttack(NPC npc, Config config) {
        this.npc = npc;
        this.config = config;
    }

    public void execute(Player target) {
        cleanup(BossCleanupReason.GENERIC);
        Position center = resolveCenter(target);
        if (center == null) {
            return;
        }
        if (config.animationId > 0) {
            npc.startAnimation(config.animationId);
        }
        int startX = center.getX() - (config.sizeX / 2);
        int startY = center.getY() - (config.sizeY / 2);
        for (int x = 0; x < config.sizeX; x++) {
            for (int y = 0; y < config.sizeY; y++) {
                activeTiles.add(new Position(startX + x, startY + y, center.getHeight()));
            }
        }
        broadcastGfx(config.telegraphGfx, activeTiles);
        if (config.soundId > 0) {
            getPlayersInInstance().forEach(player -> player.getPA().sendSound(config.soundId));
        }

        CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
            private int elapsed;

            @Override
            public void execute(CycleEventContainer container) {
                if (elapsed >= config.durationTicks) {
                    container.stop();
                    return;
                }
                if (elapsed % config.tickInterval == 0) {
                    broadcastGfx(config.groundSplatGfx, activeTiles);
                    for (Player player : getPlayersInInstance()) {
                        if (activeTiles.contains(player.getPosition())) {
                            player.appendDamage(npc, config.damagePerTick, Hitmark.HIT);
                            if (config.hitGfx > 0) {
                                player.startGraphic(new Graphic(config.hitGfx));
                            }
                        }
                    }
                }
                elapsed++;
            }
        }, 1);
    }

    private Position resolveCenter(Player target) {
        if (config.targetingStyle == TargetingStyle.FIXED_POINT) {
            return config.fixedCenter;
        }
        if (config.targetingStyle == TargetingStyle.AROUND_NPC) {
            return npc.getCenterPosition();
        }
        if (target != null) {
            return target.getPosition();
        }
        return null;
    }

    public void cleanup(BossCleanupReason reason) {
        CycleEventHandler.getSingleton().stopEvents(this);
        activeTiles.clear();
    }

    private void broadcastGfx(int gfxId, Set<Position> positions) {
        if (gfxId <= 0 || positions.isEmpty()) {
            return;
        }
        for (Player player : getPlayersInInstance()) {
            for (Position position : positions) {
                player.getPA().stillGfx(gfxId, position.getX(), position.getY(), position.getHeight(), 0);
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
}
