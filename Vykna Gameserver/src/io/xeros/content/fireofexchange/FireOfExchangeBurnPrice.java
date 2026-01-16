package io.xeros.content.fireofexchange;

import com.google.common.base.Preconditions;
import io.xeros.content.bosspoints.JarsToPoints;
import io.xeros.model.Items;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ItemAssistant;
import io.xeros.model.shops.ShopItem;
import io.xeros.model.world.ShopHandler;

import java.util.*;
import java.util.stream.Collectors;

public class FireOfExchangeBurnPrice {

    public static int SHOP_ID;

    public static void init() {
        checkPrices();
        createBurnPriceShop();
    }

    public static void createBurnPriceShop() {
        Map<Integer, Integer> burnPrices = new HashMap<>();
        for (int i = 0; i < 60_000; i++) {
            int price = getBurnPrice(null, i, false);
            if (price > 0)
                burnPrices.put(i, price);
        }

        List<Map.Entry<Integer, Integer>> list = new ArrayList<>(burnPrices.entrySet());

        list.sort((a, b) -> {
            int comparison = b.getValue().compareTo(a.getValue());
            if (comparison == 0) {
                return a.getKey().compareTo(b.getKey());
            }

            return comparison;
        });

        List<ShopItem> shopItems = list.stream().map(it -> new ShopItem(it.getKey() + 1 /* shops need this +1 lol */,
                it.getValue(), it.getValue())).collect(Collectors.toList());
        SHOP_ID = ShopHandler.addShopAnywhere("Fire of Exchange Rates", shopItems);
    }

    private static void checkPrices() {
        for (int i = 0; i < 40_000; i++) {
            int shopBuyPrice = FireOfExchange.getExchangeShopPrice(i);
            int burn = getBurnPrice(null, i, false);
            if (shopBuyPrice != Integer.MAX_VALUE) {
                Preconditions.checkState(shopBuyPrice >= burn, "Item burns for more than shop price: " + i);
            }
        }
    }

    public static void openExchangeRateShop(Player player) {
        player.getShops().openShop(SHOP_ID);
        player.sendMessage("<icon=282> @red@You cannot buy anything here.@bla@ This interface only displays @pur@Fire of Exchange Rates!");
    }

    public static boolean hasValue(int itemId) {
        return getBurnPrice(null, itemId, false) != -1;
    }

    /**
     * Burning price.
     */
    public static int getBurnPrice(Player c, int itemId, boolean displayMessage) {
        if (Arrays.stream(JarsToPoints.JARS).anyMatch(it -> itemId == it)) {
            return JarsToPoints.FOE_POINTS;
        }

        switch (itemId) {
            //Boss jars
            case 12885://Sand
            case 12936://swamp
            case 13245://souls
            case 13277://miasma
            case 19701://darkness
            case 21745://stone
            case 22106://decay
            case 23064://chemicals
            case 23525://eyes
            case 24495://dreams
            case 12007://dirt
                return 2500;
            //Dragon tools
            case 11920://Dragon pickaxe
                return 3000;
            case 6739://Dragon axe
                return 500;
            //SKILLING OUTFITS
            case 5553://Thieving
            case 5554://
            case 5555://
            case 5556://
            case 5557://
            case 10933://Lumber jack
            case 10939://
            case 10940://
            case 10941://
            case 12013://Mining
            case 12014://
            case 12015://
            case 12016://
            case 13258://Fishing
            case 13259://
            case 13260://
            case 13261://
            case 13640://Farming
            case 13642://
            case 13644://
            case 13646://
            case 20704://Firemaking
            case 20706://
            case 20708://
            case 20710://
            case 11850://Agility graceful
            case 11852://
            case 11854://
            case 11856://
            case 11858://
            case 11860://
                return 350;
            //MONSTER HEADS
            case 7979://Abyssal head
            case 7980://Kbd heads
            case 7981://Kq head
            case 21275://Dark claw
            case 23077://Alchemical hydra heads
                return 350;
            case 2425://Vorkath's head
                return 500;
            //SKILLING ARTEFACTS
            case 11180://ancient coin
                return 200;
            case 681://ancient talisman
                return 300;
            case 9034://golden stat
                return 2500;
            //CHEST ARTIFACTS
            case 21547://small enriched bone
                return 400;
            case 21549://medium enriched bone
                return 650;
            case 21551://large enriched bone
                return 1200;
            case 21553://rare enriched bone
                return 1550;
            //special items
            case 21046://chest rate relic
            case 22316://sword of rogue
                return 500;
            //FOE PETS START
            case 30010://postie pete
                return 16250;
            case 30012://toucan
            case 30011://imp
                return 19500;
            case 30013://penguin king
                return 22750;
            case 30014://klik
                return 97500;
            case 30015://melee pet
            case 30016://range pet
            case 30017://magic pet
                return 48750;
            case 30018://healer
            case 30019://prayer
                return 52000;
            case 30020://corrupt beast
                return 325000;
            case 30021://roc pet
                return 325000;
            case 30022://yama pet
                return 975000;
            case 23939://seren
                return 65000;
            //dark pets
            case 30110://postie pete
                return 16250;
            case 30112://toucan
            case 30111://imp
                return 19500;
            case 30113://penguin king
                return 22750;
            case 30114://klik
                return 97500;
            case 30115://melee pet
            case 30116://range pet
            case 30117://magic pet
                return 48750;
            case 30118://healer
            case 30119://prayer
                return 52000;
            case 30120://corrupt beast
                return 325000;
            case 30121://roc pet
                return 325000;
            case 30122://yama pet
                return 975000;
            case 30123://seren
                return 65000;
            //FOE PETS END


            case 691://foe cert
                return 10000;
            case 692://foe cert
                return 25000;
            case 693://foe cert
                return 50000;
            case 696://foe cert
                return 250000;
            case 8866://uim key
                return 100;
            case 8868://perm uim key
                return 4000;


            //STACKABLE ITEMS
            case 18825://ancient coin 30% decrease
                return 140;
            case 16623://ancient talisman 30% decrease
                return 210;
            default:
                if (c != null && displayMessage)
                    c.sendMessage("@red@You cannot exchange @blu@" + ItemAssistant.getItemName(itemId) + " for @red@ Exchange Points.");
                return -1;
        }
    }


}
