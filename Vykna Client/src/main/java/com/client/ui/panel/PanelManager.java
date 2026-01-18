package com.client.ui.panel;

import com.client.Client;
import com.client.DrawingArea;
import com.client.Sprite;
import com.client.graphics.interfaces.RSInterface;
import com.client.sound.Sound;
import com.client.sound.SoundType;
import com.client.utilities.settings.Settings;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PanelManager {
	public static final int PANEL_HEADER_HEIGHT = 18;
	private static final int PANEL_BACKGROUND = 0x141414;
	private static final int PANEL_HEADER = 0x1c1c1c;
	private static final int PANEL_BORDER = 0x2c2c2c;
	private static final int PANEL_TEXT = 0xffffff;
	private static final int RESIZE_HANDLE_SIZE = 12;
	private static final int CLOSE_BUTTON_SIZE = 12;
	private static final int CLOSE_BUTTON_PADDING = 4;
	private static final int DOCK_PREVIEW_ALPHA = 100;
	private static final int DOCK_SNAP_THRESHOLD = 12;
	private static final int GROUP_SNAP_THRESHOLD = 8;
	private static final int GROUP_UNDOCK_THRESHOLD = 6;
	public static final int PANEL_ID_INVENTORY = 1;
	public static final int PANEL_ID_PRAYER = 2;
	public static final int PANEL_ID_MAGIC = 3;
	public static final int PANEL_ID_EQUIPMENT = 4;
	public static final int PANEL_ID_QUEST = 5;
	public static final int PANEL_ID_STATS = 6;
	public static final int PANEL_ID_SKILLS = 7;
	public static final int PANEL_ID_CLAN = 8;
	public static final int PANEL_ID_FRIENDS = 9;
	public static final int PANEL_ID_SETTINGS = 10;
	public static final int PANEL_ID_EMOTES = 11;
	public static final int PANEL_ID_MUSIC = 12;
	public static final int PANEL_ID_NOTES = 13;
	public static final int PANEL_ID_LOGOUT = 14;
	public static final int PANEL_ID_MINIMAP_BASE = 20;
	public static final int PANEL_ID_CHAT = 21;
	public static final int PANEL_ID_TAB_BAR = 22;
	public static final int PANEL_ID_ORBS = 23;
	public static final int PANEL_ID_COMPASS = 24;
	public static final int PANEL_ID_HP_ORB = 25;
	public static final int PANEL_ID_PRAYER_ORB = 26;
	public static final int PANEL_ID_RUN_ORB = 27;
	public static final int PANEL_ID_SPEC_ORB = 28;
	public static final int PANEL_ID_XP_ORB = 29;
	public static final int PANEL_ID_MONEY_POUCH = 30;
	public static final int PANEL_ID_WORLD_MAP = 31;
	public static final int PANEL_ID_TELEPORT = 32;
	public static final int PANEL_ID_XP_PANEL = 33;
	public static final int PANEL_ID_ACTION_BAR = 34;
	public static final int PANEL_ID_RIGHT_STACK = 35;
	private final List<UiPanel> panels = new ArrayList<>();
	private final Map<Integer, Rectangle> preferredBounds = new HashMap<>();
	private int layoutWidth = -1;
	private int layoutHeight = -1;
	private UiPanel activePanel;
	private boolean dragging;
	private boolean resizing;
	private int dragOffsetX;
	private int dragOffsetY;
	private int resizeStartX;
	private int resizeStartY;
	private int resizeStartWidth;
	private int resizeStartHeight;
	private boolean mouseDownLastFrame;
	private Rectangle dragStartBounds;
	private Rectangle resizeStartBounds;
	private boolean invalidPlacement;
	private ResizeHandle resizeHandle;
	private DockCandidate dockCandidate;
	private int nextGroupId = 1000;
	private int dragStartMouseX;
	private int dragStartMouseY;
	private GroupPanel pendingGroupDetach;
	private UiPanel pendingGroupPanel;

	public void ensureRs3Layout(Client client) {
		if (layoutWidth == Client.currentGameWidth && layoutHeight == Client.currentGameHeight && !panels.isEmpty()) {
			return;
		}
		panels.clear();
		preferredBounds.clear();
		PanelLayout.populateRs3Panels(panels);
		for (UiPanel panel : panels) {
			preferredBounds.put(panel.getId(), new Rectangle(panel.getBounds()));
		}
		applySavedLayout(client);
		applySavedGroups(client);
		layoutWidth = Client.currentGameWidth;
		layoutHeight = Client.currentGameHeight;
	}

	public void drawPanels(Client client) {
		for (UiPanel panel : panels) {
			if (panel.isVisible()) {
				if (panel.drawsBackground()) {
					drawPanelBackground(client, panel);
				}
				panel.draw(client);
			}
		}
	}

	public void drawEditOverlays(Client client) {
		if (!client.isRs3EditModeActive()) {
			return;
		}
		for (UiPanel panel : panels) {
			if (!panel.isVisible()) {
				continue;
			}
			if (panel.isClosable() && isHeaderVisible(client, panel)) {
				drawCloseButton(client, panel);
			}
			drawResizeHandle(client, panel);
		}
	}

	public boolean handleMouse(Client client, int mouseX, int mouseY) {
		if (dragging || resizing) {
			return false;
		}
		client.clearOrbHovers();
		for (int index = panels.size() - 1; index >= 0; index--) {
			UiPanel panel = panels.get(index);
			if (!panel.isVisible()) {
				continue;
			}
			Rectangle bounds = panel.getBounds();
			if (panel.contains(mouseX, mouseY)) {
				panel.handleMouse(client, mouseX - bounds.x, mouseY - bounds.y);
				return true;
			}
		}
		return false;
	}

	public boolean handleRightClick(Client client, int mouseX, int mouseY) {
		if (dragging || resizing) {
			return false;
		}
		for (int index = panels.size() - 1; index >= 0; index--) {
			UiPanel panel = panels.get(index);
			if (!panel.isVisible()) {
				continue;
			}
			Rectangle bounds = panel.getBounds();
			if (panel.contains(mouseX, mouseY)) {
				return panel.handleRightClick(client, mouseX - bounds.x, mouseY - bounds.y);
			}
		}
		return false;
	}

	public boolean handleClick(Client client, int mouseX, int mouseY, boolean mouseClicked) {
		if (!mouseClicked || dragging || resizing) {
			return false;
		}
		for (int index = panels.size() - 1; index >= 0; index--) {
			UiPanel panel = panels.get(index);
			if (!panel.isVisible()) {
				continue;
			}
			Rectangle bounds = panel.getBounds();
			if (panel.contains(mouseX, mouseY)) {
				panel.handleClick(client, mouseX - bounds.x, mouseY - bounds.y);
				client.performMenuActionIfAvailable();
				return true;
			}
		}
		return false;
	}

	public void handleEditModeInput(Client client, int mouseX, int mouseY, boolean mouseDown) {
		boolean rs3Mode = client.isRs3InterfaceStyleActive();
		if (!mouseDown && mouseDownLastFrame && (dragging || resizing)) {
			if (rs3Mode && activePanel != null) {
				if (dragging) {
					UiPanel targetPanel = findGroupTarget(activePanel, mouseX, mouseY, client);
					if (targetPanel != null) {
						groupPanels(activePanel, targetPanel);
						dragging = false;
						resizing = false;
						invalidPlacement = false;
						dockCandidate = null;
						resizeHandle = null;
						pendingGroupDetach = null;
						pendingGroupPanel = null;
						saveLayoutToSettings(client);
						mouseDownLastFrame = mouseDown;
						return;
					}
				}
				Rectangle target = new Rectangle(activePanel.getBounds());
				if (!isPlacementValid(target, activePanel)) {
					Rectangle resolved = resolveCollision(target, activePanel);
					if (resolved != null) {
						activePanel.setPosition(resolved.x, resolved.y);
					} else {
						Rectangle fallback = dragStartBounds != null ? dragStartBounds : resizeStartBounds;
						if (fallback != null) {
							activePanel.setPosition(fallback.x, fallback.y);
							activePanel.setSize(fallback.width, fallback.height);
						}
						Sound.getSound().playSound(1042, SoundType.SOUND, 0);
					}
				}
				if (activePanel instanceof GroupPanel) {
					updatePreferredBounds((GroupPanel) activePanel);
				} else {
					preferredBounds.put(activePanel.getId(), new Rectangle(activePanel.getBounds()));
				}
			}
			dragging = false;
			resizing = false;
			invalidPlacement = false;
			dockCandidate = null;
			resizeHandle = null;
			pendingGroupDetach = null;
			pendingGroupPanel = null;
			saveLayoutToSettings(client);
		}

		if (mouseDown && !mouseDownLastFrame) {
			invalidPlacement = false;
			dragStartMouseX = mouseX;
			dragStartMouseY = mouseY;
			pendingGroupDetach = null;
			pendingGroupPanel = null;
			UiPanel hit = getTopmostPanelAt(mouseX, mouseY);
			if (hit != null && hit.drawsBackground() && hit.isClosable()
					&& isHeaderVisible(client, hit) && isOnCloseButton(hit, mouseX, mouseY)) {
				if (hit instanceof BasePanel) {
					((BasePanel) hit).setVisible(false);
				}
				saveLayoutToSettings(client);
				activePanel = null;
				mouseDownLastFrame = mouseDown;
				return;
			}
			ResizeHandle handle = hit != null && hit.resizable() ? getResizeHandle(client, hit, mouseX, mouseY) : null;
			if (handle != null) {
				activePanel = hit;
				bringToFront(hit);
				Rectangle bounds = hit.getBounds();
				resizeStartBounds = new Rectangle(bounds);
				resizeStartX = mouseX;
				resizeStartY = mouseY;
				resizeStartWidth = bounds.width;
				resizeStartHeight = bounds.height;
				resizeHandle = handle;
				dockCandidate = null;
				resizing = true;
			} else if (hit != null && hit.draggable()) {
				if (hit instanceof GroupPanel && isHeaderArea(hit, mouseX, mouseY)) {
					GroupPanel group = (GroupPanel) hit;
					if (group.getPanels().size() > 1) {
						UiPanel candidate = group.getPanelAtTabPosition(mouseX - group.getBounds().x);
						if (candidate != null && candidate != group.getActivePanel()) {
							pendingGroupDetach = group;
							pendingGroupPanel = candidate;
						}
					}
				}
				activePanel = hit;
				bringToFront(hit);
				Rectangle bounds = hit.getBounds();
				dragStartBounds = new Rectangle(bounds);
				dragOffsetX = mouseX - bounds.x;
				dragOffsetY = mouseY - bounds.y;
				dockCandidate = null;
				dragging = true;
			} else {
				activePanel = null;
			}
		}

		if (mouseDown && dragging && activePanel != null) {
			if (pendingGroupPanel != null && pendingGroupDetach != null) {
				int dx = Math.abs(mouseX - dragStartMouseX);
				int dy = Math.abs(mouseY - dragStartMouseY);
				if (dx + dy >= GROUP_UNDOCK_THRESHOLD) {
					UiPanel detached = detachPanelFromGroup(pendingGroupDetach, pendingGroupPanel);
					if (detached != null) {
						activePanel = detached;
						Rectangle bounds = detached.getBounds();
						dragStartBounds = new Rectangle(bounds);
						dragOffsetX = mouseX - bounds.x;
						dragOffsetY = mouseY - bounds.y;
					}
					pendingGroupDetach = null;
					pendingGroupPanel = null;
				} else {
					mouseDownLastFrame = mouseDown;
					return;
				}
			}
			Rectangle bounds = activePanel.getBounds();
			int newX = mouseX - dragOffsetX;
			int newY = mouseY - dragOffsetY;
			newX = clamp(newX, 0, Client.currentGameWidth - bounds.width);
			newY = clamp(newY, 0, Client.currentGameHeight - bounds.height);
			activePanel.setPosition(newX, newY);
			dockCandidate = null;
			invalidPlacement = rs3Mode && !isPlacementValid(activePanel.getBounds(), activePanel);
		}

		if (mouseDown && resizing && activePanel != null) {
			int deltaX = mouseX - resizeStartX;
			int deltaY = mouseY - resizeStartY;
			int newX = resizeStartBounds.x;
			int newY = resizeStartBounds.y;
			int newWidth = resizeStartWidth;
			int newHeight = resizeStartHeight;
			int right = resizeStartBounds.x + resizeStartBounds.width;
			int bottom = resizeStartBounds.y + resizeStartBounds.height;
			if (resizeHandle == ResizeHandle.TOP_LEFT) {
				newX = resizeStartBounds.x + deltaX;
				newY = resizeStartBounds.y + deltaY;
				newX = clamp(newX, 0, right - activePanel.getMinWidth());
				newY = clamp(newY, 0, bottom - activePanel.getMinHeight());
				newWidth = right - newX;
				newHeight = bottom - newY;
			} else if (resizeHandle == ResizeHandle.TOP_RIGHT) {
				newY = resizeStartBounds.y + deltaY;
				newY = clamp(newY, 0, bottom - activePanel.getMinHeight());
				newWidth = resizeStartWidth + deltaX;
				int maxWidth = Math.max(activePanel.getMinWidth(), Client.currentGameWidth - resizeStartBounds.x);
				newWidth = clamp(newWidth, activePanel.getMinWidth(), maxWidth);
				newHeight = bottom - newY;
			}
			if (activePanel.keepAspectRatio()) {
				int size = Math.max(newWidth, newHeight);
				newWidth = size;
				newHeight = size;
				if (resizeHandle == ResizeHandle.TOP_LEFT) {
					newX = right - newWidth;
					newY = bottom - newHeight;
				} else if (resizeHandle == ResizeHandle.TOP_RIGHT) {
					newY = bottom - newHeight;
				}
			}
			if (activePanel instanceof InventoryPanel) {
				Dimension clamped = ((InventoryPanel) activePanel).clampSizeForResize(newWidth, newHeight, client);
				newWidth = clamped.width;
				newHeight = clamped.height;
				if (resizeHandle == ResizeHandle.TOP_LEFT) {
					newX = right - newWidth;
					newY = bottom - newHeight;
				} else if (resizeHandle == ResizeHandle.TOP_RIGHT) {
					newY = bottom - newHeight;
				}
			}
			newWidth = Math.max(activePanel.getMinWidth(), newWidth);
			newHeight = Math.max(activePanel.getMinHeight(), newHeight);
			newX = clamp(newX, 0, Client.currentGameWidth - newWidth);
			newY = clamp(newY, 0, Client.currentGameHeight - newHeight);
			if (resizeHandle == ResizeHandle.TOP_LEFT) {
				newWidth = right - newX;
				newHeight = bottom - newY;
			} else if (resizeHandle == ResizeHandle.TOP_RIGHT) {
				newHeight = bottom - newY;
			}
			newWidth = clamp(newWidth, activePanel.getMinWidth(), Client.currentGameWidth - newX);
			newHeight = clamp(newHeight, activePanel.getMinHeight(), Client.currentGameHeight - newY);
			activePanel.setPosition(newX, newY);
			activePanel.setSize(newWidth, newHeight);
			activePanel.onResize(client);
			invalidPlacement = rs3Mode && !isPlacementValid(activePanel.getBounds(), activePanel);
		}

		mouseDownLastFrame = mouseDown;
	}

	public boolean handleMouseWheel(Client client, int mouseX, int mouseY, int rotation) {
		UiPanel hit = getTopmostPanelAt(mouseX, mouseY);
		if (hit instanceof GroupPanel) {
			hit = ((GroupPanel) hit).getActivePanel();
		}
		if (!(hit instanceof TabPanel)) {
			return false;
		}
		if (!hit.isScrollable()) {
			return false;
		}
		TabPanel tabPanel = (TabPanel) hit;
		int interfaceId = Client.tabInterfaceIDs[tabPanel.getTabIndex()];
		if (interfaceId <= 0) {
			return false;
		}
		RSInterface rsInterface = RSInterface.interfaceCache[interfaceId];
		if (rsInterface == null) {
			return false;
		}
		tabPanel.scrollBy(rotation * 30, rsInterface, tabPanel.getContentBounds(client));
		return true;
	}

	public void resetLayout(Client client) {
		client.getUserSettings().clearRs3PanelLayouts();
		activePanel = null;
		dragging = false;
		mouseDownLastFrame = false;
		layoutWidth = -1;
		layoutHeight = -1;
		panels.clear();
		preferredBounds.clear();
		ensureRs3Layout(client);
	}

	public void reloadLayoutFromSettings(Client client) {
		activePanel = null;
		dragging = false;
		resizing = false;
		mouseDownLastFrame = false;
		layoutWidth = -1;
		layoutHeight = -1;
		panels.clear();
		preferredBounds.clear();
		ensureRs3Layout(client);
	}

	public void saveLayout(Client client) {
		saveLayoutToSettings(client);
	}

	public UiPanel getPanel(int id) {
		for (UiPanel panel : panels) {
			if (panel.getId() == id) {
				return panel;
			}
			if (panel instanceof GroupPanel) {
				UiPanel child = ((GroupPanel) panel).getPanelById(id);
				if (child != null) {
					return child;
				}
			}
		}
		return null;
	}

	public boolean isPanelVisible(int id) {
		UiPanel panel = getPanel(id);
		return panel != null && panel.isVisible();
	}

	public void togglePanelVisibility(int id) {
		GroupPanel parent = getGroupForPanel(id);
		if (parent != null) {
			parent.togglePanelVisibility(id);
			return;
		}
		UiPanel panel = getPanel(id);
		if (!(panel instanceof BasePanel)) {
			return;
		}
		BasePanel basePanel = (BasePanel) panel;
		boolean visible = !basePanel.isVisible();
		basePanel.setVisible(visible);
		if (visible) {
			bringToFront(panel);
		}
	}

	public boolean isDragging() {
		return dragging || resizing;
	}

	public boolean isMouseOverPanel(int mouseX, int mouseY) {
		return getTopmostPanelAt(mouseX, mouseY) != null;
	}

	private UiPanel getTopmostPanelAt(int mouseX, int mouseY) {
		for (int index = panels.size() - 1; index >= 0; index--) {
			UiPanel panel = panels.get(index);
			if (panel.isVisible() && panel.contains(mouseX, mouseY)) {
				return panel;
			}
		}
		return null;
	}

	public void bringToFront(UiPanel panel) {
		panels.remove(panel);
		panels.add(panel);
	}

	private UiPanel findGroupTarget(UiPanel panel, int mouseX, int mouseY, Client client) {
		if (!isGroupable(panel)) {
			return null;
		}
		for (int index = panels.size() - 1; index >= 0; index--) {
			UiPanel other = panels.get(index);
			if (other == panel || !other.isVisible()) {
				continue;
			}
			if (!isGroupable(other)) {
				continue;
			}
			Rectangle header = getGroupHeaderBounds(client, other);
			if (header == null) {
				continue;
			}
			if (header.contains(mouseX, mouseY) && panel.getBounds().intersects(other.getBounds())) {
				return other;
			}
		}
		return null;
	}

	private boolean isGroupable(UiPanel panel) {
		if (panel == null) {
			return false;
		}
		if (panel instanceof GroupPanel) {
			return true;
		}
		return panel instanceof TabPanel;
	}

	private void groupPanels(UiPanel source, UiPanel target) {
		if (source == null || target == null || source == target) {
			return;
		}
		GroupPanel targetGroup = target instanceof GroupPanel ? (GroupPanel) target : null;
		GroupPanel sourceGroup = source instanceof GroupPanel ? (GroupPanel) source : null;
		if (sourceGroup != null && sourceGroup == targetGroup) {
			return;
		}
		if (targetGroup == null && sourceGroup == null) {
			GroupPanel group = new GroupPanel(nextGroupId++, new Rectangle(target.getBounds()));
			panels.remove(target);
			panels.remove(source);
			group.addPanel(target);
			group.addPanel(source);
			group.setActivePanel(source);
			group.syncChildBounds();
			panels.add(group);
			bringToFront(group);
			updatePreferredBounds(group);
			return;
		}
		if (targetGroup != null && sourceGroup == null) {
			panels.remove(source);
			targetGroup.addPanel(source);
			targetGroup.setActivePanel(source);
			targetGroup.syncChildBounds();
			bringToFront(targetGroup);
			updatePreferredBounds(targetGroup);
			return;
		}
		if (targetGroup == null && sourceGroup != null) {
			panels.remove(target);
			sourceGroup.addPanel(target);
			sourceGroup.setActivePanel(target);
			sourceGroup.syncChildBounds();
			bringToFront(sourceGroup);
			updatePreferredBounds(sourceGroup);
			return;
		}
		if (targetGroup != null && sourceGroup != null) {
			for (UiPanel child : new ArrayList<>(sourceGroup.getPanels())) {
				targetGroup.addPanel(child);
			}
			targetGroup.setActivePanel(sourceGroup.getActivePanel());
			panels.remove(sourceGroup);
			targetGroup.syncChildBounds();
			bringToFront(targetGroup);
			updatePreferredBounds(targetGroup);
		}
	}

	private UiPanel detachPanelFromGroup(GroupPanel group, UiPanel panel) {
		if (group == null || panel == null) {
			return null;
		}
		Rectangle groupBounds = new Rectangle(group.getBounds());
		UiPanel removed = group.removePanel(panel);
		if (removed == null) {
			return null;
		}
		removed.setPosition(groupBounds.x, groupBounds.y);
		removed.setSize(groupBounds.width, groupBounds.height);
		panels.add(removed);
		if (group.getPanels().size() == 1) {
			UiPanel remaining = group.getPanels().get(0);
			panels.remove(group);
			remaining.setPosition(groupBounds.x, groupBounds.y);
			remaining.setSize(groupBounds.width, groupBounds.height);
			panels.add(remaining);
			preferredBounds.put(remaining.getId(), new Rectangle(remaining.getBounds()));
		} else if (group.getPanels().isEmpty()) {
			panels.remove(group);
		} else {
			group.syncChildBounds();
			updatePreferredBounds(group);
		}
		preferredBounds.put(removed.getId(), new Rectangle(removed.getBounds()));
		bringToFront(removed);
		return removed;
	}

	private boolean isHeaderArea(UiPanel panel, int mouseX, int mouseY) {
		Rectangle header = getGroupHeaderBounds(null, panel);
		return header != null && header.contains(mouseX, mouseY);
	}

	private Rectangle getGroupHeaderBounds(Client client, UiPanel panel) {
		if (panel == null) {
			return null;
		}
		Rectangle bounds = panel.getBounds();
		int headerHeight = PANEL_HEADER_HEIGHT;
		Rectangle header = new Rectangle(bounds.x, bounds.y, bounds.width, headerHeight);
		if (GROUP_SNAP_THRESHOLD > 0) {
			return new Rectangle(header.x - GROUP_SNAP_THRESHOLD, header.y - GROUP_SNAP_THRESHOLD,
					header.width + GROUP_SNAP_THRESHOLD * 2, header.height + GROUP_SNAP_THRESHOLD * 2);
		}
		return header;
	}

	private void updatePreferredBounds(GroupPanel group) {
		if (group == null) {
			return;
		}
		for (UiPanel panel : group.getPanels()) {
			preferredBounds.put(panel.getId(), new Rectangle(group.getBounds()));
		}
	}

	private GroupPanel getGroupForPanel(int panelId) {
		for (UiPanel panel : panels) {
			if (!(panel instanceof GroupPanel)) {
				continue;
			}
			GroupPanel group = (GroupPanel) panel;
			if (group.getPanelById(panelId) != null) {
				return group;
			}
		}
		return null;
	}

	private void applySavedLayout(Client client) {
		Settings settings = Client.getUserSettings();
		if (settings == null) {
			return;
		}
		for (UiPanel panel : panels) {
			Settings.Rs3PanelLayout layout = settings.getRs3PanelLayouts().get(panel.getId());
			if (layout == null) {
				preferredBounds.put(panel.getId(), new Rectangle(panel.getBounds()));
				continue;
			}
			Rectangle preferred = new Rectangle(layout.getX(), layout.getY(), layout.getWidth(), layout.getHeight());
			preferredBounds.put(panel.getId(), preferred);
			int width = clamp(preferred.width, panel.getMinWidth(), Client.currentGameWidth);
			int height = clamp(preferred.height, panel.getMinHeight(), Client.currentGameHeight);
			panel.getBounds().setSize(width, height);
			int clampedX = clamp(preferred.x, 0, Client.currentGameWidth - panel.getBounds().width);
			int clampedY = clamp(preferred.y, 0, Client.currentGameHeight - panel.getBounds().height);
			panel.setPosition(clampedX, clampedY);
			if (panel instanceof BasePanel) {
				boolean visible = layout.isVisible() || !panel.isClosable();
				((BasePanel) panel).setVisible(visible);
			}
		}
	}

	private void applySavedGroups(Client client) {
		Settings settings = Client.getUserSettings();
		if (settings == null) {
			return;
		}
		Map<Integer, Integer> groupMap = settings.getRs3PanelGroups();
		if (groupMap.isEmpty()) {
			return;
		}
		Map<Integer, List<UiPanel>> groups = new HashMap<>();
		for (UiPanel panel : new ArrayList<>(panels)) {
			Integer groupId = groupMap.get(panel.getId());
			if (groupId == null) {
				continue;
			}
			groups.computeIfAbsent(groupId, key -> new ArrayList<>()).add(panel);
		}
		for (Map.Entry<Integer, List<UiPanel>> entry : groups.entrySet()) {
			List<UiPanel> groupPanels = entry.getValue();
			if (groupPanels.size() < 2) {
				continue;
			}
			GroupPanel group = new GroupPanel(entry.getKey(), new Rectangle(groupPanels.get(0).getBounds()));
			for (UiPanel panel : groupPanels) {
				panels.remove(panel);
				group.addPanel(panel);
			}
			Integer activePanelId = settings.getRs3GroupActivePanels().get(entry.getKey());
			if (activePanelId != null) {
				group.setActivePanelById(activePanelId);
			}
			group.syncChildBounds();
			panels.add(group);
			nextGroupId = Math.max(nextGroupId, entry.getKey() + 1);
		}
	}

	private void saveLayoutToSettings(Client client) {
		Settings settings = Client.getUserSettings();
		if (settings == null) {
			return;
		}
		settings.getRs3PanelGroups().clear();
		settings.getRs3GroupActivePanels().clear();
		for (UiPanel panel : panels) {
			if (panel instanceof GroupPanel) {
				GroupPanel group = (GroupPanel) panel;
				Rectangle bounds = new Rectangle(group.getBounds());
				for (UiPanel child : group.getPanels()) {
					settings.getRs3PanelLayouts().put(child.getId(), new Settings.Rs3PanelLayout(
							bounds.x, bounds.y, bounds.width, bounds.height, child.isVisible()));
					settings.getRs3PanelGroups().put(child.getId(), group.getGroupId());
				}
				settings.getRs3GroupActivePanels().put(group.getGroupId(), group.getActivePanelId());
				continue;
			}
			Rectangle bounds = preferredBounds.getOrDefault(panel.getId(), panel.getBounds());
			settings.getRs3PanelLayouts().put(panel.getId(), new Settings.Rs3PanelLayout(
					bounds.x, bounds.y, bounds.width, bounds.height, panel.isVisible()));
		}
	}

	private void drawSelectionOutline(Rectangle bounds) {
		int highlight = invalidPlacement ? 0xd1362b : 0xffd24a;
		DrawingArea.drawPixels(1, bounds.y, bounds.x, highlight, bounds.width);
		DrawingArea.drawPixels(1, bounds.y + bounds.height - 1, bounds.x, highlight, bounds.width);
		DrawingArea.drawPixels(bounds.height, bounds.y, bounds.x, highlight, 1);
		DrawingArea.drawPixels(bounds.height, bounds.y, bounds.x + bounds.width - 1, highlight, 1);
	}

	private boolean isPlacementValid(Rectangle bounds, UiPanel ignore) {
		if (bounds.x < 0 || bounds.y < 0) {
			return false;
		}
		if (bounds.x + bounds.width > Client.currentGameWidth) {
			return false;
		}
		if (bounds.y + bounds.height > Client.currentGameHeight) {
			return false;
		}
		for (UiPanel panel : panels) {
			if (panel == ignore || !panel.isVisible()) {
				continue;
			}
			if (bounds.intersects(panel.getBounds())) {
				return false;
			}
		}
		return true;
	}

	private Rectangle resolveCollision(Rectangle bounds, UiPanel ignore) {
		if (isPlacementValid(bounds, ignore)) {
			return bounds;
		}
		int step = 8;
		int maxRadius = Math.max(Client.currentGameWidth, Client.currentGameHeight);
		int baseX = bounds.x;
		int baseY = bounds.y;
		for (int radius = step; radius <= maxRadius; radius += step) {
			int left = baseX - radius;
			int right = baseX + radius;
			int top = baseY - radius;
			int bottom = baseY + radius;
			Rectangle candidate = new Rectangle(bounds);
			int[] xs = { left, baseX, right, baseX };
			int[] ys = { baseY, top, baseY, bottom };
			for (int index = 0; index < xs.length; index++) {
				candidate.setLocation(xs[index], ys[index]);
				candidate.x = clamp(candidate.x, 0, Client.currentGameWidth - candidate.width);
				candidate.y = clamp(candidate.y, 0, Client.currentGameHeight - candidate.height);
				if (isPlacementValid(candidate, ignore)) {
					return new Rectangle(candidate);
				}
			}
		}
		return null;
	}

	private static int clamp(int value, int min, int max) {
		if (value < min) {
			return min;
		}
		if (value > max) {
			return max;
		}
		return value;
	}

	private static final class PanelLayout {
		private static final int PANEL_WIDTH = 190;
		private static final int PANEL_HEIGHT = 260 + PANEL_HEADER_HEIGHT;
		private static final int PANEL_PADDING = 8;
		private static final int PANEL_MARGIN = 10;
		private static final int MINIMAP_PANEL_WIDTH = 200;
		private static final int MINIMAP_PANEL_HEIGHT = 200 + PANEL_HEADER_HEIGHT;
		private static final int COMPASS_SIZE = 36;
		private static final int ORB_SIZE = 52;
		private static final int XP_BUTTON_WIDTH = 24;
		private static final int XP_BUTTON_HEIGHT = 20;
		private static final int MONEY_POUCH_WIDTH = 70;
		private static final int MONEY_POUCH_HEIGHT = 34;
		private static final int WORLD_MAP_SIZE = 30;
		private static final int TELEPORT_WIDTH = 20;
		private static final int TELEPORT_HEIGHT = 20;
		private static final int XP_PANEL_WIDTH = 130;
		private static final int XP_PANEL_HEIGHT = 28;
		private static final int ACTION_BAR_WIDTH = 520;
		private static final int ACTION_BAR_HEIGHT = 96;
		private static final int CHAT_PANEL_WIDTH = 516;
		private static final int CHAT_PANEL_HEIGHT = 165 + PANEL_HEADER_HEIGHT;
		private static final int TAB_BAR_PANEL_WIDTH = 76;
		private static final int TAB_BAR_PANEL_HEIGHT = 7 * 36 + PANEL_HEADER_HEIGHT;

		private static void populateRs3Panels(List<UiPanel> panels) {
			int baseX = Math.max(0, Client.currentGameWidth - PANEL_WIDTH - PANEL_MARGIN);
			int inventoryY = Math.max(0, Client.currentGameHeight - PANEL_HEIGHT - PANEL_MARGIN);
			int prayerY = inventoryY - PANEL_HEIGHT - PANEL_PADDING;
			int magicY = prayerY - PANEL_HEIGHT - PANEL_PADDING;

			if (magicY < PANEL_MARGIN) {
				int shiftDown = PANEL_MARGIN - magicY;
				magicY += shiftDown;
				prayerY += shiftDown;
				inventoryY += shiftDown;
			}

			int bottomOverflow = inventoryY + PANEL_HEIGHT + PANEL_MARGIN - Client.currentGameHeight;
			if (bottomOverflow > 0) {
				magicY -= bottomOverflow;
				prayerY -= bottomOverflow;
				inventoryY -= bottomOverflow;
			}

			int minimapX = Math.max(PANEL_MARGIN, Client.currentGameWidth - MINIMAP_PANEL_WIDTH - PANEL_MARGIN);
			int minimapY = PANEL_MARGIN;
			int orbsX = minimapX - PANEL_PADDING;
			int orbsContentY = minimapY + MINIMAP_PANEL_HEIGHT + PANEL_PADDING + PANEL_HEADER_HEIGHT;
			int chatX = PANEL_MARGIN;
			int chatY = Math.max(PANEL_MARGIN, Client.currentGameHeight - CHAT_PANEL_HEIGHT - PANEL_MARGIN);
			panels.add(new MinimapBasePanel(PANEL_ID_MINIMAP_BASE, new Rectangle(minimapX, minimapY, MINIMAP_PANEL_WIDTH, MINIMAP_PANEL_HEIGHT)));
			panels.add(new CompassPanel(PANEL_ID_COMPASS, new Rectangle(minimapX + 6, minimapY + PANEL_HEADER_HEIGHT + 6, COMPASS_SIZE, COMPASS_SIZE)));
			panels.add(new HpOrbPanel(PANEL_ID_HP_ORB, new Rectangle(orbsX + 7, orbsContentY + 41, ORB_SIZE, ORB_SIZE)));
			panels.add(new PrayerOrbPanel(PANEL_ID_PRAYER_ORB, new Rectangle(orbsX + 7, orbsContentY + 75, ORB_SIZE, ORB_SIZE)));
			panels.add(new RunOrbPanel(PANEL_ID_RUN_ORB, new Rectangle(orbsX + 31, orbsContentY + 132, ORB_SIZE, 30)));
			panels.add(new SpecialOrbPanel(PANEL_ID_SPEC_ORB, new Rectangle(orbsX + 37, orbsContentY + 139, ORB_SIZE, ORB_SIZE)));
			panels.add(new XpOrbPanel(PANEL_ID_XP_ORB, new Rectangle(orbsX + 12, orbsContentY + 27, XP_BUTTON_WIDTH, XP_BUTTON_HEIGHT)));
			panels.add(new MoneyPouchPanel(PANEL_ID_MONEY_POUCH, new Rectangle(orbsX + 152, orbsContentY + 154, MONEY_POUCH_WIDTH, MONEY_POUCH_HEIGHT)));
			panels.add(new WorldMapPanel(PANEL_ID_WORLD_MAP, new Rectangle(orbsX + 183, orbsContentY + 143, WORLD_MAP_SIZE, WORLD_MAP_SIZE)));
			panels.add(new TeleportPanel(PANEL_ID_TELEPORT, new Rectangle(orbsX + 123, orbsContentY + 160, TELEPORT_WIDTH, TELEPORT_HEIGHT)));
			panels.add(new XpPanel(PANEL_ID_XP_PANEL, new Rectangle(Client.currentGameWidth - 365, PANEL_MARGIN, XP_PANEL_WIDTH, XP_PANEL_HEIGHT)));
			panels.add(new ActionBarPanel(PANEL_ID_ACTION_BAR, new Rectangle(
					Math.max(PANEL_MARGIN, (Client.currentGameWidth - ACTION_BAR_WIDTH) / 2),
					Math.max(PANEL_MARGIN, Client.currentGameHeight - ACTION_BAR_HEIGHT - PANEL_MARGIN),
					ACTION_BAR_WIDTH, ACTION_BAR_HEIGHT)));
			int tabBarX = Math.max(PANEL_MARGIN, minimapX - TAB_BAR_PANEL_WIDTH - PANEL_PADDING);
			int tabBarY = minimapY;
			panels.add(new TabBarPanel(PANEL_ID_TAB_BAR, new Rectangle(tabBarX, tabBarY, TAB_BAR_PANEL_WIDTH, TAB_BAR_PANEL_HEIGHT)));
			panels.add(new TabPanel(PANEL_ID_QUEST, 0, new Rectangle(baseX, magicY, PANEL_WIDTH, PANEL_HEIGHT), "Quest", false, true, 160, 200 + PANEL_HEADER_HEIGHT));
			panels.add(new TabPanel(PANEL_ID_STATS, 1, new Rectangle(baseX, magicY, PANEL_WIDTH, PANEL_HEIGHT), "Stats", false, true, 160, 200 + PANEL_HEADER_HEIGHT));
			panels.add(new TabPanel(PANEL_ID_SKILLS, 2, new Rectangle(baseX, prayerY, PANEL_WIDTH, PANEL_HEIGHT), "Skills", false, true, 160, 200 + PANEL_HEADER_HEIGHT));
			panels.add(new InventoryPanel(PANEL_ID_INVENTORY, new Rectangle(baseX, inventoryY, PANEL_WIDTH, PANEL_HEIGHT)));
			panels.add(new TabPanel(PANEL_ID_EQUIPMENT, 4, new Rectangle(baseX, inventoryY, PANEL_WIDTH, PANEL_HEIGHT), "Equipment", false, true, 160, 200 + PANEL_HEADER_HEIGHT));
			panels.add(new PrayerPanel(PANEL_ID_PRAYER, 5, new Rectangle(baseX, prayerY, PANEL_WIDTH, PANEL_HEIGHT), "Prayer"));
			panels.add(new TabPanel(PANEL_ID_MAGIC, 6, new Rectangle(baseX, magicY, PANEL_WIDTH, PANEL_HEIGHT), "Magic", false, true, 160, 200 + PANEL_HEADER_HEIGHT));
			panels.add(new TabPanel(PANEL_ID_CLAN, 7, new Rectangle(baseX, prayerY, PANEL_WIDTH, PANEL_HEIGHT), "Clan", false, true, 160, 200 + PANEL_HEADER_HEIGHT));
			panels.add(new TabPanel(PANEL_ID_FRIENDS, 8, new Rectangle(baseX, prayerY, PANEL_WIDTH, PANEL_HEIGHT), "Friends", false, true, 160, 200 + PANEL_HEADER_HEIGHT));
			panels.add(new TabPanel(PANEL_ID_SETTINGS, 9, new Rectangle(baseX, inventoryY, PANEL_WIDTH, PANEL_HEIGHT), "Settings", false, true, 160, 200 + PANEL_HEADER_HEIGHT));
			panels.add(new TabPanel(PANEL_ID_EMOTES, 10, new Rectangle(baseX, inventoryY, PANEL_WIDTH, PANEL_HEIGHT), "Emotes", false, true, 160, 200 + PANEL_HEADER_HEIGHT));
			panels.add(new TabPanel(PANEL_ID_MUSIC, 11, new Rectangle(baseX, inventoryY, PANEL_WIDTH, PANEL_HEIGHT), "Music", false, true, 160, 200 + PANEL_HEADER_HEIGHT));
			panels.add(new TabPanel(PANEL_ID_NOTES, 12, new Rectangle(baseX, inventoryY, PANEL_WIDTH, PANEL_HEIGHT), "Notes", false, true, 160, 200 + PANEL_HEADER_HEIGHT));
			panels.add(new TabPanel(PANEL_ID_LOGOUT, 13, new Rectangle(baseX, inventoryY, PANEL_WIDTH, PANEL_HEIGHT), "Logout", false, true, 160, 200 + PANEL_HEADER_HEIGHT));
			panels.add(new ChatPanel(PANEL_ID_CHAT, new Rectangle(chatX, chatY, CHAT_PANEL_WIDTH, CHAT_PANEL_HEIGHT)));
		}
	}

	static class GroupPanel implements UiPanel {
		private final int groupId;
		private final Rectangle bounds;
		private final List<UiPanel> panels = new ArrayList<>();
		private UiPanel activePanel;

		GroupPanel(int groupId, Rectangle bounds) {
			this.groupId = groupId;
			this.bounds = bounds;
		}

		int getGroupId() {
			return groupId;
		}

		UiPanel getActivePanel() {
			return activePanel;
		}

		int getActivePanelId() {
			return activePanel != null ? activePanel.getId() : -1;
		}

		List<UiPanel> getPanels() {
			return panels;
		}

		UiPanel getPanelById(int id) {
			for (UiPanel panel : panels) {
				if (panel.getId() == id) {
					return panel;
				}
			}
			return null;
		}

		void addPanel(UiPanel panel) {
			if (panel == null || panels.contains(panel)) {
				return;
			}
			panels.add(panel);
			if (activePanel == null) {
				activePanel = panel;
			}
			syncChildBounds();
		}

		UiPanel removePanel(UiPanel panel) {
			if (panel == null || !panels.remove(panel)) {
				return null;
			}
			if (panel == activePanel) {
				activePanel = panels.isEmpty() ? null : panels.get(0);
			}
			return panel;
		}

		void setActivePanel(UiPanel panel) {
			if (panel == null || !panels.contains(panel)) {
				return;
			}
			activePanel = panel;
		}

		void setActivePanelById(int panelId) {
			UiPanel panel = getPanelById(panelId);
			if (panel != null) {
				setActivePanel(panel);
			}
		}

		void togglePanelVisibility(int panelId) {
			UiPanel panel = getPanelById(panelId);
			if (!(panel instanceof BasePanel)) {
				return;
			}
			BasePanel basePanel = (BasePanel) panel;
			boolean visible = !basePanel.isVisible();
			basePanel.setVisible(visible);
			if (visible) {
				setActivePanel(panel);
			} else if (panel == activePanel) {
				for (UiPanel candidate : panels) {
					if (candidate.isVisible()) {
						setActivePanel(candidate);
						break;
					}
				}
			}
		}

		void syncChildBounds() {
			Rectangle contentBounds = getContentBounds();
			for (UiPanel panel : panels) {
				panel.setPosition(contentBounds.x, contentBounds.y);
				panel.setSize(contentBounds.width, contentBounds.height);
			}
		}

		@Override
		public int getId() {
			return groupId;
		}

		@Override
		public Rectangle getBounds() {
			return bounds;
		}

		@Override
		public boolean isVisible() {
			for (UiPanel panel : panels) {
				if (panel.isVisible()) {
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean draggable() {
			return true;
		}

		@Override
		public String getTitle() {
			return activePanel != null ? activePanel.getTitle() : "";
		}

		@Override
		public boolean contains(int mouseX, int mouseY) {
			return bounds.contains(mouseX, mouseY);
		}

		@Override
		public void setPosition(int x, int y) {
			bounds.setLocation(x, y);
			syncChildBounds();
		}

		@Override
		public void setSize(int width, int height) {
			bounds.setSize(width, height);
			syncChildBounds();
		}

		@Override
		public void draw(Client client) {
			if (activePanel != null) {
				activePanel.draw(client);
			}
			drawTabs(client);
		}

		@Override
		public boolean handleMouse(Client client, int mouseX, int mouseY) {
			if (activePanel == null) {
				return false;
			}
			int headerHeight = getPanelHeaderHeight(client, this);
			if (mouseY < headerHeight) {
				return false;
			}
			Rectangle childBounds = activePanel.getBounds();
			return activePanel.handleMouse(client, mouseX - (childBounds.x - bounds.x), mouseY - (childBounds.y - bounds.y));
		}

		@Override
		public boolean handleClick(Client client, int mouseX, int mouseY) {
			if (panels.isEmpty()) {
				return false;
			}
			int headerHeight = getPanelHeaderHeight(client, this);
			if (mouseY < headerHeight) {
				UiPanel selected = getPanelAtTabIndex(getTabIndexAt(mouseX));
				if (selected != null && selected != activePanel) {
					setActivePanel(selected);
					if (client != null) {
						client.getPanelManager().saveLayout(client);
					}
				}
				return true;
			}
			if (activePanel == null) {
				return false;
			}
			Rectangle childBounds = activePanel.getBounds();
			return activePanel.handleClick(client, mouseX - (childBounds.x - bounds.x), mouseY - (childBounds.y - bounds.y));
		}

		@Override
		public boolean handleRightClick(Client client, int mouseX, int mouseY) {
			if (activePanel == null) {
				return false;
			}
			int headerHeight = getPanelHeaderHeight(client, this);
			if (mouseY < headerHeight) {
				return false;
			}
			Rectangle childBounds = activePanel.getBounds();
			return activePanel.handleRightClick(client, mouseX - (childBounds.x - bounds.x), mouseY - (childBounds.y - bounds.y));
		}

		@Override
		public boolean resizable() {
			return activePanel != null && activePanel.resizable();
		}

		@Override
		public int getMinWidth() {
			int minWidth = 0;
			for (UiPanel panel : panels) {
				minWidth = Math.max(minWidth, panel.getMinWidth());
			}
			return minWidth;
		}

		@Override
		public int getMinHeight() {
			int minHeight = 0;
			for (UiPanel panel : panels) {
				minHeight = Math.max(minHeight, panel.getMinHeight());
			}
			return minHeight;
		}

		@Override
		public boolean keepAspectRatio() {
			return activePanel != null && activePanel.keepAspectRatio();
		}

		@Override
		public boolean drawsBackground() {
			return true;
		}

		@Override
		public boolean isClosable() {
			return false;
		}

		@Override
		public boolean isScrollable() {
			return activePanel != null && activePanel.isScrollable();
		}

		@Override
		public void onResize(Client client) {
			if (activePanel != null) {
				activePanel.onResize(client);
			}
		}

		private Rectangle getContentBounds() {
			int headerHeight = PANEL_HEADER_HEIGHT;
			int contentHeight = Math.max(0, bounds.height - headerHeight);
			return new Rectangle(bounds.x, bounds.y + headerHeight, bounds.width, contentHeight);
		}

		private void drawTabs(Client client) {
			if (client == null || panels.size() < 2) {
				return;
			}
			int headerHeight = getPanelHeaderHeight(client, this);
			int tabCount = panels.size();
			int baseWidth = bounds.width / tabCount;
			int remainder = bounds.width % tabCount;
			int x = bounds.x;
			for (int index = 0; index < tabCount; index++) {
				int width = baseWidth + (index < remainder ? 1 : 0);
				UiPanel panel = panels.get(index);
				boolean active = panel == activePanel;
				int color = active ? adjustColor(PANEL_HEADER, 12) : PANEL_HEADER;
				DrawingArea.drawPixels(headerHeight, bounds.y, x, color, width);
				DrawingArea.drawPixels(1, bounds.y + headerHeight - 1, x, PANEL_BORDER, width);
				String title = panel.getTitle();
				if (title != null) {
					int textColor = active ? 0xffd24a : PANEL_TEXT;
					client.newSmallFont.drawCenteredString(title, x + width / 2, bounds.y + 13, textColor, 0);
				}
				x += width;
			}
		}

		private int getTabIndexAt(int mouseX) {
			if (panels.isEmpty()) {
				return -1;
			}
			int tabCount = panels.size();
			int baseWidth = bounds.width / tabCount;
			int remainder = bounds.width % tabCount;
			int x = 0;
			for (int index = 0; index < tabCount; index++) {
				int width = baseWidth + (index < remainder ? 1 : 0);
				if (mouseX >= x && mouseX < x + width) {
					return index;
				}
				x += width;
			}
			return tabCount - 1;
		}

		private UiPanel getPanelAtTabIndex(int index) {
			if (index < 0 || index >= panels.size()) {
				return null;
			}
			return panels.get(index);
		}

		UiPanel getPanelAtTabPosition(int mouseX) {
			return getPanelAtTabIndex(getTabIndexAt(mouseX));
		}
	}

	static class BasePanel implements UiPanel {
		private final int id;
		private final Rectangle bounds;
		private boolean visible;
		private final boolean draggable;
		private final String title;
		private final boolean resizable;
		private final int minWidth;
		private final int minHeight;
		private final boolean keepAspectRatio;

		BasePanel(int id, Rectangle bounds, boolean visible, boolean draggable, String title) {
			this(id, bounds, visible, draggable, title, false, bounds.width, bounds.height, false);
		}

		BasePanel(int id, Rectangle bounds, boolean visible, boolean draggable, String title,
				 boolean resizable, int minWidth, int minHeight, boolean keepAspectRatio) {
			this.id = id;
			this.bounds = bounds;
			this.visible = visible;
			this.draggable = draggable;
			this.title = title;
			this.resizable = resizable;
			this.minWidth = minWidth;
			this.minHeight = minHeight;
			this.keepAspectRatio = keepAspectRatio;
		}

		@Override
		public int getId() {
			return id;
		}

		@Override
		public Rectangle getBounds() {
			return bounds;
		}

		@Override
		public boolean isVisible() {
			return visible;
		}

		@Override
		public boolean draggable() {
			return draggable;
		}

		@Override
		public String getTitle() {
			return title;
		}

		@Override
		public boolean contains(int mouseX, int mouseY) {
			return bounds.contains(mouseX, mouseY);
		}

		@Override
		public void setPosition(int x, int y) {
			bounds.setLocation(x, y);
		}

		@Override
		public void setSize(int width, int height) {
			bounds.setSize(width, height);
		}

		@Override
		public void draw(Client client) {

		}

		@Override
		public boolean handleMouse(Client client, int mouseX, int mouseY) {
			return false;
		}

		private void setVisible(boolean visible) {
			this.visible = visible;
		}

		@Override
		public boolean handleClick(Client client, int mouseX, int mouseY) {
			return false;
		}

		@Override
		public boolean resizable() {
			return resizable;
		}

		@Override
		public int getMinWidth() {
			return minWidth;
		}

		@Override
		public int getMinHeight() {
			return minHeight;
		}

		@Override
		public boolean keepAspectRatio() {
			return keepAspectRatio;
		}

		@Override
		public boolean drawsBackground() {
			return true;
		}

		@Override
		public boolean isClosable() {
			return true;
		}

		@Override
		public boolean isScrollable() {
			return true;
		}
	}

	static class TabPanel extends BasePanel {
		private final int tabIndex;
		protected int scrollOffset;

		private TabPanel(int id, int tabIndex, Rectangle bounds, String title, boolean visible) {
			super(id, bounds, visible, true, title);
			this.tabIndex = tabIndex;
		}

		TabPanel(int id, int tabIndex, Rectangle bounds, String title, boolean visible, boolean resizable, int minWidth, int minHeight) {
			super(id, bounds, visible, true, title, resizable, minWidth, minHeight, false);
			this.tabIndex = tabIndex;
		}

		@Override
		public void draw(Client client) {
			int interfaceId = Client.tabInterfaceIDs[tabIndex];
			if (interfaceId <= 0) {
				return;
			}
			RSInterface rsInterface = RSInterface.interfaceCache[interfaceId];
			if (rsInterface == null) {
				return;
			}
			Rectangle contentBounds = getContentBounds(client);
			updateInterfaceLayout(client, rsInterface, contentBounds);
			int scrollPosition = getScrollPosition(rsInterface, contentBounds);
			int clipLeft = DrawingArea.topX;
			int clipTop = DrawingArea.topY;
			int clipRight = DrawingArea.bottomX;
			int clipBottom = DrawingArea.bottomY;

			DrawingArea.setDrawingArea(contentBounds.y + contentBounds.height, contentBounds.x, contentBounds.x + contentBounds.width, contentBounds.y);
			client.pushUiOffset(contentBounds.x, contentBounds.y);
			client.drawInterfaceWithOffset(scrollPosition, 0, rsInterface, 0);
			client.popUiOffset();
			DrawingArea.setDrawingArea(clipBottom, clipLeft, clipRight, clipTop);
			if (needsScroll(rsInterface, contentBounds)) {
				int scrollHeight = contentBounds.height;
				int scrollMax = getContentHeight(rsInterface);
				client.drawScrollbar(scrollHeight, scrollOffset, contentBounds.y, contentBounds.x + contentBounds.width - 16, scrollMax);
			}
		}

		@Override
		public boolean handleMouse(Client client, int mouseX, int mouseY) {
			int interfaceId = Client.tabInterfaceIDs[tabIndex];
			if (interfaceId <= 0) {
				return false;
			}
			RSInterface rsInterface = RSInterface.interfaceCache[interfaceId];
			if (rsInterface == null) {
				return false;
			}
			Rectangle contentBounds = getContentBounds(client);
			int adjustedMouseY = mouseY - (contentBounds.y - getBounds().y);
			int adjustedMouseX = mouseX - (contentBounds.x - getBounds().x);
			if (adjustedMouseY < 0 || adjustedMouseX < 0) {
				return false;
			}
			updateInterfaceLayout(client, rsInterface, contentBounds);
			client.pushUiOffset(contentBounds.x, contentBounds.y);
			client.buildInterfaceMenuWithOffset(0, rsInterface, adjustedMouseX, 0, adjustedMouseY, getScrollPosition(rsInterface, contentBounds));
			client.popUiOffset();
			return true;
		}

		@Override
		public boolean handleClick(Client client, int mouseX, int mouseY) {
			return true;
		}

		@Override
		public boolean handleRightClick(Client client, int mouseX, int mouseY) {
			int interfaceId = Client.tabInterfaceIDs[tabIndex];
			if (interfaceId <= 0) {
				return false;
			}
			RSInterface rsInterface = RSInterface.interfaceCache[interfaceId];
			if (rsInterface == null) {
				return false;
			}
			Rectangle contentBounds = getContentBounds(client);
			int adjustedMouseY = mouseY - (contentBounds.y - getBounds().y);
			int adjustedMouseX = mouseX - (contentBounds.x - getBounds().x);
			if (adjustedMouseY < 0 || adjustedMouseX < 0) {
				return false;
			}
			updateInterfaceLayout(client, rsInterface, contentBounds);
			client.pushUiOffset(contentBounds.x, contentBounds.y);
			client.buildInterfaceMenuWithOffset(0, rsInterface, adjustedMouseX, 0, adjustedMouseY, getScrollPosition(rsInterface, contentBounds));
			client.popUiOffset();
			return true;
		}

		private boolean needsScroll(RSInterface rsInterface, Rectangle bounds) {
			if (!isScrollable()) {
				return false;
			}
			return getContentHeight(rsInterface) > bounds.height;
		}

		private int getContentHeight(RSInterface rsInterface) {
			return Math.max(rsInterface.height, rsInterface.scrollMax);
		}

		protected int getInterfaceContentHeight(RSInterface rsInterface) {
			int maxHeight = rsInterface.height;
			if (rsInterface.children == null) {
				return maxHeight;
			}
			for (int index = 0; index < rsInterface.children.length; index++) {
				RSInterface child = RSInterface.interfaceCache[rsInterface.children[index]];
				if (child == null) {
					continue;
				}
				int childBottom = rsInterface.childY[index] + child.height + child.anInt265;
				if (childBottom > maxHeight) {
					maxHeight = childBottom;
				}
			}
			return maxHeight;
		}

		protected int getScrollPosition(RSInterface rsInterface, Rectangle bounds) {
			if (!needsScroll(rsInterface, bounds)) {
				scrollOffset = 0;
				return 0;
			}
			int maxScroll = Math.max(0, getContentHeight(rsInterface) - bounds.height);
			scrollOffset = clamp(scrollOffset, 0, maxScroll);
			return scrollOffset;
		}

		void scrollBy(int delta, RSInterface rsInterface, Rectangle bounds) {
			if (!needsScroll(rsInterface, bounds)) {
				scrollOffset = 0;
				return;
			}
			int maxScroll = Math.max(0, getContentHeight(rsInterface) - bounds.height);
			scrollOffset = clamp(scrollOffset + delta, 0, maxScroll);
		}

		int getTabIndex() {
			return tabIndex;
		}

		protected void updateInterfaceLayout(Client client, RSInterface rsInterface, Rectangle bounds) {
			rsInterface.width = bounds.width;
			rsInterface.height = bounds.height;
		}

		protected int getContentPadding(Client client, Rectangle bounds) {
			return 0;
		}

		Rectangle getContentBounds(Client client) {
			Rectangle bounds = getBounds();
			int headerHeight = PanelManager.getPanelHeaderHeight(client, this);
			int padding = getContentPadding(client, bounds);
			return new Rectangle(
					bounds.x + padding,
					bounds.y + headerHeight + padding,
					Math.max(0, bounds.width - padding * 2),
					Math.max(0, bounds.height - headerHeight - padding * 2));
		}
	}

	private static final class RightPanelStack extends BasePanel {
		private static final int TAB_ICON_SIZE = 28;
		private static final int TAB_PADDING = 4;
		private static final int TAB_ROW_HEIGHT = TAB_ICON_SIZE + TAB_PADDING;
		private static final int TAB_ACTIVE_BG = 0x232323;
		private static final int TAB_INACTIVE_BG = 0x171717;
		private static final int TAB_HOVER_BG = 0x202020;
		private static final int TAB_BORDER = 0x2c2c2c;

		private static final int INVENTORY_CONTAINER_ID = 3214;
		private static final int INVENTORY_SLOT_SIZE = 32;
		private static final int INVENTORY_PADDING = 4;
		private static final int INVENTORY_MIN_COLUMNS = 2;
		private static final int INVENTORY_MAX_COLUMNS = 8;

		private static final TabEntry[] TABS = {
				new TabEntry("Quest", 0),
				new TabEntry("Stats", 1),
				new TabEntry("Skills", 2),
				new TabEntry("Inventory", 3),
				new TabEntry("Equipment", 4),
				new TabEntry("Prayer", 5),
				new TabEntry("Magic", 6),
				new TabEntry("Clan", 7),
				new TabEntry("Friends", 8),
				new TabEntry("Settings", 9),
				new TabEntry("Emotes", 10),
				new TabEntry("Music", 11),
				new TabEntry("Notes", 12),
				new TabEntry("Logout", 13)
		};

		private final Map<Integer, Integer> scrollOffsets = new HashMap<>();
		private final Map<Integer, Map<Integer, Point>> originalChildPositions = new HashMap<>();
		private int activeTabIndex;
		private int hoveredTabIndex = -1;

		private RightPanelStack(int id, Rectangle bounds) {
			super(id, bounds, true, true, "Panels", true, 160, 200 + PANEL_HEADER_HEIGHT, false);
			Settings settings = Client.getUserSettings();
			int defaultTab = settings == null ? 3 : settings.getRightPanelTabIndex();
			activeTabIndex = resolveTabIndex(defaultTab);
		}

		@Override
		public void draw(Client client) {
			Rectangle bounds = getBounds();
			int headerHeight = PanelManager.getPanelHeaderHeight(client, this);
			int tabRows = drawTabBar(client, bounds, headerHeight);
			Rectangle contentBounds = getContentBounds(bounds, headerHeight, tabRows);
			drawActiveInterface(client, contentBounds);
		}

		@Override
		public boolean handleMouse(Client client, int mouseX, int mouseY) {
			Rectangle bounds = getBounds();
			int headerHeight = PanelManager.getPanelHeaderHeight(client, this);
			int tabRows = getTabRows(bounds);
			int absoluteX = bounds.x + mouseX;
			int absoluteY = bounds.y + mouseY;
			hoveredTabIndex = resolveTabAt(bounds, headerHeight, absoluteX, absoluteY);
			Rectangle contentBounds = getContentBounds(bounds, headerHeight, tabRows);
			if (absoluteY < contentBounds.y) {
				return true;
			}
			return handleInterfaceMouse(client, contentBounds, absoluteX, absoluteY);
		}

		@Override
		public boolean handleClick(Client client, int mouseX, int mouseY) {
			Rectangle bounds = getBounds();
			int headerHeight = PanelManager.getPanelHeaderHeight(client, this);
			int absoluteX = bounds.x + mouseX;
			int absoluteY = bounds.y + mouseY;
			int clickedTab = resolveTabAt(bounds, headerHeight, absoluteX, absoluteY);
			if (clickedTab != -1) {
				setActiveTab(client, clickedTab);
				return true;
			}
			int tabRows = getTabRows(bounds);
			Rectangle contentBounds = getContentBounds(bounds, headerHeight, tabRows);
			if (absoluteY < contentBounds.y) {
				return true;
			}
			return handleInterfaceClick(client, contentBounds, absoluteX, absoluteY);
		}

		@Override
		public boolean handleRightClick(Client client, int mouseX, int mouseY) {
			Rectangle bounds = getBounds();
			int headerHeight = PanelManager.getPanelHeaderHeight(client, this);
			int tabRows = getTabRows(bounds);
			Rectangle contentBounds = getContentBounds(bounds, headerHeight, tabRows);
			int absoluteX = bounds.x + mouseX;
			int absoluteY = bounds.y + mouseY;
			if (absoluteY < contentBounds.y) {
				return true;
			}
			return handleInterfaceRightClick(client, contentBounds, absoluteX, absoluteY);
		}

		private int drawTabBar(Client client, Rectangle bounds, int headerHeight) {
			int columns = Math.max(1, (bounds.width - TAB_PADDING * 2) / (TAB_ICON_SIZE + TAB_PADDING));
			int rows = (int) Math.ceil(TABS.length / (double) columns);
			int startX = bounds.x + TAB_PADDING;
			int startY = bounds.y + headerHeight + TAB_PADDING;
			for (int index = 0; index < TABS.length; index++) {
				int col = index % columns;
				int row = index / columns;
				int x = startX + col * (TAB_ICON_SIZE + TAB_PADDING);
				int y = startY + row * TAB_ROW_HEIGHT;
				boolean active = TABS[index].tabIndex == activeTabIndex;
				boolean hovered = TABS[index].tabIndex == hoveredTabIndex;
				int color = active ? TAB_ACTIVE_BG : (hovered ? TAB_HOVER_BG : TAB_INACTIVE_BG);
				DrawingArea.drawPixels(TAB_ICON_SIZE, y, x, color, TAB_ICON_SIZE);
				DrawingArea.drawPixels(1, y, x, TAB_BORDER, TAB_ICON_SIZE);
				DrawingArea.drawPixels(1, y + TAB_ICON_SIZE - 1, x, TAB_BORDER, TAB_ICON_SIZE);
				DrawingArea.drawPixels(TAB_ICON_SIZE, y, x, TAB_BORDER, 1);
				DrawingArea.drawPixels(TAB_ICON_SIZE, y, x + TAB_ICON_SIZE - 1, TAB_BORDER, 1);
				Sprite icon = client.getTabIconSprite(TABS[index].tabIndex);
				if (icon != null) {
					int iconX = x + (TAB_ICON_SIZE - icon.myWidth) / 2;
					int iconY = y + (TAB_ICON_SIZE - icon.myHeight) / 2;
					icon.drawSprite(iconX, iconY);
				} else {
					client.newSmallFont.drawCenteredString(TABS[index].label.substring(0, 1), x + TAB_ICON_SIZE / 2,
							y + 20, 0xffffff, 0);
				}
			}
			return rows;
		}

		int getTabRows(Rectangle bounds) {
			int columns = Math.max(1, (bounds.width - TAB_PADDING * 2) / (TAB_ICON_SIZE + TAB_PADDING));
			return (int) Math.ceil(TABS.length / (double) columns);
		}

		Rectangle getContentBounds(Rectangle bounds, int headerHeight, int tabRows) {
			int tabHeight = tabRows * TAB_ROW_HEIGHT + TAB_PADDING;
			return new Rectangle(
					bounds.x + TAB_PADDING,
					bounds.y + headerHeight + tabHeight,
					Math.max(0, bounds.width - TAB_PADDING * 2),
					Math.max(0, bounds.height - headerHeight - tabHeight - TAB_PADDING));
		}

		private int resolveTabAt(Rectangle bounds, int headerHeight, int absoluteX, int absoluteY) {
			int columns = Math.max(1, (bounds.width - TAB_PADDING * 2) / (TAB_ICON_SIZE + TAB_PADDING));
			int startX = bounds.x + TAB_PADDING;
			int startY = bounds.y + headerHeight + TAB_PADDING;
			for (int index = 0; index < TABS.length; index++) {
				int col = index % columns;
				int row = index / columns;
				int x = startX + col * (TAB_ICON_SIZE + TAB_PADDING);
				int y = startY + row * TAB_ROW_HEIGHT;
				if (absoluteX >= x && absoluteX <= x + TAB_ICON_SIZE && absoluteY >= y && absoluteY <= y + TAB_ICON_SIZE) {
					return TABS[index].tabIndex;
				}
			}
			return -1;
		}

		private void setActiveTab(Client client, int tabIndex) {
			activeTabIndex = resolveTabIndex(tabIndex);
			Settings settings = Client.getUserSettings();
			if (settings != null) {
				settings.setRightPanelTabIndex(activeTabIndex);
			}
			try {
				com.client.utilities.settings.SettingsManager.saveSettings(client);
			} catch (Exception e) {
				System.err.println("[Settings] Failed to persist right panel tab selection.");
				e.printStackTrace();
			}
		}

		private int resolveTabIndex(int tabIndex) {
			for (TabEntry entry : TABS) {
				if (entry.tabIndex == tabIndex) {
					return tabIndex;
				}
			}
			return 3;
		}

		private void drawActiveInterface(Client client, Rectangle contentBounds) {
			int interfaceId = Client.tabInterfaceIDs[activeTabIndex];
			if (interfaceId <= 0) {
				return;
			}
			RSInterface rsInterface = RSInterface.interfaceCache[interfaceId];
			if (rsInterface == null) {
				return;
			}
			updateInterfaceLayout(client, rsInterface, contentBounds);
			int scrollPosition = getScrollPosition(rsInterface, contentBounds, activeTabIndex);
			int clipLeft = DrawingArea.topX;
			int clipTop = DrawingArea.topY;
			int clipRight = DrawingArea.bottomX;
			int clipBottom = DrawingArea.bottomY;
			DrawingArea.setDrawingArea(contentBounds.y + contentBounds.height, contentBounds.x, contentBounds.x + contentBounds.width, contentBounds.y);
			client.pushUiOffset(contentBounds.x, contentBounds.y);
			client.drawInterfaceWithOffset(scrollPosition, 0, rsInterface, 0);
			client.popUiOffset();
			DrawingArea.setDrawingArea(clipBottom, clipLeft, clipRight, clipTop);
			if (needsScroll(rsInterface, contentBounds)) {
				int scrollHeight = contentBounds.height;
				int scrollMax = getContentHeight(rsInterface);
				client.drawScrollbar(scrollHeight, scrollOffsets.getOrDefault(activeTabIndex, 0), contentBounds.y,
						contentBounds.x + contentBounds.width - 16, scrollMax);
			}
		}

		private boolean handleInterfaceMouse(Client client, Rectangle contentBounds, int absoluteX, int absoluteY) {
			int interfaceId = Client.tabInterfaceIDs[activeTabIndex];
			if (interfaceId <= 0) {
				return false;
			}
			RSInterface rsInterface = RSInterface.interfaceCache[interfaceId];
			if (rsInterface == null) {
				return false;
			}
			int adjustedMouseY = absoluteY - contentBounds.y;
			int adjustedMouseX = absoluteX - contentBounds.x;
			if (adjustedMouseY < 0 || adjustedMouseX < 0) {
				return false;
			}
			updateInterfaceLayout(client, rsInterface, contentBounds);
			client.pushUiOffset(contentBounds.x, contentBounds.y);
			client.buildInterfaceMenuWithOffset(0, rsInterface, adjustedMouseX, 0, adjustedMouseY,
					getScrollPosition(rsInterface, contentBounds, activeTabIndex));
			client.popUiOffset();
			return true;
		}

		private boolean handleInterfaceClick(Client client, Rectangle contentBounds, int absoluteX, int absoluteY) {
			int interfaceId = Client.tabInterfaceIDs[activeTabIndex];
			if (interfaceId <= 0) {
				return false;
			}
			RSInterface rsInterface = RSInterface.interfaceCache[interfaceId];
			if (rsInterface == null) {
				return false;
			}
			int adjustedMouseY = absoluteY - contentBounds.y;
			int adjustedMouseX = absoluteX - contentBounds.x;
			if (adjustedMouseY < 0 || adjustedMouseX < 0) {
				return false;
			}
			updateInterfaceLayout(client, rsInterface, contentBounds);
			client.pushUiOffset(contentBounds.x, contentBounds.y);
			client.buildInterfaceMenuWithOffset(0, rsInterface, adjustedMouseX, 0, adjustedMouseY,
					getScrollPosition(rsInterface, contentBounds, activeTabIndex));
			client.popUiOffset();
			return true;
		}

		private boolean handleInterfaceRightClick(Client client, Rectangle contentBounds, int absoluteX, int absoluteY) {
			int interfaceId = Client.tabInterfaceIDs[activeTabIndex];
			if (interfaceId <= 0) {
				return false;
			}
			RSInterface rsInterface = RSInterface.interfaceCache[interfaceId];
			if (rsInterface == null) {
				return false;
			}
			int adjustedMouseY = absoluteY - contentBounds.y;
			int adjustedMouseX = absoluteX - contentBounds.x;
			if (adjustedMouseY < 0 || adjustedMouseX < 0) {
				return false;
			}
			updateInterfaceLayout(client, rsInterface, contentBounds);
			client.pushUiOffset(contentBounds.x, contentBounds.y);
			client.buildInterfaceMenuWithOffset(0, rsInterface, adjustedMouseX, 0, adjustedMouseY,
					getScrollPosition(rsInterface, contentBounds, activeTabIndex));
			client.popUiOffset();
			return true;
		}

		void scrollBy(int delta, Rectangle contentBounds, Client client) {
			int interfaceId = Client.tabInterfaceIDs[activeTabIndex];
			if (interfaceId <= 0) {
				return;
			}
			RSInterface rsInterface = RSInterface.interfaceCache[interfaceId];
			if (rsInterface == null || !needsScroll(rsInterface, contentBounds)) {
				scrollOffsets.put(activeTabIndex, 0);
				return;
			}
			int maxScroll = Math.max(0, getContentHeight(rsInterface) - contentBounds.height);
			int current = scrollOffsets.getOrDefault(activeTabIndex, 0);
			scrollOffsets.put(activeTabIndex, clamp(current + delta, 0, maxScroll));
		}

		private boolean needsScroll(RSInterface rsInterface, Rectangle bounds) {
			return getContentHeight(rsInterface) > bounds.height;
		}

		private int getContentHeight(RSInterface rsInterface) {
			return Math.max(rsInterface.height, rsInterface.scrollMax);
		}

		private int getScrollPosition(RSInterface rsInterface, Rectangle bounds, int tabIndex) {
			if (!needsScroll(rsInterface, bounds)) {
				scrollOffsets.put(tabIndex, 0);
				return 0;
			}
			int maxScroll = Math.max(0, getContentHeight(rsInterface) - bounds.height);
			int current = scrollOffsets.getOrDefault(tabIndex, 0);
			int clamped = clamp(current, 0, maxScroll);
			scrollOffsets.put(tabIndex, clamped);
			return clamped;
		}

		private void updateInterfaceLayout(Client client, RSInterface rsInterface, Rectangle bounds) {
			rsInterface.width = bounds.width;
			rsInterface.height = bounds.height;
			if (activeTabIndex == 3) {
				applyInventoryLayout(client, rsInterface, bounds);
				return;
			}
			if (activeTabIndex == 5 || activeTabIndex == 6) {
				applyIconGridLayout(rsInterface, bounds);
			}
			rsInterface.scrollMax = Math.max(rsInterface.height, getInterfaceContentHeight(rsInterface));
		}

		private void applyInventoryLayout(Client client, RSInterface rsInterface, Rectangle bounds) {
			RSInterface container = RSInterface.interfaceCache[INVENTORY_CONTAINER_ID];
			if (container == null) {
				return;
			}
			int padX = container.invSpritePadX;
			int padY = container.invSpritePadY;
			int contentWidth = Math.max(1, bounds.width - INVENTORY_PADDING * 2);
			int columns = Math.max(INVENTORY_MIN_COLUMNS,
					Math.min(INVENTORY_MAX_COLUMNS, (contentWidth + padX) / (INVENTORY_SLOT_SIZE + padX)));
			int rows = (int) Math.ceil(28D / columns);
			int requiredHeight = rows * INVENTORY_SLOT_SIZE + Math.max(0, rows - 1) * padY + INVENTORY_PADDING * 2;
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
			client.getPanelManager().saveLayout(client);
		}

		private void applyIconGridLayout(RSInterface rsInterface, Rectangle bounds) {
			if (rsInterface.children == null) {
				return;
			}
			cacheOriginalPositions(rsInterface);
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
			int padding = 4;
			int columns = Math.max(1, (bounds.width - padding * 2) / (iconSize + padding));
			int rows = (int) Math.ceil(iconIndices.size() / (double) columns);
			int requiredHeight = rows * (iconSize + padding) + padding;
			for (int idx = 0; idx < iconIndices.size(); idx++) {
				int index = iconIndices.get(idx);
				int row = idx / columns;
				int col = idx % columns;
				rsInterface.childX[index] = padding + col * (iconSize + padding);
				rsInterface.childY[index] = padding + row * (iconSize + padding);
			}
			rsInterface.scrollMax = Math.max(bounds.height, requiredHeight);
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
			return child.width <= 40 && child.height <= 40;
		}

		private void cacheOriginalPositions(RSInterface rsInterface) {
			if (originalChildPositions.containsKey(rsInterface.id)) {
				return;
			}
			Map<Integer, Point> positions = new HashMap<>();
			for (int index = 0; index < rsInterface.children.length; index++) {
				positions.put(rsInterface.children[index], new Point(rsInterface.childX[index], rsInterface.childY[index]));
			}
			originalChildPositions.put(rsInterface.id, positions);
		}

		protected int getInterfaceContentHeight(RSInterface rsInterface) {
			int maxHeight = rsInterface.height;
			if (rsInterface.children == null) {
				return maxHeight;
			}
			for (int index = 0; index < rsInterface.children.length; index++) {
				RSInterface child = RSInterface.interfaceCache[rsInterface.children[index]];
				if (child == null) {
					continue;
				}
				int childBottom = rsInterface.childY[index] + child.height + child.anInt265;
				if (childBottom > maxHeight) {
					maxHeight = childBottom;
				}
			}
			return maxHeight;
		}

		@Override
		public boolean isClosable() {
			return false;
		}

		private static final class TabEntry {
			private final String label;
			private final int tabIndex;

			private TabEntry(String label, int tabIndex) {
				this.label = label;
				this.tabIndex = tabIndex;
			}
		}
	}

	private void drawPanelBackground(Client client, UiPanel panel) {
		if (panel instanceof ChatPanel) {
			return;
		}
		int backgroundColor = PANEL_BACKGROUND;
		int backgroundAlpha = 255;
		Settings settings = Client.getUserSettings();
		if (settings != null) {
			backgroundColor = settings.getRs3PanelBackgroundColor();
			if (client.isRs3InterfaceStyleActive()) {
				int transparency = settings.getRs3InterfaceTransparency();
				backgroundAlpha = 255 - (transparency * 155 / 60);
				backgroundAlpha = clamp(backgroundAlpha, 100, 255);
			}
		}
		int headerColor = adjustColor(backgroundColor, 10);
		int highlightColor = adjustColor(backgroundColor, 22);
		int shadowColor = adjustColor(backgroundColor, -12);
		Rectangle bounds = panel.getBounds();
		int headerHeight = getPanelHeaderHeight(client, panel);
		if (backgroundAlpha < 255) {
			DrawingArea.drawAlphaPixels(bounds.x, bounds.y, bounds.width, bounds.height, backgroundColor, backgroundAlpha);
			if (headerHeight > 0) {
				DrawingArea.drawAlphaPixels(bounds.x, bounds.y, bounds.width, headerHeight, headerColor, backgroundAlpha);
			}
			DrawingArea.drawAlphaGradient(bounds.x, bounds.y, bounds.width, bounds.height,
					highlightColor, shadowColor, Math.min(backgroundAlpha, 140));
		} else {
			DrawingArea.drawPixels(bounds.height, bounds.y, bounds.x, backgroundColor, bounds.width);
			if (headerHeight > 0) {
				DrawingArea.drawPixels(headerHeight, bounds.y, bounds.x, headerColor, bounds.width);
			}
			DrawingArea.drawAlphaGradient(bounds.x, bounds.y, bounds.width, bounds.height,
					highlightColor, shadowColor, 110);
		}
		DrawingArea.drawPixels(1, bounds.y, bounds.x, PANEL_BORDER, bounds.width);
		DrawingArea.drawPixels(1, bounds.y + bounds.height - 1, bounds.x, PANEL_BORDER, bounds.width);
		DrawingArea.drawPixels(bounds.height, bounds.y, bounds.x, PANEL_BORDER, 1);
		DrawingArea.drawPixels(bounds.height, bounds.y, bounds.x + bounds.width - 1, PANEL_BORDER, 1);
		DrawingArea.drawPixels(1, bounds.y + 1, bounds.x + 1, highlightColor, bounds.width - 2);
		DrawingArea.drawPixels(1, bounds.y + bounds.height - 2, bounds.x + 1, shadowColor, bounds.width - 2);
		if (headerHeight > 0) {
			DrawingArea.drawPixels(1, bounds.y + headerHeight, bounds.x, PANEL_BORDER, bounds.width);
			String title = panel.getTitle();
			if (!(panel instanceof GroupPanel) && client.isRs3EditModeActive() && title != null && !title.isEmpty()) {
				client.newSmallFont.drawBasicString(title, bounds.x + 6, bounds.y + 13, PANEL_TEXT, 0);
			}
		}
	}

	private void drawResizeHandle(Client client, UiPanel panel) {
		if (!panel.resizable()) {
			return;
		}
		if (!panel.drawsBackground() && !(panel instanceof MinimapBasePanel)) {
			return;
		}
		if (!isHeaderVisible(client, panel)) {
			return;
		}
		Rectangle bounds = panel.getBounds();
		drawCornerHandle(bounds.x, bounds.y);
		drawCornerHandle(getRightResizeHandleX(client, panel), bounds.y);
	}

	private ResizeHandle getResizeHandle(Client client, UiPanel panel, int mouseX, int mouseY) {
		if (!panel.drawsBackground()) {
			return null;
		}
		Rectangle bounds = panel.getBounds();
		int leftX = bounds.x;
		int rightX = getRightResizeHandleX(client, panel);
		int topY = bounds.y;
		if (mouseY >= topY && mouseY <= topY + RESIZE_HANDLE_SIZE) {
			if (mouseX >= leftX && mouseX <= leftX + RESIZE_HANDLE_SIZE) {
				return ResizeHandle.TOP_LEFT;
			}
			if (mouseX >= rightX && mouseX <= rightX + RESIZE_HANDLE_SIZE) {
				return ResizeHandle.TOP_RIGHT;
			}
		}
		return null;
	}

	private void drawCornerHandle(int x, int y) {
		DrawingArea.drawPixels(RESIZE_HANDLE_SIZE, y, x, 0x2a2a2a, RESIZE_HANDLE_SIZE);
		DrawingArea.drawPixels(1, y, x, 0x3a3a3a, RESIZE_HANDLE_SIZE);
		DrawingArea.drawPixels(1, y + RESIZE_HANDLE_SIZE - 1, x, 0x3a3a3a, RESIZE_HANDLE_SIZE);
		DrawingArea.drawPixels(RESIZE_HANDLE_SIZE, y, x, 0x3a3a3a, 1);
		DrawingArea.drawPixels(RESIZE_HANDLE_SIZE, y, x + RESIZE_HANDLE_SIZE - 1, 0x3a3a3a, 1);
	}

	private int getRightResizeHandleX(Client client, UiPanel panel) {
		Rectangle bounds = panel.getBounds();
		int rightX = bounds.x + bounds.width - RESIZE_HANDLE_SIZE;
		if (panel.isClosable() && isHeaderVisible(client, panel)) {
			int closeX = bounds.x + bounds.width - CLOSE_BUTTON_SIZE - CLOSE_BUTTON_PADDING;
			int adjusted = closeX - RESIZE_HANDLE_SIZE;
			if (adjusted >= bounds.x + RESIZE_HANDLE_SIZE) {
				rightX = Math.min(rightX, adjusted);
			}
		}
		return rightX;
	}

	private void drawCloseButton(Client client, UiPanel panel) {
		Rectangle bounds = panel.getBounds();
		int x = bounds.x + bounds.width - CLOSE_BUTTON_SIZE - CLOSE_BUTTON_PADDING;
		int y = bounds.y + (PANEL_HEADER_HEIGHT - CLOSE_BUTTON_SIZE) / 2;
		DrawingArea.drawPixels(CLOSE_BUTTON_SIZE, y, x, 0x2a2a2a, CLOSE_BUTTON_SIZE);
		DrawingArea.drawPixels(1, y, x, 0x3a3a3a, CLOSE_BUTTON_SIZE);
		DrawingArea.drawPixels(1, y + CLOSE_BUTTON_SIZE - 1, x, 0x3a3a3a, CLOSE_BUTTON_SIZE);
		DrawingArea.drawPixels(CLOSE_BUTTON_SIZE, y, x, 0x3a3a3a, 1);
		DrawingArea.drawPixels(CLOSE_BUTTON_SIZE, y, x + CLOSE_BUTTON_SIZE - 1, 0x3a3a3a, 1);
		client.newSmallFont.drawCenteredString("X", x + CLOSE_BUTTON_SIZE / 2, y + 9, 0xffffff, 0);
	}

	private boolean isOnCloseButton(UiPanel panel, int mouseX, int mouseY) {
		Rectangle bounds = panel.getBounds();
		int x = bounds.x + bounds.width - CLOSE_BUTTON_SIZE - CLOSE_BUTTON_PADDING;
		int y = bounds.y + (PANEL_HEADER_HEIGHT - CLOSE_BUTTON_SIZE) / 2;
		return mouseX >= x && mouseX <= x + CLOSE_BUTTON_SIZE && mouseY >= y && mouseY <= y + CLOSE_BUTTON_SIZE;
	}

	private static int adjustColor(int color, int delta) {
		int r = Math.min(255, Math.max(0, ((color >> 16) & 0xff) + delta));
		int g = Math.min(255, Math.max(0, ((color >> 8) & 0xff) + delta));
		int b = Math.min(255, Math.max(0, (color & 0xff) + delta));
		return (r << 16) | (g << 8) | b;
	}

	static int getPanelHeaderHeight(Client client, UiPanel panel) {
		if (client == null || !client.isRs3InterfaceStyleActive()) {
			return PANEL_HEADER_HEIGHT;
		}
		if (panel instanceof GroupPanel) {
			return PANEL_HEADER_HEIGHT;
		}
		if (client.isRs3EditModeActive()) {
			return PANEL_HEADER_HEIGHT;
		}
		return panel != null && !panel.isClosable() ? PANEL_HEADER_HEIGHT : 0;
	}

	private static boolean isHeaderVisible(Client client, UiPanel panel) {
		return getPanelHeaderHeight(client, panel) > 0;
	}

	private DockCandidate findDockCandidate(UiPanel panel) {
		Rectangle bounds = panel.getBounds();
		DockCandidate best = null;
		int bestDistance = DOCK_SNAP_THRESHOLD + 1;
		Rectangle screen = new Rectangle(0, 0, Client.currentGameWidth, Client.currentGameHeight);
		best = pickDockCandidate(best, screen, bounds, DockSide.LEFT, Math.abs(bounds.x), bestDistance);
		bestDistance = best == null ? bestDistance : best.distance;
		best = pickDockCandidate(best, screen, bounds, DockSide.RIGHT, Math.abs((bounds.x + bounds.width) - screen.width), bestDistance);
		bestDistance = best == null ? bestDistance : best.distance;
		best = pickDockCandidate(best, screen, bounds, DockSide.TOP, Math.abs(bounds.y), bestDistance);
		bestDistance = best == null ? bestDistance : best.distance;
		best = pickDockCandidate(best, screen, bounds, DockSide.BOTTOM, Math.abs((bounds.y + bounds.height) - screen.height), bestDistance);
		bestDistance = best == null ? bestDistance : best.distance;
		for (UiPanel other : panels) {
			if (other == panel || !other.isVisible()) {
				continue;
			}
			Rectangle target = other.getBounds();
			if (overlapsVertically(bounds, target)) {
				best = pickDockCandidate(best, target, bounds, DockSide.LEFT, Math.abs(bounds.x - (target.x + target.width)), bestDistance);
				bestDistance = best == null ? bestDistance : best.distance;
				best = pickDockCandidate(best, target, bounds, DockSide.RIGHT, Math.abs(bounds.x + bounds.width - target.x), bestDistance);
				bestDistance = best == null ? bestDistance : best.distance;
			}
			if (overlapsHorizontally(bounds, target)) {
				best = pickDockCandidate(best, target, bounds, DockSide.TOP, Math.abs(bounds.y - (target.y + target.height)), bestDistance);
				bestDistance = best == null ? bestDistance : best.distance;
				best = pickDockCandidate(best, target, bounds, DockSide.BOTTOM, Math.abs(bounds.y + bounds.height - target.y), bestDistance);
				bestDistance = best == null ? bestDistance : best.distance;
			}
		}
		if (best == null || !isPlacementValid(best.bounds, panel)) {
			return null;
		}
		return best;
	}

	private DockCandidate pickDockCandidate(DockCandidate current, Rectangle target, Rectangle bounds, DockSide side, int distance, int bestDistance) {
		if (distance > DOCK_SNAP_THRESHOLD || distance >= bestDistance) {
			return current;
		}
		Rectangle snap = new Rectangle(bounds);
		switch (side) {
			case LEFT:
				snap.x = target.x - bounds.width;
				if (target.x == 0) {
					snap.x = 0;
				}
				break;
			case RIGHT:
				snap.x = target.x + target.width;
				if (target.width == Client.currentGameWidth) {
					snap.x = Client.currentGameWidth - bounds.width;
				}
				break;
			case TOP:
				snap.y = target.y - bounds.height;
				if (target.y == 0) {
					snap.y = 0;
				}
				break;
			case BOTTOM:
				snap.y = target.y + target.height;
				if (target.height == Client.currentGameHeight) {
					snap.y = Client.currentGameHeight - bounds.height;
				}
				break;
			default:
				break;
		}
		snap.x = clamp(snap.x, 0, Client.currentGameWidth - bounds.width);
		snap.y = clamp(snap.y, 0, Client.currentGameHeight - bounds.height);
		return new DockCandidate(snap, distance);
	}

	private boolean overlapsVertically(Rectangle a, Rectangle b) {
		return a.y < b.y + b.height && a.y + a.height > b.y;
	}

	private boolean overlapsHorizontally(Rectangle a, Rectangle b) {
		return a.x < b.x + b.width && a.x + a.width > b.x;
	}

	private void drawDockPreview(DockCandidate candidate) {
		if (candidate == null || candidate.bounds == null) {
			return;
		}
		if (DrawingArea.pixels == null) {
			return;
		}

		// Clamp to the current raster. During live resize, width/height can be briefly inconsistent,
		// so also clamp height against pixels.length / width.
		int rasterW = DrawingArea.width;
		if (rasterW <= 0) {
			return;
		}

		int rasterH = DrawingArea.height;
		int maxHFromPixels = DrawingArea.pixels.length / rasterW;
		if (maxHFromPixels <= 0) {
			return;
		}
		if (rasterH > maxHFromPixels) {
			rasterH = maxHFromPixels;
		}

		Rectangle b = candidate.bounds;

		int x = b.x;
		int y = b.y;
		int w = b.width;
		int h = b.height;

		if (w <= 0 || h <= 0) {
			return;
		}

		// Clip left/top
		if (x < 0) {
			w += x;
			x = 0;
		}
		if (y < 0) {
			h += y;
			y = 0;
		}

		// Clip right/bottom
		if (x + w > rasterW) {
			w = rasterW - x;
		}
		if (y + h > rasterH) {
			h = rasterH - y;
		}

		if (w <= 0 || h <= 0) {
			return;
		}

		// Draw preview safely
		DrawingArea.drawAlphaPixels(x, y, w, h, 0xffd24a, DOCK_PREVIEW_ALPHA);

		// Outline should match the clamped preview (not the original b that may be off-screen)
		drawSelectionOutline(new Rectangle(x, y, w, h));
	}


	private enum ResizeHandle {
		TOP_LEFT,
		TOP_RIGHT
	}

	private enum DockSide {
		LEFT,
		RIGHT,
		TOP,
		BOTTOM
	}

	private static final class DockCandidate {
		private final Rectangle bounds;
		private final int distance;

		private DockCandidate(Rectangle bounds, int distance) {
			this.bounds = bounds;
			this.distance = distance;
		}
	}
}
