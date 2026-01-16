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
 * Afk Furnace.
 *
 * @author C.T for RuneRogue
 *
 * Handles the smelting of noted bars.
 *
 */


public class AfkFurnace {

	public enum Bars {
		COPPER_TIN(437, 439,2350, 1, 15),//
	//	IRON_ORE(441,00,2352, 15, 25),//
		IRON_COAL(441,454,2354, 30, 30),
	//	GOLD(445,00,2358, 40, 35),
		MITHRIL_COAL(448,454,2360, 45, 40),

		ADAMANT(450,454,2362, 70, 50),
		RUNE(452,454,2364, 85, 150);

		private int logID;

		private int newID;

		private int idTWO;

		private int level;

		private int xp;

		Bars(int logID, int idTWO, int newID, int level, int xp) {
			this.logID = logID;
			this.newID = newID;
			this.idTWO = idTWO;
			this.level = level;
			this.xp = xp;
		}

		public int getLogID() {
			return logID;
		}

		public int getNewID() {
			return newID;
		}

		public int getIdTWO() {
			return idTWO;
		}

		public int getLevel() {
			return level;
		}

		public int getXp() {
			return xp;
		}

	}

	public static void startMelting(final Player player, final int itemId) {
		for (final Bars g : Bars.values()) {
			if (itemId == g.getLogID()) {
				if (player.getLevel(Skill.SMITHING) < 60) {
					player.sendStatement("You need a Smithing level of 60 use this furnace.");
					return;
				}

				if (player.getLevel(Skill.SMITHING) < g.getLevel()) {
					player.sendStatement("You need a Smithing level of " + g.getLevel() + " to smelt " + ItemAssistant.getItemName(g.getLogID()) + ".");
					return;
				}
				if (!ItemAssistant.hasItemInInventory(player, g.getLogID())) {
					player.sendStatement("You have run out of ores.");
					return;
				}
				if (!ItemAssistant.hasItemInInventory(player, g.getIdTWO())) {
					player.sendStatement("You have run out of ores.");
					return;
				}
				if (ItemAssistant.hasItemAmountInInventory(player, itemId, 1)) {
					if (ItemAssistant.hasItemAmountInInventory(player, g.getIdTWO(), 1))
					bonfireCycle(player, itemId);
					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							if (!Boundary.isIn(player, Boundary.AFK_FURNACE_AREA)) {
								container.stop();
								return;
							}
							if (ItemAssistant.hasItemInInventory(player, itemId)) {
							if (ItemAssistant.hasItemInInventory(player, g.getIdTWO()))
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
		for (final Bars g : Bars.values()) {
			if (itemId == g.getLogID()) {
				if (ItemAssistant.hasItemInInventory(player, itemId) || ItemAssistant.hasItemInInventory(player, g.getIdTWO())) {
					player.getItems().deleteItem(itemId, 1);
					player.getItems().deleteItem(g.idTWO, 1);
					player.getItems().addItem(g.newID, 1);
					player.startAnimation(645);
					player.sendMessage("You add some " + ItemAssistant.getItemName(g.getLogID()) + " & " + ItemAssistant.getItemName(g.getIdTWO()) + " to the furnace.");
					player.getPA().addSkillXP((g.getXp()), Player.playerSmithing, true);
					if (Misc.random(11) == 1) {
						player.afkPoints++;
						player.sendMessage("@blu@You managed to smelt the " + ItemAssistant.getItemName(g.getLogID()) + " & " + ItemAssistant.getItemName(g.getIdTWO()) + " into some @red@Afk Points.");
					}
				}
			}
		}
	}
}
