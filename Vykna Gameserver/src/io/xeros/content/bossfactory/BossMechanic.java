package io.xeros.content.bossfactory;

public enum BossMechanic {
    BASIC_MAGIC("Basic Magic", true),
    BASIC_RANGE("Basic Ranged", true),
    FLOOR_SPLAT("Tile Splat", false),
    ARENA_WALL("Arena Wall", false),
    ARENA_BEAM("Arena Beam", false);

    private final String displayName;
    private final boolean basic;

    BossMechanic(String displayName, boolean basic) {
        this.displayName = displayName;
        this.basic = basic;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isBasic() {
        return basic;
    }
}
