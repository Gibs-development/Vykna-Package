package io.xeros.content.vykna_progression;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VyknaProgressionPlayerState {
    private final Map<Integer, Integer> progressByEntryId = new HashMap<>();
    private final Set<Integer> completedEntries = new HashSet<>();
    private int pointsTotal;

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

    public Map<Integer, Integer> getProgressByEntryId() {
        return Collections.unmodifiableMap(progressByEntryId);
    }

    public Set<Integer> getCompletedEntries() {
        return Collections.unmodifiableSet(completedEntries);
    }

    public int getPointsTotal() {
        return pointsTotal;
    }

    public void addPoints(int points) {
        pointsTotal += points;
    }
}
