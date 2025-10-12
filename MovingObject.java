import java.awt.*;
import javax.swing.*;

public abstract class MovingObject extends GameObject {

    protected int dx, dy;

    public MovingObject(int x, int y, String imagePath) {
        super(x, y, imagePath);
    }

    public void setDirection(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx() {
        return this.dx;
    }

    public int getDy() {
        return this.dy;
    }

    public void move(int GRID_WIDTH, int GRID_HEIGHT) {
        Point next = new Point(pos.x + dx, pos.y + dy);

        // keep in bounds
        if (next.x < 0 || next.y < 0 || next.x >= GRID_WIDTH || next.y >= GRID_HEIGHT) {
            return; // can't move outside bounds
        }

        // check collision with blocks
        if (BlockManager.isBlocked(next)) {
            return; // blocked by a wall
        }

        // safe to move
        pos = next;
    }

    public void setPosition(int x, int y) {
        this.pos.setLocation(x, y);
    }

    // ✅ New method for drawing with offset
    public void drawAtOffset(Graphics g, JPanel panel, int tileSize, int offsetX, int offsetY) {
        if (image != null) {
            g.drawImage(image, pos.x * tileSize + offsetX, pos.y * tileSize + offsetY, tileSize - 2 * offsetX,
                    tileSize - 2 * offsetY, panel);
        } else {
            // fallback rectangle if image is missing
            g.fillRect(pos.x * tileSize + offsetX, pos.y * tileSize + offsetY, tileSize - 2 * offsetX,
                    tileSize - 2 * offsetY);
        }
    }

    // inside MovingObject.java or GameObject.java
    public void draw(Graphics g, JPanel panel, int tileSize) {
        // Calls the offset method with zero offset for standard drawing
        drawAtOffset(g, panel, tileSize, 0, 0);
    }
}
