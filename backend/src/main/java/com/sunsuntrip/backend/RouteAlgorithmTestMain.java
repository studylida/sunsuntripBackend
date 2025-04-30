package com.sunsuntrip.backend;

import com.sunsuntrip.backend.domain.*;
import com.sunsuntrip.backend.repository.PlaceRepository;
import com.sunsuntrip.backend.repository.ThemeRepository;
import com.sunsuntrip.backend.service.RouteAlgorithmService2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class RouteAlgorithmTestMain implements CommandLineRunner {

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private PlaceRepository placeRepository;

    public static void main(String[] args) {
        SpringApplication.run(RouteAlgorithmTestMain.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            RouteAlgorithmService2 service = new RouteAlgorithmService2();

            // 🔵 1. DB에서 Theme 및 Place 조회
            List<Theme> allThemes = themeRepository.findAll();
            List<Place> allPlaces = placeRepository.findAllWithThemes();

            // 🔵 2. 사용자 조건 생성
            UserCondition userCondition = new UserCondition();
            userCondition.setDays(2);
            userCondition.setBudget(100000);
            userCondition.setStartDate(LocalDate.now());
            userCondition.setNumberOfPeople(2);
            userCondition.setThemes(allThemes); // 모든 테마를 선호한다고 가정

            // 🔵 3. 경로 생성
            RouteResult result = service.generateRoute(userCondition, allPlaces);

            // 🔵 4. 결과 출력
            printRouteSummary(result);
            printDailyPlans(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printRouteSummary(RouteResult result) {
        System.out.println("✅ [요약 정보]");
        System.out.println("총 장소 수: " + result.getRoutePlaces().size());
        System.out.printf("총 이동 거리: %.2f km\n", result.getTotalDistance());
        System.out.println("총 소요 시간: " + result.getTotalDuration() + "분");
    }

    private void printDailyPlans(RouteResult result) {
        System.out.println("\n📅 [일자별 일정]");
        int currentDay = -1;
        for (RoutePlace rp : result.getRoutePlaces()) {
            if (rp.getVisitDay() != currentDay) {
                currentDay = rp.getVisitDay();
                System.out.printf("Day %d\n", currentDay);
            }
            System.out.printf("  - %-15s (%-12s) [%d분]\n",
                    rp.getPlace().getName(),
                    rp.getPlace().getCategory(),
                    rp.getStayMinutes()
            );
        }
    }
}
