import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class VideoPlayer extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX Video Player");

        // Create a FileChooser to let the user pick a video file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Video File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.m4v", "*.flv", "*.f4v", "*.mov", "*.mkv", "*.avi")
        );

        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            // Create a Media object from the file URI
            Media media = new Media(file.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);

            // Surface playback errors (e.g., unsupported codec, missing file).
            // Without this listener, failures are silent and the window shows blank.
            mediaPlayer.setOnError(() ->
                System.err.println("Media error: " + mediaPlayer.getError()));

            MediaView mediaView = new MediaView(mediaPlayer);

            // Set up the scene and layout
            BorderPane root = new BorderPane();
            root.setCenter(mediaView);
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();

            // Start playing the video
            mediaPlayer.play();
        } else {
            System.out.println("No file selected. Exiting application.");
            primaryStage.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

