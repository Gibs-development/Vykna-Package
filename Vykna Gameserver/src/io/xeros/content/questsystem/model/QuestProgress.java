package io.xeros.content.questsystem.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class QuestProgress {
    private String questId;
    private QuestState state;
    private int stage;
    private Map<String, Object> vars;

    public QuestProgress() {
        this("", QuestState.NOT_STARTED, 0, new HashMap<>());
    }

    public QuestProgress(String questId) {
        this(questId, QuestState.NOT_STARTED, 0, new HashMap<>());
    }

    public QuestProgress(String questId, QuestState state, int stage, Map<String, Object> vars) {
        this.questId = Objects.requireNonNull(questId, "questId");
        this.state = state;
        this.stage = stage;
        this.vars = vars == null ? new HashMap<>() : vars;
    }

    public void ensureDefaults() {
        if (state == null) {
            state = QuestState.NOT_STARTED;
        }
        if (vars == null) {
            vars = new HashMap<>();
        }
    }

    public String getQuestId() {
        return questId;
    }

    public void setQuestId(String questId) {
        this.questId = Objects.requireNonNull(questId, "questId");
    }

    public QuestState getState() {
        return state;
    }

    public void setState(QuestState state) {
        this.state = state;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public Map<String, Object> getVars() {
        return vars;
    }

    public void setVars(Map<String, Object> vars) {
        this.vars = vars == null ? new HashMap<>() : vars;
    }
}
