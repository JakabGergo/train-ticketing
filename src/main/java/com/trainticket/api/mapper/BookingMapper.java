package com.trainticket.api.mapper;

import com.trainticket.api.dto.BookingResponseDTO;
import com.trainticket.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "fromStationName", source = "fromStation.name")
    @Mapping(target = "toStationName", source = "toStation.name")
    // If you want a custom string for the train name:
    @Mapping(target = "trainName", expression = "java(\"Train #\" + booking.getTrain().getId())")
    BookingResponseDTO modelToResponseDto(Booking booking);

    Collection<BookingResponseDTO> modelsToResponseDtos(Iterable<Booking> bookings);
}