import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

/**
 * Fungus — the fourth creature in the ecosystem.
 *
 * Behaviour:
 *  - Drifts slowly and randomly around the world (no directed chasing).
 *  - Eats Carrots  → gains a "spore" that produces a new Fungus nearby.
 *  - Eats Wolves   → the wolf is removed from the ecosystem (it has killed
 *                    the wolf with a toxic spore).
 *  - Dies of old age after a fixed number of ticks.
 *
 * Drawn as a purple circle with a darker ring to look like a mushroom cap.
 */
public class Fungus {

    // ── Shared Random ────────────────────────────────────────────────────────
    private static final Random RAND = new Random();

    // ── Position / size ──────────────────────────────────────────────────────
    public int x, y;
    public final int size = 20;

    // ── Motion ───────────────────────────────────────────────────────────────
    private double dx, dy;
    private static final double SPEED = 0.8;

    // ── Lifecycle ────────────────────────────────────────────────────────────
    private int age      = 0;
    private final int lifespan = 800 + RAND.nextInt(400); // 800–1200 ticks
    private boolean alive = true;

    // ── Reproduction cooldown ────────────────────────────────────────────────
    private int sporeCooldown = 0;
    private static final int SPORE_DELAY = 200; // ticks between reproductions

    // ── Constructor ──────────────────────────────────────────────────────────

    public Fungus(int x, int y) {
        this.x = x;
        this.y = y;
        // Random slow drift direction
        double angle = RAND.nextDouble() * 2 * Math.PI;
        dx = SPEED * Math.cos(angle);
        dy = SPEED * Math.sin(angle);
    }

    // ── Update ───────────────────────────────────────────────────────────────

    public void update(int worldW, int worldH) {
        // Occasionally perturb direction for organic wandering
        if (RAND.nextDouble() < 0.03) {
            double perturb = (RAND.nextDouble() - 0.5) * (Math.PI / 3);
            double spd   = Math.sqrt(dx * dx + dy * dy);
            double angle = Math.atan2(dy, dx) + perturb;
            dx = spd * Math.cos(angle);
            dy = spd * Math.sin(angle);
        }

        x += (int) dx;
        y += (int) dy;

        // Bounce off walls
        if (x < 0 || x > worldW - size) { dx = -dx; x = Math.max(0, Math.min(worldW - size, x)); }
        if (y < 0 || y > worldH - size) { dy = -dy; y = Math.max(0, Math.min(worldH - size, y)); }

        age++;
        if (sporeCooldown > 0) sporeCooldown--;
        if (age > lifespan) alive = false;
    }

    // ── State ────────────────────────────────────────────────────────────────

    public boolean isAlive() { return alive; }

    /** Call when this fungus successfully eats something; returns a new
     *  offspring Fungus if the reproduction cooldown has elapsed, else null. */
    public Fungus tryReproduce() {
        if (sporeCooldown == 0) {
            sporeCooldown = SPORE_DELAY;
            int ox = x + RAND.nextInt(40) - 20;
            int oy = y + RAND.nextInt(40) - 20;
            return new Fungus(ox, oy);
        }
        return null;
    }

    // ── Render ───────────────────────────────────────────────────────────────

    public void draw(Graphics g) {
        // Cap / body (purple)
        g.setColor(new Color(128, 0, 200));
        g.fillOval(x, y, size, size);
        // Darker ring
        g.setColor(new Color(80, 0, 130));
        g.drawOval(x + 2, y + 2, size - 4, size - 4);
        // Bright spots to look like mushroom markings
        g.setColor(new Color(200, 150, 255));
        g.fillOval(x + 4, y + 4, 5, 5);
        g.fillOval(x + 11, y + 6, 4, 4);
    }

    // ── Collision ────────────────────────────────────────────────────────────

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }
}
