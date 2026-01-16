package io.xeros.content.cutscene;

import io.xeros.model.entity.player.Player;

public final class CutsceneTiles {
    private CutsceneTiles() {}

    /**
     * Robust base computation:
     * abs = base + local  =>  base = abs - local
     * This avoids any ambiguity around mapRegionX/mapRegionY being pre-offset.
     */
    public static int baseX(Player p) { return p.getX() - p.currentX; }
    public static int baseY(Player p) { return p.getY() - p.currentY; }

    public static int localX(Player p, int absX) { return absX - baseX(p); }
    public static int localY(Player p, int absY) { return absY - baseY(p); }
}
