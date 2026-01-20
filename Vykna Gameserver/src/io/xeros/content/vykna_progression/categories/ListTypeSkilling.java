package io.xeros.content.vykna_progression.categories;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum ListTypeSkilling {

    CHOP_LOGS_100(3000, "CHOP_LOGS_100", 5),
    FISH_SHRIMP_50(3001, "FISH_SHRIMP_50", 3);

    private final int uid;
    private final String tag;
    private final int points;

    ListTypeSkilling(int uid, String tag, int points) {
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

    private static final Map<Integer, ListTypeSkilling> BY_UID;
    static {
        Map<Integer, ListTypeSkilling> map = new HashMap<>();
        for (ListTypeSkilling a : values()) {
            if (map.put(a.uid, a) != null) {
                throw new IllegalStateException("Duplicate SkillingCategory uid: " + a.uid);
            }
        }
        BY_UID = Collections.unmodifiableMap(map);
    }

    public static ListTypeSkilling forUid(int uid) {
        return BY_UID.get(uid);
    }
}
