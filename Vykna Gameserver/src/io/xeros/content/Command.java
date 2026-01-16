package io.xeros.content;


import io.xeros.model.entity.player.Player;

/**
 * Command
 * @author Mayhem Team
 *
 */
public abstract interface Command {
	
	/**
	 * Handles the commands
	 * @param player
	 * @param parser
	 */
	public abstract boolean handleCommand(Player player, CommandParser parser) throws Exception;

	/**
	 * Checks if player meets requirement(s)
	 * @param player
	 * @return
	 */
	public abstract boolean meetsRequirements(Player player);
}
