import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

class Corpse {
    double x, y;
    double vy = 0;
    double radius;
    boolean settled = false;
    double age = 0;
    double decay = 0;         // 0..1
    double decayRate = 0.02;  // per second baseline (plus seaweed bites)
    boolean consumed = false; // fully eaten by plants

    Corpse(double x, double y, double radius) {
        this.x = x; this.y = y; this.radius = radius;
    }
}
