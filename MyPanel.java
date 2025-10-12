import javax.swing.*; // JPanel, Timer
import java.awt.*; // Graphics, Color, Dimension, Font, ImageObserver, Point
import java.awt.event.*; // ActionListener, KeyListener, KeyEvent
import java.util.List; // List interface
import java.util.ArrayList; // ArrayList implementation

public class MyPanel extends JPanel implements ActionListener, KeyListener {

    private static final int TILE_SIZE = 30;
    private static final int GRID_WIDTH = 20;
    private static final int GRID_HEIGHT = 20;
    private static final int PANEL_SIZE = TILE_SIZE * GRID_WIDTH;

    private Timer timer;
    private Player player;
    private List<Block> blocks;
    private CoinManager coinManager; // <-- NEW

    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;

    private int dx = 0;
    private int dy = 0;

    public MyPanel() {
        setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        BlockManager.clear();
        blocks = new ArrayList<>();
        coinManager = new CoinManager(); // initialize coin manager

        player = new Player(GRID_WIDTH / 2, GRID_HEIGHT / 2);
        player.setDirection(0, 0);

        // Add blocks
        addBlock(5, 5);
        addBlock(5, 6);
        addBlock(5, 7);
        addBlock(10, 10);
        addBlock(11, 10);

        // Add coins
        coinManager.addCoin(2, 2);
        coinManager.addCoin(5, 4);
        coinManager.addCoin(11, 10);

        timer = new Timer(150, this);
        timer.start();
    }

    private void addBlock(int x, int y) {
        Block block = new Block(x, y);
        blocks.add(block);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }

    private void update() {
        // Determine movement direction
        if (upPressed) {
            dx = 0;
            dy = -1;
        } else if (downPressed) {
            dx = 0;
            dy = 1;
        } else if (leftPressed) {
            dx = -1;
            dy = 0;
        } else if (rightPressed) {
            dx = 1;
            dy = 0;
        } else {
            dx = 0;
            dy = 0;
        }

        if (dx != 0 || dy != 0) {
            player.setDirection(dx, dy);
            player.move(GRID_WIDTH, GRID_HEIGHT); // already checks bounds & blocks
        }

        coinManager.update(player); // <-- check for collection
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        for (int i = 0; i <= GRID_WIDTH; i++) {
            g.drawLine(i * TILE_SIZE, 0, i * TILE_SIZE, PANEL_SIZE);
        }
        for (int i = 0; i <= GRID_HEIGHT; i++) {
            g.drawLine(0, i * TILE_SIZE, PANEL_SIZE, i * TILE_SIZE);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);

        // Draw blocks first
        for (Block block : blocks) {
            block.draw(g, this, TILE_SIZE);
        }

        // Draw coins on top of blocks
        coinManager.draw(g, this, TILE_SIZE);

        // Draw player
        player.draw(g, this, TILE_SIZE);

        // Draw coin counter
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Coins: " + coinManager.getCollected(), 10, 20);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> upPressed = true;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> downPressed = true;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> leftPressed = true;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> rightPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> upPressed = false;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> downPressed = false;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> leftPressed = false;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> rightPressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
