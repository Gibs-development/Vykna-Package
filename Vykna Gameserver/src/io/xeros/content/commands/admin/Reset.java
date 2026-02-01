package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;

public class Reset extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		String[] args = input.split("-");
		if (args.length == 0 || args[0].isEmpty()) {
			c.sendMessage("@red@Usage: ::reset-quest-questId[-player]");
			return;
		}
		if (!"quest".equals(args[0]) && args.length < 2) {
			c.sendMessage("@red@Usage: ::reset-quest-questId[-player] or ::reset-district-player");
			return;
		}
		Player player = args.length > 1 ? PlayerHandler.getPlayerByDisplayName(args[1]) : null;

		switch (args[0]) {
		case "":
			c.sendMessage("@red@Usage: ::reset-farming-username");
			break;
		case "quest":
			String questId = args.length > 1 ? args[1] : "";
			Player target = args.length > 2 ? PlayerHandler.getPlayerByDisplayName(args[2]) : c;
			if (questId.isEmpty()) {
				c.sendMessage("Usage: ::reset-quest-questId[-player]");
				return;
			}
			if (target == null) {
				c.sendMessage("Player is null.");
				return;
			}
			target.getQuestProfile().getQuests().remove(questId);
			c.sendMessage("Reset quest " + questId + " for " + target.getDisplayName() + ".");
			break;
			
		case "district":
			if (player == null) {
				c.sendMessage("Player is null.");
				return;
			}
			player.pkDistrict = !player.pkDistrict;
			player.sendMessage(player.getDisplayName() + ", pk district setting have been set to " + player.pkDistrict);
			break;
			
		case "check":
			if (player == null) {
				c.sendMessage("Player is null.");
				return;
			}
			c.getPA().sendFrame126("Check Bank", 36008);
			c.getPA().sendFrame126("Kick", 36009);
			c.getPA().sendFrame126("", 36010);
			c.getPA().sendFrame126("", 36011);
			c.getPA().sendFrame126("", 36012);
			c.getPA().sendFrame126("", 36013);
			c.getPA().sendFrame126("", 36014);
			c.getPA().sendFrame126("", 36015);
			if (!c.getRights().isOrInherits(Right.MODERATOR)) {
				c.getItems().deleteItem(6713, 10);
				return;
			}
			for (int i = 0; i < player.playerEquipment.length; i++) {
				if (player.playerEquipment[i] == -1) {
					continue;
				}
				c.getPA().itemOnInterface(player.playerEquipment[i], player.playerEquipmentN[i], 36081, i);
			}
			for (int i = 0; i < player.playerItems.length; i++) {
				if (player.playerItems[i] == 0) {
					continue;
				}
				c.getPA().itemOnInterface(player.playerItems[i], player.playerItemsN[i], 36083, i);
			}
			for (int i = 0; i < player.playerLevel.length; i++) {
				c.getPA().sendFrame126("" + player.playerLevel[i], 36049 + i);
			}
			break;
		}
	}

}
