package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.content.combat.wrath.WrathSystem;
import io.xeros.content.combat.wrath.WrathPacketSender;
import io.xeros.model.entity.player.Player;

public class Setwrath extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		int v;
		try {
			v = Integer.parseInt(input.trim());
		} catch (Exception e) {
			c.sendMessage("Use: ::wrath 0-100");
			return;
		}

		// quick setter without exposing private keys:
//		int current = WrathSystem.getWrath(c);
//		int target = Math.max(0, Math.min(WrathSystem.MAX_WRATH, v));
//		if (target > current) {
//			WrathSystem.addWrath(c, target - current);
//		} else if (target < current) {
//			WrathSystem.subtractWrath(c, current - target);
//		} else {
//			WrathPacketSender.broadcastWrath(c);
//		}

	//	c.sendMessage("Wrath set to " + target);
	}
}
