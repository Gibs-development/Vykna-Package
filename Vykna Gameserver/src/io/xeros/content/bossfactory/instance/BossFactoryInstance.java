package io.xeros.content.bossfactory.instance;

import java.util.concurrent.TimeUnit;

import io.xeros.content.bossfactory.BossCleanupReason;
import io.xeros.content.bossfactory.BossFactoryRegistry;
import io.xeros.content.bossfactory.BossMechanicToggle;
import io.xeros.content.bossfactory.bosses.MotherMadera;
import io.xeros.content.instances.InstancedArea;
import io.xeros.content.instances.InstanceConfiguration;
import io.xeros.content.instances.InstanceConfigurationBuilder;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.ClientGameTimer;
import io.xeros.model.entity.player.Player;

public class BossFactoryInstance extends InstancedArea {
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
        super(CONFIGURATION, MotherMadera.ARENA_BOUNDARY);
        this.owner = owner;
        this.config = config;
        this.expiryTime = System.currentTimeMillis() + DURATION_MS;
        spawnBoss();
        startTimer();
        startBossTargetUpdates();
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
        player.moveTo(resolve(MotherMadera.TELEPORT_POSITION));
        sendTimeMessage(player, remainingMs());
        sendInstanceTimer(player);
        sendBossTarget(player);
    }

    @Override
    public void remove(Player player) {
        super.remove(player);
        clearInstanceTimer(player);
        clearBossTarget(player);
    }

    public boolean isOwnerInside() {
        return getPlayers().contains(owner);
    }

    public void onPlayerDeath(Player player) {
        clearInstanceTimer(player);
        clearBossTarget(player);
        cleanupBoss(BossCleanupReason.PLAYER_DEATH);
    }

    public void onPlayerTeleport(Player player) {
        clearInstanceTimer(player);
        clearBossTarget(player);
        cleanupBoss(BossCleanupReason.TELEPORT);
    }

    @Override
    public void onDispose() {
        CycleEventHandler.getSingleton().stopEvents(this);
        clearInstanceTimerForPlayers();
        clearBossTargetForPlayers();
        cleanupBoss(BossCleanupReason.INSTANCE_EXPIRED);
        BossFactoryInstanceManager.unregister(owner.getLoginNameLower());
    }

    private void spawnBoss() {
        bossNpc = new NPC(MotherMadera.NPC_ID, resolve(MotherMadera.BOSS_SPAWN));
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
                    clearInstanceTimerForPlayers();
                    clearBossTargetForPlayers();
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

    private void startBossTargetUpdates() {
        CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                updateBossTargetForPlayers();
            }
        }, 10);
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

    private void sendInstanceTimer(Player player) {
        int secondsRemaining = (int) Math.max(0, TimeUnit.MILLISECONDS.toSeconds(remainingMs()));
        player.getPA().sendGameTimer(ClientGameTimer.BOSS_INSTANCE, TimeUnit.SECONDS, secondsRemaining);
    }

    private void clearInstanceTimer(Player player) {
        player.getPA().sendGameTimer(ClientGameTimer.BOSS_INSTANCE, TimeUnit.SECONDS, 0);
    }

    private void clearInstanceTimerForPlayers() {
        getPlayers().forEach(this::clearInstanceTimer);
    }

    public void onBossDeath() {
        clearBossTargetForPlayers();
    }

    private void sendBossTarget(Player player) {
        if (bossNpc != null && !bossNpc.isDead()) {
            player.getPA().sendEntityTarget(1, bossNpc);
        }
    }

    private void clearBossTarget(Player player) {
        if (bossNpc == null) {
            return;
        }
        player.getPA().sendEntityTarget(0, bossNpc);
    }

    private void updateBossTargetForPlayers() {
        if (bossNpc == null) {
            return;
        }
        if (bossNpc.isDead()) {
            return;
        }
        getPlayers().forEach(this::sendBossTarget);
    }

    private void clearBossTargetForPlayers() {
        getPlayers().forEach(this::clearBossTarget);
    }

    private String formatRemaining(long remaining) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(remaining);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(remaining) - TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%d:%02d", minutes, seconds);
    }
}
