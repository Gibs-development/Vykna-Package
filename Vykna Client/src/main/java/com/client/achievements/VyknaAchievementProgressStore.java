package com.client.achievements;

import com.client.graphics.interfaces.impl.AchievementListPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class VyknaAchievementProgressStore {
    public static final class Progress {
        private int current;
        private int target;
        private boolean done;

        public Progress(int current, int target, boolean done) {
            this.current = current;
            this.target = target;
            this.done = done;
        }

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public int getTarget() {
            return target;
        }

        public void setTarget(int target) {
            this.target = target;
        }

        public boolean isDone() {
            return done;
        }

        public void setDone(boolean done) {
            this.done = done;
        }
    }

    private static final Map<String, Map<String, Map<Integer, Progress>>> PROGRESS = new HashMap<>();
    private static final Map<String, String> SELECTED_GROUP = new HashMap<>();

    private VyknaAchievementProgressStore() {
    }

    public static void applyList(String type, String group, List<ProgressEntry> entries) {
        Map<String, Map<Integer, Progress>> groups = PROGRESS.computeIfAbsent(type, key -> new HashMap<>());
        Map<Integer, Progress> groupProgress = groups.computeIfAbsent(group, key -> new HashMap<>());
        groupProgress.clear();
        for (ProgressEntry entry : entries) {
            groupProgress.put(entry.id, new Progress(entry.current, entry.target, entry.done));
        }
        SELECTED_GROUP.putIfAbsent(type, defaultGroup(type));
        if (group.equalsIgnoreCase(SELECTED_GROUP.get(type))) {
            refreshInterface(type, group);
        }
    }

    public static void applyDelta(int id, int current, int target, boolean done) {
        VyknaAchievementDefinitions.Definition def = VyknaAchievementDefinitions.get(id);
        if (def == null) {
            return;
        }
        String type = def.getType();
        String group = def.getGroup();
        Map<String, Map<Integer, Progress>> groups = PROGRESS.computeIfAbsent(type, key -> new HashMap<>());
        Map<Integer, Progress> groupProgress = groups.computeIfAbsent(group, key -> new HashMap<>());
        Progress progress = groupProgress.get(id);
        if (progress == null) {
            progress = new Progress(current, target, done);
            groupProgress.put(id, progress);
        } else {
            progress.setCurrent(current);
            progress.setTarget(target);
            progress.setDone(done);
        }
        String selected = SELECTED_GROUP.get(type);
        if (selected != null && selected.equalsIgnoreCase(group)) {
            refreshInterface(type, group);
        }
    }

    public static Progress get(int id) {
        VyknaAchievementDefinitions.Definition def = VyknaAchievementDefinitions.get(id);
        if (def == null) {
            return null;
        }
        Map<String, Map<Integer, Progress>> groups = PROGRESS.get(def.getType());
        if (groups == null) {
            return null;
        }
        Map<Integer, Progress> groupProgress = groups.get(def.getGroup());
        if (groupProgress == null) {
            return null;
        }
        return groupProgress.get(id);
    }

    public static void showList(String type, String group) {
        SELECTED_GROUP.put(type, group);
        refreshInterface(type, group);
    }

    private static void refreshInterface(String type, String group) {
        Map<String, Map<Integer, Progress>> groups = PROGRESS.get(type);
        if (groups == null) {
            AchievementListPage.updateEntries(type, group, new HashMap<>());
            return;
        }
        Map<Integer, Progress> groupProgress = groups.get(group);
        if (groupProgress == null) {
            groupProgress = new HashMap<>();
        }
        AchievementListPage.updateEntries(type, group, groupProgress);
    }

    private static String defaultGroup(String type) {
        if ("combat".equalsIgnoreCase(type)) {
            return "Starter";
        }
        if ("skilling".equalsIgnoreCase(type)) {
            return "Woodcutting";
        }
        return "";
    }

    public static final class ProgressEntry {
        private final int id;
        private final int current;
        private final int target;
        private final boolean done;

        public ProgressEntry(int id, int current, int target, boolean done) {
            this.id = id;
            this.current = current;
            this.target = target;
            this.done = done;
        }
    }
}
