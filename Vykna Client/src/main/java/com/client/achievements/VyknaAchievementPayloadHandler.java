package com.client.achievements;

import java.util.ArrayList;
import java.util.List;

public final class VyknaAchievementPayloadHandler {
    private static String pendingType;
    private static String pendingGroup;
    private static int pendingCount;
    private static StringBuilder pendingEntries;

    private VyknaAchievementPayloadHandler() {
    }

    public static boolean handle(String payload) {
        if (payload.startsWith("A_LIST_BEGIN|")) {
            String[] parts = payload.split("\\|");
            if (parts.length >= 5) {
                pendingType = parts[1];
                pendingGroup = parts[2];
                pendingCount = parseInt(parts[4]);
                pendingEntries = new StringBuilder();
            }
            return true;
        }
        if (payload.startsWith("A_LIST_CHUNK|")) {
            if (pendingEntries != null) {
                pendingEntries.append(payload.substring("A_LIST_CHUNK|".length()));
            }
            return true;
        }
        if (payload.startsWith("A_LIST_END|")) {
            if (pendingEntries != null && pendingType != null && pendingGroup != null) {
                applyListPayload(pendingType, pendingGroup, pendingCount, pendingEntries.toString());
            }
            pendingEntries = null;
            pendingType = null;
            pendingGroup = null;
            pendingCount = 0;
            return true;
        }
        if (payload.startsWith("A_LIST|")) {
            String[] parts = payload.split("\\|", 6);
            if (parts.length >= 6) {
                String type = parts[1];
                String group = parts[2];
                int count = parseInt(parts[4]);
                String entries = parts[5];
                applyListPayload(type, group, count, entries);
            }
            return true;
        }
        if (payload.startsWith("A_DELTA|")) {
            String[] parts = payload.split("\\|");
            if (parts.length >= 5) {
                int id = parseInt(parts[1]);
                int current = parseInt(parts[2]);
                int target = parseInt(parts[3]);
                boolean done = "1".equals(parts[4]);
                VyknaAchievementProgressStore.applyDelta(id, current, target, done);
            }
            return true;
        }
        return false;
    }

    private static void applyListPayload(String type, String group, int count, String entries) {
        List<VyknaAchievementProgressStore.ProgressEntry> list = new ArrayList<>();
        if (entries != null && !entries.isEmpty()) {
            String[] rows = entries.split(";");
            for (String row : rows) {
                String[] cols = row.split(",");
                if (cols.length < 4) {
                    continue;
                }
                int id = parseInt(cols[0]);
                int current = parseInt(cols[1]);
                int target = parseInt(cols[2]);
                boolean done = "1".equals(cols[3]);
                list.add(new VyknaAchievementProgressStore.ProgressEntry(id, current, target, done));
            }
        }
        if (count == 0) {
            list = new ArrayList<>();
        }
        VyknaAchievementProgressStore.applyList(type, group, list);
    }

    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return 0;
        }
    }
}
