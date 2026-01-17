package io.xeros.model.entity.npc.drops;

import io.xeros.model.Items;
import io.xeros.model.Npcs;

public final class ItemAttributeDropConfig {

    private ItemAttributeDropConfig() {
    }

    public static void configure(ItemAttributeDropRules rules) {
        if (rules == null) {
            return;
        }

        rules.allowItems(Items.ABYSSAL_WHIP);

        int[] abyssalDemons = {
                Npcs.ABYSSAL_DEMON,
                Npcs.ABYSSAL_DEMON_2,
                Npcs.ABYSSAL_DEMON_3,
                Npcs.ABYSSAL_DEMON_4,
                Npcs.GREATER_ABYSSAL_DEMON
        };

        for (int npcId : abyssalDemons) {
            rules.ruleForNpc(npcId)
                    .addRarityChance(1, 0.25)
                    .addRarityChance(2, 0.12)
                    .addRarityChance(3, 0.04)
                    .addRarityChance(4, 0.01)
                    .setMaxPerks(2)
                    .useRarityPerkPool(true)
                    .forceAttributes(1);
        }
    }
}
