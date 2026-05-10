package com.trainticket.api.controller;

import com.trainticket.api.dto.RouteCreateDTO;
import com.trainticket.model.Route;
import com.trainticket.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin/routes")
@RequiredArgsConstructor
public class AdminRouteController {
    private final RouteService routeService;

    @PostMapping
    public ResponseEntity<Route> createRoute(@RequestBody RouteCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(routeService.createRoute(dto));
    }

    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Route> updateRoute(@PathVariable Long id,
                                             @RequestBody RouteCreateDTO dto) {
        return ResponseEntity.ok(routeService.updateRoute(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }
}