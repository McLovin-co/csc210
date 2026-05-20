import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class CirclePlotter extends JFrame {
    public CirclePlotter() {
        setTitle("Circle Plotter");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new CirclePanel());
        setLocationRelativeTo(null); // Centers the window on screen
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CirclePlotter frame = new CirclePlotter();
            frame.setVisible(true);
        });
    }
}

class CirclePanel extends JPanel implements MouseListener {
    // List to hold the center points of circles
    private List<Point> circleCenters;
    private final int radius = 20; // Fixed radius for the circles

    public CirclePanel() {
        circleCenters = new ArrayList<>();
        addMouseListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw a circle for each stored center point
        for (Point center : circleCenters) {
            // Calculate top-left corner of the bounding rectangle
            int x = center.x - radius;
            int y = center.y - radius;
            g.drawOval(x, y, 2 * radius, 2 * radius);
        }
    }

    // MouseListener methods

    @Override
    public void mouseClicked(MouseEvent e) {
        // Add the point where the mouse was clicked and repaint the panel
        circleCenters.add(e.getPoint());
        repaint();
    }

    @Override public void mousePressed(MouseEvent e) { }
    @Override public void mouseReleased(MouseEvent e) { }
    @Override public void mouseEntered(MouseEvent e) { }
    @Override public void mouseExited(MouseEvent e) { }
  
}

