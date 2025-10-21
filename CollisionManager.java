import java.awt.Point;
import java.util.List;

public class CollisionManager {

    private List<Block> blocks;
    private CoinManager coinManager;
    private Player player;
    private List<Monster> monsters;

    public CollisionManager(Player player, List<Monster> monsters, List<Block> blocks, CoinManager coinManager) {
        this.player = player;
        this.monsters = monsters;
        this.blocks = blocks;
        this.coinManager = coinManager;
    }

    /**
     * Handles all collisions in a single call.
     * Returns true if the game should end (player caught by any monster).
     */
    public boolean handleCollisions() {
        handleBlockCollision();
        coinManager.update(player);

        return checkPlayerMonsterCollision();
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
     * Checks if player and any monster occupy the same grid cell.
     */
    private boolean checkPlayerMonsterCollision() {
        for (Monster monster : monsters) {
            if (player.getPos().equals(monster.getPos())) {
                return true; // player caught
            }
        }
        return false;
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

