package ru.practicum.shareit.item;

import javax.validation.Valid;
import java.util.List;

public interface ItemService {
    ItemDto create(Long ownerId, @Valid ItemDto itemDto);

    ItemDto update(ItemDto itemDto, Long itemId, Long ownerId);

    ItemDto findItemById(Long itemId);

    List<ItemDto> findAll(Long ownerId);

    List<ItemDto> search(String subString);
}
