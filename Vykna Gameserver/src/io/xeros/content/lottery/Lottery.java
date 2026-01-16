package io.xeros.content.lottery;

import io.xeros.Configuration;
import io.xeros.achievements.AchievementHandler;
import io.xeros.achievements.AchievementList;
import io.xeros.content.Announcement;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.DialogueHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.broadcasts.Broadcast;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;
import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Lottery system.
 *
 * @author C.T, for RuneRogue
 */
public class Lottery {

	public static boolean spawned = false;

	public static int lotteryNpcIndex;

	public static int getLotteryTicketCost() {
		return 5000000;
	}

	/**
	 * The amount of money that is taxed from the lottery.
	 */
	public final static int COMMISION_PERCENTAGE = 15;

	/**
	 * Location of where the total amount of tickets purchased is saved at.
	 */
	private final static String TOTAL_TICKETS_FILE = "backup/logs/lottery/total_tickets.txt";

	/**
	 * Location of where the lottery entries for each player is saved at.
	 */
	private final static String LOTTERY_ENTRIES_FILE = "backup/logs/lottery/entries.txt";


	/**
	 * The total amount of tickets purchased from the lottery, this is also used to calculate the total pot.
	 */
	private static int totalTicketsPurchased;

	/**
	 * Store time of when pre-draw announcement has started.
	 */
	public static long timePreDrawAnnounced;

	/**
	 * Asking Durial what the current pot is at.
	 */
	public static void currentPotAtDialogueOption(Player player) {

		NPC npc = NPCHandler.getNpc(1017);
		npc.forceChat("The pot is currently at " + getTotalPotString() + "!");
		player.getDH().sendNpcChatAnimation("The current lottery pot is at " + getTotalPotString() + "!", "You have a " + getWinningPercentage(player.getLoginName()) + "% chance to win.",
				DialogueHandler.FacialAnimation.HAPPY.getAnimationId());
		player.nextChat = 1596;
	}

	/**
	 * @return The total lottery pot.
	 */
	private static String getTotalPotString() {
		return Misc.formatRunescapeStyle(getTotalPotNumber()) + " " + Configuration.getMainCurrencyName();
	}

	private static int getTotalPotNumber() {
		int totalPotWorth = (totalTicketsPurchased * getLotteryTicketCost());
		double commission = 100 - COMMISION_PERCENTAGE;
		commission /= 100.0;
		return (int) (totalPotWorth * commission);
	}

	/**
	 * @return The winning percentage of the player.
	 */
	private static double getWinningPercentage(String playerName) {
		LotteryDatabase data = LotteryDatabase.getPlayerLotteryInstance(playerName);
		if (data == null) {
			return 0;
		}
		return Misc.roundDoubleToNearestTwoDecimalPlaces(((double) data.getTicketsPurchased() / (double) totalTicketsPurchased) * 100.0);
	}

	/**
	 * Asking Durial what my winning percentage is.
	 */
	public static void percentageOfWinningDialogueOption(Player player) {
		player.getDH().sendNpcChatAnimation("You have a " + getWinningPercentage(player.getLoginName()) + "% chance to win.", DialogueHandler.FacialAnimation.HAPPY.getAnimationId());
		player.nextChat = 1596; // Bring back to lottery option menu.
	}

	/**
	 * Asking Durial that i want to buy tickets/
	 */
	public static void buyLotteryTicketsDialogueOption(Player player) {
		player.getPA().closeAllWindows();
		showAmountInterface(player, "BUY LOTTERY TICKETS", "Enter the amount of tickets you will buy for " + Misc.formatNumber(getLotteryTicketCost()) + " each");
	}

	public static void showAmountInterface(Player player, String action, String text) {
		player.setAmountInterface(action);
		player.getOutStream().createFrame(28);
		player.sendMessage("" + text);
	}



	/**
	 * Receive the x amount tickets wanted to be bought by the player.
	 */
	private static final List<Player> entries = new ArrayList<>();
	public static boolean receiveLotteryBuyAmount(Player player, String action, int Xamount) {
		if (!action.equals("BUY LOTTERY TICKETS")) {
			return false;
		}
		int currentCurrencyAmount = ItemAssistant.getItemAmount(player, Configuration.getMainCurrencyId());
		int maxAmount = Integer.MAX_VALUE / getLotteryTicketCost();
		if (Xamount > maxAmount) {
			Xamount = maxAmount;
		}
		if (Xamount * getLotteryTicketCost() > currentCurrencyAmount) {
			Xamount = currentCurrencyAmount / getLotteryTicketCost();
		}
		if (hasEntered(player)) {
			player.getPA().sendStatement("You are already entered in the lottery!");
			return true;
		}

		if (Xamount == 0) {
			player.getPA().closeAllWindows();
			player
			.sendMessage("You need at least " + Misc.formatNumber(getLotteryTicketCost()) + " " + Configuration.getMainCurrencyName() + " to buy a lottery ticket.");
			return true;
		}

		if (Xamount >= 6 && Xamount <= 900_000_000) {
			player.getPA().closeAllWindows();
			player.sendMessage("You can only purchase 1 to 5 tickets at a time.");
			return true;
		}


		LotteryDatabase data = LotteryDatabase.getPlayerLotteryInstance(player.getLoginName());
		if (data == null) {
			LotteryDatabase.lotteryDatabase.add(new LotteryDatabase(player.getLoginName(), Xamount));
		} else {
			data.setTickestPurchased(data.getTicketsPurchased() + Xamount);
		}
		totalTicketsPurchased += Xamount;
		player.getItems().deleteItem(Configuration.getMainCurrencyId(), Xamount * getLotteryTicketCost());
		player.getDH().sendStatement(
				"You have purchased x" + Misc.formatNumber(Xamount) + " lottery ticket" + Misc.getPluralS(Xamount) + " for " + Misc.formatNumber(Xamount * getLotteryTicketCost())
				+ " " + Configuration.getMainCurrencyName() + ".");
		NPC npc = NPCHandler.getNpc(1017);
		npc.forceChat("The pot is currently at " + getTotalPotString() + "!");
		player.lotteryTickets+=Xamount;
		entries.add(player);
		AchievementHandler.activate(player, AchievementList.ENTER_THE_LOTTERY_5_TIMES, 1);//NEW ACHIEVEMENTS
		return true;

	}

	/**
	 * Save the lottery files.
	 */
	public static void saveLotteryFiles() {
		FileUtility.deleteAllLines(TOTAL_TICKETS_FILE);
		FileUtility.deleteAllLines(LOTTERY_ENTRIES_FILE);
		FileUtility.addLineOnTxt(TOTAL_TICKETS_FILE, totalTicketsPurchased + "");

		ArrayList<String> line = new ArrayList<String>();
		for (int index = 0; index < LotteryDatabase.lotteryDatabase.size(); index++) {
			LotteryDatabase data = LotteryDatabase.lotteryDatabase.get(index);
			line.add(data.getPlayerName() + Configuration.TEXT_SEPERATOR + data.getTicketsPurchased());
		}
		FileUtility.saveArrayContentsSilent(LOTTERY_ENTRIES_FILE, line);
	}

	/**
	 * Read the lottery file saves.
	 */
	public static void readLotteryFiles() {
		for (int index = 0; index < NPCHandler.npcs.length; index++) {
			NPC npc = NPCHandler.getNpc(1017);
			if (npc == null) {
				continue;
			}
			if (npc.npcType == 1017) {
				lotteryNpcIndex = npc.getNpcId();
			}
		}
		try {
			totalTicketsPurchased = Integer.parseInt(FileUtility.readFirstLine(TOTAL_TICKETS_FILE));
		} catch (Exception e) {
			e.printStackTrace();
		}

		ArrayList<String> data = FileUtility.readFile(LOTTERY_ENTRIES_FILE);
		for (int index = 0; index < data.size(); index++) {
			String parse[] = data.get(index).split(Configuration.TEXT_SEPERATOR);
			String name = parse[0];
			int ticketsPurchased = Integer.parseInt(parse[1]);
			LotteryDatabase.lotteryDatabase.add(new LotteryDatabase(name, ticketsPurchased));
		}
	}

	/**
	 * Asking the lottery manager how does the lottery work.
	 */
	public static void howDoesTheLotteryWorkDialogueOption(Player player) {
		player.getDH().sendNpcChatAnimation("I am here to make one lucky player a millionaire!", "Every 2 hours,", "the lottery will be, drawn and a new millionare will", "be announced!",
				DialogueHandler.FacialAnimation.HAPPY.getAnimationId());
		player.nextChat = 1598;
	}

	/**
	 * Start announcing the pre-draw which is 15 minutes before the winner is announced
	 */

	private static CycleEvent cycleEvent;
	private static CycleEventContainer cycleEventContainer;

	public static void startLottery() {
		cycleEvent = new CycleEvent() {

			@Override
			public void execute(CycleEventContainer container) {

				if(spawned == true) {
				    container.setTick(Misc.toCycles(120, TimeUnit.MINUTES));
					return;
				}
				container.setTick(generateTime());
				preDrawAnnouncement();

			}

			@Override
			public void onStopped() {

			}

		};
		setCycleEventContainer(CycleEventHandler.getSingleton().addEvent(Lottery.class, cycleEvent, generateTime()));
	}

	public static int generateTime(){
		return Misc.toCycles(120, TimeUnit.MINUTES);
	}

	public static CycleEventContainer getCycleEventContainer() {
		return cycleEventContainer;
	}

	public static void setCycleEventContainer(CycleEventContainer cycleEventContainer) {
		Lottery.cycleEventContainer = cycleEventContainer;
	}

	/**
	 * Announce the winner of the lottery.
	 */

	public final static double M_BOX_CHANCE = 35;
	public final static double SUPER_M_BOX_CHANCE = 75;
	public final static double ULTRA_M_BOX_CHANCE = 125;

	public static void announceWinner() {
		ArrayList<String> data = new ArrayList<String>();

		for (int index = 0; index < LotteryDatabase.lotteryDatabase.size(); index++) {
			LotteryDatabase lotteryData = LotteryDatabase.lotteryDatabase.get(index);
			for (int i = 0; i < lotteryData.getTicketsPurchased(); i++) {
				data.add(lotteryData.getPlayerName());
			}
		}
		if (data.isEmpty()) {
			Announcement.announce("@red@No one has entered the lottery.");
			spawned = false;
			return;
		}
		String lotteryWinnerName = data.get(Misc.random(data.size() - 1));
		new Broadcast("<img=27><col=a36718> " + lotteryWinnerName + " won the lottery worth " + getTotalPotString() + " with " + getWinningPercentage(lotteryWinnerName)
				+ "% chance of winning!").copyMessageToChatbox().submit();
		Player winner = Misc.getPlayerByName(lotteryWinnerName);
		NPC npc = NPCHandler.getNpc(1017);
		npc.forceChat("Congratulations " + lotteryWinnerName + " has won the lottery worth " + getTotalPotString() + "!");
		Discord.writeEventMessage("Congratulations " + lotteryWinnerName + " has won the lottery worth " + getTotalPotString() + "!");
		winner.getItems().addItemToBankOrDrop(Configuration.getMainCurrencyId(), getTotalPotNumber());
		winner.lotteryWins+=1;
		entries.clear();
		//AchievementHandler.activate(winner, AchievementList.WIN_THE_LOTTERY_3_TMES, 1);//NEW ACHIEVEMENTS
		if (Misc.hasOneOutOf(M_BOX_CHANCE)) {
			winner.getItems().addItemToBankOrDrop(6199, 1);
			PlayerHandler.executeGlobalMessage("@red@"+ Misc.capitalize(winner.getLoginName()) + "@blu@ has received an @red@Mystery Box @blu@from the @red@Lottery.");
			winner.lotteryMbox+=1;
		}
		if (Misc.hasOneOutOf(SUPER_M_BOX_CHANCE)) {
			winner.getItems().addItemToBankOrDrop(6828, 1);
			PlayerHandler.executeGlobalMessage("@red@"+ Misc.capitalize(winner.getLoginName()) + "@blu@ has received an @red@Super Mystery Box @blu@from the @red@Lottery.");
			winner.lotteryMbox+=1;
		}
		if (Misc.hasOneOutOf(ULTRA_M_BOX_CHANCE)) {
			winner.getItems().addItemToBankOrDrop(13346, 1);
			PlayerHandler.executeGlobalMessage("@red@"+ Misc.capitalize(winner.getLoginName()) + "@blu@ has received an @red@Ultra Mystery Box @blu@from the @red@Lottery.");
			winner.lotteryMbox+=1;
		}
		totalTicketsPurchased = 0;
		LotteryDatabase.lotteryDatabase.clear();
		spawned = false;
	}

	/**
	 * List of emotes that Durial uses during the pre-draw.
	 */
	public final static int[] EMOTES =
			{866, 2106, 2107, 2108, 2109, 0x850, 3543, 4280};

	/**
	 * @return The amount of minutes left until the winner is announced.
	 */
	private static int getMinutesLeftTillWinner() {
		return (int) (15 - ((System.currentTimeMillis() - (timePreDrawAnnounced - 1000)) / 60000));
	}

	/**
	 * True if winner has been announced.
	 */
	public static boolean winnerAnnounced;

	/**
	 * The pre-draw announcement events.
	 */
	public static void preDrawAnnouncement() {
		winnerAnnounced = false;
		spawned = true;
		timePreDrawAnnounced = System.currentTimeMillis();
		Object object = new Object();
		CycleEventHandler.getSingleton().addEvent(object, new CycleEvent() {
			int timer = 0;

			@Override
			public void execute(CycleEventContainer container) {
				NPC npc = NPCHandler.getNpc(1017);
				npc.forceChat("Lottery is at " + getTotalPotString() + "! Talk to the lottery manager at ::home to win, " + getMinutesLeftTillWinner() + " min" + Misc.getPluralS(
						getMinutesLeftTillWinner()) + " left!");
				Announcement.announce(
						"<img=27><col=a36718> Lottery is at @red@" + getTotalPotString() + "<col=a36718>! Talk to the Lottery manager at ::home to win, @red@" + getMinutesLeftTillWinner() + " min"
						+ Misc.getPluralS(getMinutesLeftTillWinner()) + " left!");
				timer++;
				if (timer == 15) {
					timer = 0;
					container.stop();
				}
			}

			@Override
			public void onStopped() {
				winnerAnnounced = true;
				announceWinner();
			}
		}, 100);

		Object object1 = new Object();
		CycleEventHandler.getSingleton().addEvent(object1, new CycleEvent() {
			int timer = 0;

			@Override
			public void execute(CycleEventContainer container) {
				if (winnerAnnounced) {
					container.stop();
					return;
				}
				NPC npc = NPCHandler.getNpc(1017);
				if (Misc.hasPercentageChance(25)) {
					npc.startAnimation(EMOTES[Misc.random(EMOTES.length - 1)]);
				}
				npc.forceChat(
						"The lottery is at " + getTotalPotString() + "! Talk to me to win, " + getMinutesLeftTillWinner() + " min" + Misc.getPluralS(getMinutesLeftTillWinner())
						+ " left!");
				timer++;
				if (timer == 1500) {
					timer = 0;
					container.stop();
				}
			}

			@Override
			public void onStopped() {
			}
		}, 1);

	}

	public static boolean hasEntered(Player player) {
		if (entries.contains(player)) {
			return true;
		}
		return false;
	}



}
