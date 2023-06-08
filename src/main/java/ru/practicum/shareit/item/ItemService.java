package ru.practicum.shareit.item;

import javax.validation.Valid;
import java.util.List;

public interface ItemService {
    ItemDto create(Long ownerId, @Valid ItemDto itemDto);

    ItemDto update(ItemDto itemDto, Long itemId, Long ownerId);

    ItemBookingCommentDto findItemById(Long userId, Long itemId);

    List<ItemBookingCommentDto> findOwnerItems(Long userId);

    List<ItemDto> search(String subString);

    CommentDtoOutput addComment(Long userId, Long itemId, CommentDtoInput dtoInput);
}
