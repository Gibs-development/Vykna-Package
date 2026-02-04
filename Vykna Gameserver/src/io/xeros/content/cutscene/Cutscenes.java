package io.xeros.content.cutscene;

import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.util.Misc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class Cutscenes {
    private Cutscenes() {}

    public static void startIntro(Player p) {
        CycleEventHandler.getSingleton().stopEvents(p, CutsceneRunner.EVENT_ID);

        // block player packets, but we will still drive movement server-side
        p.lock(new CutsceneLock());

        // Make sure they WALK for the scene
        p.updateRunningToggled(false);

        // ---- Lumbridge anchors (ABS world tiles) ----
        // Start area (you showed 3223,3218)
        final int startX = 3223;
        final int startY = 3218;

        // Waypoints: staircase -> inside -> kitchen -> range (tweak if needed)
        final int wpStairsTopX = 3221, wpStairsTopY = 3224;   // top of entrance stairs / by door
        final int wpHallX      = 3216, wpHallY      = 3223;   // inside hall-ish
        final int wpKitchenX   = 3209, wpKitchenY   = 3216;   // kitchen area
        final int wpRangeX     = 3208, wpRangeY     = 3212;   // near range

        // Camera shots (ABS)
        // Shot A: outside looking at stairs/door
        final int camA_X = 3232, camA_Y = 3230;
        final int lookA_X = 3221, lookA_Y = 3223;

        // Shot B: hall drifting toward kitchen direction
        final int camB_X = 3224, camB_Y = 3234;
        final int lookB_X = 3216, lookB_Y = 3223;

        // Shot C: kitchen close
        final int camC_X = 3216, camC_Y = 3221;
        final int lookC_X = 3208, lookC_Y = 3212;

        final int camH = 820;
        final int lookH = 70;

        List<CutsceneStep> steps = List.of(
                // 0) HARD FADE FIRST so nothing snaps on screen
                new FadeStep("Lumbridge", 1, 1, 0),
                new WaitStep(3), // wait until we're dark

                // 1) Snap camera while dark (locks input immediately)
                new CameraStep(
                        camA_X, camA_Y, camH, 10, 120,
                        lookA_X, lookA_Y, lookH, 10, 40,
                        0
                ),
                new WaitStep(2), // let fade come back up cleanly

                // 2) Walk up the main entrance staircase
                new WalkToAbsStep(wpStairsTopX, wpStairsTopY, 40),

                // 3) Fade to hide camera cut
                new FadeStep("", 1, 0, 0),
                new WaitStep(3),

                // 4) Snap to interior-ish hall shot
                new CameraStep(
                        camB_X, camB_Y, camH, 10, 120,
                        lookB_X, lookB_Y, lookH, 10, 120,
                        0
                ),
                new WaitStep(2),

                // 5) Walk toward the kitchen area
                new WalkToAbsStep(wpHallX, wpHallY, 200),
                new WalkToAbsStep(wpKitchenX, wpKitchenY, 40),

                // 6) Fade to hide final camera cut
                new FadeStep("", 1, 1, 0),
                new WaitStep(3),

                // 7) Snap kitchen camera
                new CameraStep(
                        camC_X, camC_Y, 560, 10, 120,
                        lookC_X, lookC_Y, lookH, 10, 120,
                        0
                ),
                new WaitStep(2),

                // 8) Walk to the range
                new WalkToAbsStep(wpRangeX, wpRangeY, 40),

                // 9) Quick end fade and finish (runner should reset camera + unlock)
                new FadeStep("", 1, 1, 0),
                new WaitStep(2)
        );

        CycleEventHandler.getSingleton().addEvent(CutsceneRunner.EVENT_ID, p, new CutsceneRunner(p, steps), 1);
    }

    public static void startTutorialTour(Player p, java.util.function.Consumer<Player> onComplete) {
        CycleEventHandler.getSingleton().stopEvents(p, CutsceneRunner.EVENT_ID);

        p.lock(new CutsceneLock());
        p.updateRunningToggled(false);
        p.getPA().closeAllWindows();

        final Position[] startPos = new Position[1];
        final int area1X = 3090, area1Y = 3492;
        final int area2X = 3108, area2Y = 3495;
        final int area3X = 3095, area3Y = 3504;
        final int area4X = 3080, area4Y = 3510;
        final int area5X = 3080, area5Y = 3471;

        final int tourWaitTicks = Misc.toCycles(5, TimeUnit.SECONDS);
        final NPC[] guide = new NPC[1];

        List<CutsceneStep> steps = new ArrayList<>();
        steps.add(new ActionStep(player -> startPos[0] = player.getPosition().deepCopy()));
    //    steps.add(new FadeStep("Arwyn", 1, 1, 0));
        steps.add(new WaitStep(2));
        steps.add(new ActionStep(player -> {
            int height = player.getHeight();
            guide[0] = NPCSpawning.spawnNpc(player, 3248, area1X + 1, area1Y, height, 0, 0, false, false);
            player.moveTo(new Position(area1X, area1Y, height));
            if (guide[0] != null) {
                guide[0].teleport(area1X + 1, area1Y, height);
                guide[0].facePlayer(player.getIndex());
            }
        }));
    //    steps.add(new FadeStep("", -1, 1, 0));
        steps.add(new WaitStep(1));
        steps.add(new ActionStep(player -> {
            if (guide[0] != null) {
                guide[0].facePlayer(player.getIndex());
                guide[0].forceChat("Welcome to Arwyn!");
            }
        }));
        steps.add(new WaitStep(tourWaitTicks));

     //   steps.add(new FadeStep("", 1, 1, 0));
        steps.add(new WaitStep(2));
        steps.add(new ActionStep(player -> {
            int height = player.getHeight();
            player.moveTo(new Position(area2X, area2Y, height));
            if (guide[0] != null) {
                guide[0].teleport(area2X + 1, area2Y, height);
                guide[0].facePlayer(player.getIndex());
            }
        }));
    //    steps.add(new FadeStep("", -1, 1, 0));
        steps.add(new WaitStep(1));
        steps.add(new ActionStep(player -> {
            if (guide[0] != null) {
                guide[0].facePlayer(player.getIndex());
                guide[0].forceChat("This is the starter market for gear and supplies.");
            }
        }));
        steps.add(new WaitStep(tourWaitTicks));

    //    steps.add(new FadeStep("", 1, 1, 0));
        steps.add(new WaitStep(2));
        steps.add(new ActionStep(player -> {
            int height = player.getHeight();
            player.moveTo(new Position(area3X, area3Y, height));
            if (guide[0] != null) {
                guide[0].teleport(area3X + 1, area3Y, height);
                guide[0].facePlayer(player.getIndex());
            }
        }));
  //      steps.add(new FadeStep("", -1, 1, 0));
        steps.add(new WaitStep(1));
        steps.add(new ActionStep(player -> {
            if (guide[0] != null) {
                guide[0].facePlayer(player.getIndex());
                guide[0].forceChat("This is the rewards hub for daily bonuses and voting.");
            }
        }));
        steps.add(new WaitStep(tourWaitTicks));

   //     steps.add(new FadeStep("", 1, 1, 0));
        steps.add(new WaitStep(2));
        steps.add(new ActionStep(player -> {
            int height = player.getHeight();
            player.moveTo(new Position(area4X, area4Y, height));
            if (guide[0] != null) {
                guide[0].teleport(area4X + 1, area4Y, height);
                guide[0].facePlayer(player.getIndex());
            }
        }));
 //       steps.add(new FadeStep("", -1, 1, 0));
        steps.add(new WaitStep(1));
        steps.add(new ActionStep(player -> {
            if (guide[0] != null) {
                guide[0].facePlayer(player.getIndex());
                guide[0].forceChat("This is the event portal for activities and minigames.");
            }
        }));
        steps.add(new WaitStep(tourWaitTicks));

  //      steps.add(new FadeStep("", 1, 1, 0));
        steps.add(new WaitStep(2));
        steps.add(new ActionStep(player -> {
            int height = player.getHeight();
            player.moveTo(new Position(area5X, area5Y, height));
            if (guide[0] != null) {
                guide[0].teleport(area5X + 1, area5Y, height);
                guide[0].facePlayer(player.getIndex());
            }
        }));
     //   steps.add(new FadeStep("", -1, 1, 0));
        steps.add(new WaitStep(1));
        steps.add(new ActionStep(player -> {
            if (guide[0] != null) {
                guide[0].facePlayer(player.getIndex());
                guide[0].forceChat("This is the world boss patch and farming area.");
            }
        }));
        steps.add(new WaitStep(tourWaitTicks));

   //     steps.add(new FadeStep("", 1, 1, 0));
        steps.add(new WaitStep(2));
        steps.add(new ActionStep(player -> {
            Position home = startPos[0] == null ? player.getPosition() : startPos[0];
            player.moveTo(home);
            if (guide[0] != null) {
                guide[0].teleport(home.getX() + 1, home.getY(), home.getHeight());
                guide[0].facePlayer(player.getIndex());
            }
        }));
    //    steps.add(new FadeStep("", -1, 1, 0));
        steps.add(new WaitStep(1));
        steps.add(new ActionStep(player -> {
            if (guide[0] != null) {
                guide[0].unregister();
            }
        }));

        CycleEventHandler.getSingleton().addEvent(CutsceneRunner.EVENT_ID, p, new CutsceneRunner(p, steps, onComplete), 1);
    }
}
