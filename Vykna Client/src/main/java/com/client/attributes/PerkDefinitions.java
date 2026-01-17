package com.client.attributes;

import com.client.Sprite;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class PerkDefinitions {
    public static final class PerkDefinition {
        private final int id;
        private final String name;
        private final String description;
        private final int maxRank;
        private final String iconKey;

        private PerkDefinition(int id, String name, int maxRank, String description, String iconKey) {
            this.id = id;
            this.name = name;
            this.maxRank = maxRank;
            this.description = description;
            this.iconKey = iconKey;
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

        public Sprite getIcon(int rank) {
            return new Sprite("/perks/" + iconKey);
        }
    }

    private static final Map<Integer, PerkDefinition> PERKS = new HashMap<>();

    static {
        register(1, "Aftershock", 4, "After dealing 5000 damage, triggers an explosion on the current target dealing up to 40% of your max hit damage per rank to nearby enemies.", "aftershock");
        register(2, "Biting", 4, "Increases critical hit chance by 2% per rank. Increased activation chance on level 20 items.", "biting");
        register(3, "Blunted", 5, "Reduces weapon damage by 1% per rank.", "blunted");
        register(4, "Brassican", 1, "Occasionally spawns cabbages as a humorous effect.", "brassican");
        register(5, "Bulwark", 4, "Damage reduction of 5% perk rank.", "bulwark");
        register(6, "Caroming", 4, "3% chance to apply a second immediate hit per rank.", "caroming");
        register(7, "Cautious", 1, "Can't enter the wilderness whilst equipped.", "cautious");
        register(8, "Committed", 1, "Player is always skulled while equipped.", "committed");
        register(9, "Confused", 3, "1% chance per rank to randomly teleport after taking damage.", "confused");
        register(10, "Crackling", 4, "Periodically deals 50% max hit damage per rank to the target (10% in PvP). One-minute cooldown.", "crackling");
        register(11, "Crystal Shield", 4, "10% chance when hit to store 5% damage taken per rank for 10 seconds, converting it into temporary lifepoints. Increased activation chance on level 20 items.", "crystal_shield");
        register(12, "Demon Bait", 1, "Reduces damage dealt to demons by 30%.", "demon_bait");
        register(13, "Demon Slayer", 1, "Increases damage dealt to demons by 7%.", "demon_slayer");
        register(14, "Devoted", 4, "3% chance per rank when hit to restore prayer points to full.", "devoted");
        register(15, "Dragon Bait", 1, "Reduces damage dealt to dragons by 30%.", "dragon_bait");
        register(16, "Dragon Slayer", 1, "Increases damage dealt to dragons by 7%.", "dragon_slayer");
        register(17, "Enhanced Devoted", 4, "4.5% chance per rank when hit to restore prayer points to full and overcharge prayer by 50%.", "enhanced_devoted");
        register(18, "Enlightened", 4, "Increases item experience gain by 3% per rank.", "enlightened");
        register(19, "Eruptive", 4, "Increases damage by 0.5% per rank.", "eruptive");
        register(20, "Fatiguing", 3, "Makes you attack 1 tick slower per rank at the benefit of 75% damage increase.", "fatiguing");
        register(21, "Flanking", 4, "Certain abilities deal increased damage when attacking targets not facing you, scaling up to 40% per rank depending on ability type. (OSRS-style: backstab bonus).", "flanking");
        register(22, "Genocidal", 1, "Deals up to 5% additional damage to current Slayer target based on task progress.", "genocidal");
        register(23, "Glow Worm", 1, "Provides light equivalent to a bullseye lantern.", "glow_worm");
        register(24, "Hoarding", 1, "Protect Item prayer protects two items instead of one outside PvP.", "hoarding");
        register(25, "Inaccurate", 5, "Reduces weapon accuracy by 1% per rank.", "inaccurate");
        register(26, "Invigorating", 4, "Increases wrath gained from basic attacks by 5% per rank.", "invigorating");
        register(27, "Junk Food", 3, "Food heals 3% less lifepoints per rank.", "junk_food");
        register(28, "Looting", 1, "Enemies have a 25% chance to drop an additional resource. Five-minute cooldown.", "looting");
        register(29, "Lucky", 6, "0.5% chance per rank when hit to reduce damage taken to 1.", "lucky");
        register(30, "Lunging", 4, "Bleed abilities deal 6% more damage per rank, but moving targets receive reduced bonus damage.", "lunging");
        register(31, "Mediocrity", 3, "Reduces maximum hit by 3% per rank.", "mediocrity");
        register(32, "Precise", 6, "Increases minimum damage by 1.5% per rank of maximum damage.", "precise");
        register(33, "Relentless", 5, "1% chance per rank to prevent wrath consumption when using wrath-based abilities.", "relentless");
        register(34, "Ruthless", 3, "Killing enemies grants stacking damage bonuses lasting 20 seconds. Does not work in PvP.", "ruthless");
        register(35, "Scavenging", 4, "1% chance per rank to receive uncommon fusion components from combat, with a small chance for rare components.", "scavenging");
        register(36, "Spendthrift", 6, "Chance per rank to deal extra damage at the cost of coins per damage dealt.", "spendthrift");
        register(37, "Talking", 1, "Causes gear to occasionally speak.", "talking");
        register(38, "Trophy-Taker's", 6, "Slayer kills may count as zero or double based on chance per rank. Increased activation chance on level 20 items.", "trophy_taker");
        register(39, "Undead Bait", 1, "Reduces damage dealt to undead by 30%.", "undead_bait");
        register(40, "Undead Slayer", 1, "Increases damage dealt to undead by 7%.", "undead_slayer");
        register(41, "Venomblood", 1, "Negates regular poison damage.", "venomblood");
        register(42, "Wise", 4, "Increases experience gained by 1% per rank.", "wise");
    }

    private PerkDefinitions() {
    }

    private static void register(int id, String name, int maxRank, String description, String iconKey) {
        PERKS.put(id, new PerkDefinition(id, name, maxRank, description, iconKey));
    }

    public static PerkDefinition forId(int id) {
        return PERKS.get(id);
    }

    public static Map<Integer, PerkDefinition> all() {
        return Collections.unmodifiableMap(PERKS);
    }
}
