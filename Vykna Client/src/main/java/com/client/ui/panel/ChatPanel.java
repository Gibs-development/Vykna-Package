package com.client.ui.panel;

import com.client.Client;
import com.client.DrawingArea;

import java.awt.Rectangle;

public class ChatPanel extends PanelManager.BasePanel {
	private static final int HEADER_HEIGHT = 24;
	private static final int INPUT_HEIGHT = 22;
	private static final int PADDING = 4;

	public ChatPanel(int id, Rectangle bounds) {
		super(id, bounds, true, true, "Chat", true, 360, 180 + PanelManager.PANEL_HEADER_HEIGHT, false);
	}

	@Override
	public void draw(Client client) {
		Rectangle bounds = getBounds();
		int headerHeight = PanelManager.getPanelHeaderHeight(client, this);
		Layout layout = layout(bounds, headerHeight);
		client.setRs3ChatLayout(layout.headerRect, layout.messageRect, layout.inputRect);
		int clipLeft = DrawingArea.topX;
		int clipTop = DrawingArea.topY;
		int clipRight = DrawingArea.bottomX;
		int clipBottom = DrawingArea.bottomY;

		DrawingArea.setDrawingArea(bounds.y + bounds.height, bounds.x, bounds.x + bounds.width, bounds.y + headerHeight);
		client.drawChatAreaAt(bounds.x, bounds.y + headerHeight, bounds.width, bounds.height - headerHeight);
		drawChatFrame(layout);
		DrawingArea.setDrawingArea(clipBottom, clipLeft, clipRight, clipTop);
	}

	@Override
	public boolean handleMouse(Client client, int mouseX, int mouseY) {
		Rectangle bounds = getBounds();
		int headerHeight = PanelManager.getPanelHeaderHeight(client, this);
		Layout layout = layout(bounds, headerHeight);
		client.setRs3ChatLayout(layout.headerRect, layout.messageRect, layout.inputRect);
		int baseX = bounds.x;
		int baseY = bounds.y + headerHeight;
		int absoluteX = baseX + mouseX;
		int absoluteY = baseY + mouseY;
		client.processRs3ChatModeClick(absoluteX, absoluteY, absoluteX, absoluteY, false, baseX, baseY, bounds.width, bounds.height - headerHeight);
		client.updateChatScroll(absoluteX, absoluteY, baseX, baseY, bounds.width, bounds.height - headerHeight);
		return true;
	}

	@Override
	public boolean handleClick(Client client, int mouseX, int mouseY) {
		Rectangle bounds = getBounds();
		int headerHeight = PanelManager.getPanelHeaderHeight(client, this);
		Layout layout = layout(bounds, headerHeight);
		client.setRs3ChatLayout(layout.headerRect, layout.messageRect, layout.inputRect);
		int baseX = bounds.x;
		int baseY = bounds.y + headerHeight;
		int absoluteX = baseX + mouseX;
		int absoluteY = baseY + mouseY;
		client.processRs3ChatModeClick(absoluteX, absoluteY, absoluteX, absoluteY, true, baseX, baseY, bounds.width, bounds.height - headerHeight);
		client.updateChatScroll(absoluteX, absoluteY, baseX, baseY, bounds.width, bounds.height - headerHeight);
		return true;
	}

	@Override
	public boolean isClosable() {
		return false;
	}

	@Override
	public boolean isScrollable() {
		return false;
	}

	private Layout layout(Rectangle bounds, int headerHeight) {
		int areaX = bounds.x;
		int areaY = bounds.y + headerHeight;
		int areaWidth = bounds.width;
		int areaHeight = bounds.height - headerHeight;
		int innerWidth = Math.max(0, areaWidth - PADDING * 2);
		int innerHeight = Math.max(0, areaHeight - PADDING * 2);
		Rectangle headerRect = new Rectangle(areaX + PADDING, areaY + PADDING, innerWidth, HEADER_HEIGHT);
		Rectangle inputRect = new Rectangle(areaX + PADDING,
				areaY + areaHeight - INPUT_HEIGHT - PADDING,
				innerWidth,
				INPUT_HEIGHT);
		int messageTop = headerRect.y + headerRect.height + PADDING;
		int messageBottom = inputRect.y - PADDING;
		int messageHeight = Math.max(0, messageBottom - messageTop);
		Rectangle messageRect = new Rectangle(areaX + PADDING, messageTop, innerWidth, messageHeight);
		return new Layout(headerRect, messageRect, inputRect);
	}

	private void drawChatFrame(Layout layout) {
		Rectangle header = layout.headerRect;
		Rectangle message = layout.messageRect;
		Rectangle input = layout.inputRect;

		drawInsetPanel(header, 0x17191b, 0x2b2f33);
		drawInsetPanel(message, 0x121315, 0x2a2d31);
		drawInsetPanel(input, 0x16181a, 0x35393d);

		DrawingArea.drawPixels(1, message.y + 1, message.x + 2, 0x2e3236, message.width - 4);
		DrawingArea.drawPixels(1, input.y + 1, input.x + 2, 0x3a3f44, input.width - 4);
	}

	private void drawInsetPanel(Rectangle rect, int baseColor, int accentColor) {
		DrawingArea.drawPixels(rect.height, rect.y, rect.x, baseColor, rect.width);
		DrawingArea.drawAlphaGradient(rect.x, rect.y, rect.width, rect.height, accentColor, 0x0c0d0e, 90);
		DrawingArea.drawPixels(1, rect.y, rect.x, 0x1f2124, rect.width);
		DrawingArea.drawPixels(1, rect.y + rect.height - 1, rect.x, 0x0d0e10, rect.width);
		DrawingArea.drawPixels(rect.height, rect.y, rect.x, 0x1f2124, 1);
		DrawingArea.drawPixels(rect.height, rect.y, rect.x + rect.width - 1, 0x1f2124, 1);
	}

	private static final class Layout {
		private final Rectangle headerRect;
		private final Rectangle messageRect;
		private final Rectangle inputRect;

		private Layout(Rectangle headerRect, Rectangle messageRect, Rectangle inputRect) {
			this.headerRect = headerRect;
			this.messageRect = messageRect;
			this.inputRect = inputRect;
		}
	}
}
