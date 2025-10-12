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
}

