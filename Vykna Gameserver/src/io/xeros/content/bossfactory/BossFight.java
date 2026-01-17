package io.xeros.content.bossfactory;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.xeros.content.combat.npc.NPCAutoAttack;
import io.xeros.content.combat.npc.NPCAutoAttackBuilder;
import io.xeros.model.CombatType;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BossFight implements BossController {
    private static final Logger logger = LoggerFactory.getLogger(BossFight.class);
    private static final double MIN_HP_SCALE = 0.6;
    private static final double MIN_DROP_MULTIPLIER = 0.2;

    private final BossRotation rotation;
    private final Map<BossMechanic, BossMechanicHandler> handlers = new EnumMap<>(BossMechanic.class);
    private final Set<BossMechanic> enabledMechanics = EnumSet.noneOf(BossMechanic.class);
    private final Set<BossMechanic> basics = EnumSet.of(BossMechanic.BASIC_MAGIC, BossMechanic.BASIC_RANGE);

    private NPC npc;
    private boolean scaledStatsApplied;
    private int baseMaxHp;
    private boolean nextBasicMagic = true;
    private double dropMultiplier = 1.0;

    public BossFight(BossRotation rotation, Collection<BossMechanicHandler> mechanicHandlers) {
        this.rotation = rotation;
        mechanicHandlers.forEach(handler -> handlers.put(handler.getMechanic(), handler));
        enabledMechanics.addAll(basics);
    }

    public void setEnabledMechanics(Set<BossMechanic> enabled) {
        enabledMechanics.clear();
        enabledMechanics.addAll(basics);
        enabledMechanics.addAll(enabled);
        dropMultiplier = Math.max(MIN_DROP_MULTIPLIER, calculateEnabledFraction());
    }

    @Override
    public void bind(NPC npc) {
        this.npc = npc;
        this.baseMaxHp = npc.getHealth().getMaximumHealth();
        if (npc.getNpcAutoAttacks().isEmpty()) {
            npc.setNpcAutoAttacks(java.util.List.of(new NPCAutoAttackBuilder()
                    .setCombatType(CombatType.MELEE)
                    .setMaxHit(0)
                    .setHitDelay(1)
                    .setAttackDelay(4)
                    .setDistanceRequiredForAttack(1)
                    .createNPCAutoAttack()));
        }
    }

    @Override
    public NPCAutoAttack selectAutoAttack(Entity target) {
        if (npc == null) {
            throw new IllegalStateException("BossFight not bound to NPC.");
        }
        if (target instanceof Player) {
            ensureScaledStats((Player) target);
        }

        Optional<BossMechanic> next = chooseNextMechanic();
        if (next.isEmpty()) {
            BossMechanic fallback = nextBasicMagic ? BossMechanic.BASIC_MAGIC : BossMechanic.BASIC_RANGE;
            nextBasicMagic = !nextBasicMagic;
            logger.debug("BossFactory fallback to basic mechanic={} for npcId={}", fallback, npc.getNpcId());
            return handlers.get(fallback).buildAttack(target);
        }

        BossMechanic mechanic = next.get();
        BossMechanicHandler handler = handlers.get(mechanic);
        if (handler == null) {
            logger.debug("BossFactory missing mechanic handler for {} on npcId={}, falling back.", mechanic, npc.getNpcId());
            return handlers.get(nextBasicMagic ? BossMechanic.BASIC_MAGIC : BossMechanic.BASIC_RANGE).buildAttack(target);
        }
        logger.debug("BossFactory chose mechanic={} for npcId={}", mechanic, npc.getNpcId());
        return handler.buildAttack(target);
    }

    private Optional<BossMechanic> chooseNextMechanic() {
        int attempts = rotation.getMechanics().size();
        if (attempts == 0) {
            return Optional.empty();
        }
        for (int i = 0; i < attempts; i++) {
            Optional<BossMechanic> next = rotation.next();
            if (next.isEmpty()) {
                return Optional.empty();
            }
            BossMechanic mechanic = next.get();
            BossMechanicHandler handler = handlers.get(mechanic);
            if (!enabledMechanics.contains(mechanic)) {
                logger.debug("BossFactory skipped mechanic={} for npcId={} (disabled)", mechanic, npc.getNpcId());
                continue;
            }
            if (handler != null && !handler.isAvailable()) {
                logger.debug("BossFactory skipped mechanic={} for npcId={} (unavailable/cooldown)", mechanic, npc.getNpcId());
                continue;
            }
            return Optional.of(mechanic);
        }
        return Optional.empty();
    }

    @Override
    public void ensureScaledStats(Player primaryTarget) {
        if (scaledStatsApplied) {
            return;
        }
        scaledStatsApplied = true;

        double enabledFraction = calculateEnabledFraction();
        double hpScale = Math.max(MIN_HP_SCALE, enabledFraction);
        dropMultiplier = Math.max(MIN_DROP_MULTIPLIER, enabledFraction);

        int scaledMaxHp = Math.max(1, (int) Math.round(baseMaxHp * hpScale));
        npc.getHealth().setMaximumHealth(scaledMaxHp);
        npc.getHealth().reset();
        logger.debug("BossFactory scaled stats npcId={} baseHp={} scaledHp={} dropMultiplier={} enabledFraction={}",
                npc.getNpcId(), baseMaxHp, scaledMaxHp, dropMultiplier, enabledFraction);
    }

    @Override
    public double getDropMultiplier(Player killer) {
        return dropMultiplier;
    }

    @Override
    public void cleanup(BossCleanupReason reason) {
        handlers.values().forEach(handler -> handler.cleanup(reason));
        rotation.reset();
        scaledStatsApplied = false;
        nextBasicMagic = true;
        logger.debug("BossFactory cleanup npcId={} reason={}", npc != null ? npc.getNpcId() : -1, reason);
    }

    private double calculateEnabledFraction() {
        int optionalCount = 0;
        int enabledOptionalCount = 0;
        for (BossMechanic mechanic : handlers.keySet()) {
            if (mechanic.isBasic()) {
                continue;
            }
            optionalCount++;
            if (enabledMechanics.contains(mechanic)) {
                enabledOptionalCount++;
            }
        }
        return optionalCount == 0 ? 1.0 : (double) enabledOptionalCount / optionalCount;
    }
}
