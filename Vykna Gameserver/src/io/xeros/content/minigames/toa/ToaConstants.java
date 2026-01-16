package io.xeros.content.minigames.toa;

import com.google.common.collect.Lists;
import io.xeros.content.minigames.toa.rooms.*;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Position;

import java.util.Collections;
import java.util.List;

public class ToaConstants {

    public static final String TOMBS_OF_AMASCUT = "Tombs of Amascut";
    public static final Position LOBBY_TELEPORT_POSITION = new Position(3359, 9128);//in the tomb area
    public static final Position FINISHED_TOA_POSITION = new Position(3359, 9117);//In front of the entrance in the tomb

    /**
     * Lobby party overlay interface
     */
    public static final int LOBBY_WALKABLE_INTERFACE = 21_473;
    public static final int LOBBY_WALKABLE_INTERFACE_HEADER = 21_475;
    public static final int LOBBY_WALKABLE_NAME_CONTAINER = 21_476;
    public static final List<String> LOBBY_WALKABLE_EMPTY_NAME_LIST = Collections.unmodifiableList(Lists.newArrayList("-", "-", "-", "-", "-"));

    public static final int ENTER_TOA_OBJECT_ID = 46089;//enter the toa main area to enter all the boss rooms

    public static final int ENTER_NEXT_ROOM_OBJECT_ID = 33_113;
    public static final int ENTER_FINAL_ROOM_OBJECT_ID = 32_751;
    public static final int BOSS_GATE_OBJECT_ID = 32_755;

    public static final int TREASURE_ROOM_ENTRANCE_OBJECT_ID = 32_738;
    public static final int TREASURE_ROOM_EXIT_INSTANCE_OBJECT_ID = 32_996;

    public static final int FOOD_CHEST_OBJECT_ID = 29_069;

    public static final Boundary TOA_LOBBY = new Boundary(3333, 9099, 3383, 9133);//MAIN BOUNDARY FOR THE PARTY SYSTEM

  //  public static final Boundary MAIDEN_BOSS_ROOM_BOUNDARY = new Boundary(3138, 4411, 3238, 4471);
  //  public static final Boundary BLOAT_BOSS_ROOM_BOUNDARY = new Boundary(3265, 4428, 3327, 4465);
  //  public static final Boundary NYLOCAS_BOSS_ROOM_BOUNDARY = new Boundary(3276, 4231, 3315, 4285);
  //  public static final Boundary SOTETSEG_BOSS_ROOM_BOUNDARY = new Boundary(3264, 4291, 3295, 4336);
   // public static final Boundary XARPUS_BOSS_ROOM_BOUNDARY = new Boundary(3149, 4365, 3193, 4410);
   // public static final Boundary VERZIK_BOSS_ROOM_BOUNDARY = new Boundary(3147, 4291, 3192, 4336);
   // public static final Boundary LOOT_ROOM_BOUNDARY = new Boundary(3222, 4301, 3253, 4337);


    public static final Boundary BABA_BOSS_ROOM_BOUNDARY = new Boundary(3788, 5397, 3982, 5419);
    public static final Boundary CRONDIS_BOSS_ROOM_BOUNDARY = new Boundary(3914, 5259, 3957, 5301);
    public static final Boundary APMEKEN1_BOSS_ROOM_BOUNDARY = new Boundary(3788, 5266, 3828, 5294);
    public static final Boundary AKKHA_BOSS_ROOM_BOUNDARY = new Boundary(3675, 5397, 3706, 5422);
    public static final Boundary KEPHRI_BOSS_ROOM_BOUNDARY = new Boundary(3542, 5400, 3559, 5416);
    public static final Boundary TUMEKENS_WARDEN_BOSS_ROOM_BOUNDARY = new Boundary(3790, 5136, 3826, 5174);
    public static final Boundary LOOT_ROOM_BOUNDARY = new Boundary(3667, 5135, 3696, 5174);


    public static Boundary[] ALL_BOUNDARIES = { BABA_BOSS_ROOM_BOUNDARY, CRONDIS_BOSS_ROOM_BOUNDARY,
            APMEKEN1_BOSS_ROOM_BOUNDARY, AKKHA_BOSS_ROOM_BOUNDARY, KEPHRI_BOSS_ROOM_BOUNDARY,
            TUMEKENS_WARDEN_BOSS_ROOM_BOUNDARY, LOOT_ROOM_BOUNDARY };

   // public static Boundary[] ALL_BOUNDARIES = { MAIDEN_BOSS_ROOM_BOUNDARY, BLOAT_BOSS_ROOM_BOUNDARY,
   //         NYLOCAS_BOSS_ROOM_BOUNDARY, SOTETSEG_BOSS_ROOM_BOUNDARY, XARPUS_BOSS_ROOM_BOUNDARY,
    //        VERZIK_BOSS_ROOM_BOUNDARY, LOOT_ROOM_BOUNDARY };

    public static final List<ToaRoom> ROOM_LIST = Collections.unmodifiableList(Lists.newArrayList(
            new RoomOneKephri(), new RoomTwoBloat(), new RoomThreeNylocas(), new RoomFourSotetseg(), new RoomFiveXarpus(), new RoomSixVerzik(), new RoomSevenTreasure()));




    public static double getHealthModifier(final int size) {
        if (size == 5) {
            return 1;
        } else if (size == 4) {
            return 0.875;
        } else {
            return 0.50;//075
        }
    }
}
