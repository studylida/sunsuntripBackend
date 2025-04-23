package com.sunsuntrip.backend.dto;

import com.sunsuntrip.backend.domain.Place;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// PlaceDTO
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlaceDTO {
    private Long id;
    private String name;
    private String description;
    private String category;
    private double latitude;
    private double longitude;

    public static PlaceDTO fromEntity(Place place) {
        return new PlaceDTO(
                place.getId(),
                place.getName(),
                place.getDescription(),
                place.getCategory().name(),
                place.getLatitude(),
                place.getLongitude()
        );
    }
}