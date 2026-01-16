package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.content.music.MusicNameIndex;
import io.xeros.content.music.RegionMusicById;
import io.xeros.content.music.RegionalMusic;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * ::where
 * Prints x,y,z, regionId, wild level, instance, and any matching Boundary constants.
 */
public class Where extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        int regionId = RegionalMusic.getRegionId(c);
        c.sendMessage("Pos: x=" + c.absX + " y=" + c.absY + " h=" + c.heightLevel
                + " | regionId=" + regionId
                + " | wild=" + c.wildLevel
                + " | mapRegion=(" + c.mapRegionX + "," + c.mapRegionY + ")"
        );

        c.sendMessage("Instance: " + (c.getInstance() == null ? "none" : c.getInstance().getClass().getSimpleName()));

        // Region -> song mapping info
        RegionMusicById.loadOnce();
        MusicNameIndex.loadOnce();

        Integer mappedSong = RegionMusicById.getSongIdForRegion(regionId);
        if (mappedSong == null) {
            c.sendMessage("RegionMusic: no mapping for regionId=" + regionId);
        } else {
            String name = MusicNameIndex.getSongName(mappedSong);
            c.sendMessage("RegionMusic: mapped songId=" + mappedSong + (name == null ? "" : (" (" + name + ")")));
        }

        // Boundary matches (reflection)
        List<String> matches = new ArrayList<>();
        try {
            for (Field f : Boundary.class.getDeclaredFields()) {
                if (!Modifier.isStatic(f.getModifiers())) continue;
                if (!Boundary.class.equals(f.getType())) continue;
                f.setAccessible(true);
                Boundary b = (Boundary) f.get(null);
                if (b != null && Boundary.isIn(c, b)) {
                    matches.add(f.getName());
                }
            }
        } catch (Exception e) {
            c.sendMessage("Boundary scan failed: " + e.getMessage());
        }

        if (matches.isEmpty()) {
            c.sendMessage("Boundary matches: none");
        } else {
            c.sendMessage("Boundary matches (" + matches.size() + "): " + String.join(", ", matches));
        }
    }
}
