package com.flowclaims.backend.model;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class WeatherService {

    private final String API_KEY = "864d5292c2434e9a9cc235828251407";
    private final String BASE_URL = "https://api.weatherapi.com/v1/current.json";

    private final RestTemplate restTemplate = new RestTemplate();

    public String getWeatherSummary(double latitude, double longitude) {
        String url = BASE_URL + "?key=" + API_KEY + "&q=" + latitude + "," + longitude;

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("current")) {
                Map<String, Object> current = (Map<String, Object>) response.get("current");
                Map<String, String> condition = (Map<String, String>) current.get("condition");

                String conditionText = condition.get("text");
                double tempF = (double) current.get("temp_f");
                double windMph = (double) current.get("wind_mph");

                return String.format("Weather: %s, Temp: %.1f°F, Wind: %.1f mph", conditionText, tempF, windMph);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Weather data unavailable";
    }
}
