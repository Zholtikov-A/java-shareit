package ru.practicum.shareit.user;

import java.util.List;


public interface UserService {

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto);

    List<UserDto> findAll();

    UserDto findUserById(Long id);

    void removeUser(Long id);

}
