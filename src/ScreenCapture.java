import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ScreenCapture {
    public static void main(String[] args) throws AWTException, InterruptedException, IOException {
        double captureInterval = 0.075; // Seconds to wait between each capture (minimum 0.025)
        int desiredWidth = 30; // Desired width of the downscaled image
        int desiredHeight = 30; // Desired height of the downscaled image

        int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        int skipX = screenWidth / desiredWidth;
        int skipY = screenHeight / desiredHeight;

        Robot robot = new Robot();
        StringBuilder pixelData = new StringBuilder();

        while (true) {
            for (int y = 0; y < screenHeight; y += skipY) {
                for (int x = 0; x < screenWidth; x += skipX) {
                    Color pixelColor = robot.getPixelColor(x, y);
                    String hexValue = String.format("#%02X%02X%02X", pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue());
                    pixelData.append(hexValue).append("|");
                }
            }

            sendPixelData(pixelData.toString());
            pixelData.setLength(0); // Reset the StringBuilder

            //Thread.sleep((long) (captureInterval * 1000));
        }
    }

    private static void sendPixelData(String data) throws IOException {
        String urlString = "http://127.0.0.1:3000";
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = data.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                System.out.println(responseLine);
            }
        }
    }
}