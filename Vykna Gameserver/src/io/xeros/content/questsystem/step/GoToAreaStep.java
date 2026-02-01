package io.xeros.content.questsystem.step;

import java.util.List;

import io.xeros.content.questsystem.event.QuestEvent;
import io.xeros.content.questsystem.event.QuestEventKeys;
import io.xeros.content.questsystem.event.QuestEventType;
import io.xeros.content.questsystem.model.QuestProgress;
import io.xeros.content.questsystem.model.QuestStep;
import io.xeros.model.entity.player.Player;

public class GoToAreaStep extends QuestStepSupport implements QuestStep {
    private final String stepId;
    private final AreaRequirement area;
    private final List<String> journalText;

    public GoToAreaStep(String stepId, AreaRequirement area, List<String> journalText) {
        this.stepId = stepId;
        this.area = area;
        this.journalText = journalText;
    }

    @Override
    public List<String> getJournalText(QuestProgress progress) {
        return journalText;
    }

    @Override
    public void onStart(Player player, QuestProgress progress) {
        setBool(progress, key(stepId, "started"), true);
    }

    @Override
    public void onEvent(Player player, QuestProgress progress, QuestEvent event) {
        if (event.getType() != QuestEventType.AREA_ENTER) {
            return;
        }
        Object xValue = event.get(QuestEventKeys.AREA_X);
        Object yValue = event.get(QuestEventKeys.AREA_Y);
        Object heightValue = event.get(QuestEventKeys.AREA_HEIGHT);
        if (!(xValue instanceof Number) || !(yValue instanceof Number)) {
            return;
        }
        int height = heightValue instanceof Number ? ((Number) heightValue).intValue() : 0;
        if (area.contains(((Number) xValue).intValue(), ((Number) yValue).intValue(), height)) {
            setBool(progress, key(stepId, "complete"), true);
        }
    }

    @Override
    public boolean isComplete(Player player, QuestProgress progress) {
        return getBool(progress, key(stepId, "complete"));
    }

    @Override
    public void onComplete(Player player, QuestProgress progress) {
        setBool(progress, key(stepId, "complete"), true);
    }
}
