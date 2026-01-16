package io.xeros.content;


import io.xeros.content.skills.Skill;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;


/**
 * Bonfire.
 *
 * @author C.T for RuneRogue
 *
 * Handles the burning of noted logs.
 *
 */


public class Bonfire {

	public enum Logs {
		REGULAR(1512, 1, 20),
		BLUE(7406, 1, 20),
		GREEN(7405, 1, 20),
		PURPLE(10329, 1, 20),
		WHITE(10328, 1, 20),
		RED(7404, 1, 20),
		ACHEY(2862, 1, 20),
		OAK(1522, 15, 30),
		WILLOW(1520, 30, 45),
		TEAK(6334, 35, 55),
		ARCTIC_PINE(10811, 42, 65),
		MAPLE(1518, 45, 75),
		MAHOGANY(6332, 50, 90),
		YEW(1516, 60, 104),
		MAGIC(1514, 75, 150),
		REDWOOD(19670, 90, 175);

		private int logID;

		private int level;

		private int xp;

		Logs(int logID, int level, int xp) {
			this.logID = logID;
			this.level = level;
			this.xp = xp;
		}

		public int getLogID() {
			return logID;
		}

		public int getLevel() {
			return level;
		}

		public int getXp() {
			return xp;
		}

	}

	public static void startBurning(final Player player, final int itemId) {
		for (final Logs g : Logs.values()) {
			if (itemId == g.getLogID()) {


				if (player.getLevel(Skill.FIREMAKING) < g.getLevel()) {
					player.sendStatement("You need a Firemaking level of " + g.getLevel() + " to burn " + ItemAssistant.getItemName(g.getLogID()) + ".");
					return;
				}
				if (!ItemAssistant.hasItemInInventory(player, g.getLogID())) {
					return;
				}
				if (!player.getRights().isOrInherits(Right.REGULAR_DONATOR)) {
					player.sendStatement("You need to be an regular donator to use this feature.");
					return;
				}
				if (ItemAssistant.hasItemAmountInInventory(player, itemId, 1)) {
					bonfireCycle(player, itemId);
					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							if (!Boundary.isIn(player, Boundary.BONFIRE_AREA)) {
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
		for (final Logs g : Logs.values()) {
			if (itemId == g.getLogID()) {
				if (ItemAssistant.hasItemInInventory(player, itemId)) {
					player.getItems().deleteItem(itemId, 1);
					player.startAnimation(645);
					player.sendMessage("You add some " + ItemAssistant.getItemName(g.getLogID()) + " to the bonfire.");
					player.getPA().addSkillXP((g.getXp()), Player.playerFiremaking, true);
					if (Misc.random(9) == 1) {
						player.afkPoints++;
						player.sendMessage("@blu@You managed to burn the logs into some @red@Afk Points.");
					}
				}
			}
		}
	}
}
