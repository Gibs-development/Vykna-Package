package io.xeros.content.bossfactory.instance;

import java.util.concurrent.TimeUnit;

import io.xeros.content.bossfactory.BossCleanupReason;
import io.xeros.content.bossfactory.BossFactoryRegistry;
import io.xeros.content.bossfactory.BossMechanicToggle;
import io.xeros.content.bossfactory.bosses.GoblinNecromancerBoss;
import io.xeros.content.instances.InstancedArea;
import io.xeros.content.instances.InstanceConfiguration;
import io.xeros.content.instances.InstanceConfigurationBuilder;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

public class BossFactoryInstance extends InstancedArea {
    public static final int ENTRY_OBJECT_ID = 42344;
    public static final Position TELEPORT_POSITION = new Position(1636, 4821, 0);
    public static final Position BOSS_SPAWN = new Position(1626, 4816, 0);
    public static final Boundary ARENA_BOUNDARY = new Boundary(1610, 4810, 1649, 4847);

    private static final long DURATION_MS = TimeUnit.HOURS.toMillis(1);
    private static final InstanceConfiguration CONFIGURATION = new InstanceConfigurationBuilder()
            .setCloseOnPlayersEmpty(false)
            .setRespawnNpcs(true)
            .createInstanceConfiguration();

    private final Player owner;
    private final BossFactoryInstanceConfig config;
    private final long expiryTime;
    private boolean expired;
    private NPC bossNpc;

    public BossFactoryInstance(Player owner, BossFactoryInstanceConfig config) {
        super(CONFIGURATION, ARENA_BOUNDARY);
        this.owner = owner;
        this.config = config;
        this.expiryTime = System.currentTimeMillis() + DURATION_MS;
        spawnBoss();
        startTimer();
    }

    public Player getOwner() {
        return owner;
    }

    public boolean isExpired() {
        return expired || System.currentTimeMillis() >= expiryTime;
    }

    public BossFactoryInstanceConfig getConfig() {
        return config;
    }

    public void enter(Player player) {
        add(player);
        player.moveTo(resolve(TELEPORT_POSITION));
        sendTimeMessage(player, remainingMs());
    }

    public boolean isOwnerInside() {
        return getPlayers().contains(owner);
    }

    public void onPlayerDeath(Player player) {
        cleanupBoss(BossCleanupReason.PLAYER_DEATH);
    }

    public void onPlayerTeleport(Player player) {
        cleanupBoss(BossCleanupReason.TELEPORT);
    }

    @Override
    public void onDispose() {
        CycleEventHandler.getSingleton().stopEvents(this);
        cleanupBoss(BossCleanupReason.INSTANCE_EXPIRED);
        BossFactoryInstanceManager.unregister(owner.getLoginNameLower());
    }

    private void spawnBoss() {
        bossNpc = new NPC(GoblinNecromancerBoss.NPC_ID, resolve(BOSS_SPAWN));
        add(bossNpc);
        if (BossFactoryRegistry.isBoss(bossNpc)) {
            io.xeros.content.bossfactory.BossController controller = BossFactoryRegistry.getOrCreate(bossNpc);
            if (controller instanceof BossMechanicToggle) {
                ((BossMechanicToggle) controller).applyEnabledMechanics(config.getEnabledMechanics());
            }
        }
    }

    private void cleanupBoss(BossCleanupReason reason) {
        if (bossNpc != null) {
            BossFactoryRegistry.cleanupIfBoss(bossNpc, reason);
        }
    }

    private void startTimer() {
        CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
            private long lastMinute = -1;
            private boolean warned30;
            private boolean warned1;

            @Override
            public void execute(CycleEventContainer container) {
                long remaining = remainingMs();
                if (remaining <= 0) {
                    expired = true;
                    broadcast("Your instance has expired and no further bosses will spawn.");
                    if (bossNpc != null) {
                        bossNpc.getBehaviour().setRespawn(false);
                        bossNpc.needRespawn = false;
                        if (!bossNpc.isDead()) {
                            bossNpc.unregister();
                        }
                    }
                    cleanupBoss(BossCleanupReason.INSTANCE_EXPIRED);
                    container.stop();
                    return;
                }

                long minutesRemaining = TimeUnit.MILLISECONDS.toMinutes(remaining);
                if (minutesRemaining != lastMinute) {
                    lastMinute = minutesRemaining;
                    broadcast("Boss instance time remaining: " + formatRemaining(remaining));
                }

                if (!warned30 && remaining <= TimeUnit.MINUTES.toMillis(30)) {
                    broadcast("Your instance only have 30 minutes left");
                    warned30 = true;
                }
                if (!warned1 && remaining <= TimeUnit.MINUTES.toMillis(1)) {
                    broadcast("Your instance will expire in 1 minute");
                    warned1 = true;
                }
            }
        }, 100);
    }

    private long remainingMs() {
        return expiryTime - System.currentTimeMillis();
    }

    private void broadcast(String message) {
        getPlayers().forEach(player -> player.sendMessage(message));
    }

    private void sendTimeMessage(Player player, long remaining) {
        player.sendMessage("Boss instance time remaining: " + formatRemaining(remaining));
    }

    private String formatRemaining(long remaining) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(remaining);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(remaining) - TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%d:%02d", minutes, seconds);
    }
}
