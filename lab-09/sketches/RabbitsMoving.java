import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class RabbitsMoving extends JPanel implements ActionListener {
    private final int WIDTH = 800, HEIGHT = 600;
    private java.util.List<Rabbit> rabbits;
    private javax.swing.Timer timer;
    private Random rand = new Random();

    public RabbitsMoving() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(140, 140, 140)); // light gray background
        rabbits = new ArrayList<>();
        // Create 5 rabbits at random positions.
        for (int i = 0; i < 5; i++) {
            rabbits.add(new Rabbit(rand.nextInt(WIDTH), rand.nextInt(HEIGHT)));
        }
        // Update rabbit positions every 30ms.
        timer = new javax.swing.Timer(30, this);
        timer.start();
    }

    // Update all rabbits’ positions.
    public void actionPerformed(ActionEvent e) {
        for (Rabbit r : rabbits) {
            r.move(WIDTH, HEIGHT);
        }
        repaint();
    }

    // Draw rabbits as white circles.
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        for (Rabbit r : rabbits) {
            g.fillOval(r.x, r.y, r.size, r.size);
        }
    }

    // Rabbit class with random movement.
    class Rabbit {
        int x, y;
        int size = 20;
        int dx, dy;
        
        public Rabbit(int x, int y) {
            this.x = x;
            this.y = y;
            // Random velocity between -2 and +2 (excluding 0).
            dx = rand.nextInt(5) - 2;
            dy = rand.nextInt(5) - 2;
        }
        
        // Update position and bounce off edges.
        public void move(int width, int height) {
            x += dx;
            y += dy;
            if (x < 0 || x > width - size)  dx = -dx;
            if (y < 0 || y > height - size) dy = -dy;
        }
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Rabbits Moving");
        RabbitsMoving panel = new RabbitsMoving();
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

