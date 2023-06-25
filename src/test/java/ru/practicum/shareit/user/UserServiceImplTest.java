package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    private UserMapper userMapper;
    static UserDto userDto;

    final User user = new User();
    static Optional<User> optionalUser;
    static List<User> userList;
    static List<UserDto> userDtoList;

    @BeforeEach
    public void init() {
        userDto = UserDto.builder()
                .id(1L)
                .name("User name")
                .email("test.user@mail.com")
                .build();

        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        optionalUser = Optional.of(user);

        userList = List.of(user, user, user);
        userDtoList = List.of(userDto, userDto, userDto);
    }

    @Test
    public void createUserSuccess() {
        when(userRepository.save(any()))
                .thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(userDto);
        UserDto dtoReturned = userService.create(userDto);

        assertEquals(userDto.getId(), dtoReturned.getId());
        assertEquals(userDto.getName(), dtoReturned.getName());
        assertEquals(userDto.getEmail(), dtoReturned.getEmail());
    }

    @Test
    public void updateUserSuccess() {
        when(userRepository.findById(anyLong())).thenReturn(optionalUser);
        when(userRepository.save(any())).thenReturn(user);
        userDto.setName("Update");
        userDto.setEmail("newmail@mail.com");
        when(userMapper.toUserDto(user)).thenReturn(userDto);
        UserDto output = userService.update(userDto);
        assertEquals(output.getName(), userDto.getName());
    }

    @Test
    public void failUpdateUserNotFound() {
        when(userRepository.findById(anyLong())).thenThrow(new EntityNotFoundException("Not Found"));

        assertThrows(EntityNotFoundException.class,
                () -> userService.update(userDto));
    }

    @Test
    public void findAllSuccess() {
        when(userRepository.findAll()).thenReturn(userList);
        when(userMapper.userDtoList(userList)).thenReturn(userDtoList);

        List<UserDto> output = userService.findAll();
        assertEquals(output.size(), 3);
    }

    @Test
    public void findUserByIdSuccess() {
        when(userRepository.findById(anyLong())).thenReturn(optionalUser);
        when(userMapper.toUserDto(optionalUser.get())).thenReturn(userDto);
        UserDto output = userService.findUserById(userDto.getId());
        assertEquals(userDto.getName(), output.getName());
    }

    @Test
    public void failFindUserByIdNotFound() {
        assertThrows(EntityNotFoundException.class,
                () -> userService.findUserById(-1L));
    }

    @Test
    public void failRemoveUserNotFound() {
        when(userRepository.findById(anyLong())).thenThrow(new EntityNotFoundException("Not Found"));

        assertThrows(EntityNotFoundException.class,
                () -> userService.removeUser(-1L));
    }

    @Test
    public void removeUserSuccessful() {
        when(userRepository.findById(anyLong())).thenReturn(optionalUser);

        userService.removeUser(1L);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void removeUserFailUserNotFound() {
        assertThrows(EntityNotFoundException.class,
                () -> userService.removeUser(1L));
    }

}