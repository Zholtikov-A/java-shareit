package ru.practicum.shareit.gateway.request.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDtoInput {
    @NotBlank
    String description;
    Long requesterId;
    LocalDateTime created;
}
