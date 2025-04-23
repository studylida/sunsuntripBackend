package com.sunsuntrip.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:/application.properties") // 기본 설정
@PropertySource(value = "file:.env", ignoreResourceNotFound = true) // .env 파일 로드
public class SunsuntripBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SunsuntripBackendApplication.class, args);
	}

}
