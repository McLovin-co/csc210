import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Generate3DData {
    public static void main(String[] args) {
        int numPoints = 100; // number of data points to generate
        Random random = new Random();
        try (FileWriter writer = new FileWriter("sample_data.csv")) {
            for (int i = 0; i < numPoints; i++) {
                // Generate random coordinates in the range [-200, 200]
                double x = random.nextDouble() * 400 - 200;
                double y = random.nextDouble() * 400 - 200;
                double z = random.nextDouble() * 400 - 200;
                // Write the point to the CSV file (2 decimal places)
                writer.write(String.format("%.2f,%.2f,%.2f%n", x, y, z));
            }
            System.out.println("Generated sample_data.csv with " + numPoints + " data points.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

