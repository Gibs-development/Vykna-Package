package io.xeros.content.questsystem.model;

import java.util.Objects;

public final class QuestId {
    private final String value;

    public QuestId(String value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof QuestId)) {
            return false;
        }
        QuestId that = (QuestId) other;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
