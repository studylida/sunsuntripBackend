package com.sunsuntrip.backend.util;

import com.sunsuntrip.backend.domain.Place;

public class PlaceCategoryMapper {

//    public static Place.PlaceCategory mapFromGoogleType(String googleType) {
//        if (googleType == null) return Place.PlaceCategory.ATTRACTION; // 기본값
//
//        return switch (googleType.toLowerCase()) {
//            case "lodging", "hotel" -> Place.PlaceCategory.ACCOMMODATION;
//            case "restaurant", "food", "cafe", "bakery" -> Place.PlaceCategory.FOOD;
//            case "tourist_attraction", "museum", "park", "spa", "zoo" -> Place.PlaceCategory.ATTRACTION;
//            case "shopping_mall", "department_store", "clothing_store" -> Place.PlaceCategory.SHOPPING;
//            default -> Place.PlaceCategory.ATTRACTION; // fallback
//        };
//    }

    // 예시
    public static Place.PlaceCategory mapFromGoogleType(String type) {
        switch (type.toLowerCase()) {
            case "tourist_attraction", "museum", "natural_feature", "spa", "bathhouse":
                return Place.PlaceCategory.ATTRACTION;
            case "shopping_mall", "store", "clothing_store":
                return Place.PlaceCategory.SHOPPING;
            case "restaurant", "cafe", "bakery", "food":
                return Place.PlaceCategory.FOOD;
            case "lodging", "hotel", "guest_house":
                return Place.PlaceCategory.ACCOMMODATION;
            default:
                return Place.PlaceCategory.ATTRACTION; // 기본값 fallback
        }
    }


}
