package com.trainticket.repository;

import com.trainticket.model.Booking;
import com.trainticket.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByTrainId(Long trainId);

    List<Booking> findByTrainIdAndStatus(Long trainId, BookingStatus status);

}
