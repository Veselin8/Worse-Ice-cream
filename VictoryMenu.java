import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VictoryMenu {

    public static void show(JFrame parent) {
        JDialog dialog = new JDialog(parent, "You Win!", true);
        dialog.setSize(300, 150);
        dialog.setLayout(new BorderLayout());

        JLabel label = new JLabel("Congratulations! You collected all coins!", SwingConstants.CENTER);
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

