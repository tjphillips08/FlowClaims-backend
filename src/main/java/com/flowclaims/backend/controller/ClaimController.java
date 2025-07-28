package com.flowclaims.backend.controller;

import com.flowclaims.backend.model.*;
import com.flowclaims.backend.repository.ClaimRepository;
import com.flowclaims.backend.model.WeatherService;
import com.flowclaims.backend.model.GeocodingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimRepository claimRepository;
    private final WeatherService weatherService;
    private final GeocodingService geocodingService;

    @Autowired
    public ClaimController(ClaimRepository claimRepository,
                           WeatherService weatherService,
                           GeocodingService geocodingService) {
        this.claimRepository = claimRepository;
        this.weatherService = weatherService;
        this.geocodingService = geocodingService;
    }

    @GetMapping
    public List<ClaimDTO> getAllClaims() {
        return claimRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaimDTO> getClaimById(@PathVariable Long id) {
        return claimRepository.findById(id)
                .map(claim -> ResponseEntity.ok(convertToDTO(claim)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ClaimDTO> createClaim(@RequestBody Claim claim) {
        enrichClaimWithLocationAndWeather(claim);
        Claim savedClaim = claimRepository.save(claim);
        return ResponseEntity.ok(convertToDTO(savedClaim));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClaimDTO> updateClaim(@PathVariable Long id, @RequestBody Claim updatedClaim) {
        return claimRepository.findById(id)
                .map(claim -> {
                    claim.setClaimantName(updatedClaim.getClaimantName());
                    claim.setStatus(updatedClaim.getStatus());
                    claim.setReceivedDate(updatedClaim.getReceivedDate());
                    claim.setLatitude(updatedClaim.getLatitude());
                    claim.setLongitude(updatedClaim.getLongitude());

                    enrichClaimWithLocationAndWeather(claim);

                    Claim saved = claimRepository.save(claim);
                    return ResponseEntity.ok(convertToDTO(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClaim(@PathVariable Long id) {
        if (claimRepository.existsById(id)) {
            claimRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Adds weather and location data to a claim
    private void enrichClaimWithLocationAndWeather(Claim claim) {
        Double lat = claim.getLatitude();
        Double lon = claim.getLongitude();

        if (lat != null && lon != null) {
            claim.setWeatherSummary(weatherService.getWeatherSummary(lat, lon));
            claim.setLocationName(geocodingService.getLocationName(lat, lon));
        }
    }

    // Converts Claim entity to ClaimDTO
    private ClaimDTO convertToDTO(Claim claim) {
        List<NoteDTO> noteDTOs = claim.getNotes() == null ? List.of() :
                claim.getNotes().stream()
                        .map(note -> new NoteDTO(
                                note.getId(),
                                note.getText(),
                                note.getCreatedAt() == null ? null : note.getCreatedAt().toString()
                        ))
                        .collect(Collectors.toList());

        String receivedDateStr = claim.getReceivedDate() == null ? null : claim.getReceivedDate().toString();

        return new ClaimDTO(
                claim.getId(),
                claim.getClaimantName(),
                claim.getStatus(),
                receivedDateStr,
                claim.getLatitude(),
                claim.getLongitude(),
                claim.getWeatherSummary(),
                claim.getLocationName(),
                noteDTOs
        );
    }
}
