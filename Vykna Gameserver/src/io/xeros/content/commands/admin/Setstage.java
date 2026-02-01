package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.content.questsystem.QuestSystem;
import io.xeros.content.questsystem.engine.QuestHandler;
import io.xeros.content.questsystem.engine.StepQuestHandler;
import io.xeros.content.questsystem.model.QuestProgress;
import io.xeros.content.questsystem.model.QuestState;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

public class Setstage extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        String[] args = splitArgs(input);
        if (args.length < 2) {
            c.sendMessage("Usage: ::setstage <questId> <stage> [player]");
            return;
        }
        String questId = args[0];
        Integer stage = parseInt(args[1]);
        if (stage == null) {
            c.sendMessage("Stage must be a number.");
            return;
        }
        Player target = args.length > 2 ? PlayerHandler.getPlayerByDisplayName(args[2]) : c;
        if (target == null) {
            c.sendMessage("Player is null.");
            return;
        }
        QuestProgress progress = target.getQuestProfile().getOrCreate(questId);
        progress.ensureDefaults();
        progress.setStage(stage);
        QuestHandler handler = QuestSystem.getHandler(questId);
        if (handler != null && stage >= handler.completionStage()) {
            progress.setState(QuestState.COMPLETED);
        } else {
            progress.setState(QuestState.IN_PROGRESS);
            progress.getVars().remove("step." + stage + ".started");
            if (handler instanceof StepQuestHandler stepHandler) {
                stepHandler.startCurrentStep(target, progress);
            }
        }
        c.sendMessage("Set quest " + questId + " stage to " + stage + " for " + target.getDisplayName() + ".");
    }

    private String[] splitArgs(String input) {
        if (input == null || input.isEmpty()) {
            return new String[0];
        }
        return input.contains("-") ? input.split("-") : input.split(" ");
    }

    private Integer parseInt(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
