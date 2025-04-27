package com.sunsuntrip.backend.repository;

import com.sunsuntrip.backend.domain.Place;
import com.sunsuntrip.backend.dto.PlaceDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findByCategory(Place.PlaceCategory category);

    @Query("SELECT p FROM Place p LEFT JOIN FETCH p.themes")
    List<Place> findAllWithThemes();
}
