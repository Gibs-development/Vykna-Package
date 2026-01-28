package io.xeros.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.definitions.NpcStats;
import io.xeros.model.entity.npc.stats.NpcBonus;
import io.xeros.model.entity.npc.stats.NpcCombatDefinition;
import io.xeros.model.entity.npc.stats.NpcCombatSkill;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class NpcDefDataExporter {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path OUTPUT_DIRECTORY = Paths.get("data", "NpcDefData");
    private static final Path NPC_STATS_PATH = Paths.get("etc", "cfg", "npc", "npc_stats.json");
    private static final Path NPC_COMBAT_DEFS_PATH = Paths.get("etc", "cfg", "npc", "npc_combat_defs.json");
    private static final Path NPC_DEFINITIONS_PATH = Paths.get("etc", "cfg", "npc", "npc_definitions.json");

    public static void main(String[] args) throws IOException {
        Map<Integer, NpcStats> npcStats = loadNpcStats();
        Map<Integer, NpcCombatDefinition> combatDefinitions = loadCombatDefinitions();
        Map<Integer, NpcDef> npcDefinitions = loadNpcDefinitions();

        Set<Integer> allNpcIds = new HashSet<>();
        allNpcIds.addAll(npcStats.keySet());
        allNpcIds.addAll(combatDefinitions.keySet());
        allNpcIds.addAll(npcDefinitions.keySet());

        Map<Integer, ExportedNpcDefinition> exportData = new TreeMap<>();
        for (int npcId : allNpcIds) {
            ExportedNpcDefinition export = buildDefinition(npcId, npcStats.get(npcId), combatDefinitions.get(npcId), npcDefinitions.get(npcId));
            exportData.put(npcId, export);
        }

        Files.createDirectories(OUTPUT_DIRECTORY);
        for (Map.Entry<Integer, ExportedNpcDefinition> entry : exportData.entrySet()) {
            int npcId = entry.getKey();
            ExportedNpcDefinition definition = entry.getValue();
            String filename = npcId + "_" + sanitizeName(definition.identity.name) + ".json";
            Path outputPath = OUTPUT_DIRECTORY.resolve(filename);
            try (Writer writer = Files.newBufferedWriter(outputPath)) {
                GSON.toJson(definition, writer);
            }
        }

        System.out.println("Exported " + exportData.size() + " unified npc definitions to " + OUTPUT_DIRECTORY.toAbsolutePath());
    }

    private static Map<Integer, NpcStats> loadNpcStats() throws IOException {
        if (!Files.exists(NPC_STATS_PATH)) {
            return new HashMap<>();
        }
        try (Reader reader = Files.newBufferedReader(NPC_STATS_PATH)) {
            Map<String, NpcStats> raw = GSON.fromJson(reader, new TypeToken<Map<String, NpcStats>>() {}.getType());
            Map<Integer, NpcStats> mapped = new HashMap<>();
            if (raw != null) {
                for (Map.Entry<String, NpcStats> entry : raw.entrySet()) {
                    mapped.put(Integer.parseInt(entry.getKey()), entry.getValue());
                }
            }
            return mapped;
        }
    }

    private static Map<Integer, NpcCombatDefinition> loadCombatDefinitions() throws IOException {
        if (!Files.exists(NPC_COMBAT_DEFS_PATH)) {
            return new HashMap<>();
        }
        try (Reader reader = Files.newBufferedReader(NPC_COMBAT_DEFS_PATH)) {
            NpcCombatDefinition[] definitions = GSON.fromJson(reader, NpcCombatDefinition[].class);
            Map<Integer, NpcCombatDefinition> mapped = new HashMap<>();
            if (definitions != null) {
                for (NpcCombatDefinition definition : definitions) {
                    mapped.put(definition.getId(), definition);
                }
            }
            return mapped;
        }
    }

    private static Map<Integer, NpcDef> loadNpcDefinitions() throws IOException {
        if (!Files.exists(NPC_DEFINITIONS_PATH)) {
            return new HashMap<>();
        }
        try (Reader reader = Files.newBufferedReader(NPC_DEFINITIONS_PATH)) {
            Map<String, NpcDef> raw = GSON.fromJson(reader, new TypeToken<Map<String, NpcDef>>() {}.getType());
            Map<Integer, NpcDef> mapped = new HashMap<>();
            if (raw != null) {
                for (Map.Entry<String, NpcDef> entry : raw.entrySet()) {
                    mapped.put(Integer.parseInt(entry.getKey()), entry.getValue());
                }
            }
            return mapped;
        }
    }

    private static ExportedNpcDefinition buildDefinition(int npcId, NpcStats stats, NpcCombatDefinition combat, NpcDef definition) {
        ExportedNpcDefinition export = new ExportedNpcDefinition();
        export.id = npcId;
        export.identity = new Identity();
        export.identity.name = resolveName(stats, definition);
        export.identity.size = definition != null ? definition.getSize() : null;

        export.combat = new Combat();
        export.combat.hp = stats != null ? stats.getHitpoints() : null;
        export.combat.combatLevel = stats != null ? stats.getCombatLevel() : null;
        export.combat.attackSpeed = stats != null ? stats.getAttackSpeed() : null;
        if (combat != null) {
            export.combat.attackStyle = combat.getAttackStyle();
            export.combat.attackStyles = combat.getAttackStyle() != null ? java.util.List.of(combat.getAttackStyle()) : java.util.List.of();
            export.combat.aggressive = combat.isAggressive();
            export.combat.poisonous = combat.isPoisonous();
            export.combat.immunities = new Immunities();
            export.combat.immunities.poison = combat.isImmuneToPoison();
            export.combat.immunities.venom = combat.isImmuneToVenom();
            export.combat.immunities.cannon = combat.isImmuneToCannons();
            export.combat.immunities.thralls = combat.isImmuneToThralls();
        }

        export.combat.levels = new Levels();
        if (combat != null) {
            export.combat.levels.hitpoints = combat.getLevel(NpcCombatSkill.HITPOINTS);
            export.combat.levels.attack = combat.getLevel(NpcCombatSkill.ATTACK);
            export.combat.levels.strength = combat.getLevel(NpcCombatSkill.STRENGTH);
            export.combat.levels.defence = combat.getLevel(NpcCombatSkill.DEFENCE);
            export.combat.levels.magic = combat.getLevel(NpcCombatSkill.MAGIC);
            export.combat.levels.range = combat.getLevel(NpcCombatSkill.RANGE);
        } else if (stats != null) {
            export.combat.levels.hitpoints = stats.getHitpoints();
            export.combat.levels.attack = stats.getAttackLevel();
            export.combat.levels.strength = stats.getStrengthLevel();
            export.combat.levels.defence = stats.getDefenceLevel();
            export.combat.levels.magic = stats.getMagicLevel();
            export.combat.levels.range = stats.getRangeLevel();
        }

        export.combat.bonuses = new CombatBonuses();
        export.combat.bonuses.attack = new AttackBonuses();
        export.combat.bonuses.defence = new DefenceBonuses();
        export.combat.bonuses.style = new StyleBonuses();

        if (combat != null) {
            export.combat.bonuses.attack.melee = combat.getAttackBonus(NpcBonus.ATTACK_BONUS);
            export.combat.bonuses.attack.strength = combat.getAttackBonus(NpcBonus.STRENGTH_BONUS);
            export.combat.bonuses.attack.magic = combat.getAttackBonus(NpcBonus.ATTACK_MAGIC_BONUS);
            export.combat.bonuses.attack.magicDamage = combat.getAttackBonus(NpcBonus.MAGIC_STRENGTH_BONUS);
            export.combat.bonuses.attack.range = combat.getAttackBonus(NpcBonus.ATTACK_RANGE_BONUS);
            export.combat.bonuses.attack.rangeStrength = combat.getAttackBonus(NpcBonus.RANGE_STRENGTH_BONUS);

            export.combat.bonuses.defence.stab = combat.getDefenceBonus(NpcBonus.STAB_BONUS);
            export.combat.bonuses.defence.slash = combat.getDefenceBonus(NpcBonus.SLASH_BONUS);
            export.combat.bonuses.defence.crush = combat.getDefenceBonus(NpcBonus.CRUSH_BONUS);
            export.combat.bonuses.defence.range = combat.getDefenceBonus(NpcBonus.RANGE_BONUS);
            export.combat.bonuses.defence.magic = combat.getDefenceBonus(NpcBonus.MAGIC_BONUS);
        } else if (stats != null) {
            export.combat.bonuses.defence.stab = stats.getStabDef();
            export.combat.bonuses.defence.slash = stats.getSlashDef();
            export.combat.bonuses.defence.crush = stats.getCrushDef();
            export.combat.bonuses.defence.range = stats.getRangeDef();
            export.combat.bonuses.defence.magic = stats.getMagicDef();
        }

        if (stats != null) {
            export.combat.bonuses.style.stab = stats.getStab();
            export.combat.bonuses.style.slash = stats.getSlash();
            export.combat.bonuses.style.crush = stats.getCrush();
            export.combat.bonuses.style.range = stats.getRange();
            export.combat.bonuses.style.magic = stats.getMagic();
        }

        export.metadata = new Metadata();
        export.metadata.weakness = "MELEE";
        export.metadata.type = resolveNpcType(stats);

        return export;
    }

    private static String resolveName(NpcStats stats, NpcDef definition) {
        if (definition != null && definition.getName() != null) {
            return definition.getName();
        }
        if (stats != null && stats.getName() != null) {
            return stats.getName();
        }
        return "npc";
    }

    private static String resolveNpcType(NpcStats stats) {
        if (stats == null) {
            return "NONE";
        }
        if (stats.isDragon()) {
            return "DRAGON";
        }
        if (stats.isDemon()) {
            return "DEMON";
        }
        if (stats.isUndead()) {
            return "UNDEAD";
        }
        return "NONE";
    }

    private static String sanitizeName(String name) {
        if (name == null || name.isBlank()) {
            return "npc";
        }
        String sanitized = name.trim().replace(" ", "_");
        sanitized = sanitized.replaceAll("[^A-Za-z0-9_]", "");
        if (sanitized.isBlank()) {
            return "npc";
        }
        return sanitized;
    }

    private static class ExportedNpcDefinition {
        private int id;
        private Identity identity;
        private Combat combat;
        private Metadata metadata;
    }

    private static class Identity {
        private String name;
        private String examine;
        private Integer size;
    }

    private static class Combat {
        private Integer hp;
        private Integer combatLevel;
        private Integer attackSpeed;
        private java.util.List<String> attackStyles = java.util.List.of();
        private String attackStyle;
        private boolean aggressive;
        private boolean poisonous;
        private Immunities immunities;
        private Levels levels;
        private CombatBonuses bonuses;
    }

    private static class Levels {
        private Integer hitpoints;
        private Integer attack;
        private Integer strength;
        private Integer defence;
        private Integer range;
        private Integer magic;
    }

    private static class CombatBonuses {
        private AttackBonuses attack;
        private DefenceBonuses defence;
        private StyleBonuses style;
    }

    private static class AttackBonuses {
        private Integer melee;
        private Integer strength;
        private Integer magic;
        private Integer magicDamage;
        private Integer range;
        private Integer rangeStrength;
    }

    private static class DefenceBonuses {
        private Integer stab;
        private Integer slash;
        private Integer crush;
        private Integer magic;
        private Integer range;
    }

    private static class StyleBonuses {
        private Integer stab;
        private Integer slash;
        private Integer crush;
        private Integer magic;
        private Integer range;
    }

    private static class Immunities {
        private boolean poison;
        private boolean venom;
        private boolean cannon;
        private boolean thralls;
    }

    private static class Metadata {
        private String weakness;
        private String type;
    }
}
