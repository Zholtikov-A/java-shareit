package ru.practicum.shareit.item;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDtoOutput {

    Long id;

    String text;

    String authorName;

    LocalDateTime created;

}
