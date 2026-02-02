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
import io.xeros.model.entity.player.Player;

import static io.xeros.content.questsystem.sample.CourierFavorQuestHandler.*;

public final class CourierFavorQuest {
    private CourierFavorQuest() {
    }

    public static boolean handleNpcTalk(Player player, int npcId) {
        if (npcId != NPC_ENGINEERING_ASSISTANT && npcId != NPC_BANKER) {
            return false;
        }
        QuestProgress progress = player.getQuestProfile().getOrCreate(QUEST_ID);
        progress.ensureDefaults();

        if (progress.getState() == QuestState.COMPLETED) {
            new DialogueBuilder(player)
                    .setNpcId(npcId)
                    .npc(DialogueExpression.CALM, "Thanks again for your help.")
                    .send();
            return true;
        }

        if (npcId == NPC_ENGINEERING_ASSISTANT) {
            return handleAssistant(player, progress, npcId);
        }
        return handleBanker(player, progress, npcId);
    }

    private static boolean handleAssistant(Player player, QuestProgress progress, int npcId) {
        if (progress.getState() == QuestState.NOT_STARTED) {
            DialogueBuilder builder = new DialogueBuilder(player).setNpcId(npcId);
            builder.npc(DialogueExpression.SPEAKING_CALMLY,
                    "Could you deliver this research package",
                    "to a banker for me?");
            builder.option("Select an option",
                    new DialogueOption("Sure, I'll take it.", plr -> startQuest(plr, npcId)),
                    new DialogueOption("Not right now.", plr -> plr.getPA().closeAllWindows())
            );
            builder.send();
            return true;
        }

        if (progress.getStage() >= STAGE_DELIVERED) {
            QuestSystem.handle(player, new QuestEvent(QuestEventType.NPC_TALK).with(QuestEventKeys.NPC_ID, npcId));
            return true;
        }

        if (!hasPackage(player)) {
            if (player.getItems().bankContains(ITEM_RESEARCH_PACKAGE)) {
                new DialogueBuilder(player)
                        .setNpcId(npcId)
                        .npc(DialogueExpression.SPEAKING_CALMLY,
                                "You already have the package in your bank.",
                                "Please deliver it to a banker.")
                        .send();
                return true;
            }
            if (player.getItems().freeSlots() == 0) {
                new DialogueBuilder(player)
                        .setNpcId(npcId)
                        .npc(DialogueExpression.SPEAKING_CALMLY,
                                "Make some space in your inventory,",
                                "then I'll hand you the package.")
                        .send();
                return true;
            }
            player.getItems().addItemUnderAnyCircumstance(ITEM_RESEARCH_PACKAGE, 1);
        }

        new DialogueBuilder(player)
                .setNpcId(npcId)
                .npc(DialogueExpression.SPEAKING_CALMLY,
                        "Please deliver the package to a banker.",
                        "Then return here for your reward.")
                .send();
        return true;
    }

    private static boolean handleBanker(Player player, QuestProgress progress, int npcId) {
        if (progress.getState() == QuestState.NOT_STARTED) {
            return false;
        }
        if (progress.getStage() >= STAGE_DELIVERED) {
            new DialogueBuilder(player)
                    .setNpcId(npcId)
                    .npc(DialogueExpression.CALM, "Thanks for the delivery.")
                    .send();
            return true;
        }
        if (!hasPackage(player)) {
            new DialogueBuilder(player)
                    .setNpcId(npcId)
                    .npc(DialogueExpression.SPEAKING_CALMLY,
                            "Were you meant to bring me a package?",
                            "I don't see it with you.")
                    .send();
            return true;
        }
        QuestSystem.handle(player, new QuestEvent(QuestEventType.NPC_TALK).with(QuestEventKeys.NPC_ID, npcId));
        new DialogueBuilder(player)
                .setNpcId(npcId)
                .npc(DialogueExpression.HAPPY, "Ah, the research package. Thank you.")
                .send();
        return true;
    }

    private static void startQuest(Player player, int npcId) {
        if (player.getItems().freeSlots() == 0) {
            player.sendMessage("You need at least 1 free inventory slot to take the package.");
            return;
        }
        QuestProgress progress = player.getQuestProfile().getOrCreate(QUEST_ID);
        progress.ensureDefaults();
        progress.setState(QuestState.IN_PROGRESS);
        progress.setStage(STAGE_STARTED);
        player.getItems().addItemUnderAnyCircumstance(ITEM_RESEARCH_PACKAGE, 1);
        QuestSystem.updateQuestList(player);
        QuestSystem.handle(player, new QuestEvent(QuestEventType.NPC_TALK).with(QuestEventKeys.NPC_ID, npcId));
        player.getPA().closeAllWindows();
        player.setDialogueBuilder(null);
    }

    private static boolean hasPackage(Player player) {
        return player.getItems().playerHasItem(ITEM_RESEARCH_PACKAGE, 1);
    }
}
