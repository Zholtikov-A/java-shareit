package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestDtoInputTest {
    @Autowired
    JacksonTester<ItemRequestDtoInput> json;
    static ItemRequestDtoInput itemRequestDtoInput;

    @BeforeEach
    public void setUp() {
        itemRequestDtoInput = ItemRequestDtoInput.builder()
                .description("description")
                .build();
    }

    @Test
    public void createDtoSuccess() throws Exception {
        JsonContent<ItemRequestDtoInput> result = json.write(itemRequestDtoInput);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
    }

}