package io.xeros.content.questsystem.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class QuestItemRegistry {
    private final List<QuestItemDefinition> definitions = new CopyOnWriteArrayList<>();

    public void register(QuestItemDefinition definition) {
        if (definition == null) {
            return;
        }
        definitions.add(definition);
    }

    public List<QuestItemDefinition> getDefinitions() {
        return Collections.unmodifiableList(definitions);
    }

    public List<QuestItemDefinition> getDefinitions(int itemId) {
        if (definitions.isEmpty()) {
            return List.of();
        }
        List<QuestItemDefinition> matches = new ArrayList<>();
        for (QuestItemDefinition definition : definitions) {
            if (definition.itemId() == itemId) {
                matches.add(definition);
            }
        }
        return matches;
    }

    public List<QuestItemDefinition> getDefinitionsForQuest(String questId) {
        if (questId == null || questId.isEmpty() || definitions.isEmpty()) {
            return List.of();
        }
        List<QuestItemDefinition> matches = new ArrayList<>();
        for (QuestItemDefinition definition : definitions) {
            if (questId.equalsIgnoreCase(definition.questId())) {
                matches.add(definition);
            }
        }
        return matches;
    }
}
