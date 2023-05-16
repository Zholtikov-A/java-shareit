package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@Valid @RequestBody UserDto userDto, @PathVariable("id") @Positive Long id) {
        userDto.setId(id);
        return userService.update(userDto);
    }

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable("id") @Positive Long id) {
        return userService.findUserById(id);
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable Long id) {
        userService.removeUser(id);
    }
}
