import javax.swing.*;
//import java.awt.Color;

public class MyFrame extends JFrame {
    public MyFrame() {
        setTitle("worse icecream");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        add(new MyPanel());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

