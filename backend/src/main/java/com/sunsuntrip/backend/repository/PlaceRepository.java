package com.sunsuntrip.backend.repository;

import com.sunsuntrip.backend.domain.Place;
import com.sunsuntrip.backend.dto.PlaceDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findByCategory(Place.PlaceCategory category);
    List<Place> findAll();
}
