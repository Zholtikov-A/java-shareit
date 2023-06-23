package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;

import ru.practicum.shareit.exception.AccessDeniedWith404Exception;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationExceptionCustom;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingServiceImplTest {

    @InjectMocks
    BookingServiceImpl bookingService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingMapper bookingMapper;

    final LocalDateTime start = LocalDateTime.of(3033, Month.JANUARY, 9, 17, 10, 11);
    final LocalDateTime end = LocalDateTime.of(3033, Month.JANUARY, 9, 17, 40, 11);
    final UserDto owner = UserDto.builder()
            .id(1L)
            .name("owner")
            .email("owner.user@mail.com")
            .build();
    final UserDto booker = UserDto.builder()
            .id(2L)
            .name("booker")
            .email("booker.user@mail.com")
            .build();

    final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Item name")
            .description("Item description")
            .available(true)
            .build();

    final BookingDtoOutput bookingDtoOutput = BookingDtoOutput.builder()
            .id(1L)
            .item(itemDto)
            .start(start)
            .end(end)
            .booker(booker)
            .status(BookingStatus.WAITING)
            .build();

    final BookingDtoOutput appBookingDtoOutput = BookingDtoOutput.builder()
            .id(1L)
            .item(itemDto)
            .start(start)
            .end(end)
            .booker(booker)
            .status(BookingStatus.APPROVED)
            .build();

    final BookingDtoOutput rejBookingDtoOutput = BookingDtoOutput.builder()
            .id(1L)
            .item(itemDto)
            .start(start)
            .end(end)
            .booker(booker)
            .status(BookingStatus.REJECTED)
            .build();
    BookingDtoInput bookingDtoInput;
    final User userBooker = new User();

    final User userOwner = new User();
    final Item item = new Item();
    final Booking booking = new Booking();
    final Booking appBooking = new Booking();
    final Booking rejBooking = new Booking();
    static Optional<User> optionalUserBooker;
    static Optional<User> optionalUserOwner;
    static Optional<Item> optionalItem;
    static Optional<Booking> optionalBooking;
    static Optional<Booking> optionalApprovedBooking;
    static Optional<Booking> optionalRejectedBooking;
    List<Booking> bookingPage;

    List<BookingDtoOutput> bookingsDto;

    @BeforeEach
    public void setUp() {
        userBooker.setId(booker.getId());
        userBooker.setName(booker.getName());
        userBooker.setEmail(booker.getEmail());
        optionalUserBooker = Optional.of(userBooker);

        userOwner.setId(owner.getId());
        userOwner.setName(owner.getName());
        userOwner.setEmail(owner.getEmail());
        optionalUserOwner = Optional.of(userOwner);

        item.setId(1L);
        item.setName("Item name");
        item.setDescription("Item description");
        item.setOwner(userOwner);
        item.setAvailable(true);
        optionalItem = Optional.of(item);

        appBooking.setId(1L);
        appBooking.setBooker(userBooker);
        appBooking.setItem(item);
        appBooking.setStart(start);
        appBooking.setEnd(end);
        appBooking.setStatus(BookingStatus.APPROVED);

        rejBooking.setId(1L);
        rejBooking.setBooker(userBooker);
        rejBooking.setItem(item);
        rejBooking.setStart(start);
        rejBooking.setEnd(end);
        rejBooking.setStatus(BookingStatus.REJECTED);

        booking.setId(1L);
        booking.setBooker(userBooker);
        booking.setItem(item);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(BookingStatus.WAITING);


        optionalRejectedBooking = Optional.of(rejBooking);
        optionalApprovedBooking = Optional.of(appBooking);
        optionalBooking = Optional.of(booking);

        bookingDtoInput = BookingDtoInput.builder()
                .bookerId(booker.getId())
                .itemId(itemDto.getId())
                .start(start)
                .end(end)
                .build();

        bookingPage = List.of(booking, booking, booking);

        bookingsDto = List.of(bookingDtoOutput, bookingDtoOutput, bookingDtoOutput);

    }

    @Test
    public void createBookingSuccess() {
        when(bookingRepository.save(any())).thenReturn(booking);
        when(userRepository.findById(booker.getId())).thenReturn(optionalUserBooker);
        when(itemRepository.findById(anyLong())).thenReturn(optionalItem);
        when(bookingMapper.toBooking(any(), any(), any())).thenReturn(booking);
        when(bookingMapper.toBookingDto(any())).thenReturn(bookingDtoOutput);
        BookingDtoOutput checkDto = bookingService.create(bookingDtoInput, booker.getId());
        assertEquals(checkDto, bookingDtoOutput);
    }

    @Test
    public void failToBookOwnerOrBookerNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(optionalItem);
        assertThrows(EntityNotFoundException.class,
                () -> bookingService.create(bookingDtoInput, anyLong()));
    }

    @Test
    public void failToBookItemNotFound() {
        assertThrows(EntityNotFoundException.class,
                () -> bookingService.create(bookingDtoInput, anyLong()));
    }

    @Test
    public void failToBookYourOwnItem() {
        when(itemRepository.findById(anyLong())).thenReturn(optionalItem);

        bookingDtoInput.setBookerId(owner.getId());

        assertThrows(AccessDeniedWith404Exception.class,
                () -> bookingService.create(bookingDtoInput, owner.getId()));
    }

    @Test
    public void failToBookItemNotAvailable() {
        item.setAvailable(false);
        optionalItem = Optional.of(item);
        when(itemRepository.findById(anyLong())).thenReturn(optionalItem);

        assertThrows(ValidationExceptionCustom.class,
                () -> bookingService.create(bookingDtoInput, userBooker.getId()));
    }

    @Test
    public void failToBookStartAfterEnd() {
        bookingDtoInput.setEnd(LocalDateTime.now());

        assertThrows(ValidationExceptionCustom.class,
                () -> bookingService.create(bookingDtoInput, owner.getId()));
    }

    @Test
    public void failToBookStartEqualsEnd() {
        bookingDtoInput.setEnd(start);

        assertThrows(ValidationExceptionCustom.class,
                () -> bookingService.create(bookingDtoInput, owner.getId()));
    }

    @Test
    public void setApproveSuccess() {
        when(bookingRepository.findById(anyLong())).thenReturn(optionalBooking);
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingMapper.toBookingDto(appBooking)).thenReturn(appBookingDtoOutput);

        BookingDtoOutput output = bookingService.setApprove(booking.getId(), true, owner.getId());


        assertEquals(BookingStatus.APPROVED, output.getStatus());
    }

    @Test
    public void setRejectedSuccess() {
        when(bookingRepository.findById(anyLong())).thenReturn(optionalBooking);
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingMapper.toBookingDto(rejBooking)).thenReturn(rejBookingDtoOutput);

        BookingDtoOutput output = bookingService.setApprove(owner.getId(), false, booking.getId());
        assertEquals(BookingStatus.REJECTED, output.getStatus());
    }

    @Test
    public void failSetApproveBecauseStatusWasNotWaiting() {
        booking.setStatus(BookingStatus.APPROVED);
        optionalBooking = Optional.of(booking);
        when(bookingRepository.findById(anyLong())).thenReturn(optionalBooking);

        assertThrows(ValidationExceptionCustom.class,
                () -> bookingService.setApprove(owner.getId(), false, booking.getId()));
    }

    @Test
    public void failSetApproveBecauseUserIsNotOwner() {
        when(bookingRepository.findById(anyLong())).thenReturn(optionalBooking);

        assertThrows(AccessDeniedWith404Exception.class,
                () -> bookingService.setApprove(booking.getId(), false, 999L));
    }

    @Test
    public void failSetApproveBookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenThrow(new EntityNotFoundException("Not Found"));

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.setApprove(owner.getId(), false, booking.getId()));
    }

    @Test
    public void failSetApproveBecauseStatusWasAlreadySetApproved() {
        when(bookingRepository.findById(anyLong())).thenReturn(optionalApprovedBooking);

        assertThrows(ValidationExceptionCustom.class,
                () -> bookingService.setApprove(owner.getId(), false, booking.getId()));
    }

    @Test
    public void failSetApproveBecauseStatusWasAlreadySetRejected() {
        when(bookingRepository.findById(anyLong())).thenReturn(optionalRejectedBooking);

        assertThrows(ValidationExceptionCustom.class,
                () -> bookingService.setApprove(owner.getId(), false, booking.getId()));
    }

    @Test
    public void findBookingByIdSuccess() {
        when(bookingRepository.findById(anyLong())).thenReturn(optionalBooking);

        bookingService.findById(booking.getId(), owner.getId());
    }

    @Test
    public void failFindByIdByWrongUser() {
        when(bookingRepository.findById(anyLong())).thenReturn(optionalBooking);

        assertThrows(AccessDeniedWith404Exception.class,
                () -> bookingService.findById(booking.getId(), -1L));

    }

    @Test
    public void failFindByIdBookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenThrow(new EntityNotFoundException("Not Found"));

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.findById(booking.getId(), owner.getId()));

    }

    @Test
    public void findAllForBookerAllSuccess() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findAllBookingsByUserId(anyLong(), any())).thenReturn(bookingPage);

        when(bookingMapper.bookingDtoList(bookingPage)).thenReturn(bookingsDto);

        List<BookingDtoOutput> output = bookingService.getUserBookings(booker.getId(), "ALL", 0, 3);
        assertEquals(output.size(), 3);
    }

    @Test
    public void findAllForBookerCURRENTSuccess() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllBookingsByUserIdCurrent(anyLong(), any(), any())).thenReturn(bookingPage);
        when(bookingMapper.bookingDtoList(bookingPage)).thenReturn(bookingsDto);
        List<BookingDtoOutput> output = bookingService.getUserBookings(booker.getId(), "CURRENT", 0, 3);
        assertEquals(output.size(), 3);
    }

    @Test
    public void findAllForBookerPASTSuccess() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllBookingsByUserIdPast(anyLong(), any(), any())).thenReturn(bookingPage);
        when(bookingMapper.bookingDtoList(bookingPage)).thenReturn(bookingsDto);
        List<BookingDtoOutput> output = bookingService.getUserBookings(booker.getId(), "PAST", 0, 3);
        assertEquals(output.size(), 3);
    }

    @Test
    public void findAllForBookerFUTURESuccess() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllBookingsByUserIdFuture(anyLong(), any(), any())).thenReturn(bookingPage);
        when(bookingMapper.bookingDtoList(bookingPage)).thenReturn(bookingsDto);
        List<BookingDtoOutput> output = bookingService.getUserBookings(booker.getId(), "FUTURE", 0, 3);
        assertEquals(output.size(), 3);
    }

    @Test
    public void findAllForBookerWAITINGSuccess() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllBookingsByUserIdWaiting(anyLong(), any())).thenReturn(bookingPage);
        when(bookingMapper.bookingDtoList(bookingPage)).thenReturn(bookingsDto);
        List<BookingDtoOutput> output = bookingService.getUserBookings(booker.getId(), "WAITING", 0, 3);
        assertEquals(output.size(), 3);
    }

    @Test
    public void findAllForBookerREJECTEDSuccess() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllBookingsByUserIdRejected(anyLong(), any())).thenReturn(bookingPage);
        when(bookingMapper.bookingDtoList(bookingPage)).thenReturn(bookingsDto);
        List<BookingDtoOutput> output = bookingService.getUserBookings(booker.getId(), "REJECTED", 0, 3);
        assertEquals(output.size(), 3);
    }

    @Test
    public void failFindAllForBookerUnknownState() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertThrows(ValidationExceptionCustom.class,
                () -> bookingService.getUserBookings(booker.getId(), "WRONG", 0, 3));
    }

    @Test
    public void failFindAllForOwnerUnknownState() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertThrows(ValidationExceptionCustom.class,
                () -> bookingService.findAllByOwner(owner.getId(), "WRONG", 0, 3));
    }

    @Test
    public void findAllForOwnerALLSuccess() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllBookingsByOwnerId(anyLong(), any())).thenReturn(bookingPage);
        when(bookingMapper.bookingDtoList(bookingPage)).thenReturn(bookingsDto);
        List<BookingDtoOutput> output = bookingService.findAllByOwner(owner.getId(), "ALL", 0, 3);
        assertEquals(output.size(), 3);
    }

    @Test
    public void findAllForOwnerCURRENTSuccess() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllBookingsByOwnerIdCurrent(anyLong(), any(), any())).thenReturn(bookingPage);
        when(bookingMapper.bookingDtoList(bookingPage)).thenReturn(bookingsDto);
        List<BookingDtoOutput> output = bookingService.findAllByOwner(owner.getId(), "CURRENT", 0, 3);
        assertEquals(output.size(), 3);
    }

    @Test
    public void findAllForOwnerPASTSuccess() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllBookingsByOwnerIdPast(anyLong(), any(), any())).thenReturn(bookingPage);
        when(bookingMapper.bookingDtoList(bookingPage)).thenReturn(bookingsDto);
        List<BookingDtoOutput> output = bookingService.findAllByOwner(owner.getId(), "PAST", 0, 3);
        assertEquals(output.size(), 3);
    }

    @Test
    public void findAllForOwnerFUTURESuccess() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllBookingsByOwnerIdFuture(anyLong(), any(), any())).thenReturn(bookingPage);
        when(bookingMapper.bookingDtoList(bookingPage)).thenReturn(bookingsDto);
        List<BookingDtoOutput> output = bookingService.findAllByOwner(owner.getId(), "FUTURE", 0, 3);
        assertEquals(output.size(), 3);
    }

    @Test
    public void findAllForOwnerWAITINGSuccess() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllBookingsByOwnerIdWaiting(anyLong(), any())).thenReturn(bookingPage);
        when(bookingMapper.bookingDtoList(bookingPage)).thenReturn(bookingsDto);
        List<BookingDtoOutput> output = bookingService.findAllByOwner(owner.getId(), "WAITING", 0, 3);
        assertEquals(output.size(), 3);
    }

    @Test
    public void findAllForOwnerREJECTEDSuccess() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllBookingsByOwnerIdRejected(anyLong(), any())).thenReturn(bookingPage);
        when(bookingMapper.bookingDtoList(bookingPage)).thenReturn(bookingsDto);
        List<BookingDtoOutput> output = bookingService.findAllByOwner(owner.getId(), "REJECTED", 0, 3);
        assertEquals(output.size(), 3);
    }
}
