import java.awt.Point;

public abstract class MovingObject extends GameObject {

    protected int dx, dy;

    public MovingObject(int x, int y, String imagePath) {
        super(x, y, imagePath);
    }

    public void setDirection(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
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


}
