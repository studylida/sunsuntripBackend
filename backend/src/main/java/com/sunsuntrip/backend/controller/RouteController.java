package com.sunsuntrip.backend.controller;

import com.sunsuntrip.backend.domain.Place;
import com.sunsuntrip.backend.domain.RouteResult;
import com.sunsuntrip.backend.domain.Theme;
import com.sunsuntrip.backend.domain.UserCondition;
import com.sunsuntrip.backend.dto.UserConditionRequestDTO;
import com.sunsuntrip.backend.dto.PlaceResponseDTO;
import com.sunsuntrip.backend.dto.RouteResultResponseDTO;
import com.sunsuntrip.backend.dto.ThemeResponseDTO;
import com.sunsuntrip.backend.service.RouteResultMapper;
import com.sunsuntrip.backend.service.UserConditionMapper;
import com.sunsuntrip.backend.repository.PlaceRepository;
import com.sunsuntrip.backend.repository.ThemeRepository;
import com.sunsuntrip.backend.service.RouteAlgorithmService2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RouteController {

    private final RouteAlgorithmService2 routeAlgorithmService;
    private final ThemeRepository themeRepository;
    private final PlaceRepository placeRepository;
    private final UserConditionMapper userConditionMapper;
    private final RouteResultMapper routeResultMapper;

    public RouteController(
            RouteAlgorithmService2 routeAlgorithmService,
            ThemeRepository themeRepository,
            PlaceRepository placeRepository,
            UserConditionMapper userConditionMapper,
            RouteResultMapper routeResultMapper
    ) {
        this.routeAlgorithmService = routeAlgorithmService;
        this.themeRepository = themeRepository;
        this.placeRepository = placeRepository;
        this.userConditionMapper = userConditionMapper;
        this.routeResultMapper = routeResultMapper;
    }

    /**
     * 🔹 사용자 조건 기반 경로 생성 API
     */
    @PostMapping("/route")
    public ResponseEntity<RouteResultResponseDTO> generateRoute(@RequestBody UserConditionRequestDTO requestDTO) {
        UserCondition userCondition = userConditionMapper.toEntity(requestDTO, themeRepository.findAll());
        List<Place> allPlaces = placeRepository.findAllWithThemes();

        RouteResult result = routeAlgorithmService.generateRoute(userCondition, allPlaces);
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
