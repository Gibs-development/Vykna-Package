package io.xeros.content.minigames.tob.bosses;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.tob.TobBoss;
import io.xeros.content.minigames.tob.instance.TobInstance;
import io.xeros.model.Animation;
import io.xeros.model.Direction;
import io.xeros.model.Graphic;
import io.xeros.model.Npcs;
import io.xeros.model.collisionmap.doors.Location;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.definitions.AnimationLength;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Position;
import io.xeros.util.Misc;
import lombok.Getter;
import lombok.Setter;

import static io.xeros.MultiwayCombatScript.getPossibleTargets;

public class PestilentBloat extends TobBoss {

    @Getter
    @Setter
    private boolean sleeping;

    private static final Boundary ARENA = new Boundary(3288, 4440, 3303, 4455);
    private static final int MIN_FLESH = 1570, MAX_FLESH = 1573;
    private boolean flesh;
    private static final int SLEEPING = 8082; //sleep animation

    private static final Graphic FLIES_GFX = new Graphic(1568);
    private static final Animation GO_TO_SLEEP_THEN_STOMP = new Animation(8082);
    private static final Animation DEATH = new Animation(8085);

    private static final Position MOVE_WEST_POSITION = new Position(3299, 4440, 0);
    private static final Position MOVE_SOUTH_POSITION = new Position(3299, 4451, 0);
    private static final Position MOVE_EAST_POSITION = new Position(3288, 4451, 0);
    private static final Position MOVE_NORTH_POSITION = new Position(3288, 4440, 0);

    private enum ActionState {
        WALKING,
        SLEEPING
        ;

        ActionState() {
        }

        public int getStateTimer() {
            if (this == WALKING) {
                return 30;
            } else if (this == SLEEPING) {
                return AnimationLength.getFrameLength(GO_TO_SLEEP_THEN_STOMP.getId());
            }

            throw new IllegalStateException("No length for " + this);
        }
    }

    private enum MovementState {
        SOUTH(MOVE_WEST_POSITION, Direction.SOUTH, MovementLos.SOUTH),
        WEST(MOVE_NORTH_POSITION, Direction.WEST, MovementLos.WEST),
        NORTH(MOVE_EAST_POSITION, Direction.NORTH, MovementLos.NORTH),
        EAST(MOVE_SOUTH_POSITION, Direction.EAST, MovementLos.EAST),
        ;

        private final Position end;
        private final Direction direction;
        private final MovementLos movementLos;

        MovementState(Position end, Direction direction, MovementLos movementLos) {
            this.end = end;
            this.direction = direction;
            this.movementLos = movementLos;
        }
    }

    private enum MovementLos {
        SOUTH(new Position(3299, 4440, 0), new Position(3303, 4455, 0)),
        WEST(new Position(3288, 4440, 0), new Position(3303, 4444, 0)),
        NORTH(new Position(3288, 4440, 0), new Position(3292, 4455, 0)),
        EAST(new Position(3288, 4451, 0), new Position(3303, 4455, 0)),
        ;

        private final List<Position> positions;

        MovementLos(Position start, Position end) {
            List<Position> positionList = Lists.newArrayList();
            for (int x = start.getX(); x <= end.getX(); x++) {
                for (int y = start.getY(); y <= end.getY(); y++) {
                    positionList.add(new Position(x, y));
                }
            }
            positions = Collections.unmodifiableList(positionList);
        }
    }

    private ActionState actionState = ActionState.WALKING;
    private MovementState movementState = MovementState.SOUTH;
    private int stateTimer = 0;

    public PestilentBloat(InstancedArea instancedArea) {
        super(Npcs.PESTILENT_BLOAT, new Position(3299, 4451, instancedArea.getHeight()), instancedArea);
    }

    @Override
    public void facePosition(int x, int y) {}

    @Override
    public void facePlayer(int player) {}

    @Override
    public void faceNPC(int index) { }

    @Override
    public int getDeathAnimation() {
        return DEATH.getId();
    }

    @Override
    public boolean canBeDamaged(Entity entity) {
        return actionState == ActionState.SLEEPING;
    }

    @Override
    public void process() {
        if (getHealth().getCurrentHealth() == 0) {      // Process death
            super.process();
            return;
        }

        //fixFace();

        if (actionState == ActionState.WALKING) {
            handleMovement();
            processMovement();
            damagePlayersInLos();
            tickNextState();
            this.startAnimation(Animation.RESET_ANIMATION);
        } else if (actionState == ActionState.SLEEPING) {
            this.startAnimation(SLEEPING);
            tickNextState();
            if (stateTimer == actionState.getStateTimer() - 1) {
                stompPlayers();
                fleshFall();

            }
        }
    }


    private void tickNextState() {
        if (stateTimer++ >= actionState.getStateTimer()) {
            if (actionState == ActionState.WALKING) {
                actionState = ActionState.SLEEPING;
                setSleeping(true);
                startAnimation(GO_TO_SLEEP_THEN_STOMP);
            } else if (actionState == ActionState.SLEEPING) {
                actionState = ActionState.WALKING;
                startAnimation(Animation.RESET_ANIMATION);
                setSleeping(false);
            }

            stateTimer = 0;
        }
    }

    private void handleMovement() {
        if (getPosition().equals(movementState.end.withHeight(getInstance().getHeight()))) {
            setNextMovement();
        } else {
            Position nextPositionDelta = movementState.direction.getDeltaPosition();
            Position nextPositionAbsolute = new Position(getX() + nextPositionDelta.getX(), getY() + nextPositionDelta.getY());
            moveTowards(nextPositionAbsolute.getX(), nextPositionAbsolute.getY());
        }
    }

    private void setNextMovement() {
        movementState = MovementState.values()[(movementState.ordinal() + 1) % MovementState.values().length];
    }

    private void damagePlayersInLos() {
        List<Position> positions = movementState.movementLos.positions.stream().map(it -> it.withHeight(getInstance().getHeight())).collect(Collectors.toList());
        getInstance().getPlayers().forEach(plr -> {
            positions.stream().filter(pos -> plr.getPosition().equals(pos)).findFirst().ifPresent(pos -> {
                if (plr.getAttributes().containsBoolean(TobInstance.TOB_DEAD_ATTR_KEY))
                    return;
                plr.startGraphic(FLIES_GFX);
                plr.appendDamage(9 + Misc.random(11), Hitmark.HIT);
            });
        });
    }

    private void stompPlayers() {
        getInstance().getPlayers().forEach(plr -> {
            for (Position position : getTiles()) {
                if (plr.distance(position) <= 1.5) {
                    if (plr.getAttributes().containsBoolean(TobInstance.TOB_DEAD_ATTR_KEY))
                        return;
                    plr.appendDamage(30 + Misc.random(20), Hitmark.HIT);
                    return;
                }
            }
        });
    }


    /**
     * Handles falling flesh
     * Author C.T For runerogue
     * Handles the falling flesh phase
     */
    private List<Entity> targets;
    private void fleshFall() {
        flesh = true;
        NPC npc = NPCHandler.getNpc(8359);
        CycleEvent event = new CycleEvent() {

            int cycle = 0;
            final List<Location> locs = ARENA.getRandomLocations(Misc.random(10, 20), npc.getHeight());

            @Override
            public void execute(CycleEventContainer container) {
                NPC npc = NPCHandler.getNpc(8359);
                if(npc.isDead) {
                    container.stop();
                    return;
                }
                if (cycle == 0) {
                    for (Location loc : locs) {
                        getInstance().getPlayers().forEach(plr -> {
                                plr.asPlayer().getPA().createPlayersStillGfx(Misc.random(MIN_FLESH, MAX_FLESH), loc.getX(), loc.getY(), 0, 0);
                            });
                        };
                } else if (cycle == 4) {
                    getInstance().getPlayers().forEach(plr -> {
                        for (Location loc : locs) {
                            if (plr.getLocation().equalsIgnoreHeight(loc)) {
                                plr.appendDamage(Misc.random(20, 30), Hitmark.HIT);
                            }
                        };
                    });
                } else if (cycle >= 8) {
                    flesh = false;
                    container.stop();
                }
                cycle++;
            }

        };
        CycleEventHandler.getSingleton().addEvent(-1, npc, event, 1);
    }

}
