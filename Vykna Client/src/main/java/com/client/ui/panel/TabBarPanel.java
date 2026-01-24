package com.client.ui.panel;

import com.client.Client;
import com.client.DrawingArea;
import com.client.Sprite;

import java.awt.Dimension;
import java.awt.Rectangle;

public class TabBarPanel extends PanelManager.BasePanel {
	private static final int ICON_SIZE = 32;
	private static final int ICON_PADDING = 4;

	private static final int ACTIVE_BG = 0x262626;
	private static final int INACTIVE_BG = 0x1a1a1a;
	private static final int BORDER = 0x2c2c2c;

	private static final TabEntry[] TABS = {
			new TabEntry("Quest", 0, PanelManager.PANEL_ID_QUEST),
			new TabEntry("Stats", 1, PanelManager.PANEL_ID_STATS),
			new TabEntry("Skills", 2, PanelManager.PANEL_ID_SKILLS),
			new TabEntry("Inventory", 3, PanelManager.PANEL_ID_INVENTORY),
			new TabEntry("Equipment", 4, PanelManager.PANEL_ID_EQUIPMENT),
			new TabEntry("Prayer", 5, PanelManager.PANEL_ID_PRAYER),
			new TabEntry("Magic", 6, PanelManager.PANEL_ID_MAGIC),
			new TabEntry("Clan", 7, PanelManager.PANEL_ID_CLAN),
			new TabEntry("Friends", 8, PanelManager.PANEL_ID_FRIENDS),
			new TabEntry("Settings", 9, PanelManager.PANEL_ID_SETTINGS),
			new TabEntry("Emotes", 10, PanelManager.PANEL_ID_EMOTES),
			new TabEntry("Music", 11, PanelManager.PANEL_ID_MUSIC),
			new TabEntry("Notes", 12, PanelManager.PANEL_ID_NOTES),
			new TabEntry("Logout", 13, PanelManager.PANEL_ID_LOGOUT)
	};

	public TabBarPanel(int id, Rectangle bounds) {
		super(id, bounds, true, true, "Tabs", true,
				ICON_SIZE + ICON_PADDING * 2,
				ICON_SIZE + ICON_PADDING * 2 + PanelManager.PANEL_HEADER_HEIGHT,
				false);
	}

	@Override
	public void draw(Client client) {
		Rectangle bounds = getBounds();
		int headerHeight = PanelManager.getPanelHeaderHeight(client, this);

		// Absolute drawing (matches your current panel background snapping behavior)
		int startX = bounds.x + ICON_PADDING;
		int startY = bounds.y + headerHeight + ICON_PADDING;

		// FIX: off-by-one columns bug
		// Old: (innerWidth) / (size+pad) => returns (cols - 1) for snapped widths
		int columns = computeColumns(bounds.width);

		PanelManager manager = client.getPanelManager();

		for (int index = 0; index < TABS.length; index++) {
			int col = index % columns;
			int row = index / columns;

			int x = startX + col * (ICON_SIZE + ICON_PADDING);
			int y = startY + row * (ICON_SIZE + ICON_PADDING);

			boolean active = manager.isPanelVisible(TABS[index].panelId);
			int color = active ? ACTIVE_BG : INACTIVE_BG;

			// Background + border
			DrawingArea.drawPixels(ICON_SIZE, y, x, color, ICON_SIZE);
			DrawingArea.drawPixels(1, y, x, BORDER, ICON_SIZE);                  // top
			DrawingArea.drawPixels(1, y + ICON_SIZE - 1, x, BORDER, ICON_SIZE);  // bottom
			DrawingArea.drawPixels(ICON_SIZE, y, x, BORDER, 1);                  // left
			DrawingArea.drawPixels(ICON_SIZE, y, x + ICON_SIZE - 1, BORDER, 1);  // right

			// Icon
			Sprite icon = client.getTabIconSprite(TABS[index].tabIndex);
			if (icon != null) {
				int iconX = x + (ICON_SIZE - icon.myWidth) / 2;
				int iconY = y + (ICON_SIZE - icon.myHeight) / 2;
				icon.drawSprite(iconX, iconY);
			} else {
				String label = TABS[index].label.substring(0, 1);
				client.newSmallFont.drawCenteredString(label, x + ICON_SIZE / 2, y + 20, 0xffffff, 0);
			}
		}
	}

	@Override
	public boolean handleMouse(Client client, int mouseX, int mouseY) {
		return true;
	}

	@Override
	public boolean handleClick(Client client, int mouseX, int mouseY) {
		Rectangle bounds = getBounds();

		// Some PanelManagers pass ABSOLUTE mouse coords, others pass LOCAL.
		// Auto-detect:
		boolean looksLocal =
				mouseX >= 0 && mouseY >= 0 &&
						mouseX <= bounds.width && mouseY <= bounds.height;

		int absMouseX = looksLocal ? (bounds.x + mouseX) : mouseX;
		int absMouseY = looksLocal ? (bounds.y + mouseY) : mouseY;

		// Must be inside this panel bounds (absolute check)
		if (absMouseX < bounds.x || absMouseY < bounds.y ||
				absMouseX > bounds.x + bounds.width || absMouseY > bounds.y + bounds.height) {
			return false;
		}

		int headerHeight = PanelManager.getPanelHeaderHeight(client, this);
		int startX = bounds.x + ICON_PADDING;
		int startY = bounds.y + headerHeight + ICON_PADDING;

		int columns = computeColumns(bounds.width);

		PanelManager manager = client.getPanelManager();

		for (int index = 0; index < TABS.length; index++) {
			int col = index % columns;
			int row = index / columns;

			int x = startX + col * (ICON_SIZE + ICON_PADDING);
			int y = startY + row * (ICON_SIZE + ICON_PADDING);

			if (absMouseX >= x && absMouseX <= x + ICON_SIZE &&
					absMouseY >= y && absMouseY <= y + ICON_SIZE) {
				manager.togglePanelVisibility(TABS[index].panelId);
				manager.saveLayout(client);
				return true;
			}
		}

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

	public Dimension getSnappedSize(Client client, Rectangle bounds) {
		int headerHeight = PanelManager.getPanelHeaderHeight(client, this);

		// Prefer these exact layouts
		int[][] layouts = {
				{14, 1},
				{1, 14},
				{7, 2},
				{2, 7}
		};

		int bestWidth = bounds.width;
		int bestHeight = bounds.height;
		int bestDelta = Integer.MAX_VALUE;

		for (int[] layout : layouts) {
			int columns = layout[0];
			int rows = layout[1];

			int width = ICON_PADDING * 2 + columns * ICON_SIZE + (columns - 1) * ICON_PADDING;
			int height = headerHeight + ICON_PADDING * 2 + rows * ICON_SIZE + (rows - 1) * ICON_PADDING;

			int delta = Math.abs(bounds.width - width) + Math.abs(bounds.height - height);
			if (delta < bestDelta) {
				bestDelta = delta;
				bestWidth = width;
				bestHeight = height;
			}
		}

		return new Dimension(bestWidth, bestHeight);
	}

	private static int computeColumns(int panelWidth) {
		int innerWidth = panelWidth - ICON_PADDING * 2;
		// FIX: +ICON_PADDING so snapped widths yield the intended column count
		return Math.max(1, (innerWidth + ICON_PADDING) / (ICON_SIZE + ICON_PADDING));
	}

	private static final class TabEntry {
		private final String label;
		private final int tabIndex;
		private final int panelId;

		private TabEntry(String label, int tabIndex, int panelId) {
			this.label = label;
			this.tabIndex = tabIndex;
			this.panelId = panelId;
		}
	}
}
