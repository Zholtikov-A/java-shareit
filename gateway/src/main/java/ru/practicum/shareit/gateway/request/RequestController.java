package ru.practicum.shareit.gateway.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.request.dto.ItemRequestDtoInput;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {
    private final RequestClient requestClient;

    private static final String HEADER_SHARER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER_SHARER) Long requesterId,
                                         @Validated @RequestBody ItemRequestDtoInput requestDto) {
        return requestClient.create(requesterId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findUserRequests(@RequestHeader(HEADER_SHARER) Long userId,
                                                   @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                   @RequestParam(value = "size", defaultValue = "20") @Positive Integer size) {
        return requestClient.findUserRequests(userId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@RequestHeader(HEADER_SHARER) Long requesterId,
                                          @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                          @RequestParam(value = "size", defaultValue = "20") @Positive Integer size) {
        return requestClient.findAll(requesterId, from, size);
    }


    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@RequestHeader(HEADER_SHARER) Long userId,
                                           @PathVariable @Positive Long requestId) {
        return requestClient.findItemRequestById(userId, requestId);
    }


}
