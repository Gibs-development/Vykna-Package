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

public class DesertTreasureQuestDefinition implements QuestDefinition {
    public static final QuestId ID = new QuestId(DesertTreasureQuestHandler.QUEST_ID);

    private final List<QuestStep> steps;

    public DesertTreasureQuestDefinition() {
        QuestStep placeholder = new PlaceholderStep();
        List<QuestStep> stepList = new ArrayList<>(DesertTreasureQuestHandler.STAGE_COMPLETE + 1);
        for (int i = 0; i <= DesertTreasureQuestHandler.STAGE_COMPLETE; i++) {
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
        return "Desert Treasure";
    }

    @Override
    public String description() {
        return "Help Eblis recover four ancient diamonds and free Azzanadra.";
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
            return List.of("Desert Treasure updated.");
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
