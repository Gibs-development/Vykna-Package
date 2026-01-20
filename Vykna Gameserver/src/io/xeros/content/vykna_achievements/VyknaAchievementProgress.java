package io.xeros.content.vykna_achievements;

public final class VyknaAchievementProgress {
    private int current;
    private int target;
    private boolean done;

    public VyknaAchievementProgress() {
    }

    public VyknaAchievementProgress(int current, int target, boolean done) {
        this.current = current;
        this.target = target;
        this.done = done;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
