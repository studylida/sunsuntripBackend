// 	AI가 생성한 루트 결과 (장소 리스트, 총 이동 거리, 소요 시간 등)

// 📁 RouteResult.java
package com.sunsuntrip.backend.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class RouteResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double totalDistance;
    private int totalDuration;

    @ManyToOne
    @JoinColumn(name = "user_condition_id")
    private UserCondition userCondition;

    @OneToMany(mappedBy = "routeResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutePlace> routePlaces = new ArrayList<>();

    protected RouteResult() {}

    public RouteResult(double totalDistance, int totalDuration, UserCondition userCondition) {
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
        this.userCondition = userCondition;
    }

    public void addRoutePlace(RoutePlace routePlace) {
        routePlaces.add(routePlace);
        routePlace.setRouteResult(this);
    }

    public void removeRoutePlaceById(Long routePlaceId) {
        this.routePlaces.removeIf(place -> Objects.equals(place.getId(), routePlaceId));
    }


    // Getters and Setters...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public UserCondition getUserCondition() {
        return userCondition;
    }

    public void setUserCondition(UserCondition userCondition) {
        this.userCondition = userCondition;
    }

    public List<RoutePlace> getRoutePlaces() {
        return routePlaces;
    }

    public void setRoutePlaces(List<RoutePlace> routePlaces) {
        this.routePlaces = routePlaces;
    }
}
