package io.xeros.content;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.content.skills.hunter.impling.ItemRarity;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlayerChestItems implements Lootable {

        private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    static {

        /*
         * Tier 1
         */
        items.put(LootRarity.COMMON, Arrays.asList(
                new GameItem(1618, Misc.random(20, 50)),//uncut diamond
                new GameItem(1620, Misc.random(20, 50)),//uncut ruby
                new GameItem(454, Misc.random(50, 150)),//coal
                new GameItem(445, Misc.random(50, 150)),//gold
                new GameItem(11237, Misc.random(10, 50)),//dragon arrow tips
                new GameItem(441, Misc.random(50, 85)),//iron ore
                new GameItem(1164, Misc.random(1)),//rune fullhelm
                new GameItem(1128, Misc.random(1)),//rune platebody
                new GameItem(1080, Misc.random(1)),//rune platelegs
                new GameItem(360, Misc.random(30, 40)),//tuna
                new GameItem(378, Misc.random(40, 60)),//lobster
                new GameItem(372, Misc.random(20, 50)),//swordfish
                new GameItem(384, Misc.random(15, 25)),//shark
                new GameItem(396, Misc.random(30, 40)),// sea turtle
                new GameItem(12934, Misc.random(10, 50))));//zulrah scale

        /*
         * Tier 2
         */
        items.put(LootRarity.UNCOMMON, Arrays.asList(
                new GameItem(1618, Misc.random(125, 175)),//uncut diamond
                new GameItem(1620, Misc.random(125, 175)),//uncut ruby
                new GameItem(454, Misc.random(400, 600)),//coal
                new GameItem(445, Misc.random(200, 250)),//gold
                new GameItem(11237, Misc.random(100, 300)),//dragon arrow tips
                new GameItem(441, Misc.random(350, 500)),//iron ore
                new GameItem(1164, Misc.random(3, 5)),//rune fullhelm
                new GameItem(1128, Misc.random(2, 3)),//rune platebody
                new GameItem(1080, Misc.random(2, 3)),//rune platelegs
                new GameItem(360, Misc.random(100, 350)),//tuna
                new GameItem(378, Misc.random(100, 300)),//lobster
                new GameItem(372, Misc.random(100, 300)),//swordfish
                new GameItem(7945, Misc.random(100, 300)),//monkfish
                new GameItem(384, Misc.random(200, 400)),//shark
                new GameItem(396, Misc.random(100, 250)),// sea turtle
                new GameItem(390, Misc.random(80, 160)),//manta
                new GameItem(452, Misc.random(30, 55)),// runite ore
                new GameItem(2354, Misc.random(300, 500)),//steel bar
                new GameItem(1514, Misc.random(150, 300)),// magic logs
                new GameItem(11232, Misc.random(110, 170)),//dragon dart tip
                new GameItem(5295, Misc.random(15, 25)),//rannar seed
                new GameItem(5300, Misc.random(15, 25)),//snap seed
                new GameItem(5304, Misc.random(15, 25)),//torstol seed
                new GameItem(7937, Misc.random(600, 900)),//pure ess
                new GameItem(12934, Misc.random(150, 250))));//zulrah scale

        /*
         * Tier 3
         */
        items.put(LootRarity.RARE, Arrays.asList(
                new GameItem(1618, Misc.random(135, 185)),//uncut diamond
                new GameItem(1620, Misc.random(135, 185)),//uncut ruby
                new GameItem(454, Misc.random(400, 600)),//coal
                new GameItem(445, Misc.random(200, 250)),//gold
                new GameItem(11237, Misc.random(100, 300)),//dragon arrow tips
                new GameItem(441, Misc.random(450, 500)),//iron ore
                new GameItem(1164, Misc.random(3, 6)),//rune fullhelm
                new GameItem(1128, Misc.random(2, 4)),//rune platebody
                new GameItem(1080, Misc.random(2, 4)),//rune platelegs
                new GameItem(360, Misc.random(100, 350)),//tuna
                new GameItem(378, Misc.random(100, 300)),//lobster
                new GameItem(372, Misc.random(100, 300)),//swordfish
                new GameItem(7945, Misc.random(100, 300)),//monkfish
                new GameItem(384, Misc.random(300, 500)),//shark
                new GameItem(396, Misc.random(100, 250)),// sea turtle
                new GameItem(390, Misc.random(80, 160)),//manta
                new GameItem(452, Misc.random(40, 60)),// runite ore
                new GameItem(2354, Misc.random(300, 500)),//steel bar
                new GameItem(1514, Misc.random(200, 400)),// magic logs
                new GameItem(11232, Misc.random(150, 200)),//dragon dart tip
                new GameItem(5295, Misc.random(15, 25)),//rannar seed
                new GameItem(5300, Misc.random(15, 25)),//snap seed
                new GameItem(5304, Misc.random(15, 25)),//torstol seed
                new GameItem(7937, Misc.random(600, 900)),//pure ess
                new GameItem(12934, Misc.random(200, 600)),//zulrah scale
        new GameItem(1618, Misc.random(135, 185)),//uncut diamond
                new GameItem(1620, Misc.random(135, 185)),//uncut ruby
                new GameItem(454, Misc.random(400, 600)),//coal
                new GameItem(445, Misc.random(200, 250)),//gold
                new GameItem(11237, Misc.random(100, 300)),//dragon arrow tips
                new GameItem(441, Misc.random(450, 500)),//iron ore
                new GameItem(1164, Misc.random(4, 7)),//rune fullhelm
                new GameItem(1128, Misc.random(3, 5)),//rune platebody
                new GameItem(1080, Misc.random(3, 5)),//rune platelegs
                new GameItem(360, Misc.random(100, 350)),//tuna
                new GameItem(378, Misc.random(100, 300)),//lobster
                new GameItem(372, Misc.random(100, 300)),//swordfish
                new GameItem(7945, Misc.random(100, 300)),//monkfish
                new GameItem(384, Misc.random(350, 600)),//shark
                new GameItem(396, Misc.random(100, 250)),// sea turtle
                new GameItem(390, Misc.random(80, 160)),//manta
                new GameItem(452, Misc.random(50, 70)),// runite ore
                new GameItem(2354, Misc.random(300, 500)),//steel bar
                new GameItem(1514, Misc.random(250, 450)),// magic logs
                new GameItem(11232, Misc.random(170, 250)),//dragon dart tip
                new GameItem(5295, Misc.random(15, 25)),//rannar seed
                new GameItem(5300, Misc.random(15, 25)),//snap seed
                new GameItem(5304, Misc.random(15, 25)),//torstol seed
                new GameItem(7937, Misc.random(1000, 2000)),//pure ess
                new GameItem(12934, Misc.random(200, 600)),//zulrah scale
                new GameItem(5304, Misc.random(80, 120)),//torstol seed
                new GameItem(7937, Misc.random(1000, 5000)),//pure ess               // common
                new GameItem(12934, Misc.random(600, 750)),//zulrah scale

                new GameItem(5304, Misc.random(80, 120)),//torstol seed
                new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
                new GameItem(12934, Misc.random(600, 750)),//zulrah scale

                new GameItem(5304, Misc.random(80, 120)),//torstol seed
                new GameItem(7937, Misc.random(1000, 5000)),//pure ess                  // common
                new GameItem(12934, Misc.random(600, 750)),//zulrah scale

                new GameItem(5304, Misc.random(80, 120)),//torstol seed
                new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
                new GameItem(12934, Misc.random(600, 750)),//zulrah scale

                new GameItem(5304, Misc.random(80, 120)),//torstol seed
                new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
                new GameItem(12934, Misc.random(600, 750)),//zulrah scale


                new GameItem(5304, Misc.random(80, 120)),//torstol seed
                new GameItem(7937, Misc.random(1000, 5000)),//pure ess               // common
                new GameItem(12934, Misc.random(600, 750)),//zulrah scale

                new GameItem(5304, Misc.random(80, 120)),//torstol seed
                new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
                new GameItem(12934, Misc.random(600, 750)),//zulrah scale

                new GameItem(5304, Misc.random(80, 120)),//torstol seed
                new GameItem(7937, Misc.random(1000, 5000)),//pure ess                  // common
                new GameItem(12934, Misc.random(600, 750)),//zulrah scale

                new GameItem(5304, Misc.random(80, 120)),//torstol seed
                new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
                new GameItem(12934, Misc.random(600, 750)),//zulrah scale

                new GameItem(5304, Misc.random(80, 120)),//torstol seed
                new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
                new GameItem(12934, Misc.random(600, 750)),//zulrah scale


                new GameItem(5304, Misc.random(80, 120)),//torstol seed
                new GameItem(7937, Misc.random(1000, 5000)),//pure ess               // common
                new GameItem(12934, Misc.random(600, 750)),//zulrah scale

                new GameItem(5304, Misc.random(80, 120)),//torstol seed
                new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
                new GameItem(12934, Misc.random(600, 750)),//zulrah scale

                new GameItem(5304, Misc.random(80, 120)),//torstol seed
                new GameItem(7937, Misc.random(1000, 5000)),//pure ess                  // common
                new GameItem(12934, Misc.random(600, 750)),//zulrah scale

                new GameItem(5304, Misc.random(80, 120)),//torstol seed
                new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
                new GameItem(12934, Misc.random(600, 750)),//zulrah scale

                new GameItem(5304, Misc.random(80, 120)),//torstol seed
                new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
                new GameItem(12934, Misc.random(600, 750)),//zulrah scale


                new GameItem(5304, Misc.random(80, 120)),//torstol seed
                new GameItem(7937, Misc.random(1000, 5000)),//pure ess               // common
                new GameItem(12934, Misc.random(600, 750)),//zulrah scale

                new GameItem(5304, Misc.random(80, 120)),//torstol seed
                new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
                new GameItem(12934, Misc.random(600, 750)),//zulrah scale

                new GameItem(5304, Misc.random(80, 120)),//torstol seed
                new GameItem(7937, Misc.random(1000, 5000)),//pure ess                  // common
                new GameItem(12934, Misc.random(600, 750)),//zulrah scale

                new GameItem(5304, Misc.random(80, 120)),//torstol seed
                new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
                new GameItem(12934, Misc.random(600, 750)),//zulrah scale

                new GameItem(5304, Misc.random(80, 120)),//torstol seed
                new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
                new GameItem(12934, Misc.random(600, 750)),//zulrah scale


                new GameItem(19547, 1),//zenyte
                new GameItem(19553, 1),//torture
                new GameItem(19550, 1),//suffering
                new GameItem(4151, 1),//whip
                new GameItem(12927, 1),//serp vis
                new GameItem(12932, 1),//magic fang
                new GameItem(13227, 1),//boot crystals
                new GameItem(13229, 1),//peg crystal
                new GameItem(13231, 1),//prim crystal
                new GameItem(12004, 1)));//tent

    }



        @Override
        public Map<LootRarity, List<GameItem>> getLoot() {
            return items;
        }

        @Override
        public void roll(Player c) {

        }

}
