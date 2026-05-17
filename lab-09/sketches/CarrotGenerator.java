import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class CarrotGenerator extends JPanel implements ActionListener {
    private final int WIDTH = 800, HEIGHT = 600;
    private java.util.List<Carrot> carrots;
    private javax.swing.Timer timer;
    private Random rand = new Random();

    public CarrotGenerator() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.GREEN);
        carrots = new ArrayList<>();
        // Timer fires every 2000 ms (2 seconds) to add a new carrot.
        timer = new javax.swing.Timer(2000, this);
        timer.start();
    }

    // Every 2 seconds, generate a new carrot at a random position.
    public void actionPerformed(ActionEvent e) {
        int x = rand.nextInt(WIDTH - 10);
        int y = rand.nextInt(HEIGHT - 10);
        carrots.add(new Carrot(x, y));
        repaint();
    }

    // Draw all carrots as orange squares.
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.ORANGE);
        for (Carrot c : carrots) {
            g.fillRect(c.x, c.y, c.size, c.size);
        }
    }

    // Simple Carrot class.
    class Carrot {
        int x, y;
        int size = 10;
        public Carrot(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Carrot Generator");
        CarrotGenerator panel = new CarrotGenerator();
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

