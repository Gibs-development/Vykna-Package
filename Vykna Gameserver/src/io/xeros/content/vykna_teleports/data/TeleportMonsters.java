package io.xeros.content.vykna_teleports.data;

import io.xeros.content.vykna_teleports.model.TeleportCategory;
import io.xeros.content.vykna_teleports.model.TeleportDefinition;
import io.xeros.content.vykna_teleports.model.TeleportDestination;
import io.xeros.content.vykna_teleports.model.TeleportRequirement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Monsters category teleport definitions.
 * Keep these grouped so editing/expanding is painless.
 *
 * NOTE:
 * - Destination coords are placeholders — set to your actual teleport locations.
 * - Some npcId values are marked TODO where they vary by revision/cache.
 */
public final class TeleportMonsters {

    private static final List<TeleportDefinition> LIST;

    static {
        List<TeleportDefinition> defs = new ArrayList<>();

        /*
         * ⭐ Main Training Area
         * Keep this as ID=1 so it always pins nicely if you sort by id or "featured".
         */
        defs.add(new TeleportDefinition(
                1, TeleportCategory.MONSTERS,
                "Crypt of Saiyuma",
                "Main training dungeon — mixed low → high mobs with fast respawns.",
                TeleportRequirement.combatLevel(1),
                null,
                1615, // npcId (Abyssal demon) - iconic preview for "main dungeon"
                120,
                90,
                true,
                0, // headIconIndex
                new TeleportDestination(3000, 3000, 0) // TODO set real coords
        ));

        /*
         * 🪨 Low–Mid Level Training
         */
        defs.add(new TeleportDefinition(
                2, TeleportCategory.MONSTERS,
                "Goblins",
                "A small green nuisance found across the world.",
                TeleportRequirement.combatLevel(2),
                null,
                100, // TODO verify (varies)
                2,
                50,
                false,
                1,
                new TeleportDestination(3242, 3242, 0)
        ));

        defs.add(new TeleportDefinition(
                3, TeleportCategory.MONSTERS,
                "Cows",
                "Beginner-friendly training and hides for early crafting.",
                TeleportRequirement.combatLevel(1),
                null,
                81, // cow
                2,
                60,
                false,
                2,
                new TeleportDestination(3253, 3266, 0)
        ));

        defs.add(new TeleportDefinition(
                4, TeleportCategory.MONSTERS,
                "Rock Crabs",
                "AFK-friendly melee/range training with high HP.",
                TeleportRequirement.combatLevel(10),
                null,
                1265, // rock crab
                13,
                40,
                false,
                3,
                new TeleportDestination(2675, 3710, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                5, TeleportCategory.MONSTERS,
                "Sand Crabs",
                "Alternative AFK crab spot — great for early stats.",
                TeleportRequirement.combatLevel(15),
                null,
                594, // TODO verify sand crab id
                25,
                55,
                false,
                4,
                new TeleportDestination(1700, 3460, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                6, TeleportCategory.MONSTERS,
                "Experiments",
                "Classic max-hit training — constant low damage taken.",
                TeleportRequirement.combatLevel(20),
                null,
                1677, // experiment (commonly used)
                25,
                65,
                false,
                5,
                new TeleportDestination(3550, 9948, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                7, TeleportCategory.MONSTERS,
                "Crawling Hands",
                "Early Slayer creature — good for starter tasks.",
                TeleportRequirement.combatLevel(5),
                null,
                1648, // crawling hand
                19,
                55,
                false,
                6,
                new TeleportDestination(3420, 3536, 0) // TODO set real coords
        ));

        /*
         * 🔥 Mid-Level Monsters
         */
        defs.add(new TeleportDefinition(
                8, TeleportCategory.MONSTERS,
                "Hill Giants",
                "Large humanoids that hit harder than they look.",
                TeleportRequirement.combatLevel(20),
                null,
                117, // hill giant
                28,
                35,
                true,
                7,
                new TeleportDestination(3116, 9830, 0)
        ));

        defs.add(new TeleportDefinition(
                9, TeleportCategory.MONSTERS,
                "Moss Giants",
                "A solid mid-level grind with big bone drops.",
                TeleportRequirement.combatLevel(30),
                null,
                112, // moss giant
                42,
                45,
                true,
                8,
                new TeleportDestination(3140, 9872, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                10, TeleportCategory.MONSTERS,
                "Ankous",
                "Great for fast magic/range XP and herbs/runes.",
                TeleportRequirement.combatLevel(40),
                null,
                2514, // ankou
                75,
                60,
                true,
                9,
                new TeleportDestination(2359, 5215, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                11, TeleportCategory.MONSTERS,
                "Lesser Demons",
                "Early demon tasks — decent alchs and runes.",
                TeleportRequirement.combatLevel(40),
                null,
                82, // lesser demon
                79,
                65,
                true,
                10,
                new TeleportDestination(2930, 9795, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                12, TeleportCategory.MONSTERS,
                "Greater Demons",
                "Heavier-hitting demon tasks — better drops.",
                TeleportRequirement.combatLevel(60),
                null,
                83, // greater demon
                92,
                70,
                true,
                11,
                new TeleportDestination(3287, 3885, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                13, TeleportCategory.MONSTERS,
                "Fire Giants",
                "Staple RSPS training — great XP and rune drops.",
                TeleportRequirement.combatLevel(50),
                null,
                110, // fire giant
                86,
                75,
                true,
                12,
                new TeleportDestination(2402, 9782, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                14, TeleportCategory.MONSTERS,
                "Green Dragons",
                "Deadly dragons often hunted for their hides.",
                TeleportRequirement.combatLevel(40),
                null,
                260, // green dragon
                79,
                75,
                true,
                13,
                new TeleportDestination(2980, 3614, 0)
        ));

        defs.add(new TeleportDefinition(
                15, TeleportCategory.MONSTERS,
                "Blue Dragons",
                "A safer dragon option — bones and hides.",
                TeleportRequirement.combatLevel(55),
                null,
                265, // blue dragon
                111,
                80,
                true,
                14,
                new TeleportDestination(2900, 9800, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                16, TeleportCategory.MONSTERS,
                "Black Demons",
                "High HP demons — good for cannoning and Slayer.",
                TeleportRequirement.combatLevel(70),
                null,
                84, // black demon
                172,
                85,
                true,
                15,
                new TeleportDestination(2869, 9778, 0) // TODO set real coords
        ));

        /*
         * 💀 Slayer-Oriented Monsters
         */
        defs.add(new TeleportDefinition(
                17, TeleportCategory.MONSTERS,
                "Aberrant Spectres",
                "Requires nose peg/Slayer helm — great herb drops.",
                TeleportRequirement.combatLevel(60),
                null,
                1604, // aberrant spectre
                90,
                80,
                true,
                16,
                new TeleportDestination(3437, 3572, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                18, TeleportCategory.MONSTERS,
                "Dust Devils",
                "Burst/Barrage hotspot — huge magic XP.",
                TeleportRequirement.combatLevel(70),
                null,
                1624, // dust devil
                105,
                85,
                true,
                17,
                new TeleportDestination(3238, 9364, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                19, TeleportCategory.MONSTERS,
                "Gargoyles",
                "Durable Slayer monster — consistent alchs.",
                TeleportRequirement.combatLevel(75),
                null,
                1610, // gargoyle
                111,
                85,
                true,
                18,
                new TeleportDestination(3436, 3534, 2) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                20, TeleportCategory.MONSTERS,
                "Nechryaels",
                "Excellent Slayer XP — also a barrage/burst option.",
                TeleportRequirement.combatLevel(80),
                null,
                1613, // nechryael
                115,
                90,
                true,
                19,
                new TeleportDestination(3440, 3563, 2) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                21, TeleportCategory.MONSTERS,
                "Bloodvelds",
                "Solid melee training with good Slayer XP.",
                TeleportRequirement.combatLevel(55),
                null,
                1618, // bloodveld
                76,
                75,
                true,
                20,
                new TeleportDestination(3422, 3560, 1) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                22, TeleportCategory.MONSTERS,
                "Kurasks",
                "Leaf-bladed weapons required — strong mid-high Slayer.",
                TeleportRequirement.combatLevel(75),
                null,
                1608, // kurask
                106,
                85,
                true,
                21,
                new TeleportDestination(2700, 10020, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                23, TeleportCategory.MONSTERS,
                "Turoths",
                "Leaf-bladed weapons required — early leaf requirement tasks.",
                TeleportRequirement.combatLevel(55),
                null,
                1627, // turoth (one variant)
                83,
                80,
                true,
                22,
                new TeleportDestination(2720, 10010, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                24, TeleportCategory.MONSTERS,
                "Abyssal Demons",
                "Iconic Slayer grind — whips and high XP.",
                TeleportRequirement.combatLevel(85),
                null,
                1615, // abyssal demon
                124,
                95,
                true,
                23,
                new TeleportDestination(3039, 4844, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                25, TeleportCategory.MONSTERS,
                "Dark Beasts",
                "Elite Slayer creature — late-game tasks.",
                TeleportRequirement.combatLevel(90),
                null,
                2783, // dark beast (commonly)
                182,
                95,
                true,
                24,
                new TeleportDestination(3416, 3567, 2) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                26, TeleportCategory.MONSTERS,
                "Spiritual Mages",
                "God Wars flavour — good rune/unique table potential.",
                TeleportRequirement.combatLevel(85),
                null,
                6232, // TODO verify spiritual mage id (varies by faction)
                115,
                90,
                true,
                25,
                new TeleportDestination(2885, 5354, 0) // TODO set real coords
        ));

        /*
         * ❄️ High-Level / Late Game Monsters (incl. 667-era picks)
         */
        defs.add(new TeleportDefinition(
                27, TeleportCategory.MONSTERS,
                "Frost Dragons",
                "Late-game dragons — strong drops and solid XP.",
                TeleportRequirement.combatLevel(100),
                null,
                51, // TODO verify frost dragon id (varies by cache)
                166,
                98,
                true,
                26,
                new TeleportDestination(2907, 3611, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                28, TeleportCategory.MONSTERS,
                "Edimmu",
                "Endgame Slayer mob from the 667 era — very high tier.",
                TeleportRequirement.combatLevel(110),
                null,
                9278, // TODO verify edimmu id
                260,
                99,
                true,
                27,
                new TeleportDestination(3050, 5000, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                29, TeleportCategory.MONSTERS,
                "Tormented Demons",
                "High-level demons (pre-EoC era) — great uniques potential.",
                TeleportRequirement.combatLevel(105),
                null,
                8349, // TODO verify tormented demon id
                250,
                99,
                true,
                28,
                new TeleportDestination(2562, 5739, 0) // TODO set real coords
        ));

        /*
         * 🧿 Nice Extras (variety + 667 flavour)
         */
        defs.add(new TeleportDefinition(
                30, TeleportCategory.MONSTERS,
                "Ice Strykewyrms",
                "667-era wyrms — strong Slayer-style grind.",
                TeleportRequirement.combatLevel(95),
                null,
                9463, // TODO verify ice strykewyrm id
                210,
                97,
                true,
                29,
                new TeleportDestination(3420, 4000, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                31, TeleportCategory.MONSTERS,
                "Desert Strykewyrms",
                "Fast-paced wyrm fight — great mid-high variety.",
                TeleportRequirement.combatLevel(85),
                null,
                9465, // TODO verify desert strykewyrm id
                180,
                95,
                true,
                30,
                new TeleportDestination(3300, 3000, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                32, TeleportCategory.MONSTERS,
                "Jungle Strykewyrms",
                "Lower-tier wyrm option — nice alternative tasks.",
                TeleportRequirement.combatLevel(75),
                null,
                9467, // TODO verify jungle strykewyrm id
                160,
                92,
                true,
                31,
                new TeleportDestination(2800, 3100, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                33, TeleportCategory.MONSTERS,
                "Armoured Zombies",
                "Tougher undead — great mid-level tanky training.",
                TeleportRequirement.combatLevel(60),
                null,
                73, // TODO verify armoured zombie id (varies by cache)
                98,
                85,
                true,
                32,
                new TeleportDestination(3096, 3493, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                34, TeleportCategory.MONSTERS,
                "Mutated Bloodvelds",
                "Stronger bloodveld variant — late Slayer flavour.",
                TeleportRequirement.combatLevel(80),
                null,
                1619, // TODO verify mutated bloodveld id/variant
                140,
                92,
                true,
                33,
                new TeleportDestination(3420, 3575, 1) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                35, TeleportCategory.MONSTERS,
                "Ice Giants",
                "Chill mid-level training with predictable damage.",
                TeleportRequirement.combatLevel(35),
                null,
                111, // ice giant
                70,
                70,
                true,
                34,
                new TeleportDestination(3056, 9580, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                36, TeleportCategory.MONSTERS,
                "Ice Warriors",
                "Low-mid alternative training spot with decent drops.",
                TeleportRequirement.combatLevel(25),
                null,
                125, // ice warrior
                57,
                65,
                true,
                35,
                new TeleportDestination(3011, 9585, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                37, TeleportCategory.MONSTERS,
                "Dagannoths",
                "Multi-combat training — great for cannons and tasks.",
                TeleportRequirement.combatLevel(60),
                null,
                2455, // dagannoth
                90,
                85,
                true,
                36,
                new TeleportDestination(2446, 10147, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                38, TeleportCategory.MONSTERS,
                "TzHaar",
                "High-level melee/range training in a classic location.",
                TeleportRequirement.combatLevel(80),
                null,
                2607, // TzHaar-Ket
                110,
                90,
                true,
                37,
                new TeleportDestination(2480, 5175, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                39, TeleportCategory.MONSTERS,
                "Brine Rats",
                "Niche slayer-ish mob — great for variety.",
                TeleportRequirement.combatLevel(45),
                null,
                3707, // brine rat
                50,
                75,
                true,
                38,
                new TeleportDestination(2690, 10125, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                40, TeleportCategory.MONSTERS,
                "Cave Horrors",
                "Black mask hunt — iconic slayer milestone.",
                TeleportRequirement.combatLevel(70),
                null,
                3209, // cave horror
                80,
                90,
                true,
                39,
                new TeleportDestination(3750, 9370, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                41, TeleportCategory.MONSTERS,
                "Waterfiends",
                "Summoning-era creature — great charm-style loot hooks.",
                TeleportRequirement.combatLevel(85),
                null,
                5361, // TODO verify waterfiend id
                115,
                95,
                true,
                40,
                new TeleportDestination(2730, 10008, 0) // TODO set real coords
        ));

        LIST = Collections.unmodifiableList(defs);
    }

    private TeleportMonsters() {}

    public static List<TeleportDefinition> list() {
        return LIST;
    }
}
