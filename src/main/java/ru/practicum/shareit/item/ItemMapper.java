package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.annotations.Generated;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

@Generated
@Component
@RequiredArgsConstructor
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        ItemDto returnDto =
                ItemDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .build();
        if (item.getRequest() != null) {
            returnDto.setRequestId(item.getRequest().getId());
        }
        return returnDto;
    }

    public Item toItem(ItemDto itemDto, User owner, ItemRequest request) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);
        item.setRequest(request);
        return item;
    }

    public List<ItemDto> itemDtoList(List<Item> items) {
        return items.stream().map(this::toItemDto).collect(Collectors.toList());
    }

    public ItemBookingCommentDto toItemDtoBookingComment(Item item) {
        return ItemBookingCommentDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public List<ItemBookingCommentDto> toItemDtoBookingCommentList(List<Item> items) {
        return items.stream().map(this::toItemDtoBookingComment).collect(Collectors.toList());
    }

    public ItemResponseDto toItemResponseDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId((item.getRequest().getId()))
                .build();
    }


    public List<ItemResponseDto> toItemResponseDtoList(List<Item> items) {
        return items.stream().map(this::toItemResponseDto).collect(Collectors.toList());
    }

}
