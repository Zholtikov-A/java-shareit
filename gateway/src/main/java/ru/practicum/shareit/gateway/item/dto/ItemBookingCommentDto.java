package ru.practicum.shareit.gateway.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.gateway.booking.dto.BookingDtoForItemOwner;

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
