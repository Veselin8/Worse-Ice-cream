import java.awt.Point;
import java.util.List;

public class CollisionManager {

    private List<Block> blocks;
    private CoinManager coinManager;
    private Player player;
    private Monster1 monster;

    public CollisionManager(Player player, Monster1 monster, List<Block> blocks, CoinManager coinManager) {
        this.player = player;
        this.monster = monster;
        this.blocks = blocks;
        this.coinManager = coinManager;
    }

    /** 
     * Handles all collisions in a single call.
     * Returns true if the game should end (player caught).
     */
    public boolean handleCollisions() {
        handleBlockCollision();
        coinManager.update(player);

        // Return true if player and monster overlap
        return checkPlayerMonsterCollision();
    }

    /**
     * Prevents player from moving into a block.
     */
    private void handleBlockCollision() {
        Point nextPos = new Point(
            player.getPos().x + player.getDx(),
            player.getPos().y + player.getDy()
        );

        for (Block block : blocks) {
            if (block.getPos().equals(nextPos)) {
                // Player would collide; stop movement
                player.setDirection(0, 0);
                return;
            }
        }
    }

    /**
     * Checks if player and monster occupy the same grid cell.
     */
    private boolean checkPlayerMonsterCollision() {
        return player.getPos().equals(monster.getPos());
    }

    /**
     * Optional: check if the player is adjacent to a monster (for warning or proximity logic)
     */
    public boolean isPlayerNearMonster() {
        int dx = Math.abs(player.getPos().x - monster.getPos().x);
        int dy = Math.abs(player.getPos().y - monster.getPos().y);
        return (dx + dy == 1); // Manhattan distance of 1 tile
    }
}
