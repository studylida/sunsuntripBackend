package com.sunsuntrip.backend.service;

import com.sunsuntrip.backend.domain.Place;
import com.sunsuntrip.backend.domain.RoutePlace;
import com.sunsuntrip.backend.domain.RouteResult;
import com.sunsuntrip.backend.repository.PlaceRepository;
import com.sunsuntrip.backend.repository.RouteResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteModificationService {

    private final RouteResultRepository routeResultRepository;
    private final PlaceRepository placeRepository;

    @Transactional
    public RouteResult replacePlaceByName(Long routeResultId, String oldPlaceName) {
        // 1. 기존 경로 불러오기
        RouteResult result = routeResultRepository.findByIdWithPlaces(routeResultId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 존재하지 않는 경로 ID: " + routeResultId));

        List<RoutePlace> routePlaces = result.getRoutePlaces();

        // 2. 교체할 장소 찾기
        RoutePlace toReplace = routePlaces.stream()
                .filter(rp -> rp.getPlace().getName().equalsIgnoreCase(oldPlaceName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("❌ 해당 이름의 장소가 경로에 없습니다: " + oldPlaceName));

        Place oldPlace = toReplace.getPlace();
        int day = toReplace.getVisitDay();

        // 3. 현재 경로에 사용된 장소 ID 목록
        Set<Long> usedPlaceIds = routePlaces.stream()
                .map(rp -> rp.getPlace().getId())
                .collect(Collectors.toSet());

        // 4. 후보 장소 선정 (같은 카테고리 & 미사용)
        List<Place> candidates = placeRepository.findByCategory(oldPlace.getCategory()).stream()
                .filter(p -> !usedPlaceIds.contains(p.getId()))
                .toList();

        if (candidates.isEmpty()) {
            throw new IllegalStateException("❌ 후보 장소가 없습니다.");
        }

        // 5. 기준점(anchor) 판단
        Place anchor;
        boolean isFirstOfDay = routePlaces.stream()
                .filter(rp -> rp.getVisitDay() == day)
                .findFirst()
                .map(first -> first.getPlace().getId().equals(oldPlace.getId()))
                .orElse(false);

        if (day == 1 && isFirstOfDay) {
            anchor = findNextPlace(routePlaces, toReplace);
        } else if (isFirstOfDay) {
            anchor = findPreviousAccommodation(routePlaces, day);
        } else {
            anchor = findPreviousPlace(routePlaces, toReplace);
        }

        if (anchor == null) {
            throw new IllegalStateException("❌ 기준점을 찾을 수 없습니다.");
        }

        // 6. anchor 기준 거리순 정렬 → 상위 3개 중 미사용 후보 채택
        List<Place> nearest = candidates.stream()
                .sorted(Comparator.comparingDouble(p -> haversine(anchor, p)))
                .limit(3)
                .collect(Collectors.toList());

        for (Place candidate : nearest) {
            if (!usedPlaceIds.contains(candidate.getId())) {
                toReplace.setPlace(candidate);
                return result;
            }
        }

        throw new IllegalStateException("❌ 교체할 수 있는 후보 장소를 찾지 못했습니다.");
    }

    private Place findNextPlace(List<RoutePlace> routePlaces, RoutePlace current) {
        return routePlaces.stream()
                .filter(rp -> rp.getVisitDay() == current.getVisitDay())
                .dropWhile(rp -> !rp.equals(current))
                .skip(1)
                .map(RoutePlace::getPlace)
                .findFirst()
                .orElse(null);
    }

    private Place findPreviousAccommodation(List<RoutePlace> routePlaces, int day) {
        return routePlaces.stream()
                .filter(rp -> rp.getVisitDay() == (day - 1))
                .sorted(Comparator.comparing(RoutePlace::getId).reversed())
                .map(RoutePlace::getPlace)
                .findFirst()
                .orElse(null);
    }

    private Place findPreviousPlace(List<RoutePlace> routePlaces, RoutePlace current) {
        List<RoutePlace> dayPlaces = routePlaces.stream()
                .filter(rp -> rp.getVisitDay() == current.getVisitDay())
                .toList();
        int index = dayPlaces.indexOf(current);
        if (index > 0) {
            return dayPlaces.get(index - 1).getPlace();
        }
        return null;
    }

    private double haversine(Place a, Place b) {
        double R = 6371;
        double dLat = Math.toRadians(b.getLatitude() - a.getLatitude());
        double dLon = Math.toRadians(b.getLongitude() - a.getLongitude());
        double lat1 = Math.toRadians(a.getLatitude());
        double lat2 = Math.toRadians(b.getLatitude());
        double aHarv = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(aHarv), Math.sqrt(1 - aHarv));
        return R * c;
    }
}
