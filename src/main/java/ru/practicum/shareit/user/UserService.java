package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserStorage userStorage;
    UserMapper userMapper;

    public UserDto create(UserDto userDto) {
        User user = userStorage.create(userMapper.toUser(userDto));
        return userMapper.toUserDto(user);
    }

    public UserDto update(UserDto userDto) {
        User user = userStorage.findUserById(userDto.getId());

        if (userDto.getName() != null && !userDto.getName().equals(user.getName())) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            user.setEmail(userDto.getEmail());
        }

        user = userStorage.update(user);
        return userMapper.toUserDto(user);
    }

    public List<UserDto> findAll() {
        List<User> users = userStorage.findAll();
        return userMapper.userDtoList(users);
    }

    public UserDto findUserById(Long id) {
        return userMapper.toUserDto(userStorage.findUserById(id));
    }

    public void removeUser(Long id) {
        userStorage.removeUser(id);
    }
}
