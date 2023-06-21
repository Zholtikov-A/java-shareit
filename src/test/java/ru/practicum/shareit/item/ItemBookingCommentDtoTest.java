package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingDtoForItemOwner;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemBookingCommentDtoTest {

    @Autowired
    private JacksonTester<ItemBookingCommentDto> json;

    @Test
    void makeItemBookingCommentDto() throws Exception {

        BookingDtoForItemOwner nextBooking = BookingDtoForItemOwner.builder()
                .id(2L)
                .start(LocalDateTime.of(2023, Month.JUNE, 20, 18, 0))
                .end(LocalDateTime.of(2023, Month.JUNE, 22, 10, 0))
                .bookerId(2L)
                .build();

        BookingDtoForItemOwner lastBooking = BookingDtoForItemOwner.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, Month.JUNE, 17, 10, 0))
                .end(LocalDateTime.of(2023, Month.JUNE, 20, 10, 0))
                .bookerId(2L)
                .build();

        CommentDtoOutput commentDtoOutput = CommentDtoOutput.builder()
                .id(1L)
                .text("comment")
                .authorName("User")
                .created(LocalDateTime.now())
                .build();

        ItemBookingCommentDto itemBookingCommentDto = ItemBookingCommentDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(List.of(commentDtoOutput))
                .build();
        JsonContent<ItemBookingCommentDto> jsonItemDto = json.write(itemBookingCommentDto);

        assertThat(jsonItemDto).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonItemDto).extractingJsonPathStringValue("$.name").isEqualTo("Item");
        assertThat(jsonItemDto).extractingJsonPathStringValue("$.description").isEqualTo("Description");
        assertThat(jsonItemDto).extractingJsonPathValue("$.lastBooking").isNotNull();
        assertThat(jsonItemDto).extractingJsonPathValue("$.nextBooking").isNotNull();
        assertThat(jsonItemDto).extractingJsonPathValue("$.comments").isNotNull();
        assertThat(jsonItemDto).extractingJsonPathBooleanValue("$.available").isEqualTo(true);

    }

}