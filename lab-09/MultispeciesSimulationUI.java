import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MultispeciesSimulationUI extends JFrame {
    private SimulationPanel simPanel;

    // Slider fields kept as instance variables so the reset button can restore defaults.
    private JSlider rCSlider, rRSlider, rWSlider;
    private JSlider kCSlider, kRSlider, kWSlider;
    private JSlider aSlider, bSlider, cSlider, dSlider;

    public MultispeciesSimulationUI() {
        setTitle("Multispecies Continuous Simulation with Sliders and Phase Plot");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        simPanel = new SimulationPanel();
        add(simPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        // --- Growth Rates ---
        JPanel growthPanel = new JPanel();
        growthPanel.setLayout(new BoxLayout(growthPanel, BoxLayout.Y_AXIS));
        growthPanel.setBorder(BorderFactory.createTitledBorder("Growth Rates"));

        rCSlider = makeSlider(10, 300, 150, val -> simPanel.rC = val / 100.0);
        rRSlider = makeSlider(10, 300, 100, val -> simPanel.rR = val / 100.0);
        rWSlider = makeSlider(10, 200,  50, val -> simPanel.rW = val / 100.0);
        growthPanel.add(wrapSlider("r_C", rCSlider));
        growthPanel.add(wrapSlider("r_R", rRSlider));
        growthPanel.add(wrapSlider("r_W", rWSlider));
        controlPanel.add(growthPanel);

        // --- Carrying Capacities ---
        JPanel capacityPanel = new JPanel();
        capacityPanel.setLayout(new BoxLayout(capacityPanel, BoxLayout.Y_AXIS));
        capacityPanel.setBorder(BorderFactory.createTitledBorder("Carrying Capacities"));

        kCSlider = makeSlider(10, 300, 100, val -> simPanel.KC = val / 100.0);
        kRSlider = makeSlider(10, 300, 100, val -> simPanel.KR = val / 100.0);
        kWSlider = makeSlider(10, 300, 100, val -> simPanel.KW = val / 100.0);
        capacityPanel.add(wrapSlider("K_C", kCSlider));
        capacityPanel.add(wrapSlider("K_R", kRSlider));
        capacityPanel.add(wrapSlider("K_W", kWSlider));
        controlPanel.add(capacityPanel);

        // --- Interaction Terms ---
        JPanel interactionPanel = new JPanel();
        interactionPanel.setLayout(new BoxLayout(interactionPanel, BoxLayout.Y_AXIS));
        interactionPanel.setBorder(BorderFactory.createTitledBorder("Interaction Terms"));

        aSlider = makeSlider(10, 300, 120, val -> simPanel.a = val / 100.0);
        bSlider = makeSlider(10, 300,  60, val -> simPanel.b = val / 100.0);
        cSlider = makeSlider(10, 300,  70, val -> simPanel.c = val / 100.0);
        dSlider = makeSlider(10, 300,  40, val -> simPanel.d = val / 100.0);
        interactionPanel.add(wrapSlider("a (R eats C)",     aSlider));
        interactionPanel.add(wrapSlider("b (R gains from C)", bSlider));
        interactionPanel.add(wrapSlider("c (W eats R)",     cSlider));
        interactionPanel.add(wrapSlider("d (W gains from R)", dSlider));
        controlPanel.add(interactionPanel);

        // --- Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton rerun = new JButton("Run Simulation");
        rerun.addActionListener(e -> { simPanel.simulate(); simPanel.repaint(); });
        buttonPanel.add(rerun);

        JButton plotPhase = new JButton("Show Phase Plot");
        plotPhase.addActionListener(e -> {
            simPanel.simulate();
            simPanel.repaint();
            PhasePanel phase = new PhasePanel(simPanel.R, simPanel.W, simPanel.C);
            JFrame pf = new JFrame("Phase Plot (Rabbits vs. Wolves, Carrots as colour)");
            pf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            pf.setContentPane(phase);
            pf.pack();
            pf.setLocationRelativeTo(null);
            pf.setVisible(true);
        });
        buttonPanel.add(plotPhase);

        // Reset sliders to defaults in-place rather than recreating the whole window.
        JButton reset = new JButton("Reset to Defaults");
        reset.addActionListener(e -> {
            rCSlider.setValue(150); rRSlider.setValue(100); rWSlider.setValue(50);
            kCSlider.setValue(100); kRSlider.setValue(100); kWSlider.setValue(100);
            aSlider.setValue(120);  bSlider.setValue(60);
            cSlider.setValue(70);   dSlider.setValue(40);
            simPanel.simulate();
            simPanel.repaint();
        });
        buttonPanel.add(reset);

        controlPanel.add(buttonPanel);
        add(controlPanel, BorderLayout.EAST);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Creates a configured JSlider without a label wrapper.
    private JSlider makeSlider(int min, int max, int init,
                               java.util.function.IntConsumer update) {
        JSlider slider = new JSlider(min, max, init);
        slider.setPaintTicks(true);
        slider.setMajorTickSpacing((max - min) / 5);
        slider.setMinorTickSpacing((max - min) / 10);
        // Suppress raw integer tick labels — the value label above shows the real value.
        slider.setPaintLabels(false);
        slider.addChangeListener(e -> update.accept(slider.getValue()));
        return slider;
    }

    // Wraps a slider in a panel with a live-updating decimal value label.
    private JPanel wrapSlider(String labelText, JSlider slider) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel valueLabel = new JLabel(
                String.format("%s = %.2f", labelText, slider.getValue() / 100.0));
        panel.add(valueLabel, BorderLayout.NORTH);
        slider.addChangeListener(e ->
                valueLabel.setText(String.format("%s = %.2f",
                        labelText, slider.getValue() / 100.0)));
        panel.add(slider, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MultispeciesSimulationUI::new);
    }
}

// ─────────────────────────────────────────────────────────────────────────────
class SimulationPanel extends JPanel {
    ArrayList<Double> C = new ArrayList<>();
    ArrayList<Double> R = new ArrayList<>();
    ArrayList<Double> W = new ArrayList<>();

    double rC = 1.5, KC = 1.0;
    double rR = 1.0, KR = 1.0;
    double rW = 0.5, KW = 1.0;

    // Interaction coefficients — distinct names prevent shadowing by local
    // population variables inside simulate().
    double a = 1.2, b = 0.6, c = 0.7, d = 0.4;

    double DT = 0.01, TMAX = 50;

    public SimulationPanel() {
        setPreferredSize(new Dimension(900, 500));
        simulate();
    }

    public void simulate() {
        C.clear(); R.clear(); W.clear();

        // Use distinct names (cPop, rPop, wPop) so they never shadow the
        // interaction-coefficient fields a, b, c, d above.
        double cPop = 0.9, rPop = 0.5, wPop = 0.2;
        double t = 0;

        while (t <= TMAX) {
            C.add(cPop);
            R.add(rPop);
            W.add(wPop);

            double dc = rC * cPop * (1 - cPop / KC) - a * cPop * rPop;
            double dr = rR * rPop * (1 - rPop / KR) + b * cPop * rPop - c * rPop * wPop;
            // Note: the logistic self-growth term (rW * wPop * (1 - wPop/KW)) means
            // wolves sustain a population even when rabbits go to zero.  A stricter
            // Lotka-Volterra predator model would replace it with a death term
            // (-mW * wPop) so that wolves die out without prey.
            double dw = rW * wPop * (1 - wPop / KW) + d * rPop * wPop;

            cPop = Math.max(0, cPop + dc * DT);
            rPop = Math.max(0, rPop + dr * DT);
            wPop = Math.max(0, wPop + dw * DT);
            t += DT;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth(), height = getHeight();
        int left = 65, right = 20, top = 30, bottom = 50;
        int plotW = width - left - right;
        int plotH = height - top - bottom;

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);

        // Dynamic y-axis: scale to the largest value across all three species.
        double maxY = 0;
        for (double v : C) maxY = Math.max(maxY, v);
        for (double v : R) maxY = Math.max(maxY, v);
        for (double v : W) maxY = Math.max(maxY, v);
        maxY = Math.max(maxY * 1.1, 0.1);

        // Grid lines and y-axis tick marks
        g2.setFont(g2.getFont().deriveFont(11f));
        for (int i = 0; i <= 4; i++) {
            double val = i * maxY / 4.0;
            int ty = mapY(val, 0, maxY, top, plotH);
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(left, ty, left + plotW, ty);
            g2.setColor(Color.BLACK);
            g2.drawLine(left - 4, ty, left, ty);
            g2.drawString(String.format("%.2f", val), left - 42, ty + 4);
        }

        // X-axis tick marks (time)
        for (int i = 0; i <= 5; i++) {
            double t = i * TMAX / 5.0;
            int tx = mapX(t, 0, TMAX, left, plotW);
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(tx, top, tx, top + plotH);
            g2.setColor(Color.BLACK);
            g2.drawLine(tx, top + plotH, tx, top + plotH + 4);
            g2.drawString(String.format("%.0f", t), tx - 8, top + plotH + 18);
        }

        // Axes
        g2.setColor(Color.BLACK);
        g2.drawLine(left, top + plotH, left + plotW, top + plotH);
        g2.drawLine(left, top, left, top + plotH);

        // Axis labels
        g2.drawString("Time (t)", left + plotW / 2 - 20, height - 8);
        Graphics2D g2r = (Graphics2D) g2.create();
        g2r.rotate(-Math.PI / 2, 14, top + plotH / 2);
        g2r.drawString("Population", 14 - 35, top + plotH / 2 + 4);
        g2r.dispose();

        // Legend
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
        g2.setColor(Color.GREEN.darker()); g2.drawString("Carrots", left + plotW - 120, top + 15);
        g2.setColor(Color.BLUE);           g2.drawString("Rabbits", left + plotW -  70, top + 15);
        g2.setColor(Color.RED);            g2.drawString("Wolves",  left + plotW -  10, top + 15);

        // Species curves
        drawCurve(g2, C, left, top, plotW, plotH, maxY, Color.GREEN.darker());
        drawCurve(g2, R, left, top, plotW, plotH, maxY, Color.BLUE);
        drawCurve(g2, W, left, top, plotW, plotH, maxY, Color.RED);
    }

    private void drawCurve(Graphics2D g2, ArrayList<Double> data,
                           int left, int top, int plotW, int plotH,
                           double maxY, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.8f));
        for (int i = 1; i < data.size(); i++) {
            int x1 = mapX(i - 1, 0, data.size() - 1, left, plotW);
            int y1 = mapY(data.get(i - 1), 0, maxY, top, plotH);
            int x2 = mapX(i,     0, data.size() - 1, left, plotW);
            int y2 = mapY(data.get(i),     0, maxY, top, plotH);
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

// ─────────────────────────────────────────────────────────────────────────────
class PhasePanel extends JPanel {
    ArrayList<Double> rabbits, wolves, carrots;

    public PhasePanel(ArrayList<Double> R, ArrayList<Double> W, ArrayList<Double> C) {
        this.rabbits = R;
        this.wolves  = W;
        this.carrots = C;
        setPreferredSize(new Dimension(520, 520));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int left = 60, right = 20, top = 40, bottom = 55;
        int plotW = getWidth()  - left - right;
        int plotH = getHeight() - top  - bottom;

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Dynamic axis range
        double maxR = rabbits.stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
        double maxW = wolves.stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
        maxR = Math.max(maxR * 1.1, 0.1);
        maxW = Math.max(maxW * 1.1, 0.1);

        // Grid lines and axis ticks
        g2.setFont(g2.getFont().deriveFont(11f));
        for (int i = 0; i <= 4; i++) {
            // X ticks (rabbits)
            double xVal = i * maxR / 4.0;
            int tx = left + (int)(xVal / maxR * plotW);
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(tx, top, tx, top + plotH);
            g2.setColor(Color.BLACK);
            g2.drawLine(tx, top + plotH, tx, top + plotH + 4);
            g2.drawString(String.format("%.2f", xVal), tx - 10, top + plotH + 18);

            // Y ticks (wolves)
            double yVal = i * maxW / 4.0;
            int ty = top + plotH - (int)(yVal / maxW * plotH);
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(left, ty, left + plotW, ty);
            g2.setColor(Color.BLACK);
            g2.drawLine(left - 4, ty, left, ty);
            g2.drawString(String.format("%.2f", yVal), left - 42, ty + 4);
        }

        // Axes
        g2.setColor(Color.BLACK);
        g2.drawLine(left, top + plotH, left + plotW, top + plotH);
        g2.drawLine(left, top, left, top + plotH);

        // Axis labels
        g2.drawString("Rabbits (R)", left + plotW / 2 - 30, getHeight() - 10);
        Graphics2D g2r = (Graphics2D) g2.create();
        g2r.rotate(-Math.PI / 2, 14, top + plotH / 2);
        g2r.drawString("Wolves (W)", 14 - 35, top + plotH / 2 + 4);
        g2r.dispose();

        // Title
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
        g2.drawString("Phase Plot — Carrots encoded as colour (green\u2192red = high\u2192low)",
                      left, top - 10);

        // Phase-space scatter
        for (int i = 0; i < rabbits.size(); i++) {
            double r = Math.min(rabbits.get(i), maxR);
            double w = Math.min(wolves.get(i),  maxW);
            double c = Math.min(carrots.get(i), 2.0);

            int x = left + (int)(r / maxR * plotW);
            int y = top  + plotH - (int)(w / maxW * plotH);

            float hue = (float)(0.33 * (1.0 - c / 2.0));
            g2.setColor(Color.getHSBColor(hue, 1f, 1f));
            g2.fillRect(x, y, 2, 2);
        }
    }
}
