package io.xeros.content.minigames.raids;

import io.xeros.achievements.AchievementHandler;
import io.xeros.achievements.AchievementList;
import io.xeros.content.party.PartyInterface;
import io.xeros.model.entity.player.Player;
import io.xeros.content.party.PlayerParty;

public class CoxParty extends PlayerParty {

    public static final String TYPE = "CoX Party";

    public CoxParty() {
        super(TYPE, 100);
    }

    @Override
    public boolean canJoin(Player invitedBy, Player invited) {
        if (Raids.isMissingRequirements(invited)) {
            invitedBy.sendMessage("That player doesn't have the requirements to play Chambers of Xeric.");
            return false;
        }

        return true;
    }

    @Override
    public void onJoin(Player player) {
        PartyInterface.refreshOnJoinOrLeave(player, this);
        AchievementHandler.activate(player, AchievementList.CREATE_A_RAID_PARTY, 1);//NEW ACHIEVEMNTS
    }

    @Override
    public void onLeave(Player player) {
        PartyInterface.refreshOnJoinOrLeave(player, this);
    }
}
