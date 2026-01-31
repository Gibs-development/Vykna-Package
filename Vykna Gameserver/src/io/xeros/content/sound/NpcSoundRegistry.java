package io.xeros.content.sound;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import io.xeros.model.definitions.NpcStats;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class NpcSoundRegistry {
    private static final String RESOURCE_PATH = "sound/npc_sound_registry.json";
    private static final NpcSoundRegistry INSTANCE = load();

    public static NpcSoundRegistry get() {
        return INSTANCE;
    }

    private final Map<Integer, SoundProfile> npcOverrides;
    private final Map<String, SoundProfile> categoryProfiles;
    private final Map<String, String> keywordCategories;
    private final SoundProfile fallbackProfile;

    private NpcSoundRegistry(Map<Integer, SoundProfile> npcOverrides,
                             Map<String, SoundProfile> categoryProfiles,
                             Map<String, String> keywordCategories,
                             SoundProfile fallbackProfile) {
        this.npcOverrides = npcOverrides;
        this.categoryProfiles = categoryProfiles;
        this.keywordCategories = keywordCategories;
        this.fallbackProfile = fallbackProfile;
    }

    public SoundProfile resolveProfile(int npcId, String npcName, NpcStats stats) {
        SoundProfile direct = npcOverrides.get(npcId);
        if (direct != null) {
            return direct;
        }
        String category = resolveCategory(npcName, stats);
        SoundProfile profile = categoryProfiles.get(category);
        return profile != null ? profile : fallbackProfile;
    }

    private String resolveCategory(String npcName, NpcStats stats) {
        if (stats != null) {
            if (stats.isDemon()) {
                return "demon";
            }
            if (stats.isUndead()) {
                return "undead";
            }
            if (stats.isDragon()) {
                return "beast";
            }
        }
        if (npcName != null) {
            String lower = npcName.toLowerCase(Locale.ROOT);
            for (Map.Entry<String, String> entry : keywordCategories.entrySet()) {
                if (lower.contains(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        return "humanoid";
    }

    private static NpcSoundRegistry load() {
        try (InputStream input = NpcSoundRegistry.class.getClassLoader().getResourceAsStream(RESOURCE_PATH)) {
            if (input == null) {
                return new NpcSoundRegistry(Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), new SoundProfile());
            }
            Config config = new Gson().fromJson(new InputStreamReader(input, StandardCharsets.UTF_8), Config.class);
            Map<Integer, SoundProfile> overrides = new HashMap<>();
            if (config.npcOverrides != null) {
                for (Map.Entry<String, SoundProfile> entry : config.npcOverrides.entrySet()) {
                    overrides.put(Integer.parseInt(entry.getKey()), entry.getValue());
                }
            }
            Map<String, SoundProfile> categories = new HashMap<>();
            if (config.categories != null) {
                for (Map.Entry<String, SoundProfile> entry : config.categories.entrySet()) {
                    categories.put(entry.getKey().toLowerCase(Locale.ROOT), entry.getValue());
                }
            }
            Map<String, String> keywords = new HashMap<>();
            if (config.keywords != null) {
                for (Map.Entry<String, String> entry : config.keywords.entrySet()) {
                    keywords.put(entry.getKey().toLowerCase(Locale.ROOT), entry.getValue().toLowerCase(Locale.ROOT));
                }
            }
            SoundProfile fallback = config.fallback != null ? config.fallback : new SoundProfile();
            return new NpcSoundRegistry(overrides, categories, keywords, fallback);
        } catch (JsonParseException | NumberFormatException ex) {
            return new NpcSoundRegistry(Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), new SoundProfile());
        } catch (Exception ex) {
            return new NpcSoundRegistry(Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), new SoundProfile());
        }
    }

    private static final class Config {
        private Map<String, SoundProfile> categories;
        private Map<String, String> keywords;
        private Map<String, SoundProfile> npcOverrides;
        private SoundProfile fallback;
    }

    public static final class SoundProfile {
        private int[] attack;
        private int[] death;
        private int[] hit;
        private int[] special;

        public int[] getAttack() {
            return attack;
        }

        public int[] getDeath() {
            return death;
        }

        public int[] getHit() {
            return hit;
        }

        public int[] getSpecial() {
            return special;
        }
    }
}
