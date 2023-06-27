package ru.practicum.shareit.gateway.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
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
