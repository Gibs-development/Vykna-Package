package com.client.ui.panel;

import com.client.Client;
import com.client.graphics.interfaces.RSInterface;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public class InventoryPanel extends PanelManager.TabPanel {
	public static final int RS3_INVENTORY_INTERFACE_ID = 52000;
	private static final int INVENTORY_CONTAINER_ID = 3214;
	private static final int CONTENT_PADDING = 4;
	private static final int INVENTORY_SLOT_SIZE = 32;
	private static final int INVENTORY_MIN_COLUMNS = 2;
	private static final int INVENTORY_MAX_COLUMNS = 8;

	public InventoryPanel(int id, Rectangle bounds) {
		super(id, 3, bounds, "Inventory", true, true, 140, 200 + PanelManager.PANEL_HEADER_HEIGHT);
	}

	@Override
	public void draw(Client client) {
		ensureInventoryContainerFlags();
		super.draw(client);
	}

	@Override
	public boolean handleMouse(Client client, int mouseX, int mouseY) {
		ensureInventoryContainerFlags();
		return super.handleMouse(client, mouseX, mouseY);
	}

	@Override
	protected boolean allowPanelChromeCapture(Client client, int mouseX, int mouseY) {
		Rectangle bounds = getBounds();
		int headerHeight = PanelManager.getPanelHeaderHeight(client, this);
		return mouseX >= bounds.x && mouseX <= bounds.x + bounds.width
				&& mouseY >= bounds.y && mouseY <= bounds.y + headerHeight;
	}

	@Override
	public void onResize(Client client) {
		RSInterface rsInterface = getInventoryWrapper();
		if (rsInterface == null) {
			return;
		}
		applyInventoryLayout(client, rsInterface, getContentBounds(client));
	}

	public Dimension clampSizeForResize(int width, int height, Client client) {
		RSInterface container = RSInterface.interfaceCache[INVENTORY_CONTAINER_ID];
		int padX = container == null ? 0 : container.invSpritePadX;
		int padY = container == null ? 0 : container.invSpritePadY;
		int headerHeight = PanelManager.getPanelHeaderHeight(client, this);
		int contentWidth = Math.max(1, width - CONTENT_PADDING * 2);
		int columns = clampColumns(contentWidth, padX);
		int rows = (int) Math.ceil(28D / columns);
		int requiredContentHeight = rows * INVENTORY_SLOT_SIZE + Math.max(0, rows - 1) * padY;
		int minContentWidth = INVENTORY_MIN_COLUMNS * INVENTORY_SLOT_SIZE + (INVENTORY_MIN_COLUMNS - 1) * padX;
		int minWidth = minContentWidth + CONTENT_PADDING * 2;
		int minHeight = headerHeight + requiredContentHeight + CONTENT_PADDING * 2;
		return new Dimension(Math.max(width, minWidth), Math.max(height, minHeight));
	}

	private Point getContainerOffset() {
		int interfaceId = Client.tabInterfaceIDs[getTabIndex()];
		RSInterface parent = RSInterface.interfaceCache[interfaceId];
		if (parent == null || parent.children == null) {
			return new Point(CONTENT_PADDING, CONTENT_PADDING);
		}
		for (int index = 0; index < parent.children.length; index++) {
			if (parent.children[index] == INVENTORY_CONTAINER_ID) {
				int offsetX = Math.max(CONTENT_PADDING, parent.childX[index]);
				int offsetY = Math.max(CONTENT_PADDING, parent.childY[index]);
				return new Point(offsetX, offsetY);
			}
		}
		return new Point(CONTENT_PADDING, CONTENT_PADDING);
	}

	public Point getInventoryOrigin(Client client) {
		Rectangle bounds = getBounds();
		int headerHeight = PanelManager.getPanelHeaderHeight(client, this);
		Point containerOffset = getContainerOffset();
		return new Point(bounds.x + containerOffset.x, bounds.y + headerHeight + containerOffset.y);
	}

	@Override
	public boolean isScrollable() {
		return false;
	}

	@Override
	protected int getContentPadding(Client client, Rectangle bounds) {
		return CONTENT_PADDING;
	}

	@Override
	protected void updateInterfaceLayout(Client client, RSInterface rsInterface, Rectangle bounds) {
		rsInterface.width = bounds.width;
		rsInterface.height = bounds.height;
		applyInventoryLayout(client, rsInterface, bounds);
	}

	private void ensureInventoryContainerFlags() {
		RSInterface container = RSInterface.interfaceCache[INVENTORY_CONTAINER_ID];
		if (container == null) {
			return;
		}
		container.isInventoryInterface = true;
		container.aBoolean259 = true;
	}

	private void applyInventoryLayout(Client client, RSInterface rsInterface, Rectangle bounds) {
		RSInterface container = RSInterface.interfaceCache[INVENTORY_CONTAINER_ID];
		if (container == null) {
			return;
		}
		int padX = container.invSpritePadX;
		int padY = container.invSpritePadY;
		int contentWidth = Math.max(1, bounds.width);
		int columns = clampColumns(contentWidth, padX);
		int rows = (int) Math.ceil(28D / columns);
		int requiredHeight = rows * INVENTORY_SLOT_SIZE + Math.max(0, rows - 1) * padY;
		rsInterface.height = Math.max(bounds.height, requiredHeight);
		int targetSize = columns * rows;
		if (container.inventoryItemId == null || container.inventoryItemId.length != targetSize) {
			int[] oldItems = container.inventoryItemId == null ? new int[0] : container.inventoryItemId;
			int[] oldAmounts = container.inventoryAmounts == null ? new int[0] : container.inventoryAmounts;
			container.inventoryItemId = new int[targetSize];
			container.inventoryAmounts = new int[targetSize];
			for (int index = 0; index < Math.min(28, targetSize); index++) {
				if (index < oldItems.length) {
					container.inventoryItemId[index] = oldItems[index];
					container.inventoryAmounts[index] = oldAmounts[index];
				}
			}
		}
		container.width = columns;
		container.height = rows;
	}

	private RSInterface getInventoryWrapper() {
		int interfaceId = Client.tabInterfaceIDs[getTabIndex()];
		if (interfaceId <= 0) {
			return null;
		}
		return RSInterface.interfaceCache[interfaceId];
	}

	private int clampColumns(int contentWidth, int padX) {
		return Math.max(INVENTORY_MIN_COLUMNS,
				Math.min(INVENTORY_MAX_COLUMNS, (contentWidth + padX) / (INVENTORY_SLOT_SIZE + padX)));
	}

	public static void resetInventoryContainer() {
		RSInterface container = RSInterface.interfaceCache[INVENTORY_CONTAINER_ID];
		if (container == null) {
			return;
		}
		container.isInventoryInterface = true;
		container.aBoolean259 = true;
		container.width = 4;
		container.height = 7;

		if (container.inventoryItemId == null || container.inventoryItemId.length != 28) {
			int[] oldItems = container.inventoryItemId == null ? new int[0] : container.inventoryItemId;
			int[] oldAmounts = container.inventoryAmounts == null ? new int[0] : container.inventoryAmounts;

			container.inventoryItemId = new int[28];
			container.inventoryAmounts = new int[28];

			for (int index = 0; index < Math.min(28, oldItems.length); index++) {
				container.inventoryItemId[index] = oldItems[index];
				container.inventoryAmounts[index] = oldAmounts[index];
			}
		}
	}

	public void resetCachedLayout() {
	}
}
