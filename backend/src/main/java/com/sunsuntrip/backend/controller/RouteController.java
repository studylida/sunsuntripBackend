package com.sunsuntrip.backend.controller;

import com.sunsuntrip.backend.client.GoogleMapsClient;
import com.sunsuntrip.backend.util.ThemeMinimumPlaceConfig;
import com.sunsuntrip.backend.domain.Place;
import com.sunsuntrip.backend.domain.RouteResult;
import com.sunsuntrip.backend.domain.Theme;
import com.sunsuntrip.backend.domain.UserCondition;
import com.sunsuntrip.backend.dto.*;
import com.sunsuntrip.backend.service.PlaceService;
import com.sunsuntrip.backend.util.RouteResultMapper;
import com.sunsuntrip.backend.util.ThemeKeywordMapper;
import com.sunsuntrip.backend.util.UserConditionMapper;
import com.sunsuntrip.backend.repository.PlaceRepository;
import com.sunsuntrip.backend.repository.ThemeRepository;
import com.sunsuntrip.backend.service.RouteAlgorithmService2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class RouteController {

    private final RouteAlgorithmService2 routeAlgorithmService;
    private final ThemeRepository themeRepository;
    private final PlaceRepository placeRepository;
    private final UserConditionMapper userConditionMapper;
    private final RouteResultMapper routeResultMapper;
    private final GoogleMapsClient googleMapsClient;
    private final PlaceService placeService;

    public RouteController(
            RouteAlgorithmService2 routeAlgorithmService,
            ThemeRepository themeRepository,
            PlaceRepository placeRepository,
            UserConditionMapper userConditionMapper,
            RouteResultMapper routeResultMapper,
            GoogleMapsClient googleMapsClient,
            PlaceService placeService
    ) {
        this.routeAlgorithmService = routeAlgorithmService;
        this.themeRepository = themeRepository;
        this.placeRepository = placeRepository;
        this.userConditionMapper = userConditionMapper;
        this.routeResultMapper = routeResultMapper;
        this.googleMapsClient = googleMapsClient;
        this.placeService = placeService;
    }

    /**
     * 🔹 사용자 조건 기반 경로 생성 API
     */
//    @PostMapping("/route")
//    public ResponseEntity<RouteResultResponseDTO> generateRoute(@RequestBody UserConditionRequestDTO requestDTO) {
//        UserCondition userCondition = userConditionMapper.toEntity(requestDTO, themeRepository.findAll());
//        List<Place> allPlaces = placeRepository.findAllWithThemes();
//
//        RouteResult result = routeAlgorithmService.generateRoute(userCondition, allPlaces);
//        RouteResultResponseDTO responseDTO = routeResultMapper.toDTO(result);
//
//        return ResponseEntity.ok(responseDTO);
//    }
    @PostMapping("/route")
    public ResponseEntity<RouteResultResponseDTO> generateRoute(@RequestBody UserConditionRequestDTO requestDTO) {
        // 1. 사용자 조건 → Entity 변환
        List<Theme> selectedThemes = themeRepository.findAllById(requestDTO.getThemeIds());
        UserCondition userCondition = userConditionMapper.toEntity(requestDTO, selectedThemes);

        // 2. DB에서 모든 장소 조회 (테마 포함)
        List<Place> allPlaces = placeRepository.findAllWithThemes();

        // 3. 테마별 최소 장소 수 기준 보완
        for (Theme theme : selectedThemes) {
            int minRequired = ThemeMinimumPlaceConfig.getMinimumCountFor(theme.getName());

            List<Place> themePlaces = allPlaces.stream()
                    .filter(p -> p.getThemes().stream().anyMatch(t -> t.getName().equals(theme.getName())))
                    .toList();

            if (themePlaces.size() < minRequired) {
                int needed = minRequired - themePlaces.size();
                log.info("🟡 Theme '{}' 장소 부족 ({}개 부족) → Google 보완 시도", theme.getName(), needed);

                String keyword = ThemeKeywordMapper.toSearchKeyword(theme.getName());
                List<PlaceDTO> fetched = googleMapsClient.searchByKeyword(keyword);
                List<PlaceDTO> limited = fetched.stream().limit(needed).toList();

                // 🔧 저장 시 로깅 포함
                for (PlaceDTO dto : limited) {
                    var category = dto.getCategory();
                    log.info("📌 [추가 장소] 이름: {}, 원본 category: {}, 테마: {}", dto.getName(), category, theme.getName());
                }

                placeService.saveIfNotExistAndConnectTheme(limited, theme);
            }
        }

        // 4. 최신 장소 다시 조회
        List<Place> updatedPlaces = placeRepository.findAllWithThemes();

        // 5. 경로 생성
        RouteResult result = routeAlgorithmService.generateRoute(userCondition, updatedPlaces);

        // 6. 응답 변환
        RouteResultResponseDTO responseDTO = routeResultMapper.toDTO(result);

        return ResponseEntity.ok(responseDTO);
    }



    /**
     * 🔹 테마 목록 조회 API
     */
    @GetMapping("/themes")
    public ResponseEntity<List<ThemeResponseDTO>> getThemes() {
        List<Theme> themes = themeRepository.findAll();
        List<ThemeResponseDTO> response = themes.stream()
                .map(theme -> new ThemeResponseDTO(theme.getId(), theme.getName()))
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * 🔹 장소 목록 조회 API (테스트 및 디버깅용)
     */
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
