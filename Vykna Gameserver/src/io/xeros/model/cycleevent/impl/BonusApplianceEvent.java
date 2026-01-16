package io.xeros.model.cycleevent.impl;

import java.util.concurrent.TimeUnit;

import io.xeros.Configuration;
import io.xeros.content.hespori.*;
import io.xeros.content.wogw.Wogw;
import io.xeros.model.cycleevent.Event;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;

public class BonusApplianceEvent extends Event<Object> {
	
	/**
	 * The amount of time in game cycles (600ms) that the event pulses at
	 */
	private static final int INTERVAL = Misc.toCycles(1, TimeUnit.SECONDS);

	/**
	 * Creates a new event to cycle through messages for the entirety of the runtime
	 */
	public BonusApplianceEvent() {
		super("", new Object(), INTERVAL);
	}


	@Override
	public void execute() {
		if (Wogw.EXPERIENCE_TIMER > 0) {
			Wogw.EXPERIENCE_TIMER--;
			if (Wogw.EXPERIENCE_TIMER == 1) {
				PlayerHandler.executeGlobalMessage("<col=6666FF>The well of goodwill is no longer granting bonus experience!");
				Configuration.BONUS_XP_WOGW = false;
				Wogw.appendBonus();
			}
		}
		if (Wogw.PC_POINTS_TIMER > 0) {
			Wogw.PC_POINTS_TIMER--;
			if (Wogw.PC_POINTS_TIMER == 1) {
				PlayerHandler.executeGlobalMessage("<col=6666FF>The well of goodwill is no longer granting bonus pc points!");
				Configuration.BONUS_PC_WOGW = false;
				Wogw.appendBonus();
			}
		}
		if (Wogw.DOUBLE_DROPS_TIMER > 0) {
			Wogw.DOUBLE_DROPS_TIMER--;
			if (Wogw.DOUBLE_DROPS_TIMER == 1) {
				PlayerHandler.executeGlobalMessage("<col=6666FF>The well of goodwill is no longer granting double drops!");
				Configuration.DOUBLE_DROPS = false;
				Wogw.appendBonus();
			}
		}
		if (Wogw.BARROWS_BONUS_TIMER > 0) {
			Wogw.BARROWS_BONUS_TIMER--;
			if (Wogw.BARROWS_BONUS_TIMER == 1) {
				PlayerHandler.executeGlobalMessage("<col=6666FF>The well of goodwill is no longer granting double barrows drops!");
				Configuration.DOUBLE_BARROWS = false;
				Wogw.appendBonus();
			}
		}




		/**
		 * Hespori Seeds
		 */
		if (Hespori.ATTAS_TIMER > 0) {
			Hespori.ATTAS_TIMER--;
			if (Hespori.ATTAS_TIMER == 1) {
				PlayerHandler.executeGlobalMessage("@red@The Attas plant is no longer granting XP!");
				new AttasBonus().deactivate();
			}
		}
		if (Hespori.KRONOS_TIMER > 0) {
			Hespori.KRONOS_TIMER--;
			if (Hespori.KRONOS_TIMER == 1) {
				PlayerHandler.executeGlobalMessage("@red@The Kronos plant is no longer granting double Raids 1 keys!");
				new KronosBonus().deactivate();
			}
		}
		if (Hespori.IASOR_TIMER > 0) {
			Hespori.IASOR_TIMER--;
			if (Hespori.IASOR_TIMER == 1) {
				PlayerHandler.executeGlobalMessage("@red@The Iasor plant is no longer granting drop rate bonus!");
				new IasorBonus().deactivate();
			}
		}

		if (Hespori.GOLPAR_TIMER > 0) {
			Hespori.GOLPAR_TIMER--;
			if (Hespori.GOLPAR_TIMER == 1) {
				PlayerHandler.executeGlobalMessage("@red@The Golpar plant is no longer granting more loot!");
				new GolparBonus().deactivate();
			}
		}
		if (Hespori.BUCHU_TIMER > 0) {
			Hespori.BUCHU_TIMER--;
			if (Hespori.BUCHU_TIMER == 1) {
				PlayerHandler.executeGlobalMessage("@red@The Buchu plant is no longer granting 2x Boss points!");
				new BuchuBonus().deactivate();
			}
		}
		if (Hespori.KELDA_TIMER > 0) {
			Hespori.KELDA_TIMER--;
			if (Hespori.KELDA_TIMER == 1) {
				PlayerHandler.executeGlobalMessage("@red@The Kelda plant is no longer granting 2x Larren's Keys!");
				new KeldaBonus().deactivate();
			}
		}
		if (Hespori.NOXIFER_TIMER > 0) {
			Hespori.NOXIFER_TIMER--;
			if (Hespori.NOXIFER_TIMER == 1) {
				PlayerHandler.executeGlobalMessage("@red@The Noxifer plant is no longer granting 2x Slayer points!");
				new NoxiferBonus().deactivate();
			}
		}
		if (Hespori.CELASTRUS_TIMER > 0) {
			Hespori.CELASTRUS_TIMER--;
			if (Hespori.CELASTRUS_TIMER == 1) {
				PlayerHandler.executeGlobalMessage("@red@The Celastrus plant is no longer granting x2 Brimstone keys!");
				new CelastrusBonus().deactivate();
			}
		}



	}
}
