package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.content.music.MusicNameIndex;
import io.xeros.model.entity.player.Player;

/**
 * ::music <id|name|stop|auto>
 * - id: index3 file id
 * - name: "harmony", "wilderness2", etc (looked up via Data/music/song_id_to_name.json)
 */
public class Music extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        if (input == null) input = "";
        input = input.trim();

        if (input.isEmpty()) {
            c.sendMessage("Usage: ::music <id|name|stop|auto>");
            return;
        }

        if (input.equalsIgnoreCase("stop")) {
            c.forcedMusicTrack = 0; // 0 is usually safe; if you want true stop, set volume=0 client-side
            c.getPA().sendMusic(0);
            c.sendMessage("Music stopped.");
            return;
        }

        if (input.equalsIgnoreCase("auto")) {
            c.forcedMusicTrack = -1;
            c.getPA().updateRegionalMusic();
            c.sendMessage("Music set to auto/regional.");
            return;
        }

        Integer songId = null;

        // numeric?
        try {
            songId = Integer.parseInt(input);
        } catch (NumberFormatException ignored) { }

        // name lookup
        if (songId == null) {
            songId = MusicNameIndex.findSongIdByName(input);
        }

        if (songId == null) {
            c.sendMessage("Unknown song: " + input + " (check Data/music/song_id_to_name.json)");
            return;
        }

        c.forcedMusicTrack = songId;
        c.getPA().sendMusic(songId);
        c.sendMessage("Playing songId=" + songId + " (" + MusicNameIndex.getSongName(songId) + ")");
    }
}
