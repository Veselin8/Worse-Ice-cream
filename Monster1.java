import java.awt.*;

public class Monster1 extends Monster {

    private int direction = 1; // 1 = down, -1 = up

    public Monster1(int x, int y) {
        super(x, y, "monster.png");
        setSpeed(2); // moves every 2 ticks
        setDirection(0, direction);
    }

    @Override
    protected void step(int gridWidth, int gridHeight) {
        Point next = new Point(pos.x, pos.y + direction);

        // reverse if out of bounds or blocked
        if (next.y < 0 || next.y >= gridHeight || BlockManager.isBlocked(next)) {
            direction *= -1;
            setDirection(0, direction);
        }
    }
}

