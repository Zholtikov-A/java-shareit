package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    BookingDtoOutput create(BookingDtoInput bookingDto, Long bookerId);

    BookingDtoOutput setApprove(Long bookingId, boolean approved, Long userId);

    BookingDtoOutput getBookingById(Long bookingId, Long userId);

    List<BookingDtoOutput> getUserBookings(Long userId, String state);

    List<BookingDtoOutput> getAllUserItemsBookings(long userId, String state);
}