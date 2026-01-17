package io.xeros.model.items;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class PerkModule {
    public static final int AFTERSHOCK = 1;
    public static final int BITING = 2;
    public static final int BLUNTED = 3;
    public static final int BRASSICAN = 4;
    public static final int BULWARK = 5;
    public static final int CAROMING = 6;
    public static final int CAUTIOUS = 7;
    public static final int COMMITTED = 8;
    public static final int CONFUSED = 9;
    public static final int CRACKLING = 10;
    public static final int CRYSTAL_SHIELD = 11;
    public static final int DEMON_BAIT = 12;
    public static final int DEMON_SLAYER = 13;
    public static final int DEVOTED = 14;
    public static final int DRAGON_BAIT = 15;
    public static final int DRAGON_SLAYER = 16;
    public static final int ENHANCED_DEVOTED = 17;
    public static final int ENLIGHTENED = 18;
    public static final int ERUPTIVE = 19;
    public static final int FATIGUING = 20;
    public static final int FLANKING = 21;
    public static final int GENOCIDAL = 22;
    public static final int GLOW_WORM = 23;
    public static final int HOARDING = 24;
    public static final int INACCURATE = 25;
    public static final int INVIGORATING = 26;
    public static final int JUNK_FOOD = 27;
    public static final int LOOTING = 28;
    public static final int LUCKY = 29;
    public static final int LUNGING = 30;
    public static final int MEDIOCRITY = 31;
    public static final int PRECISE = 32;
    public static final int RELENTLESS = 33;
    public static final int RUTHLESS = 34;
    public static final int SCAVENGING = 35;
    public static final int SPENDTHRIFT = 36;
    public static final int TALKING = 37;
    public static final int TROPHY_TAKER = 38;
    public static final int UNDEAD_BAIT = 39;
    public static final int UNDEAD_SLAYER = 40;
    public static final int VENOMBLOOD = 41;
    public static final int WISE = 42;

    public static final class PerkDefinition {
        private final int id;
        private final String name;
        private final String description;
        private final int maxRank;

        private PerkDefinition(int id, String name, int maxRank, String description) {
            this.id = id;
            this.name = name;
            this.maxRank = maxRank;
            this.description = description;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getMaxRank() {
            return maxRank;
        }

        public String getDescription() {
            return description;
        }
    }

    private static final Map<Integer, PerkDefinition> PERKS = new LinkedHashMap<>();
    private static final int[] PERK_IDS;

    static {
        // TODO: Implement Aftershock damage tracking/explosion trigger on hits.
        register(AFTERSHOCK, "Aftershock", 4, "After dealing 5000 damage, triggers an explosion on the current target dealing up to 40% of your max hit damage per rank to nearby enemies.");
        // TODO: Implement critical hit chance scaling for Biting.
        register(BITING, "Biting", 4, "Increases critical hit chance by 2% per rank. Increased activation chance on level 20 items.");
        // TODO: Implement Blunted damage reduction modifier.
        register(BLUNTED, "Blunted", 5, "Reduces weapon damage by 1% per rank.");
        // TODO: Implement Brassican cabbage spawn effect.
        register(BRASSICAN, "Brassican", 1, "Occasionally spawns cabbages as a humorous effect.");
        // TODO: Implement Bulwark damage reduction on incoming hits.
        register(BULWARK, "Bulwark", 4, "Damage reduction of 5% perk rank.");
        // TODO: Implement Caroming extra hit chance.
        register(CAROMING, "Caroming", 4, "3% chance to apply a second immediate hit per rank.");
        // TODO: Block wilderness entry when Cautious-equipped items are worn.
        register(CAUTIOUS, "Cautious", 1, "Can't enter the wilderness whilst equipped.");
        // TODO: Force skull status while Committed-equipped items are worn.
        register(COMMITTED, "Committed", 1, "Player is always skulled while equipped.");
        // TODO: Implement Confused teleport-on-hit effect.
        register(CONFUSED, "Confused", 3, "1% chance per rank to randomly teleport after taking damage.");
        // TODO: Implement Crackling periodic damage with cooldown and PvP scaling.
        register(CRACKLING, "Crackling", 4, "Periodically deals 50% max hit damage per rank to the target (10% in PvP). One-minute cooldown.");
        // TODO: Implement Crystal Shield damage storage/temporary lifepoints.
        register(CRYSTAL_SHIELD, "Crystal Shield", 4, "10% chance when hit to store 5% damage taken per rank for 10 seconds, converting it into temporary lifepoints. Increased activation chance on level 20 items.");
        // TODO: Implement Demon Bait damage reduction vs demons.
        register(DEMON_BAIT, "Demon Bait", 1, "Reduces damage dealt to demons by 30%.");
        // TODO: Implement Demon Slayer damage bonus vs demons.
        register(DEMON_SLAYER, "Demon Slayer", 1, "Increases damage dealt to demons by 7%.");
        // TODO: Implement Devoted prayer restoration on hit taken.
        register(DEVOTED, "Devoted", 4, "3% chance per rank when hit to restore prayer points to full.");
        // TODO: Implement Dragon Bait damage reduction vs dragons.
        register(DRAGON_BAIT, "Dragon Bait", 1, "Reduces damage dealt to dragons by 30%.");
        // TODO: Implement Dragon Slayer damage bonus vs dragons.
        register(DRAGON_SLAYER, "Dragon Slayer", 1, "Increases damage dealt to dragons by 7%.");
        // TODO: Implement Enhanced Devoted prayer overcharge.
        register(ENHANCED_DEVOTED, "Enhanced Devoted", 4, "4.5% chance per rank when hit to restore prayer points to full and overcharge prayer by 50%.");
        // TODO: Implement Enlightened item experience gain.
        register(ENLIGHTENED, "Enlightened", 4, "Increases item experience gain by 3% per rank.");
        // TODO: Implement Eruptive damage bonus.
        register(ERUPTIVE, "Eruptive", 4, "Increases damage by 0.5% per rank.");
        // TODO: Implement Fatiguing attack speed penalty with damage boost.
        register(FATIGUING, "Fatiguing", 3, "Makes you attack 1 tick slower per rank at the benefit of 75% damage increase.");
        // TODO: Implement Flanking/backstab bonus.
        register(FLANKING, "Flanking", 4, "Certain abilities deal increased damage when attacking targets not facing you, scaling up to 40% per rank depending on ability type. (OSRS-style: backstab bonus).");
        // TODO: Implement Genocidal slayer target damage scaling.
        register(GENOCIDAL, "Genocidal", 1, "Deals up to 5% additional damage to current Slayer target based on task progress.");
        // TODO: Implement Glow Worm light source effect.
        register(GLOW_WORM, "Glow Worm", 1, "Provides light equivalent to a bullseye lantern.");
        // TODO: Implement Hoarding protect-item extension outside PvP.
        register(HOARDING, "Hoarding", 1, "Protect Item prayer protects two items instead of one outside PvP.");
        // TODO: Implement Inaccurate accuracy penalty.
        register(INACCURATE, "Inaccurate", 5, "Reduces weapon accuracy by 1% per rank.");
        // TODO: Implement Invigorating wrath gain (wrath system needed).
        register(INVIGORATING, "Invigorating", 4, "Increases wrath gained from basic attacks by 5% per rank.");
        // TODO: Implement Junk Food reduced healing.
        register(JUNK_FOOD, "Junk Food", 3, "Food heals 3% less lifepoints per rank.");
        // TODO: Implement Looting extra resource drop with cooldown.
        register(LOOTING, "Looting", 1, "Enemies have a 25% chance to drop an additional resource. Five-minute cooldown.");
        // TODO: Implement Lucky damage floor effect.
        register(LUCKY, "Lucky", 6, "0.5% chance per rank when hit to reduce damage taken to 1.");
        // TODO: Implement Lunging bleed damage bonus (bleed system required).
        register(LUNGING, "Lunging", 4, "Bleed abilities deal 6% more damage per rank, but moving targets receive reduced bonus damage.");
        // TODO: Implement Mediocrity max hit reduction.
        register(MEDIOCRITY, "Mediocrity", 3, "Reduces maximum hit by 3% per rank.");
        // TODO: Implement Precise minimum damage increase.
        register(PRECISE, "Precise", 6, "Increases minimum damage by 1.5% per rank of maximum damage.");
        // TODO: Implement Relentless wrath consumption prevention (wrath system required).
        register(RELENTLESS, "Relentless", 5, "1% chance per rank to prevent wrath consumption when using wrath-based abilities.");
        // TODO: Implement Ruthless stacking damage bonus on kills.
        register(RUTHLESS, "Ruthless", 3, "Killing enemies grants stacking damage bonuses lasting 20 seconds. Does not work in PvP.");
        // TODO: Implement Scavenging component drops.
        register(SCAVENGING, "Scavenging", 4, "1% chance per rank to receive uncommon fusion components from combat, with a small chance for rare components.");
        // TODO: Implement Spendthrift coin-for-damage effect.
        register(SPENDTHRIFT, "Spendthrift", 6, "Chance per rank to deal extra damage at the cost of coins per damage dealt.");
        // TODO: Implement Talking gear chat effect.
        register(TALKING, "Talking", 1, "Causes gear to occasionally speak.");
        // TODO: Implement Trophy-Taker's slayer count variance.
        register(TROPHY_TAKER, "Trophy-Taker's", 6, "Slayer kills may count as zero or double based on chance per rank. Increased activation chance on level 20 items.");
        // TODO: Implement Undead Bait damage reduction vs undead.
        register(UNDEAD_BAIT, "Undead Bait", 1, "Reduces damage dealt to undead by 30%.");
        // TODO: Implement Undead Slayer damage bonus vs undead.
        register(UNDEAD_SLAYER, "Undead Slayer", 1, "Increases damage dealt to undead by 7%.");
        // TODO: Implement Venomblood poison immunity.
        register(VENOMBLOOD, "Venomblood", 1, "Negates regular poison damage.");
        // TODO: Implement Wise experience bonus.
        register(WISE, "Wise", 4, "Increases experience gained by 1% per rank.");

        PERK_IDS = PERKS.keySet().stream().mapToInt(Integer::intValue).toArray();
    }

    private PerkModule() {
    }

    private static void register(int id, String name, int maxRank, String description) {
        PERKS.put(id, new PerkDefinition(id, name, maxRank, description));
    }

    public static PerkDefinition forId(int id) {
        return PERKS.get(id);
    }

    public static Map<Integer, PerkDefinition> all() {
        return Collections.unmodifiableMap(PERKS);
    }

    public static int[] perkIds() {
        return Arrays.copyOf(PERK_IDS, PERK_IDS.length);
    }
}
