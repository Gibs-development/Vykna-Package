package io.xeros.content.vykna_progression;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VyknaProgressionPlayerState {
    private final Map<Integer, Integer> progressByEntryId = new HashMap<>();
    private final Set<Integer> completedEntries = new HashSet<>();
    private final Map<Integer, Long> completedAtByEntryId = new HashMap<>();
    private int pointsTotal;
    private int scoreTotal;
    private int lastCompletedEntryId;
    private int lastCompletedListTypeId;
    private boolean showCompleted = true;
    private transient boolean dirty;
    private transient long lastSavedAt;

    public int getProgress(int entryId) {
        return progressByEntryId.getOrDefault(entryId, 0);
    }

    public void setProgress(int entryId, int progress) {
        progressByEntryId.put(entryId, progress);
    }

    public boolean isCompleted(int entryId) {
        return completedEntries.contains(entryId);
    }

    public void setCompleted(int entryId, boolean completed) {
        if (completed) {
            completedEntries.add(entryId);
        } else {
            completedEntries.remove(entryId);
        }
    }

    public long getCompletedAt(int entryId) {
        return completedAtByEntryId.getOrDefault(entryId, 0L);
    }

    public void setCompletedAt(int entryId, long completedAt) {
        completedAtByEntryId.put(entryId, completedAt);
    }

    public Map<Integer, Integer> getProgressByEntryId() {
        return Collections.unmodifiableMap(progressByEntryId);
    }

    public Set<Integer> getCompletedEntries() {
        return Collections.unmodifiableSet(completedEntries);
    }

    public Map<Integer, Long> getCompletedAtByEntryId() {
        return Collections.unmodifiableMap(completedAtByEntryId);
    }

    public int getPointsTotal() {
        return pointsTotal;
    }

    public void addPoints(int points) {
        pointsTotal += points;
    }

    public void setPointsTotal(int pointsTotal) {
        this.pointsTotal = pointsTotal;
    }

    public int getScoreTotal() {
        return scoreTotal;
    }

    public void addScore(int points) {
        scoreTotal += points;
    }

    public void setScoreTotal(int scoreTotal) {
        this.scoreTotal = scoreTotal;
    }

    public int getLastCompletedEntryId() {
        return lastCompletedEntryId;
    }

    public int getLastCompletedListTypeId() {
        return lastCompletedListTypeId;
    }

    public void setLastCompleted(int entryId, int listTypeId) {
        this.lastCompletedEntryId = entryId;
        this.lastCompletedListTypeId = listTypeId;
    }

    public boolean isShowCompleted() {
        return showCompleted;
    }

    public void setShowCompleted(boolean showCompleted) {
        this.showCompleted = showCompleted;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void markDirty() {
        dirty = true;
    }

    public long getLastSavedAt() {
        return lastSavedAt;
    }

    public void markSaved(long timestamp) {
        dirty = false;
        lastSavedAt = timestamp;
    }
}
