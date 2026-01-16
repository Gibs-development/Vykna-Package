package io.xeros.content.dailytasks;

import io.xeros.model.entity.player.Player;

/**
 *
 * @author C.T for runerogue
 * Handles the daily tasks kills system
 *
 */

public class DailyTaskKills {

	public static void kills(Player player, int npcId) {
		switch (npcId) {
		case 7286: //skot
			DailyTasks.increase(player, DailyTasks.PossibleTasks.SKOTIZO);
			break;
		case 259: // black drag
			DailyTasks.increase(player, DailyTasks.PossibleTasks.BLACK_DRAGONS);
			break;
		case 268: //blue drag
			DailyTasks.increase(player, DailyTasks.PossibleTasks.BLUE_DRAGONS);
			break;
		case 415: //abyssal demon
			DailyTasks.increase(player, DailyTasks.PossibleTasks.ABYSSAL_DEMONS);
			break;
		case 4005: //dark beast
			DailyTasks.increase(player, DailyTasks.PossibleTasks.DARK_BEASTS);
			break;
		case 2215: //bandos
			DailyTasks.increase(player, DailyTasks.PossibleTasks.GENERAL_GRAARDOR);
			break;
		case 3162: //arma
			DailyTasks.increase(player, DailyTasks.PossibleTasks.KREE_ARRA);
			break;
		case 3129: //zamorak
			DailyTasks.increase(player, DailyTasks.PossibleTasks.TSUTSAROTH);
			break;
		case 2205: //saradomin
			DailyTasks.increase(player, DailyTasks.PossibleTasks.ZILYANA);
			break;
		case 1432: //black demon
			DailyTasks.increase(player, DailyTasks.PossibleTasks.BLACK_DEMONS);
			break;
		case 273: //iron drag
			DailyTasks.increase(player, DailyTasks.PossibleTasks.IRON_DRAGONS);
			break;
		case 274: //steel drag
			DailyTasks.increase(player, DailyTasks.PossibleTasks.STEEL_DRAGONS);
			break;
		case 2919: //mith dragon
			DailyTasks.increase(player, DailyTasks.PossibleTasks.MITHRIL_DRAGONS);
			break;
		}
	}
}
