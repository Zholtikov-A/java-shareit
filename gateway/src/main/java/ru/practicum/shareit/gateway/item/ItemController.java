package ru.practicum.shareit.gateway.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.item.dto.CommentDtoInput;
import ru.practicum.shareit.gateway.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    private static final String HEADER_SHARER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER_SHARER) Long ownerId,
                                         @Validated @RequestBody ItemDto itemDto) {
        return itemClient.create(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestBody ItemDto itemDto,
                                         @PathVariable Long itemId,
                                         @RequestHeader(HEADER_SHARER) Long ownerId) {

        return itemClient.update(itemDto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@RequestHeader(HEADER_SHARER) Long userId,
                                               @PathVariable Long itemId) {
        return itemClient.findItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(HEADER_SHARER) Long ownerId,
                                          @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                          @RequestParam(value = "size", defaultValue = "20") @Positive Integer size) {
        return itemClient.findOwnerItems(ownerId, from, size);
    }


    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(HEADER_SHARER) Long userId, @RequestParam("text") String subString,
                                         @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(value = "size", defaultValue = "20") @Positive Integer size) {
        return itemClient.search(userId, subString, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addCommentToItem(@RequestHeader(HEADER_SHARER) Long userId, @PathVariable Long itemId,
                                                   @Validated @RequestBody CommentDtoInput dtoInput) {
        return itemClient.addComment(userId, itemId, dtoInput);
    }


}
