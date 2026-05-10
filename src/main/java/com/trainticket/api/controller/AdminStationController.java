package com.trainticket.api.controller;

import com.trainticket.api.dto.StationCreateDTO;
import com.trainticket.model.Station;
import com.trainticket.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin/stations")
@RequiredArgsConstructor
public class AdminStationController {
    private final StationService stationService;

    @PostMapping
    public ResponseEntity<Station> createStation(@RequestBody StationCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(stationService.createStation(dto));
    }

    @GetMapping
    public ResponseEntity<List<Station>> getAllStations() {
        return ResponseEntity.ok(stationService.getAllStations());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Station> updateStation(@PathVariable Long id,
                                                 @RequestBody StationCreateDTO dto) {
        return ResponseEntity.ok(stationService.updateStation(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }
}