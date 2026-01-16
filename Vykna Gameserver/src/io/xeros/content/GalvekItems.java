package io.xeros.content;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GalvekItems implements Lootable {

        private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    static {

        items.put(LootRarity.COMMON, Arrays.asList(
                new GameItem(535, 1),
                new GameItem(537, 1),
                new GameItem(11944, 1),
                new GameItem(527, 1),
                new GameItem(11212, 1),
                new GameItem(11230, 1),
                new GameItem(200, 1),
                new GameItem(1514, 1),
                new GameItem(21880, 1),
                new GameItem(6816, 1),
                new GameItem(392, 1)));

        items.put(LootRarity.UNCOMMON, Arrays.asList(
                new GameItem(10589, 1),
                new GameItem(6809, 1),
                new GameItem(10564, 1),
                new GameItem(21643, 1),
                new GameItem(3122, 1),
                new GameItem(4153, 1),
                new GameItem(12848, 1),
                new GameItem(21298, 1),
                new GameItem(21301, 1),
                new GameItem(21304, 1),
                new GameItem(6568, 1),
                new GameItem(4151, 1),
                new GameItem(21009, 1),
                new GameItem(1249, 1),
                new GameItem(7158, 1),
                new GameItem(1434, 1),
                new GameItem(12875, 1),
                new GameItem(12877, 1),
                new GameItem(12873, 1),
                new GameItem(12881, 1),
                new GameItem(12883, 1),
                new GameItem(12879, 1)));


        items.put(LootRarity.RARE, Arrays.asList(

                new GameItem(11335, 1),
                new GameItem(21892, 1),
                new GameItem(3140, 1),
                new GameItem(21895, 1),
                new GameItem(1187, 1),
                new GameItem(6828, 1),
                new GameItem(6199, 1),
                new GameItem(691, 1),
                new GameItem(692, 1),
                new GameItem(21028, 1),
                new GameItem(21226, 1),
                new GameItem(13346, 1),
                new GameItem(21633, 1),
                new GameItem(21637, 1),
                new GameItem(13652, 1),
                new GameItem(13576, 1),
                new GameItem(6769, 1),
                new GameItem(693, 1),
                new GameItem(6828, 1),
                new GameItem(12417, 1),
                new GameItem(22242, 1),
                new GameItem(12416, 1),
                new GameItem(12415, 1),
                new GameItem(12414, 1),
                new GameItem(22234, 1),
                new GameItem(22244, 1),
                new GameItem(20002, 1),
                new GameItem(20143, 1),
                new GameItem(12771, 1)));
    }



        @Override
        public Map<LootRarity, List<GameItem>> getLoot() {
            return items;
        }

        @Override
        public void roll(Player c) {

        }

}
