package io.xeros.content.world_event;




import java.util.*;
import java.util.concurrent.TimeUnit;

import io.xeros.Configuration;
import io.xeros.achievements.AchievementHandler;
import io.xeros.achievements.AchievementList;
import io.xeros.content.Announcement;
import io.xeros.content.combat.melee.CombatPrayer;
import io.xeros.content.itemskeptondeath.ItemsKeptOnDeathInterface;
import io.xeros.content.skills.Skill;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;

import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.broadcasts.Broadcast;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;

import static io.xeros.content.world_event.TournamentSets.getRandomSerpHelm;


/**
 * Advanced Tournament system.
 *
 * @author C.T, for RuneRogue.
 */
public class Tournament {

	public static boolean activated = false;



	public final static double M_BOX_CHANCE = 35;
	public final static double SUPER_M_BOX_CHANCE = 75;
	public final static double ULTRA_M_BOX_CHANCE = 125;

	/**
	 * How many game ticks untill tournament starts once it's announced.
	 */
	public final static int TOURNAMENT_WILL_START_IN = 200; // 100 is 1 minute.

	/**
	 * How many seconds untill tournament starts once it's announced.
	 */
	public final static int TOURNAMENT_STARTED_WAIT_TIME = 300; // How many game ticks for the players to gear up once Cowkiller spawns.


	/**
	 * Get prize reward per player entered tournament.
	 */
	private static int getTournamentRewardAmount() {
		return 1_000_000;// = 5M coins for finalists
	}

	private final static String LAST_EVENT_LOCATION = "backup/logs/tournament event.txt";

	private final static String[] eventLists =
			{"Pure tribrid", "Berserker hybrid", "Main hybrid welfare", "Main hybrid barrows", "Dharok melee", "Max hybrid", "Main Nh", "Barrows tribrid", "F2P", "Vesta melee"};

	public final static int[] eventShopIds =
			{210, 211, 212, 213, 214, 215, 216, 217, 218, 219};//78 79 76 77 80 81 82 86 87

	/**
	 * Used for setting combat skills.
	 */
	private final static String[] eventSkillString =
			{"PURE", "BERSERKER", "MAIN", "MAIN", "MAIN", "MAIN", "MAIN", "MAIN", "MAIN", "F2P"};//When adding new events make sure to add the event string

	public final static int[] ITEMS_CANNOT_DROP =
			{
					//@formatter:off
			//		10499, // Ava's accumulator
			//		12695, // Super combat potion(4)
					6685, // Sara brew
					6687, // sara brew
					6689, // sara brew
					6691, // sara brew
			//		2503, // Black d'hide body
			//		8850, // Rune defender
			//		4587, // Dragon scimitar
			//		1079, // Rune platelegs
			//		10551, // Fighter torso
			//		1215, // Dragon dagger
			//		5698, // Dragon dagger p++
			//		4151, // Abyssal whip
			//		12954, // Dragon defender
			//		12006, // Abyssal tentacle
			//		4720, // Dharok's platebody
			//		4722, // Dharok's platelegs
			//		4751, // Torag's platelegs
			//		4749, // Torag's platebody
			//		4728, // Guthan's platebody
			//		4730, // Guthan's chainskirt
			//		4757, // Verac's brassard
			//		4759, // Verac's plateskirt
			//		4736, // Karil's leathertop
			//		11834, // Bandos tassets
			//		11832, // Bandos chestplate
					//@formatter:on
			};

	public static int locationIndex = -1;

	/**
	 * Using tick count instead of time.
	 */
	private static int tournamentTickCount;

	/**
	 * Amount of players lost this round.
	 */
	private static int currentLostAmount;

	/**
	 * Amount of losses needed to start a new round.
	 */
	private static int lossesNeeded;

	/**
	 * Current tournament status.
	 */
	private static String tournamentStatus = "";

	/**
	 * True if the event is active, started by an Admin via a command.
	 */
	private static boolean tournamentActive;

	public static int playersEnteredTournament;


	/**
	 * List of players that entered the lobby.
	 */
	public static ArrayList<Integer> playerListLobby = new ArrayList<>();


	/**
	 * List of players that are participating in the tournament.
	 */
	public static ArrayList<Integer> playerListTournament = new ArrayList<>();


	/**
	 * List of players that have won the titles, so i can remove it from character file when the next winner of the same title is announced.
	 */
	public static ArrayList<String> tournamentTitleWinners = new ArrayList<>();

	public static ArrayList<String> debug = new ArrayList<>();

	/**
	 * Store the time of when the tournament was first announced.
	 */
	public static long timeTournamentAnnounced;

	/**
	 * Current event type.
	 */
	public static String eventType = eventLists[eventLists.length - 1];

	public static void tournamenTick() {
		if (!isTournamentActive()) {
			return;
		}
		tournamentTickCount++;
		debug.add(Misc.getDateAndTime() + ": Here1: " + tournamentTickCount + ", " + getTournamentStatus() + ", " + playerListTournament.size() + ", " + playerListLobby.size() + ", "
				+ lossesNeeded + ", " + currentLostAmount);
		switch (getTournamentStatus()) {
			case "TOURNAMENT ANNOUNCED":
				if (tournamentTickCount == TOURNAMENT_WILL_START_IN) {
					debug.add(Misc.getDateAndTime() + ": Here2");
					tournamentTickCount = 0;
					setTournamentStatus("TOURNAMENT LOBBY WAIT");
					PlayerHandler.executeGlobalMessage("@blu@The @red@" + eventType + " @blu@tournament starts in @red@3 minutes.");
					PlayerHandler.executeGlobalMessage("@blu@Talk to @red@Cow31337Killer @blu@at @red@::tournament @blu@to join!");
					NPCSpawning.spawnNpcOld(4420, 3087, 3501, 0, 3, 0, 0, 0, 0);
					locationIndex = 0;

					Object object = new Object();
					CycleEventHandler.getSingleton().addEvent(object, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							int secondsLeft = (int) ((TOURNAMENT_STARTED_WAIT_TIME - tournamentTickCount) * 0.6);
							String stringToShow = "Next round in: " + secondsLeft;
							if (!getTournamentStatus().equals("TOURNAMENT LOBBY WAIT")) {
								container.stop();
								stringToShow = "Next round in: ";
							}
							for (int index = 0; index < Configuration.MAX_PLAYERS; index++) {
								Player loop = PlayerHandler.players[index];
								if (loop == null) {
									continue;
								}
								if (loop.getHeight() != 20) {
									continue;
								}
								loop.getPA().sendFrame126(stringToShow, 25984);
							}
						}

						@Override
						public void onStopped() {
						}
					}, 1);
				}
				break;
			case "TOURNAMENT LOBBY WAIT":
				debug.add(Misc.getDateAndTime() + ": Here3");
				if (tournamentTickCount == TOURNAMENT_STARTED_WAIT_TIME) {
					debug.add(Misc.getDateAndTime() + ": Here4");
					tournamentStart();
				} else if (tournamentTickCount == (TOURNAMENT_STARTED_WAIT_TIME - 100)) {
					debug.add(Misc.getDateAndTime() + ": Here5");
					PlayerHandler.executeGlobalMessage("@blu@The @red@" + eventType + " @blu@tournament starts in @red@1 minutes.");
					Announcement.announce("@blu@Talk to @red@Cow31337Killer @blu@at @red@::tournament @blu@to join!");
				} else if (tournamentTickCount == (TOURNAMENT_STARTED_WAIT_TIME - 200)) {
					debug.add(Misc.getDateAndTime() + ": Here6");
					PlayerHandler.executeGlobalMessage("@blu@The @red@" + eventType + " @blu@tournament starts in @red@2 minutes.");
					PlayerHandler.executeGlobalMessage("@blu@Talk to @red@Cow31337Killer @blu@at @red@::tournament @blu@to join!");
				}
				break;

			case "TOURNAMENT NEXT ROUND":
				debug.add(Misc.getDateAndTime() + ": Here7");
				// 60 seconds passed.
				if (tournamentTickCount == 100) {
					debug.add(Misc.getDateAndTime() + ": Here8");
					currentLostAmount = 0;
					int playersLeft = playerListTournament.size();
					boolean isEvenNumber = playersLeft % 2 == 0;

					if (isEvenNumber) {
						lossesNeeded = playersLeft / 2;
					} else {

						lossesNeeded = (playersLeft - 1) / 2;
					}
					debug.add(Misc.getDateAndTime() + ": Here33: " + lossesNeeded + ", " + playersLeft + ", " + isEvenNumber);
					teleportPlayersToArena();
				}
				break;
		}
	}

	/**
	 * When the tournament has started.
	 */
	private static void tournamentStart() {

		debug.add(Misc.getDateAndTime() + ": Here8: " + playerListLobby.size());
		boolean has8 = playerListLobby.size() >= 4;//Lobby size

		int maximumEntries = 0;
		if (has8) {
			maximumEntries = playerListLobby.size() & ~1; // Rounds the given number to the lowest even number.
			lossesNeeded = maximumEntries / 2;
			debug.add(Misc.getDateAndTime() + ": Here30: " + playerListLobby.size() + ", " + lossesNeeded + ", " + maximumEntries);
		} else {

			PlayerHandler.executeGlobalMessage("4 players needed to start, cancelled.");
			cancelTournament();
			return;
		}
		tournamentTickCount = 0;
		setTournamentStatus("TOURNAMENT STARTED");
		for (int index = 0; index < playerListLobby.size(); index++) {
			Player loop = PlayerHandler.players[playerListLobby.get(index)];
			if (loop == null) {
				lossesNeeded--;
				debug.add(Misc.getDateAndTime() + ": Here32: " + lossesNeeded);
				continue;
			}
			if (loop.teleporting) {
				lossesNeeded--;
				debug.add(Misc.getDateAndTime() + ": Here31: " + lossesNeeded);
				continue;
			}
			playerListTournament.add(playerListLobby.get(index));
		}
		updateText(null);
		playersEnteredTournament = playerListTournament.size();
		PlayerHandler.executeGlobalMessage(
				"The " + eventType + " tournament has started. Winner will receive " + Misc.formatRunescapeStyle((playersEnteredTournament * getTournamentRewardAmount())) + " "
						+ Configuration.getMainCurrencyName().toLowerCase() + "!");
		teleportPlayersToArena();

	}

	private static void cancelTournament() {
		debug.add(Misc.getDateAndTime() + ": Tournament cancelled.");
		NPCHandler.despawn(4420, 0);// Delete cow1337killer npc.
		for (int index = 0; index < Configuration.MAX_PLAYERS; index++) {
			Player loop = PlayerHandler.players[index];
			if (loop == null) {
				continue;
			}
			if (loop.getHeight() != 20) {
				continue;
			}
			restoreCombatStats(loop);
			loop.getHealth().reset();
			loop.getPA().refreshSkills();
			loop.getItems().deleteAllItems();
			loop.getPA().movePlayer(3087, 3500, 0);//tele player back to the cowleetkiller at home
			loop.getPA().closeAllWindows();
			//loop.freezeTimer = 0;
			loop.isSkulled = false;
			loop.skullTimer = 0;
			loop.headIconPk = -1;
			loop.getPA().requestUpdates();
		}
		eventType = "";
		setTournamentActive(false);
		tournamentTickCount = 0;
		setTournamentStatus("");
		currentLostAmount = 0;
		lossesNeeded = 0;
		playerListLobby.clear();
		playerListTournament.clear();
		locationIndex = -1;
		playersEnteredTournament = 0;


	}

	public final static int TOURNAMENT_ARENA_X = 3328;

	public final static int TOURNAMENT_ARENA_Y = 4769;

	public final static int MAXIMUM_ROAM_DISTANCE = 40;

	private final static int TELEPORT_START_X = 3328;

	private final static int TELEPORT_START_Y = 4757;

	private static void teleportPlayersToArena() {
		Collections.shuffle(playerListTournament);
		NPCHandler.despawn(4420, 0);// Delete cow1337killer npc.
		activated = false;
		int pairs = 0;
		int x = 0;
		int y = 0;
		for (int index = 0; index < playerListTournament.size(); index++) {
			Player loop = PlayerHandler.players[playerListTournament.get(index)];
			if (loop == null) {
				continue;
			}
			pairs++;
			if (pairs == 1) {
				x = TOURNAMENT_ARENA_X - 14 + Misc.random(28);
				y = TOURNAMENT_ARENA_Y - 9 + Misc.random(19);
				// If a partner won't be available because it is an odd number player list, then stop and inform the odd player out.
				if ((index + 1) > playerListTournament.size() - 1) {
					// The other finalist logs off.
					if (playerListTournament.size() == 1) {
						finalistDisconnection(loop);
						return;
					}
					redSkull(loop);   // fix me cam URGENT
					loop.skullTimer = 144000;
					loop.sendMessage("Could not find a challenger for you, you are still in the tournament.");
					return;
				}
				loop.tournamentTarget = playerListTournament.get(index + 1);
				loop.getPA().movePlayer(x, y, 20);
				Player challenger = PlayerHandler.players[playerListTournament.get(index + 1)];
				loop.sendMessage("Your challenger is " + challenger.getLoginName() + "!");
				loop.getHealth().reset();
				loop.getPA().createPlayerHints(10, challenger.getIndex());
				loop.setDuelCount(4);
				redSkull(loop);  // fix me cam URGENT
				loop.skullTimer = 144000;
				loop.tournamentTotalGames += 1;
				AchievementHandler.activate(loop, AchievementList.PARTICIPATE_IN_1_TOURNAMENT, 1);//NEW ACHIEVEMNTS
				CycleEventHandler.getSingleton().addEvent(loop, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						loop.duelForceChatCount--;
						loop.sendMessage("" + loop.duelForceChatCount + "");
						if (loop.duelForceChatCount == 0) {
							container.stop();
						}
					}

					@Override
					public void onStopped() {
						loop.sendMessage("@red@ FIGHT!");
						loop.forcedChat("FIGHT!");
						loop.duelForceChatCount = 4;
						loop.resetDamageTaken();
						loop.setDuelCount(0);
						//loop.freezeTimer = 0;
					}
				}, 2);

			} else if (pairs == 2) {
				loop.tournamentTarget = playerListTournament.get(index - 1);
				loop.getPA().movePlayer(x + 1, y, 20);
				Player challenger = PlayerHandler.players[playerListTournament.get(index - 1)];
				loop.sendMessage("Your challenger is " + challenger.getLoginName() + "!");
				loop.getPA().createPlayerHints(10, challenger.getIndex());
				pairs = 0;
				loop.setDuelCount(4);
				redSkull(loop);
				loop.skullTimer = 144000;

				CycleEventHandler.getSingleton().addEvent(loop, new CycleEvent() {

					@Override
					public void execute(CycleEventContainer container) {
						loop.duelForceChatCount--;
						loop.sendMessage("" + loop.duelForceChatCount + "");
						if (loop.duelForceChatCount == 0) {
							container.stop();
						}
					}

					@Override
					public void onStopped() {
						loop.sendMessage("@red@ FIGHT!");
						loop.forcedChat("FIGHT!");
						loop.duelForceChatCount = 4;
						loop.resetDamageTaken();
						loop.setDuelCount(0);
						//loop.freezeTimer = 0;
					}

				}, 2);
			}
		}

	}

	private static void redSkull(Player player) {
		player.isSkulled = true;
		player.skullTimer = Configuration.SKULL_TIMER;
		player.headIconPk = 1;
		player.getPA().requestUpdates();
		ItemsKeptOnDeathInterface.refreshIfOpen(player);
	}

	/**
	 * Added to log out method and log-in, incase server crashes so it won't register the log out part.
	 *
	 * @param player
	 */
	public static void logOutUpdate(Player player, boolean logIn) {
		if (logIn) {
			switch (getTournamentStatus()) {
				case "TOURNAMENT ANNOUNCED":
					player.sendMessage("@blu@The " + eventType + " @blu@ tournament has been announced.");
					break;
				case "TOURNAMENT LOBBY WAIT":
					player.sendMessage("@blu@The " + eventType + " @blu@tournament will start soon, talk to Cow31337Killer.");
					break;

				case "TOURNAMENT NEXT ROUND":
					player.sendMessage("@blu@ The " + eventType + " @blu@tournament is active!");
					break;
			}
		}

		if (player.getHeight() != 20 && (!Boundary.isIn(player, Boundary.TOURNY_COMBAT_AREA) && (!Boundary.isIn(player, Boundary.TOURNY_LOBBY)))) {
			return;
		}

		if (player.getHeight() == 20 && (Boundary.isIn(player, Boundary.TOURNY_COMBAT_AREA) && (Boundary.isIn(player, Boundary.TOURNY_LOBBY))))  {

			player.getPA().movePlayer(player.getX(), player.getY(), 0);
			//player.freezeTimer = 0;
			player.isSkulled = false;
			player.skullTimer = 0;
			player.headIconPk = -1;
			player.getPA().requestUpdates();
		}
		//restoreCombatStats(player);
		//player.getItems().deleteAllItems();
		//Skull.clearSkull(player);     // fix me cam URGENT
		player.isSkulled = false;
		player.skullTimer = 0;
		player.headIconPk = -1;
		player.getPA().requestUpdates();
		player.getHealth().reset();
		activated = false;

		if (!logIn) {
			player.getPA().createPlayerHints(10, -1);
			if (player.tournamentTarget >= 0) {
				Player other = PlayerHandler.players[player.tournamentTarget];
				if (other != null) {
					playerDied(other, player);
				}
			}
		}
		player.tournamentTarget = -1;
		//removeFromTournamentLobby(player.getIndex());
		removeFromTournamentLobby(player.getIndex());
	}

	/**
	 * Other player disconnected while in lobby and we are both in finals.
	 *
	 * @param player
	 */
	public static void finalistDisconnection(Player player) {
		player.getPA().movePlayer(TELEPORT_START_X, TELEPORT_START_Y, 20);
		player.getPA().createPlayerHints(10, -1);
		player.tournamentTarget = -1;
		player.sendMessage("@red@The finalist has disconnected, you are given an automatic win!");
		awardWinner(player);
		cancelTournament();
	}

	private static void awardWinner(Player player) {
		int amount = (playersEnteredTournament * getTournamentRewardAmount());

		if (Misc.hasOneOutOf(M_BOX_CHANCE)) {
			player.getItems().addItemToBankOrDrop(6199, 1);
			PlayerHandler.executeGlobalMessage("@red@" + Misc.capitalize(player.getLoginName()) + "@blu@ has received an @red@mystery box @blu@from the @red@tournaments!");
			Discord.writeDropsSyncMessage("News: " + player.getLoginName() + " has won the tournament and received an mystery box from the tournaments.!");
		}
		if (Misc.hasOneOutOf(SUPER_M_BOX_CHANCE)) {
			player.getItems().addItemToBankOrDrop(6828, 1);
			PlayerHandler.executeGlobalMessage("@red@" + Misc.capitalize(player.getLoginName()) + "@blu@ has received an @red@super mystery box @blu@from the @red@tournaments!");
			Discord.writeDropsSyncMessage("News: " + player.getLoginName() + " has won the tournament and received an super mystery box from the tournaments.!");
		}
		if (Misc.hasOneOutOf(ULTRA_M_BOX_CHANCE)) {
			player.getItems().addItemToBankOrDrop(13346, 1);
			PlayerHandler.executeGlobalMessage("@red@" + Misc.capitalize(player.getLoginName()) + "@blu@ has received an @red@ultra Mystery Box @blu@from the @red@tournaments!");
			Discord.writeDropsSyncMessage("News: " + player.getLoginName() + " has won the tournament and received an ultra Mystery Box from the tournaments!");
		}

		new Broadcast("" + player.getLoginName() + " has won the tournament and received " + Misc.formatRunescapeStyle(amount) + " " + Configuration.getMainCurrencyName().toLowerCase() + "!").copyMessageToChatbox().submit();
		Discord.writeEventMessage("News: " + player.getLoginName() + " has won the tournament and received " + Misc.formatRunescapeStyle(amount) + " " + Configuration.getMainCurrencyName().toLowerCase() + "!");
		player.getItems().addItemToBankOrDrop(995, amount);

		player.tournamentWins += 1;
		player.tournamentPoints += 2;
		AchievementHandler.activate(player, AchievementList.WIN_A_TOURNAMENT, 1);//NEW ACHIEVEMNTS
		AchievementHandler.activate(player, AchievementList.WIN_10_TOURNAMENTS, 1);//NEW ACHIEVEMNTS
	//	restoreCombatStats(player);
		player.getItems().deleteAllItems();
		player.getHealth().reset();
	}




	private static void announceToLobby(String text) {
		for (int index = 0; index < Configuration.MAX_PLAYERS; index++) {
			Player loop = PlayerHandler.players[index];
			if (loop == null) {
				continue;
			}
			if (loop.getHeight() != 20) {
				continue;
			}
			loop.sendMessage(text);
		}
	}

	public static void playerDied(Player killer, Player victim) {
		if (Tournament.locationIndex == -1) {
			return;
		}
		victim.getItems().deleteAllItems();
		restoreCombatStats(victim);
		victim.isSkulled = false;
		victim.skullTimer = 0;
		victim.headIconPk = -1;
		victim.getPA().requestUpdates();
		victim.getPA().movePlayer(TELEPORT_START_X, TELEPORT_START_Y, 20);
		victim.tournamentTarget = -1;
		victim.tournyDeaths += 1;
	//	Skull.clearSkull(victim);    // fix me cam URGENT
		if (killer != null) {
			killer.getPA().createPlayerHints(10, -1);
			killer.getPA().movePlayer(TELEPORT_START_X, TELEPORT_START_Y, 20);
			killer.tournamentTarget = -1;
			killer.healEverything();
			killer.tournyKills += 1;
			announceToLobby("@red@"+ killer.getLoginName() +" @pur@has knocked out @red@" + victim.getLoginName() + "!");
		}
		victim.getPA().createPlayerHints(10, -1);
		victim.healEverything();
		victim.getHealth().reset();
		currentLostAmount++;
		for (int index = 0; index < playerListTournament.size(); index++) {
			if (victim.getIndex() == playerListTournament.get(index)) {
				playerListTournament.remove(index);
				updateText(null);
				break;
			}
		}
		// 1 player left, means winner!
		if (playerListTournament.size() == 1) {
			if (killer != null) {
				awardWinner(killer);
			}
			awardRunnerUp(victim);
			cancelTournament();
			return;
		}
		if (currentLostAmount == lossesNeeded) {
			tournamentTickCount = 0;
			setTournamentStatus("TOURNAMENT NEXT ROUND");
			Object object = new Object();
			CycleEventHandler.getSingleton().addEvent(object, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					int secondsLeft = (int) ((100 - tournamentTickCount) * 0.6);
					String stringToShow = "Next round in: " + secondsLeft;
					if (secondsLeft < 0) {
						container.stop();
						stringToShow = "Next round in: ";
					}
					for (int index = 0; index < Configuration.MAX_PLAYERS; index++) {
						Player loop = PlayerHandler.players[index];
						if (loop == null) {
							continue;
						}
						if (loop.getHeight() != 20) {
							continue;
						}
						loop.getPA().sendFrame126(stringToShow, 25984);
					}
				}

				@Override
				public void onStopped() {
				}
			}, 1);

			String stage = "finals";
			int playersLeft = playerListTournament.size();
			if (playersLeft == 2) {
				stage = "finals";
			} else if (playersLeft <= 5) {
				stage = "semi-finals";
			} else if (playersLeft <= 8) {
				stage = "quarter-finals";
			} else if (playersLeft <= 16) {
				stage = "group stage 1";
			} else if (playersLeft <= 32) {
				stage = "group stage 2";
			} else if (playersLeft <= 64) {
				stage = "group stage 3";
			} else if (playersLeft <= 128) {
				stage = "group stage 4";
			}
			if (stage.equals("finals")) {
				Player playerOne = PlayerHandler.players[playerListTournament.get(0)];
				Player playerTwo = PlayerHandler.players[playerListTournament.get(1)];
				PlayerHandler.executeGlobalMessage("@pur@The @red@" + eventType + " @pur@final is between @red@" + playerOne.getLoginName() + " @pur@and @red@" + playerTwo.getLoginName() + "!");
			} else {
				PlayerHandler.executeGlobalMessage("@pur@The @red@" + eventType + " @pur@tournament has reached the @red@" + stage + "!");
			}
		}
	}

	private static void awardRunnerUp(Player victim) {
		int amount = (playersEnteredTournament * (getTournamentRewardAmount() / 2));
		victim.getItems().addItemToBankOrDrop(995, amount);
		victim.getHealth().reset();
		PlayerHandler.executeGlobalMessage(
				"@pur@The runner up @red@" + victim.getLoginName() + " @pur@has won @red@" + Misc.formatRunescapeStyle(amount) + " @pur@" + Configuration.getMainCurrencyName().toLowerCase() + ".");

	}

	public static void loadNewTournament(String command) {
		int tournamentId = 0;
		try {
			tournamentId = Integer.parseInt(command.substring(11));
		} catch (Exception e) {

		}
		if (tournamentId > eventLists.length - 1) {
			return;
		}
		debug.add(Misc.getDateAndTime() + ": Here22: " + tournamentId);
		startNewTournament(tournamentId);
	}



	private static void startNewTournament(int tournamentId) {
		cancelTournament();
		setTournamentActive(true);
		setTournamentStatus("TOURNAMENT ANNOUNCED");
		eventType = eventLists[tournamentId];
		int highest = getTournamentRewardAmount() * 50;
		new Broadcast("The " + eventType + " tournament will start in 5 minutes!").copyMessageToChatbox().submit();
		Discord.writeEventMessage("The " + eventType + " tournament will start in 5 minutes!");
	//	timeTournamentAnnounced = System.currentTimeMillis();

		Object object = new Object();
		CycleEventHandler.getSingleton().addEvent(object, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void onStopped() {
				int highest = getTournamentRewardAmount() * 5;
				PlayerHandler.executeGlobalMessage("@blu@The @red@" + eventType + " @blu@tournament will start in 4 minutes!");
				PlayerHandler.executeGlobalMessage("@blu@Finalists will receive up to @red@" + Misc.formatRunescapeStyle(highest) + " " + Configuration.getMainCurrencyName().toLowerCase() + "!");
				PlayerHandler.executeGlobalMessage("@blu@Head to @red@::tournament@blu@ and wait for @red@Cow31337Killer @blu@to spawn to enter.");

			}
		}, 100);

	}


	private static CycleEvent cycleEvent;
	private static CycleEventContainer cycleEventContainer;
	public static void startTourny() {
		cycleEvent = new CycleEvent() {

			@Override
			public void execute(CycleEventContainer container) {

				if(isTournamentActive()) {
					container.setTick(Misc.toCycles(139, TimeUnit.MINUTES));//139
					return;
				}
				container.setTick(generateTime());
				Tournament.startNewTournament(Misc.random(eventLists.length - 1));
			}

			@Override
			public void onStopped() {

			}

		};
		setCycleEventContainer(CycleEventHandler.getSingleton().addEvent(Tournament.class, cycleEvent, generateTime()));
	}

	public static int generateTime(){
		return Misc.toCycles(139, TimeUnit.MINUTES);
	}//139

	public static CycleEventContainer getCycleEventContainer() {
		return cycleEventContainer;
	}

	public static void setCycleEventContainer(CycleEventContainer cycleEventContainer) {
		Tournament.cycleEventContainer = cycleEventContainer;
	}


	public static void talkToCowKiller(Player player) {
		if (!Tournament.getTournamentStatus().equals("TOURNAMENT LOBBY WAIT") && !Tournament.getTournamentStatus().equals("TOURNAMENT STARTED") && !Tournament.getTournamentStatus().equals("TOURNAMENT NEXT ROUND")) {
			return;
		}

		//boolean accountInLobby = playerListLobby
		//		.stream()
		//		.anyMatch(playerListLobby -> player.getMacAddress().equalsIgnoreCase(player.getMacAddress()));
	//	if (accountInLobby) {
		//	player.sendMessage("@red@You already have an account in the tournament!");
		//	player.getPA().removeAllWindows();
		//	return;
		//}

		if (ItemAssistant.hasEquipment(player)) {
			player.sendMessage("@red@You cannot enter the tournament with items");
			player.sendMessage("@red@if you manage to get items inside they will be lost.");
			player.getPA().removeAllWindows();
			return;
		}

		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] > 0) {
				player.sendMessage("@red@You cannot enter the tournament with items");
				player.sendMessage("@red@if you manage to get items inside they will be lost.");
				player.getPA().removeAllWindows();
				return;
			}
		}

		if (player.hasFollower) {
			player.sendMessage("Pick up your pet before entering tournament!");
			player.getPA().removeAllWindows();
			return;
		}
        activated = true;
		storeCombatStats(player);
		player.getPA().closeAllWindows();
		player.getPA().movePlayerNew(TELEPORT_START_X, TELEPORT_START_Y, 5 * 4);
		//player.freezeTimer = 300;

		for (int index = 0; index < eventLists.length; index++) {
			if (eventLists[index].equals(eventType)) {
				final int value = index;
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if (eventType.equals("Dharok melee")) {
						    mainDharokTournament(player, eventType, eventSkillString[value]);
						} else if (eventType.contains("Barrows tribrid")) {
							mainBarrowsBridTournament(player, eventType, eventSkillString[value]);
						} else if (eventType.contains("F2P")) {
							pureF2pTournament(player, eventType, eventSkillString[value]);
						} else if (eventType.contains("Max hybrid")) {
							maxBridTournament(player, eventType, eventSkillString[value]);
						} else if (eventType.contains("Vesta melee")) {
							mainVestaTournament(player, eventType, eventSkillString[value]);
						} else {
							mainHybridTournament(player, eventType, eventSkillString[value]);
						}
						container.stop();
					}

					@Override
					public void onStopped() {
					}
				}, 10);
				break;
			}
		}
		//playerListLobby.add(player.getPlayerId());
		playerListLobby.add(player.getIndex());
		updateText(player);
	}

	public static void removeFromTournamentLobby(int getIndex) {
		if (!isTournamentActive()) {
			return;
		}
		for (int index = 0; index < playerListLobby.size(); index++) {
			if (getIndex == playerListLobby.get(index)) {
				playerListLobby.remove(index);
				break;
			}
		}
		for (int index = 0; index < playerListTournament.size(); index++) {
			if (getIndex == playerListTournament.get(index)) {
				playerListTournament.remove(index);
				break;
			}
		}
		updateText(null);
	}

	public static void updateText(Player player) {
		if (player != null) {
			player.getPA().sendFrame126("Lobby: " + Tournament.playerListLobby.size(), 25982);
			player.getPA().sendFrame126("Tournament: " + Tournament.playerListTournament.size(), 25983);
		}
		for (int index = 0; index < Configuration.MAX_PLAYERS; index++) {
			Player loop = PlayerHandler.players[index];
			if (loop == null) {
				continue;
			}
			if (loop.getHeight() != 20) {
				continue;
			}

			loop.getPA().sendFrame126("Lobby: " + Tournament.playerListLobby.size(), 25982);
			loop.getPA().sendFrame126("Tournament: " + Tournament.playerListTournament.size(), 25983);
		}

	}

	public static void openShop(Player player) {
		int shopId = 0;
		for (int index = 0; index < eventLists.length; index++) {
			if (eventLists[index].equals(eventType)) {
				shopId = eventShopIds[index];
				break;
			}
		}
		if (shopId == 0) {
			return;
		}
		player.getShops().openShop(shopId);
	}

	public static void tournyKick(Player player, String name) {
		try {
			name = name.substring(15);
			Player killer = null;
			Player victim = null;
			for (int index = 0; index < Configuration.MAX_PLAYERS; index++) {
				Player loop = PlayerHandler.players[index];
				if (loop == null) {
					continue;
				}
				if (loop.getHeight() != 20) {
					continue;
				}
				if (loop.getLoginName().equalsIgnoreCase(name) && loop.tournamentTarget >= 0) {
					victim = loop;
					Player killer1 = PlayerHandler.players[loop.tournamentTarget];
					if (killer1.tournamentTarget == loop.getIndex()) {
						killer = killer1;
					}
				}
			}
			if (killer == null || victim == null) {
				player.sendMessage("Victim or killer is not available.");
				return;
			}
			PlayerHandler.executeGlobalMessage("Tournament stalling kick, forced loser: " + victim.getLoginName() + ", forced winner: " + killer.getLoginName());
			Tournament.playerDied(killer, victim);
		} catch (Exception e) {
			player.sendMessage("Use as ::tournykick Rogue");
		}

	}

	public static boolean isTournamentActive() {
		return tournamentActive;
	}

	public static void setTournamentActive(boolean tournamentActive) {
		Tournament.tournamentActive = tournamentActive;
	}

	public static String getTournamentStatus() {
		return tournamentStatus;
	}

	public static void setTournamentStatus(String tournamentStatus) {
		Tournament.tournamentStatus = tournamentStatus;
	}

	public static void spawnInventory(Player player, int[][] array) {
		for (int i = 0; i < array.length; i++) {
			if (array[i][0] <= 1) {
				continue;
			}
			player.getItems().addItem(array[i][0], array[i][1]);
		}
		player.getItems().addItem(385, player.getInventory().freeInventorySlots());
	}


	public static void mainDharokTournament(Player player, String type, String stats) {
		spawnInventory(player, TournamentSets.tournamentInventoryDharok(player, type));
		player.getItems().equipItem(4716, 1, player.playerHat);// DH HELM
		player.getItems().equipItem(4720, 1, player.playerChest);// DH BODY
		player.getItems().equipItem(4722, 1, player.playerLegs);// DH LEGS
		player.getItems().equipItem(7462, 1, player.playerHands);// GLOVES
		player.getItems().equipItem(11840, 1, player.playerFeet);// DRAGON BOOTS
		player.getItems().equipItem(6570, 1, player.playerCape);// FIRE CAPE
		player.getItems().equipItem(1712, 1, player.playerAmulet);// GLORY
		player.getItems().equipItem(12954, 1, player.playerShield);// DRAGON DEFENDER
		player.getItems().equipItem(4587, 1, player.playerWeapon);// DRAGON SCIMITAR
		player.healEverything();
		player.getPA().refreshSkills();
		player.getItems().addSpecialBar(player.playerEquipment[Player.playerWeapon]);
		player.getItems().updateSpecialBar();
		player.getPA().resetAutocast();
		setCombatSkills(player, "MAIN", false, null);
		setPrayerAndMagicBook(player, "LUNAR");

	}

	public static void mainBarrowsBridTournament(Player player, String type, String stats) {
		spawnInventory(player, TournamentSets.tournamentInventoryBarrowsBrid(player, type));
		player.getItems().equipItem(4732, 1, player.playerHat);// Karils coif
		player.getItems().equipItem(4736, 1, player.playerChest);// Karil's body
		player.getItems().equipItem(4738, 1, player.playerLegs);// Karil's skirt
		player.getItems().equipItem(7462, 1, player.playerHands);// GLOVES
		player.getItems().equipItem(2577, 1, player.playerFeet);// Ranger boots
		player.getItems().equipItem(6570, 1, player.playerCape);// FIRE CAPE
		player.getItems().equipItem(1712, 1, player.playerAmulet);// GLORY
		player.getItems().equipItem(4734, 1, player.playerWeapon);// Karil's c'bow
		player.getItems().equipItem(6733, 1, player.playerRing);// Archer's ring
		player.getItems().equipItem(4740, 500, player.playerArrows);// Bolt rack
		player.healEverything();
		player.getPA().refreshSkills();
		player.getItems().addSpecialBar(player.playerEquipment[Player.playerWeapon]);
		player.getItems().updateSpecialBar();
		player.getPA().resetAutocast();
		setCombatSkills(player, "RANGED TANK", false, null);
		setPrayerAndMagicBook(player, "ANCIENT");
	}

	public static void pureF2pTournament(Player player, String type, String stats) {
		spawnInventory(player, TournamentSets.tournamentInventoryF2p(player, type));
		player.getItems().equipItem(1169, 1, player.playerHat);// coif
		player.getItems().equipItem(1129, 1, player.playerChest);// leather body
		player.getItems().equipItem(1099, 1, player.playerLegs);// green d hde chaps
		player.getItems().equipItem(1065, 1, player.playerHands);// green d'hide vamb
		player.getItems().equipItem(1061, 1, player.playerFeet);// Leather boots
		player.getItems().equipItem(6570, 1, player.playerCape);// FIRE CAPE
		player.getItems().equipItem(1731, 1, player.playerAmulet);// power
		player.getItems().equipItem(853, 1, player.playerWeapon);// maple shortbow
		player.getItems().equipItem(890, 1000, player.playerArrows);// adamamnt arrow
		player.healEverything();
		player.getPA().refreshSkills();
		player.getItems().addSpecialBar(player.playerEquipment[Player.playerWeapon]);
		player.getItems().updateSpecialBar();
		player.getPA().resetAutocast();
		setCombatSkills(player, stats, false, null);
		setPrayerAndMagicBook(player, "MODERN");
	}

	public static void maxBridTournament(Player player, String type, String stats) {
		spawnInventory(player, TournamentSets.tournamentInventoryMaxBrid(player, type));
		player.getItems().equipItem(getRandomSerpHelm(), 1, player.playerHat);// random serp help
		player.getItems().equipItem(4712, 1, player.playerChest);// ahrims body
		player.getItems().equipItem(4714, 1, player.playerLegs);// ahrims skirt
		player.getItems().equipItem(7462, 1, player.playerHands);// barrows gloves
		player.getItems().equipItem(13235, 1, player.playerFeet);// eaternal boots
		player.getItems().equipItem(6570, 1, player.playerCape);// FIRE CAPE
		player.getItems().equipItem(12002, 1, player.playerAmulet);//Occult necklace
		player.getItems().equipItem(12825, 1, player.playerShield);// arcane spirit shield
		player.getItems().equipItem(12904, 1, player.playerWeapon);// toxic staff of the dead
		player.getItems().equipItem(11773, 1, player.playerRing);// Berserker ring i
		player.healEverything();
		player.getPA().refreshSkills();
		player.getItems().addSpecialBar(player.playerEquipment[Player.playerWeapon]);
		player.getItems().updateSpecialBar();
		player.getPA().resetAutocast();
		setCombatSkills(player, "MAIN", false, null);
		setPrayerAndMagicBook(player, "ANCIENT");
	}

	public static void mainHybridTournament(Player player, String type, String stats) {
		//{"Pure tribrid", "Berserker hybrid", "Main hybrid welfare", "Main hybrid barrows"};
		if (type.contains("hybrid") || type.contains("Nh")) {
			spawnInventory(player, TournamentSets.tournamentInventory(player, type));
			player.getItems().equipItem(10828, 1, player.playerHat);// helm of nietz
			player.getItems().equipItem(4091, 1, player.playerChest);// mystic top
			player.getItems().equipItem(4093, 1, player.playerLegs);// mystic bottom
			player.getItems().equipItem(7462, 1, player.playerHands);// barrows gloves
			player.getItems().equipItem(3105, 1, player.playerFeet);// climbing boots
			player.getItems().equipItem(6570, 1, player.playerCape);// FIRE CAPE
			player.getItems().equipItem(1712, 1, player.playerAmulet);//glory4
			player.getItems().equipItem(12829, 1, player.playerShield);// spirit shield
			player.getItems().equipItem(4675, 1, player.playerWeapon);// ancient staff
			player.getItems().equipItem(11773, 1, player.playerRing);// Berserker ring i
			player.getItems().equipItem(9244, 100, player.playerArrows);// dragon bolts e
		} else {
			spawnInventory(player, TournamentSets.pureTribridInventory);
			player.getItems().equipItem(20595, 1, player.playerHat);// elder chaos hat
			player.getItems().equipItem(20517, 1, player.playerChest);// elder chaos top
			player.getItems().equipItem(20520, 1, player.playerLegs);// elder chaos bottom
			player.getItems().equipItem(7458, 1, player.playerHands);// mithril gloves
			player.getItems().equipItem(2579, 1, player.playerFeet);// wizard boots
			player.getItems().equipItem(34037, 1, player.playerCape);// Death cape
			player.getItems().equipItem(6585, 1, player.playerAmulet);//fury
			player.getItems().equipItem(3842, 1, player.playerShield);// unholy book
			player.getItems().equipItem(11791, 1, player.playerWeapon);// staff of the dead
			player.getItems().equipItem(22975, 1, player.playerRing);// brimstone ring
			player.getItems().equipItem(9244, 100, player.playerArrows);// dragon bolts e
		}

		   player.healEverything();
		   player.getPA().refreshSkills();
		   player.getItems().addSpecialBar(player.playerEquipment[Player.playerWeapon]);
		   player.getItems().updateSpecialBar();
		   player.getPA().resetAutocast();
		   setCombatSkills(player, stats, false, null);
		   setPrayerAndMagicBook(player, "ANCIENT");
	}

	public static void mainVestaTournament(Player player, String type, String stats) {
		spawnInventory(player, TournamentSets.tournamentInventoryVestaMelee(player, type));
		player.getItems().equipItem(26382, 1, player.playerHat);// TORVA HELM
		player.getItems().equipItem(22616, 1, player.playerChest);// VESTA BODY
		player.getItems().equipItem(22619, 1, player.playerLegs);// VESTA LEGS
		player.getItems().equipItem(22981, 1, player.playerHands);// FEROCIOUS GLOVES
		player.getItems().equipItem(13239, 1, player.playerFeet);// PRIMS BOOTS
		player.getItems().equipItem(21295, 1, player.playerCape);// infernal CAPE
		player.getItems().equipItem(19553, 1, player.playerAmulet);// torture
		player.getItems().equipItem(12468, 1, player.playerShield);// DRAGON KITE NEW
		player.getItems().equipItem(22613, 1, player.playerWeapon);// VESTA LONGSWORD
		player.getItems().equipItem(11773, 1, player.playerRing);// berserker ring i
		player.healEverything();
		player.getPA().refreshSkills();
		player.getItems().addSpecialBar(player.playerEquipment[Player.playerWeapon]);
		player.getItems().updateSpecialBar();
		player.getPA().resetAutocast();
		setCombatSkills(player, "MAIN", false, null);
		setPrayerAndMagicBook(player, "LUNAR");

	}

	public static void exitTournament(Player player) {

		if (activated == true) {
			player.sendMessage("You can not use the exit until the tournament as began.");
			return;
		}

		if (Boundary.getPlayersInBoundary(Boundary.TOURNY_LOBBY) == 1) {
			cancelTournament();
			PlayerHandler.executeGlobalMessage("@red@" + Misc.capitalize(player.getLoginName()) + " left the tournament causing it to cancel!");
		}
		if (Boundary.getPlayersInBoundary(Boundary.TOURNY_COMBAT_AREA) == 1) {
			cancelTournament();
			PlayerHandler.executeGlobalMessage("@red@" + Misc.capitalize(player.getLoginName()) + " left the tournament causing it to cancel!");
		}
		player.getItems().deleteAllItems();
		restoreCombatStats(player);
		player.getHealth().reset();
		player.getPA().refreshSkills();
		player.getPA().movePlayer(3087, 3500, 0);//tele player back to the cowleetkiller at home
		removeFromTournamentLobby(player.getIndex());
		if (player.getHeight() == 20) {
			removeFromTournamentLobby(player.getIndex());
		}
		//player.freezeTimer = 0;
		player.healEverything();
		player.isSkulled = false;
		player.skullTimer = 0;
		player.headIconPk = -1;
		player.getPA().requestUpdates();
	}

	public static void setPrayerAndMagicBook(Player player, String magic) {
		if (magic.equals("ANCIENT")) {
			player.setSidebarInterface(6, 838);
			player.playerMagicBook = 1;
		} else if (magic.equals("LUNAR")) {
			player.setSidebarInterface(6, 29999);
			player.playerMagicBook = 2;
		} else if (magic.equals("MODERN")) {
			player.setSidebarInterface(6, 938);
			player.playerMagicBook = 0;
		}
	}


	public static void setCombatSkills(Player player, String accountType, boolean custom, int[] customSkills) {
		int[] skills = new int[7];
		switch (accountType) {
			case "MAIN":
				skills = new int[]
						{99, 99, 99, 99, 99, 99, 99};
				break;
			case "BERSERKER":
				skills = new int[]
						{75, 45, 99, 99, 99, 52, 99};
				break;
			case "F2P":
				skills = new int[]
						{80, 1, 99, 99, 99, 44, 90};
				break;
			case "RANGED TANK":
				skills = new int[]
						{70, 85, 70, 99, 99, 52, 99};
				break;
			case "PURE":
			case "INITIATE":
				skills = new int[]
						{75, accountType.equals("INITIATE") ? 20 : 1, 99, 99, 99, 52, 99};
				break;
		}
		if (customSkills != null) {
			skills = customSkills;
		}
		for (int i = 0; i < 7; i++) {
			if (i != 3) {
				player.playerXP[i] = player.getPA().getXPForLevel(skills[i]);
				player.playerLevel[i] = skills[i];
				player.playerLevel[i] = skills[i];
			}
		}
		CombatPrayer.resetPrayers(player);
		calculateHitPoints(player);
		for (int i = 0; i < 7; i++) // This for loop has to be after calculateHitPoints, because giving extra
		//experience in attack skill, will give additional hitpoint experience.
		{
			if (i != 3) {
				if (player.playerLevel[i] == 99) {
					player.playerXP[i] = (player.playerXP[i]);
				}
			}
		}

		for (int i = 0; i < 7; i++) {
			player.getPA().setSkillLevel(i, player.playerLevel[i], player.playerXP[i]);
		}
		player.getPA().refreshSkills();
        player.getHealth().reset();
	}



	public static void restoreCombatStats(Player player) {
		for (int index = 0; index < player.baseSkillLevelStoredBeforeTournament.length; index++) {
			player.playerLevel[index] = player.baseSkillLevelStoredBeforeTournament[index];
			player.playerXP[index] = player.skillExperienceStoredBeforeTournament[index];
		}
	}

	public static void storeCombatStats(Player player) {
		for (int index = 0; index < player.baseSkillLevelStoredBeforeTournament.length; index++) {
			player.baseSkillLevelStoredBeforeTournament[index] = player.playerLevel[index];
			player.skillExperienceStoredBeforeTournament[index] = player.playerXP[index];
		}
	}

	public static void calculateHitPoints(Player player) {
		int attackXp = player.playerXP[0];
		if (attackXp > 13034431) {
			attackXp = 13034431;
		}
		int defenceXp = player.playerXP[1];
		if (defenceXp > 13034431) {
			defenceXp = 13034431;
		}
		int strengthXp = player.playerXP[2];
		if (strengthXp > 13034431) {
			strengthXp = 13034431;
		}
		int rangedXp = player.playerXP[4];
		if (rangedXp > 13034431) {
			rangedXp = 13034431;
		}
		int totalMeleeStatsXp = attackXp + defenceXp + strengthXp;
		int totalHPXP = 0;
		totalHPXP = totalMeleeStatsXp / 4;
		totalHPXP += rangedXp / 6;
		totalHPXP *= 1.3;
		totalHPXP += 1154;
		player.playerXP[3] = totalHPXP;
		player.playerLevel[3] = player.getPA().getLevelForXP(player.playerXP[3]);
		if (player.playerLevel[3] == 99) {
			player.playerXP[3] = 13034431;

		}
		player.getHealth().setCurrentHealth(player.getLevel(Skill.HITPOINTS));
		player.getHealth().setMaximumHealth(player.getLevel(Skill.HITPOINTS));
		player.getHealth().reset();
		player.healEverything();
		player.getPA().refreshSkill(3);
		player.getPA().refreshSkills();
		player.calculateCombatLevel();
		player.getPA().requestUpdates();
	}

	public static boolean hasExcessBrews(Player player, int excessDosesAmount) {
		// If excess doses reaches 5 or more, then flag.
		int excessDoses = 0;
		player.excessBrews = false;
		player.brewCount = 0;
		for (int index = 0; index < player.playerItems.length; index++) {
			int itemId = player.playerItems[index] - 1;

			// 4 Dose Saradomin brew.
			if (itemId == 6685) {
				excessDoses += 2;
			}

			// 3 Dose Saradomin brew.
			else if (itemId == 6687) {
				excessDoses += 1;
			}
			player.brewCount = excessDoses;
			if (excessDoses >= excessDosesAmount) {
				player.excessBrews = true;
				return true;
			}
		}
		return false;
	}

	private static String macAddress;
	public static String getMacAddress() {
		return macAddress;
	}


}
