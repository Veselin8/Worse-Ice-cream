import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class MyPanel extends JPanel implements ActionListener, KeyListener {

    private static final int TILE_SIZE = 30;
    private static final int GRID_WIDTH = 20;
    private static final int GRID_HEIGHT = 20;
    private static final int HUD_HEIGHT = 1;
    private static final int TOTAL_HEIGHT = GRID_HEIGHT + HUD_HEIGHT;
    // PANEL_SIZE is now correctly calculated as the grid + HUD
    private static final int PANEL_SIZE = TILE_SIZE * GRID_HEIGHT + TILE_SIZE * HUD_HEIGHT;

    private Timer gameTimer;
    private Timer countdownTimer;
    private int timeRemaining;

    private Player player;
    private Monster1 monster;
    private List<Block> blocks;
    private CoinManager coinManager;
    private CollisionManager collisionManager;

    private boolean upPressed, downPressed, leftPressed, rightPressed;
    private boolean gameOver = false;
    private boolean paused = false;
    private boolean buildMode = false; // build mode flag

    public MyPanel() {
        // The preferred size should use the full panel size (Grid + HUD)
        setPreferredSize(new Dimension(TILE_SIZE * GRID_WIDTH, PANEL_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        initializeGame();

        gameTimer = new Timer(150, this);
        gameTimer.start();

        countdownTimer = new Timer(1000, e -> updateCountdown());
        countdownTimer.start();
    }

    private void initializeGame() {
        BlockManager.clear();
        blocks = new ArrayList<>();
        coinManager = new CoinManager();

        player = new Player(GRID_WIDTH / 2, GRID_HEIGHT / 2);
        player.setDirection(0, 0);

        monster = new Monster1(8, 0);

        addBlock(12, 10);
        addBlock(10, 11);
        addBlock(10, 12);
        addBlock(10, 13);
        addBlock(9, 10);

        coinManager.addCoin(2, 2);
        coinManager.addCoin(5, 4);
        coinManager.addCoin(10, 18);

        collisionManager = new CollisionManager(player, monster, blocks, coinManager);
        gameOver = false;
        paused = false;
        buildMode = false;
        upPressed = downPressed = leftPressed = rightPressed = false;
        timeRemaining = 120;
    }

    public void restartGame() {
        gameTimer.stop();
        countdownTimer.stop();
        initializeGame();
        requestFocusInWindow();
        gameTimer.start();
        countdownTimer.start();
        repaint();
    }

    private void updateCountdown() {
        if (gameOver || paused) return;

        timeRemaining--;
        if (timeRemaining <= 0) {
            timeRemaining = 0;
            gameOver = true;
            gameTimer.stop();
            countdownTimer.stop();
            repaint();
            showTimeUpDialog();
        }
        repaint();
    }

    private void addBlock(int x, int y) {
        Block block = new Block(x, y);
        blocks.add(block);
        BlockManager.addBlock(block);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }

    private void update() {
        if (gameOver || paused)
            return;

        // Update coins collected
        coinManager.update(player);

        // Check win condition
        if (coinManager.getRemaining() == 0) {
            gameOver = true;
            gameTimer.stop();
            countdownTimer.stop();
            repaint();
            showVictoryDialog();
            return;
        }

        if (!buildMode) {
            updatePlayerDirection();
        } else {
            player.setDirection(0, 0);
        }

        if (player.getDx() != 0 || player.getDy() != 0) {
            player.move(GRID_WIDTH, GRID_HEIGHT);
        }

        monster.move(GRID_WIDTH, GRID_HEIGHT);

        if (collisionManager.handleCollisions()) {
            gameOver = true;
            gameTimer.stop();
            countdownTimer.stop();
            repaint();
            showGameOverDialog();
        }
    }

    private void updatePlayerDirection() {
        int dx = 0, dy = 0;
        if (upPressed) dy = -1;
        else if (downPressed) dy = 1;

        if (dy == 0) {
            if (leftPressed) dx = -1;
            else if (rightPressed) dx = 1;
        }
        player.setDirection(dx, dy);
    }

    private void showGameOverDialog() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame != null) {
            GameOverMenu.show(parentFrame, "Game Over! You were caught by a monster!");
        }
    }

    private void showTimeUpDialog() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame != null) {
            GameOverMenu.show(parentFrame, "Time’s Up! You ran out of time!");
        }
    }

    private void showVictoryDialog() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame != null) {
            VictoryMenu.show(parentFrame);
        }
    }

    private void showPauseMenu() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame != null) {
            PauseMenu.show(parentFrame, this);
        }
    }

    public void pauseGame() {
        if (paused || gameOver) return;
        paused = true;
        gameTimer.stop();
        countdownTimer.stop();
        repaint();
        showPauseMenu();
    }

    public void resumeGame() {
        if (!paused || gameOver) return;
        paused = false;
        gameTimer.start();
        countdownTimer.start();
        requestFocusInWindow();
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.DARK_GRAY);

        int gridPixelWidth = TILE_SIZE * GRID_WIDTH;
        int gridPixelHeight = TILE_SIZE * GRID_HEIGHT; // This is the bottom of the game area

        // Draw vertical lines, stopping at the bottom of the game grid
        for (int i = 0; i <= GRID_WIDTH; i++)
            g.drawLine(i * TILE_SIZE, 0, i * TILE_SIZE, gridPixelHeight);

        // Draw horizontal lines, stopping at the bottom of the game grid (i <= GRID_HEIGHT)
        // This draws the 21 lines needed (0 to 20) to create 20 rows.
        for (int i = 0; i <= GRID_HEIGHT; i++)
            g.drawLine(0, i * TILE_SIZE, gridPixelWidth, i * TILE_SIZE);
    }

    private void placeOrRemoveBlock(int dx, int dy) {
        int targetX = player.getPos().x + dx;
        int targetY = player.getPos().y + dy;

        if (targetX < 0 || targetY < 0 || targetX >= GRID_WIDTH || targetY >= GRID_HEIGHT)
            return;

        boolean added = BlockManager.toggleBlockAt(targetX, targetY);
        if (added) blocks.add(new Block(targetX, targetY));
        else blocks.removeIf(b -> b.getPos().x == targetX && b.getPos().y == targetY);

        buildMode = false; // exit build mode
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g); // This now only draws the game grid

        // Draw game elements
        for (Block block : blocks) block.draw(g, this, TILE_SIZE);
        coinManager.draw(g, this, TILE_SIZE);
        monster.draw(g, this, TILE_SIZE);
        player.draw(g, this, TILE_SIZE);

        // Draw the HUD background
        g.setColor(Color.GRAY.darker());
        g.fillRect(0, GRID_HEIGHT * TILE_SIZE, TILE_SIZE * GRID_WIDTH, TILE_SIZE * HUD_HEIGHT); // Use HUD_HEIGHT

        // Draw HUD text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        String coinText = "Coins: " + coinManager.getCollected();
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        String timeText = String.format("Time: %02d:%02d", minutes, seconds);

        FontMetrics fm = g.getFontMetrics();
        // Correctly center text in the HUD row
        int textY = GRID_HEIGHT * TILE_SIZE + (TILE_SIZE + fm.getAscent()) / 2 - 5;
        g.drawString(coinText, 10, textY);
        g.drawString(timeText, TILE_SIZE * GRID_WIDTH - fm.stringWidth(timeText) - 10, textY);

        // --- Overlays ---
        if (buildMode) {
            g.setColor(new Color(0, 0, 255, 100));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            String text = "BUILD MODE";
            int textWidth = g.getFontMetrics().stringWidth(text);
            g.drawString(text, (getWidth() - textWidth) / 2, getHeight() / 2);
        }

        if (paused) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            String text = "PAUSED";
            int textWidth = g.getFontMetrics().stringWidth(text);
            g.drawString(text, (getWidth() - textWidth) / 2, getHeight() / 2);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_ESCAPE) {
            pauseGame();
            return;
        }

        if (gameOver || paused)
            return;

        if (code == KeyEvent.VK_SPACE) {
            buildMode = !buildMode;
            repaint();
            return;
        }

        if (buildMode) {
            switch (code) {
                case KeyEvent.VK_W: case KeyEvent.VK_UP: placeOrRemoveBlock(0, -1); break;
                case KeyEvent.VK_S: case KeyEvent.VK_DOWN: placeOrRemoveBlock(0, 1); break;
                case KeyEvent.VK_A: case KeyEvent.VK_LEFT: placeOrRemoveBlock(-1, 0); break;
                case KeyEvent.VK_D: case KeyEvent.VK_RIGHT: placeOrRemoveBlock(1, 0); break;
            }
            return;
        }

        switch (code) {
            case KeyEvent.VK_W: case KeyEvent.VK_UP: upPressed = true; break;
            case KeyEvent.VK_S: case KeyEvent.VK_DOWN: downPressed = true; break;
            case KeyEvent.VK_A: case KeyEvent.VK_LEFT: leftPressed = true; break;
            case KeyEvent.VK_D: case KeyEvent.VK_RIGHT: rightPressed = true; break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver || paused || buildMode) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: case KeyEvent.VK_UP: upPressed = false; break;
            case KeyEvent.VK_S: case KeyEvent.VK_DOWN: downPressed = false; break;
            case KeyEvent.VK_A: case KeyEvent.VK_LEFT: leftPressed = false; break;
            case KeyEvent.VK_D: case KeyEvent.VK_RIGHT: rightPressed = false; break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
