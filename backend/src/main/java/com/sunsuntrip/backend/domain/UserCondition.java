// AI가 받은 사용자 조건 (여행 기간, 예산, 선호 테마 등)
package com.sunsuntrip.backend.domain;

import com.sunsuntrip.backend.domain.Theme;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class UserCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int days;
//    private int budget;
//    private int numberOfPeople;
//    private LocalDate startDate;

    @ManyToMany
    @JoinTable(
            name = "usercondition_theme",
            joinColumns = @JoinColumn(name = "usercondition_id"),
            inverseJoinColumns = @JoinColumn(name = "theme_id")
    )
    private List<Theme> themes = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

//    public int getBudget() {
//        return budget;
//    }
//
//    public void setBudget(int budget) {
//        this.budget = budget;
//    }
//
//    public int getNumberOfPeople() {
//        return numberOfPeople;
//    }
//
//    public void setNumberOfPeople(int numberOfPeople) {
//        this.numberOfPeople = numberOfPeople;
//    }
//
//    public LocalDate getStartDate() {
//        return startDate;
//    }
//
//    public void setStartDate(LocalDate startDate) {
//        this.startDate = startDate;
//    }

    public List<Theme> getThemes() {
        return themes;
    }

    public void setThemes(List<Theme> themes) {
        this.themes = themes;
    }
}
