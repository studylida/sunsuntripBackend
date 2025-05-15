package com.sunsuntrip.backend.util;

import java.util.Map;

public class ThemeMinimumPlaceConfig {

    private static final Map<String, Integer> MIN_COUNT = Map.of(
            "온천", 25,
            "역사", 27,
            "쇼핑", 26,
            "휴식", 26,
            "자연", 28,
            "먹거리", 28,
            "축제", 26,
            "전통문화", 25,
            "예술", 25,
            "체험", 26
    );

    public static int getMinimumCountFor(String themeName) {
        return MIN_COUNT.getOrDefault(themeName, 5); // 기본값
    }
}

