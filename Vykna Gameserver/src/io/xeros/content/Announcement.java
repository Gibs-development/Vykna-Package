package io.xeros.content;


import io.xeros.Configuration;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Announcements.
 *
 * @author C.T
 */
public class Announcement {


	/**
	 * How often to announce game tips.
	 */
	private final static int GAME_ANNOUNCEMENT_TIP_INTERVAL = 16;

	/**
	 * How often to announce donate announcements.
	 */
	private final static int DONATE_ANNOUNCEMENTS_MINUTES_INTERVAL = 12;

	/**
	 * How often to execute a pending announcement. When a donate or game tip announcement has been executed, it will be added to a pending list where it is executed every x minutes.
	 */
	private final static int PENDING_ANNOUNCEMENT_INTERVAL = 5;

	private static int value = Misc.random(34);//changed to 34 any problems changed back to 37 cam

	public static void announce(String string, String colour) {
		for (int i = 0; i < Configuration.MAX_PLAYERS; i++) {
			if (PlayerHandler.players[i] != null) {
				PlayerHandler.players[i].sendMessage(colour + string);
			}
		}
	}

	public static void announce(String string) {
		for (int i = 0; i < Configuration.MAX_PLAYERS; i++) {
			if (PlayerHandler.players[i] != null) {
				PlayerHandler.players[i].sendMessage(string);
			}
		}
	}

	private static int donateIndex;

	private static int donateTypeValue = 0;

	public static ArrayList<String> announcementPendingList = new ArrayList<String>();

	public static void donateAnnouncementEvent() {
		Object object = new Object();
		CycleEventHandler.getSingleton().addEvent(object, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				donateTypeValue++;
				if (donateTypeValue == 1) {
					//DonatorPost.tokensBeingPurchasedMessage();
				} else {
					donateAnnouncementAction();
				}
				if (donateTypeValue == 2) {
					donateTypeValue = 0;
				}
			}

			@Override
			public void onStopped() {
			}
		}, 100 * DONATE_ANNOUNCEMENTS_MINUTES_INTERVAL);

	}

	/**
	 * Announcements that are called every specified minutes.
	 */
	@SuppressWarnings("unchecked")
	public static void announcementGameTick() {
		Object object = new Object();
		CycleEventHandler.getSingleton().addEvent(object, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				String[] message =
					{
							"<col=f4ed2e>News:<col=3f3fff> The number 1 Voter of each week will receive a reward! Use ::vote!",
							"<col=f4ed2e>News:<col=3f3fff> Check the ::rules so you stay safe. Be friendly to everyone!",
							"<col=f4ed2e>News:<col=3f3fff> You can now check the latest updates by doing ::news!",
							"<col=f4ed2e>News:<col=3f3fff> Wilderness bosses is a good way to make some money.",
							"<col=f4ed2e>News:<col=3f3fff> Never use a password from another website/rsps!",
							"<col=f4ed2e>News:<col=3f3fff> You can purchase skill capes from the Wise Old Man.",
							"<col=f4ed2e>News:<col=3f3fff> Go to Quest tab then Coin Tab to see your collection log and more",
							"<col=f4ed2e>News:<col=3f3fff> View the guidebook in your quest tab to learn more about " + Configuration.SERVER_NAME + "!",
							"<col=f4ed2e>News:<col=3f3fff> Kill revenants! One of the top money making methods.",
							"<col=f4ed2e>News:<col=3f3fff> The higher your killstreak, the more bonus pkp you will earn!",
							"<col=f4ed2e>News:<col=3f3fff> Great pvp loot can be received from the glod chest",
							"<col=f4ed2e>News:<col=3f3fff> Use the Fire of Exchange, south of grand exchange to burn items for points!",
							"<col=f4ed2e>News:<col=3f3fff> Check your quest tab to know what event is active and what will soon spawn!",	
							"<col=f4ed2e>News:<col=3f3fff> Talk to the wizard next the vote chest to access daily rewards!",
							"<col=f4ed2e>News:<col=3f3fff> Tell your friends and family about " + Configuration.SERVER_NAME + "!",
							"<col=f4ed2e>News:<col=3f3fff> Don't forget to ::vote and then claim your reward by typing ::reward 1 all!",
							"<col=f4ed2e>News:<col=3f3fff> Become rich by thieving stalls at home",
							"<col=f4ed2e>News:<col=3f3fff> Items harvested and created through skilling can be sold to shop for profit.",
							"<col=f4ed2e>News:<col=3f3fff> " + Configuration.SERVER_NAME + " is community driven, be sure to voice your opinion on the forums!",
							"<col=f4ed2e>News:<col=3f3fff> Use the wilderness resource area earn 20% bonus experience!",
							"<col=f4ed2e>News:<col=3f3fff> Voting will bring more players to koranes ::vote",
							"<col=f4ed2e>News:<col=3f3fff> " + Configuration.SERVER_NAME + " Vote every 12 hours for nice rewards.",
							"<col=f4ed2e>News:<col=3f3fff> Did you know, killing revenants skulled gives higher droprate bonus.",
							"<col=f4ed2e>News:<col=3f3fff> ::discord to communicate with fellow players.",
							"<col=f4ed2e>News:<col=3f3fff> Earn Youtube rank & 50m coins"
							+ " if your video meets the requirements ::yt",
							"<col=f4ed2e>News:<col=3f3fff> Max cape is only those worthy to wear it at maxed total.",
							"<col=f4ed2e>News:<col=3f3fff> Use glod keys on the chest at home for rewards.",
							"<col=f4ed2e>News:<col=3f3fff> Report bugs on the forums in ::bugs",
							"<col=f4ed2e>News:<col=3f3fff> The drop table can show which monsters drop a certain item!",
							"<col=f4ed2e>News:<col=3f3fff> Join the '" + Configuration.SERVER_NAME + "' clan chat to meet other players.",
							"<col=f4ed2e>News:<col=3f3fff> Not in a clan yet? Join or create a clan and dominate the Wild at ::forums",

					};
				announcementPendingList.add(message[value]);
				value++;
				if (value > message.length - 1) {
					value = 0;
				}
			}

			@Override
			public void onStopped() {
			}
		}, 100 * GAME_ANNOUNCEMENT_TIP_INTERVAL);


	}

	private static long pendingAnnouncementSentTime;

	private static long staffAnnouncementSentTime;

	
	public static void announcementPendingEvent() {
		Object object = new Object();
		CycleEventHandler.getSingleton().addEvent(object, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (System.currentTimeMillis() - pendingAnnouncementSentTime < (PENDING_ANNOUNCEMENT_INTERVAL * 60000)) {
					return;
				}
				if (announcementPendingList.isEmpty()) {
					return;
				}
				pendingAnnouncementSentTime = System.currentTimeMillis();
				int indexRandom = Misc.random(announcementPendingList.size() - 1);
				announce(announcementPendingList.get(indexRandom), "");
				announcementPendingList.remove(indexRandom);
			}

			@Override
			public void onStopped() {
			}
		}, 50);

		CycleEventHandler.getSingleton().addEvent(object, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (System.currentTimeMillis() - pendingAnnouncementSentTime < 420000) {
					return;
				}
				if (System.currentTimeMillis() - staffAnnouncementSentTime < Misc.getMinutesToMilliseconds(30)) {
					return;
				}
				boolean online = false;
				for (int index = 0; index < Configuration.MAX_PLAYERS; index++) {
					Player loop = PlayerHandler.players[index];
					if (loop == null) {
						continue;
					}
					//if (!loop.getMode(). && !loop.isSupportRank() && !loop.isAdministratorRank()) {
					//	continue;
					//}
					//if (loop.getPrivateChat() != ServerConstants.PRIVATE_ON) {
					//	continue;
					//}
					online = true;
					break;
				}
				if (!online) {
					return;
				}
				staffAnnouncementSentTime = System.currentTimeMillis();
				announce("Need help or guidance? Pm a staff member on ::staff", "Announcements: ");
			}

			@Override
			public void onStopped() {
			}
		}, 50);
	}

	public static void donateAnnouncementAction() {
		String[] donateText =
				{
						"<col=f4ed2e>News:<col=3f3fff> Try your luck and buy a M-box for only $4.99, you may win a RuneWars crystal!",
						"<col=f4ed2e>News:<col=3f3fff> Donate to help your favourite server grow ::donate",
						"<col=f4ed2e>News:<col=3f3fff> Want to be a Donator with many special rewards & perks? ::donate today!",
						"<col=f4ed2e>News:<col=3f3fff> Check out the Donator npc at ::shops for loads of powerful rewards!",
						"<col=f4ed2e>News:<col=3f3fff> Help " + Configuration.SERVER_NAME + " by donating to help the server grow!",
						"<col=f4ed2e>News:<col=3f3fff> Want to own a dice bag to start hosting bets and fp?",
						"<col=f4ed2e>News:<col=3f3fff> Want to get rich quick? Buy Mystery boxes which contain so many rares!",
						"<col=f4ed2e>News:<col=3f3fff> Donators can yell, use an amazing Donator Zone & lots more!",
						"<col=f4ed2e>News:<col=3f3fff> M-boxes give 15 % more loot on average than anything else in the Donator shop!",
						"<col=f4ed2e>News:<col=3f3fff> Ancestral, Morrigans & rares can be purchased from the Donator shop!",
				};
		announcementPendingList.add(donateText[donateIndex]);
		donateIndex++;
		if (donateIndex > donateText.length - 1) {
			donateIndex = 0;
		}
	

	}

}
