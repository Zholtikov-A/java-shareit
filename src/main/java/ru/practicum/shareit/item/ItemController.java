package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class ItemController {

    private static final String HEADER_SHARER = "X-Sharer-User-Id";
    ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(HEADER_SHARER) Long ownerId,
                          @Validated @RequestBody ItemDto itemDto) {
        return itemService.create(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @PathVariable Long itemId,
                          @RequestHeader(HEADER_SHARER) Long ownerId) {

        return itemService.update(itemDto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemBookingCommentDto findItemById(@RequestHeader(HEADER_SHARER) Long userId,
                                              @PathVariable Long itemId) {
        return itemService.findItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemBookingCommentDto> findAll(@RequestHeader(HEADER_SHARER) Long ownerId,
                                               @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(value = "size", defaultValue = "20") @Positive Integer size) {
        return itemService.findOwnerItems(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String subString,
                                @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                @RequestParam(value = "size", defaultValue = "20") @Positive Integer size) {
        return itemService.search(subString, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoOutput addCommentToItem(@RequestHeader(HEADER_SHARER) Long userId, @PathVariable Long itemId,
                                             @Validated @RequestBody CommentDtoInput dtoInput) {
        return itemService.addComment(userId, itemId, dtoInput);
    }

}
