package io.xeros.model.entity.player.packets.action;

import io.xeros.Server;
import io.xeros.content.PlayerProfiler;
import io.xeros.content.StaffTab;
import io.xeros.content.combat.magic.CombatSpellData;
import io.xeros.content.preset.PresetManager;
import io.xeros.model.Staff;
import io.xeros.model.entity.player.PacketType;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.Clan;
import io.xeros.util.Misc;

public class EnterStringInput implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		String string = player.getInStream().readString();


		if (player.stringInputHandler != null) {
			player.stringInputHandler.handle(player, string);
			return;
		}

		if (player.documentGraphic) {
			player.sendMessage("Test: " + string);
			return;
		}
		
		if (player.viewingPresets) {
			PresetManager.getSingleton().updateName(player, string);
			return;
		}


		if (player.staffTab) {//staff tab
			StaffTab.inputField(player,  170200, string);
			player.staffTab = false;
			return;
		}

		if (player.staffTab1) {//staff tab
			StaffTab.inputField(player,  170201, string);
			player.staffTab1 = false;
			return;
		}

		if (player.staffTab2) {//staff tab
			StaffTab.inputField(player,  170202, string);
			player.staffTab2 = false;
			return;
		}

		if (player.staffTab3) {//staff tab
			StaffTab.inputField(player,  170203, string);
			player.staffTab3 = false;
			return;
		}

		if (player.staffTab4) {//staff tab
			StaffTab.inputField(player,  170204, string);
			player.staffTab4 = false;
			return;
		}

		if (player.staffTab5) {//staff tab
			StaffTab.inputField(player,  170205, string);
			player.staffTab5 = false;
			return;
		}

		if (player.staffTab6) {//staff tab
			StaffTab.inputField(player,  170206, string);
			player.staffTab6 = false;
			return;
		}

		if (player.staffTab7) {//staff tab
			StaffTab.inputField(player,  170207, string);
			player.staffTab7 = false;
			return;
		}

		if (player.staffTab8) {//staff tab
			StaffTab.inputField(player,  170208, string);
			player.staffTab8 = false;
			return;
		}

		if (player.staffTab9) {//staff tab
			StaffTab.inputField(player,  170209, string);
			player.staffTab9 = false;
			return;
		}

		if (player.staffTab10) {//staff tab
			StaffTab.inputField(player,  170210, string);
			player.staffTab10 = false;
			return;
		}

		if (player.staffTab11) {//staff tab
			StaffTab.inputField(player,  170211, string);
			player.staffTab11 = false;
			return;
		}

		if (player.staffTab12) {//staff tab
			StaffTab.inputField(player,  170212, string);
			player.staffTab12 = false;
			return;
		}

		if (player.staffTab13) {//staff tab
			StaffTab.inputField(player,  170213, string);
			player.staffTab13 = false;
			return;
		}

		if (player.staffTab14) {//staff tab
			StaffTab.inputField(player,  170214, string);
			player.staffTab14 = false;
			return;
		}

		if (player.staffTab15) {//staff tab
			StaffTab.inputField(player,  170215, string);
			player.staffTab15 = false;
			return;
		}

		if (player.staffTab16) {//staff tab
			StaffTab.inputField(player,  170216, string);
			player.staffTab16 = false;
			return;
		}

		if (player.staffTab17) {//staff tab
			StaffTab.inputField(player,  170217, string);
			player.staffTab17 = false;
			return;
		}

		if (player.staffTab18) {//staff tab
			StaffTab.inputField(player,  170218, string);
			player.staffTab18 = false;
			return;
		}

		if (player.staffTab19) {//staff tab
			StaffTab.inputField(player,  170219, string);
			player.staffTab18 = false;
			return;
		}

		if (player.playerProfile) {//player profile
			PlayerProfiler.search(player, string);
			player.playerProfile = false;
			return;
		}
		
		if (string != null && string.length() > 0) {
			if (player.clan == null) {
				Clan clan = Server.clanManager.getClan(string);
				if (clan != null) {
					clan.addMember(player);
				} else if (string.equalsIgnoreCase(player.getLoginName())) {
					Server.clanManager.create(player);
				} else {
					player.sendMessage(Misc.formatPlayerName(string) + " has not created a clan yet.");
				}
				player.getPA().refreshSkill(21);
				player.getPA().refreshSkill(22);
				player.getPA().refreshSkill(23);
			}
		}
	}

}