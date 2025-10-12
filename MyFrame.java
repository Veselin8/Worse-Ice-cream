import javax.swing.*;
import java.awt.Color;

/*public class MyFrame extends JFrame{
    MyFrame(){
        
        this.setTitle("Worse Icecream");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setSize(420,420);
        this.setVisible(true);

        ImageIcon image = new ImageIcon("Ico_badicecream.webp");
        this.setIconImage(image.getImage());
        this.getContentPane().setBackground(new Color(110, 113, 117)); 
    }
}*/

import javax.swing.*;

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

