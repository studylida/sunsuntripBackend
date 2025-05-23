package com.sunsuntrip.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data // Getter, Setter, toString, equals, hashCode 자동 생성
@NoArgsConstructor
@AllArgsConstructor
public class RouteResultResponseDTO {

    private Long routeId;           // 🔹 routeId 필드 추가
    private int totalDistance;      // km 단위
    private int totalDuration;      // 분 단위
    private List<DailyPlanDTO> dailyPlans;
}
