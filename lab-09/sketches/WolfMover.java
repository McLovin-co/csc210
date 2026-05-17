import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WolfMover extends JPanel implements ActionListener, KeyListener {
    private final int WIDTH = 800, HEIGHT = 600;
    private Wolf wolf;
    private javax.swing.Timer timer;
    private boolean up, down, left, right;

    public WolfMover() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(200, 255, 200)); // light green background
        wolf = new Wolf(WIDTH / 2, HEIGHT / 2);
        // Update the wolf's position every 30ms.
        timer = new javax.swing.Timer(30, this);
        timer.start();
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
    }

    // Update wolf position based on arrow key flags.
    public void actionPerformed(ActionEvent e) {
        int speed = 5;
        if (up)    wolf.y -= speed;
        if (down)  wolf.y += speed;
        if (left)  wolf.x -= speed;
        if (right) wolf.x += speed;
        // Keep the wolf within the panel boundaries.
        wolf.x = Math.max(0, Math.min(WIDTH - wolf.size, wolf.x));
        wolf.y = Math.max(0, Math.min(HEIGHT - wolf.size, wolf.y));
        repaint();
    }

    // Draw the wolf as a dark gray circle.
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.DARK_GRAY);
        g.fillOval(wolf.x, wolf.y, wolf.size, wolf.size);
    }

    // Key events to set/unset movement flags.
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_UP:    up = true; break;
            case KeyEvent.VK_DOWN:  down = true; break;
            case KeyEvent.VK_LEFT:  left = true; break;
            case KeyEvent.VK_RIGHT: right = true; break;
        }
    }
    public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_UP:    up = false; break;
            case KeyEvent.VK_DOWN:  down = false; break;
            case KeyEvent.VK_LEFT:  left = false; break;
            case KeyEvent.VK_RIGHT: right = false; break;
        }
    }
    public void keyTyped(KeyEvent e) {}

    // Simple Wolf class.
    class Wolf {
        int x, y;
        int size = 30;
        public Wolf(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Wolf Mover");
        WolfMover panel = new WolfMover();
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

