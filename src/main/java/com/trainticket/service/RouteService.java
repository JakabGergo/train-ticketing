package com.trainticket.service;

import com.trainticket.api.dto.JourneyLegDTO;
import com.trainticket.api.dto.SearchResponseDTO;
import com.trainticket.model.RouteStation;
import com.trainticket.model.Train;
import com.trainticket.repository.RouteStationRepository;
import com.trainticket.repository.TrainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final TrainRepository trainRepository;
    private final RouteStationRepository routeStationRepository;

    public List<SearchResponseDTO> findPossibleRoutes(Long fromId, Long toId) {
        List<SearchResponseDTO> results = new ArrayList<>();

        List<Train> trains = trainRepository.findAll();

        results.addAll(findDirectRoutes(fromId, toId, trains));

        results.addAll(findChangeoverRoutes(fromId, toId, trains));

        if (results.isEmpty()) {
            throw new NoSuchElementException("No possible link between the selected stations.");
        }
        return results;
    }

    private List<SearchResponseDTO> findDirectRoutes(Long fromId, Long toId, List<Train> trains) {
        List<SearchResponseDTO> results = new ArrayList<>();

        for (Train train : trains) {
            var start = routeStationRepository.findByRouteIdAndStationId(train.getRoute().getId(), fromId);
            var end = routeStationRepository.findByRouteIdAndStationId(train.getRoute().getId(), toId);

            if (start.isPresent() && end.isPresent() && start.get().getStopOrder() < end.get().getStopOrder()) {
                JourneyLegDTO leg = createLeg(train, start.get(), end.get());
                results.add(SearchResponseDTO.builder()
                        .legs(List.of(leg))
                        .isDirect(true)
                        .totalDurationMinutes(java.time.Duration.between(leg.getDepartureTime(), leg.getArrivalTime()).toMinutes())
                        .build());
            }
        }
        return results;
    }

    private List<SearchResponseDTO> findChangeoverRoutes(Long fromId, Long toId, List<Train> trains) {
        List<SearchResponseDTO> results = new ArrayList<>();

        for (Train t1 : trains) {
            var startLeg1 = routeStationRepository.findByRouteIdAndStationId(t1.getRoute().getId(), fromId);
            if (startLeg1.isEmpty()) continue;

            // Look for any station on T1's route that could be a transfer point
            List<RouteStation> t1Stops = routeStationRepository.findAllByRouteIdOrderByStopOrderAsc(t1.getRoute().getId());

            for (RouteStation transfer : t1Stops) {
                if (transfer.getStopOrder() <= startLeg1.get().getStopOrder()) continue;

                LocalDateTime arrivalAtTransfer = LocalDateTime.from(t1.getDepartureTime().plusMinutes(transfer.getMinutesFromOrigin()));

                // Find a second train (T2) that goes from 'transfer' to 'toId'
                for (Train t2 : trains) {
                    if (t1.getId().equals(t2.getId())) continue;

                    var startLeg2 = routeStationRepository.findByRouteIdAndStationId(t2.getRoute().getId(), transfer.getStation().getId());
                    var endLeg2 = routeStationRepository.findByRouteIdAndStationId(t2.getRoute().getId(), toId);

                    if (startLeg2.isPresent() && endLeg2.isPresent() && startLeg2.get().getStopOrder() < endLeg2.get().getStopOrder()) {
                        LocalDateTime departureFromTransfer = LocalDateTime.from(t2.getDepartureTime().plusMinutes(startLeg2.get().getMinutesFromOrigin()));

                        // Ensure there is at least a 10-minute gap for the changeover
                        if (departureFromTransfer.isAfter(arrivalAtTransfer.plusMinutes(10))) {
                            results.add(SearchResponseDTO.builder()
                                    .legs(List.of(createLeg(t1, startLeg1.get(), transfer), createLeg(t2, startLeg2.get(), endLeg2.get())))
                                    .isDirect(false)
                                    .totalDurationMinutes(java.time.Duration.between(
                                            t1.getDepartureTime().plusMinutes(startLeg1.get().getMinutesFromOrigin()),
                                            t2.getDepartureTime().plusMinutes(endLeg2.get().getMinutesFromOrigin())).toMinutes())
                                    .build());
                        }
                    }
                }
            }
        }
        return results;
    }

    private JourneyLegDTO createLeg(Train t, RouteStation s, RouteStation e) {
        return JourneyLegDTO.builder()
                .trainId(t.getId())
                .fromStationName(s.getStation().getName())
                .toStationName(e.getStation().getName())
                .departureTime(LocalDateTime.from(t.getDepartureTime().plusMinutes(s.getMinutesFromOrigin())))
                .arrivalTime(LocalDateTime.from(t.getDepartureTime().plusMinutes(e.getMinutesFromOrigin())))
                .build();
    }
}
