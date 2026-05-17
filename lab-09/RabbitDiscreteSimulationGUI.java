import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

public class RabbitDiscreteSimulationGUI extends JFrame {

    // Simulation state
    private Ecosystem ecosystem;
    private java.util.List<Integer> populationHistory = new ArrayList<>();

    // Tracks the running maximum population so the y-axis never shrinks mid-run.
    private int graphMaxPop = 1;

    // GUI Components
    private GraphPanel graphPanel;
    private JTextField initialPopulationField;
    private JTextField carrotSupplyField;
    private JButton playButton;
    private JButton stepButton;
    private JButton resetButton;
    private JButton exportDataButton;
    private JButton exportGraphButton;
    private JSlider speedSlider;

    // Timer for running simulation steps — use javax.swing.Timer explicitly.
    private javax.swing.Timer simulationTimer;
    private boolean isRunning = false;

    public RabbitDiscreteSimulationGUI() {
        super("Rabbit Simulation GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        graphPanel = new GraphPanel();
        graphPanel.setPreferredSize(new Dimension(800, 600));
        add(graphPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout());

        controlPanel.add(new JLabel("Initial Population:"));
        initialPopulationField = new JTextField("50", 5);
        controlPanel.add(initialPopulationField);

        controlPanel.add(new JLabel("Carrot Supply/Gen:"));
        carrotSupplyField = new JTextField("100", 5);
        controlPanel.add(carrotSupplyField);

        playButton = new JButton("Play");
        controlPanel.add(playButton);

        stepButton = new JButton("Step");
        controlPanel.add(stepButton);

        resetButton = new JButton("Reset");
        controlPanel.add(resetButton);

        // Speed slider: slider right = faster (lower delay).
        // Actual timer delay = 2100 - sliderValue, so value=2000 → 100 ms (fast),
        // value=100 → 2000 ms (slow).
        controlPanel.add(new JLabel("Speed:"));
        speedSlider = new JSlider(JSlider.HORIZONTAL, 100, 2000, 1000);
        speedSlider.setMajorTickSpacing(400);
        speedSlider.setMinorTickSpacing(100);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(false); // raw ms values are misleading here
        // Wrap the slider with direction labels so the fast/slow ends are obvious.
        JPanel speedPanel = new JPanel(new BorderLayout());
        speedPanel.add(new JLabel(" Slow "), BorderLayout.WEST);
        speedPanel.add(speedSlider, BorderLayout.CENTER);
        speedPanel.add(new JLabel(" Fast "), BorderLayout.EAST);
        controlPanel.add(speedPanel);

        exportDataButton = new JButton("Export Data");
        controlPanel.add(exportDataButton);

        exportGraphButton = new JButton("Export Graph");
        controlPanel.add(exportGraphButton);

        add(controlPanel, BorderLayout.SOUTH);

        playButton.addActionListener(e -> togglePlay());
        stepButton.addActionListener(e -> stepSimulation());
        resetButton.addActionListener(e -> resetSimulation());
        exportDataButton.addActionListener(e -> exportData());
        exportGraphButton.addActionListener(e -> exportGraph());
        // Slider far-right = high value = low delay = fast.
        speedSlider.addChangeListener(e -> simulationTimer.setDelay(2100 - speedSlider.getValue()));

        simulationTimer = new javax.swing.Timer(2100 - speedSlider.getValue(), e -> stepSimulation());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        resetSimulation();
    }

    private void togglePlay() {
        if (isRunning) {
            simulationTimer.stop();
            playButton.setText("Play");
            isRunning = false;
        } else {
            simulationTimer.start();
            playButton.setText("Pause");
            isRunning = true;
        }
    }

    private void stepSimulation() {
        if (ecosystem != null) {
            ecosystem.simulateGeneration();
            int pop = ecosystem.getPopulation();
            populationHistory.add(pop);
            if (pop > graphMaxPop) graphMaxPop = pop;
            graphPanel.repaint();
        }
    }

    private void resetSimulation() {
        simulationTimer.stop();
        isRunning = false;
        playButton.setText("Play");
        graphMaxPop = 1;
        populationHistory.clear();

        int initialPopulation = parseIntField(initialPopulationField, 50);
        int carrotSupply      = parseIntField(carrotSupplyField, 100);

        ecosystem = new Ecosystem(initialPopulation, carrotSupply);
        populationHistory.add(ecosystem.getPopulation());
        graphPanel.repaint();
    }

    // Parses a positive integer from a text field; shows an error and restores the
    // default value if the input is missing or invalid.
    private int parseIntField(JTextField field, int defaultValue) {
        try {
            int val = Integer.parseInt(field.getText().trim());
            if (val <= 0) throw new NumberFormatException();
            return val;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "'" + field.getText() + "' is not a valid positive integer. "
                    + "Using default: " + defaultValue);
            field.setText(String.valueOf(defaultValue));
            return defaultValue;
        }
    }

    private void exportData() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(chooser.getSelectedFile()))) {
                writer.println("Generation,Population");
                for (int i = 0; i < populationHistory.size(); i++) {
                    writer.println(i + "," + populationHistory.get(i));
                }
                JOptionPane.showMessageDialog(this, "Data exported successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting data: " + e.getMessage());
            }
        }
    }

    private void exportGraph() {
        BufferedImage image = new BufferedImage(
                graphPanel.getWidth(), graphPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        graphPanel.paint(g2);
        g2.dispose();
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ImageIO.write(image, "png", chooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Graph exported successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting graph: " + e.getMessage());
            }
        }
    }

    private class GraphPanel extends JPanel {
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

            int maxPop = Math.max(graphMaxPop, 1);

            // Horizontal grid lines and y-axis tick marks
            g2.setFont(g2.getFont().deriveFont(11f));
            for (int i = 0; i <= 4; i++) {
                int val = i * maxPop / 4;
                int ty  = top + plotH - (int)((double) val / maxPop * plotH);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawLine(left, ty, left + plotW, ty);
                g2.setColor(Color.BLACK);
                g2.drawLine(left - 4, ty, left, ty);
                g2.drawString(String.valueOf(val), left - 38, ty + 4);
            }

            // X-axis tick marks — one every ~10 generations, or spaced to fit
            if (populationHistory.size() >= 2) {
                int numPoints = populationHistory.size();
                int tickEvery = Math.max(1, numPoints / 10);
                for (int i = 0; i < numPoints; i += tickEvery) {
                    int tx = left + (int)((double) i / (numPoints - 1) * plotW);
                    g2.setColor(Color.LIGHT_GRAY);
                    g2.drawLine(tx, top, tx, top + plotH);
                    g2.setColor(Color.BLACK);
                    g2.drawLine(tx, top + plotH, tx, top + plotH + 4);
                    g2.drawString(String.valueOf(i), tx - 8, top + plotH + 18);
                }
            }

            // Axes
            g2.setColor(Color.BLACK);
            g2.drawLine(left, top + plotH, left + plotW, top + plotH); // x-axis
            g2.drawLine(left, top, left, top + plotH);                  // y-axis

            // Axis labels
            g2.drawString("Generation", left + plotW / 2 - 25, height - 10);

            Graphics2D g2r = (Graphics2D) g2.create();
            g2r.rotate(-Math.PI / 2, 14, top + plotH / 2);
            g2r.drawString("Population", 14 - 35, top + plotH / 2 + 4);
            g2r.dispose();

            if (populationHistory.size() < 2) return;

            int numPoints = populationHistory.size();
            g2.setColor(Color.BLUE);
            g2.setStroke(new BasicStroke(1.5f));
            for (int i = 1; i < numPoints; i++) {
                int x1 = left + (int)((double)(i - 1) / (numPoints - 1) * plotW);
                int y1 = top + plotH - (int)((double) populationHistory.get(i - 1) / maxPop * plotH);
                int x2 = left + (int)((double) i / (numPoints - 1) * plotW);
                int y2 = top + plotH - (int)((double) populationHistory.get(i) / maxPop * plotH);
                g2.drawLine(x1, y1, x2, y2);
            }
        }
    }

    // ----- Simulation model (inner static classes) -----

    private static class Rabbit {
        private double energy;

        public Rabbit() {
            this.energy = 1.0;
        }

        public void forage(CarrotSupply supply) {
            if (supply.takeCarrot()) energy += 1.0;
        }

        public void metabolize() { energy -= 0.5; }

        public boolean isAlive()        { return energy > 0; }
        public boolean isWillingToMate(){ return energy >= 2.0; }

        public Rabbit mateWith(Rabbit partner) {
            if (this.isWillingToMate() && partner.isWillingToMate()) {
                this.energy    -= 1.0;
                partner.energy -= 1.0;
                return new Rabbit();
            }
            return null;
        }
    }

    private static class CarrotSupply {
        private int availableCarrots;

        public CarrotSupply(int count) { availableCarrots = count; }

        public boolean takeCarrot() {
            if (availableCarrots > 0) { availableCarrots--; return true; }
            return false;
        }
    }

    private static class Ecosystem {
        private final ArrayList<Rabbit> rabbits;
        private final int carrotSupplyPerGen;

        public Ecosystem(int initialPopulation, int carrotSupplyPerGen) {
            this.carrotSupplyPerGen = carrotSupplyPerGen;
            rabbits = new ArrayList<>();
            for (int i = 0; i < initialPopulation; i++) rabbits.add(new Rabbit());
        }

        public int getPopulation() { return rabbits.size(); }

        public void simulateGeneration() {
            CarrotSupply supply = new CarrotSupply(carrotSupplyPerGen);
            Collections.shuffle(rabbits);
            for (Rabbit r : rabbits) r.forage(supply);
            for (Rabbit r : rabbits) r.metabolize();
            rabbits.removeIf(r -> !r.isAlive());

            ArrayList<Rabbit> offspring = new ArrayList<>();
            Collections.shuffle(rabbits);
            for (int i = 0; i < rabbits.size() - 1; i += 2) {
                Rabbit child = rabbits.get(i).mateWith(rabbits.get(i + 1));
                if (child != null) offspring.add(child);
            }
            rabbits.addAll(offspring);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RabbitDiscreteSimulationGUI::new);
    }
}
