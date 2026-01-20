package io.xeros.content.vykna_achievements;

public final class VyknaAchievementDefinition {
    private final int id;
    private final String type;
    private final String group;
    private final int target;
    private final int points;

    public VyknaAchievementDefinition(int id, String type, String group, int target, int points) {
        this.id = id;
        this.type = type;
        this.group = group;
        this.target = target;
        this.points = points;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getGroup() {
        return group;
    }

    public int getTarget() {
        return target;
    }

    public int getPoints() {
        return points;
    }
}
