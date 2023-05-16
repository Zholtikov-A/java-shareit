package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequest {
    Long id;
    String description;
    User requester;
    LocalDateTime created;
}
