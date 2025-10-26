import java.awt.Point;
import java.util.List;

public class CollisionManager {

    private List<Block> blocks;
    private CoinManager coinManager;
    private Player player;
    private List<Monster> monsters;
    
    // NEW FIELD: Stores the tile where the collision occurred (null if no collision)
    private Point collisionPoint = null; 

    public CollisionManager(Player player, List<Monster> monsters, List<Block> blocks, CoinManager coinManager) {
        this.player = player;
        this.monsters = monsters;
        this.blocks = blocks;
        this.coinManager = coinManager;
    }

    /**
     * Handles all collisions in a single call.
     * Returns the Monster if the player was caught, otherwise returns null.
     */
    public Monster handleCollisions() { // CHANGED RETURN TYPE: Now returns the colliding Monster
        handleBlockCollision();
        coinManager.update(player);

        return checkPlayerMonsterCollision();
    }
    
    /** NEW: Getter for the collision tile, used by MyPanel to adjust drawing. */
    public Point getCollisionPoint() {
        return collisionPoint;
    }

    /**
     * Prevents player from moving into a block.
     */
    private void handleBlockCollision() {
        Point nextPos = new Point(player.getPos().x + player.getDx(),
                                     player.getPos().y + player.getDy());

        for (Block block : blocks) {
            if (block.getPos().equals(nextPos)) {
                // Player would collide; stop movement
                player.setDirection(0, 0);
                return;
            }
        }
    }

    /**
     * Checks if player and any monster occupy the same grid cell OR have swapped cells.
     * Returns the colliding Monster object, or null if no collision.
     */
    private Monster checkPlayerMonsterCollision() { // CHANGED RETURN TYPE: Monster
        collisionPoint = null; // Reset collision point before check
        
        Point playerCurrentPos = player.getPos();
        Point playerPreviousPos = player.getPreviousPos(); // From Modified MovingObject

        for (Monster monster : monsters) {
            Point monsterCurrentPos = monster.getPos();
            Point monsterPreviousPos = monster.getPreviousPos(); // From Modified MovingObject

            // 1. Standard Collision: Check if they are on the same tile now
            if (playerCurrentPos.equals(monsterCurrentPos)) {
                collisionPoint = playerCurrentPos; // Collision occurred at final tile
                return monster;
            }

            // 2. Tunneling Collision: Check if they swapped positions (head-on collision)
            // This is the case where they are now on two different tiles, but crossed.
            if (playerCurrentPos.equals(monsterPreviousPos) &&
                monsterCurrentPos.equals(playerPreviousPos)) {
                
                // Set the collision point to where the player ended up (Monster's original spot).
                // This is the visually appealing tile for the "catch" animation.
                collisionPoint = playerCurrentPos; 
                return monster;
            }
        }
        return null; // No collision occurred
    }

    /**
     * Optional: checks if the player is adjacent to any monster (Manhattan distance = 1)
     */
    public boolean isPlayerNearMonster() {
        for (Monster monster : monsters) {
            int dx = Math.abs(player.getPos().x - monster.getPos().x);
            int dy = Math.abs(player.getPos().y - monster.getPos().y);
            if (dx + dy == 1) return true;
        }
        return false;
    }
}
