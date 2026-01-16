package io.xeros.content.bosses;

import io.xeros.content.instances.InstanceConfiguration;
import io.xeros.content.instances.impl.LegacySoloPlayerInstance;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

/**
 * 
 * @author C.T for koranes
 *
 */
public class VoteBossInstance extends LegacySoloPlayerInstance {

	public VoteBossInstance(Player player, Boundary boundary) {
		super(InstanceConfiguration.CLOSE_ON_EMPTY_RESPAWN, player, boundary);
	}

	@Override
	public void onDispose() { }
}
