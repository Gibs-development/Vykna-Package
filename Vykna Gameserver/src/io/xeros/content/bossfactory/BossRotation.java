package io.xeros.content.bossfactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BossRotation {
    private final List<BossMechanic> mechanics;
    private final boolean looping;
    private int index;

    BossRotation(List<BossMechanic> mechanics, boolean looping) {
        this.mechanics = new ArrayList<>(mechanics);
        this.looping = looping;
        this.index = 0;
    }

    public Optional<BossMechanic> next() {
        if (mechanics.isEmpty()) {
            return Optional.empty();
        }
        if (index >= mechanics.size()) {
            if (!looping) {
                return Optional.empty();
            }
            index = 0;
        }
        return Optional.of(mechanics.get(index++));
    }

    public void reset() {
        index = 0;
    }

    public List<BossMechanic> getMechanics() {
        return Collections.unmodifiableList(mechanics);
    }
}
