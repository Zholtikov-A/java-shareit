package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {

    public interface Create {
    }

    public interface Update {
    }

    Long id;
    @NotBlank(groups = {Create.class})
    String name;
    @Email(groups = {Create.class, Update.class})
    @NotBlank(groups = {Create.class})
    String email;
}
