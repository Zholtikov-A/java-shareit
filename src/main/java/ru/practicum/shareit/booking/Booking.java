package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {

    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Item item;
    User booker;

}
