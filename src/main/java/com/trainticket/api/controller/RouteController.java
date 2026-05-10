package com.trainticket.api.controller;

import com.trainticket.api.dto.SearchResponseDTO;
import com.trainticket.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/routes")
@RequiredArgsConstructor
public class RouteController {
    private final RouteService routeService;

    @GetMapping
    public ResponseEntity<List<SearchResponseDTO>> searchRoutes(@RequestParam Long from, @RequestParam Long to) {
        return ResponseEntity.ok(routeService.findPossibleRoutes(from, to));
    }
}
