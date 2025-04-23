package com.sunsuntrip.backend.service;

import com.sunsuntrip.backend.domain.Place;
import com.sunsuntrip.backend.dto.PlaceDTO;
import com.sunsuntrip.backend.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}

