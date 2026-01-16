package io.xeros.content.boosts.other;

import io.xeros.content.wogw.Wogw;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

public class WogwDropBoost extends GenericBoost {
    @Override
    public String getDescription() {
        return "+20% Drop Rate (" + Misc.cyclesToDottedTime((int) Wogw.DOUBLE_DROPS_TIMER) + ")";
    }

    @Override
    public boolean applied(Player player) {
        return Wogw.DOUBLE_DROPS_TIMER > 0;
    }
}
