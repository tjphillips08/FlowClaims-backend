package com.flowclaims.backend.model;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GeocodingService {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public String getLocationName(double latitude, double longitude) {
        try {
            String url = String.format(
                "https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f&addressdetails=1",
                latitude, longitude);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "FlowClaimsApp/1.0 (your-email@example.com)")
                .GET()
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = mapper.readTree(response.body());
                JsonNode address = root.path("address");

                if (!address.isMissingNode()) {
                    if (address.hasNonNull("city"))
                        return address.get("city").asText();
                    if (address.hasNonNull("town"))
                        return address.get("town").asText();
                    if (address.hasNonNull("state"))
                        return address.get("state").asText();
                }

                if (root.hasNonNull("display_name"))
                    return root.get("display_name").asText();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown";
    }
}

