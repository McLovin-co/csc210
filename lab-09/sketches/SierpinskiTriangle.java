import javax.swing.*;
import java.awt.*;

public class SierpinskiTriangle extends JPanel {
    private int depth;

    public SierpinskiTriangle(int depth) {
        this.depth = depth;
        setBackground(Color.WHITE);
    }

    /**
     * Draws a filled triangle given its top vertex (apex) and side length.
     * The triangle’s base is centered horizontally relative to the apex.
     *
     * @param g    the Graphics context.
     * @param x    the x-coordinate of the triangle’s apex.
     * @param y    the y-coordinate of the triangle’s apex.
     * @param side the length of the triangle’s side.
     */
    private void fillTriangle(Graphics g, double x, double y, double side) {
        double height = side * Math.sqrt(3) / 2;
        int[] xPoints = { (int)x, (int)(x - side / 2), (int)(x + side / 2) };
        int[] yPoints = { (int)y, (int)(y + height), (int)(y + height) };
        g.fillPolygon(xPoints, yPoints, 3);
    }

    /**
     * Recursively draws the Sierpinski Triangle.
     *
     * @param g     the Graphics context.
     * @param depth the current recursion depth.
     * @param x     the x-coordinate of the triangle’s apex.
     * @param y     the y-coordinate of the triangle’s apex.
     * @param side  the length of the triangle’s side.
     */
    private void drawSierpinski(Graphics g, int depth, double x, double y, double side) {
        if (depth == 0) {
            // Base case: Draw a filled triangle.
            fillTriangle(g, x, y, side);
        } else {
            // Compute the side length for the smaller triangles.
            double newSide = side / 2;
            // The height of a triangle with side 'side'
            double height = side * Math.sqrt(3) / 2;
            // The height for the smaller triangle.
            double newHeight = newSide * Math.sqrt(3) / 2;

            // Recursively draw the three sub-triangles:
            // 1. Top triangle with the same apex.
            drawSierpinski(g, depth - 1, x, y, newSide);
            // 2. Left triangle: its apex is at (x - newSide/2, y + newHeight).
            drawSierpinski(g, depth - 1, x - newSide / 2, y + newHeight, newSide);
            // 3. Right triangle: its apex is at (x + newSide/2, y + newHeight).
            drawSierpinski(g, depth - 1, x + newSide / 2, y + newHeight, newSide);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLUE);

        int width = getWidth();
        int height = getHeight();
        // Choose the side length to fit inside the panel.
        double side = Math.min(width, height * 2 / Math.sqrt(3));
        // Center the triangle horizontally; start a bit below the top.
        double x = width / 2.0;
        double y = 10;
        drawSierpinski(g, depth, x, y, side);
    }

    public static void main(String[] args) {
        int depth = 5; // Default recursion depth.
        if (args.length > 0) {
            try {
                depth = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid depth provided. Using default depth " + depth);
            }
        }
        
        JFrame frame = new JFrame("Sierpinski Triangle - Depth: " + depth);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SierpinskiTriangle panel = new SierpinskiTriangle(depth);
        frame.add(panel);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

