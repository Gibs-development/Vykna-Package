package io.xeros.content.minigames.toa.instance;

import io.xeros.model.entity.player.Player;
import io.xeros.util.logging.Log;
import io.xeros.util.logging.PlayerLog;

import java.util.Set;
import java.util.stream.Collectors;

public class DiedAtToaLog extends PlayerLog {

    private final ToaInstance instance;

    public DiedAtToaLog(Player player, ToaInstance instance) {
        super(player);
        this.instance = instance;
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("died_at_toa");
    }

    @Override
    public String getLoggedMessage() {
        String players = "";
        if (instance != null) {
            players = instance.getPlayers().stream().map(Player::getLoginNameLower).collect(Collectors.joining(", "));
        }
        return "Died at toa with " + players;
    }
}
