package io.xeros.content.music;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.xeros.Server;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Loads songId -> songName from Data/music/song_id_to_name.json (your File 2),
 * and provides normalized name lookup back to songId.
 *
 * File format:
 * {
 *   "songs": { "358": "dragontooth island", ... }
 * }
 */
public final class MusicNameIndex {

    private static final Gson GSON = new Gson();

    private static volatile boolean loaded = false;

    private static final Map<Integer, String> ID_TO_NAME = new HashMap<>();
    private static final Map<String, Integer> NORMNAME_TO_ID = new HashMap<>();

    private MusicNameIndex() {}

    public static void loadOnce() {
        if (loaded) return;
        synchronized (MusicNameIndex.class) {
            if (loaded) return;

            File f = new File("Data/music/song_id_to_name.json");
            if (!f.exists()) {
                //Server.getLogger().warn("[Music] Missing Data/music/song_id_to_name.json (File 2). Name lookup will be disabled.");
                System.out.print("[Music] Missing Data/music/song_id_to_name.json (File 2). Name lookup will be disabled.");
                loaded = true;
                return;
            }

            try (FileReader reader = new FileReader(f)) {
                Type rootType = new TypeToken<Map<String, Map<String, String>>>(){}.getType();
                Map<String, Map<String, String>> root = GSON.fromJson(reader, rootType);
                Map<String, String> songs = root == null ? null : root.get("songs");
                if (songs != null) {
                    for (Map.Entry<String, String> e : songs.entrySet()) {
                        try {
                            int id = Integer.parseInt(e.getKey());
                            String name = e.getValue();
                            if (name == null) continue;
                            ID_TO_NAME.put(id, name);
                            NORMNAME_TO_ID.put(norm(name), id);
                        } catch (NumberFormatException ignored) { }
                    }
                }
                System.out.print("[Music] Loaded {} track names.");
            } catch (Exception ex) {
                System.out.print("[Music] Failed to load song_id_to_name.json");
            } finally {
                loaded = true;
            }
        }
    }

    /** Returns the songId for a name, or null if not found. Accepts "Harmony", "harmony", "jolly-r", etc. */
    public static Integer findSongIdByName(String name) {
        if (name == null || name.isEmpty()) return null;
        loadOnce();
        return NORMNAME_TO_ID.get(norm(name));
    }

    public static String getSongName(int songId) {
        loadOnce();
        return ID_TO_NAME.get(songId);
    }

    /** Normalization: lowercase, remove non-alphanum. "jolly-r" -> "jollyr" */
    public static String norm(String s) {
        s = s.toLowerCase(Locale.ROOT);
        StringBuilder b = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) b.append(c);
        }
        return b.toString();
    }
}
