package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

import java.util.Optional;

/**
 * Simple command to teleport to afk
 * 
 * @author C.T
 *
 */
public class Afk extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (c.getPosition().inWild()) {
			c.sendMessage("You can only use this command outside the wilderness.");
			return;
		}
		c.getPA().startTeleport(3080, 3461, 0, "modern", false);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teleports you to the afk area.");
	}
}
