package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private static final String HEADER_SHARER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoOutput create(@RequestHeader(HEADER_SHARER) Long requesterId,
                                       @RequestBody ItemRequestDtoInput requestDto) {
        return itemRequestService.create(requesterId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDtoOutput> findUserRequests(@RequestHeader(HEADER_SHARER) Long userId,
                                                       @RequestParam(value = "from") Integer from,
                                                       @RequestParam(value = "size") Integer size) {
        return itemRequestService.findUserRequests(userId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOutput> findAll(@RequestHeader(HEADER_SHARER) Long requesterId,
                                              @RequestParam(value = "from") Integer from,
                                              @RequestParam(value = "size") Integer size) {
        return itemRequestService.findAll(requesterId, from, size);

    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOutput findById(@RequestHeader(HEADER_SHARER) Long userId,
                                         @PathVariable Long requestId) {
        return itemRequestService.findItemRequestById(userId, requestId);
    }
}
