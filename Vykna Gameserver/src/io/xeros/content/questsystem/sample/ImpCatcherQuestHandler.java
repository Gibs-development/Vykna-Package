package io.xeros.content.questsystem.sample;

import io.xeros.content.questsystem.QuestSystem;
import io.xeros.content.questsystem.engine.QuestHandler;
import io.xeros.content.questsystem.event.QuestEvent;
import io.xeros.content.questsystem.event.QuestEventKeys;
import io.xeros.content.questsystem.event.QuestEventType;
import io.xeros.content.questsystem.model.QuestProgress;
import io.xeros.content.questsystem.model.QuestState;
import io.xeros.model.Npcs;
import io.xeros.model.entity.player.Player;

public class ImpCatcherQuestHandler implements QuestHandler {
    public static final String QUEST_ID = "imp_catcher";
    public static final int STAGE_NOT_STARTED = 0;
    public static final int STAGE_STARTED = 10;
    public static final int STAGE_COMPLETE = 100;

    public static final int RED_BEAD = 1470;
    public static final int YELLOW_BEAD = 1472;
    public static final int BLACK_BEAD = 1474;
    public static final int WHITE_BEAD = 1476;
    public static final int AMULET_OF_ACCURACY = 1478;

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
        if (npcId != Npcs.WIZARD_MIZGOG && npcId != Npcs.WIZARD_MIZGOG_2) {
            return progress.getStage();
        }
        if (progress.getStage() >= STAGE_COMPLETE) {
            return progress.getStage();
        }
        if (!hasAllBeads(player)) {
            return progress.getStage();
        }
        removeBeads(player);
        giveRewards(player);
        QuestSystem.openQuestReward(player, "Imp Catcher");
        player.getPA().sendSound(3942);
        return STAGE_COMPLETE;
    }

    private boolean hasAllBeads(Player player) {
        return player.getItems().playerHasItem(RED_BEAD, 1)
                && player.getItems().playerHasItem(YELLOW_BEAD, 1)
                && player.getItems().playerHasItem(BLACK_BEAD, 1)
                && player.getItems().playerHasItem(WHITE_BEAD, 1);
    }

    private void removeBeads(Player player) {
        player.getItems().deleteItem2(RED_BEAD, 1);
        player.getItems().deleteItem2(YELLOW_BEAD, 1);
        player.getItems().deleteItem2(BLACK_BEAD, 1);
        player.getItems().deleteItem2(WHITE_BEAD, 1);
    }

    private void giveRewards(Player player) {
        player.getPA().addSkillXP(875, 6, true);
        player.getItems().addItemUnderAnyCircumstance(AMULET_OF_ACCURACY, 1);
        player.sendMessage("You receive a reward for completing Imp Catcher.");
    }
}
