//package com.sunsuntrip.backend.service;
//
//import com.sunsuntrip.backend.domain.Place;
//import com.sunsuntrip.backend.domain.RouteResult;
//import com.sunsuntrip.backend.domain.Theme;
//import com.sunsuntrip.backend.domain.UserCondition;
//import com.sunsuntrip.backend.repository.ThemeRepository;
//import com.sunsuntrip.backend.testdata.TestDataFactory;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//public class RouteAlgorithmServiceTest {
//
//    @Autowired
//    private ThemeRepository themeRepository;  // 🔵 주입받기
//
//    private final RouteAlgorithmService routeAlgorithmService = new RouteAlgorithmService();
//
//    @Test
//    void testGenerateRouteWith50Places() {
//        // 🔵 Arrange (준비)
//        List<Theme> allThemes = themeRepository.findAll();  // 🔵 주입받은 인스턴스로 호출
//        System.out.println("조회된 Theme 개수: " + allThemes.size());
//
//        List<Place> places = TestDataFactory.createTestPlaces(allThemes);
//        UserCondition userCondition = TestDataFactory.createTestUserCondition(allThemes);
//
//        // 🔵 Act (실행)
//        RouteResult routeResult = routeAlgorithmService.generateRoute(userCondition, places);
//
//        // 🔵 Assert (검증)
//        assertNotNull(routeResult, "RouteResult는 null이 아니어야 합니다.");
//        assertNotNull(routeResult.getRoutePlaces(), "RoutePlaces는 null이 아니어야 합니다.");
//        assertEquals(userCondition, routeResult.getUserCondition(), "UserCondition이 일치해야 합니다.");
//        assertTrue(routeResult.getRoutePlaces().size() > 0, "최소한 하나 이상의 RoutePlace가 생성되어야 합니다.");
//
//        // 추가 검증: 총 거리와 소요 시간도 양수여야 함
//        assertTrue(routeResult.getTotalDistance() > 0, "총 이동거리는 0보다 커야 합니다.");
//        assertTrue(routeResult.getTotalDuration() > 0, "총 소요시간은 0보다 커야 합니다.");
//
//        // 디버깅 출력 (optional)
//        System.out.println("총 장소 수: " + routeResult.getRoutePlaces().size());
//        System.out.println("총 이동 거리(km): " + routeResult.getTotalDistance());
//        System.out.println("총 소요 시간(분): " + routeResult.getTotalDuration());
//    }
//}
