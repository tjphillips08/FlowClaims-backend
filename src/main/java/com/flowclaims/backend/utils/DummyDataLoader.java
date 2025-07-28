package com.flowclaims.backend.utils;

import com.flowclaims.backend.model.Claim;
import com.flowclaims.backend.model.Note;
import com.flowclaims.backend.model.WeatherService;
import com.flowclaims.backend.model.GeocodingService;
import com.flowclaims.backend.repository.ClaimRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Component
public class DummyDataLoader implements CommandLineRunner {

    private final ClaimRepository claimRepository;
    private final WeatherService weatherService;
    private final GeocodingService geocodingService;

    public DummyDataLoader(ClaimRepository claimRepository,
                           WeatherService weatherService,
                           GeocodingService geocodingService) {
        this.claimRepository = claimRepository;
        this.weatherService = weatherService;
        this.geocodingService = geocodingService;
    }

    @Override
    public void run(String... args) throws Exception {
        File markerFile = new File(".dummy-data-loaded");

        if (markerFile.exists()) {
            System.out.println("🟡 Dummy data already loaded. Skipping.");
            return;
        }

        // Clear all existing data first
        claimRepository.deleteAll();
        System.out.println("🗑️  All existing claims deleted.");

        List<double[]> locations = List.of(
            new double[]{40.7128, -74.0060},   // New York
            new double[]{41.8781, -87.6298},   // Chicago
            new double[]{34.0522, -118.2437},  // Los Angeles
            new double[]{25.7617, -80.1918},   // Miami
            new double[]{39.7392, -104.9903},  // Denver
            new double[]{47.6062, -122.3321},  // Seattle
            new double[]{42.3601, -71.0589},   // Boston
            new double[]{32.7767, -96.7970},   // Dallas
            new double[]{33.7490, -84.3880},   // Atlanta
            new double[]{33.4484, -112.0740}   // Phoenix
        );

        List<String> noteSamples = List.of(
            "Hail damage to car",
            "Shingle damage from storm",
            "Water damage in basement",
            "Tree fell on roof",
            "Broken windows from debris",
            "Flooded crawl space",
            "Lightning strike on home",
            "Ice dam roof leak",
            "Fire from electrical short",
            "Heavy winds tore siding"
        );

        List<String> weatherScenarios = List.of(
            "Thunderstorms, 72°F",
            "Flooding, 68°F",
            "Heavy Snow, 29°F",
            "Tornado reported, 70°F",
            "Freezing Rain, 31°F",
            "Sunny, 85°F",
            "Partly Cloudy, 78°F",
            "Scattered Showers, 65°F",
            "Wind Advisory, 60°F",
            "Extreme Heat, 104°F"
        );

        List<String> names = List.of(
            "Olivia Johnson", "Liam Smith", "Emma Davis", "Noah Miller", "Ava Wilson",
            "William Brown", "Sophia Taylor", "James Moore", "Isabella Anderson", "Benjamin Thomas",
            "Mia Jackson", "Lucas White", "Charlotte Harris", "Henry Martin", "Amelia Thompson",
            "Elijah Garcia", "Harper Martinez", "Logan Robinson", "Evelyn Clark", "Alexander Lewis",
            "Abigail Lee", "Sebastian Walker", "Emily Hall", "Jack Allen", "Scarlett Young",
            "Daniel King", "Victoria Wright", "Matthew Scott", "Aria Torres", "Owen Nguyen",
            "Chloe Green", "Jacob Adams", "Ella Baker", "Michael Nelson", "Lily Carter",
            "Jackson Mitchell", "Grace Perez", "Aiden Roberts", "Riley Turner", "David Phillips",
            "Zoey Campbell", "Joseph Parker", "Hannah Evans", "Samuel Edwards", "Layla Collins",
            "Carter Stewart", "Nora Sanchez", "Jayden Morris", "Penelope Rogers", "Wyatt Reed"
        );

        Random rand = new Random();

        for (int i = 0; i < 50; i++) {
            Claim claim = new Claim();
            claim.setClaimantName(names.get(i));

            String status = switch (i % 3) {
                case 0 -> "Completed";
                case 1 -> "Received";
                default -> "In Progress";
            };
            claim.setStatus(status);
            claim.setReceivedDate(LocalDate.now().minusDays(rand.nextInt(30)));

            double[] loc = locations.get(rand.nextInt(locations.size()));
            double latitude = loc[0];
            double longitude = loc[1];
            claim.setLatitude(latitude);
            claim.setLongitude(longitude);

            // Enrich with fake weather + real location
            String locationName = geocodingService.getLocationName(latitude, longitude);
            String weatherSummary = weatherScenarios.get(rand.nextInt(weatherScenarios.size()));
            claim.setLocationName(locationName);
            claim.setWeatherSummary(weatherSummary);

            // Add a sample note
            Note note = new Note();
            note.setText(noteSamples.get(rand.nextInt(noteSamples.size())));
            note.setClaim(claim);
            claim.setNotes(List.of(note));

            claimRepository.save(claim);
        }

        // Create marker file to prevent rerun
        try (FileWriter writer = new FileWriter(markerFile)) {
            writer.write("dummy data inserted");
        }

        System.out.println("✅ 50 dummy claims inserted with diverse weather scenarios.");
    }
}
