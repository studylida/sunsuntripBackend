package com.sunsuntrip.backend.util;

import com.sunsuntrip.backend.domain.Place;

public class PlaceCategoryMapper {

    public static Place.PlaceCategory mapFromGoogleType(String googleType) {
        if (googleType == null) return Place.PlaceCategory.ATTRACTION; // 기본값

        return switch (googleType.toLowerCase()) {
            case "lodging", "hotel" -> Place.PlaceCategory.ACCOMMODATION;
            case "restaurant", "food", "cafe", "bakery" -> Place.PlaceCategory.FOOD;
            case "tourist_attraction", "museum", "park", "spa", "zoo" -> Place.PlaceCategory.ATTRACTION;
            case "shopping_mall", "department_store", "clothing_store" -> Place.PlaceCategory.SHOPPING;
            default -> Place.PlaceCategory.ATTRACTION; // fallback
        };
    }
}
