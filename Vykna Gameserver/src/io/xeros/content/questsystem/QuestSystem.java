package io.xeros.content.questsystem;

import io.xeros.content.questsystem.engine.QuestHandler;
import io.xeros.content.questsystem.engine.QuestHandlerRegistry;
import io.xeros.content.questsystem.engine.QuestHandlerRegistryImpl;
import io.xeros.content.questsystem.engine.StepQuestHandler;
import io.xeros.content.questsystem.event.QuestEvent;
import io.xeros.content.questsystem.item.QuestItemService;
import io.xeros.content.questsystem.model.QuestDefinition;
import io.xeros.content.questsystem.registry.QuestRegistry;
import io.xeros.content.questsystem.registry.QuestRegistryImpl;
import io.xeros.content.questsystem.sample.CooksAssistantQuestDefinition;
import io.xeros.content.questsystem.sample.DesertTreasureQuestDefinition;
import io.xeros.content.questsystem.sample.DesertTreasureQuestHandler;
import io.xeros.content.questsystem.sample.ImpCatcherQuestDefinition;
import io.xeros.content.questsystem.sample.ImpCatcherQuestHandler;
import io.xeros.content.questsystem.sample.WaterfallQuestDefinition;
import io.xeros.content.questsystem.sample.WaterfallQuestHandler;
import io.xeros.content.questsystem.service.QuestService;
import io.xeros.content.questsystem.service.QuestServiceImpl;
import io.xeros.model.entity.player.Player;

import java.util.List;

public final class QuestSystem {
    private static final QuestRegistry QUEST_REGISTRY = new QuestRegistryImpl();
    private static final QuestHandlerRegistry HANDLER_REGISTRY = new QuestHandlerRegistryImpl();
    private static final QuestService QUEST_SERVICE = new QuestServiceImpl(HANDLER_REGISTRY, QUEST_REGISTRY);
    private static boolean initialized;

    public static final int QUEST_LIST_INTERFACE_ID = 638;
    public static final int QUEST_LIST_FIRST_LINE = 639;
    public static final int QUEST_LIST_LINE_COUNT = 20;
    public static final int QUEST_JOURNAL_INTERFACE_ID = 8134;
    public static final int QUEST_JOURNAL_TITLE_ID = 8144;
    public static final int QUEST_JOURNAL_LINE_START = 8145;
    public static final int QUEST_JOURNAL_LINE_END = 8195;
    public static final int QUEST_JOURNAL_LINE_START_2 = 21614;
    public static final int QUEST_JOURNAL_LINE_END_2 = 21714;
    public static final int QUEST_REWARD_INTERFACE_ID = 297;
    public static final int QUEST_REWARD_TITLE_ID = 301;
    public static final int QUEST_REWARD_LINE_1 = 302;
    public static final int QUEST_REWARD_LINE_2 = 303;
    public static final int QUEST_REWARD_LINE_3 = 304;

    private QuestSystem() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        registerDefinition(new CooksAssistantQuestDefinition());
        registerDefinition(new ImpCatcherQuestDefinition());
        registerDefinition(new io.xeros.content.questsystem.sample.CourierFavorQuestDefinition());
        registerDefinition(new io.xeros.content.questsystem.sample.OneSmallFavorQuestDefinition());
        registerDefinition(new DesertTreasureQuestDefinition());
        registerDefinition(new WaterfallQuestDefinition());
        registerHandler(new ImpCatcherQuestHandler());
        registerHandler(new io.xeros.content.questsystem.sample.CourierFavorQuestHandler());
        registerHandler(new io.xeros.content.questsystem.sample.OneSmallFavorQuestHandler());
        registerHandler(new DesertTreasureQuestHandler());
        registerHandler(new WaterfallQuestHandler());
        QuestItemService.registerDefaultDefinitions();
    }

    public static void registerDefinition(QuestDefinition definition) {
        QUEST_REGISTRY.register(definition);
        HANDLER_REGISTRY.register(new StepQuestHandler(definition));
    }

    public static void registerHandler(QuestHandler handler) {
        HANDLER_REGISTRY.register(handler);
    }

    public static void handle(Player player, QuestEvent event) {
        QUEST_SERVICE.handle(player, event);
    }

    public static List<String> getQuestJournalLines(Player player, String questId) {
        if (player == null || questId == null) {
            return List.of("No quest data available.");
        }
        if (questId.equalsIgnoreCase(io.xeros.content.questsystem.sample.ImpCatcherQuestHandler.QUEST_ID)) {
            return buildImpCatcherJournal(player);
        }
        if (questId.equalsIgnoreCase(io.xeros.content.questsystem.sample.CourierFavorQuestHandler.QUEST_ID)) {
            return buildCourierFavorJournal(player);
        }
        if (questId.equalsIgnoreCase(io.xeros.content.questsystem.sample.OneSmallFavorQuestHandler.QUEST_ID)) {
            return buildOneSmallFavorJournal(player);
        }
        if (questId.equalsIgnoreCase(io.xeros.content.questsystem.sample.DesertTreasureQuestHandler.QUEST_ID)) {
            return buildDesertTreasureJournal(player);
        }
        if (questId.equalsIgnoreCase(io.xeros.content.questsystem.sample.WaterfallQuestHandler.QUEST_ID)) {
            return buildWaterfallQuestJournal(player);
        }
        return QUEST_SERVICE.getQuestJournalLines(player, questId);
    }

    public static QuestDefinition getDefinition(String questId) {
        if (questId == null || questId.isEmpty()) {
            return null;
        }
        return QUEST_REGISTRY.find(new io.xeros.content.questsystem.model.QuestId(questId)).orElse(null);
    }

    public static io.xeros.content.questsystem.engine.QuestHandler getHandler(String questId) {
        if (questId == null || questId.isEmpty()) {
            return null;
        }
        return HANDLER_REGISTRY.find(questId).orElse(null);
    }

    public static QuestService getQuestService() {
        return QUEST_SERVICE;
    }

    public static void updateQuestList(Player player) {
        if (player == null) {
            return;
        }
        for (int i = 0; i < QUEST_LIST_LINE_COUNT; i++) {
            player.getPA().sendFrame126("", QUEST_LIST_FIRST_LINE + i);
        }
        int line = QUEST_LIST_FIRST_LINE;
        player.getPA().sendFrame126(formatQuestLine(player, io.xeros.content.questsystem.sample.ImpCatcherQuestHandler.QUEST_ID, "Imp Catcher"), line++);
        player.getPA().sendFrame126(formatQuestLine(player, io.xeros.content.questsystem.sample.CourierFavorQuestHandler.QUEST_ID, "Courier's Favor"), line++);
        player.getPA().sendFrame126(formatQuestLine(player, io.xeros.content.questsystem.sample.OneSmallFavorQuestHandler.QUEST_ID, "One Small Favour"), line++);
        player.getPA().sendFrame126(formatQuestLine(player, io.xeros.content.questsystem.sample.DesertTreasureQuestHandler.QUEST_ID, "Desert Treasure"), line++);
        player.getPA().sendFrame126(formatQuestLine(player, io.xeros.content.questsystem.sample.WaterfallQuestHandler.QUEST_ID, "Waterfall Quest"), line);
    }

    public static boolean handleQuestListButton(Player player, int buttonId) {
        if (player == null) {
            return false;
        }
        if (buttonId == QUEST_LIST_FIRST_LINE) {
            openQuestJournal(player, io.xeros.content.questsystem.sample.ImpCatcherQuestHandler.QUEST_ID);
            return true;
        }
        if (buttonId == QUEST_LIST_FIRST_LINE + 1) {
            openQuestJournal(player, io.xeros.content.questsystem.sample.CourierFavorQuestHandler.QUEST_ID);
            return true;
        }
        if (buttonId == QUEST_LIST_FIRST_LINE + 2) {
            openQuestJournal(player, io.xeros.content.questsystem.sample.OneSmallFavorQuestHandler.QUEST_ID);
            return true;
        }
        if (buttonId == QUEST_LIST_FIRST_LINE + 3) {
            openQuestJournal(player, io.xeros.content.questsystem.sample.DesertTreasureQuestHandler.QUEST_ID);
            return true;
        }
        if (buttonId == QUEST_LIST_FIRST_LINE + 4) {
            openQuestJournal(player, io.xeros.content.questsystem.sample.WaterfallQuestHandler.QUEST_ID);
            return true;
        }
        return false;
    }

    public static void openQuestJournal(Player player, String questId) {
        if (player == null || questId == null || questId.isEmpty()) {
            return;
        }
        List<String> lines = getQuestJournalLines(player, questId);
        String title;
        if (questId.equalsIgnoreCase(io.xeros.content.questsystem.sample.ImpCatcherQuestHandler.QUEST_ID)) {
            title = "Imp Catcher";
        } else if (questId.equalsIgnoreCase(io.xeros.content.questsystem.sample.CourierFavorQuestHandler.QUEST_ID)) {
            title = "Courier's Favor";
        } else if (questId.equalsIgnoreCase(io.xeros.content.questsystem.sample.OneSmallFavorQuestHandler.QUEST_ID)) {
            title = "One Small Favour";
        } else if (questId.equalsIgnoreCase(io.xeros.content.questsystem.sample.DesertTreasureQuestHandler.QUEST_ID)) {
            title = "Desert Treasure";
        } else if (questId.equalsIgnoreCase(io.xeros.content.questsystem.sample.WaterfallQuestHandler.QUEST_ID)) {
            title = "Waterfall Quest";
        } else {
            title = questId;
        }
        player.getPA().sendFrame126(title, QUEST_JOURNAL_TITLE_ID);
        clearQuestJournalLines(player);
        int lineId = QUEST_JOURNAL_LINE_START;
        for (String line : lines) {
            if (lineId > QUEST_JOURNAL_LINE_END) {
                lineId = QUEST_JOURNAL_LINE_START_2;
            }
            if (lineId > QUEST_JOURNAL_LINE_END_2) {
                break;
            }
            player.getPA().sendFrame126(line, lineId++);
        }
        player.getPA().showInterface(QUEST_JOURNAL_INTERFACE_ID);
    }

    public static void openQuestReward(Player player, String questName) {
        if (player == null) {
            return;
        }
        player.getPA().sendFrame126(questName, QUEST_REWARD_TITLE_ID);
        player.getPA().sendFrame126("Quest Points: 1", QUEST_REWARD_LINE_1);
        player.getPA().sendFrame126("Magic XP: 875", QUEST_REWARD_LINE_2);
        player.getPA().sendFrame126("Amulet of Accuracy", QUEST_REWARD_LINE_3);
        player.getPA().showInterface(QUEST_REWARD_INTERFACE_ID);
    }

    private static void clearQuestJournalLines(Player player) {
        for (int line = QUEST_JOURNAL_LINE_START; line <= QUEST_JOURNAL_LINE_END; line++) {
            player.getPA().sendFrame126("", line);
        }
        for (int line = QUEST_JOURNAL_LINE_START_2; line <= QUEST_JOURNAL_LINE_END_2; line++) {
            player.getPA().sendFrame126("", line);
        }
    }

    private static List<String> buildImpCatcherJournal(Player player) {
        io.xeros.content.questsystem.model.QuestProgress progress = player.getQuestProfile().getOrCreate(io.xeros.content.questsystem.sample.ImpCatcherQuestHandler.QUEST_ID);
        progress.ensureDefaults();
        List<String> lines = new java.util.ArrayList<>();
        if (progress.getState() == io.xeros.content.questsystem.model.QuestState.NOT_STARTED) {
            lines.add("I can start this quest by speaking to Wizard Mizgog.");
            lines.add("To complete this quest I need: red bead,");
            lines.add("yellow bead, black bead, white bead.");
            return lines;
        }
        lines.add(strikeLine("I spoke to Wizard Mizgog."));
        lines.add("I need to find 4 beads for Mizgog:");
        lines.add("red, yellow, black, and white.");
        String obtained = getBeadStatus(player, true);
        String missing = getBeadStatus(player, false);
        if (!obtained.isEmpty()) {
            lines.add("Obtained: " + obtained);
        }
        if (!missing.isEmpty()) {
            lines.add("Missing: " + missing);
        }
        if (progress.getState() == io.xeros.content.questsystem.model.QuestState.COMPLETED) {
            for (int i = 1; i < lines.size(); i++) {
                lines.set(i, strikeLine(lines.get(i)));
            }
            lines.add("Quest Complete");
        }
        return lines;
    }

    private static List<String> buildCourierFavorJournal(Player player) {
        io.xeros.content.questsystem.model.QuestProgress progress = player.getQuestProfile().getOrCreate(io.xeros.content.questsystem.sample.CourierFavorQuestHandler.QUEST_ID);
        progress.ensureDefaults();
        List<String> lines = new java.util.ArrayList<>();
        if (progress.getState() == io.xeros.content.questsystem.model.QuestState.NOT_STARTED) {
            lines.add("I can start this quest by speaking to an Engineering assistant.");
            lines.add("They need a research package delivered to a banker.");
            return lines;
        }
        if (progress.getStage() >= io.xeros.content.questsystem.sample.CourierFavorQuestHandler.STAGE_STARTED) {
            lines.add(strikeLine("I agreed to deliver a research package."));
        }
        if (progress.getStage() >= io.xeros.content.questsystem.sample.CourierFavorQuestHandler.STAGE_DELIVERED) {
            lines.add(strikeLine("I delivered the package to the banker."));
            lines.add("I should return to the assistant for my reward.");
        } else {
            lines.add("I need to deliver the package to a banker.");
        }
        if (progress.getState() == io.xeros.content.questsystem.model.QuestState.COMPLETED) {
            for (int i = 0; i < lines.size(); i++) {
                lines.set(i, strikeLine(lines.get(i)));
            }
            lines.add("Quest Complete");
        }
        return lines;
    }

    private static List<String> buildOneSmallFavorJournal(Player player) {
        io.xeros.content.questsystem.model.QuestProgress progress = player.getQuestProfile().getOrCreate(io.xeros.content.questsystem.sample.OneSmallFavorQuestHandler.QUEST_ID);
        progress.ensureDefaults();
        List<String> lines = new java.util.ArrayList<>();
        if (progress.getState() == io.xeros.content.questsystem.model.QuestState.NOT_STARTED) {
            lines.add("I can start this quest by speaking to " + io.xeros.content.questsystem.sample.OneSmallFavorQuestHandler.getStartNpcName() + ".");
            lines.add("I've heard this might take a while...");
            return lines;
        }
        int stage = Math.max(0, progress.getStage());
        if (stage <= io.xeros.content.questsystem.sample.OneSmallFavorQuestHandler.STAGE_STARTED) {
            lines.add(strikeLine("I agreed to help with a very small favour."));
        } else {
            lines.add(strikeLine("I agreed to help with a very small favour."));
        }
        int completed = Math.max(0, stage - io.xeros.content.questsystem.sample.OneSmallFavorQuestHandler.STAGE_STARTED);
        for (int i = 0; i < completed; i++) {
            String npc = io.xeros.content.questsystem.sample.OneSmallFavorQuestHandler.getNpcNameForStep(i + 1);
            String item = io.xeros.content.questsystem.sample.OneSmallFavorQuestHandler.getItemNameForStep(i);
            lines.add(strikeLine("I delivered " + item + " to " + npc + "."));
        }
        if (progress.getState() != io.xeros.content.questsystem.model.QuestState.COMPLETED) {
            String npc = io.xeros.content.questsystem.sample.OneSmallFavorQuestHandler.getNpcNameForCurrentStage(stage);
            String item = io.xeros.content.questsystem.sample.OneSmallFavorQuestHandler.getItemNameForCurrentStage(stage);
            lines.add("I need to bring " + item + " to " + npc + ".");
        } else {
            for (int i = 0; i < lines.size(); i++) {
                lines.set(i, strikeLine(lines.get(i)));
            }
            lines.add("Quest Complete");
        }
        return lines;
    }

    private static List<String> buildDesertTreasureJournal(Player player) {
        io.xeros.content.questsystem.model.QuestProgress progress = player.getQuestProfile().getOrCreate(io.xeros.content.questsystem.sample.DesertTreasureQuestHandler.QUEST_ID);
        progress.ensureDefaults();
        List<String> lines = new java.util.ArrayList<>();
        if (progress.getState() == io.xeros.content.questsystem.model.QuestState.NOT_STARTED) {
            lines.add("I can start this quest by speaking to Eblis in Nardah.");
            lines.add("Eblis is searching for four ancient diamonds.");
            lines.add("I will need a ring of visibility to speak with Malak.");
            return lines;
        }
        lines.add(strikeLine("I agreed to help Eblis recover the four diamonds."));
        boolean hasRing = player.getItems().getTotalCount(io.xeros.content.questsystem.sample.DesertTreasureQuestHandler.RING_OF_VISIBILITY) > 0
                || progress.getStage() >= io.xeros.content.questsystem.sample.DesertTreasureQuestHandler.STAGE_RING_OBTAINED;
        if (hasRing) {
            lines.add(strikeLine("I obtained a ring of visibility from Rasolo."));
        } else {
            lines.add("I should obtain a ring of visibility from Rasolo.");
        }
        if (progress.getStage() >= io.xeros.content.questsystem.sample.DesertTreasureQuestHandler.STAGE_MALAK_TALKED) {
            lines.add(strikeLine("I spoke to Malak in Canifis."));
        } else {
            lines.add("I should speak to Malak in Canifis.");
        }
        boolean blood = getBoolVar(progress, "dt.bloodDiamond");
        boolean smoke = getBoolVar(progress, "dt.smokeDiamond");
        boolean ice = getBoolVar(progress, "dt.iceDiamond");
        boolean shadow = getBoolVar(progress, "dt.shadowDiamond");
        lines.add((blood ? strikeLine("Blood diamond obtained.") : "Blood diamond missing (Dessous)."));
        lines.add((smoke ? strikeLine("Smoke diamond obtained.") : "Smoke diamond missing (Fareed)."));
        lines.add((ice ? strikeLine("Ice diamond obtained.") : "Ice diamond missing (Kamil)."));
        lines.add((shadow ? strikeLine("Shadow diamond obtained.") : "Shadow diamond missing (Damis)."));
        if (blood && smoke && ice && shadow) {
            if (progress.getStage() < io.xeros.content.questsystem.sample.DesertTreasureQuestHandler.STAGE_AZZANADRA) {
                lines.add("I should use the four diamonds on the Ancient altar.");
            } else {
                lines.add("I should speak to Azzanadra.");
            }
        }
        if (progress.getState() == io.xeros.content.questsystem.model.QuestState.COMPLETED) {
            for (int i = 0; i < lines.size(); i++) {
                lines.set(i, strikeLine(lines.get(i)));
            }
            lines.add("Quest Complete");
        }
        return lines;
    }

    private static List<String> buildWaterfallQuestJournal(Player player) {
        io.xeros.content.questsystem.model.QuestProgress progress = player.getQuestProfile().getOrCreate(io.xeros.content.questsystem.sample.WaterfallQuestHandler.QUEST_ID);
        progress.ensureDefaults();
        List<String> lines = new java.util.ArrayList<>();
        if (progress.getState() == io.xeros.content.questsystem.model.QuestState.NOT_STARTED) {
            lines.add("I can start this quest by speaking to Almera.");
            lines.add("She needs help at the waterfall.");
            return lines;
        }
        lines.add(strikeLine("I agreed to help Almera."));
        if (progress.getStage() >= io.xeros.content.questsystem.sample.WaterfallQuestHandler.STAGE_ROPE_USED) {
            lines.add(strikeLine("I secured a rope to reach the waterfall cave."));
        } else {
            lines.add("I should use a rope on the rock near the waterfall.");
        }
        if (progress.getStage() >= io.xeros.content.questsystem.sample.WaterfallQuestHandler.STAGE_AMULET_OBTAINED
                || player.getItems().playerHasItem(io.xeros.content.questsystem.sample.WaterfallQuestHandler.GLARIAL_AMULET, 1)) {
            lines.add(strikeLine("I obtained Glarial's amulet."));
        } else {
            lines.add("I need Glarial's amulet to enter the cave.");
        }
        if (progress.getStage() >= io.xeros.content.questsystem.sample.WaterfallQuestHandler.STAGE_CAVE_ENTERED) {
            lines.add(strikeLine("I entered the waterfall cave."));
        } else {
            lines.add("I should wear the amulet to enter the cave.");
        }
        if (progress.getStage() >= io.xeros.content.questsystem.sample.WaterfallQuestHandler.STAGE_URN_OBTAINED
                || player.getItems().playerHasItem(io.xeros.content.questsystem.sample.WaterfallQuestHandler.GLARIAL_URN, 1)) {
            lines.add(strikeLine("I recovered Glarial's urn from Golrie."));
        } else {
            lines.add("I should look for Golrie in the cave.");
        }
        if (progress.getState() == io.xeros.content.questsystem.model.QuestState.COMPLETED) {
            for (int i = 0; i < lines.size(); i++) {
                lines.set(i, strikeLine(lines.get(i)));
            }
            lines.add("Quest Complete");
        }
        return lines;
    }

    private static String formatQuestLine(Player player, String questId, String questName) {
        io.xeros.content.questsystem.model.QuestProgress progress = player.getQuestProfile().getOrCreate(questId);
        progress.ensureDefaults();
        switch (progress.getState()) {
            case COMPLETED:
                return "@gre@" + questName;
            case IN_PROGRESS:
                return "@yel@" + questName;
            default:
                return "@red@" + questName;
        }
    }

    private static String strikeLine(String line) {
        return line == null || line.isEmpty() ? line : "<str>" + line;
    }

    private static boolean getBoolVar(io.xeros.content.questsystem.model.QuestProgress progress, String key) {
        Object value = progress.getVars().get(key);
        return value instanceof Boolean && (Boolean) value;
    }

    private static String getBeadStatus(Player player, boolean obtained) {
        java.util.List<String> beads = new java.util.ArrayList<>();
        if (player.getItems().playerHasItem(io.xeros.content.questsystem.sample.ImpCatcherQuestHandler.RED_BEAD, 1) == obtained) {
            beads.add("red");
        }
        if (player.getItems().playerHasItem(io.xeros.content.questsystem.sample.ImpCatcherQuestHandler.YELLOW_BEAD, 1) == obtained) {
            beads.add("yellow");
        }
        if (player.getItems().playerHasItem(io.xeros.content.questsystem.sample.ImpCatcherQuestHandler.BLACK_BEAD, 1) == obtained) {
            beads.add("black");
        }
        if (player.getItems().playerHasItem(io.xeros.content.questsystem.sample.ImpCatcherQuestHandler.WHITE_BEAD, 1) == obtained) {
            beads.add("white");
        }
        return String.join(", ", beads);
    }
}
