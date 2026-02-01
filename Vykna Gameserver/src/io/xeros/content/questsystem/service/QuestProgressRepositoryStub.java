package io.xeros.content.questsystem.service;

import io.xeros.content.questsystem.model.QuestId;
import io.xeros.content.questsystem.model.QuestProgress;

public class QuestProgressRepositoryStub implements QuestProgressRepository {
    @Override
    public QuestProgress load(QuestId questId) {
        throw new UnsupportedOperationException("QuestProgressRepository stub");
    }

    @Override
    public void save(QuestProgress progress) {
        throw new UnsupportedOperationException("QuestProgressRepository stub");
    }

    @Override
    public void delete(QuestId questId) {
        throw new UnsupportedOperationException("QuestProgressRepository stub");
    }
}
