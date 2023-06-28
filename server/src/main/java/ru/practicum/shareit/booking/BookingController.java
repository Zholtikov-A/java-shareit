package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingController {

    BookingService bookingService;
    private static final String HEADER_SHARER = "X-Sharer-User-Id";

    @PostMapping
    public BookingDtoOutput create(@RequestHeader(HEADER_SHARER) Long bookerId,
                                   @RequestBody BookingDtoInput bookingDto) {
        return bookingService.create(bookingDto, bookerId);
    }

    @PatchMapping("/{id}")
    public BookingDtoOutput setApproved(@PathVariable("id") Long bookingId, @RequestParam boolean approved,
                                        @RequestHeader(HEADER_SHARER) Long userId) {
        return bookingService.setApprove(bookingId, approved, userId);
    }

    @GetMapping("/{id}")
    public BookingDtoOutput getBookingById(@PathVariable("id") Long bookingId,
                                           @RequestHeader(HEADER_SHARER) Long userId) {
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoOutput> getUserBookings(@RequestHeader(HEADER_SHARER) Long userId,
                                                  @RequestParam(value = "from") Integer from,
                                                  @RequestParam(value = "size") Integer size,
                                                  @RequestParam(value = "state") String state) {
        return bookingService.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoOutput> findAllByOwner(@RequestHeader(HEADER_SHARER) Long userId,
                                                 @RequestParam(value = "from") Integer from,
                                                 @RequestParam(value = "size") Integer size,
                                                 @RequestParam(value = "state") String state) {
        return bookingService.findAllByOwner(userId, state, from, size);
    }

}
