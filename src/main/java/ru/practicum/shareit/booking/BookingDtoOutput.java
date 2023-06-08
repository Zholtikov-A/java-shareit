package ru.practicum.shareit.booking;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;

import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDtoOutput {

    Long id;
    @Future(message = "Booking start time can't be in the past")
    @NonNull
    LocalDateTime start;
    @Future(message = "Booking end time can't be in the past")
    @NonNull
    LocalDateTime end;

    ItemDto item;

    UserDto booker;

    BookingStatus status;
}
