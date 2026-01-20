package io.xeros.content.vykna_achievements;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class VyknaAchievementProfile {
    private int version = 1;
    private Map<Integer, VyknaAchievementProgress> progress = new HashMap<>();

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Map<Integer, VyknaAchievementProgress> getProgress() {
        return progress;
    }

    public void setProgress(Map<Integer, VyknaAchievementProgress> progress) {
        this.progress = progress == null ? new HashMap<>() : progress;
    }

    public Map<Integer, VyknaAchievementProgress> getProgressView() {
        return Collections.unmodifiableMap(progress);
    }
}
