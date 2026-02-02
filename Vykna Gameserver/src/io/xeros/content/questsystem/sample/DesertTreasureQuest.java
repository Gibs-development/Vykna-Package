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

import static io.xeros.content.questsystem.sample.DesertTreasureQuestHandler.*;

public final class DesertTreasureQuest {
    private DesertTreasureQuest() {
    }

    public static boolean handleNpcTalk(Player player, int npcId) {
        if (npcId != NPC_EBLIS && npcId != NPC_RASOLO && npcId != NPC_MALAK && npcId != NPC_AZZANADRA) {
            return false;
        }
        QuestProgress progress = player.getQuestProfile().getOrCreate(QUEST_ID);
        progress.ensureDefaults();
        switch (npcId) {
            case NPC_EBLIS:
                return handleEblis(player, progress, npcId);
            case NPC_RASOLO:
                return handleRasolo(player, progress, npcId);
            case NPC_MALAK:
                return handleMalak(player, progress, npcId);
            case NPC_AZZANADRA:
                return handleAzzanadra(player, progress, npcId);
            default:
                return false;
        }
    }

    private static boolean handleEblis(Player player, QuestProgress progress, int npcId) {
        if (progress.getState() == QuestState.COMPLETED) {
            new DialogueBuilder(player)
                    .setNpcId(npcId)
                    .npc(DialogueExpression.CALM, "Azzanadra is free once more. Thank you.")
                    .send();
            return true;
        }
        if (progress.getState() == QuestState.NOT_STARTED) {
            DialogueBuilder builder = new DialogueBuilder(player).setNpcId(npcId);
            builder.npc(DialogueExpression.SPEAKING_CALMLY,
                    "I seek four ancient diamonds to free Azzanadra.",
                    "Will you help me recover them?");
            builder.option("Select an option",
                    new DialogueOption("Yes, I'll help.", plr -> startQuest(plr, npcId)),
                    new DialogueOption("No, sorry.", plr -> plr.getPA().closeAllWindows())
            );
            builder.send();
            return true;
        }
        if (progress.getStage() >= STAGE_AZZANADRA) {
            new DialogueBuilder(player)
                    .setNpcId(npcId)
                    .npc(DialogueExpression.CALM, "The seal is broken. Speak to Azzanadra.")
                    .send();
            return true;
        }
        new DialogueBuilder(player)
                .setNpcId(npcId)
                .npc(DialogueExpression.CALM,
                        "You must recover the Blood, Smoke, Ice, and Shadow diamonds.",
                        "Rasolo has a ring of visibility you may need.")
                .send();
        return true;
    }

    private static boolean handleRasolo(Player player, QuestProgress progress, int npcId) {
        if (progress.getState() != QuestState.IN_PROGRESS) {
            new DialogueBuilder(player)
                    .setNpcId(npcId)
                    .npc(DialogueExpression.CALM, "I'm busy right now.")
                    .send();
            return true;
        }
        QuestSystem.handle(player, new QuestEvent(QuestEventType.NPC_TALK)
                .with(QuestEventKeys.NPC_ID, npcId));
        new DialogueBuilder(player)
                .setNpcId(npcId)
                .npc(DialogueExpression.CALM, "Take this ring of visibility. It may help you.")
                .send();
        return true;
    }

    private static boolean handleMalak(Player player, QuestProgress progress, int npcId) {
        if (progress.getState() != QuestState.IN_PROGRESS) {
            new DialogueBuilder(player)
                    .setNpcId(npcId)
                    .npc(DialogueExpression.CALM, "...")
                    .send();
            return true;
        }
        if (player.getItems().getTotalCount(RING_OF_VISIBILITY) == 0) {
            new DialogueBuilder(player)
                    .setNpcId(npcId)
                    .npc(DialogueExpression.SAD, "You cannot see me without a ring of visibility.")
                    .send();
            return true;
        }
        QuestSystem.handle(player, new QuestEvent(QuestEventType.NPC_TALK)
                .with(QuestEventKeys.NPC_ID, npcId));
        new DialogueBuilder(player)
                .setNpcId(npcId)
                .npc(DialogueExpression.CALM,
                        "Seek Dessous in the crypts and claim the Blood diamond.",
                        "The other diamonds lie with Fareed, Kamil, and Damis.")
                .send();
        return true;
    }

    private static boolean handleAzzanadra(Player player, QuestProgress progress, int npcId) {
        if (progress.getState() == QuestState.COMPLETED) {
            new DialogueBuilder(player)
                    .setNpcId(npcId)
                    .npc(DialogueExpression.CALM, "Use the ancient magicks wisely.")
                    .send();
            return true;
        }
        if (progress.getStage() < STAGE_AZZANADRA) {
            new DialogueBuilder(player)
                    .setNpcId(npcId)
                    .npc(DialogueExpression.CALM, "The seal still binds me.")
                    .send();
            return true;
        }
        QuestSystem.handle(player, new QuestEvent(QuestEventType.NPC_TALK)
                .with(QuestEventKeys.NPC_ID, npcId));
        new DialogueBuilder(player)
                .setNpcId(npcId)
                .npc(DialogueExpression.CALM, "You have freed me. Accept this ancient knowledge.")
                .send();
        return true;
    }

    private static void startQuest(Player player, int npcId) {
        QuestProgress progress = player.getQuestProfile().getOrCreate(QUEST_ID);
        progress.ensureDefaults();
        progress.setState(QuestState.IN_PROGRESS);
        progress.setStage(STAGE_STARTED);
        QuestSystem.updateQuestList(player);
        player.getPA().closeAllWindows();
        player.setDialogueBuilder(null);
    }
}
