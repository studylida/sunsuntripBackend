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
            case "spa", "museum", "art_gallery", "zoo", "aquarium",
                 "tourist_attraction", "natural_feature", "amusement_park",
                 "park", "movie_theater", "night_club", "stadium",
                 "campground", "synagogue", "church", "hindu_temple":
                return Place.PlaceCategory.ATTRACTION;
            case "shopping_mall", "clothing_store", "store", "department_store",
                 "shoe_store", "jewelry_store", "convenience_store", "supermarket":
                return Place.PlaceCategory.SHOPPING;
            case "restaurant", "cafe", "bakery", "meal_takeaway", "meal_delivery", "food":
                return Place.PlaceCategory.FOOD;
            case "lodging", "hotel", "motel", "guest_house":
                return Place.PlaceCategory.ACCOMMODATION;
            default:
                return Place.PlaceCategory.ATTRACTION; // 기본값 fallback
        }
    }


}
