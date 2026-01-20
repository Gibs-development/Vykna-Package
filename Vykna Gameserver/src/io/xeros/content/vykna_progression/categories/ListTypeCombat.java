package io.xeros.content.vykna_progression.categories;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum ListTypeCombat {

    KILL_GOBLINS_25(2000, "KILL_GOBLINS_25", 3),
    HIT_10_DAMAGE(2001, "HIT_10_DAMAGE", 1);

    private final int uid;
    private final String tag;
    private final int points;

    ListTypeCombat(int uid, String tag, int points) {
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

    private static final Map<Integer, ListTypeCombat> BY_UID;
    static {
        Map<Integer, ListTypeCombat> map = new HashMap<>();
        for (ListTypeCombat a : values()) {
            if (map.put(a.uid, a) != null) {
                throw new IllegalStateException("Duplicate CombatCategory uid: " + a.uid);
            }
        }
        BY_UID = Collections.unmodifiableMap(map);
    }

    public static ListTypeCombat forUid(int uid) {
        return BY_UID.get(uid);
    }
}
