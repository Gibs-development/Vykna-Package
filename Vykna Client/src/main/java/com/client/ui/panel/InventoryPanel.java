package com.client.ui.panel;

import com.client.Client;
import com.client.DrawingArea;
import com.client.graphics.interfaces.RSInterface;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public class InventoryPanel extends PanelManager.TabPanel {
	private static final int INVENTORY_CONTAINER_ID = 3214;

	private static final int SLOT_SIZE = 32;
	private static final int CONTENT_PADDING = 4;
	private static final int MIN_COLUMNS = 2;
	private static final int MAX_COLUMNS = 8;

	// Real inventory slots in 317 inventory
	private static final int REAL_INV_SLOTS = 28;

	private int cachedColumns = 4;
	private int cachedRows = 7;

	public InventoryPanel(int id, Rectangle bounds) {
		super(id, 3, bounds, "Inventory", true, false, 140, 200 + PanelManager.PANEL_HEADER_HEIGHT);
	}

	@Override
	public void draw(Client client) {
		applyResponsiveLayout(client, false);
		drawSlotGrid(client);
		super.draw(client);
	}

	@Override
	public boolean handleMouse(Client client, int mouseX, int mouseY) {
		applyResponsiveLayout(client, false);
		return super.handleMouse(client, mouseX, mouseY);
	}

	@Override
	public void onResize(Client client) {
		boolean changed = applyResponsiveLayout(client, true);
		// Persist only when the layout actually changed on resize
		if (changed) {
			client.getPanelManager().saveLayout(client);
		}
	}

	public Dimension clampSizeForResize(int width, int height, Client client) {
		RSInterface container = RSInterface.interfaceCache[INVENTORY_CONTAINER_ID];
		int padX = container == null ? 4 : container.invSpritePadX;
		int padY = container == null ? 4 : container.invSpritePadY;

		int headerHeight = PanelManager.getPanelHeaderHeight(client, this);
		int contentWidth = Math.max(1, width - CONTENT_PADDING * 2);

		int columns = Math.max(MIN_COLUMNS, Math.min(MAX_COLUMNS, (contentWidth + padX) / (SLOT_SIZE + padX)));
		int rows = (int) Math.ceil(REAL_INV_SLOTS / (double) columns);

		int neededHeight = rows * SLOT_SIZE + Math.max(0, rows - 1) * padY + CONTENT_PADDING * 2 + headerHeight;
		int neededWidth = columns * SLOT_SIZE + Math.max(0, columns - 1) * padX + CONTENT_PADDING * 2;

		return new Dimension(Math.max(width, neededWidth), Math.max(height, neededHeight));
	}

	/**
	 * Applies responsive layout to the real inventory container (3214).
	 *
	 * IMPORTANT:
	 * - The item container renderer typically assumes inventory arrays are sized to (width * height).
	 * - However, the real inventory is only 28 slots. Extra cells must be treated as non-existent visually,
	 *   and must remain empty.
	 *
	 * @return true if columns/rows changed (layout changed)
	 */
	private boolean applyResponsiveLayout(Client client, boolean fromResize) {
		RSInterface container = RSInterface.interfaceCache[INVENTORY_CONTAINER_ID];
		if (container == null) {
			return false;
		}

		// Ensure inventory interaction flags are enabled on the real container.
		// (Different bases gate dragging off different flags; this keeps it permissive.)
		container.isInventoryInterface = true;

		Rectangle bounds = getBounds();
		int padX = container.invSpritePadX;
		int padY = container.invSpritePadY;

		int headerHeight = PanelManager.getPanelHeaderHeight(client, this);
		int contentWidth = Math.max(1, bounds.width - CONTENT_PADDING * 2);

		int columns = Math.max(MIN_COLUMNS, Math.min(MAX_COLUMNS, (contentWidth + padX) / (SLOT_SIZE + padX)));
		int rows = (int) Math.ceil(REAL_INV_SLOTS / (double) columns);

		int requiredHeight = rows * SLOT_SIZE + Math.max(0, rows - 1) * padY + CONTENT_PADDING * 2 + headerHeight;
		bounds.height = Math.max(bounds.height, requiredHeight);

		if (columns == cachedColumns && rows == cachedRows) {
			return false;
		}

		cachedColumns = columns;
		cachedRows = rows;

		int targetSize = columns * rows;

		// Resize arrays to match container expectations (width*height), but preserve first 28 real slots.
		if (container.inventoryItemId == null || container.inventoryItemId.length != targetSize) {
			int[] oldItems = container.inventoryItemId == null ? new int[0] : container.inventoryItemId;
			int[] oldAmounts = container.inventoryAmounts == null ? new int[0] : container.inventoryAmounts;

			container.inventoryItemId = new int[targetSize];
			container.inventoryAmounts = new int[targetSize];

			// Preserve real slots 0..27
			for (int index = 0; index < Math.min(REAL_INV_SLOTS, targetSize); index++) {
				if (index < oldItems.length) {
					container.inventoryItemId[index] = oldItems[index];
					container.inventoryAmounts[index] = oldAmounts[index];
				}
			}
		}

		// Force any phantom cells (>= 28) to empty so they never render items.
		for (int i = REAL_INV_SLOTS; i < targetSize; i++) {
			container.inventoryItemId[i] = 0;
			container.inventoryAmounts[i] = 0;
		}

		container.width = columns;
		container.height = rows;

		// Do NOT save layout during draw/mouse; only save from onResize when changed.
		// (fromResize is provided as a hint but we gate save in onResize.)
		return true;
	}

	private void drawSlotGrid(Client client) {
		RSInterface container = RSInterface.interfaceCache[INVENTORY_CONTAINER_ID];
		if (container == null) {
			return;
		}

		Point containerOffset = getContainerOffset();
		Rectangle bounds = getBounds();
		int headerHeight = PanelManager.getPanelHeaderHeight(client, this);

		int padX = container.invSpritePadX;
		int padY = container.invSpritePadY;

		int startX = bounds.x + containerOffset.x;
		int startY = bounds.y + headerHeight + containerOffset.y;

		int gridColor = 0x1a1a1a;

		// Draw only the real 28 slots, not the phantom grid cells.
		for (int row = 0; row < cachedRows; row++) {
			for (int col = 0; col < cachedColumns; col++) {
				int slotIndex = col + row * cachedColumns;
				if (slotIndex >= REAL_INV_SLOTS) {
					continue;
				}

				int x = startX + col * (SLOT_SIZE + padX);
				int y = startY + row * (SLOT_SIZE + padY);
				DrawingArea.drawPixels(SLOT_SIZE, y, x, gridColor, SLOT_SIZE);
			}
		}
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
		applyResponsiveLayout(client, false);
		Rectangle bounds = getBounds();
		int headerHeight = PanelManager.getPanelHeaderHeight(client, this);
		Point containerOffset = getContainerOffset();
		return new Point(bounds.x + containerOffset.x, bounds.y + headerHeight + containerOffset.y);
	}

	@Override
	public boolean isScrollable() {
		return false;
	}

	public static void resetInventoryContainer() {
		RSInterface container = RSInterface.interfaceCache[INVENTORY_CONTAINER_ID];
		if (container == null) {
			return;
		}
		container.width = 4;
		container.height = 7;

		if (container.inventoryItemId == null || container.inventoryItemId.length != REAL_INV_SLOTS) {
			int[] oldItems = container.inventoryItemId == null ? new int[0] : container.inventoryItemId;
			int[] oldAmounts = container.inventoryAmounts == null ? new int[0] : container.inventoryAmounts;

			container.inventoryItemId = new int[REAL_INV_SLOTS];
			container.inventoryAmounts = new int[REAL_INV_SLOTS];

			for (int index = 0; index < Math.min(REAL_INV_SLOTS, oldItems.length); index++) {
				container.inventoryItemId[index] = oldItems[index];
				container.inventoryAmounts[index] = oldAmounts[index];
			}
		}
	}

	public void resetCachedLayout() {
		cachedColumns = 4;
		cachedRows = 7;
	}
}
