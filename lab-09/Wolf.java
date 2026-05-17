import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Wolf — the player-controlled predator.
 *
 * Moves via arrow-key input (polled through InputHandler) and is animated
 * using a 4-frame spritesheet (resources/spriteSheet.png).  Falls back to
 * drawing a plain dark-grey oval when the spritesheet cannot be loaded.
 */
public class Wolf {

    // ── Position / size ──────────────────────────────────────────────────────
    public int x, y;
    public final int size = 40;

    // ── Movement ─────────────────────────────────────────────────────────────
    private static final int SPEED = 5;

    // ── Sprite animation ─────────────────────────────────────────────────────
    private BufferedImage spriteSheet;
    private static final int FRAME_W      = 64;
    private static final int FRAME_H      = 64;
    private static final int TOTAL_FRAMES = 4;
    private int currentFrame  = 0;
    private int frameTicker   = 0;          // counts game ticks between frame advances
    private static final int FRAME_DELAY  = 8; // ticks per frame (≈4 fps at 30 ms/tick)

    // ── Constructor ──────────────────────────────────────────────────────────

    public Wolf(int x, int y) {
        this.x = x;
        this.y = y;
        try {
            spriteSheet = ImageIO.read(new File("resources/spriteSheet.png"));
        } catch (IOException e) {
            System.err.println("Wolf: spritesheet not found, using fallback shape.");
        }
    }

    // ── Update ───────────────────────────────────────────────────────────────

    /**
     * Moves the wolf based on held keys and advances the animation frame.
     * Call once per game tick.
     */
    public void update(InputHandler input, int worldW, int worldH) {
        if (input.isUp())    y -= SPEED;
        if (input.isDown())  y += SPEED;
        if (input.isLeft())  x -= SPEED;
        if (input.isRight()) x += SPEED;

        // Clamp to world bounds
        x = Math.max(0, Math.min(worldW - size, x));
        y = Math.max(0, Math.min(worldH - size, y));

        // Advance animation only while moving
        if (input.isUp() || input.isDown() || input.isLeft() || input.isRight()) {
            frameTicker++;
            if (frameTicker >= FRAME_DELAY) {
                frameTicker = 0;
                currentFrame = (currentFrame + 1) % TOTAL_FRAMES;
            }
        }
    }

    // ── Render ───────────────────────────────────────────────────────────────

    public void draw(Graphics g) {
        if (spriteSheet != null) {
            int sx = currentFrame * FRAME_W;
            g.drawImage(spriteSheet,
                        x, y, x + size, y + size,
                        sx, 0, sx + FRAME_W, FRAME_H,
                        null);
        } else {
            // Fallback: dark grey oval
            g.setColor(java.awt.Color.DARK_GRAY);
            g.fillOval(x, y, size, size);
        }
    }

    // ── Collision helper ─────────────────────────────────────────────────────

    public java.awt.Rectangle getBounds() {
        return new java.awt.Rectangle(x, y, size, size);
    }
}
