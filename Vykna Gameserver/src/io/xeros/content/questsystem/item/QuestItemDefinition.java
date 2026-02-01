package io.xeros.content.questsystem.item;

import java.util.Objects;

public final class QuestItemDefinition {
    private final String questId;
    private final int itemId;
    private final int amount;
    private final boolean undroppable;
    private final boolean untradeable;
    private final QuestItemReclaimPolicy reclaimPolicy;
    private final int minStage;
    private final int maxStage;

    private QuestItemDefinition(Builder builder) {
        this.questId = Objects.requireNonNull(builder.questId, "questId");
        this.itemId = builder.itemId;
        this.amount = Math.max(1, builder.amount);
        this.undroppable = builder.undroppable;
        this.untradeable = builder.untradeable;
        this.reclaimPolicy = builder.reclaimPolicy == null ? QuestItemReclaimPolicy.NONE : builder.reclaimPolicy;
        this.minStage = builder.minStage;
        this.maxStage = builder.maxStage;
    }

    public String questId() {
        return questId;
    }

    public int itemId() {
        return itemId;
    }

    public int amount() {
        return amount;
    }

    public boolean undroppable() {
        return undroppable;
    }

    public boolean untradeable() {
        return untradeable;
    }

    public QuestItemReclaimPolicy reclaimPolicy() {
        return reclaimPolicy;
    }

    public boolean isReclaimable() {
        return reclaimPolicy != QuestItemReclaimPolicy.NONE;
    }

    public int minStage() {
        return minStage;
    }

    public int maxStage() {
        return maxStage;
    }

    public boolean matchesStage(int stage) {
        if (minStage >= 0 && stage < minStage) {
            return false;
        }
        if (maxStage >= 0 && stage > maxStage) {
            return false;
        }
        return true;
    }

    public static Builder builder(String questId, int itemId) {
        return new Builder(questId, itemId);
    }

    public static final class Builder {
        private final String questId;
        private final int itemId;
        private int amount = 1;
        private boolean undroppable;
        private boolean untradeable;
        private QuestItemReclaimPolicy reclaimPolicy = QuestItemReclaimPolicy.NONE;
        private int minStage = -1;
        private int maxStage = -1;

        private Builder(String questId, int itemId) {
            this.questId = questId;
            this.itemId = itemId;
        }

        public Builder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public Builder undroppable(boolean undroppable) {
            this.undroppable = undroppable;
            return this;
        }

        public Builder untradeable(boolean untradeable) {
            this.untradeable = untradeable;
            return this;
        }

        public Builder reclaimPolicy(QuestItemReclaimPolicy reclaimPolicy) {
            this.reclaimPolicy = reclaimPolicy;
            return this;
        }

        public Builder stageRange(int minStage, int maxStage) {
            this.minStage = minStage;
            this.maxStage = maxStage;
            return this;
        }

        public QuestItemDefinition build() {
            return new QuestItemDefinition(this);
        }
    }
}
