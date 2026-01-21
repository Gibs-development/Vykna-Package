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
    VISIT_KARAMJA(1026, "Visit Karamja", "Travel to Karamja.", "Travel",
            "visit:karamja", 1, 2, 3),
    VISIT_BRIMHAVEN(1027, "Visit Brimhaven", "Travel to Brimhaven.", "Travel",
            "visit:brimhaven", 1, 2, 4),
    VISIT_CANIFIS(1028, "Visit Canifis", "Travel to Canifis.", "Travel",
            "visit:canifis", 1, 2, 1),
    VISIT_RELLEKKA(1029, "Visit Rellekka", "Travel to Rellekka.", "Travel",
            "visit:rellekka", 1, 2, 2),
    VISIT_YANILLE(1030, "Visit Yanille", "Travel to Yanille.", "Travel",
            "visit:yanille", 1, 2, 3),
    VISIT_GNOME_STRONGHOLD(1031, "Visit Gnome Stronghold", "Travel to the Gnome Stronghold.", "Travel",
            "visit:gnome_stronghold", 1, 2, 4),
    VISIT_DESERT(1032, "Visit the Desert", "Travel to the Kharidian Desert.", "Travel",
            "visit:desert", 1, 2, 1),
    VISIT_FELDIP_HILLS(1033, "Visit Feldip Hills", "Travel to Feldip Hills.", "Travel",
            "visit:feldip_hills", 1, 2, 2),
    VISIT_APE_ATOLL(1034, "Visit Ape Atoll", "Travel to Ape Atoll.", "Travel",
            "visit:ape_atoll", 1, 3, 3),
    VISIT_LUNAR_ISLE(1035, "Visit Lunar Isle", "Travel to Lunar Isle.", "Travel",
            "visit:lunar_isle", 1, 3, 4),
    VISIT_FREMENNIK_ISLES(1036, "Visit Fremennik Isles", "Travel to the Fremennik Isles.", "Travel",
            "visit:fremennik_isles", 1, 3, 1),
    VISIT_WATERBIRTH(1037, "Visit Waterbirth Island", "Travel to Waterbirth Island.", "Travel",
            "visit:waterbirth", 1, 3, 2),
    VISIT_MISCELLANIA(1038, "Visit Miscellania", "Travel to Miscellania.", "Travel",
            "visit:miscellania", 1, 3, 3),
    VISIT_TZHAAR(1039, "Visit TzHaar City", "Travel to the TzHaar City.", "Travel",
            "visit:tzhaar", 1, 3, 4),
    VISIT_ZEAH(1040, "Visit Zeah", "Travel to Zeah.", "Travel",
            "visit:zeah", 1, 3, 1),
    VISIT_LLETYA(1041, "Visit Lletya", "Travel to Lletya.", "Travel",
            "visit:lletya", 1, 3, 2),
    VISIT_BANDIT_CAMP(1042, "Visit Bandit Camp", "Travel to the Bandit Camp.", "Travel",
            "visit:bandit_camp", 1, 3, 3),
    VISIT_TZHAAR_CAVES(1043, "Explore TzHaar Depths", "Enter the TzHaar City caves.", "Travel",
            "visit:tzhaar", 1, 3, 4),
    VISIT_GRAND_EXCHANGE(1044, "Visit the Grand Exchange", "Travel to the Grand Exchange.", "Travel",
            "visit:varrock", 1, 2, 1),
    VISIT_CANIFIS_SWAMP(1045, "Enter Morytania", "Travel to the Morytania swamp.", "Travel",
            "visit:canifis", 1, 2, 2),
    VISIT_TAVERLY_DUNGEON(1046, "Enter Taverley Dungeon", "Travel to Taverley Dungeon.", "Travel",
            "visit:taverly", 1, 2, 3),
    VISIT_BRIMHAVEN_DUNGEON(1047, "Enter Brimhaven Dungeon", "Travel to Brimhaven Dungeon.", "Travel",
            "visit:brimhaven", 1, 2, 4),
    VISIT_KARAMJA_VOLCANO(1048, "Visit Karamja Volcano", "Travel to Karamja Volcano.", "Travel",
            "visit:karamja", 1, 2, 1),
    VISIT_VARROCK_MUSEUM(1049, "Visit Varrock Museum", "Travel to Varrock Museum.", "Travel",
            "visit:varrock", 1, 2, 2);

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
