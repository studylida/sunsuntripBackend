package com.sunsuntrip.backend.integration;

import com.sunsuntrip.backend.domain.Theme;
import com.sunsuntrip.backend.repository.ThemeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class RouteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ThemeRepository themeRepository;

    @Test
    void generateRoute_withThemeAndFallbackToGoogle_shouldReturnValidResult() throws Exception {
        // 테스트용 Theme 하나 선택 (예: 온천)
        List<Theme> themes = themeRepository.findAll();
        Theme testTheme = themes.stream()
                .filter(t -> t.getName().equals("온천"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("온천 테마가 DB에 존재하지 않습니다."));

        // 요청 JSON 구성
        String requestJson = """
        {
            "days": 2,
            "budget": 300000,
            "numberOfPeople": 2,
            "startDate": "%s",
            "themeIds": [%d]
        }
        """.formatted(LocalDate.now().plusDays(7), testTheme.getId());

        // /api/route 호출 및 응답 검증
        mockMvc.perform(post("/api/route")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dailyPlans").isArray())
                .andExpect(jsonPath("$.totalDistance").isNumber())
                .andExpect(jsonPath("$.totalDuration").isNumber());
    }
}
