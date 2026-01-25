package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

public class Newanim extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		int id = Integer.parseInt(input) + 14000;
		player.startAnimation(id);
		player.sendMessage("Playing animation: " + id);
	}

}
