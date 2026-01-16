package io.xeros.content;

import io.xeros.model.entity.player.Player;

import java.util.HashMap;

/**
 *
 * @author C.T for RuneRogue
 *
 */

public class SigilSystem {

	public enum sigilSystem {
		TIER_1("Tier 1\\nPerks:", 24565, 1),
		TIER_2("Tier 2\\nPerks:", 24567, 1),
		TIER_3("Tier 3\\nPerks:", 24569, 1),
		TIER_4("Tier 4\\nPerks:", 24571, 1),
		TIER_5("Tier 5\\nPerks:", 24573, 1),
		TIER_6("Tier 6\\nPerks", 24575, 1),
		TIER_7("Tier 7\\nPerks", 24577, 1),
		TIER_8("Tier 8\\nPerks", 24579, 1),
		TIER_9("Tier 9\\nPerks", 24581, 1),
		TIER_10("Tier 10\\nPerks", 24583, 1),
		TIER_11("Tier 11\\nPerks", 4141, 1),
		TIER_12("Tier 12\\nPerks", 4141, 1),
		TIER_13("Tier 13\\nPerks", 4141, 1),
		TIER_14("Tier 14\\nPerks", 4141, 1),
		;
		
		private final String name;
		private final int item;
		private final int amount;
		sigilSystem(String name, int item, int amount) {
			this.name = name;
			this.item = item;
			this.amount = amount;
		}
		
		public String getName() {
			return name;
		}
		
		public int getItem() {
			return item;
		}
		
		public int getAmount() {
			return amount;
		}
		
		private static HashMap<Integer, sigilSystem> lists = new HashMap<>();

		static {
			for (sigilSystem table : sigilSystem.values()) {
				sigilSystem.lists.put(table.item, table);
			}
		}
	}

	public static void openInterface(Player player) {
		int slot = 0;
		for (int i = 0; i < 145; i++) {
			player.getPA().sendString("", 60562 + i);
		}
		int index = 0;
		for (sigilSystem data : sigilSystem.values()) {
			player.getPA().sendString(data.getName(), 60508 + index);
			player.getPA().itemOnInterface(data.getItem(), data.getAmount(), 60507 + index, slot);
			index += 4;
		}
		player.getPA().showInterface(60500);
	}

}
