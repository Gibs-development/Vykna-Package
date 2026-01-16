package io.xeros.content;

import io.xeros.achievements.InterfaceHandler;
import io.xeros.model.entity.player.Player;

/**
 * Handles the credit tab interface
 * @author C.T For RuneRogue
 *
 */
public class CreditTab extends InterfaceHandler {

	private final String[] text = {
			"Vote point stores",
			"General stores",
			"Skilling supply stores",
			"Food/Potion stores",
			"Weapons stores",
			"Armour stores",
			"Combat stores",
			"Magic stores",
			"Range stores",
			"Outfits stores",
			"Daily task stores",
			"Pkp stores",
			"Jossik's god book stores",
			"Slayer points stores",
			"Boss points stores",
			"Donator point stores",
			"Holiday stores",
			"Afk point stores",
			"Prestige point stores",
			"Item exchange stores",
			"Ironman stores",
			"---------------------------------------",
			"Kratos chest",
			"Nightmare chest",
			"Solak chest",
			"Glod Chest",
			"Seren chest",
			"Loyalty chest",
			"Slayer chest",
			"Crystal chest",
			"Hunllef chest",
			"Raids reward chest",
			"",
			"",
			"",
			"",
			"",
			"",
			
	};

	public CreditTab(Player player) {
		super(player);
	}

	@Override
	protected String[] text() {
		return text;
	}

	@Override
	protected int startingLine() {
		return 44531;
	}

}

