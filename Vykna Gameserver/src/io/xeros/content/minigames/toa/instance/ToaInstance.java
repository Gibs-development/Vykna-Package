package io.xeros.content.minigames.toa.instance;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.instances.InstanceConfiguration;
import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.toa.ToaConstants;
import io.xeros.content.minigames.toa.ToaRoom;
import io.xeros.content.minigames.toa.rooms.RoomSevenTreasure;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.items.GameItem;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class ToaInstance extends InstancedArea {

    public static final String TOA_DEAD_ATTR_KEY = "dead_toa";
    private static final int TREASURE_ROOM_INDEX = 6;

    private final HashSet<String> chestRewards = new HashSet<>();
    private final FoodRewards foodRewards = new FoodRewards();
    private final MvpPoints mvpPoints = new MvpPoints();
    private final HashMap<String, List<GameItem>> chestRewardItems = new HashMap<>();
    private int roomIndex = -1;
    private boolean fightStarted = false;
    private boolean lastRoom;
    private final int size;

    private boolean finalBossComplete;

    public ToaInstance(int size) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY, ToaConstants.ALL_BOUNDARIES);
        this.size = size;
    }

    @Override
    public void remove(Player player) {
        if (roomIndex == TREASURE_ROOM_INDEX) {
            RoomSevenTreasure.openChest(player, (ToaInstance) player.getInstance());
        }

        super.remove(player);
        player.getBossTimers().remove(ToaConstants.TOMBS_OF_AMASCUT);
    }

    public void removeButLeaveInParty(Player player) {
        super.remove(player);
    }

    public void start(List<Player> playerList) {
        if (playerList.isEmpty())
            return;
        initialiseNextRoom(playerList.get(0));
        ToaRoom toaRoom = ToaConstants.ROOM_LIST.get(0);
        playerList.forEach(plr -> {
            if (plr.getPA().calculateTotalLevel() < plr.getMode().getTotalLevelForToa()) {
                plr.sendStatement("You need " + Misc.insertCommas(plr.getMode().getTotalLevelForToa()) + " total level to compete.");
                return;
            }

            add(plr);
            //plr.moveTo(resolve(toaRoom.getPlayerSpawnPosition()));
            plr.moveTo(new Position(3551, 5162, 0));//move to the lobby area
            plr.sendMessage("Welcome to the Tombs of Amascut.");
            plr.getBossTimers().track(ToaConstants.TOMBS_OF_AMASCUT);
            plr.getPA().closeAllWindows();
        });
    }

    private void initialiseNextRoom(Player player) {
        roomIndex = getPlayerRoomIndex(player) + 1;
        ToaRoom toaRoom = ToaConstants.ROOM_LIST.get(roomIndex);
        var boss = toaRoom.spawn(this);
        if (boss != null) {
            var modifier = ToaConstants.getHealthModifier(size);
            var maxHealth = (int) (boss.getHealth().getMaximumHealth() * modifier);
            boss.getHealth().setCurrentHealth(maxHealth);
            boss.getHealth().setMaximumHealth(maxHealth);
        }
        GlobalObject foodChest = toaRoom.getFoodChestPosition();
        if (foodChest != null) {
            Server.getGlobalObjects().add(foodChest.withHeight(resolveHeight(foodChest.getHeight())).setInstance(this));
        }
        fightStarted = false;
    }

    public void moveToNextRoom(Player player) {
        if (getCurrentRoom().isRoomComplete(this) || getPlayerRoomIndex(player) < roomIndex) {
            int nextRoomIndex = getPlayerRoomIndex(player) + 1;
            if (roomIndex < nextRoomIndex) {
                initialiseNextRoom(player);
            }

            player.healEverything();
            Position playerSpawnPosition = resolve(ToaConstants.ROOM_LIST.get(nextRoomIndex).getPlayerSpawnPosition());
            player.moveTo(playerSpawnPosition);
            player.getAttributes().removeBoolean(TOA_DEAD_ATTR_KEY);
        } else {
            player.sendMessage("You haven't completed this room yet!");
        }
    }

    private int getPlayerRoomIndex(Player player) {
        for (int index = 0; index < ToaConstants.ALL_BOUNDARIES.length; index++) {
            if (ToaConstants.ALL_BOUNDARIES[index].in(player)) {
                return index;
            }
        }

        return -1;
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject object, int option) {
        if (object.getId() == ToaConstants.BOSS_GATE_OBJECT_ID || object.getId() == ToaConstants.ENTER_FINAL_ROOM_OBJECT_ID) {
            if (getPlayerRoomIndex(player) == roomIndex && !getCurrentRoom().isRoomComplete(this)
                || lastRoom && object.getId() == ToaConstants.ENTER_FINAL_ROOM_OBJECT_ID) {// In last unlocked room and fight not completed
                if (!fightStarted || lastRoom) {
                    if (player.equals(player.getInstance().getPlayers().get(0))) {
                        player.start(new DialogueBuilder(player).option(new DialogueOption("Start fight", this::startFight),
                                new DialogueOption("Cancel", plr -> plr.getPA().closeAllWindows())));
                    } else {
                        player.sendMessage("Only the party leader can start a fight.");
                    }
                } else {
                    if (player.getAttributes().getBoolean(TOA_DEAD_ATTR_KEY)) {
                        player.sendMessage("You've been disqualified from the fight for dying, you must wait.");
                    } else {
                        player.sendMessage("The fight has started, there's no turning back now.");
                    }
                }
            } else {                                                                                                          // In room before last unlocked or room complete
                Optional<ToaRoom> gateRoomOptional = ToaConstants.ROOM_LIST.stream().filter(gateRoom -> gateRoom.getBoundary().in(player)).findFirst();
                gateRoomOptional.ifPresent(toaRoom -> gateRoomOptional.get().handleClickBossGate(player, object));
            }

            return true;
        }

        for (ToaRoom room : ToaConstants.ROOM_LIST) {
            if (room.handleClickObject(player, object, option)) {
                return true;
            }
        }

        switch (object.getId()) {
            case ToaConstants.TREASURE_ROOM_ENTRANCE_OBJECT_ID:
            case ToaConstants.ENTER_NEXT_ROOM_OBJECT_ID:
            case ToaConstants.ENTER_FINAL_ROOM_OBJECT_ID:
                moveToNextRoom(player);
                return true;
            case ToaConstants.FOOD_CHEST_OBJECT_ID:
                foodRewards.openFoodRewards(player);
                return true;
        }

        return false;
    }

    @Override
    public boolean handleDeath(Player player) {
        int roomIndex = getPlayerRoomIndex(player);
        if (roomIndex == -1) {
            player.moveTo(ToaConstants.FINISHED_TOA_POSITION);
            player.sendMessage("Could not handle death!");
            return true;
        }

        ToaRoom room = ToaConstants.ROOM_LIST.get(getPlayerRoomIndex(player));
        player.moveTo(resolve(room.getDeathPosition()));
        player.sendMessage("Oh dear, you have died!");
        player.getAttributes().setBoolean(TOA_DEAD_ATTR_KEY, true);
        Server.getLogging().write(new DiedAtToaLog(player, this));

        if (allDead()) {
            Lists.newArrayList(getPlayers()).forEach(plr -> {
                plr.moveTo(ToaConstants.FINISHED_TOA_POSITION);
                removeButLeaveInParty(plr);
                plr.sendMessage("Your performance in the tomb left much to be desired; your team has been defeated.");
            });
        }
        return true;
    }

    private void startFight(Player player) {
        if (getPlayers().stream().allMatch(plr -> getPlayerRoomIndex(plr) == roomIndex)) {
            fightStarted = true;
            ToaRoom currentRoom = getCurrentRoom();
            getPlayers().forEach(plr -> {
                if (lastRoom)
                    moveToNextRoom(plr);
                else
                    plr.moveTo(resolve(currentRoom.getFightStartPosition()));
                plr.getPA().closeAllWindows();
            });
            lastRoom = false;
        } else {
            player.sendStatement("All players must be in the room to start the fight.");
        }
    }

    private boolean allDead() {
        return getPlayers().stream().allMatch(plr -> plr.getAttributes().getBoolean(TOA_DEAD_ATTR_KEY));
    }

    @Override
    public void onDispose() { }

    private ToaRoom getCurrentRoom() {
        return ToaConstants.ROOM_LIST.get(roomIndex);
    }

    public HashSet<String> getChestRewards() {
        return chestRewards;
    }

    public FoodRewards getFoodRewards() {
        return foodRewards;
    }

    public MvpPoints getMvpPoints() {
        return mvpPoints;
    }

    public MvpPoints getChooseRareWinner() {
        return mvpPoints;
    }

    public HashMap<String, List<GameItem>> getChestRewardItems() {
        return chestRewardItems;
    }

    public int getPartySize() {
        return size;
    }

    public void setLastRoom(boolean lastRoom) {
        this.lastRoom = lastRoom;
    }

    public boolean isFinalBossComplete() {
        return finalBossComplete;
    }

    public void setFinalBossComplete(boolean finalBossComplete) {
        this.finalBossComplete = finalBossComplete;
    }

}
