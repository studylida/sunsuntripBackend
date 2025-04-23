// 📁 RoutePlaceRepository.java
package com.sunsuntrip.backend.repository;

import com.sunsuntrip.backend.domain.RoutePlace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutePlaceRepository extends JpaRepository<RoutePlace, Long> {
}