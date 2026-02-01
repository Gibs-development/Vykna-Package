package io.xeros.content.questsystem.service;

import io.xeros.content.questsystem.model.QuestProgress;
import io.xeros.content.questsystem.model.QuestId;

public interface QuestProgressRepository {
    QuestProgress load(QuestId questId);

    void save(QuestProgress progress);

    void delete(QuestId questId);
}
