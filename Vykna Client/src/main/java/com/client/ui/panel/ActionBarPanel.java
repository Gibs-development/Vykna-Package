package com.client.ui.panel;

import com.client.Client;
import java.awt.Rectangle;

public class ActionBarPanel extends PanelManager.BasePanel {
	public ActionBarPanel(int id, Rectangle bounds) {
		super(id, bounds, true, true, "Action Bar", true, 360, 80 + PanelManager.PANEL_HEADER_HEIGHT, false);
	}

	@Override
	public void draw(Client client) {
	}

	@Override
	public boolean handleMouse(Client client, int mouseX, int mouseY) {
		return false;
	}

	@Override
	public boolean handleClick(Client client, int mouseX, int mouseY) {
		return false;
	}

	@Override
	public boolean drawsBackground() {
		return false;
	}
}
