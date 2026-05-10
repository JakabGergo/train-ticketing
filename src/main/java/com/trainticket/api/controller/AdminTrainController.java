package com.trainticket.api.controller;

import com.trainticket.api.dto.BookingResponseDTO;
import com.trainticket.api.dto.DelayDTO;
import com.trainticket.api.dto.TrainCreateDTO;
import com.trainticket.model.Train;
import com.trainticket.service.TrainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin/trains")
@RequiredArgsConstructor
public class AdminTrainController {
    private final TrainService trainService;

    @PostMapping
    public ResponseEntity<Train> createTrain(@RequestBody TrainCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trainService.createTrain(dto));
    }

    @GetMapping
    public ResponseEntity<List<Train>> getAllTrains() {
        return ResponseEntity.ok(trainService.getAllTrains());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Train> updateTrain(@PathVariable Long id,
                                             @RequestBody TrainCreateDTO dto) {
        return ResponseEntity.ok(trainService.updateTrain(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrain(@PathVariable Long id) {
        trainService.deleteTrain(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/delay")
    public ResponseEntity<Train> setDelay(@PathVariable Long id,
                                          @RequestBody DelayDTO delayDTO) {
        return ResponseEntity.ok(trainService.setDelay(id, delayDTO));
    }

    @GetMapping("/{id}/bookings")
    public ResponseEntity<List<BookingResponseDTO>> getTrainBookings(@PathVariable Long id) {
        return ResponseEntity.ok(trainService.getTrainBookings(id));
    }
}