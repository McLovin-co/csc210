import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RawRandomHexStreamer {
    public static void main(String[] args) {
        // Random.org API URL: returns 16 random bytes in hexadecimal text form.
        String apiUrl = "https://www.random.org/cgi-bin/randbyte?nbytes=16&format=h";

        while (true) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(apiUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the response as binary data and write it directly to stdout.
                    try (InputStream is = connection.getInputStream()) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            System.out.write(buffer, 0, bytesRead);
                        }
                        // Flush to ensure immediate output.
                        System.out.flush();
                    }
                } else {
                    System.err.println("GET request failed with response code: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            // Pause briefly between requests to avoid overwhelming the service.
            try {
                Thread.sleep(1000);  // 1-second delay between requests
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

