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
        private final int gfxId;

        private PerkDefinition(int id, String name, int maxRank, String description, int gfxId) {
            this.id = id;
            this.name = name;
            this.maxRank = maxRank;
            this.description = description;
            this.gfxId = gfxId;
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

        public int getGfxId() {
            return gfxId;
        }
    }

    private static final Map<Integer, PerkDefinition> PERKS = new LinkedHashMap<>();
    private static final int[] PERK_IDS;

    static {
        register(AFTERSHOCK, "Aftershock", 4, "After dealing 5000 damage, triggers an explosion on the current target dealing up to 40% of your max hit damage per rank to nearby enemies.", 363);
        register(BITING, "Biting", 4, "Increases critical hit chance by 2% per rank. Increased activation chance on level 20 items.", 346);
        register(BLUNTED, "Blunted", 5, "Reduces weapon damage by 1% per rank.", 1050);
        register(BRASSICAN, "Brassican", 1, "Occasionally spawns cabbages as a humorous effect.", 261);
        register(BULWARK, "Bulwark", 4, "Damage reduction of 5% perk rank.", 442);
        register(CAROMING, "Caroming", 4, "3% chance to apply a second immediate hit per rank.", 442);
        register(CAUTIOUS, "Cautious", 1, "Can't enter the wilderness whilst equipped.", 1196);
        register(COMMITTED, "Committed", 1, "Player is always skulled while equipped.", 263);
        register(CONFUSED, "Confused", 3, "1% chance per rank to randomly teleport after taking damage.", 446);
        register(CRACKLING, "Crackling", 4, "Periodically deals 50% max hit damage per rank to the target (10% in PvP). One-minute cooldown.", 437);
        register(CRYSTAL_SHIELD, "Crystal Shield", 4, "10% chance when hit to store 5% damage taken per rank for 10 seconds, converting it into temporary lifepoints. Increased activation chance on level 20 items.", 107);
        register(DEMON_BAIT, "Demon Bait", 1, "Reduces damage dealt to demons by 30%.", 80);
        register(DEMON_SLAYER, "Demon Slayer", 1, "Increases damage dealt to demons by 7%.", 80);
        register(DEVOTED, "Devoted", 4, "3% chance per rank when hit to restore prayer points to full.", 436);
        register(DRAGON_BAIT, "Dragon Bait", 1, "Reduces damage dealt to dragons by 30%.", 80);
        register(DRAGON_SLAYER, "Dragon Slayer", 1, "Increases damage dealt to dragons by 7%.", 80);
        register(ENHANCED_DEVOTED, "Enhanced Devoted", 4, "4.5% chance per rank when hit to restore prayer points to full and overcharge prayer by 50%.", 436);
        register(ENLIGHTENED, "Enlightened", 4, "Increases item experience gain by 3% per rank.", 113);
        register(ERUPTIVE, "Eruptive", 4, "Increases damage by 0.5% per rank.", 363);
        register(FATIGUING, "Fatiguing", 3, "Makes you attack 1 tick slower per rank at the benefit of 75% damage increase.", 1050);
        register(FLANKING, "Flanking", 4, "Certain abilities deal increased damage when attacking targets not facing you, scaling up to 40% per rank depending on ability type. (OSRS-style: backstab bonus).", 269);
        register(GENOCIDAL, "Genocidal", 1, "Deals up to 5% additional damage to current Slayer target based on task progress.", 341);
        register(GLOW_WORM, "Glow Worm", 1, "Provides light equivalent to a bullseye lantern.", 1118);
        register(HOARDING, "Hoarding", 1, "Protect Item prayer protects two items instead of one outside PvP.", 392);
        register(INACCURATE, "Inaccurate", 5, "Reduces weapon accuracy by 1% per rank.", 1050);
        register(INVIGORATING, "Invigorating", 4, "Increases wrath gained from basic attacks by 5% per rank.", 442);
        register(JUNK_FOOD, "Junk Food", 3, "Food heals 3% less lifepoints per rank.", 1992);
        register(LOOTING, "Looting", 1, "Enemies have a 25% chance to drop an additional resource. Five-minute cooldown.", 392);
        register(LUCKY, "Lucky", 6, "0.5% chance per rank when hit to reduce damage taken to 1.", 263);
        register(LUNGING, "Lunging", 4, "Bleed abilities deal 6% more damage per rank, but moving targets receive reduced bonus damage.", 163);
        register(MEDIOCRITY, "Mediocrity", 3, "Reduces maximum hit by 3% per rank.", 1050);
        register(PRECISE, "Precise", 6, "Increases minimum damage by 1.5% per rank of maximum damage.", 363);
        register(RELENTLESS, "Relentless", 5, "1% chance per rank to prevent wrath consumption when using wrath-based abilities.", 442);
        register(RUTHLESS, "Ruthless", 3, "Killing enemies grants stacking damage bonuses lasting 20 seconds. Does not work in PvP.", 362);
        register(SCAVENGING, "Scavenging", 4, "1% chance per rank to receive uncommon fusion components from combat, with a small chance for rare components.", 392);
        register(SPENDTHRIFT, "Spendthrift", 6, "Chance per rank to deal extra damage at the cost of coins per damage dealt.", 163);
        register(TALKING, "Talking", 1, "Causes gear to occasionally speak.", 186);
        register(TROPHY_TAKER, "Trophy-Taker's", 6, "Slayer kills may count as zero or double based on chance per rank. Increased activation chance on level 20 items.", 343);
        register(UNDEAD_BAIT, "Undead Bait", 1, "Reduces damage dealt to undead by 30%.", 80);
        register(UNDEAD_SLAYER, "Undead Slayer", 1, "Increases damage dealt to undead by 7%.", 80);
        register(VENOMBLOOD, "Venomblood", 1, "Negates regular poison damage.", 107);
        register(WISE, "Wise", 4, "Increases experience gained by 1% per rank.", 107);

        PERK_IDS = PERKS.keySet().stream().mapToInt(Integer::intValue).toArray();
    }

    private PerkModule() {
    }

    private static void register(int id, String name, int maxRank, String description, int gfxId) {
        PERKS.put(id, new PerkDefinition(id, name, maxRank, description, gfxId));
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
