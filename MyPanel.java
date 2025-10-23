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
    private static final int PANEL_SIZE = TILE_SIZE * GRID_HEIGHT + TILE_SIZE * HUD_HEIGHT;

    private Timer gameTimer;
    private Timer countdownTimer;
    private int timeRemaining;

    private Player player;
    private List<Monster> monsters;
    private List<Block> blocks;
    private CoinManager coinManager;
    private CollisionManager collisionManager;

    private boolean upPressed, downPressed, leftPressed, rightPressed;
    private boolean gameOver = false;
    private boolean paused = false;
    private boolean buildMode = false;

    public MyPanel() {
        setPreferredSize(new Dimension(TILE_SIZE * GRID_WIDTH, PANEL_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        initializeGame();

        gameTimer = new Timer(50, this);
        gameTimer.start();

        countdownTimer = new Timer(1000, e -> updateCountdown());
        countdownTimer.start();
    }

    private void initializeGame() {
        BlockManager.clear();
        blocks = new ArrayList<>();
        coinManager = new CoinManager();

        // --- Player ---
        player = new Player(GRID_WIDTH / 2, GRID_HEIGHT / 2);
        player.setDirection(0, 0);
        player.setSpeed(3); // Moves every tick (adjustable)

        // --- Monsters ---
        monsters = new ArrayList<>();
        Monster1 m1 = new Monster1(8, 0);  
        Monster2 m2 = new Monster2(13, 15); 
        m1.setSpeed(2); // moves every 2 ticks
        m2.setSpeed(5); // moves every tick
        monsters.add(m1);
        monsters.add(m2);

        // --- Blocks ---
        addBlock(12, 10);
        addBlock(10, 11);
        addBlock(10, 12);
        addBlock(10, 13);
        addBlock(9, 10);

        // --- Coins ---
        coinManager.addCoin(2, 2);
        coinManager.addCoin(5, 4);
        coinManager.addCoin(10, 18);

        // --- Collision Manager ---
        collisionManager = new CollisionManager(player, monsters, blocks, coinManager);

        // --- Game state ---
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
        if (gameOver || paused) return;

        coinManager.update(player);

        // --- Win condition ---
        if (coinManager.getRemaining() == 0) {
            gameOver = true;
            gameTimer.stop();
            countdownTimer.stop();
            repaint();
            showVictoryDialog();
            return;
        }

        if (!buildMode) updatePlayerDirection();
        else player.setDirection(0, 0);

        // --- Move player ---
        if (player.getDx() != 0 || player.getDy() != 0) {
            player.move(GRID_WIDTH, GRID_HEIGHT);
        }

        // --- Move all monsters ---
        for (Monster monster : monsters) {
            monster.move(GRID_WIDTH, GRID_HEIGHT);
        }

        // --- Handle collisions ---
        // CHANGED: Now expects Monster object if caught, or null.
        Monster caughtMonster = collisionManager.handleCollisions();
        
        if (caughtMonster != null) {
            Point finalCollisionTile = collisionManager.getCollisionPoint();

            // *** CRITICAL CHANGE: Force player and monster to the same tile ***
            // This handles both standard collision and the "crossing paths" scenario
            if (finalCollisionTile != null) {
                player.setPosition(finalCollisionTile.x, finalCollisionTile.y);
                caughtMonster.setPosition(finalCollisionTile.x, finalCollisionTile.y);
            }

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

    // --- Dialogs ---
    private void showGameOverDialog() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parent != null) GameOverMenu.show(parent, "Game Over! You were caught by a monster!");
    }

    private void showTimeUpDialog() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parent != null) GameOverMenu.show(parent, "Time’s Up! You ran out of time!");
    }

    private void showVictoryDialog() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parent != null) VictoryMenu.show(parent);
    }

    private void showPauseMenu() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parent != null) PauseMenu.show(parent, this);
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

    // --- Grid drawing ---
    private void drawGrid(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        int widthPx = TILE_SIZE * GRID_WIDTH;
        int heightPx = TILE_SIZE * GRID_HEIGHT;

        for (int i = 0; i <= GRID_WIDTH; i++) g.drawLine(i * TILE_SIZE, 0, i * TILE_SIZE, heightPx);
        for (int i = 0; i <= GRID_HEIGHT; i++) g.drawLine(0, i * TILE_SIZE, widthPx, i * TILE_SIZE);
    }

    private void placeOrRemoveBlock(int dx, int dy) {
        int targetX = player.getPos().x + dx;
        int targetY = player.getPos().y + dy;

        if (targetX < 0 || targetY < 0 || targetX >= GRID_WIDTH || targetY >= GRID_HEIGHT) return;

        boolean added = BlockManager.toggleBlockAt(targetX, targetY);
        if (added) blocks.add(new Block(targetX, targetY));
        else blocks.removeIf(b -> b.getPos().x == targetX && b.getPos().y == targetY);

        buildMode = false;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);

        // --- Draw blocks, coins, monsters, player ---
        for (Block block : blocks) block.draw(g, this, TILE_SIZE);
        coinManager.draw(g, this, TILE_SIZE);
        for (Monster monster : monsters) monster.draw(g, this, TILE_SIZE);
        player.draw(g, this, TILE_SIZE);

        // --- HUD ---
        g.setColor(Color.GRAY.darker());
        g.fillRect(0, GRID_HEIGHT * TILE_SIZE, TILE_SIZE * GRID_WIDTH, TILE_SIZE * HUD_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        String coinText = "Coins: " + coinManager.getCollected();
        String timeText = String.format("Time: %02d:%02d", timeRemaining / 60, timeRemaining % 60);
        FontMetrics fm = g.getFontMetrics();
        int textY = GRID_HEIGHT * TILE_SIZE + (TILE_SIZE + fm.getAscent()) / 2 - 5;
        g.drawString(coinText, 10, textY);
        g.drawString(timeText, TILE_SIZE * GRID_WIDTH - fm.stringWidth(timeText) - 10, textY);

        // --- Build overlay ---
        if (buildMode) {
            g.setColor(new Color(0, 0, 255, 100));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            String text = "BUILD MODE";
            int textWidth = g.getFontMetrics().stringWidth(text);
            g.drawString(text, (getWidth() - textWidth) / 2, getHeight() / 2);
        }

        // --- Pause overlay ---
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

    // --- Key handling ---
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_ESCAPE) { pauseGame(); return; }
        if (gameOver || paused) return;

        if (code == KeyEvent.VK_SPACE) { buildMode = !buildMode; repaint(); return; }

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
