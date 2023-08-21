package assignment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WeatherApp {

    private static final String API_KEY = "b6907d289e10d714a6e88b30761fae22";
    private static final String API_URL_BASE = "https://samples.openweathermap.org/data/2.5/forecast/hourly?q=London,us&appid=" + API_KEY;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            int choice;
            do {
                System.out.println("1. Get Temperature");
                System.out.println("2. Get Wind Speed");
                System.out.println("3. Get Pressure");
                System.out.println("0. Exit");
                System.out.print("Enter your choice: ");
                choice = Integer.parseInt(reader.readLine());

                switch (choice) {
                    case 1:
                        printWeatherInfo("temp", reader);
                        break;
                    case 2:
                        printWeatherInfo("wind.speed", reader);
                        break;
                    case 3:
                        printWeatherInfo("pressure", reader);
                        break;
                    case 0:
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } while (choice != 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printWeatherInfo(String dataField, BufferedReader reader) throws IOException {
        System.out.print("Enter date and time (yyyy-MM-dd HH:mm:ss): ");
        String dateTimeStr = reader.readLine();

        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
            String weatherData = fetchWeatherData();
            printWeatherData(dateTime, weatherData, dataField);
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd HH:mm:ss");
        }
    }

    private static String fetchWeatherData() throws IOException {
        URL url = new URL(API_URL_BASE);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    private static void printWeatherData(LocalDateTime dateTime, String weatherData, String dataField) {
        String[] forecastList = weatherData.split("\"list\":\\[")[1].split("\\],\"city\"")[0].split("\\},\\{");

        for (String forecast : forecastList) {
            String forecastDateTimeStr = forecast.split("\"dt_txt\":\"")[1].split("\"")[0];
            LocalDateTime forecastDateTime = LocalDateTime.parse(forecastDateTimeStr, DATE_TIME_FORMATTER);

            if (forecastDateTime.isEqual(dateTime)) {
                String[] mainData = forecast.split("\"main\":\\{")[1].split("\\},\"wind\"")[0].split(",");
                double value = 0.0;
                for (String mainDatum : mainData) {
                    if (mainDatum.contains(dataField)) {
                        value = Double.parseDouble(mainDatum.split(":")[1]);
                        break;
                    }
                }
                System.out.println(dataField + ": " + value);
                return;
            }
        }

        System.out.println("Weather data not found for the specified date and time.");
    }
}
