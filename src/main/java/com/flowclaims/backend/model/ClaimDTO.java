package com.flowclaims.backend.model;

import java.util.List;

public class ClaimDTO {
    private Long id;
    private String claimantName;
    private String status;
    private String receivedDate;
    private Double latitude;
    private Double longitude;
    private String weatherSummary;
    private String locationName;
    private List<NoteDTO> notes;

    public ClaimDTO(Long id, String claimantName, String status, String receivedDate,
                    Double latitude, Double longitude, String weatherSummary,
                    String locationName, List<NoteDTO> notes) {
        this.id = id;
        this.claimantName = claimantName;
        this.status = status;
        this.receivedDate = receivedDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.weatherSummary = weatherSummary;
        this.locationName = locationName;
        this.notes = notes;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClaimantName() { return claimantName; }
    public void setClaimantName(String claimantName) { this.claimantName = claimantName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReceivedDate() { return receivedDate; }
    public void setReceivedDate(String receivedDate) { this.receivedDate = receivedDate; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getWeatherSummary() { return weatherSummary; }
    public void setWeatherSummary(String weatherSummary) { this.weatherSummary = weatherSummary; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public List<NoteDTO> getNotes() { return notes; }
    public void setNotes(List<NoteDTO> notes) { this.notes = notes; }
}
