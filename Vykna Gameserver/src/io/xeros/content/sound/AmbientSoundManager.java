package io.xeros.content.sound;

import io.xeros.Server;
import io.xeros.model.SoundType;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;

import java.util.Map;

public final class AmbientSoundManager {
    private static final long TICK_MILLIS = 600L;
    private static final AmbientSoundManager INSTANCE = new AmbientSoundManager();

    public static AmbientSoundManager get() {
        return INSTANCE;
    }

    private AmbientSoundManager() {
    }

    public void pulse(Player player) {
        long now = System.currentTimeMillis();
        if (now < player.getNextAmbientSoundAt()) {
            return;
        }

        Map<Integer, AmbientSoundRegistry.AmbientSoundProfile> profiles = AmbientSoundRegistry.get().getObjectProfiles();
        if (profiles.isEmpty()) {
            player.setNextAmbientSoundAt(now + TICK_MILLIS);
            return;
        }

        GlobalObject closestObject = null;
        AmbientSoundRegistry.AmbientSoundProfile closestProfile = null;
        int closestDistance = Integer.MAX_VALUE;

        for (Map.Entry<Integer, AmbientSoundRegistry.AmbientSoundProfile> entry : profiles.entrySet()) {
            AmbientSoundRegistry.AmbientSoundProfile profile = entry.getValue();
            if (profile == null || profile.getRadius() <= 0) {
                continue;
            }
            GlobalObject object = Server.getGlobalObjects().findNearest(entry.getKey(), player, profile.getRadius());
            if (object == null) {
                continue;
            }
            int distance = player.distanceToPoint(object.getX(), object.getY());
            if (distance < closestDistance) {
                closestDistance = distance;
                closestObject = object;
                closestProfile = profile;
            }
        }

        if (closestObject != null && closestProfile != null && closestProfile.getSoundId() > 0) {
            player.getPA().sendAreaSound(closestProfile.getSoundId(), SoundType.AREA_SOUND, closestDistance);
            long intervalTicks = Math.max(1, closestProfile.getIntervalTicks());
            player.setNextAmbientSoundAt(now + (intervalTicks * TICK_MILLIS));
        } else {
            player.setNextAmbientSoundAt(now + TICK_MILLIS);
        }
    }
}
