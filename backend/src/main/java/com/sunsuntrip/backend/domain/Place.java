package com.sunsuntrip.backend.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Place {

    public enum PlaceCategory {
        ACCOMMODATION,  // 숙소
        ATTRACTION,     // 관광지
        SHOPPING,       // 쇼핑
        FOOD            // 식당
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 장소명. 구글맵에 등록된 이름과 동일하게 설정

    private String description; // 장소에 대한 설명

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlaceCategory category;  // 관광지, 식당, 숙소 중 하나

    private double latitude; // 위도
    private double longitude; // 경도

    @ManyToMany
    @JoinTable(
            name = "place_theme",
            joinColumns = @JoinColumn(name = "place_id"),
            inverseJoinColumns = @JoinColumn(name = "theme_id")
    )
    private List<Theme> themes = new ArrayList<>();

    // 기본 생성자 (꼭 필요!)
    protected Place() {
    }

    public Place(Long id, String name, String description, PlaceCategory category, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public PlaceCategory getCategory() {
        return category;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public List<Theme> getThemes() {
        return themes;
    }

    public void setThemes(List<Theme> themes) {
        this.themes = themes;
    }
}
