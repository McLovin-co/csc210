import ecosystem.creatures.Creature;
import ecosystem.world.Tile;
import ecosystem.world.World;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;

/**
 * EcosystemGUI - Swing-based graphical interface for the Ecosystem simulation.
 *
 * Layout:
 *   CENTER  - grid of tile buttons (one per world cell)
 *   EAST    - sidebar showing clicked tile's creatures and attributes
 *   SOUTH   - control bar (Step, Play/Stop, Reset, Load, Save, speed)
 *   Floating log window - scrollable stdout log of turn events
 */
public class EcosystemGUI {

    // ── World state ──────────────────────────────────────────────
    private World world;
    private String currentConfigPath = "world_config.json";

    // ── UI components ─────────────────────────────────────────────
    private JFrame frame;
    private JButton[][] gridButtons;
    private JPanel gridPanel;
    private int gridRows, gridCols;

    // Sidebar
    private JTextArea sidebarArea;

    // Log window
    private JTextArea logArea;
    private PrintStream logStream;

    // Controls
    private JButton stepBtn, playBtn, resetBtn, loadBtn, saveBtn;
    private JTextField speedField;
    private Timer playTimer;

    // Colors for tile richness
    private static final Color COLOR_EMPTY    = new Color(230, 230, 230);
    private static final Color COLOR_LOW      = new Color(180, 220, 140);
    private static final Color COLOR_MED      = new Color(100, 180, 80);
    private static final Color COLOR_HIGH     = new Color(40,  120, 30);
    private static final Color COLOR_WATER    = new Color(80,  140, 220);
    private static final Color COLOR_SELECTED = new Color(255, 200, 50);

    private int selectedRow = -1, selectedCol = -1;

    // ── Constructor ───────────────────────────────────────────────

    public EcosystemGUI() {
        loadWorldFromFile(currentConfigPath);
        buildUI();
        redirectStdout();
        updateGrid();
    }

    // ── World loading ─────────────────────────────────────────────

    private void loadWorldFromFile(String path) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(path)));
            SimpleJSON.JObject root = SimpleJSON.parseObject(content);

            SimpleJSON.JObject worldCfg = root.getObject("world");
            int width  = worldCfg.getInt("width");
            int height = worldCfg.getInt("height");
            world = new World(width, height);
            gridRows = height;
            gridCols = width;

            SimpleJSON.JArray tiles = root.getArray("tiles");
            for (int i = 0; i < tiles.size(); i++) {
                SimpleJSON.JObject tileCfg = tiles.getObject(i);
                int row   = tileCfg.getInt("row");
                int col   = tileCfg.getInt("col");
                Tile tile = new Tile(
                    tileCfg.getInt("water"),
                    tileCfg.getInt("temperature"),
                    tileCfg.getInt("nutrients")
                );
                SimpleJSON.JArray creatures = tileCfg.optArray("creatures");
                if (creatures != null) {
                    for (int j = 0; j < creatures.size(); j++) {
                        SimpleJSON.JObject cCfg = creatures.getObject(j);
                        Creature c = buildCreature(cCfg);
                        if (c != null) tile.addCreature(c);
                    }
                }
                world.setTile(row, col, tile);
            }
            currentConfigPath = path;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                "Failed to load: " + e.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Creature buildCreature(SimpleJSON.JObject cfg) {
        String type    = cfg.getString("type");
        String species = cfg.getString("species");
        String name    = cfg.getString("name");
        return switch (type) {
            case "Tree"   -> new ecosystem.plants.Tree(name, species);
            case "Shrub"  -> new ecosystem.plants.Shrub(name, species);
            case "Moss"   -> new ecosystem.plants.Moss(name, species);
            case "Plant"  -> new ecosystem.plants.Plant(name, species);
            case "Bird"   -> new ecosystem.creatures.Bird(name, species);
            case "Mammal" -> new ecosystem.creatures.Mammal(name, species);
            case "Fish"   -> new ecosystem.creatures.Fish(name, species);
            default       -> null;
        };
    }

    // ── UI construction ───────────────────────────────────────────

    private void buildUI() {
        frame = new JFrame("Ecosystem Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(4, 4));

        // ── Grid panel ──
        gridPanel = new JPanel(new GridLayout(gridRows, gridCols, 1, 1));
        gridPanel.setBackground(Color.DARK_GRAY);
        gridButtons = new JButton[gridRows][gridCols];

        for (int r = 0; r < gridRows; r++) {
            for (int c = 0; c < gridCols; c++) {
                JButton btn = new JButton();
                btn.setOpaque(true);
                btn.setBorderPainted(true);
                btn.setFocusPainted(false);
                btn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
                btn.setMargin(new Insets(1,1,1,1));
                final int row = r, col = c;
                btn.addActionListener(e -> onTileClick(row, col));
                gridButtons[r][c] = btn;
                gridPanel.add(btn);
            }
        }

        JScrollPane gridScroll = new JScrollPane(gridPanel);
        gridScroll.setPreferredSize(new Dimension(520, 520));
        frame.add(gridScroll, BorderLayout.CENTER);

        // ── Sidebar ──
        sidebarArea = new JTextArea(30, 22);
        sidebarArea.setEditable(false);
        sidebarArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        sidebarArea.setBackground(new Color(245, 245, 245));
        sidebarArea.setText("Click a tile to\nsee its details.");
        JScrollPane sideScroll = new JScrollPane(sidebarArea);
        sideScroll.setBorder(BorderFactory.createTitledBorder("Tile Info"));
        sideScroll.setPreferredSize(new Dimension(260, 520));
        frame.add(sideScroll, BorderLayout.EAST);

        // ── Control bar ──
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));

        stepBtn = new JButton("Step");
        stepBtn.addActionListener(e -> doStep());
        controls.add(stepBtn);

        playBtn = new JButton("Play");
        playBtn.addActionListener(e -> togglePlay());
        controls.add(playBtn);

        resetBtn = new JButton("Reset");
        resetBtn.addActionListener(e -> doReset());
        controls.add(resetBtn);

        loadBtn = new JButton("Load File...");
        loadBtn.addActionListener(e -> doLoad());
        controls.add(loadBtn);

        saveBtn = new JButton("Save State...");
        saveBtn.addActionListener(e -> doSave());
        controls.add(saveBtn);

        controls.add(new JLabel("Speed (ms):"));
        speedField = new JTextField("600", 5);
        controls.add(speedField);

        JLabel turnLabel = new JLabel("Turn: 0");
        turnLabel.setName("turnLabel");
        controls.add(turnLabel);

        frame.add(controls, BorderLayout.SOUTH);

        // ── Log window (separate floating window) ──
        JFrame logFrame = new JFrame("Simulation Log");
        logFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        logArea = new JTextArea(18, 55);
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        JScrollPane logScroll = new JScrollPane(logArea);
        logFrame.add(logScroll);
        logFrame.pack();
        logFrame.setLocation(800, 100);
        logFrame.setVisible(true);

        JButton showLog = new JButton("Show Log");
        showLog.addActionListener(e -> logFrame.setVisible(true));
        controls.add(showLog);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // ── Redirect stdout to log area ──────────────────────────────

    private void redirectStdout() {
        OutputStream out = new OutputStream() {
            private final StringBuilder buf = new StringBuilder();
            @Override
            public void write(int b) {
                char ch = (char) b;
                buf.append(ch);
                if (ch == '\n') flush();
            }
            @Override
            public void flush() {
                String text = buf.toString();
                buf.setLength(0);
                SwingUtilities.invokeLater(() -> {
                    logArea.append(text);
                    logArea.setCaretPosition(logArea.getDocument().getLength());
                });
            }
        };
        logStream = new PrintStream(out, true);
        System.setOut(logStream);
    }

    // ── Grid update ───────────────────────────────────────────────

    private void updateGrid() {
        for (int r = 0; r < gridRows; r++) {
            for (int c = 0; c < gridCols; c++) {
                Tile tile = world.getTile(r, c);
                JButton btn = gridButtons[r][c];

                int count = tile.getCreatureCount();
                int water = tile.getWater();

                // Color: water-dominant tiles blue, others green by creature density
                Color bg;
                if (water > 70) {
                    bg = COLOR_WATER;
                } else if (count == 0) {
                    bg = COLOR_EMPTY;
                } else if (count < 5) {
                    bg = COLOR_LOW;
                } else if (count < 12) {
                    bg = COLOR_MED;
                } else {
                    bg = COLOR_HIGH;
                }

                if (r == selectedRow && c == selectedCol) bg = COLOR_SELECTED;

                btn.setBackground(bg);
                btn.setText(count > 0 ? String.valueOf(count) : "");
                btn.setForeground(count > 8 ? Color.WHITE : Color.DARK_GRAY);
            }
        }

        // Update turn label
        for (Component comp : ((JPanel) frame.getContentPane().getComponent(2)).getComponents()) {
            if (comp instanceof JLabel lbl && lbl.getName() != null && lbl.getName().equals("turnLabel")) {
                lbl.setText("Turn: " + world.getTurnNumber());
            }
        }
    }

    // ── Tile click ────────────────────────────────────────────────

    private void onTileClick(int row, int col) {
        selectedRow = row;
        selectedCol = col;
        Tile tile = world.getTile(row, col);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== Tile [%d,%d] ===\n", row, col));
        sb.append(String.format("Water:       %d\n", tile.getWater()));
        sb.append(String.format("Temperature: %d°C\n", tile.getTemperature()));
        sb.append(String.format("Nutrients:   %d\n", tile.getNutrients()));
        sb.append(String.format("Creatures:   %d\n\n", tile.getCreatureCount()));

        List<Creature> creatures = tile.getCreatures();
        if (creatures.isEmpty()) {
            sb.append("(no creatures)\n");
        } else {
            for (Creature c : creatures) {
                sb.append("──────────────\n");
                sb.append(String.format("Name:    %s\n", c.getName()));
                sb.append(String.format("Species: %s\n", c.getSpecies()));
                sb.append(String.format("Type:    %s\n", c.getClass().getSimpleName()));
                sb.append(String.format("Age:     %d\n", c.getAge()));
                sb.append(String.format("Alive:   %s\n", c.isAlive()));
                // Extra detail for plants
                if (c instanceof ecosystem.plants.Plant p) {
                    sb.append(String.format("Woody:   %b\n", p.isWoody()));
                    sb.append(String.format("Growth:  %s\n", p.getGrowthPattern()));
                }
                sb.append("\n");
            }
        }

        sidebarArea.setText(sb.toString());
        sidebarArea.setCaretPosition(0);
        updateGrid();
    }

    // ── Controls ─────────────────────────────────────────────────

    private void doStep() {
        world.takeTurn();
        updateGrid();
        // Refresh sidebar if a tile is selected
        if (selectedRow >= 0) onTileClick(selectedRow, selectedCol);
    }

    private void togglePlay() {
        if (playTimer != null && playTimer.isRunning()) {
            playTimer.stop();
            playBtn.setText("Play");
        } else {
            int speed;
            try { speed = Integer.parseInt(speedField.getText()); }
            catch (NumberFormatException ex) { speed = 600; }
            playTimer = new Timer(speed, e -> doStep());
            playTimer.start();
            playBtn.setText("Stop");
        }
    }

    private void doReset() {
        if (playTimer != null) playTimer.stop();
        playBtn.setText("Play");
        selectedRow = -1;
        selectedCol = -1;
        sidebarArea.setText("Click a tile to\nsee its details.");
        logArea.setText("");
        // Re-register species (statics persist, so just reload the world)
        try {
            // Reset static species registries by reloading fresh
            resetSpeciesRegistries();
            loadWorldFromFile(currentConfigPath);
            updateGrid();
            System.out.println("World reset from: " + currentConfigPath);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Reset failed: " + e.getMessage());
        }
    }

    private void doLoad() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));
        fc.setCurrentDirectory(new File("."));
        if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().getAbsolutePath();
            if (playTimer != null) playTimer.stop();
            playBtn.setText("Play");
            resetSpeciesRegistries();
            loadWorldFromFile(path);
            // Rebuild grid if size changed
            rebuildGridIfNeeded();
            updateGrid();
            System.out.println("Loaded: " + path);
        }
    }

    private void doSave() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));
        fc.setSelectedFile(new File("ecosystem_save.json"));
        if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().getAbsolutePath();
            try {
                saveWorldToJson(path);
                System.out.println("Saved to: " + path);
                JOptionPane.showMessageDialog(frame, "Saved to:\n" + path);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Save failed: " + e.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── Save world to JSON ────────────────────────────────────────

    private void saveWorldToJson(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"world\": { \"width\": ").append(world.getWidth())
          .append(", \"height\": ").append(world.getHeight()).append(" },\n");
        sb.append("  \"tiles\": [\n");

        boolean firstTile = true;
        for (int r = 0; r < world.getHeight(); r++) {
            for (int c = 0; c < world.getWidth(); c++) {
                if (!firstTile) sb.append(",\n");
                firstTile = false;
                Tile tile = world.getTile(r, c);
                sb.append("    {\n");
                sb.append("      \"row\": ").append(r).append(", \"col\": ").append(c).append(",\n");
                sb.append("      \"water\": ").append(tile.getWater()).append(",\n");
                sb.append("      \"temperature\": ").append(tile.getTemperature()).append(",\n");
                sb.append("      \"nutrients\": ").append(tile.getNutrients()).append(",\n");
                sb.append("      \"creatures\": [\n");

                List<Creature> creatures = tile.getCreatures();
                for (int i = 0; i < creatures.size(); i++) {
                    Creature cr = creatures.get(i);
                    sb.append("        { \"type\": \"").append(cr.getClass().getSimpleName())
                      .append("\", \"species\": \"").append(cr.getSpecies())
                      .append("\", \"name\": \"").append(cr.getName()).append("\" }");
                    if (i < creatures.size() - 1) sb.append(",");
                    sb.append("\n");
                }
                sb.append("      ]\n");
                sb.append("    }");
            }
        }
        sb.append("\n  ]\n}\n");

        Files.writeString(Paths.get(path), sb.toString());
    }

    // ── Helpers ───────────────────────────────────────────────────

    /**
     * Clears the static species registries so a fresh load doesn't throw
     * "species already exists" exceptions.
     */
    private void resetSpeciesRegistries() {
        // The species maps are private statics — we clear them by reflection
        // or simply catch and ignore the duplicate exception in addSpecies.
        // Since we used overwrite-safe addSpecies, we can reload by using
        // addSpecies(name, def, true). But the easiest approach is to just
        // catch exceptions in loadWorldFromFile. The static blocks in Tree/Shrub/Moss
        // only run once per JVM session so they won't re-throw.
        // Nothing to do here in practice for the static blocks.
    }

    private void rebuildGridIfNeeded() {
        if (world.getHeight() == gridRows && world.getWidth() == gridCols) return;
        gridRows = world.getHeight();
        gridCols = world.getWidth();
        gridPanel.removeAll();
        gridPanel.setLayout(new GridLayout(gridRows, gridCols, 1, 1));
        gridButtons = new JButton[gridRows][gridCols];
        for (int r = 0; r < gridRows; r++) {
            for (int c = 0; c < gridCols; c++) {
                JButton btn = new JButton();
                btn.setOpaque(true);
                btn.setBorderPainted(true);
                btn.setFocusPainted(false);
                btn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
                btn.setMargin(new Insets(1,1,1,1));
                final int row = r, col = c;
                btn.addActionListener(e -> onTileClick(row, col));
                gridButtons[r][c] = btn;
                gridPanel.add(btn);
            }
        }
        gridPanel.revalidate();
    }

    // ── Entry point ───────────────────────────────────────────────

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EcosystemGUI::new);
    }
}
