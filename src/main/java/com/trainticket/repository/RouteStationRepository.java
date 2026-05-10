package com.trainticket.repository;

import com.trainticket.model.RouteStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteStationRepository extends JpaRepository<RouteStation, Long> {
    Optional<RouteStation> findByRouteIdAndStationId(Long routeId, Long stationId);

    List<RouteStation> findAllByRouteIdOrderByStopOrderAsc(Long routeId);
}
