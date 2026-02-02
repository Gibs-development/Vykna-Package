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

public class CourierFavorQuestDefinition implements QuestDefinition {
    public static final QuestId ID = new QuestId(CourierFavorQuestHandler.QUEST_ID);

    private final List<QuestStep> steps;

    public CourierFavorQuestDefinition() {
        QuestStep placeholder = new PlaceholderStep();
        List<QuestStep> stepList = new ArrayList<>(CourierFavorQuestHandler.STAGE_COMPLETE + 1);
        for (int i = 0; i <= CourierFavorQuestHandler.STAGE_COMPLETE; i++) {
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
        return "Courier's Favor";
    }

    @Override
    public String description() {
        return "Deliver a research package for the engineering team.";
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
            return List.of("Courier's Favor updated.");
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
