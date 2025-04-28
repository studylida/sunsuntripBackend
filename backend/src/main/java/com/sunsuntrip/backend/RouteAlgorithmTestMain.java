package com.sunsuntrip.backend;

import com.sunsuntrip.backend.domain.Place;
import com.sunsuntrip.backend.domain.RouteResult;
import com.sunsuntrip.backend.domain.Theme;
import com.sunsuntrip.backend.domain.UserCondition;
import com.sunsuntrip.backend.repository.PlaceRepository;
import com.sunsuntrip.backend.repository.ThemeRepository;
import com.sunsuntrip.backend.service.RouteAlgorithmService;
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
            RouteAlgorithmService service = new RouteAlgorithmService();

            // 🔵 Theme와 Place를 DB에서 조회
            List<Theme> allThemes = themeRepository.findAll();
            List<Place> allPlaces = placeRepository.findAllWithThemes();

            // 🔵 UserCondition 직접 생성
            UserCondition userCondition = new UserCondition();
            userCondition.setDays(3);
            userCondition.setBudget(100000); // 예산
            userCondition.setStartDate(LocalDate.now());
            userCondition.setAvoidCrowd(false);
            userCondition.setMobilityLimitations(false);
            userCondition.setPreferOnsen(false);
            userCondition.setUsePublicTransportOnly(false);
            userCondition.setNumberOfPeople(2);
            userCondition.setThemes(allThemes); // 모든 테마 선호한다고 가정

            // 🔵 루트 생성
            RouteResult result = service.generateRoute(userCondition, allPlaces);

            // 🔵 결과 출력
            System.out.println("총 장소 수: " + result.getRoutePlaces().size());
            System.out.println("총 이동 거리(km): " + result.getTotalDistance());
            System.out.println("총 소요 시간(분): " + result.getTotalDuration());

            // 🔵 루트 상세 출력
            System.out.println("--- 생성된 루트 ---");
            int order = 1;
            for (var routePlace : result.getRoutePlaces()) {
                Place place = routePlace.getPlace();

                // 테마 이름들을 콤마로 이어붙이기
                String themeNames = place.getThemes().stream()
                        .map(Theme::getName)
                        .reduce((t1, t2) -> t1 + ", " + t2)
                        .orElse("테마 없음");

                System.out.println(order++ + ". " + place.getName() +
                        " (테마: " + themeNames +
                        ", 체류 시간: " + routePlace.getStayMinutes() +
                        "분, 방문 일자: " + routePlace.getVisitDay() + "일차)");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
