package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.EntityNotFoundException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemStorageInMemory implements ItemStorage {

    final Map<Long, Item> items = new HashMap<>();
    Long lastGeneratedId = 0L;

    @Override
    public Item create(@Valid Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item update(@Valid Item item) {
        checkId(item.getId());
        items.replace(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item findItemById(Long itemId) {
        checkId(itemId);
        Item itemInDB = items.get(itemId);
        return Item.builder()
                .id(itemInDB.getId())
                .name(itemInDB.getName())
                .description(itemInDB.getDescription())
                .available(itemInDB.getAvailable())
                .owner(itemInDB.getOwner())
                .build();
    }

    @Override
    public List<Item> findAll(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    public List<Item> search(String subString) {
        if (subString.isBlank()) {
            return new ArrayList<>();
        }

        return items.values().stream()
                .filter(item -> (item.getDescription().toLowerCase().contains(subString.toLowerCase())
                        || item.getName().toLowerCase().contains(subString.toLowerCase())) && item.getAvailable())
                .collect(Collectors.toList());
    }

    private Long generateId() {
        return ++lastGeneratedId;
    }

    private void checkId(Long id) {
        if (!items.containsKey(id)) {
            String message = "There's no such item in our DataBase!";
            log.debug(message);
            throw new EntityNotFoundException(message);
        }
    }
}
