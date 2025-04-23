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

    private int day;
    private int stayMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    private RouteResult routeResult;

    protected RoutePlace() {}

    public RoutePlace(Place place, int day, int stayMinutes) {
        this.place = place;
        this.day = day;
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

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
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