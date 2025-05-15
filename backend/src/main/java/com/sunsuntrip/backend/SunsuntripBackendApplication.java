package com.sunsuntrip.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SunsuntripBackendApplication {

	public static void main(String[] args) {
		// ✅ .env 로드 후 System Property에 등록
		Dotenv dotenv = Dotenv.configure()
				.filename(".env")
				.ignoreIfMissing()
				.load();

		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);

		SpringApplication.run(SunsuntripBackendApplication.class, args);

		// 🔵 2. GoogleMapsClientTestMain 실행
		GoogleMapsClientTestMain.main(new String[0]);
	}
}
