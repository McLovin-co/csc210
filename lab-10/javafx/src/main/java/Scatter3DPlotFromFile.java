// Scatter3DPlotFromFile — loads (x,y,z) data from a user-selected CSV file.
// Related variants: Scatter3DPlot.java (random data), Scatter3DPlotWithTime.java,
//                   Scatter3DPlotWithTimeExport.java.
// Sample data: sample_data.csv is in this directory.
// Run from the sketches/ directory so the FileChooser opens there by default,
// or supply the full path when prompted.
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Scatter3DPlotFromFile extends Application {

    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private final Rotate rotateX = new Rotate(20, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(40, Rotate.Y_AXIS);
    private double cameraDistance = -600;

    @Override
    public void start(Stage primaryStage) {
        // Group that holds all 3D objects.
        Group world = new Group();
        
        // Create and add the 3D axes.
        world.getChildren().add(createAxes());
        
        // Group to hold the data points.
        Group dataPoints = new Group();
        
        // Use a FileChooser to let the user select a data file.
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Data File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File file = fileChooser.showOpenDialog(primaryStage);
        
        if (file != null) {
            loadDataFromFile(file, dataPoints);
        } else {
            System.out.println("No file selected. Generating random data.");
            // Fallback: generate random data points if no file is chosen.
            for (int i = 0; i < 100; i++) {
                double x = Math.random() * 400 - 200;
                double y = Math.random() * 400 - 200;
                double z = Math.random() * 400 - 200;
                Sphere point = new Sphere(5);
                point.setMaterial(new PhongMaterial(Color.DARKBLUE));
                point.setTranslateX(x);
                point.setTranslateY(y);
                point.setTranslateZ(z);
                dataPoints.getChildren().add(point);
            }
        }
        
        world.getChildren().add(dataPoints);
        world.getTransforms().addAll(rotateX, rotateY);
        
        // Create a SubScene with depth buffering enabled.
        SubScene subScene = new SubScene(world, 800, 600, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.LIGHTGRAY);
        
        // Set up a perspective camera.
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(cameraDistance);
        subScene.setCamera(camera);
        
        // Mouse event handling for rotation.
        subScene.setOnMousePressed((MouseEvent event) -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            anchorAngleX = rotateX.getAngle();
            anchorAngleY = rotateY.getAngle();
        });
        
        subScene.setOnMouseDragged((MouseEvent event) -> {
            rotateX.setAngle(anchorAngleX - (anchorY - event.getSceneY()));
            rotateY.setAngle(anchorAngleY + (anchorX - event.getSceneX()));
        });
        
        // Scroll event handling for zooming.
        subScene.setOnScroll((ScrollEvent event) -> {
            double delta = event.getDeltaY();
            cameraDistance += delta;
            camera.setTranslateZ(cameraDistance);
        });
        
        // Create and show the main scene.
        Group root = new Group();
        root.getChildren().add(subScene);
        Scene scene = new Scene(root, 800, 600, true);
        primaryStage.setTitle("3D Data Plot");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Loads 3D data from a CSV file.
     * Each line should be in the format: x,y,z
     */
    private void loadDataFromFile(File file, Group dataPoints) {
        try (Scanner scanner = new Scanner(file)) {
            PhongMaterial pointMaterial = new PhongMaterial(Color.DARKBLUE);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue; // Skip empty lines.
                String[] tokens = line.split(",");
                if (tokens.length != 3) {
                    System.out.println("Skipping invalid line: " + line);
                    continue;
                }
                try {
                    double x = Double.parseDouble(tokens[0].trim());
                    double y = Double.parseDouble(tokens[1].trim());
                    double z = Double.parseDouble(tokens[2].trim());
                    Sphere point = new Sphere(5);
                    point.setMaterial(pointMaterial);
                    point.setTranslateX(x);
                    point.setTranslateY(y);
                    point.setTranslateZ(z);
                    dataPoints.getChildren().add(point);
                } catch (NumberFormatException e) {
                    System.out.println("Skipping line with invalid number format: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
    
    /**
     * Creates simple 3D axes: X (red), Y (green), and Z (blue).
     */
    private Group createAxes() {
        Group axes = new Group();
        
        // X-axis: red line.
        Line xAxis = new Line(-250, 0, 250, 0);
        xAxis.setStroke(Color.RED);
        
        // Y-axis: green line.
        Line yAxis = new Line(0, -250, 0, 250);
        yAxis.setStroke(Color.GREEN);
        
        // Z-axis: blue cylinder rotated to align with the Z-axis.
        Cylinder zAxis = new Cylinder(1, 500);
        zAxis.setMaterial(new PhongMaterial(Color.BLUE));
        zAxis.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
        
        axes.getChildren().addAll(xAxis, yAxis, zAxis);
        return axes;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

