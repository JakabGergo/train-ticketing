package com.trainticket.service;

import com.trainticket.api.dto.StationCreateDTO;
import com.trainticket.model.Station;
import com.trainticket.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StationService {
    private final StationRepository stationRepository;

    public Station createStation(StationCreateDTO dto) {
        Station station = Station.builder()
                .name(dto.getName())
                .city(dto.getCity())
                .build();
        return stationRepository.save(station);
    }

    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    public Station updateStation(Long id, StationCreateDTO dto) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Station not found"));
        station.setName(dto.getName());
        station.setCity(dto.getCity());
        return stationRepository.save(station);
    }

    public void deleteStation(Long id) {
        stationRepository.deleteById(id);
    }
}