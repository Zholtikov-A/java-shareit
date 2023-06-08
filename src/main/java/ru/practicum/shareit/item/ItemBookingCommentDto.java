package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.booking.BookingDtoForItemOwner;

import java.util.List;

@Data
@Builder
public class ItemBookingCommentDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingDtoForItemOwner lastBooking;

    private BookingDtoForItemOwner nextBooking;

    private List<CommentDtoOutput> comments;
}
