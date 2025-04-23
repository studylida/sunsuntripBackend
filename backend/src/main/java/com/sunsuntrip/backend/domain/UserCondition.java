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
    private int budget;
    private int numberOfPeople;
    private LocalDate startDate;

    private boolean avoidCrowd;
    private boolean preferOnsen;
    private boolean usePublicTransportOnly;
    private boolean mobilityLimitations;

    @ManyToMany
    @JoinTable(
            name = "usercondition_theme",
            joinColumns = @JoinColumn(name = "usercondition_id"),
            inverseJoinColumns = @JoinColumn(name = "theme_id")
    )
    private List<Theme> themes = new ArrayList<>();
}
