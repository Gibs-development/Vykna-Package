package io.xeros.content.vykna_progression.categories;

import io.xeros.content.vykna_progression.ProgressionEntry;
import io.xeros.content.vykna_progression.ProgressionListDefinition;
import io.xeros.content.vykna_progression.ProgressionListType;
import io.xeros.content.vykna_progression.SpriteMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public enum ListTypeSkilling {
    ATTACK_INITIATE(3000, "Attack Initiate", "Reach level 20 Attack.", "Attack",
            "skill_level:ATTACK", 20, 3, SpriteMap.ATTACK.getId()),

    DEFENCE_DEFENDER(3001, "Defence Defender", "Reach level 20 Defence.", "Defence",
            "skill_level:DEFENCE", 20, 3, SpriteMap.DEFENCE.getId()),

    STRENGTH_TRAINEE(3002, "Strength Trainee", "Reach level 20 Strength.", "Strength",
            "skill_level:STRENGTH", 20, 3, SpriteMap.STRENGTH.getId()),

    HITPOINTS_HARDENER(3003, "Hitpoints Hardener", "Reach level 20 Hitpoints.", "Hitpoints",
            "skill_level:HITPOINTS", 20, 3, SpriteMap.HITPOINTS.getId()),

    RANGED_RANGER(3004, "Ranged Ranger", "Reach level 20 Ranged.", "Ranged",
            "skill_level:RANGED", 20, 3, SpriteMap.RANGED.getId()),

    PRAYER_ACOLYTE(3005, "Prayer Acolyte", "Reach level 20 Prayer.", "Prayer",
            "skill_level:PRAYER", 20, 3, SpriteMap.PRAYER.getId()),

    MAGIC_INITIATE(3006, "Magic Initiate", "Reach level 20 Magic.", "Magic",
            "skill_level:MAGIC", 20, 3, SpriteMap.MAGIC.getId()),

    COOKING_CHEF(3007, "Culinary Chef", "Reach level 20 Cooking.", "Cooking",
            "skill_level:COOKING", 20, 3, SpriteMap.COOKING.getId()),

    WOODCUTTING_LOGGER(3008, "Forest Logger", "Reach level 20 Woodcutting.", "Woodcutting",
            "skill_level:WOODCUTTING", 20, 3, SpriteMap.WOODCUTTING.getId()),

    FLETCHING_BOWYER(3009, "Steady Bowyer", "Reach level 20 Fletching.", "Fletching",
            "skill_level:FLETCHING", 20, 3, SpriteMap.FLETCHING.getId()),

    FISHING_ANGLER(3010, "River Angler", "Reach level 20 Fishing.", "Fishing",
            "skill_level:FISHING", 20, 3, SpriteMap.FISHING.getId()),

    FIREMAKING_SPARK(3011, "Spark Keeper", "Reach level 20 Firemaking.", "Firemaking",
            "skill_level:FIREMAKING", 20, 3, SpriteMap.FIREMAKING.getId()),

    CRAFTING_ARTISAN(3012, "Crafting Artisan", "Reach level 20 Crafting.", "Crafting",
            "skill_level:CRAFTING", 20, 3, SpriteMap.CRAFTING.getId()),

    SMITHING_FORGER(3013, "Metal Forger", "Reach level 20 Smithing.", "Smithing",
            "skill_level:SMITHING", 20, 3, SpriteMap.SMITHING.getId()),

    MINING_MINER(3014, "Ore Miner", "Reach level 20 Mining.", "Mining",
            "skill_level:MINING", 20, 3, SpriteMap.MINING.getId()),

    HERBLORE_HERBALIST(3015, "Herbalist", "Reach level 20 Herblore.", "Herblore",
            "skill_level:HERBLORE", 20, 3, SpriteMap.HERBLORE.getId()),

    AGILITY_RUNNER(3016, "Agile Runner", "Reach level 20 Agility.", "Agility",
            "skill_level:AGILITY", 20, 3, SpriteMap.AGILITY.getId()),

    THIEVING_SNEAK(3017, "Silent Sneak", "Reach level 20 Thieving.", "Thieving",
            "skill_level:THIEVING", 20, 3, SpriteMap.THIEVING.getId()),

    SLAYER_TRAINEE(3018, "Slayer Trainee", "Reach level 20 Slayer.", "Slayer",
            "skill_level:SLAYER", 20, 3, SpriteMap.SLAYER.getId()),

    FARMING_PLANTER(3019, "Seed Planter", "Reach level 20 Farming.", "Farming",
            "skill_level:FARMING", 20, 3, SpriteMap.FARMING.getId()),

    RUNECRAFT_APPRENTICE(3020, "Rune Apprentice", "Reach level 20 Runecrafting.", "Runecrafting",
            "skill_level:RUNECRAFTING", 20, 3, SpriteMap.RUNECRAFTING.getId()),

    HUNTER_TRACKER(3021, "Beast Tracker", "Reach level 20 Hunter.", "Hunter",
            "skill_level:HUNTER", 20, 3, SpriteMap.HUNTER.getId()),

    BONE_BURIER(3022, "Bone Burier", "Bury 10 bones.", "Prayer",
            "bones_buried", 10, 3, SpriteMap.PRAYER.getId());

    private final int entryId;
    private final String name;
    private final String description;
    private final String subcategory;
    private final String requirementKey;
    private final int requirementTarget;
    private final int points;
    private final int spriteIndex;

    ListTypeSkilling(int entryId, String name, String description, String subcategory,
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
        return new ProgressionEntry(entryId, name, description, ProgressionListType.SKILLS.getId(),
                subcategory, points, requirementKey, requirementTarget, spriteIndex);
    }

    public static ProgressionListDefinition getDefinition() {
        List<ProgressionEntry> entries = new ArrayList<>();
        Set<String> subcategories = new TreeSet<>();
        for (ListTypeSkilling entry : values()) {
            entries.add(entry.toEntry());
            subcategories.add(entry.subcategory);
        }
        return new ProgressionListDefinition(
                ProgressionListType.SKILLS.getId(),
                ProgressionListType.SKILLS.getDisplayName(),
                new ArrayList<>(subcategories),
                entries
        );
    }
}
