package com.sunsuntrip.backend.dto;

import java.util.List;

public class RouteResultResponseDTO {

    private int totalDistance; // km 단위
    private int totalDuration; // 분 단위

    private List<DailyPlanDTO> dailyPlans;

    public RouteResultResponseDTO(int totalDistance, int totalDuration, List<DailyPlanDTO> dailyPlans) {
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
        this.dailyPlans = dailyPlans;
    }

    public RouteResultResponseDTO() {

    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public List<DailyPlanDTO> getDailyPlans() {
        return dailyPlans;
    }

    public void setDailyPlans(List<DailyPlanDTO> dailyPlans) {
        this.dailyPlans = dailyPlans;
    }
}
