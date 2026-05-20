// Scatter3DPlotWithTime — adds a time slider to animate a 4th (time) dimension.
// Related variants: Scatter3DPlot.java (random data, no animation),
//                   Scatter3DPlotFromFile.java (CSV input),
//                   Scatter3DPlotWithTimeExport.java (same as this + PNG export).
// Sample 4D data: sample_data_4d.csv is in this directory.
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Line;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Scatter3DPlotWithTime extends Application {

    // DataPoint class to hold 3D coordinates and a time value.
    static class DataPoint {
        double x, y, z, t;
        Sphere sphere;
        public DataPoint(double x, double y, double z, double t) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.t = t;
            sphere = new Sphere(5);
            sphere.setMaterial(new PhongMaterial(Color.DARKBLUE));
            sphere.setTranslateX(x);
            sphere.setTranslateY(y);
            sphere.setTranslateZ(z);
            sphere.setVisible(false); // initially invisible until time is reached
        }
    }

    private List<DataPoint> dataPoints = new ArrayList<>();
    private Group dataPointsGroup = new Group();

    // Time range values.
    private double minTime = Double.MAX_VALUE;
    private double maxTime = -Double.MAX_VALUE;

    // 3D world rotation and zoom controls.
    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private final Rotate rotateX = new Rotate(20, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(40, Rotate.Y_AXIS);
    private double cameraDistance = -600;

    // Timeline for the animation.
    private Timeline timeline;

    @Override
    public void start(Stage primaryStage) {
        // Create the world group that holds axes and data points.
        Group world = new Group();
        world.getChildren().add(createAxes());
        world.getChildren().add(dataPointsGroup);

        // Load data from file (CSV with x,y,z,t) using a FileChooser.
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Data File (CSV: x,y,z,t)");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            loadDataFromFile(file);
        } else {
            System.out.println("No file selected. Generating random data.");
            generateRandomData();
        }

        // Add the data point spheres to the group.
        for (DataPoint dp : dataPoints) {
            dataPointsGroup.getChildren().add(dp.sphere);
        }

        // Set initial visibility based on minTime.
        updateDataPointVisibility(minTime);

        // Apply rotation transforms to the world.
        world.getTransforms().addAll(rotateX, rotateY);

        // Create a SubScene for 3D content.
        SubScene subScene = new SubScene(world, 800, 600, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.LIGHTGRAY);

        // Set up a PerspectiveCamera.
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(cameraDistance);
        subScene.setCamera(camera);

        // Handle mouse events for rotating the view.
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

        // Handle scroll events for zooming.
        subScene.setOnScroll((ScrollEvent event) -> {
            double delta = event.getDeltaY();
            cameraDistance += delta;
            camera.setTranslateZ(cameraDistance);
        });

        // Create a slider for selecting time.
        Slider timeSlider = new Slider(minTime, maxTime, minTime);
        timeSlider.setPrefWidth(400);
        timeSlider.setShowTickLabels(true);
        timeSlider.setShowTickMarks(true);

        // When slider value changes, update data point visibility.
        timeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateDataPointVisibility(newVal.doubleValue());
        });

        // Create a Play/Pause button.
        Button playButton = new Button("Play");
        playButton.setOnAction(e -> {
            if (timeline != null && timeline.getStatus() == Timeline.Status.RUNNING) {
                timeline.pause();
                playButton.setText("Play");
            } else {
                // Start a new timeline if not already running.
                timeline = new Timeline(new KeyFrame(Duration.millis(100), ev -> {
                    double currentTime = timeSlider.getValue();
                    double nextTime = currentTime + (maxTime - minTime) / 100.0;
                    if (nextTime > maxTime) {
                        nextTime = minTime;
                    }
                    timeSlider.setValue(nextTime);
                }));
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play();
                playButton.setText("Pause");
            }
        });

        // Layout: place the subscene at center and controls at bottom.
        HBox controls = new HBox(10, playButton, timeSlider);
        controls.setPadding(new Insets(10));
        BorderPane root = new BorderPane();
        root.setCenter(subScene);
        root.setBottom(controls);

        Scene scene = new Scene(root, 800, 650, true);
        primaryStage.setTitle("3D Data Plot with Time");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Loads 3D data with time from a CSV file.
     * Each line should be in the format: x,y,z,t
     */
    private void loadDataFromFile(File file) {
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue; // Skip empty lines.
                String[] tokens = line.split(",");
                if (tokens.length != 4) {
                    System.out.println("Skipping invalid line: " + line);
                    continue;
                }
                try {
                    double x = Double.parseDouble(tokens[0].trim());
                    double y = Double.parseDouble(tokens[1].trim());
                    double z = Double.parseDouble(tokens[2].trim());
                    double t = Double.parseDouble(tokens[3].trim());
                    DataPoint dp = new DataPoint(x, y, z, t);
                    dataPoints.add(dp);
                    if (t < minTime) minTime = t;
                    if (t > maxTime) maxTime = t;
                } catch (NumberFormatException e) {
                    System.out.println("Skipping line with invalid number format: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    /**
     * Generates random data if no file is selected.
     * Creates 100 data points with random x,y,z and time values between 0 and 100.
     */
    private void generateRandomData() {
        minTime = 0;
        maxTime = 100;
        for (int i = 0; i < 100; i++) {
            double x = Math.random() * 400 - 200;
            double y = Math.random() * 400 - 200;
            double z = Math.random() * 400 - 200;
            double t = Math.random() * (maxTime - minTime) + minTime;
            DataPoint dp = new DataPoint(x, y, z, t);
            dataPoints.add(dp);
        }
    }

    /**
     * Updates the visibility of data point spheres based on the current time.
     * Only points with time less than or equal to currentTime are visible.
     */
    private void updateDataPointVisibility(double currentTime) {
        for (DataPoint dp : dataPoints) {
            dp.sphere.setVisible(dp.t <= currentTime);
        }
    }

    /**
     * Creates simple 3D axes for X (red), Y (green), and Z (blue).
     */
    private Group createAxes() {
        Group axes = new Group();
        // X-axis: red horizontal line.
        Line xAxis = new Line(-250, 0, 250, 0);
        xAxis.setStroke(Color.RED);
        // Y-axis: green vertical line.
        Line yAxis = new Line(0, -250, 0, 250);
        yAxis.setStroke(Color.GREEN);
        // Z-axis: blue cylinder rotated to align along Z.
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

