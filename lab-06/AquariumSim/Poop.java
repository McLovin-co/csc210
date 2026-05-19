import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

class Poop {
    double x, y;
    double vy = 0;
    double radius = 3;
    boolean settled = false;
    double age = 0;
    boolean toRemove = false;

    Poop(double x, double y) { this.x = x; this.y = y; }
}
