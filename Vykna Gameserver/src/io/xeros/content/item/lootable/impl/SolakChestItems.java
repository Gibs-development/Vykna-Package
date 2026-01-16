package io.xeros.content.item.lootable.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.xeros.content.EventChestSolak;
import io.xeros.content.event.eventcalendar.EventChallenge;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.model.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

public class SolakChestItems implements Lootable {

    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    static {
        items.put(LootRarity.COMMON, //
                Arrays.asList(
                        new GameItem(Items.BURNT_PAGE, 25),
                        new GameItem(Items.OVERLOAD_4, 3),
                        new GameItem(Items.WILLOW_LOGS_NOTED, 251),
                        new GameItem(Items.MAPLE_LOGS_NOTED, 181),
                        new GameItem(Items.YEW_LOGS_NOTED, 73),
                        new GameItem(Items.MAGIC_LOGS_NOTED, 54),

                        new GameItem(Items.YEW_ROOTS_NOTED, 30),
                        new GameItem(Items.MAGIC_ROOTS_NOTED, 30),
                        new GameItem(Items.CRUSHED_NEST_NOTED, 30),
                        new GameItem(Items.WINE_OF_ZAMORAK_NOTED, 30),
                        new GameItem(Items.SNAPE_GRASS_NOTED, 40),

                        new GameItem(Items.DRAGON_DART_TIP, 59),
                        new GameItem(Items.RUNE_DART_TIP, 86),
                        new GameItem(Items.ADAMANT_DART_TIP, 91),

                        new GameItem(Items.RUNE_ARROWTIPS, 120),
                        new GameItem(Items.DRAGON_ARROWTIPS, 80),

                        new GameItem(Items.DRAGON_BOLTS_UNF, 80),
                        new GameItem(Items.RUNITE_BOLTS_UNF, 100),

                        new GameItem(Items.RAW_SHARK_NOTED, 70),
                        new GameItem(Items.RAW_LOBSTER_NOTED, 110),

                        new GameItem(Items.STEEL_BAR_NOTED, 52),
                        new GameItem(Items.MITHRIL_BAR_NOTED, 41),
                        new GameItem(Items.ADAMANTITE_BAR_NOTED, 27),
                        new GameItem(Items.RUNITE_BAR_NOTED, 21),

                        new GameItem(Items.UNCUT_SAPPHIRE_NOTED, 41),
                        new GameItem(Items.UNCUT_DIAMOND_NOTED, 21),

                        new GameItem(Items.RANARR_POTION_UNF_NOTED, 16),
                        new GameItem(Items.DWARF_WEED_POTION_UNF_NOTED, 21),
                        new GameItem(Items.HARRALANDER_POTION_UNF_NOTED, 31),


                        new GameItem(Items.RANARR_WEED_NOTED, 12),
                        new GameItem(Items.HARRALANDER_NOTED, 24),
                        new GameItem(Items.IRIT_LEAF_NOTED, 27),

                        new GameItem(Items.TORSTOL_SEED, 4),
                        new GameItem(Items.RANARR_SEED, 6),
                        new GameItem(Items.TOADFLAX_SEED, 5),
                        new GameItem(Items.IRIT_SEED, 11),

                        new GameItem(4089),
                        new GameItem(4093),
                        new GameItem(4091),
                        new GameItem(452, 100),
                        new GameItem(7937, 300),
                        new GameItem(2435, 30),
                        new GameItem(2445, 30),
                        new GameItem(2364, 100),
                        new GameItem(4109),
                        new GameItem(11230, 150),
                        new GameItem(21905, 150),
                        new GameItem(4113),
                        new GameItem(6686, 25),
                        new GameItem(3025, 25),
                        new GameItem(12696, 10),
                        new GameItem(386, 50),
                        new GameItem(11937, 25),
                        new GameItem(5698),
                        new GameItem(4111),
                        new GameItem(21046, 1),//15% chest rate tomb
                        new GameItem(995, 100000))
        );

        items.put(LootRarity.UNCOMMON,
                Arrays.asList(
                        new GameItem(6524),
                        new GameItem(6528),
                        new GameItem(6522, 200),
                        new GameItem(4587),
                        new GameItem(9185),                            // Common
                        new GameItem(21905, 250),
                        new GameItem(11230, 300),
                        new GameItem(4153),
                        new GameItem(11840),
                        new GameItem(6585),

                        new GameItem(6524),
                        new GameItem(6528),
                        new GameItem(6522, 200),
                        new GameItem(4587),
                        new GameItem(9185),                            // Common
                        new GameItem(21905, 250),
                        new GameItem(11230, 300),
                        new GameItem(4153),
                        new GameItem(11840),
                        new GameItem(6585),


                        new GameItem(6524),
                        new GameItem(6528),
                        new GameItem(6522, 200),
                        new GameItem(4587),
                        new GameItem(9185),                            // Common
                        new GameItem(21905, 250),
                        new GameItem(11230, 300),
                        new GameItem(4153),
                        new GameItem(11840),
                        new GameItem(6585),

                        new GameItem(6524),
                        new GameItem(6528),
                        new GameItem(6522, 200),
                        new GameItem(4587),
                        new GameItem(9185),                            // Common
                        new GameItem(21905, 250),
                        new GameItem(11230, 300),
                        new GameItem(4153),
                        new GameItem(11840),
                        new GameItem(6585),

                        new GameItem(21046, 2),//15% chest rate tomb
                        new GameItem(2572),// Ring of wealth keep here to keep rare for unncommon
                        new GameItem(995, 1500000))
        );

        items.put(LootRarity.RARE,
                Arrays.asList(
                        new GameItem(Items.TOME_OF_FIRE_EMPTY, 1),
                        new GameItem(Items.TOME_OF_FIRE_EMPTY, 1),
                        new GameItem(4675),
                        new GameItem(4716),
                        new GameItem(4720),
                        new GameItem(4723),
                        new GameItem(4718),
                        new GameItem(4708),
                        new GameItem(4710),
                        new GameItem(4712),
                        new GameItem(4714),
                        new GameItem(4753),
                        new GameItem(4756),
                        new GameItem(4759),
                        new GameItem(4757),
                        new GameItem(4745),
                        new GameItem(4749),
                        new GameItem(4751),
                        new GameItem(4747),
                        new GameItem(4724),
                        new GameItem(4726),
                        new GameItem(4728),
                        new GameItem(4730),
                        new GameItem(4732),
                        new GameItem(4734),
                        new GameItem(4736),
                        new GameItem(4738),
                        new GameItem(6920),
                        new GameItem(16011),
                        new GameItem(6914)));

    }


    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

    @Override
    public void roll(Player c) {
    }
}
