package io.xeros.content.vykna_teleports.data;

import io.xeros.content.vykna_teleports.model.TeleportCategory;
import io.xeros.content.vykna_teleports.model.TeleportDefinition;
import io.xeros.content.vykna_teleports.model.TeleportDestination;
import io.xeros.content.vykna_teleports.model.TeleportRequirement;
import io.xeros.model.entity.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Monsters category teleport definitions.
 * Keep these grouped so editing/expanding is painless.
 *
 * NOTE:
 * - Destination coords are placeholders — set to your actual teleport locations.
 * - npcId values are placeholders — set them to your actual npc IDs later.
 * - Skill id 18 = Slayer (as used elsewhere in this system).
 */
public final class TeleportMonsters {

    private static final int SKILL_SLAYER = 18;

    private static final List<TeleportDefinition> LIST;
    static {
        List<TeleportDefinition> defs = new ArrayList<>();

        /*
         * ⭐ Featured / Utility
         * Keep this as ID=1 so it always pins nicely if you sort by id or "featured".
         */
        defs.add(new TeleportDefinition(
                1, TeleportCategory.MONSTERS,
                "Slayer Task",
                "Teleport straight to your current Slayer assignment location.",
                TeleportRequirement.of(
                        null, // no combat requirement
                        List.of(
                                new TeleportRequirement.SkillReq(SKILL_SLAYER, 1) // Slayer (entry-level)
                        )
                ),
                "Death to Dragith",
                6797, // TODO npcId
                126,
                99,
                true,
                0, // headIconIndex
                new TeleportDestination(3000, 3000, 0) // TODO set real coords
        ));

        /*
         * 🪨 Early Training / General Dungeons (low → high)
         * These are “monster hubs” more than Slayer-gated monsters.
         */
        defs.add(new TeleportDefinition(
                2, TeleportCategory.MONSTERS,
                "Warped Crypt",
                "Low-level undead training. A salve amulet will help.",
                TeleportRequirement.of(
                        1,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 1))
                ),
                null,
                1103, // TODO npcId
                64,
                50,
                false,
                1,
                new TeleportDestination(3179, 5193, 0)
        ));

        defs.add(new TeleportDefinition(
                3, TeleportCategory.MONSTERS,
                "Edgeville Dungeon",
                "Classic dungeon crawl with starter-to-mid monsters and bones.",
                TeleportRequirement.of(
                        1,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 1))
                ),
                null,
                70, // TODO npcId
                22,
                60,
                false,
                2,
                new TeleportDestination(3253, 3266, 0)
        ));

        defs.add(new TeleportDefinition(
                4, TeleportCategory.MONSTERS,
                "Taverley Dungeon",
                "A staple multi-room dungeon for early combat progression.",
                TeleportRequirement.of(
                        10,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 1))
                ),
                null,
                252, // TODO npcId
                227,
                40,
                false,
                3,
                new TeleportDestination(2675, 3710, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                5, TeleportCategory.MONSTERS,
                "Brimhaven Dungeon",
                "Mid-level dungeon hub with a wide mix of monsters.",
                TeleportRequirement.of(
                        15,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 1))
                ),
                null,
                274, // TODO npcId
                246,
                55,
                false,
                4,
                new TeleportDestination(1700, 3460, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                7, TeleportCategory.MONSTERS,
                "Fremennik Slayer Cave",
                "A classic Slayer cave with early-to-mid assignments.",
                TeleportRequirement.of(
                        5,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 1))
                ),
                null,
                410, // TODO npcId
                106,
                55,
                false,
                6,
                new TeleportDestination(3420, 3536, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                6, TeleportCategory.MONSTERS,
                "Kourend Catacombs",
                "Massive multi-monster dungeon for training and tasks.",
                TeleportRequirement.of(
                        20,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 1))
                ),
                null,
                7275, // TODO npcId
                318,
                65,
                false,
                5,
                new TeleportDestination(3550, 9948, 0) // TODO set real coords
        ));

        /*
         * 🕷️ Daemonheim / Dungeoneering-era Slayer Creatures (low → high)
         * These fill the “gaps” so you’ve got a proper progression ladder.
         */
        defs.add(new TeleportDefinition(
                29, TeleportCategory.MONSTERS,
                "Night Spiders",
                "Daemonheim Slayer creature. Known for the Shadow silk hood.",
                TeleportRequirement.of(
                        35,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 41))
                ),
                null,
                1105,
                73,
                70,
                true,
                28,
                new TeleportDestination(3000, 5000, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                30, TeleportCategory.MONSTERS,
                "Jellies",
                "Daemonheim Slayer creature with useful boot uniques.",
                TeleportRequirement.of(
                        45,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 52))
                ),
                null,
                169, // TODO npcId
                63,
                75,
                true,
                29,
                new TeleportDestination(3000, 5005, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                31, TeleportCategory.MONSTERS,
                "Spiritual Guardians",
                "Daemonheim Slayer creature. Potential ward/unique drops.",
                TeleportRequirement.of(
                        55,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 63))
                ),
                null,
                1106,
                82,
                80,
                true,
                30,
                new TeleportDestination(3000, 5010, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                13, TeleportCategory.MONSTERS,
                "Seekers",
                "Daemonheim Slayer creature (AoE magic). Chase the Seeker's charm.",
                TeleportRequirement.of(
                        50,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 71))
                ),
                null,
                1107,
                86,
                75,
                true,
                12,
                new TeleportDestination(2402, 9782, 0) // TODO set real coords
        ));

        /*
         * 🔥 Mid-Level Slayer Monsters (low → high)
         */
        defs.add(new TeleportDefinition(
                8, TeleportCategory.MONSTERS,
                "Cave Horrors",
                "Slayer classic — bring a Witchwood icon for the Black mask hunt.",
                TeleportRequirement.of(
                        20,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 58))
                ),
                null,
                117, // TODO npcId
                28,
                35,
                true,
                7,
                new TeleportDestination(3116, 9830, 0)
        ));

        defs.add(new TeleportDefinition(
                23, TeleportCategory.MONSTERS,
                "Turoths",
                "Leaf-bladed weapon required. Great mid-Slayer task.",
                TeleportRequirement.of(
                        55,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 55))
                ),
                null,
                1627, // TODO npcId
                83,
                80,
                true,
                22,
                new TeleportDestination(2720, 10010, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                17, TeleportCategory.MONSTERS,
                "Aberrant Spectres",
                "Requires nose peg/Slayer helm. Solid herbs and alchs.",
                TeleportRequirement.of(
                        60,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 60))
                ),
                null,
                1604, // TODO npcId
                90,
                80,
                true,
                16,
                new TeleportDestination(3437, 3572, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                15, TeleportCategory.MONSTERS,
                "Dust Devils",
                "Burst/Barrage hotspot — fast kills and strong Slayer XP.",
                TeleportRequirement.of(
                        55,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 65))
                ),
                null,
                1624, // TODO npcId
                111,
                80,
                true,
                14,
                new TeleportDestination(2900, 9800, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                22, TeleportCategory.MONSTERS,
                "Skeletal Wyverns",
                "Wyvern shield/elemental protection recommended. Strong mid-high Slayer.",
                TeleportRequirement.of(
                        75,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 72))
                ),
                null,
                1608, // TODO npcId
                106,
                85,
                true,
                21,
                new TeleportDestination(2700, 10020, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                9, TeleportCategory.MONSTERS,
                "Jungle Strykewyrms",
                "Task-only. Chase the Hexcrest and solid Slayer XP.",
                TeleportRequirement.of(
                        30,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 73))
                ),
                null,
                1108, // TODO npcId
                42,
                45,
                true,
                8,
                new TeleportDestination(3140, 9872, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                10, TeleportCategory.MONSTERS,
                "Desert Strykewyrms",
                "Task-only. Chase the Focus sight and solid Slayer XP.",
                TeleportRequirement.of(
                        40,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 73))
                ),
                null,
                1109, // TODO npcId
                75,
                60,
                true,
                9,
                new TeleportDestination(2359, 5215, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                12, TeleportCategory.MONSTERS,
                "Ice Strykewyrms",
                "Task-only. High damage — bring proper gear and prayers.",
                TeleportRequirement.of(
                        60,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 73))
                ),
                null,
                1110, // TODO npcId
                92,
                70,
                true,
                11,
                new TeleportDestination(3287, 3885, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                19, TeleportCategory.MONSTERS,
                "Gargoyles",
                "Consistent GP with alchs — bring a rock hammer.",
                TeleportRequirement.of(
                        75,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 75))
                ),
                null,
                1610, // TODO npcId
                111,
                85,
                true,
                18,
                new TeleportDestination(3436, 3534, 2) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                24, TeleportCategory.MONSTERS,
                "Aquanites",
                "High-value task with steady drops and strong Slayer XP.",
                TeleportRequirement.of(
                        85,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 78))
                ),
                null,
                1127, // TODO npcId
                124,
                95,
                true,
                23,
                new TeleportDestination(3039, 4844, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                20, TeleportCategory.MONSTERS,
                "Nechryaels",
                "Excellent Slayer XP — great for multi-kill setups.",
                TeleportRequirement.of(
                        80,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 80))
                ),
                null,
                1613, // TODO npcId
                115,
                90,
                true,
                19,
                new TeleportDestination(3440, 3563, 2) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                21, TeleportCategory.MONSTERS,
                "Glacors",
                "Tough solo fights with valuable uniques. High attention required.",
                TeleportRequirement.of(
                        55,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 75)) // Kuradal-tier content
                ),
                null,
                1112, // TODO npcId
                76,
                75,
                true,
                20,
                new TeleportDestination(3422, 3560, 1) // TODO set real coords
        ));

        /*
         * ❄️ High-Level / Late Game
         */
        defs.add(new TeleportDefinition(
                11, TeleportCategory.MONSTERS,
                "Frost Dragons",
                "High-risk prayer training — antifire and protection recommended.",
                TeleportRequirement.of(
                        40,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 85))
                ),
                null,
                1113, // TODO npcId
                79,
                65,
                true,
                10,
                new TeleportDestination(2930, 9795, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                16, TeleportCategory.MONSTERS,
                "Abyssal Demons",
                "Iconic Slayer grind — fast XP and endgame drop tables.",
                TeleportRequirement.of(
                        70,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 85))
                ),
                null,
                84, // TODO npcId
                172,
                85,
                true,
                15,
                new TeleportDestination(2869, 9778, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                18, TeleportCategory.MONSTERS,
                "Edimmu",
                "Daemonheim Slayer apex-tier. Task-style grind with big rewards.",
                TeleportRequirement.of(
                        70,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 90))
                ),
                null,
                1125, // TODO npcId
                105,
                85,
                true,
                17,
                new TeleportDestination(3238, 9364, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                14, TeleportCategory.MONSTERS,
                "Kal'gerion Demons",
                "High-tier demon fights (Daemonheim era). Bring strong supplies.",
                TeleportRequirement.of(
                        40,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 90))
                ),
                null,
                1124, // TODO npcId
                79,
                75,
                true,
                13,
                new TeleportDestination(2980, 3614, 0)
        ));

        defs.add(new TeleportDefinition(
                25, TeleportCategory.MONSTERS,
                "Tormented Demons",
                "Elite endgame demons — expect heavy damage and strong drops.",
                TeleportRequirement.of(
                        90,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 1))
                ),
                null,
                1123, // TODO npcId
                182,
                95,
                true,
                24,
                new TeleportDestination(3416, 3567, 2) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                26, TeleportCategory.MONSTERS,
                "Living Rock Creatures",
                "Tanky monsters with solid loot potential — good task filler.",
                TeleportRequirement.of(
                        85,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 1))
                ),
                null,
                1118, // TODO npcId
                115,
                90,
                true,
                25,
                new TeleportDestination(2885, 5354, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                27, TeleportCategory.MONSTERS,
                "Lava Strykewyrms",
                "High-risk variant — stronger danger, stronger rewards.",
                TeleportRequirement.of(
                        100,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 73))
                ),
                null,
                1119, // TODO npcId
                166,
                98,
                true,
                26,
                new TeleportDestination(2907, 3611, 0) // TODO set real coords
        ));

        defs.add(new TeleportDefinition(
                28, TeleportCategory.MONSTERS,
                "Dreadnauts",
                "Warped-floor bruisers (Daemonheim era). True endgame challenge.",
                TeleportRequirement.of(
                        110,
                        List.of(new TeleportRequirement.SkillReq(SKILL_SLAYER, 95))
                ),
                null,
                1120, // TODO npcId
                260,
                99,
                true,
                27,
                new TeleportDestination(3050, 5000, 0) // TODO set real coords
        ));

        LIST = Collections.unmodifiableList(defs);
    }

    private TeleportMonsters() {}

    public static List<TeleportDefinition> list() {
        return LIST;
    }
}
