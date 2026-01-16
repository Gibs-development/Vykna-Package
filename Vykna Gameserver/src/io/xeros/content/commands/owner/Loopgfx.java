package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;

public class Loopgfx extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			int gfxid = 0;
			@Override
			public void execute(CycleEventContainer container) {
				if (player.isDisconnected()) {
					container.stop();
					return;
				}
				player.gfx0(gfxid);
				player.sendMessage("GfxID: " + gfxid);
				gfxid ++;
				if(gfxid > 2096) {
					container.stop();
				}
			}
			@Override
			public void onStopped() {

			}
		}, 3);
	}
}