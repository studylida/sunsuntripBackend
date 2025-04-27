// 루트에 포함된 장소의 날짜 및 체류 시간 정보

// 📁 RoutePlace.java
package com.sunsuntrip.backend.domain;

import jakarta.persistence.*;

@Entity
public class RoutePlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Place place;

    private int visitDay;
    private int stayMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    private RouteResult routeResult;

    public RoutePlace() {}

    public RoutePlace(Place place, int visitDay, int stayMinutes) {
        this.place = place;
        this.visitDay = visitDay;
        this.stayMinutes = stayMinutes;
    }

    public void setRouteResult(RouteResult routeResult) {
        this.routeResult = routeResult;
    }

    // Getters and Setters...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public int getVisitDay() {
        return visitDay;
    }

    public void setVisitDay(int visitDay) {
        this.visitDay = visitDay;
    }

    public int getStayMinutes() {
        return stayMinutes;
    }

    public void setStayMinutes(int stayMinutes) {
        this.stayMinutes = stayMinutes;
    }

    public RouteResult getRouteResult() {
        return routeResult;
    }
}