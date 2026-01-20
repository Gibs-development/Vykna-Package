package io.xeros.content.vykna_progression.categories;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum ListTypeTasks {

    BURN_LOGS_50(1000, "BURN_LOGS_50", 5),
    OPEN_ACHIEVEMENTS(1001, "OPEN_ACHIEVEMENTS", 1);

    private final int uid;
    private final String tag;
    private final int points;

    ListTypeTasks(int uid, String tag, int points) {
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

    private static final Map<Integer, ListTypeTasks> BY_UID;
    static {
        Map<Integer, ListTypeTasks> map = new HashMap<>();
        for (ListTypeTasks a : values()) {
            if (map.put(a.uid, a) != null) {
                throw new IllegalStateException("Duplicate TasksCategory uid: " + a.uid);
            }
        }
        BY_UID = Collections.unmodifiableMap(map);
    }

    public static ListTypeTasks forUid(int uid) {
        return BY_UID.get(uid);
    }
}
