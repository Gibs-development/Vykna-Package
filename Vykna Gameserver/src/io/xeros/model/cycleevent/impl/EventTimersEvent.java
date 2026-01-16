package io.xeros.model.cycleevent.impl;



import io.xeros.Configuration;
import io.xeros.content.revenant_event.RevenantEventBossHandler;
import io.xeros.model.cycleevent.Event;
import io.xeros.util.Misc;

import java.util.concurrent.TimeUnit;


/**
 * Author C.T for koranes
 *
 */

public class EventTimersEvent extends Event<Object> {

	/**
	 * The amount of time in game cycles (600ms) that the event pulses at
	 */
	private static final int INTERVAL = Misc.toCyclesOrDefault(1, 1, TimeUnit.SECONDS);

	/**
	 * Creates a new event to cycle through messages for the entirety of the runtime
	 */
	public EventTimersEvent() {
		super("", new Object(), INTERVAL);
	}

	@Override
	public void execute() {
		//Bonus skill timer
		if (Configuration.BONUS_SKILL_TIMER > 0) {
			Configuration.BONUS_SKILL_TIMER--;
		}

		if (Configuration.REV_EVENT_TIMER > 0) {
			Configuration.REV_EVENT_TIMER--;
		}

		if (Configuration.SOLAK_EVENT_TIMER > 0) {
			Configuration.SOLAK_EVENT_TIMER--;
		}

		if (Configuration.NIGHTMARE_EVENT_TIMER > 0) {
			Configuration.NIGHTMARE_EVENT_TIMER--;
		}

		if (Configuration.GLOD_EVENT_TIMER > 0) {
			Configuration.GLOD_EVENT_TIMER--;
		}

		if (Configuration.GALVEK_EVENT_TIMER > 0) {
			Configuration.GALVEK_EVENT_TIMER--;
		}

		if (Configuration.STAR_EVENT_TIMER > 0) {
			Configuration.STAR_EVENT_TIMER--;
		}

		if (Configuration.TREE_EVENT_TIMER > 0) {
			Configuration.TREE_EVENT_TIMER--;
		}

		if (Configuration.WILD_EVENT_TIMER > 0) {
			Configuration.WILD_EVENT_TIMER--;
		}

	}
}
