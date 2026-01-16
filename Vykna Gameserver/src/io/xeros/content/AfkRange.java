package io.xeros.content;


import io.xeros.content.skills.Skill;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;


/**
 * Afk Range.
 *
 * @author C.T for RuneRogue
 *
 * Handles the cooking of noted food.
 *
 */


public class AfkRange {

	public enum Food {
		SHRIMP(318, 316, 1, 5),//
		ANCHOVIES(322,320, 1, 5),//
		SARDINE(328,326, 1, 5),
		HERRING(346,348, 5, 5),
		MACKAREL(354,356, 10, 7),
		TROUT(336,334, 15, 10),
		COD(342,340, 18, 10),
		PIKE(350,352, 30, 12),
		SALMON(332,330, 25, 15),
		TUNA(360,362, 30, 20),
		KARAWBAN(3143,3145, 30, 30),
		LOBSTER(378,380, 40, 33),
		BASS(364,366, 43, 33),
		SWORDFISH(372,374, 45, 40),
		MONKFISH(7945,7947, 62, 50),
		SHARK(384,386, 80, 65),
		SEATURTLE(396,398, 82, 70),
		ANGLERFISH(13440,13442, 84, 90),
		DARKCRAB(11935,11937, 90, 95),
		MANTARAY(390,392, 91, 150);

		private int logID;

		private int newID;

		private int level;

		private int xp;

		Food(int logID, int newID, int level, int xp) {
			this.logID = logID;
			this.newID = newID;
			this.level = level;
			this.xp = xp;
		}

		public int getLogID() {
			return logID;
		}

		public int getNewID() {
			return newID;
		}

		public int getLevel() {
			return level;
		}

		public int getXp() {
			return xp;
		}

	}

	public static void startCooking(final Player player, final int itemId) {
		for (final Food g : Food.values()) {
			if (itemId == g.getLogID()) {
				if (player.getLevel(Skill.COOKING) < 60) {
					player.sendStatement("You need a Cooking level of 60 use this range.");
					return;
				}

				if (player.getLevel(Skill.COOKING) < g.getLevel()) {
					player.sendStatement("You need a Cooking level of " + g.getLevel() + " to cook " + ItemAssistant.getItemName(g.getLogID()) + ".");
					return;
				}
				if (!ItemAssistant.hasItemInInventory(player, g.getLogID())) {
					return;
				}
				if (ItemAssistant.hasItemAmountInInventory(player, itemId, 1)) {
					bonfireCycle(player, itemId);
					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							if (!Boundary.isIn(player, Boundary.AFK_COOKING_AREA)) {
								container.stop();
								return;
							}
							if (ItemAssistant.hasItemInInventory(player, itemId)) {
								bonfireCycle(player, itemId);
							} else {
								container.stop();
							}
						}

						@Override
						public void onStopped() {
							//Skilling.endSkillingEvent(player);
						}
					}, 4);
					return;
				}
			}
		}
	}

	public static void bonfireCycle(final Player player, int itemId) {
		for (final Food g : Food.values()) {
			if (itemId == g.getLogID()) {
				if (ItemAssistant.hasItemInInventory(player, itemId)) {
					player.getItems().deleteItem(itemId, 1);
					player.getItems().addItem(g.newID, 1);
					player.startAnimation(645);
					player.sendMessage("You add some " + ItemAssistant.getItemName(g.getLogID()) + " to the range.");
					player.getPA().addSkillXP((g.getXp()), Player.playerCooking, true);
					if (Misc.random(12) == 1) {
						player.afkPoints++;
						player.sendMessage("@blu@You managed to cook the " + ItemAssistant.getItemName(g.getLogID()) + " into some @red@Afk Points.");
					}
				}
			}
		}
	}
}
