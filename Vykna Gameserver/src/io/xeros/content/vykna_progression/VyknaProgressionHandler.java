package io.xeros.content.vykna_progression;

import io.xeros.content.skills.Skill;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public final class VyknaProgressionHandler {

    public static final int ACHIEVEMENTS_INTERFACE_ID = VyknaProgressionInterfaces.HOME_INTERFACE_ID;
    private static final int CLIENT_SCRIPT_ID = 5;
    private static final String KEY_SKILL_LEVEL = "skill_level:";
    private static final String KEY_KC = "kc:";
    private static final String KEY_VISIT = "visit:";
    private static final String KEY_TOTAL_LEVEL = "total_level";
    private static final String KEY_TOTAL_XP = "total_xp";

    public VyknaProgressionHandler(Player player) {
    }

    /**
     * Opens the achievements interface. This should be the only entry-point for opening it.
     */
    public static void open(Player player) {
        if (player == null) return;

        player.getPA().showInterface(ACHIEVEMENTS_INTERFACE_ID);
        sendListTypes(player);
        addProgress(player, "open_progression", 1);
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
            case VyknaProgressionInterfaces.LIST_TAB_TASKS:
                openList(player, ProgressionListType.TASKS);
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
        VyknaProgressionPlayerState state = player.getVyknaProgressionState();
        for (ProgressionEntry entry : definition.getEntries()) {
            boolean completed = state.isCompleted(entry.getEntryId());
            int progress = state.getProgress(entry.getEntryId());
            DerivedProgress derivedProgress = resolveDerivedProgress(player, state, entry);
            if (derivedProgress != null) {
                progress = derivedProgress.progress;
                completed = derivedProgress.completed;
            }
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
        private final int spriteIndex;
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
            this.spriteIndex = entry.getSpriteIndex();
            this.completed = completed;
            this.progressCurrent = progressCurrent;
        }
    }

    private static DerivedProgress resolveDerivedProgress(Player player, VyknaProgressionPlayerState state, ProgressionEntry entry) {
        if (player == null || state == null || entry == null) {
            return null;
        }
        String key = entry.getRequirementKey();
        if (key == null || key.isEmpty()) {
            return null;
        }
        String lowerKey = key.toLowerCase();
        int progress;
        if (lowerKey.startsWith(KEY_SKILL_LEVEL)) {
            String skillName = key.substring(KEY_SKILL_LEVEL.length()).trim();
            Skill skill = resolveSkill(skillName);
            if (skill == null) {
                return null;
            }
            progress = player.getPA().getLevelForXP(player.playerXP[skill.getId()]);
        } else if (lowerKey.startsWith(KEY_KC)) {
            String npcName = key.substring(KEY_KC.length()).trim();
            progress = player.getNpcDeathTracker().getKc(npcName);
        } else if (lowerKey.startsWith(KEY_VISIT)) {
            String locationKey = key.substring(KEY_VISIT.length()).trim();
            Boundary boundary = resolveBoundary(locationKey);
            if (boundary == null) {
                return null;
            }
            progress = Boundary.isIn(player, boundary) ? entry.getRequirementTarget() : state.getProgress(entry.getEntryId());
        } else if (lowerKey.equals(KEY_TOTAL_LEVEL)) {
            progress = player.getTotalLevel();
        } else if (lowerKey.equals(KEY_TOTAL_XP)) {
            progress = (int) Math.min(Integer.MAX_VALUE, player.getTotalExperience());
        } else {
            return null;
        }
        int target = entry.getRequirementTarget();
        boolean completed = progress >= target;
        if (completed && !state.isCompleted(entry.getEntryId())) {
            state.setCompleted(entry.getEntryId(), true);
            state.addPoints(entry.getPoints());
        }
        state.setProgress(entry.getEntryId(), Math.min(progress, target));
        return new DerivedProgress(Math.min(progress, target), state.isCompleted(entry.getEntryId()));
    }

    private static Skill resolveSkill(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        try {
            return Skill.valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private static Boundary resolveBoundary(String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }
        switch (key.trim().toLowerCase()) {
            case "varrock":
                return Boundary.VARROCK_BOUNDARY;
            case "falador":
                return Boundary.FALADOR_BOUNDARY;
            case "lumbridge":
                return Boundary.LUMRIDGE_BOUNDARY;
            case "draynor":
                return Boundary.DRAYNOR_BOUNDARY;
            case "al_kharid":
                return Boundary.AL_KHARID_BOUNDARY;
            case "ardougne":
                return Boundary.ARDOUGNE_BOUNDARY;
            case "seers":
                return Boundary.SEERS_BOUNDARY;
            case "catherby":
                return Boundary.CATHERBY_BOUNDARY;
            case "taverly":
                return Boundary.TAVERLY_BOUNDARY;
            case "karamja":
                return Boundary.KARAMJA_BOUNDARY;
            case "brimhaven":
                return Boundary.BRIMHAVEN_BOUNDARY;
            case "canifis":
                return Boundary.CANIFIS_BOUNDARY;
            case "rellekka":
                return Boundary.RELLEKKA_BOUNDARY;
            case "yanille":
                return Boundary.YANILLE_BOUNDARY;
            case "gnome_stronghold":
                return Boundary.GNOME_STRONGHOLD_BOUNDARY;
            case "desert":
                return Boundary.DESERT_BOUNDARY;
            case "feldip_hills":
                return Boundary.FELDIP_HILLS_BOUNDARY;
            case "ape_atoll":
                return Boundary.APE_ATOLL_BOUNDARY;
            case "lunar_isle":
                return Boundary.LUNAR_ISLE_BOUNDARY;
            case "fremennik_isles":
                return Boundary.FREMENNIK_ISLES_BOUNDARY;
            case "waterbirth":
                return Boundary.WATERBIRTH_ISLAND_BOUNDARY;
            case "miscellania":
                return Boundary.MISCELLANIA_BOUNDARY;
            case "tzhaar":
                return Boundary.TZHAAR_CITY_BOUNDARY;
            case "zeah":
                return Boundary.ZEAH_BOUNDARY;
            case "lletya":
                return Boundary.LLETYA_BOUNDARY;
            case "bandit_camp":
                return Boundary.BANDIT_CAMP_BOUNDARY;
            default:
                return null;
        }
    }

    private static final class DerivedProgress {
        private final int progress;
        private final boolean completed;

        private DerivedProgress(int progress, boolean completed) {
            this.progress = progress;
            this.completed = completed;
        }
    }
}
