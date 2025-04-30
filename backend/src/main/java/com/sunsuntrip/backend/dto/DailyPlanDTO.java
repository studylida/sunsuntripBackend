package com.sunsuntrip.backend.dto;

import java.util.List;

public class DailyPlanDTO {

    private int day; // 1, 2, 3, ...
    private List<RoutePlaceDTO> places;

    public DailyPlanDTO(int day, List<RoutePlaceDTO> places) {
        this.day = day;
        this.places = places;
    }

    public DailyPlanDTO() {
        this.day = -1;
        this.places = null;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public List<RoutePlaceDTO> getPlaces() {
        return places;
    }

    public void setPlaces(List<RoutePlaceDTO> places) {
        this.places = places;
    }
}
