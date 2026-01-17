package io.xeros.content.bossfactory.drop;

import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.items.GameItem;

public class GlobalLootChestReceiver implements DropReceiver {
    @Override
    public void receive(Player player, GameItem item, Position position) {
        player.getBossLootChest().add(player, item);
    }
}
