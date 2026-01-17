package io.xeros.content.bossfactory.drop;

import java.util.List;

import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.save.PlayerSaveEntry;

public class BossLootChestSaveEntry implements PlayerSaveEntry {
    private static final String UNLOCKED_KEY = "boss_loot_chest_unlocked";
    private static final String ITEMS_KEY = "boss_loot_chest_items";

    @Override
    public List<String> getKeys(Player player) {
        return List.of(UNLOCKED_KEY, ITEMS_KEY);
    }

    @Override
    public boolean decode(Player player, String key, String value) {
        if (UNLOCKED_KEY.equals(key)) {
            player.setBossLootChestUnlocked(Boolean.parseBoolean(value));
            return true;
        }
        if (ITEMS_KEY.equals(key)) {
            player.getBossLootChest().decode(value);
            return true;
        }
        return false;
    }

    @Override
    public String encode(Player player, String key) {
        if (UNLOCKED_KEY.equals(key)) {
            return Boolean.toString(player.isBossLootChestUnlocked());
        }
        if (ITEMS_KEY.equals(key)) {
            return player.getBossLootChest().encode();
        }
        return "";
    }

    @Override
    public void login(Player player) {
    }
}
