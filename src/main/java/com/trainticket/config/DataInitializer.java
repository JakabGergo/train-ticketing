package com.trainticket.config;

import com.trainticket.model.*;
import com.trainticket.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            StationRepository stationRepo,
            RouteRepository routeRepo,
            RouteStationRepository routeStationRepo,
            TrainRepository trainRepo,
            UserRepository userRepo) {

        return args -> {
            // 1. Create Stations
            /*
            Station bucharest = stationRepo.save(new Station("North Station", "Bucharest"));
            Station brasov = stationRepo.save(new Station("Central Station", "Brasov"));
            Station cluj = stationRepo.save(new Station("Cluj Station", "Cluj-Napoca"));

            // 2. Create a Route
            Route route = new Route();
            route.setName("Bucharest - Cluj Corridor");
            route = routeRepo.save(route);

            // 3. Link Stations to Route (RouteStation)
            // Bucharest (Origin)
            routeStationRepo.save(new RouteStation(route, bucharest, 1, 0));
            // Brasov (Middle - 180 mins from start)
            routeStationRepo.save(new RouteStation(route, brasov, 2, 180));
            // Cluj (End - 450 mins from start)
            routeStationRepo.save(new RouteStation(route, cluj, 3, 450));

            // 4. Create a Train for this route
            Route route = routeRepo.findById(1L);

            Train interCity = new Train();
            interCity.setRoute(route);
            interCity.setDepartureTime(LocalTime.from(LocalDateTime.now().plusHours(2)));
            interCity.setTotalSeats(100);
            interCity.setTrainNumber("T1");
            trainRepo.save(interCity);

            // 5. Create a Test User
            User user = new User();
            user.setName("John Doe");
            user.setEmail("john.doe@example.com");
            user.setPassword("password123");
            user.setRole(Role.USER);
            userRepo.save(user);

            System.out.println("--- Test data initialized successfully ---");

             */
        };
    }
}