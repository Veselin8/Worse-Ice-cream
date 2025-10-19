import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

public class BlockManager {

    private static Set<Point> blockPositions = new HashSet<>();

    public static void addBlock(Block block) {
        blockPositions.add(block.getPos());
    }

    public static boolean isBlocked(Point pos) {
        return blockPositions.contains(pos);
    }

    public static void clear() {
        blockPositions.clear();
    }

    public static Set<Point> getBlocks() {
        return blockPositions;
    }
    
    public static boolean toggleBlockAt(int x, int y) {
        Point p = new Point(x, y);
        if (blockPositions.contains(p)) {
            blockPositions.remove(p);
            return false; // block removed
        } else {
            blockPositions.add(p);
            return true; // block added
        }
    }
}

