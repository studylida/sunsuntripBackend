package com.sunsuntrip.backend.controller;

import com.sunsuntrip.backend.client.GoogleMapsClient;
import com.sunsuntrip.backend.domain.Place;
import com.sunsuntrip.backend.domain.RouteResult;
import com.sunsuntrip.backend.domain.Theme;
import com.sunsuntrip.backend.domain.UserCondition;
import com.sunsuntrip.backend.dto.*;
import com.sunsuntrip.backend.repository.PlaceRepository;
import com.sunsuntrip.backend.repository.ThemeRepository;
import com.sunsuntrip.backend.repository.UserConditionRepository;
import com.sunsuntrip.backend.service.PlaceService;
import com.sunsuntrip.backend.service.RouteAlgorithmService2;
import com.sunsuntrip.backend.service.RouteModificationService;
import com.sunsuntrip.backend.util.RouteResultMapper;
import com.sunsuntrip.backend.util.ThemeKeywordMapper;
import com.sunsuntrip.backend.util.ThemeMinimumPlaceConfig;
import com.sunsuntrip.backend.util.UserConditionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RouteController {

    private final RouteAlgorithmService2 routeAlgorithmService;
    private final ThemeRepository themeRepository;
    private final PlaceRepository placeRepository;
    private final UserConditionRepository userConditionRepository;
    private final UserConditionMapper userConditionMapper;
    private final RouteResultMapper routeResultMapper;
    private final GoogleMapsClient googleMapsClient;
    private final PlaceService placeService;
    private final RouteModificationService routeModificationService;

    @PostMapping("/route/db-only")
    public ResponseEntity<RouteResultResponseDTO> generateRouteFromDbOnly(@RequestBody UserConditionRequestDTO requestDTO) {
        List<Theme> selectedThemes = themeRepository.findAllById(requestDTO.getThemeIds());
        UserCondition userCondition = userConditionMapper.toEntity(requestDTO, selectedThemes);
        userConditionRepository.save(userCondition);
        List<Place> allPlaces = placeRepository.findAllWithThemes();
        RouteResult result = routeAlgorithmService.generateRoute(userCondition, allPlaces);
        RouteResultResponseDTO responseDTO = routeResultMapper.toDTO(result);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/route")
    public ResponseEntity<RouteResultResponseDTO> generateRoute(@RequestBody UserConditionRequestDTO requestDTO) {
        System.out.println("📥 요청된 themeIds: " + requestDTO.getThemeIds());
        List<Theme> selectedThemes = themeRepository.findAllById(requestDTO.getThemeIds());
        System.out.println("📌 실제 조회된 테마 목록: " + selectedThemes.stream().map(Theme::getName).toList());

        UserCondition userCondition = userConditionMapper.toEntity(requestDTO, selectedThemes);
        userConditionRepository.save(userCondition);
        System.out.println("✅ UserCondition.days: " + userCondition.getDays());

        List<Place> allPlaces = placeRepository.findAllWithThemes();

        for (Theme theme : selectedThemes) {
            int minRequired = ThemeMinimumPlaceConfig.getMinimumCountFor(theme.getName()) * userCondition.getDays();
            allPlaces = placeRepository.findAllWithThemes();

            List<Place> themePlaces = allPlaces.stream()
                    .filter(p -> p.getThemes().stream().anyMatch(t -> t.getName().equals(theme.getName())))
                    .toList();

            if (themePlaces.size() < minRequired) {
                int needed = minRequired - themePlaces.size();
                log.info("🟡 Theme '{}' 장소 부족 ({}개 부족) → Google 보완 시도", theme.getName(), needed);
                String keyword = ThemeKeywordMapper.toSearchKeyword(theme.getName());
                List<PlaceDTO> fetched = googleMapsClient.searchByKeyword(keyword);
                List<PlaceDTO> limited = fetched.stream().limit(needed).toList();
                placeService.saveIfNotExistAndConnectTheme(limited, theme);
                allPlaces = placeRepository.findAllWithThemes();
            }
        }

        long accommodationCount = allPlaces.stream()
                .filter(p -> p.getCategory() == Place.PlaceCategory.ACCOMMODATION).count();
        if (accommodationCount < 5 * userCondition.getDays()) {
            int needed = 5 * userCondition.getDays() - (int) accommodationCount;
            log.info("🛏️ 숙소 부족 ({}개 부족) → Google 보완 시도", needed);
            List<String> keywords = List.of("lodging", "hotel", "motel", "guest house");
            List<PlaceDTO> fetched = googleMapsClient.searchByMultipleKeywords(keywords);
            List<PlaceDTO> limited = fetched.stream().limit(needed).toList();
            placeService.saveIfNotExistWithoutTheme(limited, Place.PlaceCategory.ACCOMMODATION);
        }

        long foodCount = allPlaces.stream()
                .filter(p -> p.getCategory() == Place.PlaceCategory.FOOD).count();
        if (foodCount < 10 * userCondition.getDays()) {
            int needed = 10 * userCondition.getDays() - (int) foodCount;
            log.info("🍴 음식점 부족 ({}개 부족) → Google 보완 시도", needed);
            List<String> keywords = List.of("restaurant", "cafe", "bakery", "meal takeaway", "meal delivery", "food");
            List<PlaceDTO> fetched = googleMapsClient.searchByMultipleKeywords(keywords);
            List<PlaceDTO> limited = fetched.stream().limit(needed).toList();
            placeService.saveIfNotExistWithoutTheme(limited, Place.PlaceCategory.FOOD);
        }

        List<Place> updatedPlaces = placeRepository.findAllWithThemes();
        RouteResult result = routeAlgorithmService.generateRoute(userCondition, updatedPlaces);
        RouteResultResponseDTO responseDTO = routeResultMapper.toDTO(result);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/route/{routeId}/replace")
    public ResponseEntity<RouteResultResponseDTO> replacePlaceInRoute(
            @PathVariable Long routeId,
            @RequestParam String oldPlaceName
    ) {
        RouteResult modified = routeModificationService.replacePlaceByName(routeId, oldPlaceName);
        RouteResultResponseDTO responseDTO = routeResultMapper.toDTO(modified);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/themes")
    public ResponseEntity<List<ThemeResponseDTO>> getThemes() {
        List<Theme> themes = themeRepository.findAll();
        List<ThemeResponseDTO> response = themes.stream()
                .map(theme -> new ThemeResponseDTO(theme.getId(), theme.getName()))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/places")
    public ResponseEntity<List<PlaceResponseDTO>> getPlaces() {
        List<Place> places = placeRepository.findAllWithThemes();
        List<PlaceResponseDTO> response = places.stream()
                .map(place -> new PlaceResponseDTO(
                        place.getId(),
                        place.getName(),
                        place.getCategory().name(),
                        place.getLatitude(),
                        place.getLongitude(),
                        place.getThemes().stream().map(Theme::getName).toList()
                ))
                .toList();
        return ResponseEntity.ok(response);
    }
}
