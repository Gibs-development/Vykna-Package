package io.xeros.content.item.lootable.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.minigames.raids.Raids;
import io.xeros.model.Items;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.GameItemVariableAmount;
import io.xeros.util.Misc;

public class RaidsChestItems {

    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    public static Map<LootRarity, List<GameItem>> getItems() {
        return items;
    }

    static {
        items.put(LootRarity.COMMON, Arrays.asList(
                new GameItemVariableAmount(1618, 5, 10), //uncut diamonds
                new GameItemVariableAmount(11230, 5, 10), //dragon dart
                new GameItemVariableAmount(11212, 10, 15), //dragon arrow
                new GameItemVariableAmount(560, 50, 100),  //death rune
                new GameItemVariableAmount(565, 50, 100), //blood rune       // COMMON
                new GameItemVariableAmount(220, 5, 15), //torstol
                new GameItemVariableAmount(454, 10, 40), //coal
                new GameItemVariableAmount(13440, 5, 10), //anglerfish
                new GameItemVariableAmount(3139, 5, 10), //potato cactus
                new GameItemVariableAmount(384, 5, 15), //raw shark

                new GameItemVariableAmount(1618, 5, 10), //uncut diamonds
                new GameItemVariableAmount(11230, 5, 10), //dragon dart
                new GameItemVariableAmount(11212, 10, 15), //dragon arrow
                new GameItemVariableAmount(560, 50, 100),  //death rune
                new GameItemVariableAmount(565, 50, 100), //blood rune       // COMMON
                new GameItemVariableAmount(220, 5, 15), //torstol
                new GameItemVariableAmount(454, 10, 40), //coal
                new GameItemVariableAmount(13440, 5, 10), //anglerfish
                new GameItemVariableAmount(3139, 5, 10), //potato cactus
                new GameItemVariableAmount(384, 5, 15), //raw shark

                new GameItemVariableAmount(1618, 5, 10), //uncut diamonds
                new GameItemVariableAmount(11230, 5, 10), //dragon dart
                new GameItemVariableAmount(11212, 10, 15), //dragon arrow
                new GameItemVariableAmount(560, 50, 100),  //death rune
                new GameItemVariableAmount(565, 50, 100), //blood rune       // COMMON
                new GameItemVariableAmount(220, 5, 15), //torstol
                new GameItemVariableAmount(454, 40, 80), //coal
                new GameItemVariableAmount(13440, 5, 10), //anglerfish
                new GameItemVariableAmount(3139, 5, 10), //potato cactus
                new GameItemVariableAmount(384, 5, 15), //raw shark

                new GameItemVariableAmount(1618, 5, 10), //uncut diamonds
                new GameItemVariableAmount(11230, 5, 10), //dragon dart
                new GameItemVariableAmount(11212, 10, 15), //dragon arrow
                new GameItemVariableAmount(560, 50, 100),  //death rune
                new GameItemVariableAmount(565, 50, 100), //blood rune       // COMMON
                new GameItemVariableAmount(220, 5, 15), //torstol
                new GameItemVariableAmount(454, 10, 40), //coal
                new GameItemVariableAmount(13440, 5, 10), //anglerfish
                new GameItemVariableAmount(3139, 5, 10), //potato cactus
                new GameItemVariableAmount(384, 5, 15), //raw shark

                new GameItemVariableAmount(1618, 5, 10), //uncut diamonds
                new GameItemVariableAmount(11230, 5, 10), //dragon dart
                new GameItemVariableAmount(11212, 10, 15), //dragon arrow
                new GameItemVariableAmount(560, 50, 100),  //death rune
                new GameItemVariableAmount(565, 50, 100), //blood rune       // COMMON
                new GameItemVariableAmount(220, 5, 15), //torstol
                new GameItemVariableAmount(454, 10, 40), //coal
                new GameItemVariableAmount(13440, 5, 10), //anglerfish
                new GameItemVariableAmount(3139, 5, 10), //potato cactus
                new GameItemVariableAmount(384, 5, 15), //raw shark

                new GameItemVariableAmount(1618, 5, 10), //uncut diamonds
                new GameItemVariableAmount(11230, 5, 10), //dragon dart
                new GameItemVariableAmount(11212, 10, 15), //dragon arrow
                new GameItemVariableAmount(560, 50, 100),  //death rune
                new GameItemVariableAmount(565, 50, 100), //blood rune       // COMMON
                new GameItemVariableAmount(220, 5, 15), //torstol
                new GameItemVariableAmount(454, 40, 80), //coal
                new GameItemVariableAmount(13440, 5, 10), //anglerfish
                new GameItemVariableAmount(3139, 5, 10), //potato cactus
                new GameItemVariableAmount(384, 5, 15), //raw shark


                new GameItemVariableAmount(1618, 20, 25), //uncut diamonds
                new GameItemVariableAmount(11230, 25, 30), //dragon dart      // UNCOMMON
                new GameItemVariableAmount(11212, 25, 30), //dragon arrow
                new GameItem(21027, 1), //dark relic
                new GameItem(11733, 1),  //overload (3)
                new GameItemVariableAmount(560, 100, 150),  //death rune
                new GameItemVariableAmount(565, 100, 150), //blood rune
                new GameItemVariableAmount(220, 10, 25), //torstol
                new GameItemVariableAmount(454, 100, 150), //coal
                new GameItemVariableAmount(13440, 10, 30), //anglerfish
                new GameItemVariableAmount(3139, 10, 17), //potato cactus
                new GameItemVariableAmount(384, 40, 50), //raw shark
                new GameItem(Items.RUNE_BATTLEAXE_NOTED, 4),
                new GameItem(Items.RUNE_PLATEBODY_NOTED, 4),
                new GameItem(Items.RUNE_CHAINBODY_NOTED, 4),


                new GameItem(21027, 1), //dark relic
                new GameItemVariableAmount(23686, 2, 3),  //divine combat 4     //UNCOMMON
                new GameItemVariableAmount(23734, 2, 3),  //divine range 4
                new GameItem(11733, 2),  //overload (3)




                new GameItem(Items.GOLD_ORE_NOTED, 360),
                new GameItem(Items.ADAMANTITE_ORE_NOTED, 150),
                new GameItem(Items.RUNITE_ORE_NOTED, 72),
                new GameItem(Items.GRIMY_CADANTINE_NOTED, 60),
                new GameItem(Items.GRIMY_AVANTOE_NOTED, 48),
                new GameItem(Items.GRIMY_DWARF_WEED_NOTED, 28),
                new GameItem(Items.GRIMY_TORSTOL_NOTED, 24),
                new GameItem(Items.BATTLESTAFF_NOTED, 18),


                new GameItemVariableAmount(9245, 10, 30),  //ONYX BOLTS E
                new GameItemVariableAmount(20849, 10, 45),  //DRAGON THROWN AXE     //RARISH
                new GameItemVariableAmount(22804, 10, 55),  //DRAGON KNIFE
                new GameItemVariableAmount(22804, 10, 55),  //DRAGON KNIFE
                new GameItemVariableAmount(392, 50, 150),  //MANTA RAY
                new GameItem(11733, 3),  //overload (3)

                new GameItemVariableAmount(23746, 2, 3)));  //divine mage 4


        items.put(LootRarity.RARE, Arrays.asList(
                new GameItem(22386, 1),  //metamorphic dust // keep here

                new GameItem(21000, 1),  //twisted buckler
                new GameItem(21015, 1),  //dinhs bulwark
                new GameItem(21012, 1),  //dragon hunter crossbow
                new GameItem(21006, 1),  //koadi wand
                new GameItem(21034, 1),  //arcane scroll                COMMON
                new GameItem(21000, 1),  //twisted buckler
                new GameItem(21015, 1),  //dinhs bulwark
                new GameItem(21012, 1),  //dragon hunter crossbow
                new GameItem(21006, 1),  //koadi wand
                new GameItem(21034, 1), //arcane scroll
                new GameItem(21000, 1),  //twisted buckler
                new GameItem(21015, 1),  //dinhs bulwark
                new GameItem(21012, 1),  //dragon hunter crossbow
                new GameItem(21006, 1),  //koadi wand
                new GameItem(21034, 1), //arcane scroll
                new GameItem(21000, 1),  //twisted buckler
                new GameItem(21015, 1),  //dinhs bulwark
                new GameItem(21012, 1),  //dragon hunter crossbow

                new GameItem(21000, 1),  //twisted buckler
                new GameItem(21015, 1),  //dinhs bulwark
                new GameItem(21012, 1),  //dragon hunter crossbow
                new GameItem(21006, 1),  //koadi wand
                new GameItem(21034, 1),  //arcane scroll                COMMON
                new GameItem(21000, 1),  //twisted buckler
                new GameItem(21015, 1),  //dinhs bulwark
                new GameItem(21012, 1),  //dragon hunter crossbow
                new GameItem(21006, 1),  //koadi wand
                new GameItem(21034, 1), //arcane scroll
                new GameItem(21000, 1),  //twisted buckler
                new GameItem(21015, 1),  //dinhs bulwark
                new GameItem(21012, 1),  //dragon hunter crossbow
                new GameItem(21006, 1),  //koadi wand
                new GameItem(21034, 1), //arcane scroll
                new GameItem(21000, 1),  //twisted buckler
                new GameItem(21015, 1),  //dinhs bulwark
                new GameItem(21012, 1),  //dragon hunter crossbow

                new GameItem(21006, 1),  //koadi wand
                new GameItem(21034, 1), //arcane scroll
                new GameItem(21000, 1),  //twisted buckler
                new GameItem(21015, 1),  //dinhs bulwark
                new GameItem(21012, 1),  //dragon hunter crossbow              COMMON
                new GameItem(21006, 1),  //koadi wand
                new GameItem(21034, 1), //arcane scroll
                new GameItem(21000, 1),  //twisted buckler
                new GameItem(21015, 1),  //dinhs bulwark
                new GameItem(21012, 1),  //dragon hunter crossbow
                new GameItem(21006, 1),  //koadi wand                          COMMON
                new GameItem(21034, 1), //arcane scroll
                new GameItem(21000, 1),  //twisted buckler
                new GameItem(21015, 1),  //dinhs bulwark
                new GameItem(21012, 1),  //dragon hunter crossbow
                new GameItem(21006, 1),  //koadi wand
                new GameItem(21034, 1), //arcane scroll                        COMMON
                new GameItem(21000, 1),  //twisted buckler
                new GameItem(21015, 1),  //dinhs bulwark
                new GameItem(21012, 1),  //dragon hunter crossbow
                new GameItem(21006, 1),  //koadi wand
                new GameItem(21034, 1), //arcane scroll                        COMMON
                new GameItem(21000, 1),  //twisted buckler
                new GameItem(21015, 1),  //dinhs bulwark
                new GameItem(21012, 1),  //dragon hunter crossbow
                new GameItem(21006, 1),  //koadi wand
                new GameItem(21034, 1), //arcane scroll
                new GameItem(21000, 1),  //twisted buckler
                new GameItem(21015, 1),  //dinhs bulwark
                new GameItem(21012, 1),  //dragon hunter crossbow
                new GameItem(21006, 1),  //koadi wand
                new GameItem(21034, 1), //arcane scroll                       COMMON
                new GameItem(21000, 1),  //twisted buckler
                new GameItem(21015, 1),  //dinhs bulwark
                new GameItem(21012, 1),  //dragon hunter crossbow
                new GameItem(21006, 1),  //koadi wand
                new GameItem(21034, 1), //arcane scroll
                new GameItem(21000, 1),  //twisted buckler
                new GameItem(21015, 1),  //dinhs bulwark
                new GameItem(21012, 1),  //dragon hunter crossbow
                new GameItem(21006, 1),  //koadi wand
                new GameItem(21034, 1), //arcane scroll



                new GameItem(21079, 1), //dex scroll
                new GameItem(21003, 1), //elder maul      	 	rares
                new GameItem(20784, 1),  //D claws
                new GameItem(21018, 1),//ancestral hat
                new GameItem(21021, 1),//ancestral top
                new GameItem(21024, 1),//ancestral bottom
                new GameItem(6806, 1),  //koranes imbue scroll
                new GameItem(20997, 1),  //twisted bow			RARES
                new GameItem(20851, 1)));  //olmlet pet
    }

}