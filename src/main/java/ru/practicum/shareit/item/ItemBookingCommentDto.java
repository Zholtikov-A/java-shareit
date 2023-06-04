package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.booking.BookingDtoForItemOwner;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
public class ItemBookingCommentDto {

    public interface New {
    }

    public interface Exist {
    }

    public interface Update extends ItemDto.Exist {
    }


    private Long id;

    @NotBlank(groups = {ItemDto.New.class})
    private String name;

    @NotBlank(groups = {ItemDto.New.class})
    @Size(max = 500, groups = {ItemDto.New.class, ItemDto.Update.class})
    private String description;

    @NotNull(groups = {ItemDto.New.class})
    private Boolean available;

    private BookingDtoForItemOwner lastBooking;

    private BookingDtoForItemOwner nextBooking;

    private List<CommentDtoOutput> comments;
}
