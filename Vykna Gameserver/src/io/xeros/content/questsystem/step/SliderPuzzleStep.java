package io.xeros.content.questsystem.step;

import java.util.ArrayList;
import java.util.List;

import io.xeros.content.questsystem.model.QuestProgress;
import io.xeros.model.entity.player.Player;

/**
 * Slider puzzle step that persists puzzle state in quest vars across relogs.
 */
public class SliderPuzzleStep extends PuzzleStep {
    private static final int SIZE = 3;
    private static final int TILE_COUNT = SIZE * SIZE;

    private final int[] componentIds;
    private final int[] initialState;
    private final int[] solvedState;

    public SliderPuzzleStep(String stepId, int interfaceId, int[] componentIds, int[] initialState, List<String> journalText) {
        super(stepId, interfaceId, journalText);
        if (componentIds == null || componentIds.length != TILE_COUNT) {
            throw new IllegalArgumentException("componentIds must have length " + TILE_COUNT);
        }
        this.componentIds = componentIds.clone();
        this.initialState = sanitizeState(initialState);
        this.solvedState = defaultSolvedState();
    }

    @Override
    protected void onPuzzleStart(Player player, QuestProgress progress) {
        int[] state = loadState(progress);
        if (state == null) {
            state = initialState.clone();
            saveState(progress, state);
        }
        render(player, state);
    }

    @Override
    protected void onPuzzleOpen(Player player, QuestProgress progress) {
        int[] state = loadState(progress);
        if (state == null) {
            state = initialState.clone();
            saveState(progress, state);
        }
        render(player, state);
    }

    @Override
    protected void onPuzzleClick(Player player, QuestProgress progress, int componentId, int action) {
        int index = indexOf(componentId);
        if (index == -1) {
            return;
        }
        int[] state = loadState(progress);
        if (state == null) {
            state = initialState.clone();
        }
        if (state[index] == 0) {
            return;
        }
        int emptyIndex = findEmpty(state);
        if (!isAdjacent(index, emptyIndex)) {
            return;
        }
        int tmp = state[index];
        state[index] = state[emptyIndex];
        state[emptyIndex] = tmp;
        saveState(progress, state);
        render(player, state);
        if (isSolvedState(state)) {
            setSolved(progress, true);
        }
    }

    @Override
    protected boolean handlesComponent(int componentId) {
        return indexOf(componentId) != -1;
    }

    private int indexOf(int componentId) {
        for (int i = 0; i < componentIds.length; i++) {
            if (componentIds[i] == componentId) {
                return i;
            }
        }
        return -1;
    }

    private boolean isAdjacent(int index, int emptyIndex) {
        int x1 = index % SIZE;
        int y1 = index / SIZE;
        int x2 = emptyIndex % SIZE;
        int y2 = emptyIndex / SIZE;
        return Math.abs(x1 - x2) + Math.abs(y1 - y2) == 1;
    }

    private int findEmpty(int[] state) {
        for (int i = 0; i < state.length; i++) {
            if (state[i] == 0) {
                return i;
            }
        }
        return state.length - 1;
    }

    private boolean isSolvedState(int[] state) {
        if (state == null || state.length != solvedState.length) {
            return false;
        }
        for (int i = 0; i < solvedState.length; i++) {
            if (state[i] != solvedState[i]) {
                return false;
            }
        }
        return true;
    }

    private int[] defaultSolvedState() {
        int[] solved = new int[TILE_COUNT];
        for (int i = 0; i < TILE_COUNT - 1; i++) {
            solved[i] = i + 1;
        }
        solved[TILE_COUNT - 1] = 0;
        return solved;
    }

    private int[] sanitizeState(int[] state) {
        if (state == null || state.length != TILE_COUNT) {
            return defaultSolvedState();
        }
        return state.clone();
    }

    private void render(Player player, int[] state) {
        if (player == null || state == null) {
            return;
        }
        for (int i = 0; i < componentIds.length; i++) {
            String text = state[i] == 0 ? "" : Integer.toString(state[i]);
            player.getPA().sendFrame126(text, componentIds[i]);
        }
    }

    private String stateKey() {
        return key(getStepId(), "state");
    }

    private int[] loadState(QuestProgress progress) {
        Object raw = progress.getVars().get(stateKey());
        if (raw instanceof int[]) {
            int[] state = (int[]) raw;
            return state.length == TILE_COUNT ? state.clone() : null;
        }
        if (raw instanceof String) {
            return parseState((String) raw);
        }
        if (raw instanceof List) {
            return parseStateFromList((List<?>) raw);
        }
        return null;
    }

    private int[] parseState(String raw) {
        if (raw == null || raw.isEmpty()) {
            return null;
        }
        String[] parts = raw.split(",");
        if (parts.length != TILE_COUNT) {
            return null;
        }
        int[] state = new int[TILE_COUNT];
        for (int i = 0; i < parts.length; i++) {
            try {
                state[i] = Integer.parseInt(parts[i].trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return state;
    }

    private int[] parseStateFromList(List<?> raw) {
        if (raw.size() != TILE_COUNT) {
            return null;
        }
        int[] state = new int[TILE_COUNT];
        for (int i = 0; i < raw.size(); i++) {
            Object value = raw.get(i);
            if (value instanceof Number) {
                state[i] = ((Number) value).intValue();
            } else if (value instanceof String) {
                try {
                    state[i] = Integer.parseInt((String) value);
                } catch (NumberFormatException e) {
                    return null;
                }
            } else {
                return null;
            }
        }
        return state;
    }

    private void saveState(QuestProgress progress, int[] state) {
        if (state == null || state.length != TILE_COUNT) {
            return;
        }
        List<String> parts = new ArrayList<>(TILE_COUNT);
        for (int value : state) {
            parts.add(Integer.toString(value));
        }
        progress.getVars().put(stateKey(), String.join(",", parts));
    }
}
