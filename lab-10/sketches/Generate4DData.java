import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Generate4DData {
    public static void main(String[] args) {
        int numPoints = 100; // number of data points to generate
        Random random = new Random();
        try (FileWriter writer = new FileWriter("sample_data_4d.csv")) {
            for (int i = 0; i < numPoints; i++) {
                // Generate random coordinates in the range [-200, 200] for x, y, z
                double x = random.nextDouble() * 400 - 200;
                double y = random.nextDouble() * 400 - 200;
                double z = random.nextDouble() * 400 - 200;
                // Generate a time value t in the range [0, 100]
                double t = random.nextDouble() * 100;
                // Write the 4D point to the CSV file (formatted to 2 decimal places)
                writer.write(String.format("%.2f,%.2f,%.2f,%.2f%n", x, y, z, t));
            }
            System.out.println("Generated sample_data_4d.csv with " + numPoints + " data points.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

