package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.donation_system.Donation;
import io.xeros.model.entity.player.Player;

import java.util.Optional;

/**
 * Donation claim command.
 * 
 * @author C.T
 *
 */
public class ClaimDonation extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		player.sendMessage("Please right click the donation store in the bank area to claim your donation.");
		//new Thread(new Donation(player)).start();
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Checks if you have unclaimed donations.");
	}




}
