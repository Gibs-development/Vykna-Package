package io.xeros.content.bossfactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BossRotationBuilder {
    private final List<BossMechanic> mechanics = new ArrayList<>();
    private boolean looping;

    public static BossRotationBuilder rotation() {
        return new BossRotationBuilder();
    }

    public static BossMechanic[] alternate(BossMechanic first, BossMechanic second) {
        return new BossMechanic[]{first, second};
    }

    public BossRotationBuilder repeat(int times, BossMechanic... mechanics) {
        for (int i = 0; i < times; i++) {
            this.mechanics.addAll(Arrays.asList(mechanics));
        }
        return this;
    }

    public BossRotationBuilder then(BossMechanic... mechanics) {
        this.mechanics.addAll(Arrays.asList(mechanics));
        return this;
    }

    public BossRotationBuilder loop() {
        this.looping = true;
        return this;
    }

    public BossRotation build() {
        return new BossRotation(mechanics, looping);
    }

    public BossRotation buildLooping() {
        this.looping = true;
        return new BossRotation(mechanics, looping);
    }
}
