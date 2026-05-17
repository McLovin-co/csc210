import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * ArcadeGame — main entry point.
 *
 * Refactored from the original monolithic file into separate concerns:
 *   InputHandler  — keyboard flags
 *   Wolf          — player entity (animated sprite)
 *   Rabbit        — prey entity (animated sprite)
 *   Carrot        — food item
 *   Fungus        — fourth creature (eats wolves & carrots)
 *   Ecosystem     — simulation logic / collision resolution
 *   ArcadeGame    — Swing wiring, render loop, carrot-spawner thread
 *
 * Controls: Arrow keys move the wolf.
 * Goal:     Eat as many rabbits as possible.
 * Danger:   Touching a Fungus costs 3 points and respawns you at centre.
 */
public class ArcadeGame extends JPanel implements ActionListener {

    private static final int WIDTH  = 800;
    private static final int HEIGHT = 600;

    private final Ecosystem    ecosystem;
    private final InputHandler input;
    private final javax.swing.Timer gameTimer;
    private CarrotSpawner spawner;

    // ── Constructor ──────────────────────────────────────────────────────────

    public ArcadeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(0, 130, 0));

        input     = new InputHandler();
        ecosystem = new Ecosystem(WIDTH, HEIGHT);

        addKeyListener(input);
        setFocusable(true);
        requestFocusInWindow();

        gameTimer = new javax.swing.Timer(30, this);
        gameTimer.start();

        spawner = new CarrotSpawner(ecosystem, WIDTH, HEIGHT);
        spawner.start();
    }

    public void stopSpawner() { spawner.stopRunning(); }

    // ── Game loop ────────────────────────────────────────────────────────────

    @Override
    public void actionPerformed(ActionEvent e) {
        ecosystem.update(input);
        repaint();
    }

    // ── Render ───────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw entities
        for (Carrot c  : ecosystem.carrots) c.draw(g);
        for (Rabbit r  : ecosystem.rabbits) r.draw(g);
        for (Fungus f  : ecosystem.fungi)   f.draw(g);
        ecosystem.wolf.draw(g);

        // HUD
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("Score:   " + ecosystem.getScore(),        10, 20);
        g.drawString("Rabbits: " + ecosystem.getRabbitCount(),  10, 40);
        g.drawString("Carrots: " + ecosystem.getCarrotCount(),  10, 60);
        g.drawString("Fungus:  " + ecosystem.getFungusCount(),  10, 80);

        // Controls reminder
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.drawString("Arrow keys = move wolf   |   Avoid purple Fungus!", 10, HEIGHT - 10);
    }

    // ── Inner class: CarrotSpawner thread ────────────────────────────────────

    /**
     * Background thread that spawns new carrots every ~2 seconds.
     * Tries to fetch true-random coordinates from random.org; falls back to
     * java.util.Random when the network is unavailable.
     */
    private static class CarrotSpawner extends Thread {
        private volatile boolean running = true;
        private final Ecosystem ecosystem;
        private final int worldW, worldH;
        private final java.util.Random rand = new java.util.Random();

        CarrotSpawner(Ecosystem ecosystem, int worldW, int worldH) {
            this.ecosystem = ecosystem;
            this.worldW    = worldW;
            this.worldH    = worldH;
            setDaemon(true);
        }

        public void stopRunning() { running = false; interrupt(); }

        @Override
        public void run() {
            while (running) {
                int spawnX, spawnY;
                try {
                    URL url = URI.create(
                            "https://www.random.org/cgi-bin/randbyte?nbytes=4&format=h"
                    ).toURL();
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(3000);
                    conn.setReadTimeout(3000);
                    conn.setRequestMethod("GET");
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String hex = in.readLine().replaceAll("\\s+", "");
                    in.close();
                    conn.disconnect();
                    spawnX = Integer.parseInt(hex.substring(0, 4), 16) % (worldW - 12);
                    spawnY = Integer.parseInt(hex.substring(4, 8), 16) % (worldH - 12);
                } catch (Exception e) {
                    spawnX = rand.nextInt(worldW - 12);
                    spawnY = rand.nextInt(worldH - 12);
                }

                final int fx = spawnX, fy = spawnY;
                SwingUtilities.invokeLater(() -> ecosystem.spawnCarrot(fx, fy));

                try { Thread.sleep(2000); } catch (InterruptedException ex) { break; }
            }
        }
    }

    // ── Main ─────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Wolf vs. Rabbits (+ Fungus)");
            ArcadeGame game = new ArcadeGame();
            frame.add(game);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override public void windowClosing(WindowEvent e) { game.stopSpawner(); }
            });
            frame.setVisible(true);
        });
    }
}
