package io.xeros.content.bossfactory.bosses;

import java.util.List;
import java.util.Set;

import io.xeros.content.bossfactory.BossCleanupReason;
import io.xeros.content.bossfactory.BossController;
import io.xeros.content.bossfactory.BossFight;
import io.xeros.content.bossfactory.BossMechanic;
import io.xeros.content.bossfactory.BossMechanicHandler;
import io.xeros.content.bossfactory.BossMechanicToggle;
import io.xeros.content.bossfactory.BossRotation;
import io.xeros.content.bossfactory.BossRotationBuilder;
import io.xeros.content.bossfactory.prefab.ArenaBeamAttack;
import io.xeros.content.bossfactory.prefab.TileSplatAttack;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.npc.NPCAutoAttack;
import io.xeros.content.combat.npc.NPCAutoAttackBuilder;
import io.xeros.model.Animation;
import io.xeros.model.CombatType;
import io.xeros.model.Graphic;
import io.xeros.model.ProjectileBase;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

public class GoblinNecromancerBoss implements BossController, BossMechanicToggle {
    public static final int NPC_ID = 3515;

    private static final int MAGIC_ANIM = 9829;
    private static final int RANGE_ANIM = 9830;
    private static final int SPECIAL_ANIM = 1234;

    private static final int MAGIC_PROJECTILE = 2501;
    private static final int MAGIC_IMPACT = 2502;
    private static final int RANGE_PROJECTILE = 2503;
    private static final int RANGE_IMPACT = 2504;
    private static final int SPECIAL_PROJECTILE = 2505;
    private static final int SPECIAL_IMPACT = 2506;

    private BossFight fight;
    private TileSplatAttack tileSplatAttack;
    private ArenaBeamAttack arenaBeamAttack;

    @Override
    public void bind(NPC npc) {
        BossRotation rotation = BossRotationBuilder.rotation()
                .repeat(7, BossRotationBuilder.alternate(BossMechanic.BASIC_MAGIC, BossMechanic.BASIC_RANGE))
                .then(BossMechanic.ARENA_BEAM)
                .then(BossMechanic.FLOOR_SPLAT)
                .loop()
                .build();

        tileSplatAttack = new TileSplatAttack(npc, new TileSplatAttack.Config()
                .setTelegraphGfx(3001)
                .setGroundSplatGfx(3002)
                .setHitGfx(3003)
                .setAnimationId(SPECIAL_ANIM)
                .setDurationTicks(12)
                .setTickInterval(2)
                .setDamagePerTick(12)
                .setSize(4, 4)
                .setTargetingStyle(TileSplatAttack.TargetingStyle.AROUND_PLAYER));

        arenaBeamAttack = new ArenaBeamAttack(npc, new ArenaBeamAttack.Config()
                .setBeamGfx(3004)
                .setBeamWidth(1)
                .setTickRate(2)
                .setDurationTicks(10)
                .setOnPlayerHit(player -> player.appendDamage(npc, 15, Hitmark.HIT)));

        fight = new BossFight(rotation, List.of(
                new AutoAttackMechanic(BossMechanic.BASIC_MAGIC, target -> basicMagic()),
                new AutoAttackMechanic(BossMechanic.BASIC_RANGE, target -> basicRange()),
                new AutoAttackMechanic(BossMechanic.ARENA_BEAM, target -> arenaBeamSpecial(npc, target)),
                new AutoAttackMechanic(BossMechanic.FLOOR_SPLAT, target -> tileSplatSpecial(npc, target))
        ));
        fight.bind(npc);
    }

    @Override
    public NPCAutoAttack selectAutoAttack(Entity target) {
        return fight.selectAutoAttack(target);
    }

    @Override
    public void ensureScaledStats(Player primaryTarget) {
        fight.ensureScaledStats(primaryTarget);
    }

    @Override
    public double getDropMultiplier(Player killer) {
        return fight.getDropMultiplier(killer);
    }

    @Override
    public void cleanup(BossCleanupReason reason) {
        if (tileSplatAttack != null) {
            tileSplatAttack.cleanup(reason);
        }
        if (arenaBeamAttack != null) {
            arenaBeamAttack.cleanup(reason);
        }
        if (fight != null) {
            fight.cleanup(reason);
        }
    }

    @Override
    public void applyEnabledMechanics(Set<BossMechanic> enabled) {
        if (fight != null) {
            fight.setEnabledMechanics(enabled);
        }
    }

    private NPCAutoAttack basicMagic() {
        return new NPCAutoAttackBuilder()
                .setAnimation(new Animation(MAGIC_ANIM))
                .setCombatType(CombatType.MAGE)
                .setMaxHit(22)
                .setAttackDelay(4)
                .setHitDelay(4)
                .setDistanceRequiredForAttack(12)
                .setProjectile(new ProjectileBase(MAGIC_PROJECTILE, 1, 40, 20, 30, 16, 0))
                .setEndGraphic(new Graphic(MAGIC_IMPACT, Graphic.GraphicHeight.HIGH))
                .setMultiAttack(true)
                .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                .createNPCAutoAttack();
    }

    private NPCAutoAttack basicRange() {
        return new NPCAutoAttackBuilder()
                .setAnimation(new Animation(RANGE_ANIM))
                .setCombatType(CombatType.RANGE)
                .setMaxHit(24)
                .setAttackDelay(4)
                .setHitDelay(3)
                .setDistanceRequiredForAttack(12)
                .setProjectile(new ProjectileBase(RANGE_PROJECTILE, 1, 36, 20, 30, 16, 0))
                .setEndGraphic(new Graphic(RANGE_IMPACT, Graphic.GraphicHeight.HIGH))
                .setMultiAttack(true)
                .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                .createNPCAutoAttack();
    }

    private NPCAutoAttack arenaBeamSpecial(NPC npc, Entity target) {
        return new NPCAutoAttackBuilder()
                .setAnimation(new Animation(SPECIAL_ANIM))
                .setCombatType(CombatType.MAGE)
                .setAttackDelay(6)
                .setHitDelay(4)
                .setDistanceRequiredForAttack(14)
                .setProjectile(new ProjectileBase(SPECIAL_PROJECTILE, 1, 30, 25, 35, 16, 0))
                .setEndGraphic(new Graphic(SPECIAL_IMPACT, Graphic.GraphicHeight.HIGH))
                .setAttackDamagesPlayer(false)
                .setOnAttack(attack -> {
                    Position center = npc.getCenterPosition();
                    Position targetPos = target.getPosition();
                    arenaBeamAttack.execute(center, targetPos);
                })
                .createNPCAutoAttack();
    }

    private NPCAutoAttack tileSplatSpecial(NPC npc, Entity target) {
        return new NPCAutoAttackBuilder()
                .setAnimation(new Animation(SPECIAL_ANIM))
                .setCombatType(CombatType.MAGE)
                .setAttackDelay(6)
                .setHitDelay(4)
                .setDistanceRequiredForAttack(14)
                .setProjectile(new ProjectileBase(SPECIAL_PROJECTILE, 1, 30, 25, 35, 16, 0))
                .setEndGraphic(new Graphic(SPECIAL_IMPACT, Graphic.GraphicHeight.HIGH))
                .setAttackDamagesPlayer(false)
                .setOnAttack(attack -> {
                    Player primary = (attack.getVictim() instanceof Player) ? (Player) attack.getVictim() : null;
                    tileSplatAttack.execute(primary);
                })
                .createNPCAutoAttack();
    }

    private static class AutoAttackMechanic implements BossMechanicHandler {
        private final BossMechanic mechanic;
        private final java.util.function.Function<Entity, NPCAutoAttack> factory;

        private AutoAttackMechanic(BossMechanic mechanic, java.util.function.Function<Entity, NPCAutoAttack> factory) {
            this.mechanic = mechanic;
            this.factory = factory;
        }

        @Override
        public BossMechanic getMechanic() {
            return mechanic;
        }

        @Override
        public NPCAutoAttack buildAttack(Entity target) {
            return factory.apply(target);
        }

        @Override
        public void cleanup(BossCleanupReason reason) {
        }
    }
}
