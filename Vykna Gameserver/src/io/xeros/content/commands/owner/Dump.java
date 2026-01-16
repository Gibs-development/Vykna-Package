package io.xeros.content.commands.owner;

import java.io.BufferedWriter;
import java.io.FileWriter;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

public class Dump extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		try {
			try (BufferedWriter coord=new BufferedWriter(new FileWriter("./WOGW.txt", true))) {
				int i=0;

			}
			}  catch (Exception e) {
				player.sendMessage("Invalid Format. ::dump");
			}
	}
}