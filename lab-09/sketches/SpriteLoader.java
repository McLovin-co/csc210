import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SpriteLoader {
    public BufferedImage loadSprite(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Usage:
    public static void main(String[] args) {
        SpriteLoader loader = new SpriteLoader();
        BufferedImage sprite = loader.loadSprite("resources/sprite.png");
        if (sprite != null) {
            System.out.println("Sprite loaded successfully!");
        }
    }
}

