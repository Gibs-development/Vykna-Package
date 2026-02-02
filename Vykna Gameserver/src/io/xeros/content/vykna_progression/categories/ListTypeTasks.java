package io.xeros.content.vykna_progression.categories;

import io.xeros.content.vykna_progression.ProgressionEntry;
import io.xeros.content.vykna_progression.ProgressionListDefinition;
import io.xeros.content.vykna_progression.ProgressionListType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public enum ListTypeTasks {
    EARLY_CHECKIN(1000, "Early Check-in", "Open the Vykna Progression menu.", "General",
            "open_progression", 1, 1, 1),
    TOTAL_LEVEL_250(1001, "Rising Adventurer", "Reach total level 250.", "Milestones",
            "total_level", 250, 2, 2),
    TOTAL_LEVEL_500(1002, "Seasoned Adventurer", "Reach total level 500.", "Milestones",
            "total_level", 500, 3, 3),
    TOTAL_LEVEL_750(1003, "Veteran Adventurer", "Reach total level 750.", "Milestones",
            "total_level", 750, 4, 4),
    TOTAL_LEVEL_1000(1004, "Elite Adventurer", "Reach total level 1000.", "Milestones",
            "total_level", 1000, 5, 1),
    TOTAL_LEVEL_1250(1005, "Master Adventurer", "Reach total level 1250.", "Milestones",
            "total_level", 1250, 6, 2),
    TOTAL_LEVEL_1500(1006, "Grandmaster Adventurer", "Reach total level 1500.", "Milestones",
            "total_level", 1500, 8, 3),
    TOTAL_LEVEL_1750(1007, "Mythic Adventurer", "Reach total level 1750.", "Milestones",
            "total_level", 1750, 10, 4),
    TOTAL_LEVEL_2000(1008, "Legendary Adventurer", "Reach total level 2000.", "Milestones",
            "total_level", 2000, 12, 1),
    TOTAL_XP_1M(1009, "Path of Experience", "Reach 1,000,000 total XP.", "Milestones",
            "total_xp", 1_000_000, 2, 2),
    TOTAL_XP_10M(1010, "Deep Experience", "Reach 10,000,000 total XP.", "Milestones",
            "total_xp", 10_000_000, 3, 3),
    TOTAL_XP_25M(1011, "Seasoned Experience", "Reach 25,000,000 total XP.", "Milestones",
            "total_xp", 25_000_000, 4, 4),
    TOTAL_XP_50M(1012, "Veteran Experience", "Reach 50,000,000 total XP.", "Milestones",
            "total_xp", 50_000_000, 5, 1),
    TOTAL_XP_75M(1013, "Masterful Experience", "Reach 75,000,000 total XP.", "Milestones",
            "total_xp", 75_000_000, 6, 2),
    TOTAL_XP_100M(1014, "Grandmaster Experience", "Reach 100,000,000 total XP.", "Milestones",
            "total_xp", 100_000_000, 8, 3),
    TOTAL_XP_150M(1015, "Mythic Experience", "Reach 150,000,000 total XP.", "Milestones",
            "total_xp", 150_000_000, 10, 4),
    TOTAL_XP_200M(1016, "Legendary Experience", "Reach 200,000,000 total XP.", "Milestones",
            "total_xp", 200_000_000, 12, 1),
    VISIT_VARROCK(1017, "Visit Varrock", "Travel to Varrock.", "Travel",
            "visit:varrock", 1, 1, 2),
    VISIT_FALADOR(1018, "Visit Falador", "Travel to Falador.", "Travel",
            "visit:falador", 1, 1, 3),
    VISIT_LUMBRIDGE(1019, "Visit Lumbridge", "Travel to Lumbridge.", "Travel",
            "visit:lumbridge", 1, 1, 4),
    VISIT_DRAYNOR(1020, "Visit Draynor", "Travel to Draynor Village.", "Travel",
            "visit:draynor", 1, 1, 1),
    VISIT_AL_KHARID(1021, "Visit Al Kharid", "Travel to Al Kharid.", "Travel",
            "visit:al_kharid", 1, 1, 2),
    VISIT_ARDOUGNE(1022, "Visit Ardougne", "Travel to Ardougne.", "Travel",
            "visit:ardougne", 1, 2, 3),
    VISIT_SEERS(1023, "Visit Seers' Village", "Travel to Seers' Village.", "Travel",
            "visit:seers", 1, 2, 4),
    VISIT_CATHERBY(1024, "Visit Catherby", "Travel to Catherby.", "Travel",
            "visit:catherby", 1, 2, 1),
    VISIT_TAVERLY(1025, "Visit Taverley", "Travel to Taverley.", "Travel",
            "visit:taverly", 1, 2, 2),
    VISIT_VARROCK_MUSEUM(1049, "Visit Varrock Museum", "Travel to Varrock Museum.", "Travel",
            "visit:varrock", 1, 2, 2),
    QUEST_IMP_CATCHER(1050, "Complete Imp Catcher", "Complete the Imp Catcher quest.", "Quests",
            "quest_complete:imp_catcher", 1, 4, 1),
    QUEST_COURIER_FAVOR(1051, "Complete Courier's Favor", "Complete the Courier's Favor quest.", "Quests",
            "quest_complete:courier_favor", 1, 4, 2),
    QUEST_ONE_SMALL_FAVOUR(1052, "Complete One Small Favour", "Complete the One Small Favour quest.", "Quests",
            "quest_complete:one_small_favour", 1, 5, 3),
    QUEST_DESERT_TREASURE(1053, "Complete Desert Treasure", "Complete the Desert Treasure quest.", "Quests",
            "quest_complete:desert_treasure", 1, 6, 4),
    QUEST_WATERFALL(1054, "Complete Waterfall Quest", "Complete the Waterfall Quest.", "Quests",
            "quest_complete:waterfall_quest", 1, 4, 1),
    EQUIP_COMMON(1055, "Equip a Common Item", "Equip a common rarity item.", "Equipment",
            "equip_rarity:common", 1, 2, 1),
    EQUIP_UNCOMMON(1056, "Equip an Uncommon Item", "Equip an uncommon rarity item.", "Equipment",
            "equip_rarity:uncommon", 1, 3, 2),
    EQUIP_RARE(1057, "Equip a Rare Item", "Equip a rare rarity item.", "Equipment",
            "equip_rarity:rare", 1, 4, 3),
    EQUIP_EPIC(1058, "Equip an Epic Item", "Equip an epic rarity item.", "Equipment",
            "equip_rarity:epic", 1, 5, 4),
    EQUIP_MYTHIC(1059, "Equip a Mythic Item", "Equip a mythic rarity item.", "Equipment",
            "equip_rarity:mythic", 1, 6, 1);

    private final int entryId;
    private final String name;
    private final String description;
    private final String subcategory;
    private final String requirementKey;
    private final int requirementTarget;
    private final int points;
    private final int spriteIndex;

    ListTypeTasks(int entryId, String name, String description, String subcategory,
                  String requirementKey, int requirementTarget, int points, int spriteIndex) {
        this.entryId = entryId;
        this.name = name;
        this.description = description;
        this.subcategory = subcategory;
        this.requirementKey = requirementKey;
        this.requirementTarget = requirementTarget;
        this.points = points;
        this.spriteIndex = spriteIndex;
    }

    public ProgressionEntry toEntry() {
        return new ProgressionEntry(entryId, name, description, ProgressionListType.TASKS.getId(),
                subcategory, points, requirementKey, requirementTarget, spriteIndex);
    }

    public static ProgressionListDefinition getDefinition() {
        List<ProgressionEntry> entries = new ArrayList<>();
        Set<String> subcategories = new TreeSet<>();
        for (ListTypeTasks entry : values()) {
            entries.add(entry.toEntry());
            subcategories.add(entry.subcategory);
        }
        return new ProgressionListDefinition(
                ProgressionListType.TASKS.getId(),
                ProgressionListType.TASKS.getDisplayName(),
                new ArrayList<>(subcategories),
                entries
        );
    }
}
