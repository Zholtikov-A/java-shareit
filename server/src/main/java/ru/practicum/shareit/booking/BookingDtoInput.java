package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDtoInput {

    Long bookerId;
    Long itemId;
    LocalDateTime start;
    LocalDateTime end;
    BookingSearchState state;

}
