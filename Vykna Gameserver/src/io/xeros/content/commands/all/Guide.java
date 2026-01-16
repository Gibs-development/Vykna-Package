package io.xeros.content.commands.all;

import io.xeros.Configuration;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

import java.util.Optional;

/**
 * Open the forums in the default web browser.
 * 
 * @author Emiel
 */
public class Guide extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		c.getPA().sendFrame126(Configuration.DISCORD_INVITE, 12000);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Opens our discord server with all our guides.");
	}

}
