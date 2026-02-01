package io.xeros.content.questsystem.engine;

import io.xeros.content.questsystem.event.QuestEvent;
import io.xeros.content.questsystem.model.QuestDefinition;
import io.xeros.content.questsystem.model.QuestProgress;
import io.xeros.content.questsystem.model.QuestStep;
import io.xeros.model.entity.player.Player;

public class StepQuestHandler implements QuestHandler {
    private final QuestDefinition definition;

    public StepQuestHandler(QuestDefinition definition) {
        this.definition = definition;
    }

    @Override
    public String questId() {
        return definition.id().value();
    }

    @Override
    public int completionStage() {
        return definition.steps().size();
    }

    @Override
    public int nextStage(Player player, QuestProgress progress, QuestEvent event) {
        int stage = Math.max(0, progress.getStage());
        if (stage >= definition.steps().size()) {
            return stage;
        }
        QuestStep step = definition.steps().get(stage);
        ensureStarted(player, progress, stage, step);
        step.onEvent(player, progress, event);
        if (step.isComplete(player, progress)) {
            step.onComplete(player, progress);
            return stage + 1;
        }
        return stage;
    }

    public void startCurrentStep(Player player, QuestProgress progress) {
        int stage = Math.max(0, progress.getStage());
        if (stage >= definition.steps().size()) {
            return;
        }
        QuestStep step = definition.steps().get(stage);
        ensureStarted(player, progress, stage, step);
    }

    private void ensureStarted(Player player, QuestProgress progress, int stage, QuestStep step) {
        String startedKey = "step." + stage + ".started";
        Object startedValue = progress.getVars().get(startedKey);
        if (!(startedValue instanceof Boolean) || !(Boolean) startedValue) {
            progress.getVars().put(startedKey, true);
            step.onStart(player, progress);
        }
    }
}
