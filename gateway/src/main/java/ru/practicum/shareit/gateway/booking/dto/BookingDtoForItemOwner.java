package ru.practicum.shareit.gateway.booking.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDtoForItemOwner {

    Long id;

    LocalDateTime start;

    LocalDateTime end;

    Long bookerId;
}
