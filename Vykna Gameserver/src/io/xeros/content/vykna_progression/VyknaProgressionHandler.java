package io.xeros.content.vykna_progression;

import io.xeros.content.skills.Skill;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.util.JsonUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class VyknaProgressionHandler {

    public static final int ACHIEVEMENTS_INTERFACE_ID = VyknaProgressionInterfaces.HOME_INTERFACE_ID;
    private static final int CLIENT_SCRIPT_ID = 5;
    private static final String KEY_SKILL_LEVEL = "skill_level:";
    private static final String KEY_KC = "kc:";
    private static final String KEY_VISIT = "visit:";
    private static final String KEY_TOTAL_LEVEL = "total_level";
    private static final String KEY_TOTAL_XP = "total_xp";
    private static final int DEFAULT_PAGE_SIZE = 25;
    private static final int MAX_LIST_JSON_CHARS = 12000;
    private static final Map<String, Integer> SCORE_BY_PLAYER = new HashMap<>();

    public VyknaProgressionHandler(Player player) {
    }

    /**
     * Opens the achievements interface. This should be the only entry-point for opening it.
     */
    public static void open(Player player) {
        if (player == null) return;

        player.getPA().showInterface(ACHIEVEMENTS_INTERFACE_ID);
        sendListTypes(player);
        sendListData(player, ProgressionListType.TASKS);
        sendListData(player, ProgressionListType.SKILLS);
        sendListData(player, ProgressionListType.COMBAT);
        updateLeaderboard(player, player.getVyknaProgressionState());
        sendSummaryData(player);
        addProgress(player, "open_progression", 1);
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
            case VyknaProgressionInterfaces.LIST_TAB_TASKS_REAL:
                openList(player, ProgressionListType.TASKS);
                return true;
            case VyknaProgressionInterfaces.LIST_TAB_SKILLING:
                openList(player, ProgressionListType.SKILLS);
                return true;
            case VyknaProgressionInterfaces.LIST_TAB_COMBAT:
                openList(player, ProgressionListType.COMBAT);
                return true;
            case VyknaProgressionInterfaces.LIST_TOGGLE_COMPLETED:
                toggleShowCompleted(player);
                return true;
            case VyknaProgressionInterfaces.LIST_CLOSE:
            case VyknaProgressionInterfaces.HOME_CLOSE:
                player.getPA().closeAllWindows();
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
                    state.addScore(entry.getPoints());
                    state.setLastCompleted(entry.getEntryId(), entry.getListTypeId());
                    updateLeaderboard(player, state);
                    sendSummaryData(player);
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

    private static void sendSummaryData(Player player) {
        if (player == null) {
            return;
        }
        VyknaProgressionPlayerState state = player.getVyknaProgressionState();
        SummaryPayload payload = new SummaryPayload(
                state.getScoreTotal(),
                state.getPointsTotal(),
                state.getLastCompletedEntryId(),
                state.getLastCompletedListTypeId(),
                state.isShowCompleted(),
                getTopLeaderboard()
        );
        player.getPA().runClientScript(CLIENT_SCRIPT_ID, "summaryData", JsonUtil.toJson(payload));
    }

    private static void toggleShowCompleted(Player player) {
        VyknaProgressionPlayerState state = player.getVyknaProgressionState();
        boolean show = !state.isShowCompleted();
        state.setShowCompleted(show);
        player.getPA().runClientScript(CLIENT_SCRIPT_ID, "toggleCompleted", show ? 1 : 0);
    }

    private static void updateLeaderboard(Player player, VyknaProgressionPlayerState state) {
        String name = player == null ? "unknown" : player.playerName;
        SCORE_BY_PLAYER.put(name, state.getScoreTotal());
    }

    private static List<LeaderboardEntry> getTopLeaderboard() {
        List<LeaderboardEntry> entries = new ArrayList<>();
        SCORE_BY_PLAYER.forEach((name, score) -> entries.add(new LeaderboardEntry(name, score)));
        entries.sort(Comparator.comparingInt(LeaderboardEntry::getScore).reversed());
        return entries.subList(0, Math.min(3, entries.size()));
    }

// Put these inside the same class as openList/sendListData

    private static final boolean DEBUG_PROGRESSIONS = true;

    private static void debug(Player player, String msg) {
        if (!DEBUG_PROGRESSIONS) return;
        String name;
        try {
            // Adjust if your Player uses a different field/getter.
            name = (player == null ? "null" : player.playerName);
        } catch (Exception ignored) {
            name = "unknown";
        }
        System.out.println("[VyknaProgressions] [" + name + "] " + msg);
    }

    private static void error(Player player, String msg, Throwable t) {
        String name;
        try {
            name = (player == null ? "null" : player.playerName);
        } catch (Exception ignored) {
            name = "unknown";
        }
        System.err.println("[VyknaProgressions][ERROR] [" + name + "] " + msg);
        if (t != null) t.printStackTrace();
    }

    public static void openList(Player player, ProgressionListType listType) {
        if (player == null || listType == null) return;

        try {
            debug(player, "openList() -> showInterface id=" + VyknaProgressionInterfaces.LIST_INTERFACE_ID
                    + ", listType=" + listType + " (id=" + listType.getId() + ")");
            player.getPA().showInterface(VyknaProgressionInterfaces.LIST_INTERFACE_ID);
        } catch (Exception e) {
            error(player, "openList() failed while showing interface.", e);
            // Optional: player.sendMessage("Progressions UI failed to open. Check server logs.");
            return;
        }

        try {
            sendListData(player, listType);
        } catch (Exception e) {
            error(player, "openList() failed while sending list data for listType id=" + listType.getId(), e);
            // Optional: player.sendMessage("Failed to load progression list. Check server logs.");
        }
    }

    private static void sendListData(Player player, ProgressionListType listType) {
        debug(player, "sendListData() start for listType=" + listType + " (id=" + listType.getId() + ")");

        ProgressionListDefinition definition;
        try {
            definition = VyknaProgressionRegistry.getByListTypeId(listType.getId());
        } catch (Exception e) {
            error(player, "Registry lookup threw for listTypeId=" + listType.getId(), e);
            return;
        }

        if (definition == null) {
            debug(player, "No ProgressionListDefinition found for listTypeId=" + listType.getId() + " (definition=null)");
            return;
        }

        List<ProgressionEntry> defEntries = null;
        try {
            defEntries = definition.getEntries();
        } catch (Exception e) {
            error(player, "definition.getEntries() threw. definitionId=" + safeDefId(definition), e);
            return;
        }

        if (defEntries == null) {
            debug(player, "definition.getEntries() returned null. definitionId=" + safeDefId(definition));
            return;
        }

        VyknaProgressionPlayerState state;
        try {
            state = player.getVyknaProgressionState();
        } catch (Exception e) {
            error(player, "player.getVyknaProgressionState() threw.", e);
            return;
        }

        if (state == null) {
            debug(player, "player progression state is null (state=null). Cannot build list.");
            return;
        }

        debug(player, "Building payload: definitionId=" + safeDefId(definition)
                + ", subcats=" + (definition.getSubcategories() == null ? "null" : definition.getSubcategories().size())
                + ", entries=" + defEntries.size());

        List<EntryPayload> entries = new ArrayList<>(defEntries.size());

        for (int i = 0; i < defEntries.size(); i++) {
            ProgressionEntry entry = defEntries.get(i);
            if (entry == null) {
                debug(player, "Null entry at index=" + i + " in definitionId=" + safeDefId(definition));
                continue;
            }

            try {
                boolean completed = state.isCompleted(entry.getEntryId());
                int progress = state.getProgress(entry.getEntryId());

                DerivedProgress derivedProgress = null;
                try {
                    derivedProgress = resolveDerivedProgress(player, state, entry);
                } catch (Exception e) {
                    // isolate derived resolver issues specifically
                    error(player, "resolveDerivedProgress() threw for entryId=" + entry.getEntryId()
                            + " (index=" + i + ", definitionId=" + safeDefId(definition) + ")", e);
                }

                if (derivedProgress != null) {
                    progress = derivedProgress.progress;
                    completed = derivedProgress.completed;
                }

                entries.add(new EntryPayload(entry, completed, progress));
            } catch (Exception e) {
                error(player, "Failed building EntryPayload for entryId=" + safeEntryId(entry)
                        + " (index=" + i + ", definitionId=" + safeDefId(definition) + ")", e);
            }
        }

        sendPagedListData(player, definition, entries);
    }

    private static void sendPagedListData(Player player, ProgressionListDefinition definition, List<EntryPayload> entries) {
        int totalEntries = entries.size();
        int pageSize = DEFAULT_PAGE_SIZE;
        int pageIndex = 0;
        int start = 0;
        int totalPages = Math.max(1, (int) Math.ceil(totalEntries / (double) pageSize));

        debug(player, "Paging list payload: totalEntries=" + totalEntries
                + ", pageSize=" + pageSize
                + ", totalPages=" + totalPages
                + ", maxJsonChars=" + MAX_LIST_JSON_CHARS);

        while (start < totalEntries) {
            int end = Math.min(start + pageSize, totalEntries);
            List<EntryPayload> pageEntries = new ArrayList<>(entries.subList(start, end));
            ListPayload payload;
            try {
                payload = new ListPayload(
                        definition.getId(),
                        definition.getSubcategories(),
                        pageEntries,
                        pageIndex,
                        pageSize,
                        totalEntries,
                        totalPages
                );
            } catch (Exception e) {
                error(player, "Failed constructing ListPayload page. definitionId=" + safeDefId(definition)
                        + ", pageIndex=" + pageIndex, e);
                return;
            }

            String json;
            try {
                json = JsonUtil.toJson(payload);
            } catch (Exception e) {
                error(player, "JsonUtil.toJson(payload) threw. definitionId=" + safeDefId(definition)
                        + ", pageIndex=" + pageIndex
                        + ", entriesBuilt=" + pageEntries.size(), e);
                return;
            }

            if (json != null && json.length() > MAX_LIST_JSON_CHARS && pageSize > 1) {
                int nextPageSize = Math.max(1, pageSize / 2);
                debug(player, "Payload page exceeded limit, shrinking pageSize from " + pageSize
                        + " to " + nextPageSize
                        + " (jsonLength=" + json.length() + ")");
                pageSize = nextPageSize;
                totalPages = Math.max(1, (int) Math.ceil(totalEntries / (double) pageSize));
                continue;
            }

            if (json != null && json.length() > MAX_LIST_JSON_CHARS) {
                debug(player, "Payload page still exceeds safe limit, skipping send. definitionId=" + safeDefId(definition)
                        + ", pageIndex=" + pageIndex
                        + ", jsonLength=" + json.length()
                        + ", entriesBuilt=" + pageEntries.size());
                start = end;
                pageIndex++;
                continue;
            }

            debug(player, "Sending clientscript page: id=" + CLIENT_SCRIPT_ID
                    + ", key=listData, pageIndex=" + pageIndex
                    + ", pageSize=" + pageSize
                    + ", jsonLength=" + (json == null ? -1 : json.length())
                    + ", entriesBuilt=" + pageEntries.size());

            try {
                player.getPA().runClientScript(CLIENT_SCRIPT_ID, "listData", json);
            } catch (Exception e) {
                error(player, "runClientScript() threw. clientScriptId=" + CLIENT_SCRIPT_ID
                        + ", definitionId=" + safeDefId(definition)
                        + ", pageIndex=" + pageIndex, e);
                return;
            }

            start = end;
            pageIndex++;
        }
    }

    private static int safeDefId(ProgressionListDefinition def) {
        try { return def == null ? -1 : def.getId(); } catch (Exception e) { return -2; }
    }

    private static int safeEntryId(ProgressionEntry entry) {
        try { return entry == null ? -1 : entry.getEntryId(); } catch (Exception e) { return -2; }
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
        private final int pageIndex;
        private final int pageSize;
        private final int totalEntries;
        private final int totalPages;

        private ListPayload(int listTypeId, List<String> subcategories, List<EntryPayload> entries,
                            int pageIndex, int pageSize, int totalEntries, int totalPages) {
            this.listTypeId = listTypeId;
            this.subcategories = subcategories;
            this.entries = entries;
            this.pageIndex = pageIndex;
            this.pageSize = pageSize;
            this.totalEntries = totalEntries;
            this.totalPages = totalPages;
        }
    }

    private static final class SummaryPayload {
        private final int scoreTotal;
        private final int pointsTotal;
        private final int lastCompletedEntryId;
        private final int lastCompletedListTypeId;
        private final boolean showCompleted;
        private final List<LeaderboardEntry> leaderboard;

        private SummaryPayload(int scoreTotal, int pointsTotal, int lastCompletedEntryId, int lastCompletedListTypeId,
                               boolean showCompleted, List<LeaderboardEntry> leaderboard) {
            this.scoreTotal = scoreTotal;
            this.pointsTotal = pointsTotal;
            this.lastCompletedEntryId = lastCompletedEntryId;
            this.lastCompletedListTypeId = lastCompletedListTypeId;
            this.showCompleted = showCompleted;
            this.leaderboard = leaderboard;
        }
    }

    private static final class LeaderboardEntry {
        private final String name;
        private final int score;

        private LeaderboardEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }

        private int getScore() {
            return score;
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
            state.addScore(entry.getPoints());
            state.setLastCompleted(entry.getEntryId(), entry.getListTypeId());
            updateLeaderboard(player, state);
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
