package io.xeros.content.music;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class RegionMusicById {

    private static final Map<Integer, Integer> REGION_TO_SONG = new HashMap<>();
    private static boolean loaded;

    private RegionMusicById() {}

    public static void loadOnce() {
        if (loaded) return;
        loaded = true;

        try {
            Path p = Path.of(System.getProperty("user.dir"), "Data", "music", "region_id_to_song_id.json");
            File f = p.toFile();

            if (!f.exists()) {
                System.out.println("[Music] region map not found: " + f.getAbsolutePath());
                return;
            }

            try (FileReader r = new FileReader(f)) {
                // Old Gson-compatible parsing:
                JsonElement rootEl = new JsonParser().parse(r);
                if (!rootEl.isJsonObject()) {
                    System.out.println("[Music] region map invalid JSON root (not object): " + f.getAbsolutePath());
                    return;
                }

                JsonObject root = rootEl.getAsJsonObject();

                // Your file has: { "meta": {...}, "regionId_to_songId": { "12345": 76, ... } }
                JsonObject mapObj = root.has("regionId_to_songId")
                        ? root.getAsJsonObject("regionId_to_songId")
                        : root;

                int count = 0;
                for (Map.Entry<String, com.google.gson.JsonElement> e : mapObj.entrySet()) {
                    if (e.getValue().isJsonObject()) continue; // skip meta if flat
                    int regionId = Integer.parseInt(e.getKey());
                    int songId = e.getValue().getAsInt();
                    REGION_TO_SONG.put(regionId, songId);
                    count++;
                }

                System.out.println("[Music] Loaded region music map: " + count + " entries from " + f.getAbsolutePath());
            }
        } catch (Exception ex) {
            System.out.println("[Music] Failed to load region music map. user.dir=" + System.getProperty("user.dir"));
            ex.printStackTrace();
        }
    }

    public static Integer getSongIdForRegion(int regionId) {
        return REGION_TO_SONG.get(regionId);
    }

    public static void reload() {
        REGION_TO_SONG.clear();
        loaded = false;
        loadOnce();
    }
}
