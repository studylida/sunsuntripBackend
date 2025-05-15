package com.sunsuntrip.backend.client;

import com.sunsuntrip.backend.dto.PlaceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.*;

@Component
public class GoogleMapsClient {

    private final String apiKey;
    private final RestTemplate restTemplate;

    /**
     * ✅ Spring 주입용 생성자
     */
    @Autowired
    public GoogleMapsClient(@Value("${google.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.restTemplate = new RestTemplate();
    }

    /**
     * ✅ main 테스트용 수동 생성자
     */
    public GoogleMapsClient(RestTemplate restTemplate, String apiKey) {
        this.apiKey = apiKey;
        this.restTemplate = restTemplate;
    }

    /**
     * 🔍 키워드 기반 장소 검색
     */
    public List<PlaceDTO> searchByKeyword(String keyword) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("query", keyword)
                .queryParam("location", "37.7500,140.4678")  // 후쿠시마 중심
                .queryParam("radius", 30000)                // 반경 30km
                .queryParam("key", apiKey)
                .queryParam("language", "ko")
                .toUriString();

        System.out.println("🔗 호출 URL: " + url);  // 디버깅용

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Google Places API 호출 실패: " + response.getStatusCode());
        }

        Map<String, Object> body = response.getBody();
        if (body == null) {
            System.out.println("⚠️ 응답 본문이 null입니다.");
            return Collections.emptyList();
        }

        // 응답 상태 출력
        System.out.println("🔁 응답 상태: " + body.get("status"));
        if (body.containsKey("error_message")) {
            System.out.println("❗ 에러 메시지: " + body.get("error_message"));
        }

        List<Map<String, Object>> results = (List<Map<String, Object>>) body.get("results");
        if (results == null || results.isEmpty()) {
            System.out.println("⚠️ 장소 결과가 비어 있습니다.");
            return Collections.emptyList();
        }

        List<PlaceDTO> places = new ArrayList<>();

        for (Map<String, Object> item : results) {
            try {
                String name = (String) item.get("name");
                String description = (String) item.get("formatted_address");

                List<String> types = (List<String>) item.get("types");
                String category = types != null && !types.isEmpty() ? types.get(0) : "unknown";

                Map<String, Object> geometry = (Map<String, Object>) item.get("geometry");
                Map<String, Object> location = (Map<String, Object>) geometry.get("location");

                double lat = ((Number) location.get("lat")).doubleValue();
                double lng = ((Number) location.get("lng")).doubleValue();

                PlaceDTO dto = new PlaceDTO(null, name, description, category, lat, lng);
                places.add(dto);
            } catch (Exception e) {
                System.out.println("⚠️ 파싱 오류 발생: " + e.getMessage());
                continue;
            }
        }

        return places;
    }

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json";
}
