import java.awt.*;

public class Player {
    private int x, y;
    private int dx, dy;

    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.dx = 0;
        this.dy = 0;
    }

    public void setDirection(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public void move() {
        x += dx;
        y += dy;
    }

    public void keepInBounds(int width, int height) {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x >= width) x = width - 1;
        if (y >= height) y = height - 1;
    }

    public void draw(Graphics g, int tileSize) {
        g.setColor(Color.GREEN);
        g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
    }
}