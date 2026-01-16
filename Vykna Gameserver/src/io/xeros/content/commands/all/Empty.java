package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

/**
 * Empty the inventory of the player.
 * 
 * @author Emiel
 */
public class Empty extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		c.getDH().sendDialogues(450,-1);
	}
	@Override
	public Optional<String> getDescription() {
		return Optional.of("Empty your inventory");
	}
}
