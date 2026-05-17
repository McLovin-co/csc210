import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MultispeciesContinuousSimulation extends JPanel {
    static final int WIDTH = 900;
    static final int HEIGHT = 600;
    static final double DT = 0.01;
    static final double T_MAX = 50;

    ArrayList<Double> carrots = new ArrayList<>();
    ArrayList<Double> rabbits = new ArrayList<>();
    ArrayList<Double> wolves  = new ArrayList<>();

    public MultispeciesContinuousSimulation() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        simulate();
    }

    private void simulate() {
        // Initial populations
        double C = 0.9;
        double R = 0.5;
        double W = 0.2;

        // Parameters
        double rC = 1.5, KC = 1.0;
        double rR = 1.0, KR = 1.0;
        double rW = 0.5, KW = 1.0;

        double a = 1.2;  // rabbits eat carrots
        double b = 0.6;  // rabbits benefit from carrots
        double c = 0.7;  // wolves eat rabbits
        double d = 0.4;  // wolves benefit from rabbits

        double t = 0;
        while (t <= T_MAX) {
            carrots.add(C);
            rabbits.add(R);
            wolves.add(W);

            // Calculate derivatives
            double dC = rC * C * (1 - C / KC) - a * C * R;
            double dR = rR * R * (1 - R / KR) + b * C * R - c * R * W;
            double dW = rW * W * (1 - W / KW) + d * R * W;

            // Euler update
            C += dC * DT;
            R += dR * DT;
            W += dW * DT;

            // Clamp to non-negative values
            C = Math.max(0, C);
            R = Math.max(0, R);
            W = Math.max(0, W);

            t += DT;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int margin = 60;
        int graphWidth = getWidth() - 2 * margin;
        int graphHeight = getHeight() - 2 * margin;

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw box
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(margin, margin, graphWidth, graphHeight);

        g.setColor(Color.BLACK);
        g.drawString("Carrots (Green), Rabbits (Blue), Wolves (Red)", margin, 20);

        drawLineGraph(g, carrots, margin, margin, graphWidth, graphHeight, Color.GREEN);
        drawLineGraph(g, rabbits, margin, margin, graphWidth, graphHeight, Color.BLUE);
        drawLineGraph(g, wolves,  margin, margin, graphWidth, graphHeight, Color.RED);
    }

    private void drawLineGraph(Graphics g, ArrayList<Double> data, int x, int y, int w, int h, Color color) {
        g.setColor(color);
        double maxY = 2.0;
        int prevX = x;
        int prevY = y + h - (int) (data.get(0) / maxY * h);

        for (int i = 1; i < data.size(); i++) {
            int currX = x + i * w / data.size();
            int currY = y + h - (int) (data.get(i) / maxY * h);
            g.drawLine(prevX, prevY, currX, currY);
            prevX = currX;
            prevY = currY;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Multispecies Continuous Simulation");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new MultispeciesContinuousSimulation());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

