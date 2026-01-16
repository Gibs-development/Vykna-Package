package io.xeros.content.minigames.toa.party;

import io.xeros.content.minigames.toa.ToaConstants;
import io.xeros.content.party.PartyFormAreaController;
import io.xeros.content.party.PlayerParty;
import io.xeros.model.entity.player.Boundary;

import java.util.Set;

public class ToaPartyFormAreaController extends PartyFormAreaController {

    @Override
    public String getKey() {
        return ToaParty.TYPE;
    }

    @Override
    public Set<Boundary> getBoundaries() {
        return Set.of(ToaConstants.TOA_LOBBY);
    }

    @Override
    public PlayerParty createParty() {
        return new ToaParty();
    }
}
