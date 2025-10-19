import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CoinManager {

    private List<Coin> coins;
    private int collected;

    public CoinManager() {
        coins = new ArrayList<>();
        collected = 0;
    }

    public void addCoin(int x, int y) {
        coins.add(new Coin(x, y));
    }

    // Update collected coins
    public void update(Player player) {
        Iterator<Coin> it = coins.iterator();
        while (it.hasNext()) {
            Coin coin = it.next();
            if (coin.getPos().equals(player.getPos())) {
                collected++;
                it.remove();
            }
        }
    }

    public void draw(Graphics g, ImageObserver observer, int tileSize) {
        for (Coin coin : coins) {
            coin.draw(g, observer, tileSize);
        }
    }

    public int getCollected() {
        return collected;
    }

    public int getRemaining() {
        return coins.size();
    }

    public void reset() {
        collected = 0;
        coins.clear();
    }
}

