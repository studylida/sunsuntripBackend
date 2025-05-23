package com.sunsuntrip.backend.util;

import com.sunsuntrip.backend.domain.Place;
import com.sunsuntrip.backend.domain.RoutePlace;
import com.sunsuntrip.backend.domain.RouteResult;
import com.sunsuntrip.backend.domain.Theme;
import com.sunsuntrip.backend.dto.DailyPlanDTO;
import com.sunsuntrip.backend.dto.RoutePlaceDTO;
import com.sunsuntrip.backend.dto.RouteResultResponseDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class RouteResultMapper {

    // ✅ 정적 메서드 버전 (routeId 포함)
    public static RouteResultResponseDTO toDto(RouteResult routeResult) {
        RouteResultResponseDTO dto = new RouteResultResponseDTO();

        dto.setRouteId(routeResult.getId()); // 🔹 routeId 설정
        dto.setTotalDistance((int) routeResult.getTotalDistance());
        dto.setTotalDuration(routeResult.getTotalDuration());

        // RoutePlace를 일자별로 묶어서 DailyPlanDTO 리스트 생성
        Map<Integer, List<RoutePlace>> groupedByDay = routeResult.getRoutePlaces().stream()
                .collect(Collectors.groupingBy(RoutePlace::getVisitDay, TreeMap::new, Collectors.toList()));

        List<DailyPlanDTO> dailyPlans = new ArrayList<>();

        for (Map.Entry<Integer, List<RoutePlace>> entry : groupedByDay.entrySet()) {
            int day = entry.getKey();
            List<RoutePlace> placesForDay = entry.getValue();

            DailyPlanDTO dailyPlan = new DailyPlanDTO();
            dailyPlan.setDay(day);

            List<RoutePlaceDTO> routePlaceDTOs = placesForDay.stream()
                    .map(RouteResultMapper::toRoutePlaceDto)
                    .collect(Collectors.toList());

            dailyPlan.setPlaces(routePlaceDTOs);
            dailyPlans.add(dailyPlan);
        }

        dto.setDailyPlans(dailyPlans);

        return dto;
    }

    // ✅ 비정적 메서드 버전 (routeId 포함)
    public RouteResultResponseDTO toDTO(RouteResult routeResult) {
        RouteResultResponseDTO dto = new RouteResultResponseDTO();

        dto.setRouteId(routeResult.getId()); // 🔹 routeId 설정
        dto.setTotalDistance((int) routeResult.getTotalDistance());
        dto.setTotalDuration(routeResult.getTotalDuration());

        // 일자별 RoutePlace를 그룹화 (visitDay 기준)
        Map<Integer, List<RoutePlace>> grouped = routeResult.getRoutePlaces().stream()
                .collect(Collectors.groupingBy(RoutePlace::getVisitDay));

        List<DailyPlanDTO> dailyPlans = new ArrayList<>();
        grouped.keySet().stream().sorted().forEach(day -> {
            DailyPlanDTO dailyDTO = new DailyPlanDTO();
            dailyDTO.setDay(day);

            List<RoutePlaceDTO> places = grouped.get(day).stream().map(routePlace -> {
                Place place = routePlace.getPlace();
                return new RoutePlaceDTO(
                        place.getId(),
                        place.getName(),
                        place.getCategory().name(), // PlaceCategory → String
                        place.getThemes().stream()
                                .map(Theme::getName)
                                .collect(Collectors.toList()),
                        place.getLatitude(),
                        place.getLongitude(),
                        routePlace.getStayMinutes()
                );
            }).collect(Collectors.toList());

            dailyDTO.setPlaces(places);
            dailyPlans.add(dailyDTO);
        });

        dto.setDailyPlans(dailyPlans);
        return dto;
    }

    // ✅ RoutePlace → RoutePlaceDTO 변환
    private static RoutePlaceDTO toRoutePlaceDto(RoutePlace routePlace) {
        Place place = routePlace.getPlace();

        RoutePlaceDTO dto = new RoutePlaceDTO();
        dto.setId(place.getId());
        dto.setName(place.getName());
        dto.setCategory(place.getCategory().toString());
        dto.setLatitude(place.getLatitude());
        dto.setLongitude(place.getLongitude());
        dto.setStayMinutes(routePlace.getStayMinutes());

        if (place.getThemes() != null) {
            List<String> themeNames = place.getThemes().stream()
                    .map(Theme::getName)
                    .collect(Collectors.toList());
            dto.setThemes(themeNames);
        }

        return dto;
    }
}
