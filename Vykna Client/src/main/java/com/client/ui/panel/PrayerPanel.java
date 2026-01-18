package com.client.ui.panel;

import com.client.Client;
import com.client.graphics.interfaces.RSInterface;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrayerPanel extends PanelManager.TabPanel {
	private static final int PRAYER_INTERFACE_ID = 5608;
	private static final int CONTENT_PADDING = 4;
	private static final int MAX_ICON_SIZE = 40;
	private static final int FILTERED_TRANSPARENCY = 140;

	private int[] baseChildX;
	private int[] baseChildY;
	private boolean[] baseDrawsTransparent;
	private int[] baseTransparency;
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
		if (isFilterModeEnabled()) {
			return true;
		}
		return super.handleMouse(client, mouseX, mouseY);
	}

	@Override
	public void onResize(Client client) {
		applyResponsiveLayout(client);
	}

	@Override
	public boolean handleClick(Client client, int mouseX, int mouseY) {
		if (!isFilterModeEnabled()) {
			return super.handleClick(client, mouseX, mouseY);
		}
		if (toggleFilterAt(client, mouseX, mouseY)) {
			client.menuOpen = false;
			client.menuActionRow = 0;
			applyResponsiveLayout(client);
		}
		return true;
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
		boolean filterMode = isFilterModeEnabled();
		java.util.Set<Integer> filtered = getFilteredPrayerIndices();
		prayerBook.width = bounds.width;
		prayerBook.height = bounds.height;
		int contentWidth = Math.max(1, bounds.width - CONTENT_PADDING * 2);
		List<Integer> visibleIcons = new ArrayList<>();
		for (int index : iconOrder) {
			boolean isFiltered = filtered.contains(index);
			RSInterface child = RSInterface.interfaceCache[prayerBook.children[index]];
			if (child != null) {
				child.interfaceHidden = !filterMode && isFiltered;
				child.drawsTransparent = baseDrawsTransparent[index];
				child.transparency = baseTransparency[index];
				if (filterMode && isFiltered) {
					child.drawsTransparent = true;
					child.transparency = FILTERED_TRANSPARENCY;
				}
			}
			if (filterMode || !isFiltered) {
				visibleIcons.add(index);
			}
		}
		int columns = Math.max(1, (contentWidth + padX) / (iconSize + padX));
		int rows = (int) Math.ceil(visibleIcons.size() / (double) columns);
		for (int idx = 0; idx < visibleIcons.size(); idx++) {
			int index = visibleIcons.get(idx);
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
		baseDrawsTransparent = new boolean[rsInterface.children.length];
		baseTransparency = new int[rsInterface.children.length];
		iconOrder = new ArrayList<>();
		iconSize = 0;
		Set<Integer> uniqueX = new HashSet<>();
		Set<Integer> uniqueY = new HashSet<>();
		for (int index = 0; index < rsInterface.children.length; index++) {
			RSInterface child = RSInterface.interfaceCache[rsInterface.children[index]];
			if (child != null) {
				baseDrawsTransparent[index] = child.drawsTransparent;
				baseTransparency[index] = child.transparency;
			}
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

	private boolean isFilterModeEnabled() {
		return Client.getUserSettings() != null && Client.getUserSettings().isPrayerFilterMode();
	}

	private java.util.Set<Integer> getFilteredPrayerIndices() {
		if (Client.getUserSettings() == null) {
			return Collections.emptySet();
		}
		return Client.getUserSettings().getFilteredPrayerIndices();
	}

	private boolean toggleFilterAt(Client client, int mouseX, int mouseY) {
		RSInterface prayerBook = RSInterface.interfaceCache[PRAYER_INTERFACE_ID];
		if (prayerBook == null || iconOrder == null) {
			return false;
		}
		Rectangle bounds = getContentBounds(client);
		int absoluteX = getBounds().x + mouseX;
		int absoluteY = getBounds().y + mouseY;
		if (!bounds.contains(absoluteX, absoluteY)) {
			return false;
		}
		int localX = absoluteX - bounds.x;
		int localY = absoluteY - bounds.y;
		for (int index : iconOrder) {
			RSInterface child = RSInterface.interfaceCache[prayerBook.children[index]];
			if (child == null || child.interfaceHidden) {
				continue;
			}
			int x = prayerBook.childX[index];
			int y = prayerBook.childY[index];
			int width = child.width;
			int height = child.height;
			if (localX >= x && localX <= x + width && localY >= y && localY <= y + height) {
				java.util.Set<Integer> filtered = getFilteredPrayerIndices();
				if (filtered.contains(index)) {
					filtered.remove(index);
				} else {
					filtered.add(index);
				}
				return true;
			}
		}
		return false;
	}
}
