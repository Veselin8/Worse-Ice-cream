import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartMenu extends JPanel {

    public StartMenu(MyFrame frame) {
        setPreferredSize(new Dimension(600, 650)); // Match your game window size
        setBackground(Color.BLACK);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel title = new JLabel("WORSE ICECREAM", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.WHITE);

        gbc.gridy = 0;
        add(title, gbc);

        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        startButton.setFocusPainted(false);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.startGame();
            }
        });

        gbc.gridy = 1;
        add(startButton, gbc);

        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 20));
        exitButton.setFocusPainted(false);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        gbc.gridy = 2;
        add(exitButton, gbc);

        JLabel instructions = new JLabel("Collect fruit, avoid the monster and be fast!", SwingConstants.CENTER);
        instructions.setFont(new Font("Ariel", Font.ITALIC, 18));

        gbc.gridy = 3;
        add(instructions, gbc);

        JLabel text1 = new JLabel("Player movement: UP/DOWN/LEFT/RIGHT arrows", SwingConstants.CENTER);
        text1.setFont(new Font("Ariel", Font.PLAIN, 14));

        gbc.gridy = 4;
        add(text1, gbc);

        JLabel text2 = new JLabel("Ice blocks: SPACE + UP/DOWN/LEFT/RIGHT for creating/destroying ice", SwingConstants.CENTER);
        text2.setFont(new Font("Ariel", Font.PLAIN, 14));

        gbc.gridy = 5;
        add(text2, gbc);

        JLabel text3 = new JLabel("Pause: ESC", SwingConstants.CENTER);
        text3.setFont(new Font("Ariel", Font.PLAIN, 14));

        gbc.gridy = 6;
        add(text3, gbc);
    }
}
