import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class MyPanel extends JPanel implements ActionListener, KeyListener {

    private static final int TILE_SIZE = 30;
    private static final int GRID_WIDTH = 20;
    private static final int GRID_HEIGHT = 20;
    private static final int PANEL_SIZE = TILE_SIZE * GRID_WIDTH;

    private Timer timer;
    private Player player;
    private Monster1 monster;
    private List<Block> blocks;
    private CoinManager coinManager;
    private CollisionManager collisionManager;

    private boolean upPressed, downPressed, leftPressed, rightPressed;
    private boolean gameOver = false;

    public MyPanel() {
        setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        initializeGame();

        timer = new Timer(150, this);
        timer.start();
    }

    private void initializeGame() {
        blocks = new ArrayList<>();
        coinManager = new CoinManager();

        // Assuming Player, Monster1, Block, CoinManager, and CollisionManager classes exist
        player = new Player(GRID_WIDTH / 2, GRID_HEIGHT / 2);
        player.setDirection(0, 0);

        monster = new Monster1(8, 0);

        // Add some example blocks
        addBlock(5, 5);
        addBlock(5, 6);
        addBlock(5, 7);
        addBlock(10, 10);
        addBlock(11, 10);

        // Add coins
        coinManager.addCoin(2, 2);
        coinManager.addCoin(5, 4);
        coinManager.addCoin(11, 10);

        collisionManager = new CollisionManager(player, monster, blocks, coinManager);
        gameOver = false;
        upPressed = downPressed = leftPressed = rightPressed = false;
    }

    public void restartGame() {
        timer.stop();
        initializeGame();
        requestFocusInWindow();
        timer.start();
        repaint();
    }

    private void addBlock(int x, int y) {
        blocks.add(new Block(x, y));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }

    private void update() {
        if (gameOver)
            return;

        updatePlayerDirection();

        // Move player
        if (player.getDx() != 0 || player.getDy() != 0) {
            player.move(GRID_WIDTH, GRID_HEIGHT);
        }

        // Move monster
        monster.move(GRID_WIDTH, GRID_HEIGHT);

        // Handle collisions via the manager
        if (collisionManager.handleCollisions()) {
            gameOver = true;
            timer.stop();
            // FIX: Force a repaint of the final collision state before the modal dialog blocks the thread.
            repaint(); 
            showGameOverDialog();
        }
    }

    private void updatePlayerDirection() {
        int dx = 0, dy = 0;
        // Prioritize vertical over horizontal if both are pressed (or based on press order)
        if (upPressed) {
            dy = -1;
        } else if (downPressed) {
            dy = 1;
        }

        // Prioritize horizontal if no vertical movement is selected, or allow for diagonal
        // Based on the original structure, it seems like only one direction (dx or dy) should be non-zero at a time
        if (dy == 0) {
            if (leftPressed) {
                dx = -1;
            } else if (rightPressed) {
                dx = 1;
            }
        }
        player.setDirection(dx, dy);
    }

    private void showGameOverDialog() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame != null) {
            // FIX: Ensure this line is NOT commented out to show the restart menu.
            GameOverMenu.show(parentFrame);
        }
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

        // Assuming Block, CoinManager, Monster1, and Player have a draw method
        for (Block block : blocks)
            block.draw(g, this, TILE_SIZE);
        coinManager.draw(g, this, TILE_SIZE);
        monster.draw(g, this, TILE_SIZE);
        player.draw(g, this, TILE_SIZE);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Coins: " + coinManager.getCollected(), 10, 20);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver)
            return;

        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                upPressed = true;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                downPressed = true;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                leftPressed = true;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                rightPressed = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver)
            return;

        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                upPressed = false;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                downPressed = false;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                leftPressed = false;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                rightPressed = false;
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }
}