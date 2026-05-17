import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class SpriteAnimationPanel extends JPanel {
    private BufferedImage spriteSheet;
    private int frameWidth = 64, frameHeight = 64;
    private int currentFrame = 0;
    private int totalFrames = 4; // Assuming 4 frames in the sprite sheet

    public SpriteAnimationPanel() {
        // Load your sprite sheet (error handling omitted for brevity)
        try {
            spriteSheet = ImageIO.read(new File("resources/spriteSheet.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Timer animationTimer = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentFrame = (currentFrame + 1) % totalFrames;
                repaint();
            }
        });
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Calculate source rectangle for current frame
        int sx = currentFrame * frameWidth;
        int sy = 0;
        g.drawImage(spriteSheet,
                    50, 50, 50 + frameWidth, 50 + frameHeight, // Destination rectangle
                    sx, sy, sx + frameWidth, sy + frameHeight, // Source rectangle
                    null);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sprite Animation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new SpriteAnimationPanel());
        frame.setSize(300, 300);
        frame.setVisible(true);
    }
}

