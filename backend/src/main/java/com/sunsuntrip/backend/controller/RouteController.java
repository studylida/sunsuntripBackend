// 📁 RouteController.java
package com.sunsuntrip.backend.controller;

import com.sunsuntrip.backend.domain.RouteResult;
import com.sunsuntrip.backend.service.RouteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping
    public RouteResult createRoute(@RequestBody RouteResult routeResult) {
        return routeService.saveRoute(routeResult);
    }

    @GetMapping
    public List<RouteResult> getAllRoutes() {
        return routeService.getAllRoutes();
    }

    @GetMapping("/{id}")
    public RouteResult getRoute(@PathVariable Long id) {
        return routeService.getRouteById(id);
    }
}