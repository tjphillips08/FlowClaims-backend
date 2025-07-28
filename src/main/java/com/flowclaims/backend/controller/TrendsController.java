package com.flowclaims.backend.controller;

import com.flowclaims.backend.model.Claim;
import com.flowclaims.backend.repository.ClaimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trends")
public class TrendsController {

    @Autowired
    private ClaimRepository claimRepository;

    @GetMapping("/summary")
    public Map<String, Object> getTrendsSummary() {
        List<Claim> claims = claimRepository.findAll();

        Map<String, Object> summary = new HashMap<>();

        // Chart 1: Count by status
        Map<String, Long> statusCounts = claims.stream()
                .collect(Collectors.groupingBy(Claim::getStatus, Collectors.counting()));
        summary.put("statusCounts", statusCounts);

        // Chart 2: Count by weather summary
        Map<String, Long> weatherCounts = claims.stream()
                .filter(c -> c.getWeatherSummary() != null)
                .collect(Collectors.groupingBy(Claim::getWeatherSummary, Collectors.counting()));
        summary.put("weatherCounts", weatherCounts);

        // Chart 3: Count by location (rounded lat/lon to simulate city-level grouping)
        Map<String, Long> locationCounts = claims.stream()
                .collect(Collectors.groupingBy(
                        c -> String.format("%.1f, %.1f", c.getLatitude(), c.getLongitude()),
                        Collectors.counting()
                ));
        summary.put("locationCounts", locationCounts);

        return summary;
    }
}

