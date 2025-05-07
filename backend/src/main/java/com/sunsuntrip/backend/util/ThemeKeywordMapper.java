package com.sunsuntrip.backend.util;

import java.util.Map;

public class ThemeKeywordMapper {

    private static final Map<String, String> themeToKeyword = Map.ofEntries(
            Map.entry("자연", "자연경관"),
            Map.entry("역사", "유적지"),
            Map.entry("온천", "온천"),
            Map.entry("휴식", "휴양"),
            Map.entry("엔터테인먼트", "놀이공원"),
            Map.entry("쇼핑", "쇼핑몰"),
            Map.entry("먹거리", "맛집"),
            Map.entry("체험", "체험활동"),
            Map.entry("전통문화", "전통 마을"),
            Map.entry("예술", "미술관"),
            Map.entry("축제", "지역 축제"),
            Map.entry("레저/액티비티", "레저 활동")
    );

    public static String toSearchKeyword(String themeName) {
        return themeToKeyword.getOrDefault(themeName, themeName); // 기본은 동일 이름
    }
}
