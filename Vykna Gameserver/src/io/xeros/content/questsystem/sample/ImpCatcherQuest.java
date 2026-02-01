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
import io.xeros.model.Npcs;
import io.xeros.model.entity.player.Player;

import static io.xeros.content.questsystem.sample.ImpCatcherQuestHandler.*;

public final class  ImpCatcherQuest {
    private ImpCatcherQuest() {
    }

    public static boolean handleNpcTalk(Player player, int npcId) {
        if (npcId != Npcs.WIZARD_MIZGOG && npcId != Npcs.WIZARD_MIZGOG_2) {
            return false;
        }
        QuestProgress progress = player.getQuestProfile().getOrCreate(QUEST_ID);
        progress.ensureDefaults();
        if (progress.getState() == QuestState.COMPLETED) {
            new DialogueBuilder(player)
                    .setNpcId(npcId)
                    .npc(DialogueExpression.CALM, "Thank you again for your help.")
                    .send();
            return true;
        }
        if (progress.getState() == QuestState.NOT_STARTED) {
            DialogueBuilder builder = new DialogueBuilder(player).setNpcId(npcId);
            builder.setNpcId(7746);
            builder.npc(DialogueExpression.SPEAKING_CALMLY,
                    "Hello there, I am in need of some beads.",
                    "Can you help me collect them?");
            builder.option("Select an option",
                    new DialogueOption("Yes, I'll help.", plr -> startQuest(plr, npcId)),
                    new DialogueOption("No, sorry.", plr -> plr.getPA().closeAllWindows())
            );
            builder.send();
            return true;
        }
        if (hasAllBeads(player)) {
            QuestSystem.handle(player, new QuestEvent(QuestEventType.NPC_TALK)
                    .with(QuestEventKeys.NPC_ID, npcId));
            return true;
        }
        String missing = getMissingBeadsLine(player);
        new DialogueBuilder(player)
                .setNpcId(npcId)
                .npc(DialogueExpression.SPEAKING_CALMLY,
                        "Please bring me a red, yellow, black,",
                        "and white bead.",
                        missing)
                .send();
        return true;
    }

    private static void startQuest(Player player, int npcId) {
        QuestProgress progress = player.getQuestProfile().getOrCreate(QUEST_ID);
        progress.ensureDefaults();
        progress.setState(QuestState.IN_PROGRESS);
        progress.setStage(STAGE_STARTED);
        QuestSystem.updateQuestList(player);
        QuestSystem.handle(player, new QuestEvent(QuestEventType.NPC_TALK).with(QuestEventKeys.NPC_ID, npcId));
        player.getPA().closeAllWindows();
        player.setDialogueBuilder(null);
    }

    private static boolean hasAllBeads(Player player) {
        return player.getItems().playerHasItem(RED_BEAD, 1)
                && player.getItems().playerHasItem(YELLOW_BEAD, 1)
                && player.getItems().playerHasItem(BLACK_BEAD, 1)
                && player.getItems().playerHasItem(WHITE_BEAD, 1);
    }

    private static String getMissingBeadsLine(Player player) {
        StringBuilder missing = new StringBuilder("Missing: ");
        boolean first = true;
        if (!player.getItems().playerHasItem(RED_BEAD, 1)) {
            missing.append("red");
            first = false;
        }
        if (!player.getItems().playerHasItem(YELLOW_BEAD, 1)) {
            if (!first) missing.append(", ");
            missing.append("yellow");
            first = false;
        }
        if (!player.getItems().playerHasItem(BLACK_BEAD, 1)) {
            if (!first) missing.append(", ");
            missing.append("black");
            first = false;
        }
        if (!player.getItems().playerHasItem(WHITE_BEAD, 1)) {
            if (!first) missing.append(", ");
            missing.append("white");
        }
        return missing.toString();
    }
}
