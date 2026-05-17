import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

public class InteractiveLogisticGrowth extends JFrame {
    private LogisticPanel graphPanel;

    public InteractiveLogisticGrowth() {
        setTitle("Interactive Continuous Logistic Growth");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 650);
        setLayout(new BorderLayout());

        graphPanel = new LogisticPanel();
        add(graphPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(3, 1));

        // Growth rate r: range 0–200 representing 0.00–2.00
        JSlider rSlider = new JSlider(0, 200, 100);
        rSlider.setLabelTable(makeDecimalLabels(0, 200, 50, 0.01));
        rSlider.setMajorTickSpacing(50);
        rSlider.setMinorTickSpacing(10);
        rSlider.setPaintTicks(true);
        rSlider.setPaintLabels(true);
        rSlider.addChangeListener(e -> graphPanel.setR(rSlider.getValue() / 100.0));
        controlPanel.add(labeled("Growth rate r", rSlider));

        // Carrying capacity K: range 0–300 representing 0.00–3.00
        JSlider kSlider = new JSlider(0, 300, 100);
        kSlider.setLabelTable(makeDecimalLabels(0, 300, 100, 0.01));
        kSlider.setMajorTickSpacing(100);
        kSlider.setMinorTickSpacing(25);
        kSlider.setPaintTicks(true);
        kSlider.setPaintLabels(true);
        // Guard K > 0 to avoid division by zero in the logistic formula.
        kSlider.addChangeListener(e -> graphPanel.setK(Math.max(0.01, kSlider.getValue() / 100.0)));
        controlPanel.add(labeled("Carrying capacity K", kSlider));

        // Initial population x₀: range 0–100 representing 0.00–1.00
        JSlider x0Slider = new JSlider(0, 100, 10);
        x0Slider.setLabelTable(makeDecimalLabels(0, 100, 25, 0.01));
        x0Slider.setMajorTickSpacing(25);
        x0Slider.setMinorTickSpacing(5);
        x0Slider.setPaintTicks(true);
        x0Slider.setPaintLabels(true);
        x0Slider.addChangeListener(e -> graphPanel.setX0(x0Slider.getValue() / 100.0));
        controlPanel.add(labeled("Initial population x\u2080", x0Slider));

        add(controlPanel, BorderLayout.SOUTH);
    }

    // Builds a label table mapping integer slider values to displayed decimal strings.
    private static Hashtable<Integer, JLabel> makeDecimalLabels(int min, int max, int step, double scale) {
        Hashtable<Integer, JLabel> t = new Hashtable<>();
        for (int v = min; v <= max; v += step) {
            t.put(v, new JLabel(String.format("%.2f", v * scale)));
        }
        return t;
    }

    private JPanel labeled(String text, JSlider slider) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(text), BorderLayout.WEST);
        panel.add(slider, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InteractiveLogisticGrowth().setVisible(true));
    }

    static class LogisticPanel extends JPanel {
        private double r   = 1.0;
        private double K   = 1.0;
        private double x0  = 0.1;
        private final double dt   = 0.05;
        private final double tMax = 20.0;
        private final ArrayList<Double> time       = new ArrayList<>();
        private final ArrayList<Double> population = new ArrayList<>();

        public LogisticPanel() {
            setPreferredSize(new Dimension(800, 500));
            simulate();
        }

        public void setR(double r)   { this.r  = r;  simulate(); repaint(); }
        public void setK(double K)   { this.K  = K;  simulate(); repaint(); }
        public void setX0(double x0) { this.x0 = x0; simulate(); repaint(); }

        private void simulate() {
            time.clear();
            population.clear();
            double t = 0, x = x0;
            while (t <= tMax) {
                time.add(t);
                population.add(x);
                double dx = r * x * (1 - x / K);
                x = Math.max(0, x + dx * dt);
                t += dt;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth(), height = getHeight();
            int left = 65, right = 20, top = 35, bottom = 50;
            int plotW = width - left - right;
            int plotH = height - top - bottom;

            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, width, height);

            // Title showing current parameter values
            g2.setColor(Color.BLACK);
            g2.setFont(g2.getFont().deriveFont(12f));
            g2.drawString(String.format("r = %.2f   K = %.2f   x\u2080 = %.2f", r, K, x0),
                          left, top - 12);

            // Y-axis scale: accommodate population that may exceed K (e.g., x0 > K)
            double maxPop = population.isEmpty() ? K
                    : Math.max(K, Collections.max(population)) * 1.1;

            // Horizontal grid lines and y-axis tick labels
            g2.setFont(g2.getFont().deriveFont(11f));
            for (int i = 0; i <= 5; i++) {
                double val = i * maxPop / 5.0;
                int ty = mapY(val, 0, maxPop, top, plotH);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawLine(left, ty, left + plotW, ty);
                g2.setColor(Color.BLACK);
                g2.drawLine(left - 4, ty, left, ty);
                g2.drawString(String.format("%.2f", val), left - 42, ty + 4);
            }

            // X-axis tick marks and time labels
            for (int i = 0; i <= 5; i++) {
                double t = i * tMax / 5.0;
                int tx = mapX(t, 0, tMax, left, plotW);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawLine(tx, top, tx, top + plotH);
                g2.setColor(Color.BLACK);
                g2.drawLine(tx, top + plotH, tx, top + plotH + 4);
                g2.drawString(String.format("%.0f", t), tx - 8, top + plotH + 18);
            }

            // Axes
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawLine(left, top + plotH, left + plotW, top + plotH); // x-axis
            g2.drawLine(left, top, left, top + plotH);                  // y-axis

            // Axis labels
            g2.drawString("Time (t)", left + plotW / 2 - 20, height - 8);
            Graphics2D g2r = (Graphics2D) g2.create();
            g2r.rotate(-Math.PI / 2, 14, top + plotH / 2);
            g2r.drawString("Population (x)", 14 - 45, top + plotH / 2 + 4);
            g2r.dispose();

            if (population.isEmpty()) return;

            // K equilibrium line (dashed red)
            if (K <= maxPop) {
                int eqY = mapY(K, 0, maxPop, top, plotH);
                g2.setColor(Color.RED);
                g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                        0, new float[]{6, 4}, 0));
                g2.drawLine(left, eqY, left + plotW, eqY);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawString(String.format("K = %.2f", K), left + 5, eqY - 5);
            }

            // Population curve
            g2.setColor(Color.BLUE);
            g2.setStroke(new BasicStroke(1.8f));
            int n = population.size();
            for (int i = 1; i < n; i++) {
                int x1 = mapX(time.get(i - 1), 0, tMax, left, plotW);
                int y1 = mapY(population.get(i - 1), 0, maxPop, top, plotH);
                int x2 = mapX(time.get(i), 0, tMax, left, plotW);
                int y2 = mapY(population.get(i), 0, maxPop, top, plotH);
                g2.drawLine(x1, y1, x2, y2);
            }
        }

        private int mapX(double v, double srcMin, double srcMax, int left, int plotW) {
            return (int) (left + (v - srcMin) * plotW / (srcMax - srcMin));
        }

        private int mapY(double v, double srcMin, double srcMax, int top, int plotH) {
            return (int) (top + plotH - (v - srcMin) * plotH / (srcMax - srcMin));
        }
    }
}
