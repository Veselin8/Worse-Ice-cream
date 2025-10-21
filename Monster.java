public abstract class Monster extends MovingObject {

    public Monster(int x, int y, String imagePath) {
        super(x, y, imagePath);
    }

    // Each monster defines its own movement logic
    protected abstract void step(int gridWidth, int gridHeight);

    @Override
    public void move(int gridWidth, int gridHeight) {
        // speed is already handled in MovingObject
        super.move(gridWidth, gridHeight);
        step(gridWidth, gridHeight); // additional logic like reversing direction
    }
}

