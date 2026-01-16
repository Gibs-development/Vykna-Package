package io.xeros.content;

import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.group.GroupIronmanGroup;
import io.xeros.model.entity.player.mode.group.GroupIronmanRepository;
import io.xeros.util.Misc;
import org.apache.commons.collections4.ListUtils;

import java.util.List;
import java.util.stream.Collectors;


/**
 * The Ironman interface.
 *
 * @author C.T for koranes
 *
 */


public class IronManTeamInterface {

	public static final int INTERFACE_ID = 62_000;

	public static final int RECENT_GROUPS_START = 62008;
	public static final int RECENT_GROUPS_END = 62012;
	public static final int LEADERBOARD_TEAM_NAME = 62401;
	public static final int LEADERBOARD_AVERAGE_COMBAT = 62501;
	public static final int LEADERBOARD_ONLINE_STATUS = 62601;
	public static final int LEADERBOARD_AVERAGE_TOTAL = 62701;
	public static final int LEADERBOARD_AVERAGE_XP = 62801;
	public static final int LEADERBOARD_TEAM_NAMES = 62901;


	/**
	 * Opens the interface for a player
	 * @param player
	 */
//	public static void openInterface(Player player) {





//		for (Player p : group.getOnline()) {
//		for (int i = 0; i < group.getOnline(); i++) {
//			player.getPA().sendFrame126( Misc.optimizeText(best.get(i).getTeamName()) , i + LEADERBOARD_TEAM_NAME);
//			player.getPA().sendFrame126( "Avg. Combat: "+best.get(i).getAverageCombatLevel() , i+LEADERBOARD_AVERAGE_COMBAT);
//			player.getPA().sendFrame126( best.get(i).getOnlineStatusText(), i+LEADERBOARD_ONLINE_STATUS);
//			player.getPA().sendFrame126( "Avg. Total Level: "+ best.get(i).getAverageTotalLevel(), i+LEADERBOARD_AVERAGE_TOTAL);
//			player.getPA().sendFrame126( "Avg. Total XP: "+ Misc.getValueWithoutRepresentationK(best.get(i).getAverageTotalXp()), i+LEADERBOARD_AVERAGE_XP);
//			player.getPA().sendFrame126( Misc.optimizeText(best.get(i).getMembersAsString()) , i + LEADERBOARD_TEAM_NAMES);
//		}

//		List<IronmanTeam> recent = IronmanTeamHandler.getLatestIronmanTeams(5);
//		for (int i = 0; i < recent.size(); i++) {
//			player.getPA().sendFrame126(Misc.optimizeText(recent.get(i).getTeamName()) + " - " + recent.get(i).getMembersAsString(),
//					i + RECENT_GROUPS_START);
//		}


//		player.getPA().showInterface(INTERFACE_ID);
//	}


}
