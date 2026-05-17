import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SpriteLoader2 {

    public BufferedImage loadSprite(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Custom JPanel to display the sprite
    private static class SpritePanel extends JPanel {
        private final BufferedImage sprite;

        public SpritePanel(BufferedImage sprite) {
            this.sprite = sprite;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (sprite != null) {
                g.drawImage(sprite, 0, 0, this);
            }
        }

        @Override
        public Dimension getPreferredSize() {
            if (sprite != null) {
                return new Dimension(sprite.getWidth(), sprite.getHeight());
            } else {
                return super.getPreferredSize();
            }
        }
    }

    public static void main(String[] args) {
        SpriteLoader2 loader = new SpriteLoader2();
        BufferedImage sprite = loader.loadSprite("resources/sprite.png");
        if (sprite != null) {
            System.out.println("Sprite loaded successfully!");

            // Create a JFrame to display the sprite
            JFrame frame = new JFrame("Sprite Display");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Create and add the sprite panel
            SpritePanel panel = new SpritePanel(sprite);
            frame.add(panel);

            frame.pack();
            frame.setLocationRelativeTo(null); // Center the window
            frame.setVisible(true);
        } else {
            System.out.println("Failed to load sprite.");
        }
    }
}

