package io.xeros.content.questsystem.registry;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import io.xeros.content.questsystem.model.QuestDefinition;
import io.xeros.content.questsystem.model.QuestId;

public class QuestRegistryStub implements QuestRegistry {
    @Override
    public void register(QuestDefinition definition) {
        throw new UnsupportedOperationException("QuestRegistry stub");
    }

    @Override
    public Optional<QuestDefinition> find(QuestId questId) {
        return Optional.empty();
    }

    @Override
    public Collection<QuestDefinition> all() {
        return List.of();
    }
}
