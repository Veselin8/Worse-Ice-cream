import java.awt.*;

public class Monster2 extends Monster {

    private int direction = 1; // 1 = right, -1 = left

    public Monster2(int x, int y) {
        super(x, y, "monster.png");
        setSpeed(1); // moves every tick
        setDirection(direction, 0);
    }

    @Override
    protected void step(int gridWidth, int gridHeight) {
        Point next = new Point(pos.x + direction, pos.y);

        // reverse if out of bounds or blocked
        if (next.x < 0 || next.x >= gridWidth || BlockManager.isBlocked(next)) {
            direction *= -1;
            setDirection(direction, 0);
        }
    }
}


