package com.sunsuntrip.backend.util;

/**
 * 한글 테마명을 Google Places API에 적합한 영어 키워드로 매핑하는 유틸리티 클래스
 */
public class ThemeKeywordMapper {

    public static String toSearchKeyword(String themeName) {
        return switch (themeName) {
            case "온천" -> "spa";
            case "자연" -> "nature";
            case "전통문화" -> "cultural site";
            case "예술" -> "art";
            case "축제" -> "festival";
            default -> themeName;  // 예외적으로 들어오는 경우, 그대로 사용
        };
    }
}
