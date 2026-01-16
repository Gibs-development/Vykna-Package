package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

/**
 * Manual music control.
 *
 * ::music <id>
 * ::music stop
 * ::music auto
 */
public class Music extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        String in = (input == null) ? "" : input.trim();
        if (in.isEmpty()) {
            player.sendMessage("Usage: ::music <id> | ::music stop | ::music auto");
            return;
        }

        if (in.equalsIgnoreCase("stop")) {
            player.regionalMusicAuto = false;
            player.lastMusicTrack = Integer.MIN_VALUE;
            player.getPA().stopMusic();
            player.sendMessage("Music stopped.");
            return;
        }

        if (in.equalsIgnoreCase("auto")) {
            player.regionalMusicAuto = true;
            player.lastMusicTrack = Integer.MIN_VALUE; // force re-evaluate
            player.getPA().updateRegionalMusic();
            player.sendMessage("Regional music enabled.");
            return;
        }

        try {
            int id = Integer.parseInt(in);
            if (id < 0 || id > 65535) {
                player.sendMessage("Invalid track id: " + id);
                return;
            }
            player.regionalMusicAuto = false;
            player.lastMusicTrack = id;
            player.getPA().sendMusic(id);
            player.sendMessage("Playing track: " + id);
        } catch (NumberFormatException e) {
            player.sendMessage("Usage: ::music <id> | ::music stop | ::music auto");
        }
    }
}
