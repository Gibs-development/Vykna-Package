package io.xeros.content.minigames.toa.rooms;

import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.toa.ToaConstants;
import io.xeros.content.minigames.toa.ToaRoom;
import io.xeros.content.minigames.toa.bosses.MaidenOfSugadinti;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.world.objects.GlobalObject;

public class RoomOneKephri extends ToaRoom {

    @Override
    public MaidenOfSugadinti spawn(InstancedArea instancedArea) {
        return new MaidenOfSugadinti(instancedArea);
    }

    @Override
    public Position getPlayerSpawnPosition() {
        return new Position(3551, 5162);//Moves party into the main area to start the boss fights
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject worldObject, int option) {
        return false;
    }

    @Override
    public void handleClickBossGate(Player player, WorldObject worldObject) {
        if (player.getX() >= 3186) {
            player.getPA().movePlayer(3184, player.getY(), player.getHeight());
        } else if (player.getX() <= 3184) {
            player.getPA().movePlayer(3186, player.getY(), player.getHeight());
        }
    }

    @Override
    public boolean isRoomComplete(InstancedArea instancedArea) {
        return instancedArea.getNpcs().isEmpty();
    }

    @Override
    public Boundary getBoundary() {
        return ToaConstants.BABA_BOSS_ROOM_BOUNDARY;
    }

    @Override
    public Position getDeathPosition() {
        return new Position(3186, 4446);
    }

    @Override
    public Position getFightStartPosition() {
        return new Position(3184, 4446, 0);
    }

    @Override
    public GlobalObject getFoodChestPosition() {
        return getFoodChest(new Position(3175, 4422, 0), 0);
    }
}
