package io.xeros.content.questsystem.model;

import java.util.List;

public interface QuestDefinition {
    QuestId id();

    String name();

    String description();

    List<QuestStep> steps();

    List<QuestReward> rewards();
}
