package io.xeros.content.vykna_teleports.model;

/**
 * Simple destination container (x, y, height).
 */
public final class TeleportDestination {
    private final int x;
    private final int y;
    private final int height;

    public TeleportDestination(int x, int y, int height) {
        this.x = x;
        this.y = y;
        this.height = height;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getHeight() { return height; }
}
