// 📁 RouteResultRepository.java
package com.sunsuntrip.backend.repository;

import com.sunsuntrip.backend.domain.RouteResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteResultRepository extends JpaRepository<RouteResult, Long> {
}