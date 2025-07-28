package com.flowclaims.backend.controller;

import com.flowclaims.backend.model.Claim;
import com.flowclaims.backend.model.HeatMapPoint;
import com.flowclaims.backend.repository.ClaimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api")
public class HeatMapController {

    private final ClaimRepository claimRepository;

    @Autowired
    public HeatMapController(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    @GetMapping("/heatmap")
    public ResponseEntity<List<HeatMapPoint>> getHeatMapData(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String weather,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<Claim> claims = claimRepository.findAll();

        // Optional filtering
        if (status != null) {
            claims = claims.stream()
                    .filter(c -> status.equalsIgnoreCase(c.getStatus()))
                    .toList();
        }

        if (weather != null) {
            claims = claims.stream()
                    .filter(c -> c.getWeatherSummary() != null &&
                            c.getWeatherSummary().toLowerCase().contains(weather.toLowerCase()))
                    .toList();
        }

        if (startDate != null && endDate != null) {
            claims = claims.stream()
                    .filter(c -> c.getReceivedDate() != null &&
                            (c.getReceivedDate().isEqual(startDate) || c.getReceivedDate().isAfter(startDate)) &&
                            (c.getReceivedDate().isEqual(endDate) || c.getReceivedDate().isBefore(endDate)))
                    .toList();
        }

        // Group by location and accumulate weight
        Map<String, HeatMapPoint> grouped = new HashMap<>();
        for (Claim claim : claims) {
            Double lat = claim.getLatitude();
            Double lon = claim.getLongitude();
            if (lat == null || lon == null) continue;

            String key = lat + "," + lon;
            grouped.putIfAbsent(key, new HeatMapPoint(lat, lon, 0));

            double weight = 1.0;

            // Status weighting
            if ("In Progress".equalsIgnoreCase(claim.getStatus())) weight += 1;
            else if ("Completed".equalsIgnoreCase(claim.getStatus())) weight += 2;

            // Weather weighting
            if (claim.getWeatherSummary() != null) {
                String w = claim.getWeatherSummary().toLowerCase();
                if (w.contains("tornado") || w.contains("hail") || w.contains("lightning") || w.contains("storm")) {
                    weight += 3;
                } else if (w.contains("wind") || w.contains("rain")) {
                    weight += 1;
                }
            }

            grouped.get(key).addWeight(weight);
        }

        // Normalize weights to range 0.1 to 1.0
        double maxWeight = grouped.values().stream()
                .mapToDouble(HeatMapPoint::getWeight)
                .max().orElse(1.0);

        List<HeatMapPoint> result = new ArrayList<>();
        for (HeatMapPoint point : grouped.values()) {
            double normalizedWeight = Math.max(0.1, point.getWeight() / maxWeight);
            result.add(new HeatMapPoint(point.getLatitude(), point.getLongitude(), normalizedWeight));
        }

        return ResponseEntity.ok(result);
    }
}
