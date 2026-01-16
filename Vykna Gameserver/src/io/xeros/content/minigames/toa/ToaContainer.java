package io.xeros.content.minigames.toa;

import io.xeros.content.minigames.toa.instance.ToaInstance;
import io.xeros.content.minigames.toa.party.ToaParty;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

import java.util.List;

/**
 * Handles actions outside of toa instance.
 */
public class ToaContainer {

    private final Player player;

    public ToaContainer(Player player) {
        this.player = player;
    }

    public void displayRewardInterface(List<GameItem> rewards) {
        player.getItems().sendItemContainer(22961, rewards);
        player.getPA().showInterface(22959);
    }

    public boolean handleClickObject(WorldObject object, int option) {
        if (object.getId() != ToaConstants.ENTER_TOA_OBJECT_ID)
            return false;

        startToa();
        return true;
    }

    public void startToa() {
        if (!player.inParty(ToaParty.TYPE)) {
            player.sendMessage("You must be in a party to start Tombs of Amascut.");
            return;
        }

        if (player.getPA().calculateTotalLevel() < player.getMode().getTotalLevelForToa()) {
            player.sendStatement("You need " + Misc.insertCommas(player.getMode().getTotalLevelForToa()) + " total level to compete.");
            return;
        }

        player.getParty().openStartActivityDialogue(player, "Tombs of amascut", ToaConstants.TOA_LOBBY::in, list -> new ToaInstance(list.size()).start(list));
    }

    public boolean handleContainerAction1(int interfaceId, int slot) {
        if (inToa()) {
            return ((ToaInstance) player.getInstance()).getFoodRewards().handleBuy(player, interfaceId, slot);
        }
        return false;
    }

    public boolean inToa() {
        return player.getInstance() != null && player.getInstance() instanceof ToaInstance;
    }

}
