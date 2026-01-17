package io.xeros.model.items;

import io.xeros.util.Misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum RarityPerkPool {
    COMMON(0, new Option[] {
            option(PerkModule.BITING, 1, 2, 3),
            option(PerkModule.LOOTING, 1, 1, 2),
            option(PerkModule.PRECISE, 1, 2, 2),
            option(PerkModule.WISE, 1, 2, 2),
            option(PerkModule.ERUPTIVE, 1, 2, 1)
    }),
    UNCOMMON(1, new Option[] {
            option(PerkModule.PRECISE, 1, 3, 3),
            option(PerkModule.CRACKLING, 1, 2, 2),
            option(PerkModule.BITING, 2, 3, 2),
            option(PerkModule.BULWARK, 1, 2, 2),
            option(PerkModule.WISE, 2, 3, 1),
            option(PerkModule.ERUPTIVE, 2, 3, 1)
    }),
    RARE(2, new Option[] {
            option(PerkModule.CRACKLING, 2, 3, 3),
            option(PerkModule.PRECISE, 3, 4, 2),
            option(PerkModule.BITING, 3, 4, 2),
            option(PerkModule.BULWARK, 2, 3, 2),
            option(PerkModule.LUCKY, 1, 2, 1),
            option(PerkModule.ERUPTIVE, 3, 4, 1)
    }),
    EPIC(3, new Option[] {
            option(PerkModule.AFTERSHOCK, 1, 2, 3),
            option(PerkModule.CRACKLING, 3, 4, 2),
            option(PerkModule.PRECISE, 4, 5, 2),
            option(PerkModule.BITING, 4, 4, 2),
            option(PerkModule.BULWARK, 3, 4, 2),
            option(PerkModule.LUCKY, 2, 4, 1)
    }),
    MYTHIC(4, new Option[] {
            option(PerkModule.AFTERSHOCK, 2, 4, 3),
            option(PerkModule.CRACKLING, 4, 4, 2),
            option(PerkModule.PRECISE, 5, 6, 2),
            option(PerkModule.BITING, 4, 4, 2),
            option(PerkModule.BULWARK, 4, 4, 2),
            option(PerkModule.LUCKY, 4, 6, 1)
    });

    private final int rarityId;
    private final List<Option> options;

    RarityPerkPool(int rarityId, Option[] options) {
        this.rarityId = rarityId;
        List<Option> list = new ArrayList<>();
        Collections.addAll(list, options);
        this.options = Collections.unmodifiableList(list);
    }

    public int getRarityId() {
        return rarityId;
    }

    public List<Option> getOptions() {
        return options;
    }

    public static RarityPerkPool forRarity(int rarityId) {
        if (rarityId >= 4) {
            return MYTHIC;
        }
        if (rarityId == 3) {
            return EPIC;
        }
        if (rarityId == 2) {
            return RARE;
        }
        if (rarityId == 1) {
            return UNCOMMON;
        }
        return COMMON;
    }

    public static List<PerkRoll> rollPerks(int rarityId, int count) {
        if (count <= 0) {
            return Collections.emptyList();
        }
        // Note: No item-type gating exists here; the pool applies to any eligible drop.
        RarityPerkPool pool = forRarity(rarityId);
        List<PerkRoll> rolls = new ArrayList<>();
        int attempts = 0;
        while (rolls.size() < count && attempts < count * 5) {
            attempts++;
            Option option = pool.rollOption();
            if (option == null) {
                break;
            }
            if (rolls.stream().anyMatch(r -> r.perkId == option.perkId)) {
                continue;
            }
            int rank = option.rollRank();
            rolls.add(new PerkRoll(option.perkId, rank));
        }
        return rolls;
    }

    private Option rollOption() {
        int totalWeight = 0;
        for (Option option : options) {
            totalWeight += option.weight;
        }
        if (totalWeight <= 0) {
            return null;
        }
        int roll = Misc.random(totalWeight - 1);
        int tally = 0;
        for (Option option : options) {
            tally += option.weight;
            if (roll < tally) {
                return option;
            }
        }
        return options.get(0);
    }

    private static Option option(int perkId, int minRank, int maxRank, int weight) {
        return new Option(perkId, minRank, maxRank, weight);
    }

    public static final class Option {
        private final int perkId;
        private final int minRank;
        private final int maxRank;
        private final int weight;

        private Option(int perkId, int minRank, int maxRank, int weight) {
            this.perkId = perkId;
            this.minRank = Math.max(1, minRank);
            this.maxRank = Math.max(this.minRank, maxRank);
            this.weight = Math.max(1, weight);
        }

        public int rollRank() {
            if (minRank == maxRank) {
                return minRank;
            }
            return minRank + Misc.random(maxRank - minRank);
        }
    }

    public static final class PerkRoll {
        public final int perkId;
        public final int rank;

        private PerkRoll(int perkId, int rank) {
            this.perkId = perkId;
            this.rank = rank;
        }
    }
}
