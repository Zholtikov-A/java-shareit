package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class ItemRequestController {

    private static final String HEADER_SHARER = "X-Sharer-User-Id";
    ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoOutput create(@RequestHeader(HEADER_SHARER) Long requesterId,
                                       @RequestBody ItemRequestDtoInput requestDto) {
        return itemRequestService.create(requesterId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDtoOutput> findUserRequests(@RequestHeader(HEADER_SHARER) Long userId,
                                                       @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                       @RequestParam(value = "size", defaultValue = "20") @PositiveOrZero Integer size) {
        return itemRequestService.findUserRequests(userId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOutput> findAll(@RequestHeader(HEADER_SHARER) Long requesterId,
                                              @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(value = "size", defaultValue = "20") @PositiveOrZero Integer size) {
        return itemRequestService.findAll(requesterId, from, size);

    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOutput findById(@RequestHeader(HEADER_SHARER) Long userId,
                                         @PathVariable @Positive Long requestId) {
        return itemRequestService.findItemRequestById(userId, requestId);
    }
}
