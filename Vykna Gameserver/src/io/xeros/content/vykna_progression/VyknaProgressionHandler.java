package io.xeros.content.vykna_progression;

import io.xeros.model.entity.player.Player;
import io.xeros.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public final class VyknaProgressionHandler {

    public static final int ACHIEVEMENTS_INTERFACE_ID = VyknaProgressionInterfaces.HOME_INTERFACE_ID;
    private static final int CLIENT_SCRIPT_ID = 5;

    public VyknaProgressionHandler(Player player) {
    }

    /**
     * Opens the achievements interface. This should be the only entry-point for opening it.
     */
    public static void open(Player player) {
        if (player == null) return;

        player.getPA().showInterface(ACHIEVEMENTS_INTERFACE_ID);
        sendListTypes(player);
    }

    public static void openList(Player player, ProgressionListType listType) {
        if (player == null || listType == null) return;

        player.getPA().showInterface(VyknaProgressionInterfaces.LIST_INTERFACE_ID);
        sendListData(player, listType);
    }

    public static boolean handleButton(Player player, int buttonId) {
        if (player == null) return false;

        switch (buttonId) {
            case VyknaProgressionInterfaces.HOME_TAB_TASKS:
                openList(player, ProgressionListType.TASKS);
                return true;
            case VyknaProgressionInterfaces.HOME_TAB_SKILLING:
                openList(player, ProgressionListType.SKILLS);
                return true;
            case VyknaProgressionInterfaces.HOME_TAB_COMBAT:
                openList(player, ProgressionListType.COMBAT);
                return true;
            case VyknaProgressionInterfaces.LIST_TAB_HOME:
                open(player);
                return true;
            case VyknaProgressionInterfaces.LIST_TAB_SKILLING:
                openList(player, ProgressionListType.SKILLS);
                return true;
            case VyknaProgressionInterfaces.LIST_TAB_COMBAT:
                openList(player, ProgressionListType.COMBAT);
                return true;
            default:
                return false;
        }
    }

    /**
     * Central click handler for the achievements interface.
     * Wire this from your Button/Interface action listener.
     *
     * @return true if this handler consumed the click.
     */
    public static boolean handleClick(Player player, int interfaceId, int componentId, int opcode) {
        if (player == null) return false;
        if (interfaceId != ACHIEVEMENTS_INTERFACE_ID) return false;

        // TODO: Hook your actual component ids here once you finalize the client interface.
        // Keep all achievements UI clicking routed through this method.

        switch (componentId) {
            // Example placeholders:
            // case 35010: openTasks(player); return true;
            // case 35011: openCombat(player); return true;
            // case 35012: openSkilling(player); return true;

            default:
                return false;
        }
    }

    public static void addProgress(Player player, String requirementKey, int amount) {
        if (player == null || requirementKey == null || amount <= 0) {
            return;
        }
        VyknaProgressionPlayerState state = player.getVyknaProgressionState();
        for (ProgressionListDefinition definition : VyknaProgressionRegistry.getAll().values()) {
            for (ProgressionEntry entry : definition.getEntries()) {
                if (!requirementKey.equalsIgnoreCase(entry.getRequirementKey())) {
                    continue;
                }
                int current = state.getProgress(entry.getEntryId());
                int target = entry.getRequirementTarget();
                int updated = Math.min(target, current + amount);
                state.setProgress(entry.getEntryId(), updated);
                if (updated >= target && !state.isCompleted(entry.getEntryId())) {
                    state.setCompleted(entry.getEntryId(), true);
                    state.addPoints(entry.getPoints());
                }
            }
        }
    }

    private static void sendListTypes(Player player) {
        List<ListTypePayload> listTypes = new ArrayList<>();
        for (ProgressionListType type : ProgressionListType.values()) {
            listTypes.add(new ListTypePayload(type.getId(), type.getDisplayName()));
        }
        player.getPA().runClientScript(CLIENT_SCRIPT_ID, "listTypes", JsonUtil.toJson(listTypes));
    }

    private static void sendListData(Player player, ProgressionListType listType) {
        ProgressionListDefinition definition = VyknaProgressionRegistry.getByListTypeId(listType.getId());
        if (definition == null) {
            return;
        }

        List<EntryPayload> entries = new ArrayList<>();
        for (ProgressionEntry entry : definition.getEntries()) {
            boolean completed = player.getVyknaProgressionState().isCompleted(entry.getEntryId());
            int progress = player.getVyknaProgressionState().getProgress(entry.getEntryId());
            entries.add(new EntryPayload(entry, completed, progress));
        }

        ListPayload payload = new ListPayload(
                definition.getId(),
                definition.getSubcategories(),
                entries
        );
        player.getPA().runClientScript(CLIENT_SCRIPT_ID, "listData", JsonUtil.toJson(payload));
    }

    private static final class ListTypePayload {
        private final int id;
        private final String displayName;

        private ListTypePayload(int id, String displayName) {
            this.id = id;
            this.displayName = displayName;
        }
    }

    private static final class ListPayload {
        private final int listTypeId;
        private final List<String> subcategories;
        private final List<EntryPayload> entries;

        private ListPayload(int listTypeId, List<String> subcategories, List<EntryPayload> entries) {
            this.listTypeId = listTypeId;
            this.subcategories = subcategories;
            this.entries = entries;
        }
    }

    private static final class EntryPayload {
        private final int entryId;
        private final String name;
        private final String description;
        private final int listTypeId;
        private final String subcategory;
        private final int points;
        private final String requirementKey;
        private final int requirementTarget;
        private final boolean completed;
        private final int progressCurrent;

        private EntryPayload(ProgressionEntry entry, boolean completed, int progressCurrent) {
            this.entryId = entry.getEntryId();
            this.name = entry.getName();
            this.description = entry.getDescription();
            this.listTypeId = entry.getListTypeId();
            this.subcategory = entry.getSubcategory();
            this.points = entry.getPoints();
            this.requirementKey = entry.getRequirementKey();
            this.requirementTarget = entry.getRequirementTarget();
            this.completed = completed;
            this.progressCurrent = progressCurrent;
        }
    }
}
