package com.sunsuntrip.backend.controller;

import com.sunsuntrip.backend.dto.PlaceDTO;
import com.sunsuntrip.backend.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping
    public List<PlaceDTO> getAllPlaces() {
        return placeService.getAllPlaces();
    }
}
