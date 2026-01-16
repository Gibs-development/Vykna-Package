package io.xeros.content.cutscene;

import io.xeros.model.entity.player.Player;

public final class CameraStep implements CutsceneStep {
    private boolean sent;

    private final int camX, camY, camH, camSpeed, camAccel;
    private final int lookX, lookY, lookH, lookSpeed, lookAccel;
    private int waitTicks;

    public CameraStep(
            int camX, int camY, int camH, int camSpeed, int camAccel,
            int lookX, int lookY, int lookH, int lookSpeed, int lookAccel,
            int waitTicks
    ) {
        this.camX = camX;
        this.camY = camY;
        this.camH = camH;
        this.camSpeed = camSpeed;
        this.camAccel = camAccel;

        this.lookX = lookX;
        this.lookY = lookY;
        this.lookH = lookH;
        this.lookSpeed = lookSpeed;
        this.lookAccel = lookAccel;

        this.waitTicks = Math.max(0, waitTicks);
    }

    @Override
    public boolean tick(Player p) {
        if (!sent) {
            CutscenePackets.moveCameraAbs(p, camX, camY, camH, camSpeed, camAccel);
            CutscenePackets.lookAtAbs(p, lookX, lookY, lookH, lookSpeed, lookAccel);

            sent = true;
        }
        return waitTicks-- <= 0;
    }
}
