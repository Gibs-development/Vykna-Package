package io.xeros.content.questsystem.feedback;

import java.util.List;

import io.xeros.model.entity.player.Player;

public final class QuestFeedback {
    private QuestFeedback() {
    }

    public static void showQuestUpdatedToast(Player player, String text) {
        if (player == null) {
            return;
        }
        String message = text == null || text.isEmpty() ? "Quest updated." : ("Quest updated: " + text);
        player.sendMessage(message);
    }

    public static void showQuestComplete(Player player, List<String> rewards) {
        if (player == null) {
            return;
        }
        player.sendMessage("Quest complete!");
        if (rewards == null || rewards.isEmpty()) {
            return;
        }
        for (String reward : rewards) {
            if (reward != null && !reward.isEmpty()) {
                player.sendMessage("Reward: " + reward);
            }
        }
    }
}
