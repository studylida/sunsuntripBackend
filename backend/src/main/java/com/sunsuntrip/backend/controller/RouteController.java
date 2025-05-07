package com.sunsuntrip.backend.controller;

import com.sunsuntrip.backend.client.GoogleMapsClient;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
        try {// 1. 사용자 조건 → Entity 변환 (선택한 Theme ID 기준)
            List<Theme> selectedThemes = themeRepository.findAllById(requestDTO.getThemeIds());
            UserCondition userCondition = userConditionMapper.toEntity(requestDTO, selectedThemes);

            // 2. 현재 DB의 모든 장소 조회 (테마 포함)
            List<Place> places = placeRepository.findAllWithThemes();

            // 3. 각 테마별로 장소가 부족하면 Google Maps API를 통해 장소 보충
            for (Theme theme : selectedThemes) {
                long count = places.stream()
                        .filter(p -> p.getThemes().stream().anyMatch(t -> t.getName().equals(theme.getName())))
                        .count();

                if (count < 15) { // 최소 15개 미만인 경우 보충
                    String keyword = ThemeKeywordMapper.toSearchKeyword(theme.getName());
                    List<PlaceDTO> fetchedPlaces = googleMapsClient.searchByKeyword(keyword);
                    placeService.saveIfNotExist(fetchedPlaces);
                }
            }

            // 4. DB에서 다시 장소를 읽어 최신 상태 반영
            List<Place> allPlaces = placeRepository.findAllWithThemes();

            // 5. 경로 생성
            RouteResult result = routeAlgorithmService.generateRoute(userCondition, allPlaces);

            // 6. 응답 DTO로 변환
            RouteResultResponseDTO responseDTO = routeResultMapper.toDTO(result);

            return ResponseEntity.ok(responseDTO);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
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
