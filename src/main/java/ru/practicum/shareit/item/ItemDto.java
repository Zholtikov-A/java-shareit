package ru.practicum.shareit.item;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Generated
@Data
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ItemDto {

    public interface Create {
    }

    Long id;
    @NotBlank(groups = {Create.class})
    String name;
    @NotBlank(groups = {Create.class})
    String description;

    @NotNull(groups = {Create.class})
    Boolean available;

    Long requestId;
}

