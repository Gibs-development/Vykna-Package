package io.xeros.content;

import java.util.HashMap;


import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ItemAssistant;

/**
 * Handles the opening of Tomes
 * Author C.T For RuneRogue
 */
public enum Tomes {
	FISHING(10, 7779, 15550),
	AGILITY(16, 7782, 22000),
	THIEVING(17, 7785, 12500),
	SLAYER(18, 7788, 48000),
	MINING(14, 7791, 14000),
	FIREMAKING(11, 7794, 17000),
	WOODUCTTING(8, 7797, 15000);
	

	private final int skillId, tomesId, xp;

	private Tomes(int skillId, int tomesId, int xp) {
		this.skillId = skillId;
		this.tomesId = tomesId;
		this.xp = xp;
	}
	
	public int getSkill() {
		return skillId;
	}
	
	private static HashMap<Integer, Tomes> tomes = new HashMap<Integer, Tomes>();

	static {
		for (final Tomes tomes : Tomes.values()) {
			Tomes.tomes.put(tomes.tomesId, tomes);
		}
	}

	/**
	 * Handles opening the tomes
	 */
	public static boolean openSet(Player player, int item) {
		/* Get the tomes */
		Tomes data = Tomes.tomes.get(item);
		
		/* If tomes is nulled; return */
		if (data == null) {
			return false;
		}
		
		/* Remove the tomes from inventory */
		player.getItems().deleteItem(data.tomesId, 1);

		/* Add the xp to the player */
		player.getPA().addSkillXP(data.xp, data.skillId, true);

		/* Send a message of a successful opening */
		player.sendMessage("@pur@You have been given some XP, Keep up the good work!");
		//Announcement.announce("@red@"+player.getLoginName()+" @dre@as been given @red@"+data.xp+" experience @dre@from an @red@" + ItemAssistant.getItemName(data.tomesId) + "!");
		return true;
	}
}