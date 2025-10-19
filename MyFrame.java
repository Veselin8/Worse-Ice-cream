import javax.swing.*;

public class MyFrame extends JFrame {
    private MyPanel myPanel;

    public MyFrame() {
        setTitle("worse icecream");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Start with the Start Menu
        showStartMenu();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void showStartMenu() {
        StartMenu startMenu = new StartMenu(this);
        setContentPane(startMenu);
    }

    // Called by StartMenu when the game begins
    public void startGame() {
        myPanel = new MyPanel();
        setContentPane(myPanel);
        revalidate();
        myPanel.requestFocusInWindow();
    }

    // Called by GameOverMenu or PauseMenu when "Restart" is pressed
    public void restartGame() {
        myPanel.restartGame();
    }
}
