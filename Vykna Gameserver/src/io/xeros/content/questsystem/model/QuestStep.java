package io.xeros.content.questsystem.model;

import java.util.List;

import io.xeros.content.questsystem.event.QuestEvent;
import io.xeros.model.entity.player.Player;

public interface QuestStep {
    List<String> getJournalText(QuestProgress progress);

    void onStart(Player player, QuestProgress progress);

    void onEvent(Player player, QuestProgress progress, QuestEvent event);

    boolean isComplete(Player player, QuestProgress progress);

    void onComplete(Player player, QuestProgress progress);
}
