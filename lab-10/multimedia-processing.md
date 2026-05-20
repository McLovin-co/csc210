# Multimedia Processing

## 1. Overview of Multimedia in Java

Java provides several options for multimedia support:

- **JavaFX Media API:** Built into JavaFX, it supports common audio (MP3, WAV) and video formats (MP4, FLV, etc.). It is the current standard for multimedia in Java desktop applications.
- **Third-Party Libraries:** Libraries like VLCJ (for VLC integration) or FMJ (Freedom for Media in Java) can be used when more format support or advanced control is needed.
- **Deprecated APIs:** Historically, the Java Media Framework (JMF) was used for multimedia but is now largely obsolete.

---

## 2. Using JavaFX for Audio and Video

### 2.1. Setting Up JavaFX

JavaFX is included in JDK 8, but later versions (post JDK 11) require you to add the JavaFX SDK as a dependency. When using an IDE or Maven/Gradle, include the necessary JavaFX modules (such as `javafx-media`).

### 2.2. Core Classes

- **`Media`:** Represents a media resource (audio or video) by specifying a URI.
- **`MediaPlayer`:** Controls playback (play, pause, stop, volume control).
- **`MediaView`:** A JavaFX Node that displays video content.

### 2.3. Basic Audio Playback Example

Below is an example that loads an audio file and plays it:

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class AudioPlayerExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Replace with the path to your audio file (ensure it's a valid URI)
        String audioFile = "file:///path/to/audio.mp3";
        Media media = new Media(audioFile);
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        // Optionally, set properties such as volume
        mediaPlayer.setVolume(0.7);

        // Start playing the audio
        mediaPlayer.play();

        StackPane root = new StackPane();
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("Audio Player Example");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Release native media resources when the window is closed.
        // Without this call the MediaPlayer may hold audio/video handles
        // and prevent the JVM from exiting cleanly.
        primaryStage.setOnHidden(e -> mediaPlayer.dispose());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

**Key Points:**
- Ensure the media file path is a proper URI (use `file:///` for local files).
- The `MediaPlayer` provides controls for playback and event listeners for monitoring playback status.

### 2.4. Basic Video Playback Example

For video, JavaFX requires a `MediaView` to render the video content:

```java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

public class VideoPlayerExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Replace with the path to your video file (must be a valid URI)
        String videoFile = "file:///path/to/video.mp4";
        Media media = new Media(videoFile);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);

        // Create a layout and add the MediaView
        StackPane root = new StackPane();
        root.getChildren().add(mediaView);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Video Player Example");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Start video playback
        mediaPlayer.play();

        // Dispose of native resources when the window closes.
        primaryStage.setOnHidden(e -> mediaPlayer.dispose());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

**Key Points:**
- The `MediaView` node is used to display video; it can be added to any JavaFX layout.
- Video controls (e.g., play, pause) are handled via the `MediaPlayer`.

---

## 3. Advanced Features and Controls

### 3.1. Event Handling

Both audio and video `MediaPlayer` support events such as:

- **`setOnReady`**: Triggered when media is ready for playback.
- **`setOnEndOfMedia`**: Called when playback completes.
- **`setOnError`**: Used to handle errors during loading or playback.

Example for handling the end of media:

```java
mediaPlayer.setOnEndOfMedia(() -> {
    System.out.println("Playback completed!");
    // Loop playback or reset as needed
});
```

### 3.2. Playback Control

- **Volume Control:** Adjust volume via `mediaPlayer.setVolume(double)`, where value is between 0.0 and 1.0.
- **Seeking:** Use `mediaPlayer.seek(Duration)` to jump to a specific time in the media.
- **Looping:** To loop playback, you can reset the media position when it reaches the end.

### 3.3. Synchronization and Media Properties

- **Synchronizing Multiple Media Players:** In scenarios where multiple media streams need to be in sync, you can listen to the `MediaPlayer` events and adjust the playback rate or seek positions accordingly.
- **Media Metadata:** Retrieve metadata (like duration, title, artist) using methods like `media.getMetadata()` once the media is loaded.

---

## 4. Third-Party Libraries

### 4.1. VLCJ

If you need support for additional formats or advanced playback features, consider VLCJ:

- **Pros:** Extensive format support and robust features.
- **Cons:** Requires native VLC installation and JNI bindings.

### 4.2. Other Options

- **FMJ (Freedom for Media in Java):** A project intended as an alternative to JMF.
- **GStreamer Java Bindings:** For complex multimedia processing.

Each library will have its own setup instructions and APIs, so consult the respective documentation for details.

---

## 5. Best Practices

- **Resource Management:** Always ensure you release resources by calling `mediaPlayer.dispose()` when the player is no longer needed.
- **Threading:** JavaFX’s media operations must occur on the JavaFX Application Thread. Use `Platform.runLater` for UI updates.
- **Error Handling:** Monitor for errors with `mediaPlayer.setOnError` and log or handle exceptions accordingly.
- **File Formats and URIs:** Ensure media paths are correctly formatted as URIs and that the formats are supported by the JavaFX media engine or your chosen library.
- **User Interface:** Consider building custom controls (buttons, sliders) or using existing frameworks to enhance user experience.

---

## 6. Conclusion

Java offers robust support for multimedia via JavaFX’s Media API, which simplifies the process of loading and playing both audio and video. For most desktop applications, JavaFX is sufficient, but for more advanced needs or format support, third-party libraries like VLCJ provide additional flexibility. This review sheet covers the basics from setting up a JavaFX project, coding simple audio/video players, and extending the functionality with event handling and synchronization techniques.
