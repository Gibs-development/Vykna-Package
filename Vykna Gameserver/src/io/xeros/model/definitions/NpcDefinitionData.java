package io.xeros.model.definitions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.xeros.model.entity.npc.stats.NpcBonus;
import io.xeros.model.entity.npc.stats.NpcCombatDefinition;
import io.xeros.model.entity.npc.stats.NpcCombatSkill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class NpcDefinitionData {

    private static final Logger log = LoggerFactory.getLogger(NpcDefinitionData.class);
    private static final Gson GSON = new GsonBuilder().create();
    private static final Path DATA_DIRECTORY = Paths.get("data", "NpcDefData");

    private static final Map<Integer, NpcDefinitionData> DEFINITIONS = new HashMap<>();

    public static void load() throws IOException {
        DEFINITIONS.clear();
        if (!Files.isDirectory(DATA_DIRECTORY)) {
            log.warn("NpcDefData directory not found at {}. Unified NPC data will be skipped.", DATA_DIRECTORY.toAbsolutePath());
            return;
        }

        try (Stream<Path> paths = Files.list(DATA_DIRECTORY)) {
            paths.filter(path -> path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".json"))
                    .forEach(NpcDefinitionData::loadDefinitionFile);
        }

        log.info("Loaded {} unified NPC definitions from {}.", DEFINITIONS.size(), DATA_DIRECTORY.toAbsolutePath());
    }

    public static NpcDefinitionData forId(int npcId) {
        return DEFINITIONS.get(npcId);
    }

    public static Map<Integer, NpcDefinitionData> getDefinitions() {
        return Collections.unmodifiableMap(DEFINITIONS);
    }

    private static void loadDefinitionFile(Path path) {
        String filename = path.getFileName().toString();
        String baseName = filename.substring(0, filename.length() - ".json".length());
        String[] tokens = baseName.split("_", 2);
        if (tokens.length == 0) {
            log.warn("NpcDefData file {} does not contain an id prefix.", filename);
            return;
        }

        int npcId;
        try {
            npcId = Integer.parseInt(tokens[0]);
        } catch (NumberFormatException ex) {
            log.warn("NpcDefData file {} has invalid id prefix '{}'.", filename, tokens[0]);
            return;
        }

        try (Reader reader = Files.newBufferedReader(path)) {
            NpcDefinitionData definition = GSON.fromJson(reader, NpcDefinitionData.class);
            if (definition == null) {
                log.warn("NpcDefData file {} did not contain a valid definition.", filename);
                return;
            }
            if (definition.id != 0 && definition.id != npcId) {
                log.warn("NpcDefData id mismatch for {}: filename id {} != json id {}.", filename, npcId, definition.id);
            }
            definition.id = npcId;
            DEFINITIONS.put(npcId, definition);
        } catch (IOException ex) {
            log.warn("Failed to load NpcDefData file {}.", filename, ex);
        }
    }

    private int id;
    private Identity identity;
    private Combat combat;
    private Metadata metadata;

    private transient NpcStats cachedStats;
    private transient NpcCombatDefinition cachedCombatDefinition;

    public int getId() {
        return id;
    }

    public Identity getIdentity() {
        return identity;
    }

    public Combat getCombat() {
        return combat;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public NpcStats toNpcStats() {
        if (cachedStats != null) {
            return cachedStats;
        }

        Identity identity = Optional.ofNullable(this.identity).orElse(new Identity());
        Combat combat = Optional.ofNullable(this.combat).orElse(new Combat());
        Levels levels = Optional.ofNullable(combat.levels).orElse(new Levels());
        CombatBonuses bonuses = Optional.ofNullable(combat.bonuses).orElse(new CombatBonuses());
        StyleBonuses styleBonuses = Optional.ofNullable(bonuses.style).orElse(new StyleBonuses());
        DefenceBonuses defenceBonuses = Optional.ofNullable(bonuses.defence).orElse(new DefenceBonuses());
        AttackBonuses attackBonuses = Optional.ofNullable(bonuses.attack).orElse(new AttackBonuses());
        Immunities immunities = Optional.ofNullable(combat.immunities).orElse(new Immunities());

        int hitpoints = firstNonZero(combat.hp, levels.hitpoints);

        NpcStatsBuilder builder = NpcStats.builder()
                .setName(identity.name)
                .setHitpoints(hitpoints)
                .setCombatLevel(defaultIfNull(combat.combatLevel, 0))
                .setAttackSpeed(defaultIfNull(combat.attackSpeed, 4))
                .setAttackLevel(defaultIfNull(levels.attack, 1))
                .setStrengthLevel(defaultIfNull(levels.strength, 1))
                .setDefenceLevel(defaultIfNull(levels.defence, 1))
                .setRangeLevel(defaultIfNull(levels.range, 1))
                .setMagicLevel(defaultIfNull(levels.magic, 1))
                .setStab(defaultIfNull(styleBonuses.stab, 0))
                .setSlash(defaultIfNull(styleBonuses.slash, 0))
                .setCrush(defaultIfNull(styleBonuses.crush, 0))
                .setRange(defaultIfNull(styleBonuses.range, 0))
                .setMagic(defaultIfNull(styleBonuses.magic, 0))
                .setStabDef(defaultIfNull(defenceBonuses.stab, 0))
                .setSlashDef(defaultIfNull(defenceBonuses.slash, 0))
                .setCrushDef(defaultIfNull(defenceBonuses.crush, 0))
                .setRangeDef(defaultIfNull(defenceBonuses.range, 0))
                .setMagicDef(defaultIfNull(defenceBonuses.magic, 0))
                .setBonusAttack(defaultIfNull(attackBonuses.melee, 0))
                .setBonusStrength(defaultIfNull(attackBonuses.strength, 0))
                .setBonusRangeStrength(defaultIfNull(attackBonuses.rangeStrength, 0))
                .setBonusMagicDamage(defaultIfNull(attackBonuses.magicDamage, 0))
                .setPoisonImmune(immunities.poison)
                .setVenomImmune(immunities.venom);

        Metadata metadata = Optional.ofNullable(this.metadata).orElse(new Metadata());
        if (metadata.type != null) {
            builder.setDragon(metadata.type == NpcType.DRAGON)
                    .setDemon(metadata.type == NpcType.DEMON)
                    .setUndead(metadata.type == NpcType.UNDEAD);
        }

        cachedStats = builder.createNpcStats();
        return cachedStats;
    }

    public NpcCombatDefinition toNpcCombatDefinition() {
        if (cachedCombatDefinition != null) {
            return cachedCombatDefinition;
        }

        Combat combat = Optional.ofNullable(this.combat).orElse(new Combat());
        Levels levels = Optional.ofNullable(combat.levels).orElse(new Levels());
        CombatBonuses bonuses = Optional.ofNullable(combat.bonuses).orElse(new CombatBonuses());
        AttackBonuses attackBonuses = Optional.ofNullable(bonuses.attack).orElse(new AttackBonuses());
        DefenceBonuses defenceBonuses = Optional.ofNullable(bonuses.defence).orElse(new DefenceBonuses());
        Immunities immunities = Optional.ofNullable(combat.immunities).orElse(new Immunities());

        NpcCombatDefinition definition = new NpcCombatDefinition(id);
        definition.setAttackSpeed(defaultIfNull(combat.attackSpeed, 4));
        definition.setAttackStyle(combat.resolveAttackStyle());
        definition.setAggressive(combat.aggressive);
        definition.setPoisonous(combat.poisonous);
        definition.setImmuneToPoison(immunities.poison);
        definition.setImmuneToVenom(immunities.venom);
        definition.setImmuneToCannons(immunities.cannon);
        definition.setImmuneToThralls(immunities.thralls);

        definition.setLevel(NpcCombatSkill.HITPOINTS, firstNonZero(combat.hp, levels.hitpoints, 1));
        definition.setLevel(NpcCombatSkill.ATTACK, defaultIfNull(levels.attack, 1));
        definition.setLevel(NpcCombatSkill.STRENGTH, defaultIfNull(levels.strength, 1));
        definition.setLevel(NpcCombatSkill.DEFENCE, defaultIfNull(levels.defence, 1));
        definition.setLevel(NpcCombatSkill.MAGIC, defaultIfNull(levels.magic, 1));
        definition.setLevel(NpcCombatSkill.RANGE, defaultIfNull(levels.range, 1));

        definition.setAttackBonus(NpcBonus.ATTACK_BONUS, defaultIfNull(attackBonuses.melee, 0));
        definition.setAttackBonus(NpcBonus.STRENGTH_BONUS, defaultIfNull(attackBonuses.strength, 0));
        definition.setAttackBonus(NpcBonus.ATTACK_MAGIC_BONUS, defaultIfNull(attackBonuses.magic, 0));
        definition.setAttackBonus(NpcBonus.MAGIC_STRENGTH_BONUS, defaultIfNull(attackBonuses.magicDamage, 0));
        definition.setAttackBonus(NpcBonus.ATTACK_RANGE_BONUS, defaultIfNull(attackBonuses.range, 0));
        definition.setAttackBonus(NpcBonus.RANGE_STRENGTH_BONUS, defaultIfNull(attackBonuses.rangeStrength, 0));

        definition.setDefenceBonus(NpcBonus.STAB_BONUS, defaultIfNull(defenceBonuses.stab, 0));
        definition.setDefenceBonus(NpcBonus.SLASH_BONUS, defaultIfNull(defenceBonuses.slash, 0));
        definition.setDefenceBonus(NpcBonus.CRUSH_BONUS, defaultIfNull(defenceBonuses.crush, 0));
        definition.setDefenceBonus(NpcBonus.RANGE_BONUS, defaultIfNull(defenceBonuses.range, 0));
        definition.setDefenceBonus(NpcBonus.MAGIC_BONUS, defaultIfNull(defenceBonuses.magic, 0));

        cachedCombatDefinition = definition;
        return cachedCombatDefinition;
    }

    private static int defaultIfNull(Integer value, int fallback) {
        return value == null ? fallback : value;
    }

    private static int firstNonZero(Integer primary, Integer secondary) {
        return firstNonZero(primary, secondary, 0);
    }

    private static int firstNonZero(Integer primary, Integer secondary, int fallback) {
        if (primary != null && primary > 0) {
            return primary;
        }
        if (secondary != null && secondary > 0) {
            return secondary;
        }
        return fallback;
    }

    public static class Identity {
        private String name;
        private String examine;
        private Integer size;

        public String getName() {
            return name;
        }

        public String getExamine() {
            return examine;
        }

        public Integer getSize() {
            return size;
        }
    }

    public static class Combat {
        private Integer hp;
        private Integer combatLevel;
        private Integer attackSpeed;
        private List<String> attackStyles = new ArrayList<>();
        private String attackStyle;
        private boolean aggressive;
        private boolean poisonous;
        private Immunities immunities;
        private Levels levels;
        private CombatBonuses bonuses;

        public String resolveAttackStyle() {
            if (attackStyles != null && !attackStyles.isEmpty()) {
                return attackStyles.get(0);
            }
            if (attackStyle != null && !attackStyle.isBlank()) {
                return attackStyle;
            }
            return "Melee";
        }
    }

    public static class Levels {
        private Integer hitpoints;
        private Integer attack;
        private Integer strength;
        private Integer defence;
        private Integer range;
        private Integer magic;
    }

    public static class CombatBonuses {
        private AttackBonuses attack;
        private DefenceBonuses defence;
        private StyleBonuses style;
    }

    public static class AttackBonuses {
        private Integer melee;
        private Integer strength;
        private Integer magic;
        private Integer magicDamage;
        private Integer range;
        private Integer rangeStrength;
    }

    public static class DefenceBonuses {
        private Integer stab;
        private Integer slash;
        private Integer crush;
        private Integer magic;
        private Integer range;
    }

    public static class StyleBonuses {
        private Integer stab;
        private Integer slash;
        private Integer crush;
        private Integer range;
        private Integer magic;
    }

    public static class Immunities {
        private boolean poison;
        private boolean venom;
        private boolean cannon;
        private boolean thralls;
    }

    public static class Metadata {
        private Weakness weakness = Weakness.MELEE;
        private NpcType type = NpcType.NONE;

        public Weakness getWeakness() {
            return weakness;
        }

        public NpcType getType() {
            return type;
        }
    }

    public enum Weakness {
        MELEE,
        RANGED,
        AIR_MAGIC,
        WATER_MAGIC,
        EARTH_MAGIC,
        FIRE_MAGIC
    }

    public enum NpcType {
        NONE,
        DEMON,
        UNDEAD,
        DRAGON
    }
}
