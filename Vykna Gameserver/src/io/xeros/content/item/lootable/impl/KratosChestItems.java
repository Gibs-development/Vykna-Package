package io.xeros.content.item.lootable.impl;

import io.xeros.content.KratosChest;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KratosChestItems implements Lootable {


    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    static {
        items.put(LootRarity.COMMON, Arrays.asList(
                new GameItem(21046, 1),//15% chest rate tomb
                new GameItem(995, 500000), //coins
                new GameItem(11230, 40),//dragon darts
                new GameItem(20849, 50),//dragon thrownaxe
                new GameItem(22804, 45),//dragon knife
                new GameItem(537, 5 + Misc.random(30)),//dragon bones
                new GameItem(1306, 3),//dragon longsword
                new GameItem(4087, 1),//dragon platelegs
                new GameItem(4585, 1),//dragon plateskirt
                new GameItem(11232, 50 + Misc.random(10)),//dragon darts
                new GameItem(11840),//dragon boots
                new GameItem(6889),//mages book
                new GameItem(2364, 100),//runite bar
                new GameItem(1514, 100),// magic logs
                new GameItem(1632, 50),//uncut dragonstone

                new GameItem(995, 500000), //coins
                new GameItem(11230, 12),//dragon darts
                new GameItem(20849, 32),//dragon thrownaxe
                new GameItem(22804, 35),//dragon knife
                new GameItem(537, 5 + Misc.random(30)),//dragon bones
                new GameItem(1306, 3),//dragon longsword
                new GameItem(4087, 1),//dragon platelegs
                new GameItem(4585, 1),//dragon plateskirt
                new GameItem(11232, 50 + Misc.random(10)),//dragon darts
                new GameItem(11840),//dragon boots
                new GameItem(6889),//mages book
                new GameItem(2364, 100),//runite bar
                new GameItem(1514, 100),// magic logs
                new GameItem(1632, 50)));//uncut dragonstone


        items.put(LootRarity.UNCOMMON, Arrays.asList(
                new GameItem(995, 600000), //coins
                new GameItem(11230, 12),//dragon darts
                new GameItem(20849, 32),//dragon thrownaxe
                new GameItem(22804, 35),//dragon knife
                new GameItem(537, 5 + Misc.random(30)),//dragon bones
                new GameItem(1306, 3),//dragon longsword
                new GameItem(4087, 1),//dragon platelegs
                new GameItem(4585, 1),//dragon plateskirt
                new GameItem(11232, 50 + Misc.random(10)),//dragon darts
                new GameItem(11840),//dragon boots
                new GameItem(6889),//mages book
                new GameItem(2364, 100),//runite bar
                new GameItem(1514, 100),// magic logs

                new GameItem(995, 600000), //coins
                new GameItem(11230, 12),//dragon darts
                new GameItem(20849, 32),//dragon thrownaxe
                new GameItem(22804, 35),//dragon knife
                new GameItem(537, 5 + Misc.random(30)),//dragon bones
                new GameItem(1306, 3),//dragon longsword
                new GameItem(4087, 1),//dragon platelegs
                new GameItem(4585, 1),//dragon plateskirt
                new GameItem(11232, 50 + Misc.random(10)),//dragon darts
                new GameItem(11840),//dragon boots
                new GameItem(6889),//mages book
                new GameItem(2364, 100),//runite bar
                new GameItem(1514, 100),// magic logs


                new GameItem(9426, 30 + Misc.random(90)),

                new GameItem(22093, 1),// vote streak key

                new GameItem(3204, 1)));

        items.put(LootRarity.RARE, Arrays.asList(
                new GameItem(11230, 180),//dragon darts
                new GameItem(20849, 132),//dragon thrownaxe
                new GameItem(22804, 135),//dragon knife
                new GameItem(537, 5 + Misc.random(30)),//dragon bones                 //Common
                new GameItem(1306, 3),//dragon longsword
                new GameItem(4087, 1),//dragon platelegs
                new GameItem(4585, 1),//dragon plateskirt
                new GameItem(11232, 100 + Misc.random(10)),//dragon darts
                new GameItem(11840),//dragon boots
                new GameItem(6889),//mages book
                new GameItem(2364, 100),//runite bar
                new GameItem(1514, 100),// magic logs

                new GameItem(11230, 180),//dragon darts
                new GameItem(20849, 132),//dragon thrownaxe
                new GameItem(22804, 135),//dragon knife
                new GameItem(537, 5 + Misc.random(30)),//dragon bones                 //Common
                new GameItem(1306, 3),//dragon longsword
                new GameItem(4087, 1),//dragon platelegs
                new GameItem(4585, 1),//dragon plateskirt
                new GameItem(11232, 100 + Misc.random(10)),//dragon darts
                new GameItem(11840),//dragon boots
                new GameItem(6889),//mages book
                new GameItem(2364, 100),//runite bar
                new GameItem(1514, 100),// magic logs

                new GameItem(11230, 180),//dragon darts
                new GameItem(20849, 132),//dragon thrownaxe
                new GameItem(22804, 135),//dragon knife
                new GameItem(537, 5 + Misc.random(30)),//dragon bones                 //Common
                new GameItem(1306, 3),//dragon longsword
                new GameItem(4087, 1),//dragon platelegs
                new GameItem(4585, 1),//dragon plateskirt
                new GameItem(11232, 100 + Misc.random(10)),//dragon darts
                new GameItem(11840),//dragon boots
                new GameItem(6889),//mages book
                new GameItem(2364, 100),//runite bar
                new GameItem(1514, 100),// magic logs

                new GameItem(11230, 180),//dragon darts
                new GameItem(20849, 132),//dragon thrownaxe
                new GameItem(22804, 135),//dragon knife
                new GameItem(537, 5 + Misc.random(30)),//dragon bones                 //Common
                new GameItem(1306, 3),//dragon longsword
                new GameItem(4087, 1),//dragon platelegs
                new GameItem(4585, 1),//dragon plateskirt
                new GameItem(11232, 100 + Misc.random(10)),//dragon darts
                new GameItem(11840),//dragon boots
                new GameItem(6889),//mages book
                new GameItem(2364, 100),//runite bar
                new GameItem(1514, 100),// magic logs

                new GameItem(4151, 1),//whip
                new GameItem(4151, 1),//whip
                new GameItem(4151, 1),//whip
                new GameItem(4151, 1),//whip
                new GameItem(4151, 1),//whip
                new GameItem(4151, 1),//whip
                new GameItem(4151, 1),//whip
                new GameItem(4151, 1),//whip
                new GameItem(4151, 1),//whip
                new GameItem(4151, 1),//whip
                new GameItem(4151, 1),//whip
                new GameItem(4151, 1),//whip
                new GameItem(4151, 1),//whip
                new GameItem(4151, 1),//whip
                new GameItem(4151, 1),//whip
                new GameItem(4151, 1),//whip
                new GameItem(4151, 1),//whip
                new GameItem(4151, 1),//whip
                new GameItem(4151, 1),//whip
                new GameItem(4151, 1),//whip
                new GameItem(4151, 1),//whip
                new GameItem(22093, 3),// vote streak key
                new GameItem(22622, 1), //statius warhammer
                new GameItem(22610, 1), //vesta spear                  //RARE
                new GameItem(22613, 1), //vesta longsword

                //new GameItem(34037, 1), //death cape
                new GameItem(12468, 1), //New dragon kite
                new GameItem(30022, 1), //kratos pet
                new GameItem(30122, 1), //kratos pet                   //RARE

                new GameItem(12877, 1)));

    }



    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

    @Override
    public void roll(Player c) {

    }
}
