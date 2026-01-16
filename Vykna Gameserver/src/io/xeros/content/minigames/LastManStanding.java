package io.xeros.content.minigames;

import com.everythingrs.marketplace.Item;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.xeros.content.combat.death.PlayerDeath;
import io.xeros.content.commands.all.Skull;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.broadcasts.Broadcast;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;


import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author C.T
 * The last man standing arena.
 */
public class LastManStanding {


	public static LinkedList<Player> lastManPlayers = new LinkedList<>();
	//private static final List <Player> lastManPlayers = new CopyOnWriteArrayList<Player>();//gets players in lobby

	public static boolean activated = false;
	
	public boolean applyDead;
	public int actionTimer;
	public boolean isDead;

	public static final Boundary GAME_BOUNDARY = new Boundary(3121, 3489, 3128, 3495);

	public static final Boundary LOBBY_BOUNDARY = new Boundary(3117, 3489, 3119, 3495);




	public static void removeFromLobby(Player player) {
		//removeLobbyMember(player);
		if (Boundary.isIn(player, LOBBY_BOUNDARY)) {
			player.getPA().movePlayer(3116, 3492, 0);
			player.sendMessage("You have left the LMS waiting lobby.");
		}
	}

	

	private static List<LastManStanding> games = Lists.newArrayList();
	private static Map<String, Integer> team = Maps.newConcurrentMap();
	
	
	private boolean finished;
	private long startTime;
	private long endTime;

	static int gameIndex = 1;

	private LastManStanding(List<Player> players) {
		
		activated = true;
		gameIndex++;
		final LastManStanding instance = this;
		for (Player p : players) {
			p.addQueuedAction(plr ->
			p.getPA().movePlayer((3124 + Misc.random(1)), (3492 + Misc.random(1)), getHeight()));
			lastManPlayers.add(p);
			p.addQueuedAction(plr ->
			p.sendMessage("Welcome to the last man standing arena!"));
			p.addQueuedAction(plr ->
			p.sendMessage("The last man standing will win the reward!"));
			getTeam().put(p.getLoginName().toLowerCase(), 0);
			CycleEventHandler.getSingleton().addEvent(p, new CycleEvent() {

				@Override
				public void execute(CycleEventContainer container) {
					p.setLastManStandingInstance(instance);
					container.stop();
				}

				@Override
				public void onStopped() {
					
				}

			}, 2);
		}
		stopwatch = Stopwatch.createStarted();
	}



	/*
	 * spawns
	 */
	private static LastManStanding lastmanstanding;


	public static void start(List<Player> lobbyPlayers) {
		LastManStanding instance = new LastManStanding(lobbyPlayers);
		addGame(instance);
		
	}

	/**
	 * Constantly checks if the player is the event, otherwise removes.
	 *
	 * @param c
	 */
	public void process(Player c) {
		if (this.getTeam().isEmpty()) {
			//kill();
		}
		if (!Boundary.isIn(c, Boundary.LMS_ARENA)) {
			removePlayer(c);
			
		}
	}



	public static List<Player> getPlayers() {
		return getTeam().keySet().stream().map(PlayerHandler::getPlayerByLoginName).filter(Objects::nonNull).collect(Collectors.toList());
	}

	public void removePlayer(Player player) {
		this.getTeam().remove(player.getLoginName().toLowerCase());
		player.setLastManStandingInstance(null);
		if (this.getTeam().isEmpty()) {
		//	this.destroyGame();
		} else {
			getPlayers().forEach(plr -> plr.sendMessage("<col=ff0000>" + player.getLoginName() + " has left the party."));
		}
	}


	private static void addGame(LastManStanding lastmanstanding) {
		if (!games.contains(lastmanstanding)) {
			games.add(lastmanstanding);
		}
	}

	public static void removeGame(LastManStanding lastmanstanding) {

		if (games.remove(lastmanstanding)) {
			
		}

	}

	public void destroyGame(LastManStanding lastmanstanding) {
		activated = false;
		if (lastmanstanding != null) {
		}
		games.remove(lastmanstanding);
		
	}
	
	
	/*
     * Leaving the arena on death Reset the items and location
     */
    public static void leaveArena(Player player) {
		if (!Boundary.isIn(player, Boundary.LMS_ARENA)) {
			return;
		}
		lastManPlayers.remove(player);
        player.getPA().movePlayer(3116, 3492, 0);
		checkForWinner(player);
		player.isSkulled = false;
		player.attackedPlayers.clear();
		player.headIconPk = -1;
		player.skullTimer = -1;
		player.getPA().requestUpdates();
		player.getHealth().reset();
		player.getHealth().removeAllStatuses();
		player.getHealth().removeAllImmunities();
    }
	
	

	 private static void checkForWinner(Player player ) {
		 activated = false;
		 if (!Boundary.isIn(player, Boundary.LMS_ARENA)) {
			 return;
		 }
	        if (lastManPlayers.size() == 1) {
	            Player winner = lastManPlayers.removeFirst();
				new Broadcast("" + winner.getLoginName() + " has won the last man standing event!").copyMessageToChatbox().submit();
	            winner.sendMessage("<col=3f3fff>You have won the last man standing event and receive an lms point!");
				Discord.writeDropsSyncMessage(""+ winner.getLoginName() + " has won the last man standing event!");
	    		winner.asPlayer().setLastManPoints(winner.asPlayer().getLastManPoints() + 1);
	    		winner.asPlayer().setLastManWins(winner.asPlayer().getLastManWins() + 1);
	            winner.getPA().movePlayer(3116, 3492, 0 );
				winner.isSkulled = false;
				winner.attackedPlayers.clear();
				winner.headIconPk = -1;
				winner.skullTimer = -1;
				winner.getPA().requestUpdates();
				winner.getHealth().reset();
				winner.getHealth().removeAllStatuses();
				winner.getHealth().removeAllImmunities();
	            
	        }
	    }

	private Stopwatch stopwatch;

	public String getTimeElapsed() {
		long milliseconds = stopwatch.elapsed(TimeUnit.MILLISECONDS);
		long minutes = (milliseconds / 1000) / 60;
		long seconds = (milliseconds / 1000) % 60;
		return new String(minutes + ":" + seconds);
	}

	public void stop() {
		
		destroyGame(this);
	}





	public int getHeight() {
		return 0;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	
	public Stopwatch getStopwatch() {
		return stopwatch;
	}

	public static Map<String, Integer> getTeam() {
		return team;
	}

	public static void setTeam(Map<String, Integer> team) {
		LastManStanding.team = team;
	}

	

}
