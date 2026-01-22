package com.client.vykna_progression;

import java.util.List;

public class ProgressionSummaryPayload {
    private int scoreTotal;
    private int pointsTotal;
    private int lastCompletedEntryId;
    private int lastCompletedListTypeId;
    private boolean showCompleted;
    private List<LeaderboardEntry> leaderboard;

    public int getScoreTotal() {
        return scoreTotal;
    }

    public int getPointsTotal() {
        return pointsTotal;
    }

    public int getLastCompletedEntryId() {
        return lastCompletedEntryId;
    }

    public int getLastCompletedListTypeId() {
        return lastCompletedListTypeId;
    }

    public boolean isShowCompleted() {
        return showCompleted;
    }

    public List<LeaderboardEntry> getLeaderboard() {
        return leaderboard;
    }

    public static class LeaderboardEntry {
        private String name;
        private int score;

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }
    }
}
