import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class CarrotRabbitSimulation extends JFrame {

    final int SIM_WIDTH = 800, SIM_HEIGHT = 600;
    final int GRAPH_WIDTH = 300, GRAPH_HEIGHT = 600;

    final int rabbitCarryingCapacity = 50;
    final int rabbitFecundity = 3;

    int initialRabbits = 5;
    int carrotMax = 20;
    int carrotRefillInterval = 500;

    CardLayout cardLayout = new CardLayout();
    JPanel cards = new JPanel(cardLayout);

    SetupPanel setupPanel = new SetupPanel();
    SimulationContainer simContainer;

    public CarrotRabbitSimulation() {
        super("Ecosystem Simulation");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        cards.add(setupPanel, "Setup");
        getContentPane().add(cards);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ---------------- SetupPanel ------------------
    class SetupPanel extends JPanel {
        JTextField rabbitField;
        JTextField carrotMaxField;
        JTextField carrotIntervalField;
        JButton playButton;

        public SetupPanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel titleLabel = new JLabel("Ecosystem Simulation Setup", JLabel.CENTER);
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            add(titleLabel, gbc);

            gbc.gridwidth = 1;
            gbc.gridx = 0; gbc.gridy = 1;
            add(new JLabel("Initial Rabbits:"), gbc);
            rabbitField = new JTextField("5", 10);
            gbc.gridx = 1;
            add(rabbitField, gbc);

            // Rows are now consecutive — no gap between rabbits and carrot max.
            gbc.gridx = 0; gbc.gridy = 2;
            add(new JLabel("Carrot Maximum:"), gbc);
            carrotMaxField = new JTextField("20", 10);
            gbc.gridx = 1;
            add(carrotMaxField, gbc);

            gbc.gridx = 0; gbc.gridy = 3;
            add(new JLabel("Carrot Refill Interval (ticks):"), gbc);
            carrotIntervalField = new JTextField("500", 10);
            gbc.gridx = 1;
            add(carrotIntervalField, gbc);

            playButton = new JButton("Play");
            gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
            add(playButton, gbc);

            playButton.addActionListener(e -> {
                try {
                    initialRabbits = Integer.parseInt(rabbitField.getText());
                    carrotMax = Integer.parseInt(carrotMaxField.getText());
                    carrotRefillInterval = Integer.parseInt(carrotIntervalField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(SetupPanel.this,
                            "Please enter valid integer values.",
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                simContainer = new SimulationContainer(initialRabbits, carrotMax, carrotRefillInterval);
                cards.add(simContainer, "Simulation");
                cardLayout.show(cards, "Simulation");
                simContainer.startSimulation();
                CarrotRabbitSimulation.this.pack();
            });
        }
    }

    // ---------------- SimulationContainer ------------------
    class SimulationContainer extends JPanel {
        SimulationArea simArea;
        GraphPanel rabbitGraph;
        javax.swing.Timer mainTimer;
        int tickCount = 0;

        public SimulationContainer(int initRabbits, int carrotMax, int carrotInterval) {
            setLayout(new BorderLayout());

            simArea = new SimulationArea(initRabbits, carrotMax, carrotInterval);
            simArea.setPreferredSize(new Dimension(SIM_WIDTH, SIM_HEIGHT));
            add(simArea, BorderLayout.CENTER);

            JPanel graphContainer = new JPanel(new GridLayout(2, 1));
            rabbitGraph = new GraphPanel("Rabbits");
            rabbitGraph.setPreferredSize(new Dimension(GRAPH_WIDTH, GRAPH_HEIGHT / 2));
            graphContainer.add(rabbitGraph);
            add(graphContainer, BorderLayout.EAST);

            // Pause/resume and back-to-setup controls
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton pauseButton = new JButton("Pause");
            pauseButton.addActionListener(e -> {
                if (mainTimer.isRunning()) {
                    mainTimer.stop();
                    pauseButton.setText("Resume");
                } else {
                    mainTimer.start();
                    pauseButton.setText("Pause");
                }
            });
            JButton backButton = new JButton("Back to Setup");
            backButton.addActionListener(e -> {
                mainTimer.stop();
                cardLayout.show(cards, "Setup");
            });
            buttonPanel.add(pauseButton);
            buttonPanel.add(backButton);
            add(buttonPanel, BorderLayout.NORTH);
        }

        public void startSimulation() {
            mainTimer = new javax.swing.Timer(50, e -> {
                tickCount++;
                simArea.updateSimulation(tickCount);
                rabbitGraph.updateData(tickCount, simArea.getRabbitCount());
                simArea.repaint();
                rabbitGraph.repaint();
            });
            mainTimer.start();
        }
    }

    // ---------------- SimulationArea ------------------
    class SimulationArea extends JPanel {
        int width = SIM_WIDTH, height = SIM_HEIGHT;
        List<Carrot> carrots;
        List<Rabbit> rabbits;
        Random rand = new Random();

        int carrotMax;
        int carrotRefillInterval;

        public SimulationArea(int initRabbits, int carrotMax, int carrotRefillInterval) {
            setBackground(new Color(30, 100, 30));
            carrots = new ArrayList<>();
            rabbits = new ArrayList<>();
            this.carrotMax = carrotMax;
            this.carrotRefillInterval = carrotRefillInterval;
            for (int i = 0; i < initRabbits; i++) {
                rabbits.add(new Rabbit(rand.nextInt(width), rand.nextInt(height)));
            }
            refillCarrots();
        }

        public void updateSimulation(int tick) {
            if (tick % carrotRefillInterval == 0) refillCarrots();

            List<Rabbit> newRabbits = new ArrayList<>();

            Iterator<Rabbit> rit = rabbits.iterator();
            while (rit.hasNext()) {
                Rabbit r = rit.next();
                r.move();
                r.age++;
                if (r.age > r.lifespan) { rit.remove(); continue; }

                Iterator<Carrot> cit = carrots.iterator();
                while (cit.hasNext()) {
                    Carrot c = cit.next();
                    if (r.intersects(c)) {
                        cit.remove();
                        // logisticFactor goes negative when population exceeds
                        // rabbitCarryingCapacity, making offspring = 0 — this is
                        // exactly how the carrying-capacity ceiling is enforced here.
                        double logisticFactor = 1 - ((double) rabbits.size() / rabbitCarryingCapacity);
                        int offspring = (int) (rabbitFecundity * logisticFactor);
                        for (int j = 0; j < offspring; j++) newRabbits.add(new Rabbit(r.x, r.y));
                        break;
                    }
                }
            }
            rabbits.addAll(newRabbits);
        }

        private void refillCarrots() {
            carrots.clear();
            for (int i = 0; i < carrotMax; i++) {
                carrots.add(new Carrot(rand.nextInt(width - 10), rand.nextInt(height - 10)));
            }
        }

        public int getRabbitCount() { return rabbits.size(); }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.ORANGE);
            for (Carrot c : carrots) g.fillRect(c.x, c.y, c.size, c.size);
            g.setColor(Color.WHITE);
            for (Rabbit r : rabbits) g.fillOval((int) r.x, (int) r.y, r.size, r.size);
        }

        class Carrot {
            int x, y, size = 10;
            public Carrot(int x, int y) { this.x = x; this.y = y; }
        }

        class Rabbit {
            double x, y;
            int size = 15, age = 0, lifespan = 500;
            double dx, dy;
            final double speed = 2.0;

            public Rabbit(double x, double y) {
                this.x = x; this.y = y;
                double angle = rand.nextDouble() * 2 * Math.PI;
                dx = speed * Math.cos(angle);
                dy = speed * Math.sin(angle);
            }

            public void move() {
                if (rand.nextDouble() < 0.05) {
                    double perturb = (rand.nextDouble() - 0.5) * (Math.PI / 4);
                    double spd = Math.sqrt(dx * dx + dy * dy);
                    double angle = Math.atan2(dy, dx) + perturb;
                    dx = spd * Math.cos(angle);
                    dy = spd * Math.sin(angle);
                }
                x += dx; y += dy;
                if (x < 0) { x = 0; dx = -dx; }
                if (x > width  - size) { x = width  - size; dx = -dx; }
                if (y < 0) { y = 0; dy = -dy; }
                if (y > height - size) { y = height - size; dy = -dy; }
            }

            public boolean intersects(Carrot c) {
                return new Rectangle((int) x, (int) y, size, size)
                        .intersects(new Rectangle(c.x, c.y, c.size, c.size));
            }
        }
    }

    // ---------------- GraphPanel ------------------
    class GraphPanel extends JPanel {
        String species;
        List<Integer> timeData = new ArrayList<>();
        List<Integer> popData  = new ArrayList<>();
        private int maxPopSeen = 1;
        // Cap the rolling window so the graph stays readable over long runs
        // and memory use stays bounded.
        private static final int MAX_DATA_POINTS = 2000;

        public GraphPanel(String species) {
            this.species = species;
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createTitledBorder(species + " Population"));
        }

        public void updateData(int t, int pop) {
            timeData.add(t);
            popData.add(pop);
            if (pop > maxPopSeen) maxPopSeen = pop;
            if (timeData.size() > MAX_DATA_POINTS) {
                timeData.remove(0);
                popData.remove(0);
            }
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            int w = getWidth(), h = getHeight();
            int left = 40, right = 10, top = 10, bottom = 30;
            int plotW = w - left - right;
            int plotH = h - top - bottom;

            // Grid lines and y-axis tick marks
            int maxPop = Math.max(maxPopSeen, 1);
            g2.setFont(g2.getFont().deriveFont(10f));
            for (int i = 0; i <= 4; i++) {
                int val = i * maxPop / 4;
                int ty  = top + plotH - (int)((double) val / maxPop * plotH);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawLine(left, ty, left + plotW, ty);
                g2.setColor(Color.BLACK);
                g2.drawLine(left - 3, ty, left, ty);
                g2.drawString(String.valueOf(val), 2, ty + 3);
            }

            // Axes
            g2.setColor(Color.BLACK);
            g2.drawLine(left, top + plotH, left + plotW, top + plotH);
            g2.drawLine(left, top, left, top + plotH);

            if (timeData.size() < 2) return;

            int n = timeData.size();
            int maxTime = timeData.get(n - 1);
            g2.setColor(species.equals("Rabbits") ? Color.RED : Color.BLUE);
            for (int i = 1; i < n; i++) {
                int x1 = left + (int)((double) timeData.get(i - 1) / maxTime * plotW);
                int y1 = top + plotH - (int)((double) popData.get(i - 1) / maxPop * plotH);
                int x2 = left + (int)((double) timeData.get(i) / maxTime * plotW);
                int y2 = top + plotH - (int)((double) popData.get(i) / maxPop * plotH);
                g2.drawLine(x1, y1, x2, y2);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CarrotRabbitSimulation::new);
    }
}
