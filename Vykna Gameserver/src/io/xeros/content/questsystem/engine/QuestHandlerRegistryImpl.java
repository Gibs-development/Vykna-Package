package io.xeros.content.questsystem.engine;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class QuestHandlerRegistryImpl implements QuestHandlerRegistry {
    private final Map<String, QuestHandler> handlers = new ConcurrentHashMap<>();

    @Override
    public void register(QuestHandler handler) {
        handlers.put(handler.questId(), handler);
    }

    @Override
    public Optional<QuestHandler> find(String questId) {
        return Optional.ofNullable(handlers.get(questId));
    }

    @Override
    public Collection<QuestHandler> all() {
        return Collections.unmodifiableCollection(handlers.values());
    }
}
