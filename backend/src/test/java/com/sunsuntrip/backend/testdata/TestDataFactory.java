//package com.sunsuntrip.backend.testdata;
//
//import com.sunsuntrip.backend.domain.Place;
//import com.sunsuntrip.backend.domain.Place.PlaceCategory;
//import com.sunsuntrip.backend.domain.Theme;
//import com.sunsuntrip.backend.domain.UserCondition;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public class TestDataFactory {
//
//    private static final List<String> THEME_NAMES = Arrays.asList(
//            "자연", "역사", "온천", "휴식", "엔터테인먼트",
//            "쇼핑", "먹거리", "체험", "전통문화", "예술",
//            "축제", "레저/액티비티"
//    );
//
//    public static List<Place> createTestPlaces(List<Theme> themes) {
//        List<Place> places = new ArrayList<>();
//
//        for (int i = 1; i <= 50; i++) {
//            Place place = new Place();
//            place.setName("Place " + i);
//            place.setDescription("Description " + i);
//
//            if (i % 4 == 0) {
//                place.setCategory(PlaceCategory.ACCOMMODATION);
//            } else if (i % 4 == 1) {
//                place.setCategory(PlaceCategory.ATTRACTION);
//            } else if (i % 4 == 2) {
//                place.setCategory(PlaceCategory.FOOD);
//            } else {
//                place.setCategory(PlaceCategory.SHOPPING);
//            }
//
//            place.setLatitude(37.55 + Math.random() * 0.1);
//            place.setLongitude(126.98 + Math.random() * 0.1);
//
//            // 테마 랜덤 1~2개 할당
//            List<Theme> assignedThemes = new ArrayList<>();
//            assignedThemes.add(themes.get(i % themes.size()));
//            if (i % 3 == 0) assignedThemes.add(themes.get((i * 2) % themes.size()));
//
//            place.setThemes(assignedThemes);
//
//            places.add(place);
//        }
//
//        return places;
//    }
//
//    public static UserCondition createTestUserCondition(List<Theme> themes) {
//        UserCondition userCondition = new UserCondition();
//        userCondition.setDays(5);
//        userCondition.setBudget(500000);
//        userCondition.setNumberOfPeople(2);
//        userCondition.setPreferOnsen(false);
//        userCondition.setAvoidCrowd(true);
//        userCondition.setMobilityLimitations(false);
//        userCondition.setUsePublicTransportOnly(true);
//        userCondition.setStartDate(java.time.LocalDate.now());
//
//        // 선호 테마 2개 고르기
//        userCondition.setThemes(List.of(
//                themes.get(0),  // "자연"
//                themes.get(5)   // "쇼핑"
//        ));
//
//        return userCondition;
//    }
//
//}
