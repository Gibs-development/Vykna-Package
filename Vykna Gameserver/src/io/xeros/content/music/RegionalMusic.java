package io.xeros.content.music;

import io.xeros.model.entity.player.Player;

/**
 * Final decision for which songId to send to the client.
 *
 * Priority:
 *  1) forced track (Player.forcedMusicTrack)
 *  2) instance overrides (custom)
 *  3) regionId mapping (Data/music/region_id_to_song_id.json)
 *  4) wilderness fallback
 *  5) default
 */
public final class RegionalMusic {

    private static final int DEFAULT_SONG_ID = 358; // your proven working track

    private RegionalMusic() {}

    public static int resolve(Player c) {
        // 1) Forced
        if (c.forcedMusicTrack >= 0) return c.forcedMusicTrack;

        // Ensure indexes loaded
        MusicNameIndex.loadOnce();
        RegionMusicById.loadOnce();

        // 2) Instance overrides (optional, keep simple for now)
        // Example:
        // if (c.getInstance() instanceof io.xeros.content.instances.raids.RaidsInstance) return 999;

        // 3) RegionId mapping
        int regionId = getRegionId(c);
        Integer mapped = RegionMusicById.getSongIdForRegion(regionId);
        if (mapped != null) return mapped;

        // 4) Wilderness fallback (if you want different by wild level later)
        if (c.wildLevel > 0) {
            Integer wild2 = MusicNameIndex.findSongIdByName("wilderness2");
            if (wild2 != null) return wild2;
        }

        // 5) Default
        return DEFAULT_SONG_ID;
    }

    public static int getRegionId(Player c) {
        return ((c.absX >> 6) << 8) | (c.absY >> 6);
    }
}
