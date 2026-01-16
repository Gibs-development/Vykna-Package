package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.content.cutscene.Cutscenes;
import io.xeros.model.entity.player.Player;

public class Intro extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        Cutscenes.startIntro(player);
        player.sendMessage("Started intro cutscene.");
    }
}
