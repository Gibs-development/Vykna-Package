package io.xeros.content.vykna_progression;

import java.util.stream.Stream;

public enum SpriteMap {
    ATTACK(1),
    HITPOINTS(2),
    FIREMAKING(3),
    WOODCUTTING(4),
    RUNECRAFTING(5),
    CRAFTING(6),
    FARMING(7),
    SPADE(8),
    RAT(9),
    HUNTER(10),
    STRENGTH(11),
    FLETCHING(12),
    DEFENCE(13),
    RANGED(14),
    AGILITY(15),
    PRAYER(16),
    MAGIC(17),
    MINING(18),
    KBD(19),
    SIRE(20),
    DUKE(21),
    BLOODHOUND(22),
    GRAARDOR(23),
    SMITHING(24),
    HYDRA(25),
    KRIL(26),
    KREE(27),
    HERBLORE(28),
    THIEVING(29),
    FISHING(30),
    COOKING(31),
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
    public int getId() {
        return id;
    }
    private final int id;
}
