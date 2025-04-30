package com.sunsuntrip.backend.dto;

import java.util.List;

public class PlaceResponseDTO {

    private Long id;
    private String name;
    private String category;
    private double latitude;
    private double longitude;
    private List<String> themes;

    public PlaceResponseDTO() {
    }

    public PlaceResponseDTO(Long id, String name, String category, double latitude, double longitude, List<String> themes) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
        this.themes = themes;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public List<String> getThemes() {
        return themes;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setThemes(List<String> themes) {
        this.themes = themes;
    }
}
