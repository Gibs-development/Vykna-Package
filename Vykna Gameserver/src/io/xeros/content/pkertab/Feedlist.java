package io.xeros.content.pkertab;


import io.xeros.Configuration;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

/**
 *
 * Feed list
 *
 *
 * @author C.T for runerogue
 */

public class Feedlist {

    /**
     * The max amount of entries in the feed.
     */
    private static final int MAX_ENTRIES = 10;

    /**
     * The start of the string interface ids.
     */
    private static final int FRAME_START = 17814;

    /**
     * Our list of entries.
     */
    private static final String[] entries = new String[MAX_ENTRIES];

    /**
     * Adds an entry to the list.
     * @param entry		The entry to add.
     */
    public static void submit(final String entry) {

        //First we shift our other entries...
        for(int i = MAX_ENTRIES - 1; i > 0; i--) {
            entries[i] = entries[i-1];
        }

        //Then we set our new entry the first one..
        entries[0] = entry;

        for (int i = 0; i < Configuration.MAX_PLAYERS; i++) {
            if (PlayerHandler.players[i] != null) {
                sendEntries(PlayerHandler.players[i]);
            }
        }
    }

    /**
     * Clears all entries.
     */
    public static void clear() {

        //Reset entries
        for(int i = 0; i < MAX_ENTRIES; i++) {
            entries[i] = null;
        }

        for (int i = 0; i < Configuration.MAX_PLAYERS; i++) {
            if (PlayerHandler.players[i] != null) {
                sendEntries(PlayerHandler.players[i]);
            }
        }
    }

    /**
     * Sends all current entries to the specified player.
     * @param p		The player to send entries to.
     */
    public static void sendEntries(Player p) {

        //Send new strings
        for(int i = 0; i < MAX_ENTRIES; i++) {

            //Fetch current entry
            String entry = "";
            if(entries[i] != null) {
                entry = entries[i];
            }

            //Send the entry
            p.getPA().sendFrame126(entry,FRAME_START + i);
        }
    }

}

