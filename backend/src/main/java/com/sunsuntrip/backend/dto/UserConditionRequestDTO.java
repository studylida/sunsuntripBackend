package com.sunsuntrip.backend.dto;

import java.time.LocalDate;
import java.util.List;

public class UserConditionRequestDTO {
    private int days;
//    private int budget;
//    private int numberOfPeople;
//    private LocalDate startDate;
    private List<Long> themeIds;  // Theme ID 목록

    public UserConditionRequestDTO(int days, List<Long> themeIds) {
        this.days = days;
//        this.budget = budget;
//        this.numberOfPeople = numberOfPeople;
//        this.startDate = startDate;
        this.themeIds = themeIds;
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

    public List<Long> getThemeIds() {
        return themeIds;
    }

    public void setThemeIds(List<Long> themeIds) {
        this.themeIds = themeIds;
    }
}
