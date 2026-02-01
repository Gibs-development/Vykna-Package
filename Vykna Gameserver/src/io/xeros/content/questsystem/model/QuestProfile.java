package io.xeros.content.questsystem.model;

import java.util.HashMap;
import java.util.Map;

public final class QuestProfile {
    private Map<String, QuestProgress> quests;

    public QuestProfile() {
        this.quests = new HashMap<>();
    }

    public Map<String, QuestProgress> getQuests() {
        return quests;
    }

    public void setQuests(Map<String, QuestProgress> quests) {
        this.quests = quests == null ? new HashMap<>() : quests;
    }

    public QuestProgress getOrCreate(String questId) {
        if (quests == null) {
            quests = new HashMap<>();
        }
        return quests.computeIfAbsent(questId, QuestProgress::new);
    }

    public void ensureDefaults() {
        if (quests == null) {
            quests = new HashMap<>();
            return;
        }
        for (Map.Entry<String, QuestProgress> entry : quests.entrySet()) {
            QuestProgress progress = entry.getValue();
            if (progress == null) {
                continue;
            }
            if (progress.getQuestId() == null || progress.getQuestId().isEmpty()) {
                progress.setQuestId(entry.getKey());
            }
            progress.ensureDefaults();
        }
    }
}
