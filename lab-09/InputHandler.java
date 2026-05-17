import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * InputHandler — translates raw KeyEvents into simple boolean flags that game
 * objects can poll each tick.  Implements KeyListener so it can be registered
 * directly on any Swing component.
 */
public class InputHandler implements KeyListener {

    private boolean up, down, left, right;

    // ── KeyListener callbacks ────────────────────────────────────────────────

    @Override
    public void keyPressed(KeyEvent e) {
        setFlag(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        setFlag(e.getKeyCode(), false);
    }

    @Override
    public void keyTyped(KeyEvent e) { /* not used */ }

    // ── Public accessors ─────────────────────────────────────────────────────

    public boolean isUp()    { return up; }
    public boolean isDown()  { return down; }
    public boolean isLeft()  { return left; }
    public boolean isRight() { return right; }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void setFlag(int keyCode, boolean value) {
        switch (keyCode) {
            case KeyEvent.VK_UP:    up    = value; break;
            case KeyEvent.VK_DOWN:  down  = value; break;
            case KeyEvent.VK_LEFT:  left  = value; break;
            case KeyEvent.VK_RIGHT: right = value; break;
        }
    }
}
