package com.sunsuntrip.backend.client;

import com.sunsuntrip.backend.dto.PlaceDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class GoogleMapsClient {

    private final String apiKey;
    private final RestTemplate restTemplate;

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json";
    private static final double NUCLEAR_LAT = 37.4210;
    private static final double NUCLEAR_LNG = 141.0328;
    private static final double MIN_SAFE_DISTANCE_KM = 40.0;
    private static final double MAX_FUKUSHIMA_DISTANCE_KM = 100.0; // ✅ 후쿠시마 권역 상한
    private static final int MAX_PAGES = 3;

    public GoogleMapsClient(@Value("${google.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.restTemplate = new RestTemplate();
    }

    public List<PlaceDTO> searchByKeyword(String keyword) {
        return fetchAllPages(keyword + " in Fukushima");
    }

    public List<PlaceDTO> searchByMultipleKeywords(List<String> keywords) {
        List<PlaceDTO> aggregated = new ArrayList<>();
        for (String kw : keywords) {
            aggregated.addAll(searchByKeyword(kw));
        }
        return aggregated;
    }

    @SuppressWarnings("unchecked")
    private List<PlaceDTO> fetchAllPages(String fullQuery) {
        List<PlaceDTO> places = new ArrayList<>();
        String pageToken = null;
        int pageCount = 0;

        String encodedQuery = URLEncoder.encode(fullQuery, StandardCharsets.UTF_8);

        do {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL);
            if (pageToken == null) {
                builder.queryParam("query", encodedQuery);
            } else {
                builder.queryParam("pagetoken", pageToken);
            }
            builder
                    .queryParam("language", "en")
                    .queryParam("key", apiKey);

            String url = builder.build(false).toUriString();
            System.out.println("🔗 호출 URL (" + (pageCount + 1) + "): " + url);

            ResponseEntity<Map> resp = restTemplate.getForEntity(url, Map.class);
            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                throw new RuntimeException("Google Places API 호출 실패: " + resp.getStatusCode());
            }

            Map<String, Object> body = resp.getBody();
            String status = (String) body.get("status");
            if (!"OK".equals(status)) {
                System.out.println("⚠️ 응답 상태: " + status + " → " + body.get("error_message"));
                break;
            }

            List<Map<String, Object>> results = (List<Map<String, Object>>) body.get("results");
            if (results == null || results.isEmpty()) break;

            for (Map<String, Object> item : results) {
                try {
                    String name = (String) item.get("name");
                    Map<String, Object> geometry = (Map<String, Object>) item.get("geometry");
                    Map<String, Object> location = (Map<String, Object>) geometry.get("location");
                    double lat = ((Number) location.get("lat")).doubleValue();
                    double lng = ((Number) location.get("lng")).doubleValue();

                    double dist = haversine(lat, lng, NUCLEAR_LAT, NUCLEAR_LNG);
                    if (dist < MIN_SAFE_DISTANCE_KM) {
                        System.out.printf("❌ 원전 반경 내 제외 (%.1fkm): %s\n", dist, name);
                        continue;
                    }
                    if (dist > MAX_FUKUSHIMA_DISTANCE_KM) {
                        System.out.printf("❌ 후쿠시마 외곽 제외 (%.1fkm): %s\n", dist, name);
                        continue;
                    }

                    String description = (String) item.get("formatted_address");
                    List<String> types = (List<String>) item.get("types");
                    String category = (types != null && !types.isEmpty()) ? types.get(0) : "unknown";

                    places.add(new PlaceDTO(null, name, description, category, lat, lng));
                } catch (Exception e) {
                    System.out.println("⚠️ 파싱 오류: " + e.getMessage());
                }
            }

            pageToken = (String) body.get("next_page_token");
            pageCount++;

            if (pageToken != null && pageCount < MAX_PAGES) {
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            } else {
                break;
            }
        } while (pageCount < MAX_PAGES);

        return places;
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
