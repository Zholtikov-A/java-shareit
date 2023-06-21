package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingDtoInput;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {

    private final UserMapper userMapper;

    public ItemRequestDtoOutput toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDtoOutput.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(userMapper.toUserDto(itemRequest.getRequester()))
                .created(itemRequest.getCreated())
                .items(new ArrayList<>())
                .build();
    }


    public Booking toBooking(BookingDtoInput bookingDto, User booker, Item item) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        return booking;
    }

    public ItemRequest toItemRequest(ItemRequestDtoInput itemRequest, User requester) {
        ItemRequest request = new ItemRequest();
        request.setDescription(itemRequest.getDescription());
        request.setRequester(requester);
        return request;
    }

    public List<ItemRequestDtoOutput> toItemRequestDtoList(List<ItemRequest> requests) {
        return requests.stream().map(this::toItemRequestDto).collect(Collectors.toList());
    }

}
