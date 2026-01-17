package com.client.ui.shell;

import com.client.Client;
import com.client.Rasterizer;
import com.client.features.settings.Preferences;
import com.client.graphics.interfaces.dropdown.StretchedModeMenu;
import com.client.graphics.interfaces.settings.Setting;
import com.client.graphics.interfaces.settings.SettingsInterface;
import com.client.utilities.settings.InterfaceStyle;
import com.client.utilities.settings.Settings;
import com.client.utilities.settings.SettingsManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

final class SettingsPanel extends JPanel {

    private final JPanel body;
    private final VyknaShell shell;

    private JTextField search;
    private final List<SettingsSection> sections = new ArrayList<>();
    private final List<FilterItem> filterItems = new ArrayList<>();
    private SettingsSection currentSection;

    private JToggleButton rs3EditModeToggle;
    private JButton rs3ResetLayoutButton;
    private JComboBox<String> rs3PanelBackgroundDropdown;
    private JTextField presetNameField;
    private JToggleButton loadPresetToggle;
    private JButton savePresetButton;
    private JButton loadPresetButton;

    SettingsPanel(VyknaShell shell) {
        super(new BorderLayout());
        this.shell = shell;

        setOpaque(true);
        setBackground(VyknaShell.BG);

        Settings settings = Client.getUserSettings();
        if (settings == null) {
            settings = Settings.getDefault();
            Client.setUserSettings(settings);
        }

        body = VyknaShell.cardRoot();
        body.add(new VyknaShell.SectionTitle("Settings"));
        body.add(searchBar());

        // --- Presets & Profiles ---
        addSection("Presets & Profiles", "\uD83D\uDCBE");
        addPresetControls(settings);

        // --- Graphics ---
        addSection("Graphics", "\uD83D\uDDA5");
        addSettingToggle(SettingsInterface.ANTI_ALIASING, settings.isAntiAliasing());
        addSettingToggle(SettingsInterface.FOG, settings.isFog());
        addSettingToggle(SettingsInterface.SMOOTH_SHADING, settings.isSmoothShading());
        addSettingToggle(SettingsInterface.TILE_BLENDING, settings.isTileBlending());
        addSettingToggle(SettingsInterface.STATUS_BARS, isStatusBarsEnabled());
        addSettingDropdown(SettingsInterface.DRAW_DISTANCE, drawDistanceIndex(settings.getDrawDistance()));
        addSettingDropdown(SettingsInterface.STRETCHED_MODE, booleanToIndex(settings.isStretchedMode()));
        addSliderRow("Brightness", Preferences.getPreferences().brightness * 100.0, 60, 100, value -> {
            Preferences.getPreferences().brightness = value / 100.0;
            Rasterizer.setBrightness(Preferences.getPreferences().brightness);
            Preferences.save();
        });

        // --- Interface ---
        addSection("Interface", "\uD83E\uDDE9");
        addSettingDropdown(SettingsInterface.INTERFACE_STYLE, interfaceStyleIndex(settings.getInterfaceStyle()));

        // “RS3 Gameframe” row: toggle + gear (your end-goal UX)
        addSettingToggleWithGear(SettingsInterface.OLD_GAMEFRAME, settings.isOldGameframe(), this::openGameframeAdvanced);

        addSettingDropdown(SettingsInterface.INVENTORY_MENU, inventoryMenuIndex());
        addSettingDropdown(SettingsInterface.CHAT_EFFECT, settings.getChatColor());
        addSettingToggle(SettingsInterface.GROUND_ITEM_NAMES, settings.isGroundItemOverlay());
        addSettingToggle(SettingsInterface.MENU_HOVERS, isMenuHoversEnabled());
        addSettingToggle(SettingsInterface.PLAYER_PROFILE, false);
        addSettingToggle(SettingsInterface.GAME_TIMERS, settings.isGameTimers());
        addSettingDropdown(SettingsInterface.PM_NOTIFICATION, booleanToIndex(Preferences.getPreferences().pmNotifications));

        addRs3EditModeControls(settings);
        addRs3PanelBackgroundSetting(settings);

        // --- Gameplay ---
        addSection("Gameplay", "\u2694");
        addSettingToggle(SettingsInterface.BOUNTY_HUNTER, settings.isBountyHunter());
        addSettingToggle(SettingsInterface.ENTITY_TARGET, settings.isShowEntityTarget());
        addSettingDropdown(SettingsInterface.DRAG, dragTimeIndex());

        // --- Misc ---
        addSection("Misc", "\u2699");
        addSettingToggle(SettingsInterface.ROOF, !isRemoveRoofsEnabled());
        addSettingToggle(SettingsInterface.PVP_TAB, false);

        // Make sure everything gets your global theme pass too
        VyknaShell.applyThemeRecursive(body);

        add(body, BorderLayout.CENTER);
    }

    // ---------------- Filter model ----------------

    private static final class FilterItem {
        final String key;      // lowercased searchable text
        final JComponent comp;
        final SettingsSection section;

        FilterItem(String key, JComponent comp, SettingsSection section) {
            this.key = key;
            this.comp = comp;
            this.section = section;
        }
    }

    private static final class SettingsSection {
        final String title;
        final JPanel container;
        final JPanel header;
        final JPanel content;
        final JToggleButton toggle;
        final List<FilterItem> items = new ArrayList<>();
        boolean collapsed = false;

        SettingsSection(String title, String icon) {
            this.title = title;
            this.container = new JPanel(new BorderLayout());
            this.container.setOpaque(true);
            this.container.setBackground(VyknaShell.BG);

            this.header = new JPanel(new BorderLayout());
            this.header.setOpaque(true);
            this.header.setBackground(VyknaShell.BG);
            this.header.setBorder(new EmptyBorder(10, 2, 6, 2));

            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            left.setOpaque(false);
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setForeground(VyknaShell.TEXT_DIM);
            iconLabel.setFont(iconLabel.getFont().deriveFont(12f));
            JLabel titleLabel = new JLabel(title);
            titleLabel.setForeground(VyknaShell.TEXT_DIM);
            titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 12f));
            left.add(iconLabel);
            left.add(titleLabel);
            header.add(left, BorderLayout.WEST);

            toggle = new JToggleButton("\u25BE");
            toggle.setFocusable(false);
            toggle.setOpaque(false);
            toggle.setBorder(new EmptyBorder(0, 6, 0, 6));
            toggle.setForeground(VyknaShell.TEXT_DIM);
            toggle.setSelected(true);
            header.add(toggle, BorderLayout.EAST);

            content = new JPanel();
            content.setOpaque(false);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBorder(new EmptyBorder(0, 0, 8, 0));

            container.add(header, BorderLayout.NORTH);
            container.add(content, BorderLayout.CENTER);
            container.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(24, 26, 29)));
        }

        void setCollapsed(boolean collapsed) {
            this.collapsed = collapsed;
            content.setVisible(!collapsed);
            toggle.setSelected(!collapsed);
            toggle.setText(collapsed ? "\u25B8" : "\u25BE");
        }

        void setExpandedForFilter(boolean expanded) {
            content.setVisible(expanded);
            if (expanded) {
                toggle.setText("\u25BE");
            } else {
                toggle.setText(collapsed ? "\u25B8" : "\u25BE");
            }
        }
    }

    private JComponent searchBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(true);
        p.setBackground(VyknaShell.BG);
        p.setBorder(new EmptyBorder(6, 0, 10, 0));

        search = new JTextField();
        search.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(VyknaShell.BORDER),
                new EmptyBorder(8, 10, 8, 10)
        ));
        search.setBackground(VyknaShell.PANEL);
        search.setForeground(VyknaShell.TEXT);
        search.setCaretColor(VyknaShell.TEXT);

        search.getDocument().addDocumentListener(new DocumentListener() {
            private void update() { applyFilter(search.getText()); }
            @Override public void insertUpdate(DocumentEvent e) { update(); }
            @Override public void removeUpdate(DocumentEvent e) { update(); }
            @Override public void changedUpdate(DocumentEvent e) { update(); }
        });

        p.add(search, BorderLayout.CENTER);
        return p;
    }

    private void addSection(String title, String icon) {
        SettingsSection section = new SettingsSection(title, icon);
        section.toggle.addActionListener(e -> section.setCollapsed(!section.collapsed));
        section.header.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                section.setCollapsed(!section.collapsed);
            }
        });
        section.setCollapsed(false);
        sections.add(section);
        currentSection = section;
        body.add(section.container);
    }

    private void addRowItem(String label, JComponent row) {
        if (currentSection == null) {
            return;
        }
        FilterItem item = new FilterItem(label.toLowerCase(), row, currentSection);
        currentSection.items.add(item);
        filterItems.add(item);
        currentSection.content.add(row);
    }

    private void addPresetControls(Settings settings) {
        presetNameField = new JTextField(settings.getActivePresetName());
        presetNameField.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                Settings current = Client.getUserSettings();
                if (current != null) {
                    current.setActivePresetName(presetNameField.getText());
                    persistSettings();
                }
            }
            @Override public void insertUpdate(DocumentEvent e) { update(); }
            @Override public void removeUpdate(DocumentEvent e) { update(); }
            @Override public void changedUpdate(DocumentEvent e) { update(); }
        });

        savePresetButton = new JButton("Save Preset");
        savePresetButton.addActionListener(e -> {
            Settings current = Client.getUserSettings();
            if (current == null) {
                return;
            }
            String presetName = presetNameField.getText();
            current.setActivePresetName(presetName);
            try {
                SettingsManager.savePreset(current, presetName);
                persistSettings();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        loadPresetButton = new JButton("Load Preset");
        loadPresetButton.addActionListener(e -> {
            String presetName = presetNameField.getText();
            Settings preset = SettingsManager.loadPreset(presetName);
            if (preset == null) {
                return;
            }
            Settings current = Client.getUserSettings();
            boolean loadOnLogin = current != null && current.isLoadPresetOnLogin();
            preset.setActivePresetName(presetName);
            preset.setLoadPresetOnLogin(loadOnLogin);
            Client.setUserSettings(preset);
            Client instance = Client.getInstance();
            if (instance != null) {
                instance.getPanelManager().reloadLayoutFromSettings(instance);
            }
            persistSettings();
            refreshRs3Controls();
            if (loadPresetToggle != null) {
                loadPresetToggle.setSelected(loadOnLogin);
                syncToggleVisual(loadPresetToggle);
            }
        });

        loadPresetToggle = pillToggle(settings.isLoadPresetOnLogin());
        loadPresetToggle.addActionListener(e -> {
            Settings current = Client.getUserSettings();
            if (current != null) {
                current.setLoadPresetOnLogin(loadPresetToggle.isSelected());
                persistSettings();
                syncToggleVisual(loadPresetToggle);
            }
        });

        addRowItem("Preset Name", row("Preset Name", presetNameField, null));
        addRowItem("Save Preset", row("Save Preset", savePresetButton, null));
        addRowItem("Load Preset", row("Load Preset", loadPresetButton, null));
        addRowItem("Load Preset on Login", row("Load Preset on Login", loadPresetToggle, null));
    }

    private void applyFilter(String q) {
        String query = (q == null) ? "" : q.trim().toLowerCase();
        boolean anyQuery = !query.isEmpty();

        for (SettingsSection section : sections) {
            section.content.removeAll();
            boolean hasMatch = false;
            for (FilterItem item : section.items) {
                boolean match = !anyQuery || item.key.contains(query);
                if (match) {
                    hasMatch = true;
                    section.content.add(item.comp);
                }
            }
            section.container.setVisible(!anyQuery || hasMatch);
            section.setExpandedForFilter(anyQuery ? hasMatch : !section.collapsed);
        }

        body.revalidate();
        body.repaint();
    }

    // ---------------- UI pieces ----------------

    private JPanel row(String label, JComponent control, JButton gearOrNull) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(true);
        row.setBackground(VyknaShell.BG);
        row.setBorder(new EmptyBorder(2, 2, 2, 2));


        JPanel left = new JPanel(new GridBagLayout());
        left.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel l = new JLabel(label);
        l.setForeground(VyknaShell.TEXT);
        l.setFont(l.getFont().deriveFont(11.5f));
        left.add(l, gbc);

        if (gearOrNull != null) {
            gbc.gridx = 1;
            gbc.weightx = 0.0;
            gbc.fill = GridBagConstraints.NONE;
            gbc.insets = new Insets(0, 6, 0, 0);
            left.add(gearOrNull, gbc);
        }

        row.add(left, BorderLayout.WEST);
        row.add(control, BorderLayout.EAST);
        return row;
    }

    private void addSettingToggle(Setting setting, boolean initial) {
        JToggleButton toggle = pillToggle(initial);
        toggle.addActionListener(e -> {
            int option = toggle.isSelected() ? 0 : 1;
            setting.getMenuItem().select(option, null);
            persistSettings();
            syncToggleVisual(toggle);
        });
        addRowItem(setting.getSettingName(), row(setting.getSettingName(), toggle, null));
    }

    private void addSettingToggleWithGear(Setting setting, boolean initial, Runnable advanced) {
        JToggleButton toggle = pillToggle(initial);
        toggle.addActionListener(e -> {
            int option = toggle.isSelected() ? 0 : 1;
            setting.getMenuItem().select(option, null);
            persistSettings();
            syncToggleVisual(toggle);
        });

        JButton gear = gearButton(advanced);
        addRowItem(setting.getSettingName(), row(setting.getSettingName(), toggle, gear));
    }

    private void addSettingDropdown(Setting setting, int selectedIndex) {
        JComboBox<String> combo = new JComboBox<>(setting.getOptions());
        VyknaShell.styleComboBox(combo);


        combo.setSelectedIndex(Math.max(0, Math.min(selectedIndex, setting.getOptions().length - 1)));
        combo.addActionListener(e -> {
            int index = combo.getSelectedIndex();
            setting.getMenuItem().select(index, null);

            if (setting == SettingsInterface.STRETCHED_MODE) {
                StretchedModeMenu.updateStretchedMode(index == 0);
            }
            if (setting == SettingsInterface.INTERFACE_STYLE) {
                refreshRs3Controls();
            }
            persistSettings();
        });

        addRowItem(setting.getSettingName(), row(setting.getSettingName(), combo, null));
    }

    private void addSliderRow(String label, double initialValue, int min, int max, SliderValueConsumer onChange) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(true);
        row.setBackground(VyknaShell.BG);
        row.setBorder(new EmptyBorder(8, 2, 8, 2));

        JLabel l = new JLabel(label);
        l.setForeground(VyknaShell.TEXT);
        l.setFont(l.getFont().deriveFont(12f));
        row.add(l, BorderLayout.WEST);

        int initial = (int) Math.round(initialValue);
        JSlider slider = new JSlider(min, max, initial);
        slider.setPreferredSize(new Dimension(150, 20));
        VyknaShell.styleSlider(slider);
        slider.addChangeListener(e -> {
            if (!slider.getValueIsAdjusting()) {
                onChange.accept(slider.getValue());
            }
        });

        row.add(slider, BorderLayout.EAST);

        // register it for filtering
        addRowItem(label, row);
    }

    private JToggleButton pillToggle(boolean def) {
        JToggleButton t = new JToggleButton(def ? "ON" : "OFF", def);
        t.setFocusable(false);
        t.setOpaque(true);
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(34, 36, 40)),
                new EmptyBorder(6, 12, 6, 12)
        ));
        syncToggleVisual(t);
        return t;
    }

    private void syncToggleVisual(JToggleButton t) {
        boolean on = t.isSelected();
        t.setText(on ? "ON" : "OFF");
        t.setForeground(on ? VyknaShell.TEXT : VyknaShell.TEXT_DIM);
        t.setBackground(on ? new Color(24, 26, 28) : new Color(16, 17, 19));
    }

    // ---------------- Gear + advanced dialogs ----------------

    private JButton gearButton(Runnable onClick) {
        JButton b = new JButton("\u2699"); // ⚙
        b.setFocusable(false);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(34, 36, 40)),
                new EmptyBorder(4, 8, 4, 8)
        ));
        b.setBackground(new Color(16, 17, 19));
        b.setForeground(VyknaShell.TEXT_DIM);
        b.addActionListener(e -> onClick.run());
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setForeground(VyknaShell.TEXT); }
            @Override public void mouseExited(MouseEvent e) { b.setForeground(VyknaShell.TEXT_DIM); }
        });
        return b;
    }

    /**
     * This is where you’ll put:
     * - color tint
     * - transparency
     * - edit mode toggles
     * - keybinds
     *
     * Right now it’s a clean scaffold that matches the UX you want.
     */
    private void openGameframeAdvanced() {
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this), "RS3 Gameframe", Dialog.ModalityType.APPLICATION_MODAL);
        d.setLayout(new BorderLayout());
        d.getContentPane().setBackground(VyknaShell.BG);

        JPanel content = new JPanel();
        content.setOpaque(true);
        content.setBackground(VyknaShell.BG);
        content.setBorder(new EmptyBorder(12, 12, 12, 12));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel t1 = new JLabel("Panel transparency");
        t1.setForeground(VyknaShell.TEXT);
        content.add(t1);

        JSlider alpha = new JSlider(40, 100, 85);
        alpha.setOpaque(false);
        alpha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        content.add(alpha);

        content.add(Box.createVerticalStrut(10));

        JCheckBox enableHotkey = new JCheckBox("Enable edit-mode hotkey");
        enableHotkey.setOpaque(false);
        enableHotkey.setForeground(VyknaShell.TEXT);
        content.add(enableHotkey);

        content.add(Box.createVerticalStrut(8));

        JLabel hint = new JLabel("Wire these into your Settings/Preferences when ready.");
        hint.setForeground(VyknaShell.TEXT_DIM);
        content.add(hint);

        d.add(content, BorderLayout.CENTER);

        JButton close = new JButton("Close");
        close.addActionListener(e -> d.dispose());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(true);
        footer.setBackground(VyknaShell.BG);
        footer.add(close);

        d.add(footer, BorderLayout.SOUTH);

        VyknaShell.applyThemeRecursive(d);
        d.pack();
        d.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        d.setVisible(true);
    }

    // ---------------- RS3 controls (kept from your original) ----------------

    private void addRs3EditModeControls(Settings settings) {
        rs3EditModeToggle = pillToggle(settings.isRs3EditMode());
        rs3EditModeToggle.addActionListener(e -> {
            Settings currentSettings = Client.getUserSettings();
            if (currentSettings == null || currentSettings.getInterfaceStyle() != InterfaceStyle.RS3) {
                rs3EditModeToggle.setSelected(false);
                syncToggleVisual(rs3EditModeToggle);
                return;
            }
            Client instance = Client.getInstance();
            if (instance != null) {
                instance.setRs3EditMode(rs3EditModeToggle.isSelected());
            } else {
                currentSettings.setRs3EditMode(rs3EditModeToggle.isSelected());
            }
            persistSettings();
            syncToggleVisual(rs3EditModeToggle);
        });

        rs3ResetLayoutButton = new JButton("Reset");
        rs3ResetLayoutButton.addActionListener(e -> {
            if (settings.getInterfaceStyle() != InterfaceStyle.RS3) {
                return;
            }
            Client.getInstance().resetRs3PanelLayout();
            persistSettings();
        });

        addRowItem("RS3 Edit Mode", row("RS3 Edit Mode", rs3EditModeToggle, gearButton(this::openGameframeAdvanced)));
        addRowItem("Reset RS3 Layout", row("Reset RS3 Layout", rs3ResetLayoutButton, null));

        refreshRs3Controls();
    }

    private void addRs3PanelBackgroundSetting(Settings settings) {
        String[] options = { "Dark", "Slate", "Blue", "Crimson" };
        rs3PanelBackgroundDropdown = new JComboBox<>(options);
        VyknaShell.styleComboBox(rs3PanelBackgroundDropdown);

        rs3PanelBackgroundDropdown.setSelectedIndex(rs3PanelBackgroundIndex(settings.getRs3PanelBackgroundColor()));
        rs3PanelBackgroundDropdown.addActionListener(e -> {
            int index = rs3PanelBackgroundDropdown.getSelectedIndex();
            Settings current = Client.getUserSettings();
            if (current != null) {
                current.setRs3PanelBackgroundColor(rs3PanelBackgroundColorForIndex(index));
            }
            persistSettings();
        });

        addRowItem("RS3 Panel Background", row("RS3 Panel Background", rs3PanelBackgroundDropdown, null));
    }

    private void refreshRs3Controls() {
        Settings settings = Client.getUserSettings();
        if (settings == null) return;

        boolean rs3 = settings.getInterfaceStyle() == InterfaceStyle.RS3;

        if (rs3EditModeToggle != null) {
            rs3EditModeToggle.setEnabled(rs3);
            rs3EditModeToggle.setSelected(settings.isRs3EditMode());
            syncToggleVisual(rs3EditModeToggle);

            if (!rs3) {
                Client instance = Client.getInstance();
                if (instance != null) instance.setRs3EditMode(false);

                rs3EditModeToggle.setSelected(false);
                syncToggleVisual(rs3EditModeToggle);
            }
        }

        if (rs3ResetLayoutButton != null) {
            rs3ResetLayoutButton.setEnabled(rs3);
        }

        if (rs3PanelBackgroundDropdown != null) {
            rs3PanelBackgroundDropdown.setEnabled(rs3);
            rs3PanelBackgroundDropdown.setSelectedIndex(rs3PanelBackgroundIndex(settings.getRs3PanelBackgroundColor()));
        }
    }

    private int rs3PanelBackgroundIndex(int color) {
        if (color == 0x1a1f24) return 1;
        if (color == 0x121a2c) return 2;
        if (color == 0x2a1616) return 3;
        return 0;
    }

    private int rs3PanelBackgroundColorForIndex(int index) {
        switch (index) {
            case 1: return 0x1a1f24;
            case 2: return 0x121a2c;
            case 3: return 0x2a1616;
            default: return 0x141414;
        }
    }

    // ---------------- Index helpers (kept from your original) ----------------

    private int drawDistanceIndex(int drawDistance) {
        if (drawDistance == 30) return 0;
        if (drawDistance == 40) return 1;
        if (drawDistance == 50) return 2;
        if (drawDistance == 60) return 3;
        return 4;
    }

    private int interfaceStyleIndex(InterfaceStyle style) {
        return style == InterfaceStyle.RS3 ? 1 : 0;
    }

    private int inventoryMenuIndex() {
        Settings s = Client.getUserSettings();
        if (s == null || !s.isInventoryContextMenu()) return 0;

        int color = s.getStartMenuColor();
        if (color == 0xFF00FF) return 1;
        if (color == 0x00FF00) return 2;
        if (color == 0x00FFFF) return 3;
        if (color == 0xFF0000) return 4;
        return 1;
    }

    private int dragTimeIndex() {
        int drag = Preferences.getPreferences().dragTime;
        if (drag == 5) return 0;
        if (drag == 6) return 1;
        if (drag == 8) return 2;
        if (drag == 10) return 3;
        return 4;
    }

    private boolean isStatusBarsEnabled() {
        return com.client.Configuration.statusBars;
    }

    private boolean isRemoveRoofsEnabled() {
        return Client.removeRoofs;
    }

    private boolean isMenuHoversEnabled() {
        return com.client.Configuration.menuHovers;
    }

    private int booleanToIndex(boolean value) {
        return value ? 0 : 1;
    }

    private void persistSettings() {
        try {
            SettingsManager.saveSettings(Client.getInstance());
        } catch (Exception ignored) {
        }
        Preferences.save();
    }

    private interface SliderValueConsumer {
        void accept(double value);
    }
}
