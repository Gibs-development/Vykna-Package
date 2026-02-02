package io.xeros.content.questsystem.sample;

import io.xeros.content.questsystem.engine.QuestHandler;
import io.xeros.content.questsystem.event.QuestEvent;
import io.xeros.content.questsystem.event.QuestEventKeys;
import io.xeros.content.questsystem.event.QuestEventType;
import io.xeros.content.questsystem.model.QuestProgress;
import io.xeros.content.questsystem.model.QuestState;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.entity.player.Player;

public class OneSmallFavorQuestHandler implements QuestHandler {
    public static final String QUEST_ID = "one_small_favour";
    public static final int STAGE_NOT_STARTED = 0;
    public static final int STAGE_STARTED = 1;
    public static final int STAGE_COMPLETE = 51;

    public static final int[] NPC_IDS = {
            9, 359, 673, 851, 1078, 1222, 1375, 1577, 1669, 1901,
            2128, 2346, 2438, 2668, 2880, 3097, 3309, 3506, 3642, 3831,
            3955, 4118, 4269, 4463, 4626, 4771, 4966, 5191, 5385, 5533,
            5767, 5975, 6418, 6652, 6801, 6969, 7083, 7300, 7450, 7645,
            7787, 8023, 8208, 8405, 8584, 8757, 8891, 9065, 9156, 9329
    };

    public static final int[] ITEM_IDS = {
            1, 8, 14, 20, 26, 32, 38, 44, 50, 56,
            62, 68, 74, 84, 90, 96, 102, 108, 114, 120,
            126, 132, 138, 144, 150, 156, 162, 168, 174, 180,
            186, 192, 198, 209, 220, 226, 232, 238, 244, 250,
            256, 262, 268, 274, 280, 286, 293, 299, 305, 311
    };

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
        int stage = progress.getStage();
        if (stage < STAGE_STARTED) {
            return stage;
        }
        if (stage == STAGE_COMPLETE - 1) {
            if (npcId != NPC_IDS[0]) {
                return stage;
            }
            int finalItem = ITEM_IDS[ITEM_IDS.length - 1];
            if (!player.getItems().playerHasItem(finalItem, 1)) {
                return stage;
            }
            player.getItems().deleteItem2(finalItem, 1);
            giveRewards(player);
            return STAGE_COMPLETE;
        }
        int stepIndex = stage;
        if (stepIndex < 1 || stepIndex >= NPC_IDS.length) {
            return stage;
        }
        if (npcId != NPC_IDS[stepIndex]) {
            return stage;
        }
        int requiredItem = ITEM_IDS[stepIndex - 1];
        if (!player.getItems().playerHasItem(requiredItem, 1)) {
            return stage;
        }
        player.getItems().deleteItem2(requiredItem, 1);
        int nextItem = ITEM_IDS[stepIndex];
        player.getItems().addItemUnderAnyCircumstance(nextItem, 1);
        return stage + 1;
    }

    private void giveRewards(Player player) {
        player.getItems().addItemUnderAnyCircumstance(995, 10000);
        player.sendMessage("You receive 10,000 coins for surviving the favour chain.");
    }

    public static String getNpcNameForStep(int step) {
        if (step < 1 || step >= NPC_IDS.length) {
            return "someone";
        }
        return NpcDef.forId(NPC_IDS[step]).getName();
    }

    public static String getItemNameForStep(int step) {
        if (step < 0 || step >= ITEM_IDS.length) {
            return "something";
        }
        ItemDef def = ItemDef.forId(ITEM_IDS[step]);
        return def == null ? "something" : def.getName();
    }

    public static String getItemNameForCurrentStage(int stage) {
        if (stage <= STAGE_STARTED) {
            return getItemNameForStep(0);
        }
        int stepIndex = Math.min(ITEM_IDS.length - 1, stage - 1);
        return getItemNameForStep(stepIndex);
    }

    public static String getNpcNameForCurrentStage(int stage) {
        if (stage <= STAGE_STARTED) {
            return getNpcNameForStep(1);
        }
        int stepIndex = Math.min(NPC_IDS.length - 1, stage);
        return NpcDef.forId(NPC_IDS[stepIndex]).getName();
    }

    public static String getStartNpcName() {
        return NpcDef.forId(NPC_IDS[0]).getName();
    }
}
