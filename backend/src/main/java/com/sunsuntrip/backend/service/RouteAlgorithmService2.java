package com.sunsuntrip.backend.service;

import com.sunsuntrip.backend.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RouteAlgorithmService2 {

    public RouteResult generateRoute(UserCondition userCondition, List<Place> allPlaces) {
        // 1. 사용자 조건 기반 필터링
        List<Place> candidates = filterPlacesByUserCondition(allPlaces, userCondition);

        // 2. 카테고리별 분류
        List<Place> accommodations = filterByCategory(candidates, Place.PlaceCategory.ACCOMMODATION);
        List<Place> foods = filterByCategory(candidates, Place.PlaceCategory.FOOD);
        List<Place> attractions = filterByCategory(candidates, Place.PlaceCategory.ATTRACTION);
        List<Place> shoppings = filterByCategory(candidates, Place.PlaceCategory.SHOPPING);
        List<Place> coreSpots = new ArrayList<>();
        coreSpots.addAll(attractions);
        coreSpots.addAll(shoppings);

        // 3. 하루 일정 경로 최적화 반복
        int totalDays = userCondition.getDays();
        List<List<Place>> dailyPlans = new ArrayList<>();
        Place prevAccommodation = null;

        for (int day = 1; day <= totalDays; day++) {
            boolean isLastDay = (day == totalDays);

            List<Place> todayPlan = planOneDay(
                    prevAccommodation, foods, coreSpots, accommodations, isLastDay
            );

            prevAccommodation = isLastDay ? null : todayPlan.get(todayPlan.size() - 1); // 숙소
            dailyPlans.add(todayPlan);
        }

        // 4. RouteResult 생성
        return buildRouteResult(userCondition, dailyPlans);
    }

    // === 하루치 일정 최적화 ===
    private List<Place> planOneDay(
            Place prevAccommodation,
            List<Place> foods,
            List<Place> coreSpots,
            List<Place> accommodations,
            boolean isLastDay
    ) {
        List<Place> result = new ArrayList<>();

        // 선택 시 null 방지 확인
        Place breakfast = selectNearest(prevAccommodation, foods);
        if (breakfast == null) throw new IllegalStateException("아침 식당 장소가 부족합니다.");
        result.add(breakfast);
        foods.remove(breakfast);

        Place spotA = selectNearest(breakfast, coreSpots);
        if (spotA == null) throw new IllegalStateException("관광지/쇼핑 장소가 부족합니다.");
        result.add(spotA);
        coreSpots.remove(spotA);

        Place lunch = selectNearest(spotA, foods);
        if (lunch == null) throw new IllegalStateException("점심 식당 장소가 부족합니다.");
        result.add(lunch);
        foods.remove(lunch);

        List<Place> next2 = selectNearestN(lunch, coreSpots, 5);
        if (next2.size() < 2) throw new IllegalStateException("오후 관광지 후보가 부족합니다.");
        Place[] pair = findClosestPair(next2);
        result.add(pair[0]);
        result.add(pair[1]);
        coreSpots.remove(pair[0]);
        coreSpots.remove(pair[1]);

        if (!isLastDay) {
            Place dinner = selectNearest(pair[1], foods);
            if (dinner == null) throw new IllegalStateException("저녁 식당 장소가 부족합니다.");
            result.add(dinner);
            foods.remove(dinner);

            Place spotD = selectNearest(dinner, coreSpots);
            if (spotD == null) throw new IllegalStateException("저녁 이후 관광 장소가 부족합니다.");
            result.add(spotD);
            coreSpots.remove(spotD);

            Place accommodation = selectNearest(spotD, accommodations);
            if (accommodation == null) throw new IllegalStateException("숙소 장소가 부족합니다.");
            result.add(accommodation);
            accommodations.remove(accommodation);
        }

        return result;
    }

    // === 필터링 ===
    private List<Place> filterPlacesByUserCondition(List<Place> places, UserCondition condition) {
        return places.stream()
                .filter(place -> condition.getThemes().stream()
                        .anyMatch(userTheme ->
                                place.getThemes().stream()
                                        .anyMatch(placeTheme -> placeTheme.getName().equals(userTheme.getName()))
                        ))
                .collect(Collectors.toList());
    }

    private List<Place> filterByCategory(List<Place> places, Place.PlaceCategory category) {
        return places.stream()
                .filter(p -> p.getCategory() == category)
                .collect(Collectors.toList());
    }

    // === 선택 로직 ===
    private Place selectNearest(Place from, List<Place> options) {
        if (options == null || options.isEmpty()) return null;

        if (from == null) {
            return options.get(new Random().nextInt(Math.min(3, options.size())));
        }

        return options.stream()
                .min(Comparator.comparingDouble(p -> haversineDistance(from, p)))
                .orElse(null); // ✅ null 반환
    }

    private List<Place> selectNearestN(Place from, List<Place> options, int n) {
        return options.stream()
                .sorted(Comparator.comparingDouble(p -> haversineDistance(from, p)))
                .limit(n)
                .collect(Collectors.toList());
    }

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

    // === 결과 생성 ===
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

    // === 거리, 시간 계산 ===
    private double haversineDistance(Place a, Place b) {
        double R = 6371;
        double dLat = Math.toRadians(b.getLatitude() - a.getLatitude());
        double dLon = Math.toRadians(b.getLongitude() - a.getLongitude());
        double lat1 = Math.toRadians(a.getLatitude());
        double lat2 = Math.toRadians(b.getLatitude());

        double aHarv = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(aHarv), Math.sqrt(1 - aHarv));
        return R * c;
    }

    private int estimateStayTime(Place place) {
        return switch (place.getCategory()) {
            case ATTRACTION -> 120;
            case FOOD -> 90;
            case SHOPPING -> 60;
            case ACCOMMODATION -> 480;
        };
    }

    private double calculateTotalDistance(List<RoutePlace> routePlaces) {
        double sum = 0;
        for (int i = 0; i < routePlaces.size() - 1; i++) {
            sum += haversineDistance(routePlaces.get(i).getPlace(), routePlaces.get(i + 1).getPlace());
        }
        return sum;
    }

    private int estimateTotalDuration(List<RoutePlace> routePlaces) {
        int stay = routePlaces.stream().mapToInt(RoutePlace::getStayMinutes).sum();
        int move = 0;
        for (int i = 0; i < routePlaces.size() - 1; i++) {
            move += estimateMoveTime(haversineDistance(routePlaces.get(i).getPlace(), routePlaces.get(i + 1).getPlace()));
        }
        return stay + move;
    }

    private int estimateMoveTime(double distanceKm) {
        return (int) ((distanceKm / 4.0) * 60); // 도보 기준 4km/h
    }
}
