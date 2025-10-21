import java.awt.*;
import javax.swing.*;

public abstract class MovingObject extends GameObject {

    protected int dx, dy;          // Direction of movement
    private int speed = 1;         // Moves every `speed` ticks
    private int tickCounter = 0;   // Counts ticks to handle speed

    public MovingObject(int x, int y, String imagePath) {
        super(x, y, imagePath);
        this.dx = 0;
        this.dy = 0;
    }

    /** Set movement direction (-1, 0, 1) */
    public void setDirection(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx() { return dx; }
    public int getDy() { return dy; }

    /** Set how frequently this object moves (1 = every tick, 2 = every 2 ticks, etc.) */
    public void setSpeed(int speed) {
        this.speed = Math.max(1, speed);
    }

    public int getSpeed() {
        return speed;
    }

    /** Moves the object respecting speed and collisions */
    public void move(int GRID_WIDTH, int GRID_HEIGHT) {
        tickCounter++;
        if (tickCounter < speed) return; // Skip movement until enough ticks
        tickCounter = 0;

        Point next = new Point(pos.x + dx, pos.y + dy);

        // Keep inside grid
        if (next.x < 0 || next.y < 0 || next.x >= GRID_WIDTH || next.y >= GRID_HEIGHT) {
            return;
        }

        // Check collision with blocks
        if (BlockManager.isBlocked(next)) {
            return;
        }

        pos = next;
    }

    /** Directly set position */
    public void setPosition(int x, int y) {
        this.pos.setLocation(x, y);
    }

    /** Draw with optional offset inside tile */
    public void drawAtOffset(Graphics g, JPanel panel, int tileSize, int offsetX, int offsetY) {
        if (image != null) {
            g.drawImage(image, pos.x * tileSize + offsetX, pos.y * tileSize + offsetY,
                        tileSize - 2 * offsetX, tileSize - 2 * offsetY, panel);
        } else {
            g.fillRect(pos.x * tileSize + offsetX, pos.y * tileSize + offsetY,
                       tileSize - 2 * offsetX, tileSize - 2 * offsetY);
        }
    }

    /** Standard draw (no offset) */
    public void draw(Graphics g, JPanel panel, int tileSize) {
        drawAtOffset(g, panel, tileSize, 0, 0);
    }
}

