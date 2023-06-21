package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    BookingDtoOutput create(BookingDtoInput bookingDto, Long bookerId);

    BookingDtoOutput setApprove(Long bookingId, boolean approved, Long userId);

    BookingDtoOutput findById(Long bookingId, Long userId);

    List<BookingDtoOutput> getUserBookings(Long userId, String state, Integer from, Integer size);

    List<BookingDtoOutput> findAllByOwner(Long userId, String state, Integer from, Integer size);

    // List<BookingDtoOutput> findAll(Long userId, Integer from, Integer size);

}