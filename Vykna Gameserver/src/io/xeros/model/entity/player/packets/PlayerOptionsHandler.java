package io.xeros.model.entity.player.packets;

import java.util.Objects;

import io.xeros.Configuration;
import io.xeros.content.PlayerProfiler;
import io.xeros.model.entity.player.*;
import io.xeros.model.multiplayersession.flowerpoker.FlowerPoker;
import io.xeros.util.Misc;

import static io.xeros.content.PlayerProfiler.*;

public class PlayerOptionsHandler implements PacketType {

	@Override
	public void processPacket(Player player, int opCode, int opSize) {

		if (player.getMovementState().isLocked() || player.getLock().cannotInteract(player))
			return;

		if (player.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			return;
		}

		player.interruptActions();

		int index = player.getInStream().readUnsignedWord();

		if (index >= PlayerHandler.players.length || index < 0)
			return;

		if (PlayerHandler.players[index] == null)
			return;

		Player requested = PlayerHandler.players[index];

		if (Objects.isNull(requested))
			return;

		if (player.getBankPin().requiresUnlock()) {
			player.getBankPin().open(2);
			return;
		}

		if (requested.getBankPin().requiresUnlock()) {
			return;
		}

		if (player.getInterfaceEvent().isActive()) {
			player.sendMessage("Please finish what you're doing.");
			return;
		}

		if (requested.getInterfaceEvent().isActive()) {
			player.sendMessage("That player is busy right now.");
			return;
		}

		player.faceEntity(requested);


		switch (opCode) {



		case 128:

			if (Boundary.isIn(player, Boundary.EDGE)
					|| Boundary.isIn(player, Boundary.VARROCK)
					|| Boundary.isIn(player, Boundary.NEX_BOSS_ROOM)
					|| Boundary.isIn(player, Boundary.NEX_REAVER_ROOM)) {

				player.sendMessage("@dre@Searching account '" + Misc.capitalizeFirstLetter(requested.getLoginName()) + "' for profile...");
				if (requested.getProfilePrivacy()) {
					player.sendMessage("@dre@" + Misc.capitalizeFirstLetter(requested.getLoginName()) + " has disabled profile viewing.");
					return;
				}
				if (player.getProfilePrivacy()) {
					player.sendMessage("@dre@You cannot view profiles while your profile privacy is off!");
					return;
				}
				displayProfile(player, requested);
				return;
			}


			/**
			 * Duel challenge / Flower Poker
			 */
			if (Boundary.isIn(player, Boundary.DUEL_ARENA) || Boundary.isIn(requested, Boundary.DUEL_ARENA)) {
				player.sendMessage("You cannot do this inside of the duel arena.");
				return;
			}

			if (Boundary.isIn(player, FlowerPoker.BOUNDARIES)) {
				if (Boundary.isIn(requested, FlowerPoker.BOUNDARIES)) {
					if (player.getFlowerPokerRequest().requestable(requested)) {
						player.getFlowerPokerRequest().request(requested);
						return;
					}
				}
				return;
			}

			if (requested.getPosition().inDuelArena()) {
				if (!Boundary.isIn(player, Boundary.DUEL_ARENA)) {
					if (!Configuration.NEW_DUEL_ARENA_ACTIVE) {
						player.getDH().sendStatement("@red@Dueling Temporarily Disabled", "The duel arena minigame is currently being rewritten.",
								"No player has access to this minigame during this time.", "", "Thank you for your patience, Rogue.");
						player.nextChat = -1;
						return;
					}
					if (player.getDuel().requestable(requested)) {
						player.getDuel().request(requested);
					}
				}
			}
			return;
		}
	}
}
