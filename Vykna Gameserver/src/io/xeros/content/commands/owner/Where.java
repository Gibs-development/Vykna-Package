package io.xeros.content.commands.owner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.xeros.content.commands.Command;
import io.xeros.content.music.RegionalMusic;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

/**
 * Debug command for mapping regional music.
 *
 * ::where
 */
public class Where extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        int x = player.absX;
        int y = player.absY;
        int h = player.heightLevel;
        int regionId = ((x >> 6) << 8) | (y >> 6);

        player.sendMessage("Pos: x=" + x + " y=" + y + " h=" + h
                + " | regionId=" + regionId + " | wildLevel=" + player.wildLevel);

        if (player.getInstance() != null) {
            player.sendMessage("Instance: " + player.getInstance().getClass().getSimpleName());
        }

        List<String> matches = findBoundaryMatches(player);
        if (matches.isEmpty()) {
            player.sendMessage("Boundary matches: (none)");
        } else {
            int shown = Math.min(matches.size(), 12);
            player.sendMessage("Boundary matches (" + matches.size() + "): " + String.join(", ", matches.subList(0, shown))
                    + (matches.size() > shown ? " ..." : ""));
        }

      //  player.sendMessage("RegionalMusic: " + RegionalMusic.debugMatch(player));
    }

    private static List<String> findBoundaryMatches(Player player) {
        List<String> out = new ArrayList<>();
        try {
            for (Field f : Boundary.class.getDeclaredFields()) {
                if (!java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                f.setAccessible(true);

                if (f.getType() == Boundary.class) {
                    Boundary b = (Boundary) f.get(null);
                    if (b != null && Boundary.isIn(player, b)) {
                        out.add(f.getName());
                    }
                } else if (f.getType() == Boundary[].class) {
                    Boundary[] arr = (Boundary[]) f.get(null);
                    if (arr == null) continue;
                    for (int i = 0; i < arr.length; i++) {
                        Boundary b = arr[i];
                        if (b != null && Boundary.isIn(player, b)) {
                            out.add(f.getName() + "[" + i + "]");
                        }
                    }
                }
            }
        } catch (Exception e) {
            player.sendMessage("Boundary reflection failed: " + e.getClass().getSimpleName());
        }
        return out;
    }
}
