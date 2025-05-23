package com.sunsuntrip.backend.service;

import com.sunsuntrip.backend.domain.Place;
import com.sunsuntrip.backend.domain.Theme;
import com.sunsuntrip.backend.dto.PlaceDTO;
import com.sunsuntrip.backend.repository.PlaceRepository;
import com.sunsuntrip.backend.util.PlaceCategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    // ✅ 모든 장소 조회
    public List<PlaceDTO> getAllPlaces() {
        return placeRepository.findAll().stream()
                .map(PlaceDTO::fromEntity)
                .toList();
    }

    // ✅ 카테고리별 장소 조회
    public List<PlaceDTO> getPlacesByCategory(Place.PlaceCategory category) {
        return placeRepository.findByCategory(category).stream()
                .map(PlaceDTO::fromEntity)
                .toList();
    }

    // ✅ Google API로 가져온 장소 + 테마 연결하여 저장
    public void saveIfNotExistAndConnectTheme(List<PlaceDTO> dtos, Theme theme) {
        for (PlaceDTO dto : dtos) {
            boolean exists = placeRepository.existsByNameAndLatitudeAndLongitude(
                    dto.getName(), dto.getLatitude(), dto.getLongitude());

            if (!exists) {
                Place place = Place.builder()
                        .name(dto.getName())
                        .description(dto.getDescription())
                        .category(PlaceCategoryMapper.mapFromGoogleType(dto.getCategory()))
                        .latitude(dto.getLatitude())
                        .longitude(dto.getLongitude())
                        .themes(List.of(theme)) // ✅ 테마 연결
                        .build();

                placeRepository.save(place);
                log.info("📌 저장된 장소: {}, 카테고리: {}, 테마: {}", dto.getName(), dto.getCategory(), theme.getName());
            }
        }
    }

    // ✅ 테마 연결 없이 저장 (숙소, 음식점 등)
    public void saveIfNotExistWithoutTheme(List<PlaceDTO> dtos, Place.PlaceCategory fixedCategory) {
        for (PlaceDTO dto : dtos) {
            boolean exists = placeRepository.existsByNameAndLatitudeAndLongitude(
                    dto.getName(), dto.getLatitude(), dto.getLongitude());

            if (!exists) {
                Place place = Place.builder()
                        .name(dto.getName())
                        .description(dto.getDescription())
                        .category(fixedCategory)  // ✅ 직접 전달된 카테고리 사용
                        .latitude(dto.getLatitude())
                        .longitude(dto.getLongitude())
                        .build();

                placeRepository.save(place);
                log.info("📌 저장된 장소(테마 없음): {}, 카테고리: {}", dto.getName(), fixedCategory);
            }
        }
    }

    // ⚙️ 내부 변환용 메서드 (미사용 시 생략 가능)
    private Place convertToEntity(PlaceDTO dto) {
        return Place.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .category(PlaceCategoryMapper.mapFromGoogleType(dto.getCategory()))
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();
    }
}
