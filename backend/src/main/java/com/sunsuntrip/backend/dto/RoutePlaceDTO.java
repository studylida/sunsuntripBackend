package com.sunsuntrip.backend.dto;

import java.util.List;

public class RoutePlaceDTO {

    private Long id;              // 장소 ID
    private String name;          // 장소 이름
    private String category;      // PlaceCategory (e.g., "FOOD", "ACCOMMODATION")
    private List<String> themes;  // 테마 이름 리스트
    private double latitude;      // 위도
    private double longitude;     // 경도
    private int stayMinutes;      // 체류 시간 (분 단위)

    public RoutePlaceDTO(Long id, String name, String category, List<String> themes, double latitude, double longitude, int stayMinutes) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.themes = themes;
        this.latitude = latitude;
        this.longitude = longitude;
        this.stayMinutes = stayMinutes;
    }

    public RoutePlaceDTO() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getThemes() {
        return themes;
    }

    public void setThemes(List<String> themes) {
        this.themes = themes;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getStayMinutes() {
        return stayMinutes;
    }

    public void setStayMinutes(int stayMinutes) {
        this.stayMinutes = stayMinutes;
    }
}
