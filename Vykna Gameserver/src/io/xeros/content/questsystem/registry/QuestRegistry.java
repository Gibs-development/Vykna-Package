package io.xeros.content.questsystem.registry;

import java.util.Collection;
import java.util.Optional;

import io.xeros.content.questsystem.model.QuestDefinition;
import io.xeros.content.questsystem.model.QuestId;

public interface QuestRegistry {
    void register(QuestDefinition definition);

    Optional<QuestDefinition> find(QuestId questId);

    Collection<QuestDefinition> all();
}
