package io.xeros.content.cutscene;

import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.lock.PlayerLock;

/**
 * Blocks interaction during cutscenes.
 * Your walking packet already checks player.getLock().cannotInteract(player).
 */
public final class CutsceneLock implements PlayerLock {
    @Override public boolean cannotLogout(Player p) { return true; }
    @Override public boolean cannotInteract(Player p) { return true; }
    @Override public boolean cannotClickItem(Player p, int itemId) { return true; }
    @Override public boolean cannotTeleport(Player p) { return true; }
}
