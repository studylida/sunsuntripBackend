// 📁 RouteService.java
package com.sunsuntrip.backend.service;

import com.sunsuntrip.backend.domain.*;
import com.sunsuntrip.backend.repository.RouteResultRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RouteService {

    private final RouteResultRepository routeResultRepository;

    public RouteService(RouteResultRepository routeResultRepository) {
        this.routeResultRepository = routeResultRepository;
    }

    @Transactional
    public RouteResult saveRoute(RouteResult routeResult) {
        return routeResultRepository.save(routeResult);
    }

    public List<RouteResult> getAllRoutes() {
        return routeResultRepository.findAll();
    }

    public RouteResult getRouteById(Long id) {
        return routeResultRepository.findById(id).orElse(null);
    }
}