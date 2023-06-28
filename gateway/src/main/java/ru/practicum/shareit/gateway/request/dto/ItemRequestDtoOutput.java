package ru.practicum.shareit.gateway.request.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.gateway.item.dto.ItemResponseDto;
import ru.practicum.shareit.gateway.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDtoOutput {
    Long id;
    String description;
    UserDto requester;
    LocalDateTime created;
    List<ItemResponseDto> items;
}
