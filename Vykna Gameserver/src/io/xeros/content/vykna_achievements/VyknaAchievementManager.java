package io.xeros.content.vykna_achievements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class VyknaAchievementManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String SAVE_FOLDER = "data/vykna_achievements";
    private static final int LIST_CHUNK_SIZE = 190;

    private final Player player;
    private VyknaAchievementProfile profile;

    public VyknaAchievementManager(Player player) {
        this.player = player;
    }

    public void load() {
        File file = getSaveFile();
        if (!file.exists()) {
            profile = seedDevProfile();
            validateProfile();
            save();
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            profile = GSON.fromJson(reader, VyknaAchievementProfile.class);
        } catch (IOException e) {
            e.printStackTrace();
            profile = seedDevProfile();
        }
        validateProfile();
    }

    public void save() {
        if (profile == null) {
            return;
        }
        File file = getSaveFile();
        Misc.createDirectory(file.getParent());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(GSON.toJson(profile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendList(String type, String group) {
        ensureLoaded();
        String payload = buildListPayload(type, group);
        sendPayload(payload);
    }

    public void sendAllLists() {
        ensureLoaded();
        for (String type : VyknaAchievements.getTypes()) {
            for (String group : VyknaAchievements.getGroupsByType(type)) {
                String payload = buildListPayload(type, group);
                sendPayload(payload);
            }
        }
    }

    public void sendDelta(int id) {
        ensureLoaded();
        VyknaAchievementProgress progress = profile.getProgress().get(id);
        if (progress == null) {
            return;
        }
        String payload = "A_DELTA|" + id
                + "|" + progress.getCurrent()
                + "|" + progress.getTarget()
                + "|" + (progress.isDone() ? 1 : 0);
        sendPayload(payload);
    }

    public boolean addProgress(int id, int amount) {
        ensureLoaded();
        VyknaAchievementDefinition def = VyknaAchievements.getDefinition(id);
        if (def == null) {
            return false;
        }
        VyknaAchievementProgress progress = profile.getProgress().computeIfAbsent(id,
                key -> new VyknaAchievementProgress(0, def.getTarget(), false));
        if (progress.isDone()) {
            return false;
        }
        int newValue = Math.min(def.getTarget(), progress.getCurrent() + amount);
        progress.setCurrent(newValue);
        progress.setTarget(def.getTarget());
        if (newValue >= def.getTarget()) {
            progress.setDone(true);
        }
        sendDelta(id);
        return true;
    }

    private void ensureLoaded() {
        if (profile == null) {
            load();
        }
    }

    private void validateProfile() {
        if (profile == null) {
            profile = new VyknaAchievementProfile();
        }
        if (profile.getProgress() == null) {
            profile.setProgress(new HashMap<>());
        }
        for (VyknaAchievementDefinition def : VyknaAchievements.getAll()) {
            VyknaAchievementProgress progress = profile.getProgress().get(def.getId());
            if (progress == null) {
                profile.getProgress().put(def.getId(), new VyknaAchievementProgress(0, def.getTarget(), false));
                continue;
            }
            progress.setTarget(def.getTarget());
            if (progress.getCurrent() >= def.getTarget()) {
                progress.setCurrent(def.getTarget());
                progress.setDone(true);
            }
        }
    }

    private String buildListPayload(String type, String group) {
        StringBuilder builder = new StringBuilder();
        builder.append("A_LIST|")
                .append(type)
                .append("|")
                .append(group)
                .append("|v=1");

        int count = 0;
        StringBuilder entries = new StringBuilder();
        for (VyknaAchievementDefinition def : VyknaAchievements.getByTypeGroup(type, group)) {
            VyknaAchievementProgress progress = profile.getProgress().get(def.getId());
            if (progress == null) {
                progress = new VyknaAchievementProgress(0, def.getTarget(), false);
                profile.getProgress().put(def.getId(), progress);
            }
            if (entries.length() > 0) {
                entries.append(";");
            }
            entries.append(def.getId())
                    .append(",")
                    .append(progress.getCurrent())
                    .append(",")
                    .append(progress.getTarget())
                    .append(",")
                    .append(progress.isDone() ? 1 : 0);
            count++;
        }

        if (entries.length() == 0) {
            builder.append("|0|");
            return builder.toString();
        }

        String entriesText = entries.toString();
        builder.append("|").append(count).append("|");

        if (entriesText.length() <= LIST_CHUNK_SIZE) {
            builder.append(entriesText);
            return builder.toString();
        }

        sendChunkedList(type, group, count, entriesText);
        return null;
    }

    private void sendChunkedList(String type, String group, int count, String entriesText) {
        sendPayload("A_LIST_BEGIN|" + type + "|" + group + "|v=1|" + count);
        int offset = 0;
        while (offset < entriesText.length()) {
            int end = Math.min(entriesText.length(), offset + LIST_CHUNK_SIZE);
            String chunk = entriesText.substring(offset, end);
            sendPayload("A_LIST_CHUNK|" + chunk);
            offset = end;
        }
        sendPayload("A_LIST_END|" + type + "|" + group + "|v=1|" + count);
    }

    private void sendPayload(String payload) {
        if (payload == null) {
            return;
        }
        if (player.getPA() == null) {
            return;
        }
        player.getPA().sendClientCommand(payload);
    }

    private VyknaAchievementProfile seedDevProfile() {
        VyknaAchievementProfile seeded = new VyknaAchievementProfile();
        Map<Integer, VyknaAchievementProgress> seededProgress = new HashMap<>();
        if (Server.isDebug()) {
            for (VyknaAchievementDefinition def : VyknaAchievements.getAll()) {
                int current;
                boolean done;
                int selector = def.getId() % 3;
                if (selector == 0) {
                    current = def.getTarget();
                    done = true;
                } else if (selector == 1) {
                    current = Math.max(0, def.getTarget() / 2);
                    done = false;
                } else {
                    current = 0;
                    done = false;
                }
                seededProgress.put(def.getId(), new VyknaAchievementProgress(current, def.getTarget(), done));
            }
        }
        seeded.setProgress(seededProgress);
        return seeded;
    }

    private File getSaveFile() {
        String base = SAVE_FOLDER + "/";
        return new File(base + player.getLoginNameLower() + ".json");
    }
}
