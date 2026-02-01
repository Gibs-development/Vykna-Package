package io.xeros.content.questsystem.step;

import java.util.List;

import io.xeros.content.questsystem.event.QuestEvent;
import io.xeros.content.questsystem.event.QuestEventKeys;
import io.xeros.content.questsystem.event.QuestEventType;
import io.xeros.content.questsystem.model.QuestProgress;
import io.xeros.content.questsystem.model.QuestStep;
import io.xeros.model.entity.player.Player;

public abstract class PuzzleStep extends QuestStepSupport implements QuestStep {
    public static final String ACTION_OPEN = "open";
    public static final String ACTION_CLOSE = "close";

    private final String stepId;
    private final int interfaceId;
    private final List<String> journalText;

    protected PuzzleStep(String stepId, int interfaceId, List<String> journalText) {
        this.stepId = stepId;
        this.interfaceId = interfaceId;
        this.journalText = journalText;
    }

    @Override
    public List<String> getJournalText(QuestProgress progress) {
        return journalText;
    }

    @Override
    public void onStart(Player player, QuestProgress progress) {
        setBool(progress, key(stepId, "started"), true);
        if (isSolved(progress)) {
            return;
        }
        openPuzzle(player, progress);
        onPuzzleStart(player, progress);
    }

    @Override
    public void onEvent(Player player, QuestProgress progress, QuestEvent event) {
        if (event.getType() != QuestEventType.INTERFACE_ACTION) {
            return;
        }
        if (isSolved(progress)) {
            return;
        }
        Object actionValue = event.get(QuestEventKeys.INTERFACE_ACTION);
        Object idValue = event.get(QuestEventKeys.INTERFACE_ID);

        if (ACTION_OPEN.equals(actionValue)) {
            if (matchesInterfaceId(idValue)) {
                setOpen(progress, true);
                onPuzzleOpen(player, progress);
            }
            return;
        }
        if (ACTION_CLOSE.equals(actionValue)) {
            if (matchesInterfaceId(idValue)) {
                closePuzzle(player, progress);
                onPuzzleClose(player, progress);
            }
            return;
        }

        Integer componentId = toInt(idValue);
        if (componentId == null || !handlesComponent(componentId)) {
            return;
        }
        if (!isOpen(progress)) {
            return;
        }
        Integer actionInt = toInt(actionValue);
        onPuzzleClick(player, progress, componentId, actionInt == null ? 0 : actionInt);
    }

    @Override
    public boolean isComplete(Player player, QuestProgress progress) {
        return isSolved(progress);
    }

    @Override
    public void onComplete(Player player, QuestProgress progress) {
        setSolved(progress, true);
    }

    protected abstract void onPuzzleStart(Player player, QuestProgress progress);

    protected void onPuzzleOpen(Player player, QuestProgress progress) {
    }

    protected void onPuzzleClose(Player player, QuestProgress progress) {
    }

    protected abstract void onPuzzleClick(Player player, QuestProgress progress, int componentId, int action);

    protected abstract boolean handlesComponent(int componentId);

    protected final String getStepId() {
        return stepId;
    }

    protected final int getInterfaceId() {
        return interfaceId;
    }

    protected final boolean isSolved(QuestProgress progress) {
        return getBool(progress, key(stepId, "solved"));
    }

    protected final void setSolved(QuestProgress progress, boolean solved) {
        setBool(progress, key(stepId, "solved"), solved);
    }

    protected final boolean isOpen(QuestProgress progress) {
        return getBool(progress, key(stepId, "open"));
    }

    protected final void setOpen(QuestProgress progress, boolean open) {
        setBool(progress, key(stepId, "open"), open);
    }

    protected final void openPuzzle(Player player, QuestProgress progress) {
        if (player == null) {
            return;
        }
        setOpen(progress, true);
        player.getPA().showInterface(interfaceId);
    }

    protected final void closePuzzle(Player player, QuestProgress progress) {
        setOpen(progress, false);
    }

    private boolean matchesInterfaceId(Object value) {
        Integer id = toInt(value);
        return id != null && id == interfaceId;
    }

    private Integer toInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }
}
