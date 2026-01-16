package io.xeros.content.minigames.toa.bosses;

import io.xeros.content.instances.InstancedArea;
import io.xeros.model.entity.player.Player;
import io.xeros.util.logging.Log;
import io.xeros.util.logging.PlayerLog;

import java.util.Set;
import java.util.stream.Collectors;

public class CompletedToaLog extends PlayerLog {

    private final InstancedArea instance;

    public CompletedToaLog(Player player, InstancedArea instance) {
        super(player);
        this.instance = instance;
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("completed_toa");
    }

    @Override
    public String getLoggedMessage() {
        String players = "";
        if (instance != null) {
            players = instance.getPlayers().stream().map(Player::getLoginNameLower).collect(Collectors.joining(", "));
        }
        return "Completed toa with " + players;
    }
}

