import javax.swing.JFrame;
import java.awt.Dimension;

public class MyFrame extends JFrame {
    private MyPanel myPanel; // Keep a reference to the panel

    public MyFrame() {
        setTitle("worse icecream");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        myPanel = new MyPanel(); // Create and store the panel
        add(myPanel); 

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // New method to restart the game
    public void restartGame() {
        myPanel.restartGame();
    }
}

