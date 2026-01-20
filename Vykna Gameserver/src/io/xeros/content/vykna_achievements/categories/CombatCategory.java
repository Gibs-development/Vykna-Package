package io.xeros.content.vykna_achievements.categories;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum CombatCategory {

    KILL_GOBLINS_25(2000, "KILL_GOBLINS_25", 3),
    HIT_10_DAMAGE(2001, "HIT_10_DAMAGE", 1);

    private final int uid;
    private final String tag;
    private final int points;

    CombatCategory(int uid, String tag, int points) {
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

    private static final Map<Integer, CombatCategory> BY_UID;
    static {
        Map<Integer, CombatCategory> map = new HashMap<>();
        for (CombatCategory a : values()) {
            if (map.put(a.uid, a) != null) {
                throw new IllegalStateException("Duplicate CombatCategory uid: " + a.uid);
            }
        }
        BY_UID = Collections.unmodifiableMap(map);
    }

    public static CombatCategory forUid(int uid) {
        return BY_UID.get(uid);
    }
}
