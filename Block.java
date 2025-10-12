public class Block extends GameObject {
    public Block(int x, int y) {
        super(x, y, "ice.png");
        BlockManager.addBlock(this); // automatically register block
    }
}
