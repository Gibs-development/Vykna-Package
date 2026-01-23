package io.xeros.content.vykna_progression;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * JSON persistence model for Vykna progression.
 */
public final class VyknaProgressionSaveData {
    private int version = 1;
    private Map<Integer, Integer> progressByEntryId = new HashMap<>();
    private Set<Integer> completedEntryIds = new HashSet<>();
    private Map<Integer, Long> completedAtByEntryId = new HashMap<>();
    private int pointsTotal;
    private int scoreTotal;
    private int lastCompletedEntryId;
    private int lastCompletedListTypeId;
    private boolean showCompleted = true;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Map<Integer, Integer> getProgressByEntryId() {
        return progressByEntryId;
    }

    public void setProgressByEntryId(Map<Integer, Integer> progressByEntryId) {
        this.progressByEntryId = progressByEntryId;
    }

    public Set<Integer> getCompletedEntryIds() {
        return completedEntryIds;
    }

    public void setCompletedEntryIds(Set<Integer> completedEntryIds) {
        this.completedEntryIds = completedEntryIds;
    }

    public Map<Integer, Long> getCompletedAtByEntryId() {
        return completedAtByEntryId;
    }

    public void setCompletedAtByEntryId(Map<Integer, Long> completedAtByEntryId) {
        this.completedAtByEntryId = completedAtByEntryId;
    }

    public int getPointsTotal() {
        return pointsTotal;
    }

    public void setPointsTotal(int pointsTotal) {
        this.pointsTotal = pointsTotal;
    }

    public int getScoreTotal() {
        return scoreTotal;
    }

    public void setScoreTotal(int scoreTotal) {
        this.scoreTotal = scoreTotal;
    }

    public int getLastCompletedEntryId() {
        return lastCompletedEntryId;
    }

    public void setLastCompletedEntryId(int lastCompletedEntryId) {
        this.lastCompletedEntryId = lastCompletedEntryId;
    }

    public int getLastCompletedListTypeId() {
        return lastCompletedListTypeId;
    }

    public void setLastCompletedListTypeId(int lastCompletedListTypeId) {
        this.lastCompletedListTypeId = lastCompletedListTypeId;
    }

    public boolean isShowCompleted() {
        return showCompleted;
    }

    public void setShowCompleted(boolean showCompleted) {
        this.showCompleted = showCompleted;
    }
}
