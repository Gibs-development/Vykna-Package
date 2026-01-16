package io.xeros.content;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaledictusItems implements Lootable {

        private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    static {

        items.put(LootRarity.COMMON, Arrays.asList(
                new GameItem(7947, 1),
                new GameItem(3025, 1),
                new GameItem(537, 1),
                new GameItem(892, 1),
                new GameItem(21820, 1),
                new GameItem(2364, 1),
                new GameItem(452, 1),
                new GameItem(454, 1),
                new GameItem(2362, 1),
                new GameItem(1516, 1),
                new GameItem(1514, 1),
                new GameItem(1632, 1),
                new GameItem(9245, 1),
                new GameItem(212, 1),
                new GameItem(214, 1),
                new GameItem(216, 1),
                new GameItem(218, 1),
                new GameItem(220, 1),
                new GameItem(384, 1),
                new GameItem(13440, 1),
                new GameItem(7937, 1),
                new GameItem(1748, 1),
                new GameItem(44, 1),
                new GameItem(824, 1),
                new GameItem(2996, 1),
                new GameItem(3050, 1),
                new GameItem(3052, 1),
                new GameItem(11935, 1)));

        items.put(LootRarity.UNCOMMON, Arrays.asList(
                new GameItem(4716, 1),
                new GameItem(4718, 1),
                new GameItem(4720, 1),
                new GameItem(4722, 1),
                new GameItem(4708, 1),
                new GameItem(4710, 1),
                new GameItem(4712, 1),
                new GameItem(4714, 1),
                new GameItem(4724, 1),
                new GameItem(4726, 1),
                new GameItem(4728, 1),
                new GameItem(4730, 1),
                new GameItem(4753, 1),
                new GameItem(4755, 1),
                new GameItem(4757, 1),
                new GameItem(4759, 1),
                new GameItem(4732, 1),
                new GameItem(4734, 1),
                new GameItem(4736, 1),
                new GameItem(4738, 1),
                new GameItem(4716, 1),
                new GameItem(4718, 1),
                new GameItem(4720, 1),
                new GameItem(4722, 1),
                new GameItem(4708, 1),
                new GameItem(4710, 1),
                new GameItem(4712, 1),
                new GameItem(4714, 1),
                new GameItem(4724, 1),
                new GameItem(4726, 1),
                new GameItem(4728, 1),
                new GameItem(4730, 1),
                new GameItem(4753, 1),
                new GameItem(4755, 1),
                new GameItem(4757, 1),
                new GameItem(4759, 1),
                new GameItem(4732, 1),
                new GameItem(4734, 1),
                new GameItem(4736, 1),
                new GameItem(4738, 1),
                new GameItem(12875, 1),
                new GameItem(12877, 1),
                new GameItem(12873, 1),
                new GameItem(12883, 1),
                new GameItem(12879, 1),
                new GameItem(12881, 1)));


        items.put(LootRarity.RARE, Arrays.asList(

                new GameItem(6828, 1),
                new GameItem(6199, 1),
                new GameItem(22557, 1),
                new GameItem(692, 1),
                new GameItem(691, 1),
                new GameItem(19707, 1),
                new GameItem(22542, 1),
                new GameItem(22552, 1),
                new GameItem(22547, 1),
                new GameItem(22885, 1),
                new GameItem(22883, 1),
                new GameItem(22881, 1),
                new GameItem(6112, 1),
                new GameItem(20903, 1),
                new GameItem(20909, 1),
                new GameItem(22869, 1),
                new GameItem(20906, 1),
                new GameItem(4205, 1),
                new GameItem(22613, 1),
                new GameItem(22610, 1),
                new GameItem(22616, 1),
                new GameItem(22619, 1),
                new GameItem(22647, 1),
                new GameItem(22650, 1),
                new GameItem(22653, 1),
                new GameItem(22656, 1),
                new GameItem(22622, 1),
                new GameItem(22625, 1),
                new GameItem(22628, 1),
                new GameItem(22631, 1),
                new GameItem(22638, 1),
                new GameItem(22641, 1),
                new GameItem(22644, 1),
                new GameItem(13652, 1),
                new GameItem(2403, 1),
                new GameItem(6769, 1),
                new GameItem(13346, 1),
                new GameItem(6828, 1),
                new GameItem(693, 1),
                new GameItem(692, 1),
                new GameItem(13576, 1)));
    }



        @Override
        public Map<LootRarity, List<GameItem>> getLoot() {
            return items;
        }

        @Override
        public void roll(Player c) {

        }

}
