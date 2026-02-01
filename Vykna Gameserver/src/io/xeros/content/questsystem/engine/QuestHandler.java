package io.xeros.content.questsystem.engine;

import io.xeros.content.questsystem.event.QuestEvent;
import io.xeros.content.questsystem.model.QuestProgress;
import io.xeros.model.entity.player.Player;

public interface QuestHandler {
    String questId();

    int completionStage();

    int nextStage(Player player, QuestProgress progress, QuestEvent event);
}
