import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

class Seaweed {
    double x, y;
    double h;
    double phase;
    double cleanRadius = 42;
    double eatRate = 1.0; // multiplier for corpse-eating

    Seaweed(double x, double y, double h) {
        this.x = x; this.y = y; this.h = h;
        this.phase = new Random().nextDouble() * Math.PI * 2;
    }
}

/* ===================== Small Geometry Helper ===================== */
