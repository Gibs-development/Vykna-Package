package com.client.ui.panel;

import com.client.Client;
import com.client.DrawingArea;

import java.awt.Rectangle;

public class ActionBarPanel extends PanelManager.BasePanel {
	private static final int SLOT_SIZE = 30;
	private static final int SLOT_GAP = 4;
	private static final int SLOT_COUNT = 12;

	public ActionBarPanel(int id, Rectangle bounds) {
		super(id, bounds, true, true, "Action Bar", true, 360, 80 + PanelManager.PANEL_HEADER_HEIGHT, false);
	}

	@Override
	public void draw(Client client) {
		updateCooldown();
		Rectangle bounds = getBounds();
		int headerHeight = PanelManager.getPanelHeaderHeight(client, this);
		int contentX = bounds.x + 6;
		int contentY = bounds.y + headerHeight + 6;
		int contentWidth = bounds.width - 12;
		drawSlots(contentX, contentY, contentWidth);
	}

	@Override
	public boolean handleMouse(Client client, int mouseX, int mouseY) {
		return false;
	}

	@Override
	public boolean handleClick(Client client, int mouseX, int mouseY) {
		return false;
	}

	private void drawSlots(int x, int y, int contentWidth) {
		int totalWidth = SLOT_COUNT * SLOT_SIZE + (SLOT_COUNT - 1) * SLOT_GAP;
		int startX = x + Math.max(0, (contentWidth - totalWidth) / 2);
		for (int slot = 0; slot < SLOT_COUNT; slot++) {
			int slotX = startX + slot * (SLOT_SIZE + SLOT_GAP);
			drawSlot(slotX, y);
		}
	}

	private void drawSlot(int x, int y) {
		DrawingArea.drawPixels(SLOT_SIZE, y, x, 0x1b1b1b, SLOT_SIZE);
		DrawingArea.drawPixels(1, y, x, 0x3b3f45, SLOT_SIZE);
		DrawingArea.drawPixels(1, y + SLOT_SIZE - 1, x, 0x0f1113, SLOT_SIZE);
		DrawingArea.drawPixels(SLOT_SIZE, y, x, 0x2d3136, 1);
		DrawingArea.drawPixels(SLOT_SIZE, y, x + SLOT_SIZE - 1, 0x24272b, 1);
	}
}
