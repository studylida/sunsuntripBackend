package com.sunsuntrip.backend;

import com.sunsuntrip.backend.client.GoogleMapsClient;
import com.sunsuntrip.backend.dto.PlaceDTO;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class GoogleMapsClientTestMain {

    public static void main(String[] args) {
        // API 키를 명시적으로 설정 (테스트용으로)
        String apiKey = "stop-tracking";  // 안전하게 사용

        GoogleMapsClient client = new GoogleMapsClient(new RestTemplate(), apiKey);


        // 검색 키워드 테스트 (예: 온천)
        System.out.println("GoogleMapsClientTestMain");
        String keyword = "spa";
        List<PlaceDTO> results = client.searchByKeyword(keyword);

        for (PlaceDTO dto : results) {
            System.out.println("📍 이름: " + dto.getName());
            System.out.println("🗺️  위도: " + dto.getLatitude());
            System.out.println("🗺️  경도: " + dto.getLongitude());
            System.out.println("📄 설명: " + dto.getDescription());
            System.out.println("🏷️  타입: " + dto.getCategory());
            System.out.println("--------------------------------------------------");
        }
    }
}
