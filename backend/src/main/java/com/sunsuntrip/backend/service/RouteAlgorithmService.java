package com.sunsuntrip.backend.service;

import com.sunsuntrip.backend.domain.*;
import com.sunsuntrip.backend.domain.Place.PlaceCategory;

import java.util.*;
import java.util.stream.Collectors;

public class RouteAlgorithmService {

    // 🔵 메인 메서드: 사용자 조건과 장소 리스트를 받아 최적 여행 경로(RouteResult)를 생성
    public RouteResult generateRoute(UserCondition userCondition, List<Place> allPlaces) {

        // 1. 사용자 조건 기반으로 필터링하여 후보지 선정
        List<Place> candidatePlaces = filterPlacesByUserCondition(allPlaces, userCondition);

        if (candidatePlaces.isEmpty()) {
            throw new IllegalStateException("조건에 맞는 장소가 없습니다.");
        }

        // 2. 후보지들 간 거리 행렬 생성
        double[][] distanceMatrix = calculateDistanceMatrix(candidatePlaces);

        // 3. Nearest Neighbor + 2-opt를 이용해 최적 방문 순서 결정
        List<Place> optimizedPlaces = optimizeRoute(candidatePlaces, distanceMatrix);

        // 4. 최적 순서대로, 장소 종류(숙소/관광지/식당)를 고려하여 일별 계획으로 분배
        List<List<Place>> dailyPlans = splitIntoDailyPlansConsideringCategories(optimizedPlaces, userCondition.getDays());

        // 5. 일별 계획을 바탕으로 최종 RouteResult 객체 구성 및 반환
        return buildRouteResult(userCondition, dailyPlans);
    }

    // === 1. 사용자 조건에 맞는 장소 필터링 ===
    private List<Place> filterPlacesByUserCondition(List<Place> places, UserCondition userCondition) {
        System.out.println("=== start ===");

        // 🔵 1. 사용자 조건 (UserCondition) 출력
        System.out.println("--- [사용자 조건] ---");
        System.out.println("여행일수: " + userCondition.getDays());
        System.out.println("예산: " + userCondition.getBudget());
        System.out.println("출발일: " + userCondition.getStartDate());
        System.out.println("군중 회피 여부: " + userCondition.isAvoidCrowd());
        System.out.println("이동 제한 여부: " + userCondition.isMobilityLimitations());
        System.out.println("온천 선호 여부: " + userCondition.isPreferOnsen());
        System.out.println("대중교통만 이용 여부: " + userCondition.isUsePublicTransportOnly());
        System.out.println("여행 인원 수: " + userCondition.getNumberOfPeople());

        System.out.print("선호 테마: ");
        if (userCondition.getThemes() != null && !userCondition.getThemes().isEmpty()) {
            for (Theme theme : userCondition.getThemes()) {
                System.out.print(theme.getName() + " ");
            }
            System.out.println(); // 줄바꿈
        } else {
            System.out.println("없음");
        }

        // 🔵 2. 전체 장소 리스트 출력
        System.out.println("\n--- [장소 리스트] ---");
        for (Place place : places) {
            System.out.println("장소 이름: " + place.getName());

            if (place.getThemes() != null && !place.getThemes().isEmpty()) {
                System.out.print("테마들: ");
                for (Theme theme : place.getThemes()) {
                    System.out.print(theme.getName() + " ");
                }
                System.out.println(); // 줄바꿈
            } else {
                System.out.println("테마 없음");
            }
        }

        // 🔵 3. 실제 필터링 로직
        return places.stream()
                .filter(place ->
                        userCondition.getThemes().stream()
                                .anyMatch(userTheme ->
                                        place.getThemes().stream()
                                                .anyMatch(placeTheme -> placeTheme.getName().equals(userTheme.getName()))
                                )
                )
                .collect(Collectors.toList());

    }



    // === 2. 장소 간 거리 행렬 생성 ===
    private double[][] calculateDistanceMatrix(List<Place> places) {
        int n = places.size();
        double[][] matrix = new double[n][n];

        // 각 장소 간 거리 계산하여 행렬에 저장
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    matrix[i][j] = 0;
                } else {
                    matrix[i][j] = haversineDistance(places.get(i), places.get(j));
                }
            }
        }
        return matrix;
    }

    // === 3. 최적 경로 생성 ===
    private List<Place> optimizeRoute(List<Place> places, double[][] distanceMatrix) {
        // 3-1. Nearest Neighbor로 초기 경로 생성
        List<Place> initialPath = nearestNeighbor(places, distanceMatrix);

        // 3-2. 2-opt를 적용해 경로 최적화
        return twoOpt(initialPath, distanceMatrix);
    }

    // ▪ Nearest Neighbor 알고리즘: 현재 위치에서 가장 가까운 장소 선택 반복
    private List<Place> nearestNeighbor(List<Place> places, double[][] distanceMatrix) {
        List<Place> path = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        int n = places.size();
        int current = 0; // 시작 지점

        path.add(places.get(current));
        visited.add(current);

        // 아직 방문하지 않은 장소가 있을 때까지
        while (visited.size() < n) {
            int nearest = -1;
            double minDist = Double.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (!visited.contains(i) && distanceMatrix[current][i] < minDist) {
                    minDist = distanceMatrix[current][i];
                    nearest = i;
                }
            }

            current = nearest;
            visited.add(current);
            path.add(places.get(current));
        }

        return path;
    }

    // ▪ 2-opt 최적화: 경로 일부를 뒤집어서 더 짧은 경로가 나오면 반영
    private List<Place> twoOpt(List<Place> path, double[][] distanceMatrix) {
        boolean improved = true;
        int n = path.size();

        while (improved) {
            improved = false;
            for (int i = 1; i < n - 2; i++) {
                for (int j = i + 1; j < n - 1; j++) {
                    if (swapImproves(path, i, j, distanceMatrix)) {
                        Collections.reverse(path.subList(i, j + 1)); // 경로 일부 뒤집기
                        improved = true;
                    }
                }
            }
        }
        return path;
    }

    // ▪ 특정 두 구간을 스왑했을 때 경로가 짧아지는지 확인
    private boolean swapImproves(List<Place> path, int i, int j, double[][] distanceMatrix) {
        int prevI = i - 1;
        int start = i;
        int end = j;
        int nextJ = (j + 1) % path.size();

        double oldDist = distanceMatrix[prevI][start] + distanceMatrix[end][nextJ];
        double newDist = distanceMatrix[prevI][end] + distanceMatrix[start][nextJ];

        return newDist < oldDist;
    }

    // === 4. 종류를 고려한 장소 일별 분배 ===
    private List<List<Place>> splitIntoDailyPlansConsideringCategories(List<Place> places, int totalDays) {
        // 4-1. 장소를 종류별로 분류
        List<Place> accommodations = new ArrayList<>();
        List<Place> attractions = new ArrayList<>();
        List<Place> foodPlaces = new ArrayList<>();
        List<Place> shoppingPlaces = new ArrayList<>(); // 🛍️ 쇼핑 추가

        for (Place place : places) {
            if (place.getCategory() == PlaceCategory.ACCOMMODATION) {
                accommodations.add(place);
            } else if (place.getCategory() == PlaceCategory.ATTRACTION) {
                attractions.add(place);
            } else if (place.getCategory() == PlaceCategory.FOOD) {
                foodPlaces.add(place);
            } else if (place.getCategory() == PlaceCategory.SHOPPING) { // 쇼핑 추가
                shoppingPlaces.add(place);
            }
        }


        // 4-2. 일별로 골고루 배치
        List<List<Place>> dailyPlans = new ArrayList<>();
        for (int day = 0; day < totalDays; day++) {
            List<Place> todayPlan = new ArrayList<>();

            if (!accommodations.isEmpty()) todayPlan.add(accommodations.remove(0));

            for (int i = 0; i < 2 && !attractions.isEmpty(); i++) {
                todayPlan.add(attractions.remove(0));
            }

            if (!foodPlaces.isEmpty()) todayPlan.add(foodPlaces.remove(0));
            if (!shoppingPlaces.isEmpty()) todayPlan.add(shoppingPlaces.remove(0)); // 🛍️ 쇼핑 추가

            dailyPlans.add(todayPlan);
        }

        // 4-3. 남은 장소 순차 분배
        List<Place> remaining = new ArrayList<>();
        remaining.addAll(attractions);
        remaining.addAll(foodPlaces);
        remaining.addAll(shoppingPlaces); // 🛍️ 쇼핑 추가

        int idx = 0;
        for (Place place : remaining) {
            dailyPlans.get(idx % totalDays).add(place);
            idx++;
        }


        return dailyPlans;
    }

    // === 5. RouteResult 및 RoutePlace 구성 ===
    private RouteResult buildRouteResult(UserCondition userCondition, List<List<Place>> dailyPlans) {
        RouteResult routeResult = new RouteResult();
        routeResult.setUserCondition(userCondition);

        int visitDay = 1;
        List<RoutePlace> routePlaces = new ArrayList<>();

        // 각 일자별로 RoutePlace 생성
        for (List<Place> dayPlan : dailyPlans) {
            for (Place place : dayPlan) {
                RoutePlace routePlace = new RoutePlace();
                routePlace.setPlace(place);
                routePlace.setVisitDay(visitDay);
                routePlace.setStayMinutes(estimateStayTime(place));
                routePlaces.add(routePlace);
                routePlace.setRouteResult(routeResult);
            }
            visitDay++;
        }

        routeResult.setRoutePlaces(routePlaces);
        routeResult.setTotalDistance(calculateTotalDistance(routePlaces));
        routeResult.setTotalDuration(estimateTotalDuration(routePlaces));

        return routeResult;
    }

    // === 보조 함수들 ===

    // ▪ 전체 이동 거리 계산
    private double calculateTotalDistance(List<RoutePlace> routePlaces) {
        double total = 0;
        for (int i = 0; i < routePlaces.size() - 1; i++) {
            total += haversineDistance(
                    routePlaces.get(i).getPlace(),
                    routePlaces.get(i + 1).getPlace()
            );
        }
        return total;
    }

    // ▪ 전체 소요 시간(체류 + 이동) 계산
    private int estimateTotalDuration(List<RoutePlace> routePlaces) {
        int stayTimeSum = routePlaces.stream().mapToInt(RoutePlace::getStayMinutes).sum();
        int moveTimeSum = 0;

        for (int i = 0; i < routePlaces.size() - 1; i++) {
            double distanceKm = haversineDistance(
                    routePlaces.get(i).getPlace(),
                    routePlaces.get(i + 1).getPlace()
            );
            moveTimeSum += estimateMoveTime(distanceKm);
        }
        return stayTimeSum + moveTimeSum;
    }

    // ▪ Haversine 공식을 이용한 장소 간 거리 계산
    private double haversineDistance(Place a, Place b) {
        double R = 6371.0; // 지구 반지름 (km)
        double lat1 = Math.toRadians(a.getLatitude());
        double lon1 = Math.toRadians(a.getLongitude());
        double lat2 = Math.toRadians(b.getLatitude());
        double lon2 = Math.toRadians(b.getLongitude());

        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;

        double haversine = Math.sin(dlat / 2) * Math.sin(dlat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dlon / 2) * Math.sin(dlon / 2);

        double c = 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));

        return R * c;
    }

    // ▪ 거리(km)로 이동 시간(분) 추정 (도보 기준)
    private int estimateMoveTime(double distanceKm) {
        double walkingSpeed = 4.0; // km/h
        double timeHours = distanceKm / walkingSpeed;
        return (int) (timeHours * 60);
    }

    // ▪ 장소 종류에 따른 체류 시간 추정
    private int estimateStayTime(Place place) {
        if (place.getCategory() == PlaceCategory.ATTRACTION) return 120;
        if (place.getCategory() == PlaceCategory.FOOD) return 90;
        if (place.getCategory() == PlaceCategory.SHOPPING) return 60;
        if (place.getCategory() == PlaceCategory.ACCOMMODATION) return 480;
        return 60;
    }
}
