package io.xeros.achievements;

import io.xeros.model.entity.player.Player;

/**
 * Handles the interfaces
 * 
 * @author C.T
 *
 */
public abstract class InterfaceHandler {

	public InterfaceHandler(Player player) {
		this.player = player;
	}

	protected Player player;

	protected abstract String[] text();

	protected abstract int startingLine();

	public static void writeText(InterfaceHandler interfacetext) {
		int line = interfacetext.startingLine();
		for (int i1 = 0; i1 < interfacetext.text().length; i1++) {
			interfacetext.player.getPA().sendString(interfacetext.text()[i1], line++);
		}
	}
}
