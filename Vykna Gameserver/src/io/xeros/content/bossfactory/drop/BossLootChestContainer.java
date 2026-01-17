package io.xeros.content.bossfactory.drop;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.inventory.Inventory;

public class BossLootChestContainer {
    private static final int CAPACITY = 128;

    private final Inventory inventory = new Inventory(CAPACITY);

    public void add(Player player, GameItem item) {
        Optional<GameItem> remaining = inventory.add(item.copy());
        player.sendMessage("Your boss loot chest receives: {}.", item.getFormattedString());
        remaining.ifPresent(leftover -> player.sendMessage("Your boss loot chest is full and rejected: {}.", leftover.getFormattedString()));
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String encode() {
        return inventory.buildList().stream()
                .map(it -> it.getId() + ":" + it.getAmount())
                .collect(Collectors.joining(";"));
    }

    public void decode(String value) {
        if (value == null || value.isEmpty()) {
            return;
        }
        String[] data = value.split(";");
        List<GameItem> items = java.util.Arrays.stream(data)
                .map(it -> it.split(":"))
                .map(split -> new GameItem(Integer.parseInt(split[0]), Integer.parseInt(split[1])))
                .collect(Collectors.toList());
        GameItem[] itemArray = new GameItem[items.size()];
        items.toArray(itemArray);
        inventory.set(itemArray);
    }
}
