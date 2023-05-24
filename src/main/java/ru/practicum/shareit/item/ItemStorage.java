package ru.practicum.shareit.item;

import javax.validation.Valid;
import java.util.List;

public interface ItemStorage {

    Item create(@Valid Item item);

    Item update(@Valid Item item);

    Item findItemById(Long itemId);

    List<Item> findAll(Long ownerId);

    List<Item> search(String subString);
}
