package com.client.ui.panel;

import com.client.Client;
import com.client.graphics.interfaces.RSInterface;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public class InventoryPanel extends PanelManager.TabPanel {
	private static final int INVENTORY_CONTAINER_ID = 3214;
	private static final int CONTENT_PADDING = 4;

	public InventoryPanel(int id, Rectangle bounds) {
		super(id, 3, bounds, "Inventory", true, false, 140, 200 + PanelManager.PANEL_HEADER_HEIGHT);
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
	}

	public Dimension clampSizeForResize(int width, int height, Client client) {
		return new Dimension(width, height);
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

	private void ensureInventoryContainerFlags() {
		RSInterface container = RSInterface.interfaceCache[INVENTORY_CONTAINER_ID];
		if (container == null) {
			return;
		}
		container.isInventoryInterface = true;
		container.aBoolean259 = true;
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
