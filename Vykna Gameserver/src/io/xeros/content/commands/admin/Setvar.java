package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.content.questsystem.model.QuestProgress;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

public class Setvar extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        String[] args = splitArgs(input);
        if (args.length < 3) {
            c.sendMessage("Usage: ::setvar <questId> <key> <value> [player]");
            return;
        }
        String questId = args[0];
        String key = args[1];
        String valueRaw = args[2];
        Player target = args.length > 3 ? PlayerHandler.getPlayerByDisplayName(args[3]) : c;
        if (target == null) {
            c.sendMessage("Player is null.");
            return;
        }
        QuestProgress progress = target.getQuestProfile().getOrCreate(questId);
        progress.ensureDefaults();
        Object value = parseValue(valueRaw);
        progress.getVars().put(key, value);
        c.sendMessage("Set " + questId + " var " + key + "=" + value + " for " + target.getDisplayName() + ".");
    }

    private String[] splitArgs(String input) {
        if (input == null || input.isEmpty()) {
            return new String[0];
        }
        return input.contains("-") ? input.split("-") : input.split(" ");
    }

    private Object parseValue(String raw) {
        if (raw == null) {
            return "";
        }
        String lower = raw.toLowerCase();
        if ("true".equals(lower) || "false".equals(lower)) {
            return Boolean.parseBoolean(lower);
        }
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException ignored) {
            return raw;
        }
    }
}
