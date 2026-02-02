package io.xeros.content.questsystem.sample;

import io.xeros.content.questsystem.engine.QuestHandler;
import io.xeros.content.questsystem.event.QuestEvent;
import io.xeros.content.questsystem.event.QuestEventKeys;
import io.xeros.content.questsystem.event.QuestEventType;
import io.xeros.content.questsystem.model.QuestProgress;
import io.xeros.content.questsystem.model.QuestState;
import io.xeros.model.entity.player.Player;

public class CourierFavorQuestHandler implements QuestHandler {
    public static final String QUEST_ID = "courier_favor";
    public static final int STAGE_NOT_STARTED = 0;
    public static final int STAGE_STARTED = 10;
    public static final int STAGE_DELIVERED = 20;
    public static final int STAGE_COMPLETE = 100;

    public static final int NPC_ENGINEERING_ASSISTANT = 1413;
    public static final int NPC_BANKER = 766;
    public static final int ITEM_RESEARCH_PACKAGE = 290;

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
        if (event.getType() != QuestEventType.NPC_TALK) {
            return progress.getStage();
        }
        Object value = event.get(QuestEventKeys.NPC_ID);
        if (!(value instanceof Number)) {
            return progress.getStage();
        }
        int npcId = ((Number) value).intValue();
        if (npcId == NPC_BANKER && progress.getStage() == STAGE_STARTED) {
            if (player.getItems().playerHasItem(ITEM_RESEARCH_PACKAGE, 1)) {
                player.getItems().deleteItem2(ITEM_RESEARCH_PACKAGE, 1);
                return STAGE_DELIVERED;
            }
            return progress.getStage();
        }
        if (npcId == NPC_ENGINEERING_ASSISTANT && progress.getStage() == STAGE_DELIVERED) {
            giveRewards(player);
            return STAGE_COMPLETE;
        }
        return progress.getStage();
    }

    private void giveRewards(Player player) {
        player.getItems().addItemUnderAnyCircumstance(995, 2000);
        player.sendMessage("You receive 2,000 coins for your delivery.");
    }
}
