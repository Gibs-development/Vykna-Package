package io.xeros.content.sound;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class AmbientSoundRegistry {
    private static final String RESOURCE_PATH = "sound/ambient_sound_registry.json";
    private static final AmbientSoundRegistry INSTANCE = load();

    public static AmbientSoundRegistry get() {
        return INSTANCE;
    }

    private final Map<Integer, AmbientSoundProfile> objectProfiles;

    private AmbientSoundRegistry(Map<Integer, AmbientSoundProfile> objectProfiles) {
        this.objectProfiles = objectProfiles;
    }

    public Map<Integer, AmbientSoundProfile> getObjectProfiles() {
        return objectProfiles;
    }

    public AmbientSoundProfile getProfile(int objectId) {
        return objectProfiles.get(objectId);
    }

    private static AmbientSoundRegistry load() {
        try (InputStream input = AmbientSoundRegistry.class.getClassLoader().getResourceAsStream(RESOURCE_PATH)) {
            if (input == null) {
                return new AmbientSoundRegistry(Collections.emptyMap());
            }
            Config config = new Gson().fromJson(new InputStreamReader(input, StandardCharsets.UTF_8), Config.class);
            Map<Integer, AmbientSoundProfile> profiles = new HashMap<>();
            if (config.objects != null) {
                for (Map.Entry<String, AmbientSoundProfile> entry : config.objects.entrySet()) {
                    profiles.put(Integer.parseInt(entry.getKey()), entry.getValue());
                }
            }
            return new AmbientSoundRegistry(Collections.unmodifiableMap(profiles));
        } catch (JsonParseException | NumberFormatException ex) {
            return new AmbientSoundRegistry(Collections.emptyMap());
        } catch (Exception ex) {
            return new AmbientSoundRegistry(Collections.emptyMap());
        }
    }

    private static final class Config {
        private Map<String, AmbientSoundProfile> objects;
    }

    public static final class AmbientSoundProfile {
        private int soundId;
        private int radius;
        private int intervalTicks;

        public int getSoundId() {
            return soundId;
        }

        public int getRadius() {
            return radius;
        }

        public int getIntervalTicks() {
            return intervalTicks;
        }
    }
}
