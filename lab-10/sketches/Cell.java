import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Path2D;

/**
 * A Cell represents one hexagon on the board.
 * The axial coordinates (q, r) determine the position.
 *
 * Shared by HexagonGame, HexagonGameClient, and HexagonGameP2P.
 */
public class Cell {
    public int q, r;        // axial coordinates
    public int state;       // 0 = empty, 1 = player1, 2 = player2
    public Polygon hex;     // the polygon points (for hit detection)
    public Shape hexPath;   // A Path2D version for smoother hit-testing and drawing.
    public int centerX, centerY; // Pixel coordinates of the cell center

    /**
     * Constructs a cell from its axial coordinates.
     * The cell's center is computed using the axial-to-pixel conversion for pointy-topped hexagons.
     * offsetX and offsetY are added to center the board.
     */
    public Cell(int q, int r, int hexSize, int offsetX, int offsetY) {
        this.q = q;
        this.r = r;
        this.state = 0;
        // For pointy-topped hexagons, conversion from axial (q, r) to pixel coordinates:
        // x = hexSize * sqrt(3) * (q + r/2)
        // y = hexSize * 3/2 * r
        double x = hexSize * Math.sqrt(3) * (q + r / 2.0);
        double y = hexSize * 1.5 * r;
        centerX = (int) Math.round(x) + offsetX;
        centerY = (int) Math.round(y) + offsetY;
        // Create the hexagon polygon for this cell.
        hex = createHexagon(centerX, centerY, hexSize);
        Path2D.Double path = new Path2D.Double();
        path.moveTo(hex.xpoints[0], hex.ypoints[0]);
        for (int i = 1; i < hex.npoints; i++) {
            path.lineTo(hex.xpoints[i], hex.ypoints[i]);
        }
        path.closePath();
        hexPath = path;
    }

    /**
     * Create a regular pointy-topped hexagon centered at (centerX, centerY).
     */
    private Polygon createHexagon(int centerX, int centerY, int size) {
        Polygon poly = new Polygon();
        // For a pointy-topped hexagon, the vertices are at angles 30°, 90°, 150°, 210°, 270°, 330°
        for (int i = 0; i < 6; i++) {
            double angleDeg = 30 + i * 60;
            double angleRad = Math.toRadians(angleDeg);
            int px = (int) Math.round(centerX + size * Math.cos(angleRad));
            int py = (int) Math.round(centerY + size * Math.sin(angleRad));
            poly.addPoint(px, py);
        }
        return poly;
    }
}
