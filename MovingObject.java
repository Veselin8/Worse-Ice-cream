import java.awt.*;
import javax.swing.*;

public abstract class MovingObject extends GameObject {

    protected int dx, dy;           // Direction of movement
    protected Point previousPos;    // NEW FIELD: Stores position before the last move
    private int speed = 1;          // Moves every `speed` ticks
    private int tickCounter = 0;    // Counts ticks to handle speed

    public MovingObject(int x, int y, String imagePath) {
        super(x, y, imagePath);
        this.dx = 0;
        this.dy = 0;
        this.previousPos = new Point(x, y); // Initialize previousPos
    }

    /** Set movement direction (-1, 0, 1) */
    public void setDirection(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx() { return dx; }
    public int getDy() { return dy; }
    
    /** Getter for the previous position */
    public Point getPreviousPos() {
        return previousPos;
    }

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

        // *** CRITICAL CHANGE: Save current position before movement ***
        this.previousPos = new Point(this.pos); 

        Point next = new Point(pos.x + dx, pos.y + dy);

        // Keep inside grid
        if (next.x < 0 || next.y < 0 || next.x >= GRID_WIDTH || next.y >= GRID_HEIGHT) {
            return;
        }

        // *** BLOCK COLLISION CHECK RESTORED ***
        // NOTE: BlockManager is assumed to be accessible here, as in the original code.
        if (BlockManager.isBlocked(next)) {
            // If we hit a block, we stop but we don't change previousPos or pos.
            // If the object's direction is reset elsewhere (like in CollisionManager),
            // this handles the check before the move happens.
            return;
        }

        pos = next;
    }

    /** Directly set position */
    public void setPosition(int x, int y) {
        this.pos.setLocation(x, y);
        // Also update previousPos to match when setting position directly.
        this.previousPos.setLocation(x, y); 
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
