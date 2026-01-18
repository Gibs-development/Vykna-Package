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
	private static final int ANCHOR_DISTANCE_BUFFER = 6;

	private int[] baseChildX;
	private int[] baseChildY;
	private boolean[] baseDrawsTransparent;
	private int[] baseTransparency;
	private List<Anchor> anchors;
	private int[] childAnchors;
	private int iconSize;
	private int padX = 4;
	private int padY = 4;
	private boolean layoutDirty = true;
	private int lastLayoutWidth = -1;
	private int lastLayoutHeight = -1;
	private int lastClientWidth = -1;
	private int lastClientHeight = -1;
	private int lastInterfaceId = -1;
	private int lastScrollMax = -1;
	private boolean lastRs3Mode;

	public PrayerPanel(int id, int tabIndex, Rectangle bounds, String title) {
		super(id, tabIndex, bounds, title, false, true, 160, 200 + PanelManager.PANEL_HEADER_HEIGHT);
	}

	@Override
	public void draw(Client client) {
		super.draw(client);
		if (isDebugEnabled()) {
			System.out.println("[PrayerLayout] hoverId=" + client.getHoverId()
					+ " mouse=(" + client.getMouseX() + "," + client.getMouseY() + ")"
					+ " scrollOffset=" + scrollOffset);
		}
	}

	@Override
	public boolean handleMouse(Client client, int mouseX, int mouseY) {
		return super.handleMouse(client, mouseX, mouseY);
	}

	@Override
	public void onResize(Client client) {
		layoutDirty = true;
	}

	@Override
	public boolean handleClick(Client client, int mouseX, int mouseY) {
		if (!isFilterModeEnabled()) {
			return super.handleClick(client, mouseX, mouseY);
		}
		if (toggleFilterAt(client, mouseX, mouseY)) {
			client.menuOpen = false;
			client.menuActionRow = 0;
			layoutDirty = true;
		}
		return true;
	}

	@Override
	protected void updateInterfaceLayout(Client client, RSInterface rsInterface, Rectangle bounds) {
		rsInterface.width = bounds.width;
		rsInterface.height = bounds.height;
		RSInterface prayerBook = RSInterface.interfaceCache[PRAYER_INTERFACE_ID];
		boolean rs3Mode = client.isRs3InterfaceStyleActive();
		boolean sizeChanged = bounds.width != lastLayoutWidth || bounds.height != lastLayoutHeight;
		boolean clientSizeChanged = Client.currentGameWidth != lastClientWidth || Client.currentGameHeight != lastClientHeight;
		boolean interfaceChanged = rsInterface.id != lastInterfaceId;
		int currentScrollMax = prayerBook == null ? rsInterface.scrollMax : prayerBook.scrollMax;
		boolean scrollMaxChanged = currentScrollMax != lastScrollMax;
		if (layoutDirty || sizeChanged || clientSizeChanged || interfaceChanged || rs3Mode != lastRs3Mode
				|| scrollMaxChanged) {
			applyPrayerLayout(rsInterface, bounds);
			rsInterface.scrollMax = Math.max(rsInterface.height, getInterfaceContentHeight(rsInterface));
			layoutDirty = false;
			lastLayoutWidth = bounds.width;
			lastLayoutHeight = bounds.height;
			lastClientWidth = Client.currentGameWidth;
			lastClientHeight = Client.currentGameHeight;
			lastInterfaceId = rsInterface.id;
			lastRs3Mode = rs3Mode;
			lastScrollMax = prayerBook == null ? rsInterface.scrollMax : prayerBook.scrollMax;
		}
	}

	private void applyPrayerLayout(RSInterface rsInterface, Rectangle bounds) {
		RSInterface prayerBook = RSInterface.interfaceCache[PRAYER_INTERFACE_ID];
		if (prayerBook == null || prayerBook.children == null) {
			return;
		}
		cacheBaseLayout(prayerBook);
		if (anchors == null || anchors.isEmpty() || iconSize == 0) {
			return;
		}
		boolean filterMode = isFilterModeEnabled();
		Set<Integer> filtered = getFilteredPrayerIndices();
		prayerBook.width = bounds.width;
		prayerBook.height = bounds.height;
		int contentWidth = Math.max(1, bounds.width - CONTENT_PADDING * 2);
		List<Anchor> visibleAnchors = new ArrayList<>();
		for (Anchor anchor : anchors) {
			if (filterMode || !filtered.contains(anchor.childIndex)) {
				visibleAnchors.add(anchor);
			}
		}
		int columns = Math.max(1, (contentWidth + padX) / (iconSize + padX));
		int rows = (int) Math.ceil(visibleAnchors.size() / (double) columns);
		for (int idx = 0; idx < visibleAnchors.size(); idx++) {
			Anchor anchor = visibleAnchors.get(idx);
			int row = idx / columns;
			int col = idx % columns;
			anchor.targetX = CONTENT_PADDING + col * (iconSize + padX);
			anchor.targetY = CONTENT_PADDING + row * (iconSize + padY);
		}
		for (int index = 0; index < prayerBook.children.length; index++) {
			int anchorIndex = childAnchors[index];
			RSInterface child = RSInterface.interfaceCache[prayerBook.children[index]];
			if (anchorIndex >= 0 && anchorIndex < anchors.size()) {
				Anchor anchor = anchors.get(anchorIndex);
				int dx = anchor.targetX - anchor.baseX;
				int dy = anchor.targetY - anchor.baseY;
				prayerBook.childX[index] = baseChildX[index] + dx;
				prayerBook.childY[index] = baseChildY[index] + dy;
				boolean anchorFiltered = filtered.contains(anchor.childIndex);
				if (child != null) {
					child.interfaceHidden = !filterMode && anchorFiltered;
					if (child.type == RSInterface.TYPE_SPRITE) {
						child.drawsTransparent = baseDrawsTransparent[index];
						child.transparency = baseTransparency[index];
						if (filterMode && anchorFiltered) {
							child.drawsTransparent = true;
							child.transparency = FILTERED_TRANSPARENCY;
						}
					}
				}
			} else if (child != null) {
				child.interfaceHidden = false;
			}
		}
		int requiredHeight = rows * iconSize + Math.max(0, rows - 1) * padY + CONTENT_PADDING * 2;
		prayerBook.scrollMax = Math.max(bounds.height, Math.max(requiredHeight, getInterfaceContentHeight(prayerBook)));
		rsInterface.scrollMax = prayerBook.scrollMax;
		if (isDebugEnabled()) {
			System.out.println("[PrayerLayout] bounds=" + bounds.width + "x" + bounds.height
					+ " columns=" + columns + " rows=" + rows
					+ " scrollMax=" + prayerBook.scrollMax + " visibleHeight=" + prayerBook.height);
		}
	}

	private void cacheBaseLayout(RSInterface rsInterface) {
		if (baseChildX != null && baseChildY != null) {
			return;
		}
		baseChildX = rsInterface.childX.clone();
		baseChildY = rsInterface.childY.clone();
		baseDrawsTransparent = new boolean[rsInterface.children.length];
		baseTransparency = new int[rsInterface.children.length];
		anchors = new ArrayList<>();
		childAnchors = new int[rsInterface.children.length];
		for (int i = 0; i < childAnchors.length; i++) {
			childAnchors[i] = -1;
		}
		iconSize = 0;
		Set<Integer> uniqueX = new HashSet<>();
		Set<Integer> uniqueY = new HashSet<>();
		for (int index = 0; index < rsInterface.children.length; index++) {
			RSInterface child = RSInterface.interfaceCache[rsInterface.children[index]];
			if (child != null) {
				baseDrawsTransparent[index] = child.drawsTransparent;
				baseTransparency[index] = child.transparency;
			}
			if (!isPrayerAnchor(child)) {
				continue;
			}
			anchors.add(new Anchor(index, baseChildX[index], baseChildY[index]));
			iconSize = Math.max(iconSize, Math.max(child.width, child.height));
			uniqueX.add(baseChildX[index]);
			uniqueY.add(baseChildY[index]);
		}
		anchors.sort(Comparator.comparingInt((Anchor anchor) -> anchor.baseY)
				.thenComparingInt(anchor -> anchor.baseX));
		for (int index = 0; index < rsInterface.children.length; index++) {
			childAnchors[index] = resolveAnchorIndex(index);
		}
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

	private boolean isPrayerAnchor(RSInterface child) {
		if (!shouldReflowChild(child)) {
			return false;
		}
		return child.atActionType > 0;
	}

	private int resolveAnchorIndex(int childIndex) {
		if (anchors.isEmpty()) {
			return -1;
		}
		int baseX = baseChildX[childIndex];
		int baseY = baseChildY[childIndex];
		int bestIndex = -1;
		int bestDistance = Integer.MAX_VALUE;
		for (int idx = 0; idx < anchors.size(); idx++) {
			Anchor anchor = anchors.get(idx);
			int dx = Math.abs(baseX - anchor.baseX);
			int dy = Math.abs(baseY - anchor.baseY);
			int distance = dx + dy;
			if (distance < bestDistance) {
				bestDistance = distance;
				bestIndex = idx;
			}
		}
		int threshold = iconSize + Math.max(padX, padY) + ANCHOR_DISTANCE_BUFFER;
		if (bestDistance > threshold) {
			return -1;
		}
		return bestIndex;
	}

	private boolean isFilterModeEnabled() {
		return Client.getUserSettings() != null && Client.getUserSettings().isPrayerFilterMode();
	}

	private Set<Integer> getFilteredPrayerIndices() {
		if (Client.getUserSettings() == null) {
			return Collections.emptySet();
		}
		return Client.getUserSettings().getFilteredPrayerIndices();
	}

	private boolean toggleFilterAt(Client client, int mouseX, int mouseY) {
		RSInterface prayerBook = RSInterface.interfaceCache[PRAYER_INTERFACE_ID];
		if (prayerBook == null || anchors == null) {
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
		int scrollPosition = getScrollPosition(RSInterface.interfaceCache[Client.tabInterfaceIDs[getTabIndex()]], bounds);
		localY += scrollPosition;
		for (Anchor anchor : anchors) {
			int index = anchor.childIndex;
			RSInterface child = RSInterface.interfaceCache[prayerBook.children[index]];
			if (child == null || child.interfaceHidden) {
				continue;
			}
			int x = prayerBook.childX[index];
			int y = prayerBook.childY[index];
			int width = child.width;
			int height = child.height;
			if (localX >= x && localX <= x + width && localY >= y && localY <= y + height) {
				Set<Integer> filtered = getFilteredPrayerIndices();
				if (filtered.contains(index)) {
					filtered.remove(index);
				} else {
					filtered.add(index);
				}
				if (isDebugEnabled()) {
					System.out.println("[PrayerLayout] clicked child=" + prayerBook.children[index] + " index=" + index);
				}
				return true;
			}
		}
		return false;
	}

	private boolean isDebugEnabled() {
		return Client.getUserSettings() != null && Client.getUserSettings().isPrayerLayoutDebug();
	}

	private static final class Anchor {
		private final int childIndex;
		private final int baseX;
		private final int baseY;
		private int targetX;
		private int targetY;

		private Anchor(int childIndex, int baseX, int baseY) {
			this.childIndex = childIndex;
			this.baseX = baseX;
			this.baseY = baseY;
			this.targetX = baseX;
			this.targetY = baseY;
		}
	}
}
