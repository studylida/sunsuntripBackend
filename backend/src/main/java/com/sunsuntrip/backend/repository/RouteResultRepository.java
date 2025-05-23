package com.sunsuntrip.backend.repository;

import com.sunsuntrip.backend.domain.RouteResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RouteResultRepository extends JpaRepository<RouteResult, Long> {

    @Query("SELECT r FROM RouteResult r " +
            "JOIN FETCH r.routePlaces rp " +
            "JOIN FETCH rp.place " +
            "WHERE r.id = :id")
    Optional<RouteResult> findByIdWithPlaces(@Param("id") Long id);
}
