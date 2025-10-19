import java.awt.Point;

public class Monster1 extends MovingObject {

    private int direction = 1; // 1 = down, -1 = up

    public Monster1(int x, int y) {
        super(x, y, "monster.png");
        this.dx = 0;
        this.dy = direction; // start moving down
    }

    // Call this every tick
    public void update(int gridHeight) {
        Point next = new Point(pos.x, pos.y + direction);

        // If the next cell is blocked or out of bounds, reverse direction
        if (next.y < 0 || next.y >= gridHeight || BlockManager.isBlocked(next)) {
            direction *= -1;
            next.y = pos.y + direction;

            // Check again in case both sides are blocked
            if (next.y < 0 || next.y >= gridHeight || BlockManager.isBlocked(next)) {
                return;
            }
        }

        pos = next;
    }


    // Override move to ignore blocks for vertical movement if you want
    @Override
    public void move(int gridWidth, int gridHeight) {
        update(gridHeight);
    }
}
