package io.xeros.content.questsystem.sample;

import io.xeros.content.questsystem.QuestSystem;
import io.xeros.content.questsystem.engine.QuestHandler;
import io.xeros.content.questsystem.event.QuestEvent;
import io.xeros.content.questsystem.event.QuestEventKeys;
import io.xeros.content.questsystem.event.QuestEventType;
import io.xeros.content.questsystem.model.QuestProgress;
import io.xeros.content.questsystem.model.QuestState;
import io.xeros.model.entity.player.Player;

public class WaterfallQuestHandler implements QuestHandler {
    public static final String QUEST_ID = "waterfall_quest";
    public static final int STAGE_NOT_STARTED = 0;
    public static final int STAGE_STARTED = 10;
    public static final int STAGE_ROPE_USED = 20;
    public static final int STAGE_AMULET_OBTAINED = 30;
    public static final int STAGE_CAVE_ENTERED = 40;
    public static final int STAGE_URN_OBTAINED = 50;
    public static final int STAGE_COMPLETE = 100;

    public static final int ROPE = 954;
    public static final int GLARIAL_PEBBLE = 294;
    public static final int GLARIAL_AMULET = 295;
    public static final int GLARIAL_URN = 296;

    public static final int NPC_ALMERA = 4181;
    public static final int NPC_HUDON = 4182;
    public static final int NPC_GOLRIE = 4183;
    public static final int NPC_GOLRIE_ALT = 892;
    public static final int NPC_HUDO = 12;

    public static final int OBJECT_ROPE_ROCK_ID = 1999;
    public static final int OBJECT_PEBBLE_STATUE_ID = 2000;

    @Override
    public String questId() {
        return QUEST_ID;
    }

    @Override
    public int completionStage() {
        return STAGE_COMPLETE;
    }

    @Override
    public int nextStage(Player player, QuestProgress progress, QuestEvent event) {
        if (player == null || progress == null || event == null) {
            return progress == null ? STAGE_NOT_STARTED : progress.getStage();
        }
        if (progress.getState() != QuestState.IN_PROGRESS) {
            return progress.getStage();
        }
        switch (event.getType()) {
            case ITEM_ON_OBJECT:
                return handleItemOnObject(player, progress, event);
            case NPC_TALK:
                return handleNpcTalk(player, progress, event);
            default:
                return progress.getStage();
        }
    }

    private int handleItemOnObject(Player player, QuestProgress progress, QuestEvent event) {
        Object objectValue = event.get(QuestEventKeys.OBJECT_ID);
        Object itemValue = event.get(QuestEventKeys.ITEM_ID);
        if (!(objectValue instanceof Number) || !(itemValue instanceof Number)) {
            return progress.getStage();
        }
        int objectId = ((Number) objectValue).intValue();
        int itemId = ((Number) itemValue).intValue();
        if (itemId == ROPE && objectId == OBJECT_ROPE_ROCK_ID) {
            player.sendMessage("You secure the rope to the rock.");
            return advanceTo(progress, STAGE_ROPE_USED);
        }
        if (itemId == GLARIAL_PEBBLE && objectId == OBJECT_PEBBLE_STATUE_ID) {
            if (player.getItems().playerHasItem(GLARIAL_AMULET, 1)) {
                return progress.getStage();
            }
            player.getItems().deleteItem2(GLARIAL_PEBBLE, 1);
            player.getItems().addItemUnderAnyCircumstance(GLARIAL_AMULET, 1);
            player.sendMessage("You take Glarial's amulet from the statue.");
            return advanceTo(progress, STAGE_AMULET_OBTAINED);
        }
        return progress.getStage();
    }

    private int handleNpcTalk(Player player, QuestProgress progress, QuestEvent event) {
        Object npcValue = event.get(QuestEventKeys.NPC_ID);
        if (!(npcValue instanceof Number)) {
            return progress.getStage();
        }
        int npcId = ((Number) npcValue).intValue();
        if (npcId == NPC_GOLRIE || npcId == NPC_GOLRIE_ALT) {
            if (progress.getStage() >= STAGE_CAVE_ENTERED && progress.getStage() < STAGE_URN_OBTAINED) {
                if (!player.getItems().playerHasItem(GLARIAL_URN, 1)) {
                    player.getItems().addItemUnderAnyCircumstance(GLARIAL_URN, 1);
                }
                return advanceTo(progress, STAGE_URN_OBTAINED);
            }
            return progress.getStage();
        }
        if (npcId == NPC_ALMERA) {
            if (progress.getStage() >= STAGE_URN_OBTAINED && player.getItems().playerHasItem(GLARIAL_URN, 1)) {
                player.getItems().deleteItem2(GLARIAL_URN, 1);
                grantCompletionRewards(player);
                return STAGE_COMPLETE;
            }
        }
        if (npcId == NPC_HUDON || npcId == NPC_HUDO) {
            return progress.getStage();
        }
        return progress.getStage();
    }

    private void grantCompletionRewards(Player player) {
        player.getPA().addSkillXP(13750, 0, true);
        player.getPA().addSkillXP(13750, 2, true);
        player.sendMessage("You receive a reward for completing Waterfall Quest.");
        player.getPA().sendFrame126("Waterfall Quest", QuestSystem.QUEST_REWARD_TITLE_ID);
        player.getPA().sendFrame126("Quest Points: 1", QuestSystem.QUEST_REWARD_LINE_1);
        player.getPA().sendFrame126("Attack XP: 13,750", QuestSystem.QUEST_REWARD_LINE_2);
        player.getPA().sendFrame126("Strength XP: 13,750", QuestSystem.QUEST_REWARD_LINE_3);
        player.getPA().showInterface(QuestSystem.QUEST_REWARD_INTERFACE_ID);
    }

    private int advanceTo(QuestProgress progress, int stage) {
        return Math.max(progress.getStage(), stage);
    }
}
