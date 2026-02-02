package io.xeros.content.questsystem.sample;

import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueExpression;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.questsystem.QuestSystem;
import io.xeros.content.questsystem.event.QuestEvent;
import io.xeros.content.questsystem.event.QuestEventKeys;
import io.xeros.content.questsystem.event.QuestEventType;
import io.xeros.content.questsystem.model.QuestProgress;
import io.xeros.content.questsystem.model.QuestState;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.entity.player.Player;

import static io.xeros.content.questsystem.sample.OneSmallFavorQuestHandler.*;

public final class OneSmallFavorQuest {
    private OneSmallFavorQuest() {
    }

    public static boolean handleNpcTalk(Player player, int npcId) {
        if (!isQuestNpc(npcId)) {
            return false;
        }
        QuestProgress progress = player.getQuestProfile().getOrCreate(QUEST_ID);
        progress.ensureDefaults();

        if (progress.getState() == QuestState.COMPLETED) {
            new DialogueBuilder(player)
                    .setNpcId(npcId)
                    .npc(DialogueExpression.CALM, "Please don't ask me for any favours.")
                    .send();
            return true;
        }

        if (progress.getState() == QuestState.NOT_STARTED) {
            if (npcId != NPC_IDS[0]) {
                return false;
            }
            DialogueBuilder builder = new DialogueBuilder(player).setNpcId(npcId);
            builder.npc(DialogueExpression.SPEAKING_CALMLY,
                    "I only need a tiny favour.",
                    "It should be quick. Probably.");
            builder.option("Select an option",
                    new DialogueOption("Sure, why not.", plr -> startQuest(plr)),
                    new DialogueOption("No thanks.", plr -> plr.getPA().closeAllWindows())
            );
            builder.send();
            return true;
        }

        int stage = progress.getStage();
        if (stage == STAGE_COMPLETE - 1 && npcId == NPC_IDS[0]) {
            int finalItem = ITEM_IDS[ITEM_IDS.length - 1];
            if (!player.getItems().playerHasItem(finalItem, 1)) {
                remindMissingItem(player, npcId, finalItem);
                return true;
            }
            QuestSystem.handle(player, new QuestEvent(QuestEventType.NPC_TALK).with(QuestEventKeys.NPC_ID, npcId));
            new DialogueBuilder(player)
                    .setNpcId(npcId)
                    .npc(DialogueExpression.HAPPY, "You actually did it. Bless you.")
                    .send();
            return true;
        }

        int expectedNpc = getExpectedNpcId(stage);
        if (npcId != expectedNpc) {
            new DialogueBuilder(player)
                    .setNpcId(npcId)
                    .npc(DialogueExpression.CALM, "I'm busy right now.")
                    .send();
            return true;
        }

        int requiredItem = getRequiredItemForStage(stage);
        if (!player.getItems().playerHasItem(requiredItem, 1)) {
            if (player.getItems().bankContains(requiredItem)) {
                new DialogueBuilder(player)
                        .setNpcId(npcId)
                        .npc(DialogueExpression.SPEAKING_CALMLY,
                                "You left it in your bank.",
                                "Please bring it here.")
                        .send();
                return true;
            }
            if (player.getItems().freeSlots() == 0) {
                new DialogueBuilder(player)
                        .setNpcId(npcId)
                        .npc(DialogueExpression.SPEAKING_CALMLY,
                                "Make some space first, then I'll hand you",
                                "the item again.")
                        .send();
                return true;
            }
            player.getItems().addItemUnderAnyCircumstance(requiredItem, 1);
            new DialogueBuilder(player)
                    .setNpcId(npcId)
                    .npc(DialogueExpression.SPEAKING_CALMLY,
                            "Fine, take another one.",
                            "Don't lose it.")
                    .send();
            return true;
        }

        QuestSystem.handle(player, new QuestEvent(QuestEventType.NPC_TALK).with(QuestEventKeys.NPC_ID, npcId));
        if (progress.getStage() >= STAGE_COMPLETE - 1) {
            return true;
        }
        int nextItem = ITEM_IDS[Math.min(ITEM_IDS.length - 1, stage)];
        String nextItemName = itemName(nextItem);
        String nextNpcName = npcName(getExpectedNpcId(stage + 1));
        new DialogueBuilder(player)
                .setNpcId(npcId)
                .npc(DialogueExpression.SPEAKING_CALMLY,
                        "Oh great, you have it. Now, could you take",
                        nextItemName + " to " + nextNpcName + "?")
                .send();
        return true;
    }

    private static void startQuest(Player player) {
        if (player.getItems().freeSlots() == 0) {
            player.sendMessage("You need at least 1 free inventory slot to begin.");
            return;
        }
        QuestProgress progress = player.getQuestProfile().getOrCreate(QUEST_ID);
        progress.ensureDefaults();
        progress.setState(QuestState.IN_PROGRESS);
        progress.setStage(STAGE_STARTED);
        player.getItems().addItemUnderAnyCircumstance(ITEM_IDS[0], 1);
        QuestSystem.updateQuestList(player);
        player.getPA().closeAllWindows();
        player.setDialogueBuilder(null);
    }

    private static boolean isQuestNpc(int npcId) {
        for (int id : NPC_IDS) {
            if (id == npcId) {
                return true;
            }
        }
        return false;
    }

    private static int getExpectedNpcId(int stage) {
        if (stage <= STAGE_STARTED) {
            return NPC_IDS[1];
        }
        int index = Math.min(NPC_IDS.length - 1, stage);
        return NPC_IDS[index];
    }

    private static int getRequiredItemForStage(int stage) {
        if (stage <= STAGE_STARTED) {
            return ITEM_IDS[0];
        }
        int index = Math.min(ITEM_IDS.length - 1, stage - 1);
        return ITEM_IDS[index];
    }

    private static void remindMissingItem(Player player, int npcId, int itemId) {
        new DialogueBuilder(player)
                .setNpcId(npcId)
                .npc(DialogueExpression.SPEAKING_CALMLY,
                        "You still have something to bring me.",
                        "I'm waiting on " + itemName(itemId) + ".")
                .send();
    }

    private static String npcName(int npcId) {
        return NpcDef.forId(npcId).getName();
    }

    private static String itemName(int itemId) {
        ItemDef def = ItemDef.forId(itemId);
        return def == null ? "something" : def.getName();
    }
}
