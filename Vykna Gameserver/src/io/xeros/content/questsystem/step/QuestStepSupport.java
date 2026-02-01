package io.xeros.content.questsystem.step;

import java.util.Map;

import io.xeros.content.questsystem.model.QuestProgress;

public abstract class QuestStepSupport {
    protected static boolean getBool(QuestProgress progress, String key) {
        Object value = progress.getVars().get(key);
        return value instanceof Boolean && (Boolean) value;
    }

    protected static void setBool(QuestProgress progress, String key, boolean value) {
        progress.getVars().put(key, value);
    }

    protected static int getInt(QuestProgress progress, String key) {
        Object value = progress.getVars().get(key);
        return value instanceof Number ? ((Number) value).intValue() : 0;
    }

    protected static void setInt(QuestProgress progress, String key, int value) {
        progress.getVars().put(key, value);
    }

    protected static String key(String stepId, String suffix) {
        return "step." + stepId + "." + suffix;
    }

    protected static Map<String, Object> vars(QuestProgress progress) {
        return progress.getVars();
    }
}
