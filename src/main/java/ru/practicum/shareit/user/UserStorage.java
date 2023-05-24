package ru.practicum.shareit.user;

import javax.validation.Valid;
import java.util.List;

public interface UserStorage {
    User create(@Valid User user);

    User update(@Valid User user);

    List<User> findAll();

    User findUserById(Long id);

    void removeUser(Long id);
}
