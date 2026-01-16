package io.xeros.content.bosses.nex;

import com.google.common.collect.Lists;
import io.xeros.content.bosses.hydra.CombatProjectile;
import io.xeros.content.bosses.nex.attacks.*;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.npc.NPCAutoAttack;
import io.xeros.content.combat.npc.NPCAutoAttackBuilder;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.model.*;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.HealthStatus;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ImmutableItem;
import io.xeros.util.Misc;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author ArkCane#1489 @ ArkCane.net
 **/

public class NexNPC extends NPC {

    public static final Position SPAWN_POSITION = new Position(2924, 5202);
    public static final Boundary BOUNDARY = new Boundary(2909, 5187, 2943, 5220);
    public static int Loot_KEY = 3460;
    final int MELEE_ANIM = 9181;
    final int MAGE_ANIM = 9179;
    final int RANGE_ANIM = 9180;
    final int SIPHON_ANIM = 9183;
    private int attackSpeed = 4;
    private boolean isAttacking = false;
    private boolean canAttack = false;
    private boolean healthIncreased;

    final int CRUOR = 11285;
    final int FUMUS = 11283;
    final int UMBRA = 11284;
    final int GLACIES = 11286;
    public boolean fumusStarted = false;
    public boolean cruorStarted = false;
    public boolean umbraStarted = false;
    public boolean glaciesStarted = false;
    NPC glaciesNPC;
    NPC cruorNPC;
    NPC umbraNPC;
    NPC fumusNPC;
    Position fumusSpawnPoint = new Position(2915, 5213);
    Position umbraSpawnPoint = new Position(2935, 5213);
    Position cruorSpawnPoint = new Position(2915, 5193);
    Position glaciesSpawnPoint = new Position(2935, 5193);
    int spawnMageAnimation = 9189;
    List<Player> targets;
    Phase currentPhase;
    Player currenTarget;
    int specialTimer = 75;
    boolean canBeAttacked;
    boolean started = false;
    public boolean siphoning = false;

    private final Nex instance;

    List<NPC> reavers = new ArrayList<>();

    public NexNPC(int npcId, Position position, Nex instance) {
        super(npcId, position);
        getBehaviour().setRespawn(false);

        this.instance = instance;
        instance.add(this);
        init();
        loadItems();
        currentPlayersize = instance.getPlayers().size();
    }

    @Override
    public boolean susceptibleTo(HealthStatus status) {
        return false;
    }

    @Override
    public boolean hasBlockAnimation() {
        return false;
    }

    @Override
    public boolean canBeDamaged(Entity entity) {
        return !isDead() && !isInvisible();
    }


    /*
     Gets the targets in the area
      */
    public void updateTargets() {
        targets = PlayerHandler.getPlayers().stream().filter(plr ->
                !plr.isDead && BOUNDARY.in(plr) && plr.getHeight() == instance.getHeight()).collect(Collectors.toList());
    }
    /*
    Initialising Nex
     */
    public void init() {
        currentPhase = Phase.SMOKE;
//        setAttacks();
        canBeAttacked = true;
        nexStartupEvent(this);
    }
    @Override
    public boolean isAutoRetaliate() {
        return !isDead() && !isInvisible() && getBehaviour().isAggressive();
    }

    /*
    Method is called after every hit nex takes
     */
    public void postHitDefend(NPC npc) {
        if (fumusNPC != null && fumusNPC.isInvisible() && fumusStarted) {
            fumusNPC.setInvisible(false);
        } else if (umbraNPC != null && umbraNPC.isInvisible() && umbraStarted) {
            umbraNPC.setInvisible(false);
        } else if (cruorNPC != null && cruorNPC.isInvisible() && cruorStarted) {
            cruorNPC.setInvisible(false);
        } else if (glaciesNPC != null && glaciesNPC.isInvisible() && glaciesStarted) {
            glaciesNPC.setInvisible(false);
        }

        if(currentPhase == Phase.SMOKE &&
                npc.getHealth().getCurrentHealth() <= (npc.getHealth().getMaximumHealth() * 0.80) && !fumusNPC.isDead && !fumusStarted) {
            npc.forceChat("Fumus, don't fail me!");
            for (Player target : targets) {
                target.sendMessage("@bla@[@red@NEX@bla@] @red@ Time to kill Fumus!");
            }
            fumusNPC.setInvisible(false);
            canAttack = false;
            canBeAttacked = false;
            fumusStarted = true;
        }
        else if(currentPhase == Phase.SHADOW &&
                npc.getHealth().getCurrentHealth() <= (npc.getHealth().getMaximumHealth() * 0.60) && !umbraNPC.isDead && !umbraStarted) {
            npc.forceChat("Umbra, don't fail me!");
            for (Player target : targets) {
                target.sendMessage("@bla@[@red@NEX@bla@] @red@ Time to kill Umbra!");
            }
            umbraNPC.setInvisible(false);
            canAttack = false;
            canBeAttacked = false;
            umbraStarted = true;
        } else if(currentPhase == Phase.BLOOD &&
                npc.getHealth().getCurrentHealth() <= (npc.getHealth().getMaximumHealth() * 0.40) && !cruorNPC.isDead && !cruorStarted) {
            npc.forceChat("Cruor, don't fail me!");
            for (Player target : targets) {
                target.sendMessage("@bla@[@red@NEX@bla@] @red@ Time to kill Cruor!");
            }
            cruorNPC.setInvisible(false);
            canAttack = false;
            canBeAttacked = false;
            cruorStarted = true;
        } else if(currentPhase == Phase.ICE &&
                npc.getHealth().getCurrentHealth() <= (npc.getHealth().getMaximumHealth() * 0.20) && !glaciesNPC.isDead && !glaciesStarted) {
            npc.forceChat("Glacies, don't fail me!");
            for (Player target : targets) {
                target.sendMessage("@bla@[@red@NEX@bla@] @red@ Time to kill Glacies!");
            }
            glaciesNPC.setInvisible(false);
            canAttack = false;
            canBeAttacked = false;
            glaciesStarted = true;
        }
    } 
    /*
    Nex is starting a new phase
     */
    public void startNewPhase(Phase newPhase) {
        currentPhase = newPhase;
        canBeAttacked = true;
        canAttack = true;
//        setAttacks();
        switch (newPhase) {
            case SMOKE:
                isAttacking = true;
                asNPC().forceChat("Fill my soul with smoke!");
                break;
            case SHADOW:
                asNPC().forceChat("Darken my shadow!");
                break;
            case BLOOD:
                asNPC().forceChat("Flood my lungs with blood!");
                break;
            case ICE:
                asNPC().forceChat("Infuse me with the power of ice!");
                break;
            case ZAROS:
                asNPC().forceChat("NOW, THE POWER OF ZAROS!");
                asNPC().appendHeal(500, Hitmark.HEAL_PURPLE);
                break;
        }
    }


    @Override
    public boolean canBeAttacked(Entity entity) {
        return canBeAttacked;
    }

    private void meleeAttack(Player target) {
        asNPC().startAnimation(MELEE_ANIM);
        int dmg = Misc.random(0,15);
        if (target.protectingMelee()) {
            dmg = (dmg/2);
        }
        target.appendDamage(dmg, (dmg > 0 ? Hitmark.HIT : Hitmark.MISS));
    }

    public void sendProjectile(Player target, CombatProjectile projectile, CombatType combatType, int maxDamage) {
        int size = (int) Math.ceil((double) asNPC().getSize() / 2.0);
        int centerX = asNPC().getX() + size;
        int centerY = asNPC().getY() + size;
        int offsetX = (centerY - target.getY()) * -1;
        int offsetY = (centerX - target.getX()) * -1;
        currenTarget.getPA().createPlayersProjectile(centerX, centerY, offsetX, offsetY, projectile.getAngle(), projectile.getSpeed(), projectile.getGfx(),
                projectile.getStartHeight(), projectile.getEndHeight(), -1, 65, projectile.getDelay());
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if(container.getTotalTicks() == 3) {
                    int damage = Misc.random(maxDamage);
                    if(combatType.equals(CombatType.MAGE) && target.protectingMagic())
                        damage = (damage / 2);
                    else if(combatType.equals(CombatType.RANGE) && target.protectingRange())
                        damage = (damage / 2);
                    else if(combatType.equals(CombatType.MELEE) && target.protectingMelee())
                        damage = (damage / 2);

                    target.appendDamage(damage, (damage > 0 ? Hitmark.HIT : Hitmark.MISS));
                    container.stop();
                }
            }
        }, 1);
    }

    private static final CombatProjectile SMOKE_RUSH_PROJECTILE = new CombatProjectile(384, 50, 25, 4, 50, 0, 50);
    private static final CombatProjectile SHADOW_RANGE_PROJECTILE = new CombatProjectile(2012, 50, 25, 4, 50, 0, 50);
    private static final CombatProjectile BASIC_MAGIC_PROJECTILE = new CombatProjectile(2004, 50, 25, 4, 50, 0, 50);

    private void attack() {
        if (!canAttack) {
            return;
        }
        attackSpeed = 4;
        updateTargets();
        currenTarget = getRandomTarget();
        asNPC().facePlayer(currenTarget.getIndex());
        switch (currentPhase) {
            case SMOKE:
                if (currenTarget.getPosition().getAbsDistance(asNPC().getPosition()) <= 2 && Misc.random(0, 1) == 0) {
                    meleeAttack(currenTarget);
                    this.startAnimation(MELEE_ANIM);
                } else if (Misc.random(0, 10) == 0 && specialTimer <= 0) {
                    specialTimer = 75;
                    this.forceChat("Let the virus flow through you!");
                    this.startAnimation(9188);
                    new ChokeAttack(getRandomTarget());
                } else {
                    for (Player player : targets) {
                        this.startAnimation(MAGE_ANIM);
                        sendProjectile(player, SMOKE_RUSH_PROJECTILE, CombatType.MAGE, 33);
                        player.startGraphic(new Graphic(387, Graphic.GraphicHeight.HIGH));
                    }
                }
                break;
            case SHADOW:
                if (currenTarget.getPosition().getAbsDistance(asNPC().getPosition()) <= 2 && Misc.random(0, 1) == 0) {
                    meleeAttack(currenTarget);
                    this.startAnimation(MELEE_ANIM);
                } else if (Misc.random(0, 10) == 0 && specialTimer <= 0) {
                    specialTimer = 75;
                    this.forceChat("Fear the shadow!");
                    this.startAnimation(9188);
                    new ShadowSmash(targets);
                } else if (currenTarget.getPosition().getAbsDistance(asNPC().getPosition()) > 2 && Misc.random(2) == 0) {
                    startAnimation(9181);
                    meleeAttack(currenTarget);
                } else {
                    for (Player player : targets) {
                        this.forceChat("Shadow Fire!");
                        player.startGraphic(new Graphic(382, Graphic.GraphicHeight.HIGH));
                        sendProjectile(player, SHADOW_RANGE_PROJECTILE, CombatType.RANGE, 35);
                        this.startAnimation(RANGE_ANIM);
                    }
                }
                break;
            case BLOOD:
                if (Misc.random(0,10) == 0 && specialTimer <= 0) {
                    new BloodBarrage(this, getRandomTarget(), targets);
                    this.startAnimation(MAGE_ANIM);
                    specialTimer = 75;
                } else if (Misc.random(0, 10) == 0 && specialTimer <= 0) {
                    specialTimer = 75;
                    new BloodSacrifice(asNPC(), getRandomTarget());
                    this.forceChat("I demand a blood sacrifice!");
                } else if (Misc.random(0, 10) == 0 && specialTimer <= 0) {
                    specialTimer = 75;
                    spawnBloodReavers();
                    this.forceChat("A siphon will solve this!");
                    nexSiphon(this);
                    this.startAnimation(SIPHON_ANIM);
                } else if (currenTarget.getPosition().getAbsDistance(asNPC().getPosition()) <= 2) {
                    meleeAttack(currenTarget);
                    this.startAnimation(MELEE_ANIM);
                } else if (currenTarget.getPosition().getAbsDistance(asNPC().getPosition()) > 2 && Misc.random(2) == 0) {
                    startAnimation(9181);
                    meleeAttack(currenTarget);}
                break;
            case ICE:
                if (Misc.random(2) == 0 && specialTimer <= 0) {
                    this.forceChat("Die now, in a prison of ice!");
                    updateTargets();
                    System.out.println("IcePrison");
                    this.startAnimation(MAGE_ANIM);
                    specialTimer = 75;
                    for (Player target : targets) {
                        new IceBarrage(target, targets);
                        this.startAnimation(MAGE_ANIM);
                    }
                } else if (Misc.random(0, 10) == 0 && specialTimer <= 0) {
                    this.forceChat("Contain this!");
                    updateTargets();
                    for (Player target : targets) {
                        new IceBarrage(target, targets);
                        this.startAnimation(MAGE_ANIM);
                    }
                    specialTimer = 75;
                } else if (Misc.random(0, 10) == 0 && specialTimer <= 0) {
                    this.forceChat("Die now, in a prison of ice!");
                    updateTargets();
                    System.out.println("IcePrison");
                    this.startAnimation(MAGE_ANIM);
                    specialTimer = 75;
                    for (Player target : targets) {
                        new IceBarrage(target, targets);
                        this.startAnimation(MAGE_ANIM);
                    }
                } else if (currenTarget.getPosition().getAbsDistance(asNPC().getPosition()) <= 2) {
                    this.moveTowards(currenTarget.getX(), currenTarget.getY());
                    meleeAttack(currenTarget);
                    this.startAnimation(9181);
                } else if (currenTarget.getPosition().getAbsDistance(asNPC().getPosition()) > 2 && Misc.random(2) == 0) {
                    startAnimation(9181);
                    meleeAttack(currenTarget);}
                break;
            case ZAROS:
                if (currenTarget.getPosition().getAbsDistance(asNPC().getPosition()) < 2 && Misc.random(2) == 0) {
                    meleeAttack(currenTarget);
                    this.startAnimation(9181);
                } else if (currenTarget.getPosition().getAbsDistance(asNPC().getPosition()) > 2 && Misc.random(2) == 0) {
                    startAnimation(9181);
                    meleeAttack(currenTarget);
//                    this.startAnimation(9181); // 9175 = walk / 9176 = speed walk / 9178 = Fly?
                } else {
                    for (Player player : targets) {
                    sendProjectile(player, BASIC_MAGIC_PROJECTILE, CombatType.MAGE, 40);
                    this.startAnimation(MAGE_ANIM);
                }
                }
                break;
        }
    }

    private void setAttacks() {
        switch (currentPhase) {
            case SMOKE:
                setNpcAutoAttacks(Lists.newArrayList(
                        new NPCAutoAttackBuilder()
                                .setSelectAutoAttack(attack -> Misc.trueRand(10) == 0 && specialTimer <= 0)//&& !isImmune(attack.getEntity().asPlayer()))
                                .setCombatType(CombatType.MAGE)
                                .setDistanceRequiredForAttack(2)
                                .setHitDelay(2) .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                                .setAnimation(new Animation(MAGE_ANIM))
                                .setMaxHit(0)
                                .setAttackDelay(2)
                                .setOnAttack(attack -> {
                                    this.forceChat("Let the virus flow through you!");
                                    new ChokeAttack(getRandomTarget());
                                    specialTimer = 75;
                                })
                                .createNPCAutoAttack(),

                        /**
                         * Magic attack
                         */
                        new NPCAutoAttackBuilder()
                                .setCombatType(CombatType.MAGE)
                                .setDistanceRequiredForAttack(2)
                                .setHitDelay(4)
                                .setMaxHit(33) .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                                .setMultiAttack(true)
                                .setAnimation(new Animation(MAGE_ANIM))//magic attack
                                .setAttackDelay(6)
                                .setProjectile(new ProjectileBaseBuilder().setProjectileId(384).setCurve(16).setSpeed(50).setSendDelay(3).createProjectileBase())
                                .setOnHit(attack -> {
                                    attack.getVictim().asPlayer().startGraphic(new Graphic(85, Graphic.GraphicHeight.HIGH));//85 if fail 140 is hit
                                })
                                .setOnHit(attack -> {
                                    attack.getVictim().asPlayer().startGraphic(new Graphic(385, Graphic.GraphicHeight.HIGH));//85 if fail 140 is hit
                                    if(Misc.random(4) == 0)
                                        attack.getVictim().getHealth().proposeStatus(HealthStatus.POISON, 6, Optional.of(this));
                                })
                                .createNPCAutoAttack(),

                        new NPCAutoAttackBuilder()
                                .setSelectAutoAttack(attack -> Misc.trueRand(1) == 0)
                                .setCombatType(CombatType.MELEE)
                                .setDistanceRequiredForAttack(1)
                                .setHitDelay(2) .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                                .setAnimation(new Animation(MELEE_ANIM))
                                .setMaxHit(30)
                                .setAttackDelay(6)
                                .createNPCAutoAttack()

                ));
                break;
            case SHADOW:
                setNpcAutoAttacks(Lists.newArrayList(
                        new NPCAutoAttackBuilder()
                                .setSelectAutoAttack(attack -> Misc.trueRand(10) == 0 && specialTimer <= 0)//&& !isImmune(attack.getEntity().asPlayer()))
                                .setCombatType(CombatType.MAGE)
                                .setDistanceRequiredForAttack(2)
                                .setHitDelay(2) .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                                .setAnimation(new Animation(MAGE_ANIM))
                                .setProjectile(new ProjectileBaseBuilder().setProjectileId(390).setCurve(16).setSpeed(10).setSendDelay(3).createProjectileBase())//SMOKE
                                .setMaxHit(0)
                                .setAttackDelay(2)
                                .setOnAttack(attack -> {
                                    this.forceChat("Fear the shadow!");
                                    updateTargets();
                                    new ShadowSmash(targets);
                                    specialTimer = 75;
                                })
                                .createNPCAutoAttack(),

                        /**
                         * Magic attack
                         */
                        new NPCAutoAttackBuilder()
                                .setCombatType(CombatType.RANGE)
                                .setDistanceRequiredForAttack(2)
                                .setHitDelay(4)
                                .setMaxHit(33) .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                                .setMultiAttack(true)
                                .setAnimation(new Animation(RANGE_ANIM))
                                .setAttackDelay(6)
                                .setProjectile(new ProjectileBaseBuilder().setProjectileId(2012).setCurve(16).setSpeed(50).setSendDelay(3).createProjectileBase())
                                .setOnHit(attack -> {
                                    attack.getVictim().asPlayer().startGraphic(new Graphic(85, Graphic.GraphicHeight.HIGH));//85 if fail 140 is hit
                                })
                                .createNPCAutoAttack(),

                        new NPCAutoAttackBuilder()
                                .setSelectAutoAttack(attack -> Misc.trueRand(1) == 0)
                                .setCombatType(CombatType.MELEE)
                                .setDistanceRequiredForAttack(1)
                                .setHitDelay(2)
                                .setAnimation(new Animation(MELEE_ANIM))
                                .setMaxHit(30) .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                                .setAttackDelay(6)
                                .createNPCAutoAttack()
                ));
                break;
            case BLOOD:
                setNpcAutoAttacks(Lists.newArrayList(
                        new NPCAutoAttackBuilder()
                                .setSelectAutoAttack(attack -> Misc.trueRand(10) == 2 && specialTimer <= 0)//&& !isImmune(attack.getEntity().asPlayer()))
                                .setCombatType(CombatType.MAGE)
                                .setDistanceRequiredForAttack(2)
                                .setHitDelay(2) .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                                .setAnimation(new Animation(-1))
                                .setMaxHit(0)
                                .setAttackDelay(2)
                                .setOnAttack(attack -> {
                                    this.forceChat("I demand a blood sacrifice!");
                                    new BloodSacrifice(this, getRandomTarget());
                                    specialTimer = 75;
                                })
                                .createNPCAutoAttack(),

                        new NPCAutoAttackBuilder()
                                .setSelectAutoAttack(attack -> Misc.trueRand(10) == 2 && specialTimer <= 0)//&& !isImmune(attack.getEntity().asPlayer()))
                                .setCombatType(CombatType.MAGE)
                                .setDistanceRequiredForAttack(2)
                                .setHitDelay(2) .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                                .setAnimation(new Animation(-1))
                                .setMaxHit(0)
                                .setAttackDelay(2)
                                .setOnAttack(attack -> {
                                    this.forceChat("A siphon will solve this!");
                                    spawnBloodReavers();
                                    nexSiphon(this);
                                    specialTimer = 75;
                                })
                                .createNPCAutoAttack(),



                        /**
                         * Magic attack
                         */
                        new NPCAutoAttackBuilder()
                                .setCombatType(CombatType.MAGE)
                                .setDistanceRequiredForAttack(2)
                                .setHitDelay(4) .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                                .setMaxHit(33)
                                .setMultiAttack(false)
                                .setAnimation(new Animation(MAGE_ANIM))
                                .setAttackDelay(6)
                                .setProjectile(new ProjectileBaseBuilder().setProjectileId(374).setCurve(16).setSpeed(10).setSendDelay(3).createProjectileBase())
                                .setOnHit(attack -> {
                                    attack.getVictim().asPlayer().startGraphic(new Graphic(85, Graphic.GraphicHeight.HIGH));//85 if fail 140 is hit
                                })
                                .setOnHit(attack -> {
                                    attack.getVictim().asPlayer().startGraphic(new Graphic(377, Graphic.GraphicHeight.HIGH));//85 if fail 140 is hit
                                    new BloodBarrage(this, attack.getVictim().asPlayer(), targets);
                                })
                                .createNPCAutoAttack(),

                        new NPCAutoAttackBuilder()
                                .setSelectAutoAttack(attack -> Misc.trueRand(1) == 0)
                                .setCombatType(CombatType.MELEE)
                                .setDistanceRequiredForAttack(1)
                                .setHitDelay(2) .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                                .setAnimation(new Animation(MELEE_ANIM))
                                .setMaxHit(30)
                                .setAttackDelay(6)
                                .createNPCAutoAttack()

                ));
                break;
            case ICE:
                setNpcAutoAttacks(Lists.newArrayList(

                        new NPCAutoAttackBuilder()
                                .setSelectAutoAttack(attack -> Misc.trueRand(6) == 2 && specialTimer <= 0)//&& !isImmune(attack.getEntity().asPlayer()))
                                .setCombatType(CombatType.MAGE)
                                .setDistanceRequiredForAttack(2)
                                .setHitDelay(2)
                                .setAnimation(new Animation(-1))
                                .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                                .setMaxHit(0)
                                .setAttackDelay(2)
                                .setOnAttack(attack -> {
                                    this.forceChat("Contain this!");
                                    updateTargets();
                                    new Containment(this, targets);
                                    specialTimer = 75;
                                })
                                .createNPCAutoAttack(),

                        new NPCAutoAttackBuilder()
                                .setSelectAutoAttack(attack -> Misc.trueRand(6) == 2 && specialTimer <= 0)//&& !isImmune(attack.getEntity().asPlayer()))
                                .setCombatType(CombatType.MAGE)
                                .setDistanceRequiredForAttack(2)
                                .setHitDelay(2)
                                .setAnimation(new Animation(-1))
                                .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                                .setMaxHit(0)
                                .setAttackDelay(2)
                                .setOnAttack(attack -> {
                                    this.forceChat("Die now, in a prison of ice!");
                                    updateTargets();
                                    new IcePrison(targets);
                                    specialTimer = 75;
                                })
                                .createNPCAutoAttack(),



                        /**
                         * Magic attack
                         */
                        new NPCAutoAttackBuilder()
                                .setCombatType(CombatType.MAGE)
                                .setDistanceRequiredForAttack(2)
                                .setHitDelay(4)
                                .setMaxHit(33)
                                .setMultiAttack(false)
                                .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                                .setAnimation(new Animation(MAGE_ANIM))
                                .setAttackDelay(6)
                                .setProjectile(new ProjectileBaseBuilder().setProjectileId(-1).setCurve(16).setSpeed(50).setSendDelay(3).createProjectileBase())
                                .setOnHit(attack -> {
                                    attack.getVictim().asPlayer().startGraphic(new Graphic(85, Graphic.GraphicHeight.HIGH));//85 if fail 140 is hit
                                })
                                .setOnHit(attack -> {
                                    attack.getVictim().asPlayer().startGraphic(new Graphic(369, Graphic.GraphicHeight.HIGH));//85 if fail 140 is hit
                                    new IceBarrage(attack.getVictim().asPlayer(), targets);
                                })
                                .createNPCAutoAttack(),

                        new NPCAutoAttackBuilder()
                                .setSelectAutoAttack(attack -> Misc.trueRand(1) == 0)
                                .setCombatType(CombatType.MELEE) .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                                .setDistanceRequiredForAttack(1)
                                .setHitDelay(2)
                                .setAnimation(new Animation(MELEE_ANIM))
                                .setMaxHit(30)
                                .setAttackDelay(6)
                                .createNPCAutoAttack()

                ));
                break;
            case ZAROS:
                setNpcAutoAttacks(Lists.newArrayList(
                        /**
                         * Magic attack
                         */
                        new NPCAutoAttackBuilder()
                                .setCombatType(CombatType.MAGE)
                                .setDistanceRequiredForAttack(2)
                                .setHitDelay(4) .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                                .setMaxHit(33)
                                .setMultiAttack(false)
                                .setAnimation(new Animation(MAGE_ANIM))
                                .setAttackDelay(6)
                                .setProjectile(new ProjectileBaseBuilder().setProjectileId(2010).setCurve(16).setSpeed(50).setSendDelay(3).createProjectileBase())
                                .setOnHit(attack -> {
                                    attack.getVictim().asPlayer().startGraphic(new Graphic(85, Graphic.GraphicHeight.HIGH));//85 if fail 140 is hit
                                })
                                .setOnHit(attack -> {
                                    attack.getVictim().asPlayer().startGraphic(new Graphic(1473, Graphic.GraphicHeight.HIGH));//85 if fail 140 is hit
                                    attack.getVictim().asPlayer().prayerPoint = attack.getVictim().asPlayer().prayerPoint - 5;
                                })
                                .createNPCAutoAttack(),

                        new NPCAutoAttackBuilder()
                                .setSelectAutoAttack(attack -> Misc.trueRand(1) == 0)
                                .setCombatType(CombatType.MELEE)
                                .setDistanceRequiredForAttack(1)
                                .setHitDelay(2) .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                                .setAnimation(new Animation(MELEE_ANIM))
                                .setMaxHit(40)
                                .setAttackDelay(6)
                                .createNPCAutoAttack()

                ));
                break;
        }

    }
    /*
    Gets a random target
     */
    Player getRandomTarget() {
        updateTargets();
        if (instance.getPlayers().size() <= 0) {
            return null;
        }
        return targets.get(Misc.random(targets.size() - 1));
    }

    /*
    Gets the closest player to nex
     */
    void getNearestTarget(NPC npc) {
        updateTargets();
        int nearestDistance = 100;
        for (Player p : targets) {
            if (npc.getPosition().getAbsDistance(p.getPosition()) < nearestDistance) {
                nearestDistance = (int) npc.getPosition().getAbsDistance(p.getPosition());
                currenTarget = p;
            }
        }
    }

    private void spawnBloodReavers() {
        updateTargets();
        int amountToSpawn = targets.size();
        if(amountToSpawn > 8)
            amountToSpawn = 8;

        if(reavers.size() > 0) {
            for (NPC npc: reavers) {
                npc.appendHeal(npc.getHealth().getCurrentHealth(), Hitmark.HEAL_PURPLE);
                reavers.remove(npc);
            }
        }

        for(int i = 0; i < amountToSpawn; i++) {
            reavers.add(NPCSpawning.spawnNpc(11293, targets.get(i).absX, targets.get(i).absY, instance.getHeight(), 1, 20));

        }
        for (NPC reaver : reavers) {
            reaver.getCombatDefinition().setAggressive(true);
            instance.add(reaver);
        }
    }

    private void nexSiphon(NPC npc) {
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                npc.startAnimation(SIPHON_ANIM);
                siphoning = true;
                if(container.getTotalTicks() == 8) {
                    npc.startAnimation(-1);
                    siphoning = false;
                }
            }
        }, 1);
    }

    private void nexStartupEvent(NPC npc) {
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                switch (container.getTotalTicks()) {
                    case 3:
                        npc.startAnimation(spawnMageAnimation);
                        npc.forceChat("Fumus!");
                        npc.facePosition(fumusSpawnPoint);
                        break;
                    case 4:
                        fumusNPC = NPCSpawning.spawnNpc(FUMUS, 2915, 5213, npc.getHeight(), -1, 13);
                        instance.add(fumusNPC);
                        fumusNPC.setInvisible(true);
                        fumusNPC.getCombatDefinition().setAggressive(false);
                        fumusNPC.setNpcAutoAttacks(Lists.newArrayList(new NPCAutoAttackBuilder()
                                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                                .setCombatType(CombatType.MAGE)
                                .setDistanceRequiredForAttack(100)
                                .setProjectile(new ProjectileBaseBuilder().setProjectileId(390).setCurve(16).setSpeed(10).setSendDelay(3).createProjectileBase())//SMOKE
                                .setHitDelay(2)
                                .setMultiAttack(true)
                                .setAnimation(new Animation(1979))
                                .setMaxHit(29)
                                .setAttackDelay(5)
                                .setOnHit(attack -> attack.getVictim().asPlayer().startGraphic(new Graphic(387, Graphic.GraphicHeight.HIGH)))
                                .createNPCAutoAttack()));
                        break;
                    case 6:
                        npc.startAnimation(spawnMageAnimation);
                        npc.forceChat("Umbra!");
                        npc.facePosition(umbraSpawnPoint);
                        break;
                    case 7:
                        umbraNPC = NPCSpawning.spawnNpc(UMBRA, 2935, 5213, npc.getHeight(), -1, 13);
                        instance.add(umbraNPC);
                        umbraNPC.setInvisible(true);
                        umbraNPC.setNpcAutoAttacks(Lists.newArrayList(new NPCAutoAttackBuilder()
                                .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                                .setCombatType(CombatType.MAGE)
                                .setDistanceRequiredForAttack(100)
                                .setHitDelay(4)
                                .setMaxHit(29)
                                .setMultiAttack(true)
                                .setAnimation(new Animation(1979))
                                .setAttackDelay(6)
                                .setProjectile(new ProjectileBaseBuilder().setProjectileId(380).setCurve(16).setSpeed(10).setSendDelay(3).createProjectileBase())//SHADOW
                                .setOnHit(attack -> attack.getVictim().asPlayer().startGraphic(new Graphic(381, Graphic.GraphicHeight.HIGH)))
                                .createNPCAutoAttack()));
                        break;
                    case 9:
                        npc.startAnimation(spawnMageAnimation);
                        npc.forceChat("Cruor!");
                        npc.facePosition(cruorSpawnPoint);
                        break;
                    case 10:
                        cruorNPC = NPCSpawning.spawnNpc(CRUOR, 2915, 5193, npc.getHeight(), -1, 13);
                        instance.add(cruorNPC);
                        cruorNPC.setInvisible(true);
                        cruorNPC.setNpcAutoAttacks(Lists.newArrayList(new NPCAutoAttackBuilder()
                                .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                                .setCombatType(CombatType.MAGE)
                                .setDistanceRequiredForAttack(100)
                                .setHitDelay(4)
                                .setMaxHit(29)
                                .setMultiAttack(true)
                                .setAnimation(new Animation(1979))
                                .setAttackDelay(6)
                                .setProjectile(new ProjectileBaseBuilder().setProjectileId(374).setCurve(16).setSpeed(10).setSendDelay(3).createProjectileBase())//BLOOD
                                .setOnHit(attack -> {
                                    attack.getVictim().asPlayer().startGraphic(new Graphic(377, Graphic.GraphicHeight.HIGH));
                                    new BloodBarrage(cruorNPC, attack.getVictim().asPlayer(), targets);
                                })
                                .createNPCAutoAttack()));
                        break;
                    case 12:
                        npc.startAnimation(spawnMageAnimation);
                        npc.forceChat("Glacies!");
                        npc.facePosition(glaciesSpawnPoint);
                        break;
                    case 13:
                        glaciesNPC = NPCSpawning.spawnNpc(GLACIES, 2935, 5193, npc.getHeight(), -1, 13);
                        instance.add(glaciesNPC);
                        glaciesNPC.setInvisible(true);
                        glaciesNPC.setNpcAutoAttacks(Lists.newArrayList(new NPCAutoAttackBuilder()
                                .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                                .setCombatType(CombatType.MAGE)
                                .setDistanceRequiredForAttack(100)
                                .setHitDelay(4)
                                .setMaxHit(29)
                                .setMultiAttack(true)
                                .setAnimation(new Animation(1979))
                                .setAttackDelay(6)
                                .setProjectile(new ProjectileBaseBuilder().setProjectileId(368).setCurve(16).setSpeed(10).setSendDelay(3).createProjectileBase())//ICE
                                .setOnHit(attack -> {
                                    attack.getVictim().asPlayer().startGraphic(new Graphic(369, Graphic.GraphicHeight.HIGH));
                                    new IceBarrage(attack.getVictim().asPlayer(), targets);
                                })
                                .createNPCAutoAttack()));

                        break;
                    case 14:
                        started = false;
                        canBeAttacked = true;
                        isAttacking = true;
                        startNewPhase(currentPhase);
                        break;
                }
            }
        }, 1);
    }

    private int currentPlayersize;

    @Override
    public void process() {
        if (!isAttacking)
            return;
        postHitDefend(this);
        setAttacks();
        if (currentPlayersize != instance.getPlayers().size()) {
            int hp = (int) ((instance.getPlayers().size() == 3 ? 4250 : 3400) * 1.25);
            asNPC().getHealth().setMaximumHealth(hp);
            asNPC().getHealth().reset();
            currentPlayersize = instance.getPlayers().size();
        }

        if (instance == null) {
            System.out.println("Nex Instance is null or not equal to original instance, disposing.");
            for (NPC npc : instance.getNpcs()) {
                npc.unregister();
            }
            instance.dispose();
            return;
        }
        if (instance.getPlayers().size() <= 0) {
            System.out.println("Instance or players is null, unregistering.");
            instance.dispose();
            this.unregister();
            return;
        }
        if (isInvisible())
            return;
        if (currenTarget == null) {
            currenTarget = instance.getPlayers().get(0);
            System.out.println("Target is null, resetting target to " + currenTarget.getDisplayName());
        }
        if (currenTarget == null) {
            System.out.println("No target was found, discarding fight.");
            unregister();
            return;
        }

        if (isAttacking) {
            attackSpeed--;
            if (attackSpeed <= 0) {
                attack();
            }
        }

        if (fumusStarted && fumusNPC.getHealth().getCurrentHealth() <= 0) {
            startNewPhase(Phase.SHADOW);
        }
        if (umbraStarted && umbraNPC.getHealth().getCurrentHealth() <= 0) {
            startNewPhase(Phase.BLOOD);
        }
        if (cruorStarted && cruorNPC.getHealth().getCurrentHealth() <= 0) {
            startNewPhase(Phase.ICE);
        }
        if (glaciesStarted && glaciesNPC.getHealth().getCurrentHealth() <= 0 && currentPhase != Phase.ZAROS) {
            startNewPhase(Phase.ZAROS);
        }
        reavers.removeIf(npc -> npc.getHealth().getCurrentHealth() <= 0 || npc.isDead);
        if (!started) {
            updateTargets();
            for (Player p : targets) {
                if (p.getPosition().getAbsDistance(p.getPosition()) < 7) {
                    started = true;
                }
            }
        }
        specialTimer--;
        processCombat();
    }

    private void processCombat() {
        super.process();
    }

    @Override
    public void onDeath() {
        removeNpcs();
        asNPC().startAnimation(9184);
        asNPC().forceChat("Taste my wrath!");
        new Wrath(this, targets);

        super.onDeath();

        for (Player player : instance.getPlayers()) {
            getReward(player);
            player.moveTo(new Position(2904, 5206, 0));
        }
    }
    @Override
    public int getSize() {
        return NpcDef.forId(getNpcId()).getSize();
    }

    /*
        Enum of nex phases
         */
    enum Phase {
        SMOKE,
        SHADOW,
        BLOOD,
        ICE,
        ZAROS
    }

    public void removeNpcs() {
        if (fumusNPC != null && !fumusNPC.isDead) {
            fumusNPC.unregister();
        }

        if (cruorNPC != null && !cruorNPC.isDead) {
            cruorNPC.unregister();
        }

        if (glaciesNPC != null && !glaciesNPC.isDead) {
            glaciesNPC.unregister();
        }

        if (umbraNPC != null && !umbraNPC.isDead) {
            umbraNPC.unregister();
        }

        if (this.asNPC() != null && !this.isDead) {
            this.unregister();
        }

        for (NPC reaver : reavers) {
            if (reaver != null && !reaver.isDead) {
                reaver.unregister();
            }
        }
    }

    private final Map<LootRarity, List<GameItem>> items = new HashMap<>();
    private static final Map<LootRarity, List<GameItem>> itemz = new HashMap<>();

    public void loadItems() {
        if (items.isEmpty()) {
            items.put(LootRarity.COMMON, Arrays.asList(
                    new GameItem(Items.DEATH_RUNE, 325),
                    new GameItem(Items.BLOOD_RUNE, 170),
                    new GameItem(Items.SOUL_RUNE, 227),
                    new GameItem(Items.DRAGON_BOLTS_UNF, 90),
                    new GameItem(Items.CANNONBALL, 298),
                    new GameItem(Items.AIR_RUNE, 1365),
                    new GameItem(Items.FIRE_RUNE, 1655),
                    new GameItem(Items.WATER_RUNE, 1599),
                    new GameItem(Items.ONYX_BOLTS_E, 29),
                    new GameItem(Items.AIR_ORB_NOTED, 20),
                    new GameItem(Items.UNCUT_RUBY_NOTED, 26),
                    new GameItem(Items.UNCUT_DIAMOND_NOTED, 17),
                    new GameItem(Items.WINE_OF_ZAMORAK_NOTED, 14),
                    new GameItem(Items.COAL_NOTED, 95),
                    new GameItem(Items.RUNITE_ORE_NOTED, 28),
                    new GameItem(Items.SHARK, 3),
                    new GameItem(Items.PRAYER_POTION4, 1),
                    new GameItem(Items.SARADOMIN_BREW4, 2),
                    new GameItem(Items.SUPER_RESTORE4, 1),
                    new GameItem(Items.COINS, 26748)
            ));

            items.put(LootRarity.RARE, Arrays.asList(
                    new GameItem(19841, 1),
                    new GameItem(19841, 1),
                    new GameItem(19841, 1),
                    new GameItem(19841, 1),
                    new GameItem(19841, 1),
                    new GameItem(19841, 1),
                    new GameItem(19841, 1),
                    new GameItem(19841, 1),
                    new GameItem(19841, 1),
                    new GameItem(19841, 1),
                    new GameItem(19841, 1),
                    new GameItem(19841, 1),
                    new GameItem(26235, 1),

                    new GameItem(26382),
                    new GameItem(26382),
                    new GameItem(26382),

                    new GameItem(26384),
                    new GameItem(26384),
                    new GameItem(26384),

                    new GameItem(26386),
                    new GameItem(26386),
                    new GameItem(26386),

                    new GameItem(26233),
                    new GameItem(26374)

            ));
        }
    }

    static {
        itemz.put(LootRarity.COMMON, Arrays.asList(
                new GameItem(Items.DEATH_RUNE, 325),
                new GameItem(Items.BLOOD_RUNE, 170),
                new GameItem(Items.SOUL_RUNE, 227),
                new GameItem(Items.DRAGON_BOLTS_UNF, 90),
                new GameItem(Items.CANNONBALL, 298),
                new GameItem(Items.AIR_RUNE, 1365),
                new GameItem(Items.FIRE_RUNE, 1655),
                new GameItem(Items.WATER_RUNE, 1599),
                new GameItem(Items.ONYX_BOLTS_E, 29),
                new GameItem(Items.AIR_ORB_NOTED, 20),
                new GameItem(Items.UNCUT_RUBY_NOTED, 26),
                new GameItem(Items.UNCUT_DIAMOND_NOTED, 17),
                new GameItem(Items.WINE_OF_ZAMORAK_NOTED, 14),
                new GameItem(Items.COAL_NOTED, 95),
                new GameItem(Items.RUNITE_ORE_NOTED, 28),
                new GameItem(Items.SHARK, 3),
                new GameItem(Items.PRAYER_POTION4, 1),
                new GameItem(Items.SARADOMIN_BREW4, 2),
                new GameItem(Items.SUPER_RESTORE4, 1),
                new GameItem(Items.COINS, 26748)
        ));

        itemz.put(LootRarity.RARE, Arrays.asList(
                new GameItem(19841, 1),
                new GameItem(19841, 1),
                new GameItem(19841, 1),
                new GameItem(19841, 1),
                new GameItem(19841, 1),
                new GameItem(19841, 1),
                new GameItem(19841, 1),
                new GameItem(19841, 1),
                new GameItem(19841, 1),
                new GameItem(19841, 1),
                new GameItem(19841, 1),
                new GameItem(19841, 1),
                new GameItem(26235, 1),

                new GameItem(26382),
                new GameItem(26382),
                new GameItem(26382),

                new GameItem(26384),
                new GameItem(26384),
                new GameItem(26384),

                new GameItem(26386),
                new GameItem(26386),
                new GameItem(26386),

                new GameItem(26233),
                new GameItem(26374)
        ));
    }

    public static ArrayList<GameItem> getAllDrops() {
        ArrayList<GameItem> drops = new ArrayList<>();
        itemz.forEach((lootRarity, gameItems) -> {
            gameItems.forEach(g -> {
                if (!drops.contains(g)) {
                    drops.add(g);
                }
            });
        });
        return drops;
    }

    public void getReward(Player player) {
            if (player.getInventory().freeInventorySlots() < 1) {
                player.getInventory().addToBank(new ImmutableItem(Loot_KEY));
                player.sendMessage("A loot key Has been added to your bank!");
            } else {
                player.getInventory().addOrDrop(new ImmutableItem(Loot_KEY));
            }
    }

    public List<GameItem> getRandomItems() {
        List<GameItem> rewards = Lists.newArrayList();
        int rareChance = 10;

        if (Misc.random(1, rareChance) == 1) {
            rewards.add(Misc.getRandomItem(items.get(LootRarity.RARE)));
        } else {
            for (int count = 0; count < 2; count++) {
                rewards.add(Misc.getRandomItem(items.get(LootRarity.COMMON)));
            }
        }
        return rewards;
    }

    public static Map<LootRarity, List<GameItem>> getItems() {
        return itemz;
    }

    public static ArrayList<GameItem> getRareDrops() {
        ArrayList<GameItem> drops = new ArrayList<>();
        List<GameItem> found = getItems().get(LootRarity.RARE);
        for(GameItem f : found) {
            boolean foundItem = false;
            for(GameItem drop : drops) {
                if (drop.getId() == f.getId()) {
                    foundItem = true;
                    break;
                }
            }
            if (!foundItem) {
                drops.add(f);
            }
        }
        return drops;
    }
}
