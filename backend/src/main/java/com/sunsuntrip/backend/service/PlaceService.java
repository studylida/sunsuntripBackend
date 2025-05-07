package com.sunsuntrip.backend.service;

import com.sunsuntrip.backend.domain.Place;
import com.sunsuntrip.backend.dto.PlaceDTO;
import com.sunsuntrip.backend.repository.PlaceRepository;
import com.sunsuntrip.backend.util.PlaceCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public void saveIfNotExist(List<PlaceDTO> placeDTOs) {
        for (PlaceDTO dto : placeDTOs) {
            boolean exists = placeRepository.existsByNameAndLatitudeAndLongitude(
                    dto.getName(), dto.getLatitude(), dto.getLongitude());

            if (!exists) {
                Place place = convertToEntity(dto);
                placeRepository.save(place);
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

