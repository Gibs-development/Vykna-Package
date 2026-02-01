package io.xeros.model.entity.npc.drops;

import io.xeros.model.Items;
public final class ItemAttributeDropConfig {

    private ItemAttributeDropConfig() {
    }

    public static void configure(ItemAttributeDropRules rules) {
        if (rules == null) {
            return;
        }

        rules.allowItems(
                Items.ABYSSAL_WHIP,
                1332,  // Adamant scimitar
                9183,  // Adamant c'bow
                6911,  // Apprentice wand
                20402, // Rune scimitar
                20555, // Rune 2h sword
                9185,  // Rune c'bow
                6913,  // Teacher wand
                20406, // Dragon scimitar
                20407, // Dragon dagger
                21902, // Dragon Crossbow
                20560, // Master wand
                3842,  // Unholy book
                3840,  // Holy book
                3844,  // Book of balance
                6890,  // Mage's book
                12954  // Dragon defender
        );

        rules.ruleForItem(Items.ABYSSAL_WHIP)
                .addRarityChance(1, 0.25)
                .addRarityChance(2, 0.12)
                .addRarityChance(3, 0.04)
                .addRarityChance(4, 0.01)
                .setMaxPerks(2)
                .useRarityPerkPool(true)
                .forceAttributes(1);

        // Warped Rat (t30) - up to Uncommon
        rules.ruleForItem(1332)  // Adamant scimitar
                .addRarityChance(1, 0.25)
                .setMaxPerks(1)
                .useRarityPerkPool(true)
                .forceAttributes(1);
        rules.ruleForItem(9183)  // Adamant c'bow
                .addRarityChance(1, 0.25)
                .setMaxPerks(1)
                .useRarityPerkPool(true)
                .forceAttributes(1);
        rules.ruleForItem(6911)  // Apprentice wand
                .addRarityChance(1, 0.25)
                .setMaxPerks(1)
                .useRarityPerkPool(true)
                .forceAttributes(1);

        // Warped Fly (t40) - up to Rare
        rules.ruleForItem(1333) // Rune scimitar
                .addRarityChance(1, 0.25)
                .addRarityChance(2, 0.12)
                .setMaxPerks(2)
                .useRarityPerkPool(true)
                .forceAttributes(1);
        rules.ruleForItem(1319) // Rune 2h sword
                .addRarityChance(1, 0.25)
                .addRarityChance(2, 0.12)
                .setMaxPerks(2)
                .useRarityPerkPool(true)
                .forceAttributes(1);
        rules.ruleForItem(9185)  // Rune c'bow
                .addRarityChance(1, 0.25)
                .addRarityChance(2, 0.12)
                .setMaxPerks(2)
                .useRarityPerkPool(true)
                .forceAttributes(1);
        rules.ruleForItem(6912)  // Teacher wand
                .addRarityChance(1, 0.25)
                .addRarityChance(2, 0.12)
                .setMaxPerks(2)
                .useRarityPerkPool(true)
                .forceAttributes(1);

        // Warped Terrorbird (t60) - up to Epic
        rules.ruleForItem(4587) // Dragon scimitar
                .addRarityChance(1, 0.25)
                .addRarityChance(2, 0.12)
                .addRarityChance(3, 0.04)
                .setMaxPerks(2)
                .useRarityPerkPool(true)
                .forceAttributes(1);
        rules.ruleForItem(20407) // Dragon dagger
                .addRarityChance(1, 0.25)
                .addRarityChance(2, 0.12)
                .addRarityChance(3, 0.04)
                .setMaxPerks(2)
                .useRarityPerkPool(true)
                .forceAttributes(1);
        rules.ruleForItem(21902) // Dragon Crossbow
                .addRarityChance(1, 0.25)
                .addRarityChance(2, 0.12)
                .addRarityChance(3, 0.04)
                .setMaxPerks(2)
                .useRarityPerkPool(true)
                .forceAttributes(1);
        rules.ruleForItem(6914) // Master wand
                .addRarityChance(1, 0.25)
                .addRarityChance(2, 0.12)
                .addRarityChance(3, 0.04)
                .setMaxPerks(2)
                .useRarityPerkPool(true)
                .forceAttributes(1);

        // Warped Skoblin books - up to Mythic (max 1 perk)
        rules.ruleForItem(3842)  // Unholy book
                .addRarityChance(1, 0.25)
                .addRarityChance(2, 0.12)
                .addRarityChance(3, 0.04)
                .addRarityChance(4, 0.01)
                .setMaxPerks(1)
                .useRarityPerkPool(true)
                .forceAttributes(1);
        rules.ruleForItem(3840)  // Holy book
                .addRarityChance(1, 0.25)
                .addRarityChance(2, 0.12)
                .addRarityChance(3, 0.04)
                .addRarityChance(4, 0.01)
                .setMaxPerks(1)
                .useRarityPerkPool(true)
                .forceAttributes(1);
        rules.ruleForItem(3844)  // Book of balance
                .addRarityChance(1, 0.25)
                .addRarityChance(2, 0.12)
                .addRarityChance(3, 0.04)
                .addRarityChance(4, 0.01)
                .setMaxPerks(1)
                .useRarityPerkPool(true)
                .forceAttributes(1);
        rules.ruleForItem(6890)  // Mage's book
                .addRarityChance(1, 0.25)
                .addRarityChance(2, 0.12)
                .addRarityChance(3, 0.04)
                .addRarityChance(4, 0.01)
                .setMaxPerks(1)
                .useRarityPerkPool(true)
                .forceAttributes(1);

        // Dragon defender - up to Mythic (max 2 perks)
        rules.ruleForItem(12954)
                .addRarityChance(1, 0.25)
                .addRarityChance(2, 0.12)
                .addRarityChance(3, 0.04)
                .addRarityChance(4, 0.01)
                .setMaxPerks(2)
                .useRarityPerkPool(true)
                .forceAttributes(1);
    }
}
