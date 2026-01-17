package io.xeros.model.entity.npc.drops;

import io.xeros.model.Items;
public final class ItemAttributeDropConfig {

    private ItemAttributeDropConfig() {
    }

    public static void configure(ItemAttributeDropRules rules) {
        if (rules == null) {
            return;
        }

        rules.allowItems(Items.ABYSSAL_WHIP);

        rules.ruleForItem(Items.ABYSSAL_WHIP)
                .addRarityChance(1, 0.25)
                .addRarityChance(2, 0.12)
                .addRarityChance(3, 0.04)
                .addRarityChance(4, 0.01)
                .setMaxPerks(2)
                .useRarityPerkPool(true)
                .forceAttributes(1);
    }
}
