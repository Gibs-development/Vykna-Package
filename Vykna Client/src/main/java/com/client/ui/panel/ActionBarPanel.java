package com.client.ui.panel;

import com.client.Client;
import com.client.DrawingArea;
import com.client.graphics.interfaces.RSInterface;

import java.awt.Rectangle;

public class ActionBarPanel extends PanelManager.BasePanel {
	private static final int SLOT_SIZE = 30;
	private static final int SLOT_GAP = 4;
	private static final int SLOT_COUNT = 14;
	private static final int ENABLED_SLOTS = 12;
	private static final int BAR_HEIGHT = 10;
	private static final int BAR_GAP = 6;
	private static final int FRAME_PAD = 8;
	private static final int COOLDOWN_STEP = 3;

	private int hoveredSlot = -1;
	private int activeSlot = -1;
	private int cooldownSlot = -1;
	private int cooldownPercent = 0;

	public ActionBarPanel(int id, Rectangle bounds) {
		super(id, bounds, true, true, "Action Bar", true, 360, 80 + PanelManager.PANEL_HEADER_HEIGHT, false);
	}

	@Override
	public void draw(Client client) {
		updateCooldown();
		Rectangle bounds = getBounds();
		int headerHeight = PanelManager.getPanelHeaderHeight(client, this);
		int frameX = bounds.x + FRAME_PAD;
		int frameY = bounds.y + headerHeight + FRAME_PAD;
		int frameWidth = bounds.width - FRAME_PAD * 2;
		int frameHeight = bounds.height - headerHeight - FRAME_PAD * 2;
		drawFrame(frameX, frameY, frameWidth, frameHeight);

		int contentX = frameX + 6;
		int contentY = frameY + 6;
		int contentWidth = Math.max(100, frameWidth - 12);
		int barWidth = Math.max(60, contentWidth);
		drawBar(contentX, contentY, barWidth, BAR_HEIGHT, getHealthPercent(client), 0x2f2f2f, 0x9b2727);
		drawBar(contentX, contentY + BAR_HEIGHT + BAR_GAP, barWidth, BAR_HEIGHT, getPrayerPercent(client), 0x2f2f2f, 0x2a5fa8);
		int slotY = contentY + (BAR_HEIGHT * 2) + (BAR_GAP * 2) + 4;
		drawSlots(contentX, slotY, contentWidth);
	}

	@Override
	public boolean handleMouse(Client client, int mouseX, int mouseY) {
		hoveredSlot = resolveSlot(client, mouseX, mouseY);
		return hoveredSlot != -1;
	}

	@Override
	public boolean handleClick(Client client, int mouseX, int mouseY) {
		int slot = resolveSlot(client, mouseX, mouseY);
		if (slot == -1 || slot >= ENABLED_SLOTS) {
			return false;
		}
		activeSlot = slot;
		cooldownSlot = slot;
		cooldownPercent = 100;
		return true;
	}

	private int resolveSlot(Client client, int mouseX, int mouseY) {
		Rectangle bounds = getBounds();
		int headerHeight = PanelManager.getPanelHeaderHeight(client, this);
		int frameX = bounds.x + FRAME_PAD;
		int frameY = bounds.y + headerHeight + FRAME_PAD;
		int contentX = frameX + 6;
		int contentY = frameY + 6;
		int contentWidth = Math.max(100, bounds.width - FRAME_PAD * 2 - 12);
		int slotY = contentY + (BAR_HEIGHT * 2) + (BAR_GAP * 2) + 4;
		int totalWidth = SLOT_COUNT * SLOT_SIZE + (SLOT_COUNT - 1) * SLOT_GAP;
		int startX = contentX + Math.max(0, (contentWidth - totalWidth) / 2);
		if (mouseY < slotY || mouseY > slotY + SLOT_SIZE) {
			return -1;
		}
		for (int slot = 0; slot < SLOT_COUNT; slot++) {
			int slotX = startX + slot * (SLOT_SIZE + SLOT_GAP);
			if (mouseX >= slotX && mouseX <= slotX + SLOT_SIZE) {
				return slot;
			}
		}
		return -1;
	}

	private void updateCooldown() {
		if (cooldownSlot == -1) {
			return;
		}
		cooldownPercent = Math.max(0, cooldownPercent - COOLDOWN_STEP);
		if (cooldownPercent == 0) {
			cooldownSlot = -1;
		}
	}

	private void drawFrame(int x, int y, int width, int height) {
		DrawingArea.drawPixels(height, y, x, 0x16181b, width);
		DrawingArea.drawAlphaGradient(x, y, width, height, 0x2f3237, 0x0f1113, 120);
		DrawingArea.drawPixels(1, y, x, 0x3d4148, width);
		DrawingArea.drawPixels(1, y + height - 1, x, 0x1b1d20, width);
		DrawingArea.drawPixels(height, y, x, 0x1f2125, 1);
		DrawingArea.drawPixels(height, y, x + width - 1, 0x2b2f34, 1);
		DrawingArea.drawPixels(1, y + 1, x + 1, 0x4b4f55, width - 2);
		DrawingArea.drawPixels(1, y + height - 2, x + 1, 0x0b0c0e, width - 2);
	}

	private void drawBar(int x, int y, int width, int height, int percent, int background, int fill) {
		int clamped = Math.max(0, Math.min(100, percent));
		DrawingArea.drawPixels(height, y, x, background, width);
		int fillWidth = (width * clamped) / 100;
		if (fillWidth > 0) {
			DrawingArea.drawPixels(height, y, x, fill, fillWidth);
		}
		DrawingArea.drawAlphaGradient(x, y, width, height, 0x4b4e54, 0x0f1113, 90);
		DrawingArea.drawPixels(1, y, x, 0x1c1f22, width);
		DrawingArea.drawPixels(1, y + height - 1, x, 0x141618, width);
		DrawingArea.drawPixels(height, y, x, 0x1c1f22, 1);
		DrawingArea.drawPixels(height, y, x + width - 1, 0x1c1f22, 1);
	}

	private void drawSlots(int x, int y, int contentWidth) {
		int totalWidth = SLOT_COUNT * SLOT_SIZE + (SLOT_COUNT - 1) * SLOT_GAP;
		int startX = x + Math.max(0, (contentWidth - totalWidth) / 2);
		for (int slot = 0; slot < SLOT_COUNT; slot++) {
			int slotX = startX + slot * (SLOT_SIZE + SLOT_GAP);
			boolean disabled = slot >= ENABLED_SLOTS;
			boolean hovered = slot == hoveredSlot;
			boolean active = slot == activeSlot;
			int cooldown = (slot == cooldownSlot) ? cooldownPercent : 0;
			drawSlot(slotX, y, disabled, hovered, active, cooldown);
		}
	}

	private void drawSlot(int x, int y, boolean disabled, boolean hovered, boolean active, int cooldownPercent) {
		int base = disabled ? 0x131416 : 0x191b1e;
		int highlight = disabled ? 0x24272b : 0x30343a;
		int shadow = disabled ? 0x0d0f11 : 0x111316;
		DrawingArea.drawPixels(SLOT_SIZE, y, x, base, SLOT_SIZE);
		DrawingArea.drawAlphaGradient(x, y, SLOT_SIZE, SLOT_SIZE, highlight, shadow, 120);
		DrawingArea.drawPixels(1, y, x, 0x3b3f45, SLOT_SIZE);
		DrawingArea.drawPixels(1, y + SLOT_SIZE - 1, x, 0x0f1113, SLOT_SIZE);
		DrawingArea.drawPixels(SLOT_SIZE, y, x, 0x2d3136, 1);
		DrawingArea.drawPixels(SLOT_SIZE, y, x + SLOT_SIZE - 1, 0x24272b, 1);
		if (active) {
			DrawingArea.drawPixels(1, y + 1, x + 1, 0xc89b41, SLOT_SIZE - 2);
			DrawingArea.drawPixels(1, y + SLOT_SIZE - 2, x + 1, 0x6a4a16, SLOT_SIZE - 2);
		}
		if (hovered) {
			DrawingArea.drawPixels(1, y, x, 0xffd24a, SLOT_SIZE);
			DrawingArea.drawPixels(1, y + SLOT_SIZE - 1, x, 0xffd24a, SLOT_SIZE);
			DrawingArea.drawPixels(SLOT_SIZE, y, x, 0xffd24a, 1);
			DrawingArea.drawPixels(SLOT_SIZE, y, x + SLOT_SIZE - 1, 0xffd24a, 1);
		}
		if (cooldownPercent > 0) {
			int cooldownHeight = (SLOT_SIZE * cooldownPercent) / 100;
			int cooldownY = y + SLOT_SIZE - cooldownHeight;
			DrawingArea.drawAlphaPixels(x, cooldownY, SLOT_SIZE, cooldownHeight, 0x0a0b0d, 140);
		}
		if (disabled) {
			DrawingArea.drawAlphaPixels(x, y, SLOT_SIZE, SLOT_SIZE, 0x000000, 140);
		}
	}

	private int getHealthPercent(Client client) {
		RSInterface current = RSInterface.interfaceCache[4016];
		RSInterface max = RSInterface.interfaceCache[4017];
		if (current == null || max == null || current.message == null || max.message == null) {
			return 0;
		}
		try {
			int currentHp = Integer.parseInt(current.message);
			int maxHp = Integer.parseInt(max.message);
			if (maxHp <= 0) {
				return 0;
			}
			int clamped = Math.max(0, Math.min(currentHp, maxHp));
			return (int) ((clamped / (double) maxHp) * 100);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private int getPrayerPercent(Client client) {
		RSInterface current = RSInterface.interfaceCache[4012];
		if (current == null || current.message == null) {
			return 0;
		}
		try {
			int currentPrayer = Integer.parseInt(current.message.replaceAll("%", ""));
			int maxPrayer = client.maxStats[5];
			if (maxPrayer <= 0) {
				return 0;
			}
			int clamped = Math.max(0, Math.min(currentPrayer, maxPrayer));
			return (int) ((clamped / (double) maxPrayer) * 100);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}
