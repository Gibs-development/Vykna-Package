package com.client.achievements;

import com.client.graphics.interfaces.builder.impl.tasks.TaskInterfaceActions;
import com.client.graphics.interfaces.builder.impl.tasks.model.TaskDifficulty;
import com.client.graphics.interfaces.builder.impl.tasks.model.TaskEntry;
import com.client.graphics.interfaces.impl.Interfaces;
import com.google.common.collect.Lists;

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

    private static final Map<Integer, Progress> PROGRESS = new HashMap<>();
    private static String lastType = "combat";
    private static String lastGroup = "starter";

    private VyknaAchievementProgressStore() {
    }

    public static void applyList(String type, String group, List<ProgressEntry> entries) {
        lastType = type;
        lastGroup = group;
        for (ProgressEntry entry : entries) {
            PROGRESS.put(entry.id, new Progress(entry.current, entry.target, entry.done));
        }
        refreshInterface();
    }

    public static void applyDelta(int id, int current, int target, boolean done) {
        Progress progress = PROGRESS.get(id);
        if (progress == null) {
            progress = new Progress(current, target, done);
            PROGRESS.put(id, progress);
        } else {
            progress.setCurrent(current);
            progress.setTarget(target);
            progress.setDone(done);
        }
        refreshInterface();
    }

    public static Progress get(int id) {
        return PROGRESS.get(id);
    }

    private static void refreshInterface() {
        TaskInterfaceActions actions = Interfaces.taskInterface.actions;
        List<TaskEntry> entries = Lists.newArrayList();
        for (VyknaAchievementDefinitions.Definition def : VyknaAchievementDefinitions.byTypeGroup(lastType, lastGroup)) {
            Progress progress = PROGRESS.get(def.getId());
            int current = progress == null ? 0 : progress.getCurrent();
            int target = progress == null ? 1 : progress.getTarget();
            boolean done = progress != null && progress.isDone();
            String extraRewards = "Points: " + def.getPoints();
            TaskDifficulty difficulty = difficultyFor(def.getPoints());
            entries.add(new TaskEntry(
                    def.getName(),
                    def.getDescription(),
                    done,
                    difficulty,
                    null,
                    extraRewards,
                    Lists.newArrayList(current + "/" + target)
            ));
        }
        actions.setAchievements(entries);
    }

    private static TaskDifficulty difficultyFor(int points) {
        if (points <= 10) {
            return TaskDifficulty.STARTER;
        }
        if (points <= 20) {
            return TaskDifficulty.BEGINNER;
        }
        if (points <= 30) {
            return TaskDifficulty.INTERMEDIATE;
        }
        if (points <= 50) {
            return TaskDifficulty.EXPERT;
        }
        return TaskDifficulty.LEGENDARY;
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
