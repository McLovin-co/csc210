import javax.swing.*;
import java.awt.*;

public class CircleDisplay extends JPanel {
    private final int WIDTH = 400, HEIGHT = 400;
    
    public CircleDisplay() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);
    }
    
    // Draw a blue circle centered in the panel.
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLUE);
        int diameter = 100;
        int x = (WIDTH - diameter) / 2;
        int y = (HEIGHT - diameter) / 2;
        g.fillOval(x, y, diameter, diameter);
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Circle Display");
        CircleDisplay panel = new CircleDisplay();
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

