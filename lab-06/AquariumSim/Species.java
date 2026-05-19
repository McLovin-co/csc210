import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

class Species {
    final Color baseColor;
    final Color finTint;
    final Color patternColor;
    final SpeciesPattern pattern;
    final boolean tailLong;
    final boolean tailWide;

    Species(Color baseColor, Color finTint, Color patternColor, SpeciesPattern pattern, boolean tailLong, boolean tailWide) {
        this.baseColor = baseColor;
        this.finTint = finTint;
        this.patternColor = patternColor;
        this.pattern = pattern;
        this.tailLong = tailLong;
        this.tailWide = tailWide;
    }

    static Species randomSpeciesFor(FishType type, Random rng) {
        switch (type) {
            case MID:
                switch (rng.nextInt(4)) {
                    case 0: return new Species(new Color(255, 180, 70), new Color(255, 230, 160), new Color(180, 110, 20), SpeciesPattern.STRIPES, false, true);
                    case 1: return new Species(new Color(220, 90, 90), new Color(255, 190, 190), new Color(140, 30, 30), SpeciesPattern.SPOTS, true, false);
                    case 2: return new Species(new Color(120, 180, 255), new Color(200, 230, 255), new Color(60, 100, 180), SpeciesPattern.STRIPES, true, true);
                    default: return new Species(new Color(240, 220, 120), new Color(255, 240, 180), new Color(170, 150, 60), SpeciesPattern.NONE, false, false);
                }
            case BOTTOM:
                switch (rng.nextInt(3)) {
                    case 0: return new Species(new Color(190, 170, 110), new Color(220, 200, 150), new Color(120, 100, 70), SpeciesPattern.SPOTS, false, true);
                    case 1: return new Species(new Color(170, 150, 100), new Color(200, 180, 140), new Color(120, 100, 70), SpeciesPattern.NONE, false, false);
                    default: return new Species(new Color(160, 140, 120), new Color(210, 190, 170), new Color(100, 80, 60), SpeciesPattern.STRIPES, false, false);
                }
            case ALGAE:
                switch (rng.nextInt(3)) {
                    case 0: return new Species(new Color(100, 210, 120), new Color(180, 255, 200), new Color(60, 150, 70), SpeciesPattern.NONE, false, true);
                    case 1: return new Species(new Color(90, 200, 170), new Color(180, 250, 230), new Color(40, 140, 120), SpeciesPattern.STRIPES, true, false);
                    default: return new Species(new Color(120, 220, 110), new Color(200, 255, 190), new Color(70, 160, 60), SpeciesPattern.SPOTS, false, false);
                }
            default:
                return new Species(new Color(200, 200, 200), new Color(220, 220, 220), new Color(150, 150, 150), SpeciesPattern.NONE, false, false);
        }
    }
}
