# Lab 10 Sketches

## Directory Structure

```
lab-10/
  sketches/          ← Swing/plain-Java programs (compile with javac)
  javafx/            ← JavaFX programs (build with Maven)
    pom.xml
    src/main/java/
    data/            ← sample_data.csv, sample_data_4d.csv, test.mp4
```

---

## Compiling and Running Swing / Plain-Java Programs

### CirclePlotter, SquarePlotter, SquarePlotterAdvanced
```
javac CirclePlotter.java
java CirclePlotter
```

### BombermanGame
```
javac BombermanGame.java
java OptionsScreen
```

### Chat (server + client)
```
javac ChatServer.java ChatClient.java
java ChatServer          # terminal 1
java ChatClient          # terminals 2, 3, 4 …
```

### Hexagon (networked — compile all files together)
```
javac *.java
java HexagonGameServer       # terminal 1
java HexagonGameClient       # terminal 2
java HexagonGameClient       # terminal 3
```

### HexagonGameP2P / DeathmatchGame
```
javac *.java
java HexagonGameP2P          # run two instances
java DeathmatchGame          # run two instances
```

### Database examples
```
javac PostgresConnectionExample.java
java -cp .:postgresql-42.7.5.jar PostgresConnectionExample

javac PostgresQueryExample.java
java -cp .:postgresql-42.7.5.jar PostgresQueryExample
```

---

## Compiling and Running JavaFX Programs

JavaFX programs live in `lab-10/javafx/` and are built with Maven.
No need to download a JavaFX SDK or set `--module-path` by hand — Maven
handles dependencies automatically.

### Prerequisites
- Java 21+
- Maven 3.6+ (`mvn -version` to check)

### Build (compile all JavaFX programs at once)
```
cd lab-10/javafx
mvn compile
```

### Run each program
```
mvn javafx:run                                                  # HelloJavaFX (default)
mvn javafx:run -DmainClass=VideoPlayer
mvn javafx:run -DmainClass=Scatter3DPlot
mvn javafx:run -DmainClass=Scatter3DPlotFromFile
mvn javafx:run -DmainClass=Scatter3DPlotWithTime
mvn javafx:run -DmainClass=Scatter3DPlotWithTimeExport
```

### Sample data files
Sample data for the Scatter3D programs and the test video are in `javafx/data/`.
When a file-picker dialog opens, navigate there to load them.

---

## Generating Data

`Generate3DData.java` and `Generate4DData.java` are plain-Java programs in `sketches/`:
```
javac Generate3DData.java
java Generate3DData

javac Generate4DData.java
java Generate4DData
```
