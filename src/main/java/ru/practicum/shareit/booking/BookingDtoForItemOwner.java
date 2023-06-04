package ru.practicum.shareit.booking;

import lombok.*;
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
