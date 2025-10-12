import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameOverMenu {

    public static void show(JFrame parent) {
        JDialog dialog = new JDialog(parent, "Game Over", true);
        dialog.setSize(300, 150);
        dialog.setLayout(new BorderLayout());

        JLabel label = new JLabel("Game Over! You were caught by a monster!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        dialog.add(label, BorderLayout.CENTER);

        JButton restartButton = new JButton("Restart");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose(); // Close the "Game Over" dialog

                // Check if the parent is an instance of MyFrame
                if (parent instanceof MyFrame) {
                    // Cast and call the new restart method
                    ((MyFrame) parent).restartGame();
                }
            }
        });

        dialog.add(restartButton, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}