// Scatter3DPlot — baseline: plots 100 random 3D points.
// Related variants in this directory (all share the same axes/camera/mouse setup):
//   Scatter3DPlotFromFile.java      — loads (x,y,z) data from a CSV file
//   Scatter3DPlotWithTime.java      — adds a time-slider to animate a 4th dimension
//   Scatter3DPlotWithTimeExport.java — same as WithTime plus PNG export
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Line;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class Scatter3DPlot extends Application {

    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private final Rotate rotateX = new Rotate(20, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(40, Rotate.Y_AXIS);
    private double cameraDistance = -600;

    @Override
    public void start(Stage primaryStage) {
        // Group for all 3D objects in the scene.
        Group world = new Group();
        
        // Create 3D axes.
        world.getChildren().add(createAxes());
        
        // Create a group for data points (here we plot random points).
        Group dataPoints = new Group();
        PhongMaterial pointMaterial = new PhongMaterial(Color.DARKBLUE);
        for (int i = 0; i < 100; i++) {
            double x = Math.random() * 400 - 200;
            double y = Math.random() * 400 - 200;
            double z = Math.random() * 400 - 200;
            Sphere point = new Sphere(5);
            point.setMaterial(pointMaterial);
            point.setTranslateX(x);
            point.setTranslateY(y);
            point.setTranslateZ(z);
            dataPoints.getChildren().add(point);
        }
        world.getChildren().add(dataPoints);

        // Apply rotation transforms to the world group.
        world.getTransforms().addAll(rotateX, rotateY);

        // Create a SubScene with depth buffer enabled.
        SubScene subScene = new SubScene(world, 800, 600, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.LIGHTGRAY);

        // Set up a perspective camera.
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(cameraDistance);
        subScene.setCamera(camera);

        // Handle mouse press: record starting positions.
        subScene.setOnMousePressed((MouseEvent event) -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            anchorAngleX = rotateX.getAngle();
            anchorAngleY = rotateY.getAngle();
        });

        // Handle mouse drag: update rotation based on mouse movement.
        subScene.setOnMouseDragged((MouseEvent event) -> {
            rotateX.setAngle(anchorAngleX - (anchorY - event.getSceneY()));
            rotateY.setAngle(anchorAngleY + (anchorX - event.getSceneX()));
        });

        // Handle scroll event for zooming.
        subScene.setOnScroll((ScrollEvent event) -> {
            double delta = event.getDeltaY();
            cameraDistance += delta;
            camera.setTranslateZ(cameraDistance);
        });

        // Create the main scene and add the subscene.
        Group root = new Group();
        root.getChildren().add(subScene);
        Scene scene = new Scene(root, 800, 600, true);

        primaryStage.setTitle("3D Data Plot");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Create simple 3D axes: X (red), Y (green) and Z (blue).
    private Group createAxes() {
        Group axes = new Group();

        // X-axis from -250 to 250.
        Line xAxis = new Line(-250, 0, 250, 0);
        xAxis.setStroke(Color.RED);
        xAxis.setTranslateY(0);
        // Y-axis from -250 to 250.
        Line yAxis = new Line(0, -250, 0, 250);
        yAxis.setStroke(Color.GREEN);
        // For the Z-axis, we use a thin Cylinder (rotated to align with Z).
        Cylinder zAxis = new Cylinder(1, 500);
        zAxis.setMaterial(new PhongMaterial(Color.BLUE));
        zAxis.setTranslateX(0);
        zAxis.setTranslateY(0);
        zAxis.setTranslateZ(0);
        zAxis.getTransforms().add(new Rotate(90, Rotate.X_AXIS));

        axes.getChildren().addAll(xAxis, yAxis, zAxis);
        return axes;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

