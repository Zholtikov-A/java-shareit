package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
import java.util.List;
import java.util.stream.Collectors;


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
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Not found: item's id " + bookingDto.getItemId()));
        if (item.getOwner().getId().equals(bookerId)) {
            throw new AccessDeniedWith404Exception("User with id " + bookerId + " is owner of the item with id " + item.getId());
        }
        if (!item.getAvailable()) {
            throw new ValidationExceptionCustom("Item is not available for booking");
        }

        if ((bookingDto.getEnd().isBefore(bookingDto.getStart())) || bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new ValidationExceptionCustom("Booking end time can't be before or at same time then start time");
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
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else booking.setStatus((BookingStatus.REJECTED));
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoOutput getBookingById(Long bookingId, Long userId) {
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
    public List<BookingDtoOutput> getUserBookings(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Not found: user's id " + userId));
        List<Booking> bookings;
        BookingSearchState bookingSearchState;
        try {
            bookingSearchState = BookingSearchState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationExceptionCustom("Unknown state: UNSUPPORTED_STATUS");
        }

        switch (bookingSearchState) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(userId, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case CURRENT:
                bookings = bookingRepository.findAllCurrentBookingsByUser(userId, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                break;
            case PAST:
                bookings = bookingRepository.findAllPastBookingsByUser(userId, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                break;
            case REJECTED:
                bookings = bookingRepository.findAllRejectedBookingsByUser(userId, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findAllFutureBookingsByUser(userId, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                break;
            case WAITING:
                bookings = bookingRepository.findAllWaitingBookingsByUser(userId, Sort.by(Sort.Direction.DESC, "start"));
                break;
            default:
                throw new ValidationExceptionCustom("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingMapper.bookingDtoList(bookings);
    }

    @Override
    public List<BookingDtoOutput> getAllUserItemsBookings(long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Not found: user's id " + userId));
        List<Long> userItems = itemRepository.findAllByOwnerIdOrderByIdAsc(userId)
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> bookings;
        BookingSearchState bookingSearchState;
        try {
            bookingSearchState = BookingSearchState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationExceptionCustom("Unknown state: UNSUPPORTED_STATUS");
        }

        switch (bookingSearchState) {
            case ALL:
                bookings = bookingRepository.findAllItemsBookings(userItems, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case CURRENT:
                bookings = bookingRepository.findAllItemsCurrentBookings(userItems, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                break;
            case PAST:
                bookings = bookingRepository.findAllItemsPastBookings(userItems, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                break;
            case REJECTED:
                bookings = bookingRepository.findAllItemsRejectedBookings(userItems, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findAllItemsFutureBookings(userItems, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                break;
            case WAITING:
                bookings = bookingRepository.findAllItemsWaitingBookings(userItems, Sort.by(Sort.Direction.DESC, "start"));
                break;
            default:
                throw new ValidationExceptionCustom("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingMapper.bookingDtoList(bookings);
    }


}

