package io.xeros.content.questsystem.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import io.xeros.content.questsystem.model.QuestDefinition;
import io.xeros.content.questsystem.model.QuestId;

public class QuestRegistryImpl implements QuestRegistry {
    private final Map<QuestId, QuestDefinition> definitions = new ConcurrentHashMap<>();

    @Override
    public void register(QuestDefinition definition) {
        definitions.put(definition.id(), definition);
    }

    @Override
    public Optional<QuestDefinition> find(QuestId questId) {
        return Optional.ofNullable(definitions.get(questId));
    }

    @Override
    public Collection<QuestDefinition> all() {
        return Collections.unmodifiableCollection(definitions.values());
    }
}
