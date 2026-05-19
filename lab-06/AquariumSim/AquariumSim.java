import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class AquariumSim extends JFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AquariumSim sim = new AquariumSim();
            sim.setVisible(true);
        });
    }

    public AquariumSim() {
        super("Aquarium Simulation - Decomposition, Appetite, Smooth Gliding");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 720);
        setLocationByPlatform(true);

        AquariumPanel panel = new AquariumPanel(960, 620);
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(panel.buildHUD(), BorderLayout.SOUTH);
    }
}

/* ===================== Panel / World ===================== */
