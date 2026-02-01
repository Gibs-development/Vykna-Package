package io.xeros.content.questsystem.service;

import io.xeros.content.questsystem.engine.QuestHandler;
import io.xeros.content.questsystem.engine.QuestHandlerRegistry;
import io.xeros.content.questsystem.event.QuestEvent;
import io.xeros.content.questsystem.feedback.QuestFeedback;
import io.xeros.content.questsystem.model.QuestDefinition;
import io.xeros.content.questsystem.model.QuestId;
import io.xeros.content.questsystem.model.QuestProfile;
import io.xeros.content.questsystem.model.QuestProgress;
import io.xeros.content.questsystem.model.QuestState;
import io.xeros.content.questsystem.model.QuestStep;
import io.xeros.content.questsystem.registry.QuestRegistry;
import io.xeros.model.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class QuestServiceImpl implements QuestService {
    private static final Logger logger = LoggerFactory.getLogger(QuestServiceImpl.class);
    private final QuestHandlerRegistry handlerRegistry;
    private final QuestRegistry questRegistry;

    public QuestServiceImpl(QuestHandlerRegistry handlerRegistry) {
        this(handlerRegistry, null);
    }

    public QuestServiceImpl(QuestHandlerRegistry handlerRegistry, QuestRegistry questRegistry) {
        this.handlerRegistry = Objects.requireNonNull(handlerRegistry, "handlerRegistry");
        this.questRegistry = questRegistry;
    }

    @Override
    public QuestProgress startQuest(QuestId questId) {
        throw new UnsupportedOperationException("QuestServiceImpl.startQuest requires player context");
    }

    @Override
    public QuestProgress getProgress(QuestId questId) {
        throw new UnsupportedOperationException("QuestServiceImpl.getProgress requires player context");
    }

    @Override
    public QuestState getState(QuestId questId) {
        throw new UnsupportedOperationException("QuestServiceImpl.getState requires player context");
    }

    @Override
    public void advanceObjective(QuestId questId, String objectiveId) {
        throw new UnsupportedOperationException("QuestServiceImpl.advanceObjective requires player context");
    }

    @Override
    public void completeQuest(QuestId questId) {
        throw new UnsupportedOperationException("QuestServiceImpl.completeQuest requires player context");
    }

    @Override
    public void handle(Player player, QuestEvent event) {
        if (player == null || event == null) {
            return;
        }
        QuestProfile profile = player.getQuestProfile();
        profile.ensureDefaults();
        for (Map.Entry<String, QuestProgress> entry : profile.getQuests().entrySet()) {
            QuestProgress progress = entry.getValue();
            if (progress == null) {
                continue;
            }
            progress.ensureDefaults();
            if (progress.getState() == QuestState.COMPLETED) {
                continue;
            }
            if (progress.getState() == QuestState.NOT_STARTED) {
                continue;
            }
            handlerRegistry.find(entry.getKey()).ifPresent(handler -> handleQuestEvent(player, progress, handler, event));
        }
    }

    private void handleQuestEvent(Player player, QuestProgress progress, QuestHandler handler, QuestEvent event) {
        int oldStage = progress.getStage();
        int newStage = handler.nextStage(player, progress, event);
        if (newStage <= oldStage) {
            return;
        }
        progress.setStage(newStage);
        if (newStage >= handler.completionStage()) {
            progress.setState(QuestState.COMPLETED);
            QuestFeedback.showQuestComplete(player, getRewardLines(handler.questId()));
        } else {
            progress.setState(QuestState.IN_PROGRESS);
            QuestFeedback.showQuestUpdatedToast(player, getCurrentObjectiveText(player, handler.questId(), newStage));
            if (handler instanceof io.xeros.content.questsystem.engine.StepQuestHandler) {
                ((io.xeros.content.questsystem.engine.StepQuestHandler) handler).startCurrentStep(player, progress);
            }
        }
        io.xeros.content.questsystem.QuestSystem.updateQuestList(player);
        logger.info("Quest advance questId={}, {}->{} via {}", handler.questId(), oldStage, newStage, event.getType());
    }

    @Override
    public List<String> getQuestJournalLines(Player player, String questId) {
        if (player == null || questId == null || questId.isEmpty() || questRegistry == null) {
            return List.of("No quest data available.");
        }
        QuestDefinition definition = questRegistry.find(new QuestId(questId)).orElse(null);
        if (definition == null) {
            return List.of("Unknown quest.");
        }
        QuestProgress progress = player.getQuestProfile().getOrCreate(questId);
        progress.ensureDefaults();
        int stage = progress.getStage();
        if (progress.getState() == QuestState.COMPLETED || stage >= definition.steps().size()) {
            return List.of("Quest complete.");
        }
        QuestStep step = definition.steps().get(Math.max(0, stage));
        List<String> lines = step.getJournalText(progress);
        return lines == null || lines.isEmpty() ? List.of("Quest updated.") : lines;
    }

    private String getCurrentObjectiveText(Player player, String questId, int stage) {
        if (questRegistry == null) {
            return "Quest updated.";
        }
        QuestDefinition definition = questRegistry.find(new QuestId(questId)).orElse(null);
        if (definition == null) {
            return "Quest updated.";
        }
        if (stage >= definition.steps().size()) {
            return "Quest complete.";
        }
        QuestProgress progress = player.getQuestProfile().getOrCreate(questId);
        progress.ensureDefaults();
        List<String> lines = definition.steps().get(stage).getJournalText(progress);
        if (lines == null || lines.isEmpty()) {
            return "Quest updated.";
        }
        return lines.get(0);
    }

    private List<String> getRewardLines(String questId) {
        if (questRegistry == null) {
            return Collections.emptyList();
        }
        QuestDefinition definition = questRegistry.find(new QuestId(questId)).orElse(null);
        if (definition == null || definition.rewards() == null) {
            return Collections.emptyList();
        }
        return definition.rewards().stream()
                .map(reward -> reward == null ? null : reward.description())
                .filter(line -> line != null && !line.isEmpty())
                .toList();
    }
}
