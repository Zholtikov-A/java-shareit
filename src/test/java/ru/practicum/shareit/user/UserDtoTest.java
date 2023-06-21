package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {
    @Autowired
    JacksonTester<UserDto> jacksonTester;
    static UserDto userDto;

    @BeforeEach
    public void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .name("User name")
                .email("mail@me.com")
                .build();
    }

    @Test
    public void createDtoSuccess() throws Exception {
        JsonContent<UserDto> result = jacksonTester.write(userDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());
    }
}