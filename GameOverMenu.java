import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameOverMenu {

    public static void show(JFrame parent, String message) {
        JDialog dialog = new JDialog(parent, "Game Over", true);
        dialog.setSize(320, 160);
        dialog.setLayout(new BorderLayout());

        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        dialog.add(label, BorderLayout.CENTER);

        JButton restartButton = new JButton("Restart");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                if (parent instanceof MyFrame) {
                    ((MyFrame) parent).restartGame();
                }
            }
        });

        dialog.add(restartButton, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}
