import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public abstract class GameObject {

    protected BufferedImage image;
    protected Point pos;

    public GameObject(int x, int y, String imagePath) {
        pos = new Point(x, y);
        loadImage(imagePath);
    }

    private void loadImage(String path) {
        try {
            image = ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("Error loading image: " + path + " - " + e.getMessage());
        }
    }

    public void draw(Graphics g, ImageObserver observer, int tileSize) {
        if (image != null) {
            g.drawImage(image, pos.x * tileSize, pos.y * tileSize, observer);
        }
    }

    public Point getPos() {
        return pos;
    }

    public void setPos(int x, int y) {
        pos.setLocation(x, y);
    }
}
