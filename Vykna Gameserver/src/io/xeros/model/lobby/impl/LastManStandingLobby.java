package io.xeros.model.lobby.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


import io.xeros.content.minigames.LastManStanding;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.DialogueHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.lobby.Lobby;

/**
 * @author C.T, For Koranes
 * Handles the lobby system for The last man standing system.
 */

public class LastManStandingLobby extends Lobby {


	public static final List <Player> lmsLobby = new CopyOnWriteArrayList<Player>();//gets players in lobby

	public static boolean disabled = false;
	
	public static boolean activated = false;
	
	public static boolean playerOne = false;
	
	@Override
	public void onJoin(Player player) {
		player.getPA().movePlayer((3117), (3492));
		player.sendMessage("Welcome to the last man standing waiting area.");
		player.sendMessage("Make sure you are ready for the war.");
		String timeLeftString = formattedTimeLeft();
		player.sendMessage("Last man standing starts in: @red@" + timeLeftString);
	}

	@Override
	public void onLeave(Player player) {
		player.getPA().movePlayer(3116, 3492);
		//player.getPA().walkableInterface(-1);
		player.sendMessage("you have left the last man standing lobby.");
	}

	@Override
	public boolean canJoin(Player player) {
		if (disabled == true) {
			player.sendMessage("The lms is currently under development!");
			return false;
		}

		if (player.calculateCombatLevel() < 70) {
			  player.sendMessage("You need a combat level of 70 to join the @red@lms!");
			   return false;
		}
		if (LastManStanding.activated == true ) {
			  player.talkingNpc = 7599;
			  player.getDH().sendNpcChat("The last man standing arena is", "currently active try again in a few moments.");
			  return false;
		  }
		  if (player.getHeight() == 4) {
			  player.talkingNpc = 7599;
			  player.getDH().sendNpcChat("The last man standing arena is", "not available in edge pvp.");
			  return false;
		  }
		  if (player.hasFollower) {
			  player.talkingNpc = 7599;
			  player.getDH().sendNpcChat("Bank your pet before","entering the last man standing arena!");
			  return false;
		  }

		boolean accountInLobby = getFilteredPlayers()
				.stream()
				.anyMatch(lobbyPlr -> lobbyPlr.getMacAddress().equalsIgnoreCase(player.getMacAddress()));
		if (accountInLobby) {
			player.sendMessage("@red@You already have an account in the lobby!");
			return false;
		}

		return true;
		
	}

	@Override
	public void onTimerFinished(List<Player> lobbyPlayers) {
		if (lobbyPlayers.size() == 1) {
			for (Player p : lobbyPlayers) {
				p.addQueuedAction(players ->
				p.sendMessage("<col=ef1020>The game could not begin as you was the only player."));
				p.getPA().movePlayer(3116, 3492, 0);
		}}else {
			    LastManStanding.start(lobbyPlayers);
			    lmsLobby.remove(lobbyPlayers);
			
		}
	}

	@Override
	public void onTimerUpdate(Player player) {
		player.addQueuedAction(plr -> {
			String timeLeftString = formattedTimeLeft();
			plr.getPA().sendString("Lms begins in: @gre@" + timeLeftString, 6570);
			plr.getPA().sendString("", 6572);
			plr.getPA().sendString("", 6664);
			plr.getPA().walkableInterface(6673);
		});
	}

	@Override
	public long waitTime() {
		return 120000;//60000 = 60 SECONDS
	}

	@Override
	public int capacity() {
		return 20;
	}

	@Override
	public String lobbyFullMessage() {
		return "The lobby is currently full! Please wait for the next game!";
	}

	@Override
	public boolean shouldResetTimer() {
		return this.getWaitingPlayers().isEmpty();
	}

	public List<Player> getWaitingPlayers() {
		return waitingPlayers;
	}

	@Override
	public Boundary getBounds() {
		return Boundary.LMS_LOBBY;
	}


}
