package io.xeros.content.questsystem.engine;

import java.util.Collection;
import java.util.Optional;

public interface QuestHandlerRegistry {
    void register(QuestHandler handler);

    Optional<QuestHandler> find(String questId);

    Collection<QuestHandler> all();
}
