import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Carrot — a stationary food item.  Rabbits eat carrots to reproduce;
 * Fungus also feeds on carrots.  Drawn as an orange square.
 */
public class Carrot {

    public int x, y;
    public final int size = 12;

    public Carrot(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g) {
        g.setColor(Color.ORANGE);
        g.fillRect(x, y, size, size);
        g.setColor(Color.YELLOW);
        // Small highlight
        g.drawLine(x + 2, y + 2, x + size - 3, y + 2);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }
}
