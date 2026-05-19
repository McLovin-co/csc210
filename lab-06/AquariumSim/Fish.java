import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

class Fish {
    final FishType type;
    final Species species;

    // Kinematics
    double x, y;
    double vx, vy;
    double wanderVX;
    double size;          // visual scale
    double maxSize;
    double speed;

    // Appetite & metabolism
    double hunger;        // 0..1; higher is hungrier
    double hungerRate;    // per second
    double eatThreshold;  // must exceed to pursue/eat
    double stomachTimer;  // counts down to poop

    // Lifecycle
    double ageSeconds;
    double lifespanSeconds;
    double growthPerMeal;
    double pendingMealGrowth = 0;
    boolean alive = true;
    boolean convertedToCorpse = false;
    double scareTimer = 0;

    // Smooth idle gliding
    double preferredY;    // target depth
    double depthBand;     // slack band around preferred depth
    double depthKp = 0.35, depthKd = 0.25;
    double glideT = 0.0;
    double glideOmega;    // horizontal undulation frequency
    double glideAmpX;     // undulation amplitude

    Fish(FishType type, double size, Rectangle tankBounds, Random rng) {
        this.type = type;
        this.species = Species.randomSpeciesFor(type, rng);
        this.size = size;

        double waterTopY = tankBounds.y + 50; // FEED_TOP_MARGIN (air band)
        double waterBotY = tankBounds.y + tankBounds.height - 10;

        switch (type) {
            case MID:
                maxSize = size * 2.0;
                speed = 80 + rng.nextDouble() * 40;
                x = tankBounds.getCenterX() + rng.nextDouble() * 120 - 60;
                y = waterTopY + (waterBotY - waterTopY) * (0.35 + rng.nextDouble() * 0.30);
                wanderVX = (rng.nextDouble() * 2 - 1) * 20;
                hunger = 0.5;
                hungerRate = 0.02 + rng.nextDouble() * 0.03;
                eatThreshold = 0.35;
                lifespanSeconds = 180 + rng.nextDouble() * 240;
                growthPerMeal = 0.30;

                preferredY = y;
                depthBand = 20 + rng.nextDouble() * 20;
                glideOmega = 0.6 + rng.nextDouble() * 0.4;
                glideAmpX = 20 + rng.nextDouble() * 25;
                break;
            case BOTTOM:
                maxSize = size * 1.8;
                speed = 70 + rng.nextDouble() * 30;
                x = tankBounds.getCenterX() + rng.nextDouble() * 140 - 70;
                y = waterBotY - 30;
                wanderVX = (rng.nextDouble() * 2 - 1) * 15;
                hunger = 0.4;
                hungerRate = 0.015 + rng.nextDouble() * 0.025;
                eatThreshold = 0.30;
                lifespanSeconds = 210 + rng.nextDouble() * 300;
                growthPerMeal = 0.25;

                preferredY = waterBotY - 25;
                depthBand = 15 + rng.nextDouble() * 10;
                glideOmega = 0.5 + rng.nextDouble() * 0.3;
                glideAmpX = 18 + rng.nextDouble() * 22;
                break;
            case ALGAE:
                maxSize = size * 1.7;
                speed = 85 + rng.nextDouble() * 35;
                x = tankBounds.getCenterX() + rng.nextDouble() * 160 - 80;
                y = waterTopY + (waterBotY - waterTopY) * (0.45 + rng.nextDouble() * 0.25);
                wanderVX = (rng.nextDouble() * 2 - 1) * 18;
                hunger = 0.3;
                hungerRate = 0.012 + rng.nextDouble() * 0.02;
                eatThreshold = 0.25;
                lifespanSeconds = 240 + rng.nextDouble() * 300;
                growthPerMeal = 0.20;

                preferredY = y;
                depthBand = 22 + rng.nextDouble() * 18;
                glideOmega = 0.55 + rng.nextDouble() * 0.35;
                glideAmpX = 18 + rng.nextDouble() * 22;
                break;
            default:
                break;
        }
    }
}
