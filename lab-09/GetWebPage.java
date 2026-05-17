import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * GetWebPage — fetches and prints the HTML content of www.java.com.
 * Written as the answer to Lab 09 Question 8d.
 */
public class GetWebPage {
    public static void main(String[] args) {
        try {
            URL url = new URL("https://www.java.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            // Some servers reject requests without a User-Agent header
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            in.close();
            connection.disconnect();

            System.out.println(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
