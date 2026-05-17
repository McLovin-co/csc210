import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * Rabbit — autonomous prey that bounces around the world, eats Carrots to
 * reproduce, and is eaten by the Wolf.
 *
 * Animation: uses the same 4-frame spritesheet as the Wolf but tinted white
 * so it is visually distinct.  Falls back to a white oval when the sheet is
 * missing.
 */
public class Rabbit {

    // ── Shared resources (loaded once for all instances) ─────────────────────
    private static BufferedImage spriteSheet;
    private static boolean sheetLoaded = false;

    static {
        try {
            spriteSheet = ImageIO.read(new File("resources/spriteSheet.png"));
            sheetLoaded = true;
        } catch (IOException e) {
            System.err.println("Rabbit: spritesheet not found, using fallback shape.");
        }
    }

    // ── Shared Random ────────────────────────────────────────────────────────
    private static final Random RAND = new Random();

    // ── Position / size ──────────────────────────────────────────────────────
    public int x, y;
    public final int size = 24;

    // ── Velocity ─────────────────────────────────────────────────────────────
    private int dx, dy;

    // ── Sprite animation ─────────────────────────────────────────────────────
    private static final int FRAME_W      = 64;
    private static final int FRAME_H      = 64;
    private static final int TOTAL_FRAMES = 4;
    private int currentFrame = 0;
    private int frameTicker  = 0;
    private static final int FRAME_DELAY  = 6; // ticks per frame

    // ── Constructor ──────────────────────────────────────────────────────────

    public Rabbit(int x, int y) {
        this.x = x;
        this.y = y;
        // Non-zero random velocity
        do {
            dx = RAND.nextInt(5) - 2;
            dy = RAND.nextInt(5) - 2;
        } while (dx == 0 && dy == 0);
    }

    // ── Update ───────────────────────────────────────────────────────────────

    public void move(int worldW, int worldH) {
        x += dx;
        y += dy;

        // Bounce off walls
        if (x < 0 || x > worldW - size) { dx = -dx; x = Math.max(0, Math.min(worldW - size, x)); }
        if (y < 0 || y > worldH - size) { dy = -dy; y = Math.max(0, Math.min(worldH - size, y)); }

        // Advance animation frame
        frameTicker++;
        if (frameTicker >= FRAME_DELAY) {
            frameTicker = 0;
            currentFrame = (currentFrame + 1) % TOTAL_FRAMES;
        }
    }

    // ── Render ───────────────────────────────────────────────────────────────

    public void draw(Graphics g) {
        if (sheetLoaded && spriteSheet != null) {
            int sx = currentFrame * FRAME_W;
            // Draw with a white tint by painting a tinted copy
            g.drawImage(spriteSheet,
                        x, y, x + size, y + size,
                        sx, 0, sx + FRAME_W, FRAME_H,
                        null);
            // Overlay a semi-transparent white tint so rabbits look different from wolf
            g.setColor(new Color(255, 255, 255, 100));
            g.fillRect(x, y, size, size);
        } else {
            g.setColor(Color.WHITE);
            g.fillOval(x, y, size, size);
        }
    }

    // ── Collision ────────────────────────────────────────────────────────────

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }
}
