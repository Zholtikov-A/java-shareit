package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Data
@RequestMapping(path = "/bookings")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingController {

    BookingService bookingService;
    private static final String HEADER_SHARER = "X-Sharer-User-Id";

    @PostMapping
    public BookingDtoOutput create(@RequestHeader(HEADER_SHARER) Long bookerId,
                                   @RequestBody @Valid BookingDtoInput bookingDto) {
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
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoOutput> getUserBookings(@RequestHeader(HEADER_SHARER) Long userId,
                                                  @RequestParam(value = "state", required = false, defaultValue = "ALL")
                                                  String state) {
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoOutput> getAllUserItemsBookings(@RequestHeader(HEADER_SHARER) long userId,
                                                          @RequestParam(value = "state", required = false, defaultValue = "ALL")
                                                          String state) {
        return bookingService.getAllUserItemsBookings(userId, state);
    }

}
