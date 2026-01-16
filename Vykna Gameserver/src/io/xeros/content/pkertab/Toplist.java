package io.xeros.content.pkertab;

import io.xeros.Configuration;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Pvp toplist
 *
 *
 * @author C.T for runerogue
 */
public class Toplist {

    /**
     * The frame start (interface id)
     */
    private static final int FRAME_START = 17802;

    /**
     * The maximum size of the toplist
     */
    private static final int TOPLIST_SIZE = 10;

    /**
     * The toplist
     */
    private static String[] toplist = new String[TOPLIST_SIZE];

    /**
     * Fetches a new toplist and sends it to
     * online players.
     */
    public static void updateToplist() {
        List<Player> topList = new ArrayList<>();

        for (int i = 0; i < Configuration.MAX_PLAYERS; i++) {
            if (PlayerHandler.players[i] != null) {
                if(PlayerHandler.players[i].killStreak > 0) {
                    topList.add(PlayerHandler.players[i]);
                }
            }
        }

        topList.sort((player1, player2) -> (player1.killStreak > player2.killStreak) ? -1 : 0);

        if(topList.size() > TOPLIST_SIZE) {
            topList = topList.subList(0, TOPLIST_SIZE);
        }

        for(int i = 0; i < TOPLIST_SIZE; i++) {
            if(i < topList.size()) {
                Player p = topList.get(i);

                //Set the entry
                String entry = "@or1@"+p.getLoginName()+" @whi@has @yel@"+p.killStreak+"@whi@ killstreak.";
                toplist[i] = entry;
            } else {
                toplist[i] = null;
            }
        }

        for (int i = 0; i < Configuration.MAX_PLAYERS; i++) {
            if (PlayerHandler.players[i] != null) {
                sendToplist(PlayerHandler.players[i]);
            }
        }

        System.out.println();
    }

    /**
     * Sends the current toplist entries to the specified player.
     * @param p		The player to send the toplist to.
     */
    public static void sendToplist(Player p) {
        for(int i = 0; i < TOPLIST_SIZE; i++) {
            String entry = "";
            if(toplist[i] != null) {
                entry = toplist[i];
            }
            p.getPA().sendFrame126(entry, FRAME_START + i);
        }
    }
}

