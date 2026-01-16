package io.xeros.content.commands.admin;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;

/**
 * Shows the bank of a given player.
 * 
 * @author Emiel
 */
public class Checkbank extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (PlayerHandler.updateRunning) {
			c.sendMessage("You cannot view a bank whilst the server is updating.");
			return;
		}
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(input);
		if (optionalPlayer.isPresent()) {
			c.getPA().openOtherBank(optionalPlayer.get());
			Player otherPlayer = optionalPlayer.get();
			c.sendMessage("@red@" + otherPlayer.getLoginName() + " @blu@has @red@" + Misc.format(otherPlayer.getMoneyPouch()) + " @blu@in their money pouch.");
		} else {
			c.sendMessage(input + " is not online. You can only view the bank of online players.");
		}
	}
}
