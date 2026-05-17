import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ContinuousLogisticGrowth extends JPanel {
    static final int WIDTH = 800;
    static final int HEIGHT = 600;

    // Parameters
    double r = 1.0;     // growth rate
    double K = 1.0;     // carrying capacity
    double x0 = 0.1;    // initial population
    double dt = 0.05;   // time step
    double tMax = 20;   // total time

    // Data storage
    ArrayList<Double> time = new ArrayList<>();
    ArrayList<Double> population = new ArrayList<>();

    public ContinuousLogisticGrowth() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        simulate();
    }

    private void simulate() {
        double t = 0;
        double x = x0;

        while (t <= tMax) {
            time.add(t);
            population.add(x);

            // Euler's method
            double dx = r * x * (1 - x / K);
            x = x + dx * dt;
            t = t + dt;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int margin = 60;
        int graphWidth = getWidth() - 2 * margin;
        int graphHeight = getHeight() - 2 * margin;

        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(margin, margin, graphWidth, graphHeight);

        // Axis labels
        g.setColor(Color.BLACK);
        g.drawString("Time →", getWidth() / 2, getHeight() - 10);
        g.drawString("Population", 10, getHeight() / 2);

        // Draw the curve
        g.setColor(Color.BLUE);
        int prevX = margin;
        int prevY = margin + graphHeight - (int)(population.get(0) / K * graphHeight);

        for (int i = 1; i < population.size(); i++) {
            int xPixel = margin + (int)(i * graphWidth / (double)population.size());
            int yPixel = margin + graphHeight - (int)(population.get(i) / K * graphHeight);

            g.drawLine(prevX, prevY, xPixel, yPixel);
            prevX = xPixel;
            prevY = yPixel;
        }

        // Draw final value (approximate equilibrium)
        g.setColor(Color.RED);
        int eqY = margin + graphHeight - (int)(K / K * graphHeight);
        g.drawLine(margin, eqY, margin + graphWidth, eqY);
        g.drawString("K = " + K, margin + 5, eqY - 5);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Continuous Logistic Growth");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new ContinuousLogisticGrowth());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

