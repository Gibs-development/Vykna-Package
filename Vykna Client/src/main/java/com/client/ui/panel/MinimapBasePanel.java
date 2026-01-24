package com.client.ui.panel;

import com.client.Client;
import com.client.DrawingArea;

import java.awt.Rectangle;

public class MinimapBasePanel extends PanelManager.BasePanel {
	public MinimapBasePanel(int id, Rectangle bounds) {
		super(id, bounds, true, true, "Minimap", true, 160, 160, false);
	}

	@Override
	public void draw(Client client) {
		Rectangle bounds = getBounds();
		int headerHeight = PanelManager.getPanelHeaderHeight(client, this);
		int clipLeft = DrawingArea.topX;
		int clipTop = DrawingArea.topY;
		int clipRight = DrawingArea.bottomX;
		int clipBottom = DrawingArea.bottomY;

		DrawingArea.setDrawingArea(bounds.y + bounds.height, bounds.x, bounds.x + bounds.width, bounds.y + headerHeight);
		client.drawMinimapAt(bounds.x, bounds.y + headerHeight, bounds.width, bounds.height - headerHeight);
		if (!client.isRs3EditModeActive()) {
			drawFrame(bounds, headerHeight);
		}
		DrawingArea.setDrawingArea(clipBottom, clipLeft, clipRight, clipTop);
	}

	private void drawFrame(Rectangle bounds, int headerHeight) {
		int frameX = bounds.x;
		int frameY = bounds.y + headerHeight;
		int frameWidth = bounds.width;
		int frameHeight = Math.max(0, bounds.height - headerHeight);
		if (frameWidth <= 0 || frameHeight <= 0) {
			return;
		}
		int color = 0x2c2c2c;
		DrawingArea.drawPixels(1, frameY, frameX, color, frameWidth);
		DrawingArea.drawPixels(1, frameY + frameHeight - 1, frameX, color, frameWidth);
		DrawingArea.drawPixels(frameHeight, frameY, frameX, color, 1);
		DrawingArea.drawPixels(frameHeight, frameY, frameX + frameWidth - 1, color, 1);
	}

	@Override
	public boolean handleMouse(Client client, int mouseX, int mouseY) {
		Rectangle bounds = getBounds();
		int headerHeight = PanelManager.getPanelHeaderHeight(client, this);
		int absoluteX = bounds.x + mouseX;
		int absoluteY = bounds.y + mouseY;
		client.updateRs3MinimapHovers(absoluteX, absoluteY, bounds.x, bounds.y + headerHeight, bounds.width, bounds.height - headerHeight);
		client.processRs3MinimapActions(absoluteX, absoluteY, bounds.x, bounds.y + headerHeight, bounds.width, bounds.height - headerHeight);
		return true;
	}

	@Override
	public boolean handleClick(Client client, int mouseX, int mouseY) {
		Rectangle bounds = getBounds();
		int headerHeight = PanelManager.getPanelHeaderHeight(client, this);
		int absoluteX = bounds.x + mouseX;
		int absoluteY = bounds.y + mouseY;
		client.processRs3MinimapClick(absoluteX, absoluteY, bounds.x, bounds.y + headerHeight, bounds.width, bounds.height - headerHeight);
		return true;
	}

	@Override
	public boolean isClosable() {
		return false;
	}

	@Override
	public boolean drawsBackground() {
		return false;
	}
}
