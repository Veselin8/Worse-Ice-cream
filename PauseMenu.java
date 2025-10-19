import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PauseMenu {

    public static void show(JFrame parent, MyPanel panel) {
        JDialog dialog = new JDialog(parent, "Paused", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new GridLayout(3, 1, 10, 10));

        JLabel label = new JLabel("Game Paused", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        dialog.add(label);

        JButton resumeButton = new JButton("Resume");
        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                panel.resumeGame();
            }
        });
        dialog.add(resumeButton);

        JButton restartButton = new JButton("Restart");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                panel.restartGame();
            }
        });
        dialog.add(restartButton);

        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}
