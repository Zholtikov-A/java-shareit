package ru.practicum.shareit.item;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.BookingDtoForItemOwner;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemBookingCommentDto {
    Long id;

    String name;

    String description;

    Boolean available;

    BookingDtoForItemOwner lastBooking;

    BookingDtoForItemOwner nextBooking;

    List<CommentDtoOutput> comments;
}
