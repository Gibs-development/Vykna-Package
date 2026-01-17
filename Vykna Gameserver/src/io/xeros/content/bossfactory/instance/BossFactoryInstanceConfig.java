package io.xeros.content.bossfactory.instance;

import java.util.EnumSet;
import java.util.Set;

import io.xeros.content.bossfactory.BossMechanic;

public class BossFactoryInstanceConfig {
    private JoinPolicy joinPolicy = JoinPolicy.SOLO;
    private final Set<BossMechanic> enabledMechanics = EnumSet.noneOf(BossMechanic.class);

    public BossFactoryInstanceConfig() {
        enabledMechanics.add(BossMechanic.FLOOR_SPLAT);
        enabledMechanics.add(BossMechanic.ARENA_BEAM);
    }

    public JoinPolicy getJoinPolicy() {
        return joinPolicy;
    }

    public void setJoinPolicy(JoinPolicy joinPolicy) {
        this.joinPolicy = joinPolicy;
    }

    public Set<BossMechanic> getEnabledMechanics() {
        return EnumSet.copyOf(enabledMechanics);
    }

    public void toggleMechanic(BossMechanic mechanic) {
        if (enabledMechanics.contains(mechanic)) {
            enabledMechanics.remove(mechanic);
        } else {
            enabledMechanics.add(mechanic);
        }
    }

    public boolean isEnabled(BossMechanic mechanic) {
        return enabledMechanics.contains(mechanic);
    }

    public int enabledOptionalCount() {
        int count = 0;
        for (BossMechanic mechanic : enabledMechanics) {
            if (!mechanic.isBasic()) {
                count++;
            }
        }
        return count;
    }

    public double enabledFraction(int totalOptional) {
        if (totalOptional <= 0) {
            return 1.0;
        }
        return Math.min(1.0, (double) enabledOptionalCount() / totalOptional);
    }
}
