import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MyPanel extends JPanel implements ActionListener, KeyListener {

    private static final int TILE_SIZE = 30;
    private static final int GRID_WIDTH = 20;
    private static final int GRID_HEIGHT = 20;
    private static final int PANEL_SIZE = TILE_SIZE * GRID_WIDTH;

    private Timer timer;
    private Player player; // Assuming Player class exists and has setDirection, move, keepInBounds, draw methods
    private boolean upPressed, downPressed, leftPressed, rightPressed;
    
    // Add variables to track the player's current direction
    private int dx = 0; 
    private int dy = 0; 

    public MyPanel() {
        setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        // Player starting in the middle, direction set to stationary (0, 0)
        player = new Player(GRID_WIDTH / 2, GRID_HEIGHT / 2); 
        player.setDirection(0, 0); 
        
        timer = new Timer(150, this); // update every 150 ms
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }

    private void update() {
        // 1. Determine the desired direction based on pressed keys
        // The movement logic is now in keyPressed, but we need to re-evaluate it 
        // if multiple keys are pressed/released quickly. A simpler approach for 
        // single-step movement is to just move if any key is currently pressed.
        
        // This logic is simplified for single-step movement:
        // Set direction only if a key is pressed (flags are true)
        if (upPressed) {
            dx = 0; dy = -1;
        } else if (downPressed) {
            dx = 0; dy = 1;
        } else if (leftPressed) {
            dx = -1; dy = 0;
        } else if (rightPressed) {
            dx = 1; dy = 0;
        } else {
            // No key is currently pressed for movement
            dx = 0; dy = 0; 
        }

        // 2. Only move if a movement key is pressed (dx or dy is non-zero)
        if (dx != 0 || dy != 0) {
            player.setDirection(dx, dy); // Set the calculated direction
            player.move();              // Move one tile
            player.keepInBounds(GRID_WIDTH, GRID_HEIGHT);
            
            // OPTIONAL: Reset flags after movement if you want single-tile movement 
            // per key press, but a better approach is using keyReleased to stop.
            // For continuous movement while pressed, keep the flags set.
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        // Assuming TILE_SIZE is used by Player.draw for positioning
        player.draw(g, TILE_SIZE); 
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
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        
        // When a key is pressed, set its corresponding flag to true.
        // It's usually better to NOT unset the other flags here, 
        // as you might press two keys simultaneously (though for simple 4-way, it's okay).
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
        
        // When a key is released, set its corresponding flag to false.
        switch (code) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> upPressed = false; 
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> downPressed = false; 
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> leftPressed = false; 
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> rightPressed = false; 
        }
        
        // The 'update' method (called by the Timer) will now see that 
        // all directional flags are false and set the direction to (0, 0), 
        // stopping the player on the next timer tick.
    }

    @Override
    public void keyTyped(KeyEvent e) { }
}