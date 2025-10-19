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
    private static final int PANEL_SIZE = TILE_SIZE * TOTAL_HEIGHT;

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

    private int tickCounter = 0; // Counts how many ticks have passed

    public MyPanel() {
        setPreferredSize(new Dimension(TILE_SIZE * GRID_WIDTH, PANEL_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        initializeGame();

        // Game loop timer — 5× faster (was 150ms → now 30ms)
        gameTimer = new Timer(30, this);
        gameTimer.start();

        // Countdown timer (still 1 second per tick)
        countdownTimer = new Timer(1000, e -> updateCountdown());
        countdownTimer.start();
    }

    private void initializeGame() {
        blocks = new ArrayList<>();
        coinManager = new CoinManager();

        player = new Player(GRID_WIDTH / 2, GRID_HEIGHT / 2);
        player.setDirection(0, 0);

        monster = new Monster1(8, 0);

        // Example blocks
        addBlock(12, 10);
        addBlock(10, 11);
        addBlock(10, 12);
        addBlock(10, 13);
        addBlock(9, 10);

        // Example coins
        coinManager.addCoin(2, 2);
        coinManager.addCoin(5, 4);
        coinManager.addCoin(10, 18);

        collisionManager = new CollisionManager(player, monster, blocks, coinManager);
        gameOver = false;
        paused = false;
        upPressed = downPressed = leftPressed = rightPressed = false;

        timeRemaining = 120;
        tickCounter = 0;
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
        blocks.add(new Block(x, y));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }

    private void update() {
        if (gameOver || paused)
            return;

        tickCounter++;

        // Update direction each tick for responsiveness
        updatePlayerDirection();

        // Move player every 5 ticks
        if (tickCounter % 5 == 0 && (player.getDx() != 0 || player.getDy() != 0)) {
            player.move(GRID_WIDTH, GRID_HEIGHT);
        }

        // Move monster every 4 ticks
        if (tickCounter % 4 == 0) {
            monster.move(GRID_WIDTH, GRID_HEIGHT);
        }

        // Handle collisions
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
        for (int i = 0; i <= GRID_WIDTH; i++) {
            g.drawLine(i * TILE_SIZE, 0, i * TILE_SIZE, PANEL_SIZE);
        }
        for (int i = 0; i <= TOTAL_HEIGHT; i++) {
            g.drawLine(0, i * TILE_SIZE, TILE_SIZE * GRID_WIDTH, i * TILE_SIZE);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);

        for (Block block : blocks)
            block.draw(g, this, TILE_SIZE);
        coinManager.draw(g, this, TILE_SIZE);
        monster.draw(g, this, TILE_SIZE);
        player.draw(g, this, TILE_SIZE);

        // HUD background
        g.setColor(Color.GRAY.darker());
        g.fillRect(0, GRID_HEIGHT * TILE_SIZE, TILE_SIZE * GRID_WIDTH, TILE_SIZE);

        // HUD text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));

        String coinText = "Coins: " + coinManager.getCollected();
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        String timeText = String.format("Time: %02d:%02d", minutes, seconds);

        FontMetrics fm = g.getFontMetrics();
        g.drawString(coinText, 10, GRID_HEIGHT * TILE_SIZE + (TILE_SIZE + fm.getAscent()) / 2 - 5);
        g.drawString(timeText, TILE_SIZE * GRID_WIDTH - fm.stringWidth(timeText) - 10,
                     GRID_HEIGHT * TILE_SIZE + (TILE_SIZE + fm.getAscent()) / 2 - 5);

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
        if (gameOver || paused)
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
    public void keyTyped(KeyEvent e) {}
}
