package io.xeros.content.questsystem.event;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class QuestEvent {
    private final QuestEventType type;
    private final Map<String, Object> data;

    public QuestEvent(QuestEventType type) {
        this(type, new HashMap<>());
    }

    public QuestEvent(QuestEventType type, Map<String, Object> data) {
        this.type = type;
        this.data = data == null ? new HashMap<>() : new HashMap<>(data);
    }

    public QuestEventType getType() {
        return type;
    }

    public Map<String, Object> getData() {
        return Collections.unmodifiableMap(data);
    }

    public Object get(String key) {
        return data.get(key);
    }

    public QuestEvent with(String key, Object value) {
        data.put(key, value);
        return this;
    }
}
