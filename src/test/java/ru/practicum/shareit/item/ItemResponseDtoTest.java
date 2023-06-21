package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;


import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemResponseDtoTest {
    @Autowired
    private JacksonTester<ItemResponseDto> json;

    @Test
    void makeItemBookingCommentDto() throws Exception {

        ItemResponseDto itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .requestId(1L)
                .build();
        JsonContent<ItemResponseDto> jsonItemDto = json.write(itemResponseDto);

        assertThat(jsonItemDto).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonItemDto).extractingJsonPathStringValue("$.name").isEqualTo("Item");
        assertThat(jsonItemDto).extractingJsonPathStringValue("$.description").isEqualTo("Description");
        assertThat(jsonItemDto).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonItemDto).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }

}