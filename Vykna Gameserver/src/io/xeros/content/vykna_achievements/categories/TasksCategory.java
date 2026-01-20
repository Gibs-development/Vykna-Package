package io.xeros.content.vykna_achievements.categories;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum TasksCategory {

    BURN_LOGS_50(1000, "BURN_LOGS_50", 5),
    OPEN_ACHIEVEMENTS(1001, "OPEN_ACHIEVEMENTS", 1);

    private final int uid;
    private final String tag;
    private final int points;

    TasksCategory(int uid, String tag, int points) {
        this.uid = uid;
        this.tag = tag;
        this.points = points;
    }

    public int getUid() {
        return uid;
    }

    public String getTag() {
        return tag;
    }

    public int getPoints() {
        return points;
    }

    private static final Map<Integer, TasksCategory> BY_UID;
    static {
        Map<Integer, TasksCategory> map = new HashMap<>();
        for (TasksCategory a : values()) {
            if (map.put(a.uid, a) != null) {
                throw new IllegalStateException("Duplicate TasksCategory uid: " + a.uid);
            }
        }
        BY_UID = Collections.unmodifiableMap(map);
    }

    public static TasksCategory forUid(int uid) {
        return BY_UID.get(uid);
    }
}
