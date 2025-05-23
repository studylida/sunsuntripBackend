package com.sunsuntrip.backend.service;

import com.sunsuntrip.backend.domain.*;
import com.sunsuntrip.backend.repository.RouteResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RouteAlgorithmService2 {

    private final RouteResultRepository routeResultRepository;

    public RouteAlgorithmService2(RouteResultRepository routeResultRepository) {
        this.routeResultRepository = routeResultRepository;
    }

    /**
     * 경로 생성을 위한 메인 진입점.
     * 사용자 조건과 전체 장소 리스트를 받아 일자별 여행 일정을 생성.
     */
    @Transactional
    public RouteResult generateRoute(UserCondition userCondition, List<Place> allPlaces) {
        // ✅ 카테고리별 장소 분류
        List<Place> accommodations = filterByCategory(allPlaces, Place.PlaceCategory.ACCOMMODATION);
        for(Place accommodation : accommodations) {
            System.out.println("숙소" + accommodation.getName());
        }
        List<Place> foods = filterByCategory(allPlaces, Place.PlaceCategory.FOOD);
        for(Place food : foods) {
            System.out.println("음식점" + food.getName());
        }
        List<Place> attractions = filterByCategory(allPlaces, Place.PlaceCategory.ATTRACTION);
        for(Place attraction : attractions) {
            System.out.println("관광지" + attraction.getName());
        }

        // ✅ 관광지: 사용자 선호 테마 기반으로 우선순위 정렬
        List<Place> prioritizedAttractions = prioritizeByTheme(attractions, userCondition.getThemes());

        // ✅ 디버깅 로그 출력
        System.out.println("🏨 숙소 수: " + accommodations.size());
        System.out.println("🍽️ 음식점 수: " + foods.size());
        System.out.println("🎯 관광지 수: " + attractions.size());
        System.out.println("🔵 우선순위 관광지 수: " + prioritizedAttractions.size());

        // ✅ 일자별 경로 생성 루프
        int totalDays = userCondition.getDays();
        List<List<Place>> dailyPlans = new ArrayList<>();
        Place prevAccommodation = null;

        for (int day = 1; day <= totalDays; day++) {
            System.out.println("⏱ Day " + day + " 시작, 남은 관광지 수: " + prioritizedAttractions.size());
            boolean isLastDay = (day == totalDays);

            List<Place> todayPlan = planOneDay(
                    prevAccommodation, foods, prioritizedAttractions, accommodations, isLastDay
            );

            dailyPlans.add(todayPlan);
            prevAccommodation = isLastDay ? null : todayPlan.get(todayPlan.size() - 1); // 마지막 숙소 저장
        }

        RouteResult result = buildRouteResult(userCondition, dailyPlans);

        return routeResultRepository.save(result);
    }

    /**
     * 하루치 일정 생성 로직.
     * 식당/관광지/숙소 리스트에서 장소를 선택해 하나의 일정을 구성함.
     */
    private List<Place> planOneDay(
            Place prevAccommodation,
            List<Place> foods,
            List<Place> attractions,
            List<Place> accommodations,
            boolean isLastDay
    ) {
        List<Place> result = new ArrayList<>();

        // 아침
        Place breakfast = selectNearest(prevAccommodation, foods);
        if (breakfast == null) throw new IllegalStateException("아침 식당 장소가 부족합니다.");
        result.add(breakfast);
        foods.remove(breakfast);

        // 오전 관광
        Place spotA = selectNearest(breakfast, attractions);
        if (spotA == null) throw new IllegalStateException("관광지 장소가 부족합니다.");
        result.add(spotA);
        attractions.remove(spotA);

        // 점심
        Place lunch = selectNearest(spotA, foods);
        if (lunch == null) throw new IllegalStateException("점심 식당 장소가 부족합니다.");
        result.add(lunch);
        foods.remove(lunch);

        // 오후 관광 (2곳)
        List<Place> next2 = selectNearestN(lunch, attractions, 5);
        if (next2.size() < 2) throw new IllegalStateException("오후 관광지 후보가 부족합니다.");
        Place[] pair = findClosestPair(next2);
        result.add(pair[0]);
        result.add(pair[1]);
        attractions.remove(pair[0]);
        attractions.remove(pair[1]);

        // 저녁 + 숙소 (마지막 날이 아닐 때만)
        if (!isLastDay) {
            Place dinner = selectNearest(pair[1], foods);
            if (dinner == null) throw new IllegalStateException("저녁 식당 장소가 부족합니다.");
            result.add(dinner);
            foods.remove(dinner);

            Place eveningSpot = selectNearest(dinner, attractions);
            if (eveningSpot == null) throw new IllegalStateException("저녁 이후 관광 장소가 부족합니다.");
            result.add(eveningSpot);
            attractions.remove(eveningSpot);

            Place accommodation = selectNearest(eveningSpot, accommodations);
            if (accommodation == null) throw new IllegalStateException("숙소 장소가 부족합니다.");
            result.add(accommodation);
            accommodations.remove(accommodation);
        }

        return result;
    }

    /**
     * 주어진 카테고리 기준으로 장소 필터링
     */
    private List<Place> filterByCategory(List<Place> places, Place.PlaceCategory category) {
        return places.stream()
                .filter(p -> p.getCategory() == category)
                .collect(Collectors.toList());
    }

    /**
     * 사용자 선호 테마와 일치하는 관광지를 우선적으로 정렬.
     * 일치하지 않는 관광지도 포함하여 보완 가능하도록 설정.
     */
    private List<Place> prioritizeByTheme(List<Place> attractions, List<Theme> preferredThemes) {
        Set<String> preferredThemeNames = preferredThemes.stream()
                .map(Theme::getName)
                .collect(Collectors.toSet());

        List<Place> preferred = new ArrayList<>();
        List<Place> fallback = new ArrayList<>();

        for (Place p : attractions) {
            boolean matches = p.getThemes().stream().anyMatch(t -> preferredThemeNames.contains(t.getName()));
            if (matches) preferred.add(p);
            else fallback.add(p);
        }

        preferred.addAll(fallback);
        return preferred;
    }

    /**
     * 기준 장소에서 가장 가까운 장소 선택
     */
    private Place selectNearest(Place from, List<Place> options) {
        if (options == null || options.isEmpty()) return null;
        if (from == null) return options.get(new Random().nextInt(Math.min(3, options.size())));
        return options.stream()
                .min(Comparator.comparingDouble(p -> haversineDistance(from, p)))
                .orElse(null);
    }

    /**
     * 기준 장소에서 가까운 n개 장소 선택
     */
    private List<Place> selectNearestN(Place from, List<Place> options, int n) {
        return options.stream()
                .sorted(Comparator.comparingDouble(p -> haversineDistance(from, p)))
                .limit(n)
                .collect(Collectors.toList());
    }

    /**
     * 주어진 장소 리스트 중 가장 가까운 두 장소를 반환
     */
    private Place[] findClosestPair(List<Place> places) {
        double minDist = Double.MAX_VALUE;
        Place[] result = new Place[2];
        for (int i = 0; i < places.size(); i++) {
            for (int j = i + 1; j < places.size(); j++) {
                double dist = haversineDistance(places.get(i), places.get(j));
                if (dist < minDist) {
                    minDist = dist;
                    result[0] = places.get(i);
                    result[1] = places.get(j);
                }
            }
        }
        return result;
    }

    /**
     * 최종적으로 사용자 조건과 장소 계획으로 RouteResult를 구성
     */
    private RouteResult buildRouteResult(UserCondition condition, List<List<Place>> dailyPlans) {
        RouteResult result = new RouteResult();
        result.setUserCondition(condition);
        List<RoutePlace> routePlaces = new ArrayList<>();
        int visitDay = 1;

        for (List<Place> daily : dailyPlans) {
            for (Place place : daily) {
                RoutePlace rp = new RoutePlace();
                rp.setVisitDay(visitDay);
                rp.setPlace(place);
                rp.setStayMinutes(estimateStayTime(place));
                rp.setRouteResult(result);
                routePlaces.add(rp);
            }
            visitDay++;
        }

        result.setRoutePlaces(routePlaces);
        result.setTotalDistance(calculateTotalDistance(routePlaces));
        result.setTotalDuration(estimateTotalDuration(routePlaces));
        return result;
    }

    /**
     * 두 좌표 간 거리 계산 (단위: km)
     */
    private double haversineDistance(Place a, Place b) {
        double R = 6371;
        double dLat = Math.toRadians(b.getLatitude() - a.getLatitude());
        double dLon = Math.toRadians(b.getLongitude() - a.getLongitude());
        double lat1 = Math.toRadians(a.getLatitude());
        double lat2 = Math.toRadians(b.getLatitude());

        double aHarv = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(aHarv), Math.sqrt(1 - aHarv));
        return R * c;
    }

    /**
     * 장소 카테고리에 따라 체류 시간 추정
     */
    private int estimateStayTime(Place place) {
        return switch (place.getCategory()) {
            case ATTRACTION -> 120;
            case FOOD -> 90;
            case SHOPPING -> 60;
            case ACCOMMODATION -> 480;
        };
    }

    /**
     * 총 이동 거리 계산
     */
    private double calculateTotalDistance(List<RoutePlace> routePlaces) {
        double sum = 0;
        for (int i = 0; i < routePlaces.size() - 1; i++) {
            sum += haversineDistance(routePlaces.get(i).getPlace(), routePlaces.get(i + 1).getPlace());
        }
        return sum;
    }

    /**
     * 총 소요 시간 계산 (이동 + 체류)
     */
    private int estimateTotalDuration(List<RoutePlace> routePlaces) {
        int stay = routePlaces.stream().mapToInt(RoutePlace::getStayMinutes).sum();
        int move = 0;
        for (int i = 0; i < routePlaces.size() - 1; i++) {
            move += estimateMoveTime(haversineDistance(routePlaces.get(i).getPlace(), routePlaces.get(i + 1).getPlace()));
        }
        return stay + move;
    }

    /**
     * 거리(km) 기준으로 이동 시간 예측 (도보 4km/h 기준)
     */
    private int estimateMoveTime(double distanceKm) {
        return (int) ((distanceKm / 4.0) * 60);
    }
}
