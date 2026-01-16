package io.xeros.content.bosses.kratos;

import com.google.common.collect.Lists;
import io.xeros.model.CombatType;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.HealthStatus;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;



public class KratosNpc extends NPC {




    public static final Position SPAWN_POSITION = new Position(3027, 5233, 0);

    public static boolean spawned = false;

    private boolean minionSpawn;
    public int attackCounter;
    public Position nextWalk;

    public KratosNpc(int id, Position pos) {
        super(id, pos);
        resetForSpawn();
        spawned = true;
    }

    public void resetForSpawn() {
        walkingType = 1;
        nextWalk = null;
        attackCounter = 0;
        getBehaviour().setWalkHome(false);
        getBehaviour().setAggressive(true);
        if (getNpcId() == 6009) {//Kratos
            getBehaviour().setRespawn(true);
        } else {
            getBehaviour().setRespawn(false);
        }
        if (getNpcId() == 6006) {//Npcs.SPAWN_OF_SARACHNIS
            setNpcAutoAttacks(Lists.newArrayList(
                    new KratosMinionMelee().apply(this)
            ));
        } else if (getNpcId() == 6005) {//Npcs.SPAWN_OF_SARACHNIS_2
            setNpcAutoAttacks(Lists.newArrayList(
                    new KratosMinionMage().apply(this)
            ));

        } else {
            setNpcAutoAttacks(Lists.newArrayList(
                    new KratosMelee().apply(this),
                    new KratosRanged().apply(this)
            ));
        }
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
        return !isDead();
    }

    @Override
    public boolean canBeAttacked(Entity entity) {
        return !isDead();
    }

    @Override
    public boolean isAutoRetaliate() {
        return getBehaviour().isAggressive() && !isDead() && nextWalk == null;
    }

    @Override
    public void process() {
        try {
            if (this.getNpcId() == 6009) {
                if (attackCounter >= 5) {
                    attackCounter = 0;
                    int p = getPlayerAttackingIndex();
                    if (PlayerHandler.players[p] != null) {
                        Player player = PlayerHandler.players[p];
                        this.attack(player, new KratosWeb().apply(this));
                    }
                }
                if (this.getHealth().getCurrentHealth() <= (this.getHealth().getMaximumHealth() * 0.66) && !minionSpawn ||
                        this.getHealth().getCurrentHealth() <= (this.getHealth().getMaximumHealth() * 0.33) && !minionSpawn) {
                    minionSpawn = true;
                    spawnMinions();
                }

                if (this.getNpcId() == 6008 && this.getHealth().getCurrentHealth() <= 450) {
                   // int p = getPlayerAttackingIndex();
                    this.setAttackType(CombatType.SPECIAL);
                    this.hitDelayTimer = 3;
                }


                if (nextWalk != null) {
                    if (getPosition().equals(nextWalk)) {
                        nextWalk = null;
                        getBehaviour().setRunnable(false);
                        getBehaviour().setAggressive(true);
                    } else {
                        getBehaviour().setRunnable(true);
                        moveTowards(nextWalk.getX(),nextWalk.getY(),false);
                    }
                }
            }
            processCombat();
        } catch (Exception e) {
            e.printStackTrace();
            unregister();
        }
    }

    private void processCombat() {
        super.process();
    }

    @Override
    public NPC provideRespawnInstance() {
        NPC boss = new KratosNpc(6009, SPAWN_POSITION);
        boss.getBehaviour().setAggressive(true);
        return boss;
    }

    @Override
    public void onDeath() {
        super.onDeath();
        spawned = false;
      //  if (!this.getName().equalsIgnoreCase("Kratos")) {
       if( this.getNpcId() == 6009 && this.getNpcId() == 6006 && this.getNpcId() == 6005){
            if (minionSpawn && minionsDead()) {
                minionSpawn = false;
            }
        } else {
            resetForSpawn();
            minionSpawn = false;
            despawnMinions();
        }
    }

    private void spawnMinions() {
        if (minionSpawn) {
            new KratosNpc(6006, SPAWN_POSITION);//Npcs.SPAWN_OF_SARACHNIS
            new KratosNpc(6005, SPAWN_POSITION);//Npcs.SPAWN_OF_SARACHNIS_2
            new KratosNpc(6005, SPAWN_POSITION);//Npcs.SPAWN_OF_SARACHNIS_2
        }
    }

    private void despawnMinions() {
        NPCHandler.despawn(6006, getHeight());//Npcs.SPAWN_OF_SARACHNIS
        NPCHandler.despawn(6005, getHeight());//Npcs.SPAWN_OF_SARACHNIS_2
    }

    private boolean minionsDead() {
        return NPCHandler.getNpc(6006, getHeight()) == null || NPCHandler.getNpc(6005, getHeight()) == null;
    }


    private void enrangedSpecial(NPC npc) {
        NPCHandler.handler.groundSpell(npc, null, 315, 317, "corp", 4);
    }

}
