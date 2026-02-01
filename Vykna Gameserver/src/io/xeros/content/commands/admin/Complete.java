package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.content.questsystem.QuestSystem;
import io.xeros.content.questsystem.engine.QuestHandler;
import io.xeros.content.questsystem.model.QuestProgress;
import io.xeros.content.questsystem.model.QuestState;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

public class Complete extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        String[] args = splitArgs(input);
        if (args.length < 1) {
            c.sendMessage("Usage: ::complete <questId> [player]");
            return;
        }
        String questId = args[0];
        Player target = args.length > 1 ? PlayerHandler.getPlayerByDisplayName(args[1]) : c;
        if (target == null) {
            c.sendMessage("Player is null.");
            return;
        }
        QuestProgress progress = target.getQuestProfile().getOrCreate(questId);
        progress.ensureDefaults();
        QuestHandler handler = QuestSystem.getHandler(questId);
        if (handler != null) {
            progress.setStage(handler.completionStage());
        }
        progress.setState(QuestState.COMPLETED);
        c.sendMessage("Completed quest " + questId + " for " + target.getDisplayName() + ".");
    }

    private String[] splitArgs(String input) {
        if (input == null || input.isEmpty()) {
            return new String[0];
        }
        return input.contains("-") ? input.split("-") : input.split(" ");
    }
}
