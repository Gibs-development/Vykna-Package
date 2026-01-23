package io.xeros.content.vykna_progression;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Handles JSON persistence for Vykna progression.
 *
 * How to test:
 * - Complete an entry, relog, verify it remains completed.
 * - Make partial progress, relog, verify progress persists.
 * - Complete multiple entries quickly, verify toast queue displays sequentially.
 * - Corrupt the JSON file, relog, verify login does not crash and state resets safely (warning logged).
 */
public final class VyknaProgressionPersistence {
    private static final Logger logger = LoggerFactory.getLogger(VyknaProgressionPersistence.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final long SAVE_THROTTLE_MS = TimeUnit.SECONDS.toMillis(20);
    private static final int SAVE_VERSION = 1; // Bump when save format changes.
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(new ThreadFactory() {
        private int index = 0;

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "vykna-progression-save-" + index++);
        }
    });

    private VyknaProgressionPersistence() {
    }

    public static void load(Player player) {
        if (player == null) {
            return;
        }
        Path file = resolveSavePath(player);
        if (file == null || !Files.exists(file)) {
            return;
        }
        try {
            String json = Files.readString(file, StandardCharsets.UTF_8);
            if (json == null || json.trim().isEmpty()) {
                return;
            }
            VyknaProgressionSaveData data = GSON.fromJson(json, VyknaProgressionSaveData.class);
            if (data == null) {
                return;
            }
            applyToPlayerState(player, data);
        } catch (JsonSyntaxException e) {
            logger.warn("Malformed Vykna progression save for {}. Resetting to defaults.", player.getLoginName(), e);
            player.setVyknaProgressionState(new VyknaProgressionPlayerState());
        } catch (IOException e) {
            logger.warn("Failed to load Vykna progression save for {}.", player.getLoginName(), e);
        }
    }

    public static void markDirty(Player player) {
        if (player == null) {
            return;
        }
        player.getVyknaProgressionState().markDirty();
    }

    public static void save(Player player, boolean force) {
        if (player == null) {
            return;
        }
        VyknaProgressionPlayerState state = player.getVyknaProgressionState();
        if (state == null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (!force) {
            if (!state.isDirty()) {
                return;
            }
            if (now - state.getLastSavedAt() < SAVE_THROTTLE_MS) {
                return;
            }
        }
        VyknaProgressionSaveData snapshot = snapshot(state);
        if (snapshot == null) {
            return;
        }
        state.markSaved(now);
        EXECUTOR.submit(() -> writeSnapshot(player, snapshot));
    }

    private static void writeSnapshot(Player player, VyknaProgressionSaveData snapshot) {
        Path file = resolveSavePath(player);
        if (file == null) {
            return;
        }
        try {
            Misc.createDirectory(file.getParent().toString());
            String json = GSON.toJson(snapshot);
            Path tempFile = Paths.get(file.toString() + ".tmp");
            // Atomic write: temp file then replace to reduce corruption risk on crash.
            Files.writeString(tempFile, json, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            try {
                Files.move(tempFile, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(tempFile, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            logger.warn("Failed to save Vykna progression for {}.", player.getLoginName(), e);
        }
    }

    private static VyknaProgressionSaveData snapshot(VyknaProgressionPlayerState state) {
        VyknaProgressionSaveData data = new VyknaProgressionSaveData();
        data.setVersion(SAVE_VERSION);
        data.setProgressByEntryId(new HashMap<>(state.getProgressByEntryId()));
        data.setCompletedEntryIds(new HashSet<>(state.getCompletedEntries()));
        data.setCompletedAtByEntryId(new HashMap<>(state.getCompletedAtByEntryId()));
        data.setPointsTotal(state.getPointsTotal());
        data.setScoreTotal(state.getScoreTotal());
        data.setLastCompletedEntryId(state.getLastCompletedEntryId());
        data.setLastCompletedListTypeId(state.getLastCompletedListTypeId());
        data.setShowCompleted(state.isShowCompleted());
        return data;
    }

    private static void applyToPlayerState(Player player, VyknaProgressionSaveData data) {
        VyknaProgressionPlayerState state = new VyknaProgressionPlayerState();
        Set<Integer> knownEntries = resolveKnownEntryIds();
        if (data.getProgressByEntryId() != null) {
            data.getProgressByEntryId().forEach((entryId, progress) -> {
                if (knownEntries.contains(entryId)) {
                    state.setProgress(entryId, progress == null ? 0 : progress);
                }
            });
        }
        if (data.getCompletedEntryIds() != null) {
            data.getCompletedEntryIds().forEach(entryId -> {
                if (knownEntries.contains(entryId)) {
                    state.setCompleted(entryId, true);
                }
            });
        }
        if (data.getCompletedAtByEntryId() != null) {
            data.getCompletedAtByEntryId().forEach((entryId, completedAt) -> {
                if (knownEntries.contains(entryId) && completedAt != null) {
                    state.setCompletedAt(entryId, completedAt);
                }
            });
        }
        state.setPointsTotal(data.getPointsTotal());
        state.setScoreTotal(data.getScoreTotal());
        state.setLastCompleted(data.getLastCompletedEntryId(), data.getLastCompletedListTypeId());
        state.setShowCompleted(data.isShowCompleted());
        player.setVyknaProgressionState(state);
    }

    private static Set<Integer> resolveKnownEntryIds() {
        Set<Integer> known = new HashSet<>();
        for (ProgressionListDefinition definition : VyknaProgressionRegistry.getAll().values()) {
            for (ProgressionEntry entry : definition.getEntries()) {
                known.add(entry.getEntryId());
            }
        }
        return known;
    }

    private static Path resolveSavePath(Player player) {
        String username = resolveSafeUsername(player);
        if (username == null || username.isEmpty()) {
            return null;
        }
        String directory = Server.getSaveDirectory() + "/vykna/progression/";
        return Paths.get(directory, username + ".json");
    }

    private static String resolveSafeUsername(Player player) {
        String loginName = player.getLoginName();
        String name = (loginName == null || loginName.isEmpty()) ? player.playerName : loginName;
        if (name == null) {
            return null;
        }
        return name.toLowerCase().replaceAll("[^a-z0-9._-]", "_");
    }
}
