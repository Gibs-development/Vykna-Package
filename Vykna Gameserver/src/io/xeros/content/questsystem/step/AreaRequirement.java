package io.xeros.content.questsystem.step;

public final class AreaRequirement {
    private final int minX;
    private final int minY;
    private final int maxX;
    private final int maxY;
    private final int height;

    public AreaRequirement(int minX, int minY, int maxX, int maxY, int height) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.height = height;
    }

    public boolean contains(int x, int y, int heightLevel) {
        if (height >= 0 && height != heightLevel) {
            return false;
        }
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }
}
