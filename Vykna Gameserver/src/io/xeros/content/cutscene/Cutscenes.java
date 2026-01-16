package io.xeros.content.cutscene;

import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;

import java.util.List;

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
}
