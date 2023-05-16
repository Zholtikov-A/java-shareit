package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("userStorageInMemory")
@Slf4j
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserStorageInMemory implements UserStorage {

    final Map<Long, User> users = new HashMap<>();
    Long lastGeneratedId = 0L;

    @Override
    public User create(@Valid User user) {
        checkEmail(user.getEmail());
        user.setId(generateId());
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User update(@Valid User user) {
        checkId(user.getId());
        if (!user.getEmail().equals(findUserById(user.getId()).getEmail())) {
            checkEmail(user.getEmail());
        }
        users.replace(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findUserById(Long id) {
        checkId(id);
        User userInDB = users.get(id);
        return new User(id, userInDB.getName(), userInDB.getEmail());
    }

    @Override
    public void removeUser(Long id) {
        checkId(id);
        users.remove(id);
    }

    private Long generateId() {
        return ++lastGeneratedId;
    }

    private void checkEmail(String emailCheck) {
        for (User user : users.values()) {
            if (user.getEmail().equals(emailCheck)) {
                String message = "Email " + emailCheck + " is already in use. Try another one.";
                log.debug(message);
                throw new ConflictException(message);
            }
        }
    }

    private void checkId(Long id) {
        if (!users.containsKey(id)) {
            String message = "There's no such user in our DataBase!";
            log.debug(message);
            throw new NotFoundException(message);
        }
    }

}
