package com.sunsuntrip.backend.client;

import com.sunsuntrip.backend.dto.PlaceDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class GoogleMapsClient {

    @Value("${google.api.key}")
    private String apiKey;

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json";
    private final RestTemplate restTemplate = new RestTemplate();

    public List<PlaceDTO> searchByKeyword(String keyword) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("query", keyword + " near Fukushima")
                .queryParam("key", apiKey)
                .queryParam("language", "ko")
                .toUriString();

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Google Places API 호출 실패: " + response.getStatusCode());
        }

        List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("results");
        if (results == null) return Collections.emptyList();

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
                // 파싱 오류 무시
                continue;
            }
        }

        return places;
    }
}
