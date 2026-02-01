package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.content.questsystem.QuestSystem;
import io.xeros.content.questsystem.model.QuestProgress;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

import java.util.Comparator;
import java.util.Map;

public class Questinspect extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        String[] args = splitArgs(input);
        if (args.length < 1) {
            c.sendMessage("Usage: ::questinspect <questId> [player]");
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
        c.sendMessage("Quest " + questId + " state=" + progress.getState() + " stage=" + progress.getStage());
        if (progress.getVars().isEmpty()) {
            c.sendMessage("Vars: (empty)");
        } else {
            c.sendMessage("Vars:");
            progress.getVars().entrySet().stream()
                    .sorted(Comparator.comparing(Map.Entry::getKey))
                    .forEach(entry -> c.sendMessage(" - " + entry.getKey() + " = " + entry.getValue()));
        }
        c.sendMessage("Journal:");
        for (String line : QuestSystem.getQuestJournalLines(target, questId)) {
            c.sendMessage(" - " + line);
        }
    }

    private String[] splitArgs(String input) {
        if (input == null || input.isEmpty()) {
            return new String[0];
        }
        return input.contains("-") ? input.split("-") : input.split(" ");
    }
}
