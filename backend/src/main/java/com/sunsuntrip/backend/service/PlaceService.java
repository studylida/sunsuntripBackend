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

    public List<PlaceDTO> getAllPlaces() {
        return placeRepository.findAll().stream()
                .map(PlaceDTO::fromEntity)
                .toList();
    }

    public List<PlaceDTO> getPlacesByCategory(Place.PlaceCategory category) {
        return placeRepository.findByCategory(category).stream()
                .map(PlaceDTO::fromEntity)
                .toList();
    }

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
                        .themes(List.of(theme)) // 📌 자동 연결
                        .build();

                placeRepository.save(place);
                log.info("📌 저장된 장소: {}, 카테고리: {}, 테마: {}", dto.getName(), dto.getCategory(), theme.getName());

            }
        }
    }


    private Place convertToEntity(PlaceDTO dto) {
        return Place.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .category(PlaceCategoryMapper.mapFromGoogleType(dto.getCategory())) // 여기 변경
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();
    }

}

