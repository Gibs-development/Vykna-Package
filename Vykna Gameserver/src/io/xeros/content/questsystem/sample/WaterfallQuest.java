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
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

import static io.xeros.content.questsystem.sample.WaterfallQuestHandler.*;

public final class WaterfallQuest {
    public static final int OBJECT_WATERFALL_ENTRANCE_ID = 2001;
    private static final Position CAVE_ENTRY = new Position(2574, 9862, 0);

    private WaterfallQuest() {
    }

    public static boolean handleNpcTalk(Player player, int npcId) {
        if (npcId != NPC_ALMERA && npcId != NPC_HUDON && npcId != NPC_GOLRIE && npcId != NPC_GOLRIE_ALT && npcId != NPC_HUDO) {
            return false;
        }
        QuestProgress progress = player.getQuestProfile().getOrCreate(QUEST_ID);
        progress.ensureDefaults();
        if (npcId == NPC_ALMERA) {
            return handleAlmera(player, progress, npcId);
        }
        if (npcId == NPC_GOLRIE || npcId == NPC_GOLRIE_ALT) {
            return handleGolrie(player, progress, npcId);
        }
        if (npcId == NPC_HUDON || npcId == NPC_HUDO) {
            return handleHudon(player, progress, npcId);
        }
        return false;
    }

    public static boolean handleObjectClick(Player player, WorldObject object, int option) {
        if (player == null || object == null) {
            return false;
        }
        if (object.getId() != OBJECT_WATERFALL_ENTRANCE_ID) {
            return false;
        }
        QuestProgress progress = player.getQuestProfile().getOrCreate(QUEST_ID);
        progress.ensureDefaults();
        if (progress.getState() == QuestState.NOT_STARTED) {
            player.sendMessage("Perhaps I should speak to Almera first.");
            return true;
        }
        if (!player.getItems().isWearingItem(GLARIAL_AMULET)) {
            player.sendMessage("You must be wearing Glarial's amulet to enter.");
            return true;
        }
        player.getPA().movePlayer(CAVE_ENTRY.getX(), CAVE_ENTRY.getY(), CAVE_ENTRY.getHeight());
        if (progress.getStage() < STAGE_CAVE_ENTERED) {
            progress.setStage(STAGE_CAVE_ENTERED);
            progress.setState(QuestState.IN_PROGRESS);
            QuestSystem.updateQuestList(player);
        }
        return true;
    }

    private static boolean handleAlmera(Player player, QuestProgress progress, int npcId) {
        if (progress.getState() == QuestState.COMPLETED) {
            new DialogueBuilder(player)
                    .setNpcId(npcId)
                    .npc(DialogueExpression.CALM, "Thank you for returning the urn.")
                    .send();
            return true;
        }
        if (progress.getState() == QuestState.NOT_STARTED) {
            DialogueBuilder builder = new DialogueBuilder(player).setNpcId(npcId);
            builder.npc(DialogueExpression.SAD,
                    "My son is trapped near the waterfall.",
                    "Will you help us?");
            builder.option("Select an option",
                    new DialogueOption("Yes, I'll help.", plr -> startQuest(plr, npcId)),
                    new DialogueOption("No, sorry.", plr -> plr.getPA().closeAllWindows())
            );
            builder.send();
            return true;
        }
        if (!player.getItems().playerHasItem(GLARIAL_PEBBLE, 1) && progress.getStage() < STAGE_AMULET_OBTAINED) {
            player.getItems().addItemUnderAnyCircumstance(GLARIAL_PEBBLE, 1);
            player.sendMessage("Almera gives you Glarial's pebble.");
        }
        if (progress.getStage() >= STAGE_URN_OBTAINED && player.getItems().playerHasItem(GLARIAL_URN, 1)) {
            QuestSystem.handle(player, new QuestEvent(QuestEventType.NPC_TALK)
                    .with(QuestEventKeys.NPC_ID, npcId));
            return true;
        }
        new DialogueBuilder(player)
                .setNpcId(npcId)
                .npc(DialogueExpression.SAD,
                        "Please search the waterfall cave for Glarial's urn.",
                        "You may need her amulet to enter.")
                .send();
        return true;
    }

    private static boolean handleHudon(Player player, QuestProgress progress, int npcId) {
        if (progress.getState() == QuestState.NOT_STARTED) {
            new DialogueBuilder(player)
                    .setNpcId(npcId)
                    .npc(DialogueExpression.CALM, "Please, help Almera find our son.")
                    .send();
            return true;
        }
        new DialogueBuilder(player)
                .setNpcId(npcId)
                .npc(DialogueExpression.CALM, "The waterfall cave is dangerous. Be careful.")
                .send();
        return true;
    }

    private static boolean handleGolrie(Player player, QuestProgress progress, int npcId) {
        if (progress.getState() != QuestState.IN_PROGRESS) {
            new DialogueBuilder(player)
                    .setNpcId(npcId)
                    .npc(DialogueExpression.CALM, "...")
                    .send();
            return true;
        }
        QuestSystem.handle(player, new QuestEvent(QuestEventType.NPC_TALK)
                .with(QuestEventKeys.NPC_ID, npcId));
        new DialogueBuilder(player)
                .setNpcId(npcId)
                .npc(DialogueExpression.CALM, "Take this urn back to Almera.")
                .send();
        return true;
    }

    private static void startQuest(Player player, int npcId) {
        QuestProgress progress = player.getQuestProfile().getOrCreate(QUEST_ID);
        progress.ensureDefaults();
        progress.setState(QuestState.IN_PROGRESS);
        progress.setStage(STAGE_STARTED);
        player.getItems().addItemUnderAnyCircumstance(GLARIAL_PEBBLE, 1);
        QuestSystem.updateQuestList(player);
        player.getPA().closeAllWindows();
        player.setDialogueBuilder(null);
    }
}
