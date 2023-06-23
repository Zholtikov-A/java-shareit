package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedWith404Exception;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationExceptionCustom;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingServiceImpl implements BookingService {

    BookingRepository bookingRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;
    BookingMapper bookingMapper;

    @Override
    public BookingDtoOutput create(BookingDtoInput bookingDto, Long bookerId) {

        if ((bookingDto.getEnd().isBefore(bookingDto.getStart())) || bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new ValidationExceptionCustom("Booking end time can't be before or at same time then start time");
        }
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Not found: item's id " + bookingDto.getItemId()));
        if (item.getOwner().getId().equals(bookerId)) {
            throw new AccessDeniedWith404Exception("User with id " + bookerId + " is owner of the item with id " + item.getId());
        }
        if (!item.getAvailable()) {
            throw new ValidationExceptionCustom("Item is not available for booking");
        }
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new EntityNotFoundException("Not found: booker's id " + bookerId));
        Booking booking = bookingMapper.toBooking(bookingDto, booker, item);
        booking.setStatus(BookingStatus.WAITING);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoOutput setApprove(Long bookingId, boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Not found: booking's id " + bookingId));
        if ((booking.getStatus() == BookingStatus.APPROVED && approved)
                || (booking.getStatus() == BookingStatus.REJECTED && !approved)) {
            throw new ValidationExceptionCustom("Booking status is already set");
        }
        Item item = booking.getItem();
        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessDeniedWith404Exception("Booking can be approved only by items owner!");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationExceptionCustom("Booking status must be 'WAITING'");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else booking.setStatus((BookingStatus.REJECTED));
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoOutput findById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Not found: booking's id " + bookingId));
        Item item = booking.getItem();
        if (booking.getBooker().getId().equals(userId) || item.getOwner().getId().equals(userId)) {
            return bookingMapper.toBookingDto(booking);
        } else {
            throw new AccessDeniedWith404Exception("Only booker or item owner can watch this booking!");
        }
    }

    @Override
    public List<BookingDtoOutput> getUserBookings(Long userId, String state, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw (new EntityNotFoundException("Not found: owner id: " + userId));
        }
        BookingSearchState bookingSearchState;
        try {
            bookingSearchState = BookingSearchState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationExceptionCustom("Unknown state: UNSUPPORTED_STATUS");
        }
        int page = from / size;
        Pageable params = PageRequest.of(page, size, Sort.by("start"));
        LocalDateTime now = LocalDateTime.now();
        List<BookingDtoOutput> bookings = new ArrayList<>();
        switch (bookingSearchState) {
            case ALL:
                bookings = bookingMapper.bookingDtoList(bookingRepository.findAllBookingsByUserId(userId, params));
                break;
            case CURRENT:
                bookings = bookingMapper.bookingDtoList(bookingRepository.findAllBookingsByUserIdCurrent(userId, now, params));
                break;
            case PAST:
                bookings = bookingMapper.bookingDtoList(bookingRepository.findAllBookingsByUserIdPast(userId, now, params));
                break;
            case FUTURE:
                bookings = bookingMapper.bookingDtoList(bookingRepository.findAllBookingsByUserIdFuture(userId, now, params));
                break;
            case WAITING:
                bookings = bookingMapper.bookingDtoList(bookingRepository.findAllBookingsByUserIdWaiting(userId, params));
                break;
            case REJECTED:
                bookings = bookingMapper.bookingDtoList(bookingRepository.findAllBookingsByUserIdRejected(userId, params));
        }
        return bookings;
    }

    @Override
    public List<BookingDtoOutput> findAllByOwner(Long ownerId, String state, Integer from, Integer size) {
        if (!userRepository.existsById(ownerId)) {
            throw (new EntityNotFoundException("Not found: owner id: " + ownerId));
        }
        BookingSearchState bookingSearchState;
        try {
            bookingSearchState = BookingSearchState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationExceptionCustom("Unknown state: UNSUPPORTED_STATUS");
        }
        int page = from / size;
        Pageable params = PageRequest.of(page, size, Sort.by("start"));
        LocalDateTime now = LocalDateTime.now();
        List<BookingDtoOutput> bookings = new ArrayList<>();
        switch (bookingSearchState) {
            case ALL:
                bookings = bookingMapper.bookingDtoList(bookingRepository.findAllBookingsByOwnerId(ownerId, params));
                break;
            case CURRENT:
                bookings = bookingMapper.bookingDtoList(bookingRepository.findAllBookingsByOwnerIdCurrent(ownerId, now, params));
                break;
            case PAST:
                bookings = bookingMapper.bookingDtoList(bookingRepository.findAllBookingsByOwnerIdPast(ownerId, now, params));
                break;
            case FUTURE:
                bookings = bookingMapper.bookingDtoList(bookingRepository.findAllBookingsByOwnerIdFuture(ownerId, now, params));
                break;
            case WAITING:
                bookings = bookingMapper.bookingDtoList(bookingRepository.findAllBookingsByOwnerIdWaiting(ownerId, params));
                break;
            case REJECTED:
                bookings = bookingMapper.bookingDtoList(bookingRepository.findAllBookingsByOwnerIdRejected(ownerId, params));
        }
        return bookings;
    }

}

