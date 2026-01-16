package io.xeros.content.world_event;


import io.xeros.Configuration;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.broadcasts.Broadcast;
import io.xeros.util.Misc;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 *
 *
 * @author C.T for runerogue.
 */
public class WorldEvent {

	public static ArrayList<String> debug = new ArrayList<String>();


	/**
	 * Event length in minutes.
	 */
	private static int EVENT_LENGTH = 20;

	/**
	 * Time the event has ended or is scheduled to end.
	 */
	public static long timeEventEnd;

	/**
	 * Current active event.
	 */
	private static String currentEvent = "";

	/**
	 * Store the upcoming event.
	 */
	public static String nextEvent = "";

	public static boolean eventStartedAnnounced;

	private static CycleEvent cycleEvent;

	public static void startEvent() {

		cycleEvent = new CycleEvent() {

			@Override
			public void execute(CycleEventContainer container) {

				if(getCurrentEvent() == null) {
					container.setTick(Misc.toCycles(100, TimeUnit.MINUTES));
					Configuration.WILD_EVENT_TIMER = WorldEvent.generateTime();
					return;
				}
				container.setTick(generateTime());
				WorldEvent.start20MinutesLastingEvent("WILDERNESS EVENT");
				Configuration.WILD_EVENT_TIMER = WorldEvent.generateTime();
			}

			@Override
			public void onStopped() {

			}

		};
		setCycleEventContainer(CycleEventHandler.getSingleton().addEvent(WorldEvent.class, cycleEvent, generateTime()));
	}



	public static int generateTime(){
		return Misc.toCycles(100, TimeUnit.MINUTES);
	}

	private static CycleEventContainer cycleEventContainer;

	public static CycleEventContainer getCycleEventContainer() {
		return cycleEventContainer;
	}

	public static void setCycleEventContainer(CycleEventContainer cycleEventContainer) {
		WorldEvent.cycleEventContainer = cycleEventContainer;
	}

	public static void start20MinutesLastingEvent(String eventType) {
		timeEventEnd = System.currentTimeMillis() + (EVENT_LENGTH * 60000);
		getRandomEvent(eventType);
		debug.add(Misc.getDateAndTime() + ": Here9: " + System.currentTimeMillis() + ", " + timeEventEnd + ", " + getCurrentEvent() + ", " + nextEvent);
		eventStartedAnnounced = true;
		PlayerHandler.executeGlobalMessage("@red@Wild event has started! It will last for " + EVENT_LENGTH + " minutes.");
		new Broadcast(nextEvent).copyMessageToChatbox().submit();
		Object object = new Object();
		CycleEventHandler.getSingleton().addEvent(object, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				container.stop();
			}

			@Override
			public void onStopped() {
				eventEnded();
			}
		}, 100 * EVENT_LENGTH);
	}

	public static void logInUpdate(Player player) {
		if (!eventStartedAnnounced) {
			return;
		}
		long minutesLeft = ((timeEventEnd - System.currentTimeMillis()) / 1000) / 60;
		player.sendMessage("@blu@" + "Event active: " + nextEvent + ", " + minutesLeft + " minutes left.");
	}

	/**
	 * Set a random event.
	 */
	public static void getRandomEvent(String eventType) {
		if (eventType.equals("WILDERNESS EVENT")) {

				String[] wildTypes =
						{"Venenatis", "Callisto", "Seren", "Chaos elemental", "Revenants", "Scorpia"};

				int amountOfNpcsToKill = 3;
				int currentNpcsAdded = 0;
				int maximumCancels = wildTypes.length - amountOfNpcsToKill;
				int currentCancels = 0;
				String npcsToKill = "";
				for (int index = 0; index < wildTypes.length; index++) {
					if (currentNpcsAdded == amountOfNpcsToKill) {
						break;
					}
					if (currentCancels == maximumCancels) {
						if (npcsToKill.isEmpty()) {
							npcsToKill = wildTypes[index];
						} else {
							npcsToKill = npcsToKill + ", " + wildTypes[index];
						}
						currentNpcsAdded++;
						continue;
					}
					double chance = (double) wildTypes.length / (double) amountOfNpcsToKill;
					chance *= 10;
					if (Misc.random(1, (int) chance) > 10) {
						currentCancels++;
						continue;
					}
					if (npcsToKill.isEmpty()) {
						npcsToKill = wildTypes[index];
					} else {
						npcsToKill = npcsToKill + ", " + wildTypes[index];
					}
					currentNpcsAdded++;
				}
				//nextEvent = "Kill " + npcsToKill + " for 3x rare drop chance!";
			    nextEvent = "All wild mobs will x2 there drops & send half of them to your bank.";
				setCurrentEvent(npcsToKill);
			}
		}

	/**
	 * Event has ended.
	 */
	public static void eventEnded() {
		PlayerHandler.executeGlobalMessage("@red@The wild event has now ended!");
		debug.add(Misc.getDateAndTime() + ": Here10: " + System.currentTimeMillis() + ", " + timeEventEnd + ", " + getCurrentEvent() + ", " + nextEvent + ", "
		          + eventStartedAnnounced);
		setCurrentEvent("");
		eventStartedAnnounced = false;
	}

	/**
	 * @return True if the eventName matches the current active event.
	 */
	public static boolean getActiveEvent(String eventName) {
		return getCurrentEvent().equals(eventName);
	}

	public static String getCurrentEvent() {
		return currentEvent;
	}

	public static void setCurrentEvent(String currentEvent) {
		WorldEvent.currentEvent = currentEvent;
	}

}
