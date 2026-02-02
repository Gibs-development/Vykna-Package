package io.xeros.content.questsystem.sample;

import java.util.ArrayList;
import java.util.List;

import io.xeros.content.questsystem.event.QuestEvent;
import io.xeros.content.questsystem.model.QuestDefinition;
import io.xeros.content.questsystem.model.QuestId;
import io.xeros.content.questsystem.model.QuestProgress;
import io.xeros.content.questsystem.model.QuestReward;
import io.xeros.content.questsystem.model.QuestStep;
import io.xeros.content.questsystem.step.QuestStepSupport;
import io.xeros.model.entity.player.Player;

public class OneSmallFavorQuestDefinition implements QuestDefinition {
    public static final QuestId ID = new QuestId(OneSmallFavorQuestHandler.QUEST_ID);

    private final List<QuestStep> steps;

    public OneSmallFavorQuestDefinition() {
        QuestStep placeholder = new PlaceholderStep();
        List<QuestStep> stepList = new ArrayList<>(OneSmallFavorQuestHandler.STAGE_COMPLETE + 1);
        for (int i = 0; i <= OneSmallFavorQuestHandler.STAGE_COMPLETE; i++) {
            stepList.add(placeholder);
        }
        this.steps = List.copyOf(stepList);
    }

    @Override
    public QuestId id() {
        return ID;
    }

    @Override
    public String name() {
        return "One Small Favour";
    }

    @Override
    public String description() {
        return "A favour that spirals wildly out of control.";
    }

    @Override
    public List<QuestStep> steps() {
        return steps;
    }

    @Override
    public List<QuestReward> rewards() {
        return List.of();
    }

    private static class PlaceholderStep extends QuestStepSupport implements QuestStep {
        @Override
        public List<String> getJournalText(QuestProgress progress) {
            return List.of("One Small Favour updated.");
        }

        @Override
        public void onStart(Player player, QuestProgress progress) {
        }

        @Override
        public void onEvent(Player player, QuestProgress progress, QuestEvent event) {
        }

        @Override
        public boolean isComplete(Player player, QuestProgress progress) {
            return false;
        }

        @Override
        public void onComplete(Player player, QuestProgress progress) {
        }
    }
}
