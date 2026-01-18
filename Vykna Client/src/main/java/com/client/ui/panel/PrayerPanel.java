package com.client.ui.panel;

import com.client.Client;
import com.client.graphics.interfaces.RSInterface;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrayerPanel extends PanelManager.TabPanel {
	private static final int PRAYER_INTERFACE_ID = 5608;
	private static final int CONTENT_PADDING = 4;
	private static final int MAX_ICON_SIZE = 40;

	private int[] baseChildX;
	private int[] baseChildY;
	private List<Integer> iconOrder;
	private int iconSize;
	private int padX = 4;
	private int padY = 4;

	public PrayerPanel(int id, int tabIndex, Rectangle bounds, String title) {
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
		applyPrayerLayout(rsInterface, bounds);
		rsInterface.scrollMax = Math.max(rsInterface.height, getInterfaceContentHeight(rsInterface));
	}

	private void applyPrayerLayout(RSInterface rsInterface, Rectangle bounds) {
		RSInterface prayerBook = RSInterface.interfaceCache[PRAYER_INTERFACE_ID];
		if (prayerBook == null || prayerBook.children == null) {
			return;
		}
		cacheBaseLayout(prayerBook);
		if (iconOrder == null || iconOrder.isEmpty() || iconSize == 0) {
			return;
		}
		prayerBook.width = bounds.width;
		prayerBook.height = bounds.height;
		int contentWidth = Math.max(1, bounds.width - CONTENT_PADDING * 2);
		int columns = Math.max(1, (contentWidth + padX) / (iconSize + padX));
		int rows = (int) Math.ceil(iconOrder.size() / (double) columns);
		for (int idx = 0; idx < iconOrder.size(); idx++) {
			int index = iconOrder.get(idx);
			int row = idx / columns;
			int col = idx % columns;
			prayerBook.childX[index] = CONTENT_PADDING + col * (iconSize + padX);
			prayerBook.childY[index] = CONTENT_PADDING + row * (iconSize + padY);
		}
		int requiredHeight = rows * iconSize + Math.max(0, rows - 1) * padY + CONTENT_PADDING * 2;
		prayerBook.scrollMax = Math.max(bounds.height, Math.max(requiredHeight, getInterfaceContentHeight(prayerBook)));
	}

	private void cacheBaseLayout(RSInterface rsInterface) {
		if (baseChildX != null && baseChildY != null) {
			return;
		}
		baseChildX = rsInterface.childX.clone();
		baseChildY = rsInterface.childY.clone();
		iconOrder = new ArrayList<>();
		iconSize = 0;
		Set<Integer> uniqueX = new HashSet<>();
		Set<Integer> uniqueY = new HashSet<>();
		for (int index = 0; index < rsInterface.children.length; index++) {
			RSInterface child = RSInterface.interfaceCache[rsInterface.children[index]];
			if (!shouldReflowChild(child)) {
				continue;
			}
			iconOrder.add(index);
			iconSize = Math.max(iconSize, Math.max(child.width, child.height));
			uniqueX.add(baseChildX[index]);
			uniqueY.add(baseChildY[index]);
		}
		iconOrder.sort(Comparator.comparingInt((Integer index) -> baseChildY[index])
				.thenComparingInt(index -> baseChildX[index]));
		padX = Math.max(0, resolvePadding(uniqueX, iconSize, 4));
		padY = Math.max(0, resolvePadding(uniqueY, iconSize, 4));
	}

	private int resolvePadding(Set<Integer> positions, int size, int fallback) {
		if (positions.size() < 2) {
			return fallback;
		}
		List<Integer> sorted = new ArrayList<>(positions);
		sorted.sort(Integer::compareTo);
		int minDelta = Integer.MAX_VALUE;
		for (int i = 1; i < sorted.size(); i++) {
			int delta = sorted.get(i) - sorted.get(i - 1);
			if (delta > 0 && delta < minDelta) {
				minDelta = delta;
			}
		}
		if (minDelta == Integer.MAX_VALUE) {
			return fallback;
		}
		return Math.max(fallback, minDelta - size);
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
