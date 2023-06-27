package ru.practicum.shareit.gateway.booking.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.gateway.item.dto.BookingSearchState;

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
