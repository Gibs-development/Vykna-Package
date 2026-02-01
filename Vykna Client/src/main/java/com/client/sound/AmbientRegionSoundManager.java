package com.client.sound;

import com.client.Entity;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class AmbientRegionSoundManager {
    public enum RegionType {
        CAVE,
        CITY,
        VILLAGE,
        NATURE
    }

    private static final class RegionSoundSet {
        private final int[] sounds;
        private final int minDelay;
        private final int maxDelay;

        private RegionSoundSet(int[] sounds, int minDelay, int maxDelay) {
            this.sounds = sounds;
            this.minDelay = minDelay;
            this.maxDelay = maxDelay;
        }
    }

    private static final Map<Integer, RegionType> REGION_TYPES = new HashMap<>();
    private static final Map<RegionType, RegionSoundSet> SOUND_SETS = new EnumMap<>(RegionType.class);
    private static int nextPlayTick;
    private static int lastRegionId = -1;

    private AmbientRegionSoundManager() {}

    public static void initDefaults() {
        // Define the sound pools + delays here (ticks).
        // Adjust sound ids and delays to taste.
        setSoundSet(RegionType.CAVE, new int[] { 1700, 1701, 1702 }, 150, 300);
        setSoundSet(RegionType.CITY, new int[] { 1710, 1711, 1712 }, 120, 240);
        setSoundSet(RegionType.VILLAGE, new int[] { 1720, 1721 }, 180, 360);
        setSoundSet(RegionType.NATURE, new int[] { 1730, 1731, 1732, 1733 }, 200, 420);
        // Register regions after you know their ids.
        // Example: registerRegion((48 << 8) | 54, RegionType.CITY);
    }

    public static void registerRegion(int regionId, RegionType type) {
        REGION_TYPES.put(regionId, type);
    }

    public static void setSoundSet(RegionType type, int[] sounds, int minDelay, int maxDelay) {
        if (sounds == null || sounds.length == 0) {
            SOUND_SETS.remove(type);
            return;
        }
        SOUND_SETS.put(type, new RegionSoundSet(sounds, minDelay, maxDelay));
    }

    public static void tick(int loopCycle, Entity player) {
        if (player == null) {
            return;
        }
        int regionId = getRegionId(player);
        if (regionId != lastRegionId) {
            lastRegionId = regionId;
            nextPlayTick = loopCycle + 50;
        }
        if (loopCycle < nextPlayTick) {
            return;
        }
        RegionType type = REGION_TYPES.get(regionId);
        if (type == null) {
            nextPlayTick = loopCycle + 100;
            return;
        }
        RegionSoundSet set = SOUND_SETS.get(type);
        if (set == null || set.sounds.length == 0) {
            nextPlayTick = loopCycle + 100;
            return;
        }
        int soundId = set.sounds[ThreadLocalRandom.current().nextInt(set.sounds.length)];
        Sound.getSound().playSound(soundId, SoundType.AREA_SOUND, 0);
        int delay = randomDelay(set.minDelay, set.maxDelay);
        nextPlayTick = loopCycle + delay;
    }

    private static int randomDelay(int minDelay, int maxDelay) {
        if (maxDelay < minDelay) {
            int swap = minDelay;
            minDelay = maxDelay;
            maxDelay = swap;
        }
        if (maxDelay <= 0) {
            return 100;
        }
        if (minDelay <= 0) {
            minDelay = 20;
        }
        if (maxDelay == minDelay) {
            return minDelay;
        }
        return minDelay + ThreadLocalRandom.current().nextInt(maxDelay - minDelay + 1);
    }

    private static int getRegionId(Entity player) {
        int tileX = player.x >> 7;
        int tileY = player.y >> 7;
        int regionX = tileX >> 6;
        int regionY = tileY >> 6;
        return (regionX << 8) | regionY;
    }
}
