package ru.practicum.shareit.booking;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDtoInput {

    Long bookerId;
    Long itemId;
    @Future(message = "Booking start time can't be in the past")
    @NonNull
    LocalDateTime start;
    @Future(message = "Booking end time can't be in the past")
    @NonNull
    LocalDateTime end;
    BookingSearchState state;


}
