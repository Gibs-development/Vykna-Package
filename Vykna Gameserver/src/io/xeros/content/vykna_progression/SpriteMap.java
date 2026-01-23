package io.xeros.content.vykna_progression;

import java.util.stream.Stream;

public enum SpriteMap {
    ATTACK(1),
    HITPOINTS(2),
    FIREMAKING(2),
    WOODCUTTING(2),
    RUNECRAFTING(2),
    CRAFTING(2),
    FARMING(2),
    SPADE(2),
    RAT(2),
    HUNTER(2),
    STRENGTH(2),
    FLETCHING(2),
    DEFENCE(2),
    RANGED(2),
    AGILITY(2),
    PRAYER(2),
    MAGIC(2),
    MINING(2),
    KBD(2),
    SIRE(2),
    DUKE(2),
    BLOODHOUND(2),
    GRAARDOR(2),
    SMITHING(2),
    HYDRA(2),
    KRIL(2),
    KREE(2),
    HERBLORE(2),
    THIEVING(2),
    FISHING(2),
    COOKING(2),
    SLAYER(32);


    SpriteMap(int id) {
        this.id = id;
    }

    public static SpriteMap forId(int id) {
        return Stream.of(values()).filter(s -> s.id == id).findFirst().orElse(null);
    }

    public static Stream<SpriteMap> stream() {
        return Stream.of(values());
    }

    public static int length() {
        return values().length;
    }

    private final int id;
}
