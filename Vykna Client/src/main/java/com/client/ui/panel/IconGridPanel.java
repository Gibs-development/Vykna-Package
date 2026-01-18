package com.client.ui.panel;

import com.client.Client;
import com.client.graphics.interfaces.RSInterface;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class IconGridPanel extends PanelManager.TabPanel {
	private static final int CONTENT_PADDING = 4;
	private static final int MAX_ICON_SIZE = 40;

	private int cachedColumns = -1;
	private int cachedRows = -1;
	private int cachedIconCount = -1;
	private int cachedIconSize = -1;

	public IconGridPanel(int id, int tabIndex, Rectangle bounds, String title) {
		super(id, tabIndex, bounds, title, false, true, 160, 200 + PanelManager.PANEL_HEADER_HEIGHT);
	}

	@Override
	public void draw(Client client) {
		applyResponsiveLayout(client);
		super.draw(client);
	}

	@Override
	public boolean handleMouse(Client client, int mouseX, int mouseY) {
		applyResponsiveLayout(client);
		return super.handleMouse(client, mouseX, mouseY);
	}

	@Override
	public void onResize(Client client) {
		applyResponsiveLayout(client);
	}

	private void applyResponsiveLayout(Client client) {
		int interfaceId = Client.tabInterfaceIDs[getTabIndex()];
		if (interfaceId <= 0) {
			return;
		}
		RSInterface rsInterface = RSInterface.interfaceCache[interfaceId];
		if (rsInterface == null) {
			return;
		}
		Rectangle bounds = getContentBounds(client);
		updateInterfaceLayout(client, rsInterface, bounds);
	}

	@Override
	protected void updateInterfaceLayout(Client client, RSInterface rsInterface, Rectangle bounds) {
		rsInterface.width = bounds.width;
		rsInterface.height = bounds.height;
		applyIconGridLayout(rsInterface, bounds);
		rsInterface.scrollMax = Math.max(rsInterface.height, getInterfaceContentHeight(rsInterface));
	}

	private void applyIconGridLayout(RSInterface rsInterface, Rectangle bounds) {
		if (rsInterface.children == null) {
			return;
		}
		List<Integer> iconIndices = new ArrayList<>();
		int iconSize = 0;
		for (int index = 0; index < rsInterface.children.length; index++) {
			RSInterface child = RSInterface.interfaceCache[rsInterface.children[index]];
			if (!shouldReflowChild(child)) {
				continue;
			}
			iconIndices.add(index);
			iconSize = Math.max(iconSize, Math.max(child.width, child.height));
		}
		if (iconIndices.isEmpty() || iconSize == 0) {
			return;
		}
		int contentWidth = Math.max(1, bounds.width - CONTENT_PADDING * 2);
		int columns = Math.max(1, (contentWidth + CONTENT_PADDING) / (iconSize + CONTENT_PADDING));
		int rows = (int) Math.ceil(iconIndices.size() / (double) columns);
		int requiredHeight = rows * iconSize + Math.max(0, rows - 1) * CONTENT_PADDING + CONTENT_PADDING * 2;
		if (columns == cachedColumns && rows == cachedRows && iconSize == cachedIconSize && iconIndices.size() == cachedIconCount) {
			rsInterface.scrollMax = Math.max(bounds.height, Math.max(requiredHeight, getInterfaceContentHeight(rsInterface)));
			return;
		}
		cachedColumns = columns;
		cachedRows = rows;
		cachedIconCount = iconIndices.size();
		cachedIconSize = iconSize;
		for (int idx = 0; idx < iconIndices.size(); idx++) {
			int index = iconIndices.get(idx);
			int row = idx / columns;
			int col = idx % columns;
			rsInterface.childX[index] = CONTENT_PADDING + col * (iconSize + CONTENT_PADDING);
			rsInterface.childY[index] = CONTENT_PADDING + row * (iconSize + CONTENT_PADDING);
		}
		rsInterface.scrollMax = Math.max(bounds.height, Math.max(requiredHeight, getInterfaceContentHeight(rsInterface)));
	}

	private boolean shouldReflowChild(RSInterface child) {
		if (child == null) {
			return false;
		}
		if (child.type != 5) {
			return false;
		}
		if (child.width <= 0 || child.height <= 0) {
			return false;
		}
		return child.width <= MAX_ICON_SIZE && child.height <= MAX_ICON_SIZE;
	}
}
