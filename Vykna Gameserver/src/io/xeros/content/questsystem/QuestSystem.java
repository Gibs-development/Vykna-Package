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
import io.xeros.content.questsystem.sample.ImpCatcherQuestDefinition;
import io.xeros.content.questsystem.sample.ImpCatcherQuestHandler;
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
        registerHandler(new ImpCatcherQuestHandler());
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
        String questName = "Imp Catcher";
        io.xeros.content.questsystem.model.QuestProgress progress = player.getQuestProfile().getOrCreate(io.xeros.content.questsystem.sample.ImpCatcherQuestHandler.QUEST_ID);
        progress.ensureDefaults();
        switch (progress.getState()) {
            case COMPLETED:
                questName = "@gre@" + questName;
                break;
            case IN_PROGRESS:
                questName = "@yel@" + questName;
                break;
            default:
                questName = "@red@" + questName;
                break;
        }
        player.getPA().sendFrame126(questName, QUEST_LIST_FIRST_LINE);
    }

    public static boolean handleQuestListButton(Player player, int buttonId) {
        if (player == null) {
            return false;
        }
        if (buttonId == QUEST_LIST_FIRST_LINE) {
            openQuestJournal(player, io.xeros.content.questsystem.sample.ImpCatcherQuestHandler.QUEST_ID);
            return true;
        }
        return false;
    }

    public static void openQuestJournal(Player player, String questId) {
        if (player == null || questId == null || questId.isEmpty()) {
            return;
        }
        List<String> lines = getQuestJournalLines(player, questId);
        String title = questId.equalsIgnoreCase(io.xeros.content.questsystem.sample.ImpCatcherQuestHandler.QUEST_ID) ? "Imp Catcher" : questId;
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
        lines.add("I spoke to Wizard Mizgog.");
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
            lines.add("Quest Complete");
        }
        return lines;
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
