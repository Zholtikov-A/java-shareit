package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserStorage;

import javax.validation.Valid;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemServiceImpl implements ItemService {
    ItemStorage itemStorage;
    UserStorage userStorage;
    ItemMapper itemMapper;
    UserMapper userMapper;

    public ItemDto create(Long ownerId, @Valid ItemDto itemDto) {

        UserDto owner = userMapper.toUserDto(userStorage.findUserById(ownerId));
        itemDto.setOwner(owner);
        Item item = itemMapper.toItem(itemDto);
        return itemMapper.toItemDto(itemStorage.create(item));
    }

    public ItemDto update(ItemDto itemDto, Long itemId, Long ownerId) {
        UserDto owner = userMapper.toUserDto(userStorage.findUserById(ownerId));
        Item item = itemStorage.findItemById(itemId);
        itemDto.setId(itemId);
        itemDto.setOwner(owner);
        if (itemDto.getOwner() != null &&
                !itemDto.getOwner().getId().equals(item.getOwner().getId())) {
            throw new ForbiddenOperationException("Item can be changed only by it's owner!");
        }

        if (itemDto.getName() != null &&
                !itemDto.getName().equals(item.getName())) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null &&
                !itemDto.getDescription().equals(item.getDescription())) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null &&
                !itemDto.getAvailable().equals(item.getAvailable())) {
            item.setAvailable(itemDto.getAvailable());
        }
        return itemMapper.toItemDto(itemStorage.update(item));
    }

    public ItemDto findItemById(Long itemId) {
        return itemMapper.toItemDto(itemStorage.findItemById(itemId));
    }

    public List<ItemDto> findAll(Long ownerId) {
        userStorage.findUserById(ownerId);
        List<Item> items = itemStorage.findAll(ownerId);
        return itemMapper.itemDtoList(items);
    }

    public List<ItemDto> search(String subString) {
        List<Item> items = itemStorage.search(subString);
        return itemMapper.itemDtoList(items);
    }
}