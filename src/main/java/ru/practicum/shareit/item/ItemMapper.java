package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    private final UserMapper userMapper;

    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public Item toItem(ItemDto itemDto, User owner) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);
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

}
