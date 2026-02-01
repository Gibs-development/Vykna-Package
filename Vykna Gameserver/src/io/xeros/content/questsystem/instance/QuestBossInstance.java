package io.xeros.content.questsystem.instance;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import io.xeros.content.instances.InstanceConfiguration;
import io.xeros.content.instances.InstanceConfigurationBuilder;
import io.xeros.content.instances.InstancedArea;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

public class QuestBossInstance extends InstancedArea {
    private static final AtomicLong INSTANCE_COUNTER = new AtomicLong(1);

    private final long instanceId;
    private final Player owner;
    private final String questId;
    private final String stepId;
    private final Position entryPosition;
    private final Position exitPosition;
    private final Position bossSpawn;
    private final int bossNpcId;
    private final int bossMaxHit;
    private final long expiresAt;
    private NPC bossNpc;

    public QuestBossInstance(Player owner,
                             String questId,
                             String stepId,
                             Boundary boundary,
                             Position entryPosition,
                             Position exitPosition,
                             Position bossSpawn,
                             int bossNpcId,
                             int bossMaxHit,
                             long timeoutMillis) {
        super(defaultConfig(), boundary);
        this.instanceId = INSTANCE_COUNTER.getAndIncrement();
        this.owner = owner;
        this.questId = questId;
        this.stepId = stepId;
        this.entryPosition = entryPosition;
        this.exitPosition = exitPosition;
        this.bossSpawn = bossSpawn;
        this.bossNpcId = bossNpcId;
        this.bossMaxHit = bossMaxHit;
        long timeout = timeoutMillis <= 0 ? TimeUnit.MINUTES.toMillis(10) : timeoutMillis;
        this.expiresAt = System.currentTimeMillis() + timeout;
    }

    private static InstanceConfiguration defaultConfig() {
        return new InstanceConfigurationBuilder()
     //           .closeOnPlayersEmpty(true)
                .createInstanceConfiguration();
    }

    public long getInstanceId() {
        return instanceId;
    }

    public String getQuestId() {
        return questId;
    }

    public String getStepId() {
        return stepId;
    }

    public void enter(Player player) {
        if (player == null) {
            return;
        }
        Position resolved = resolve(entryPosition);
        player.getPA().movePlayer(resolved.getX(), resolved.getY(), resolved.getHeight());
        add(player);
        ensureBossSpawned();
    }

    public void ensureBossSpawned() {
        if (bossNpc != null && !bossNpc.isDead() && !bossNpc.isDeadOrDying()) {
            return;
        }
        Position spawn = resolve(bossSpawn);
        bossNpc = NPCSpawning.spawnNpc(this, bossNpcId, spawn.getX(), spawn.getY(), spawn.getHeight(), 1, bossMaxHit);
    }

    @Override
    public void onDispose() {
        for (Player player : getPlayers()) {
            if (exitPosition != null) {
                player.getPA().movePlayer(exitPosition.getX(), exitPosition.getY(), exitPosition.getHeight());
            }
        }
        QuestInstanceManager.remove(owner, questId, stepId);
    }

    @Override
    public void tick(Entity entity) {
        if (System.currentTimeMillis() >= expiresAt) {
            dispose();
        }
    }

    @Override
    public boolean handleDeath(Player player) {
        if (player == null || player != owner) {
            return false;
        }
        ensureBossSpawned();
        Position resolved = resolve(entryPosition);
        player.getPA().movePlayer(resolved.getX(), resolved.getY(), resolved.getHeight());
        return true;
    }
}
