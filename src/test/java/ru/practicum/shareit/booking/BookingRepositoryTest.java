package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;


import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingRepositoryTest {

    @Autowired
    TestEntityManager entityManager;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    final LocalDateTime start = LocalDateTime.of(3033, Month.JANUARY, 9, 17, 10, 11);
    final LocalDateTime end = LocalDateTime.of(3033, Month.JANUARY, 9, 17, 40, 11);
    static User owner;
    static User booker;
    static Item item;
    static Booking booking;
    static Pageable params = PageRequest.of(0, 1, Sort.by("start"));

    @BeforeEach
    public void setUp() {
        owner = new User();
        owner.setName("User Owner");
        owner.setEmail("owner.user@mail.com");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("User Booker");
        booker.setEmail("booker.user@mail.com");
        booker = userRepository.save(booker);

        item = new Item();
        item.setOwner(owner);
        item.setName("Item name");
        item.setDescription("Item description");
        item.setAvailable(true);
        item = itemRepository.save(item);

        booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);
    }

    @Test
    public void findBookingByItemIdSuccess() {
        booking.setStatus(BookingStatus.APPROVED);
        List<Booking> bookingFound = bookingRepository.findByItemId(item.getId());
        assertEquals(booking, bookingFound.get(0));
    }

    @Test
    public void findBookingByItemIdListSuccess() {
        booking.setStatus(BookingStatus.APPROVED);
        List<Booking> bookingFound = bookingRepository.findByItemIdList(List.of(item.getId()));
        assertEquals(booking, bookingFound.get(0));
    }

    @Test
    public void findAllCompletedBookingsByBookerIdAndItemIdSuccess() {
        booking.setStart(LocalDateTime.of(2020, Month.JANUARY, 1, 12, 1));
        booking.setEnd(LocalDateTime.of(2020, Month.JANUARY, 1, 13, 1));
        List<Booking> bookingFound = bookingRepository.findAllCompletedBookingsByBookerIdAndItemId(
                booker.getId(), item.getId(), LocalDateTime.now());
        assertEquals(booking, bookingFound.get(0));
    }

    @Test
    public void findAllBookingsByUserIdSuccess() {
        List<Booking> bookingFound = bookingRepository.findAllBookingsByUserId(booker.getId(), params);
        assertEquals(booking, bookingFound.get(0));
    }

    @Test
    public void findAllBookingsByUserIdCurrentSuccess() {
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().plusHours(1));
        List<Booking> bookingFound = bookingRepository.findAllBookingsByUserIdCurrent(
                booker.getId(), LocalDateTime.now(), params);
        assertEquals(booking, bookingFound.get(0));
    }

    @Test
    public void findAllBookingsByBookerIdPastSuccess() {
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        List<Booking> bookingFound = bookingRepository.findAllBookingsByUserIdPast(
                booker.getId(), LocalDateTime.now(), params);
        assertEquals(booking, bookingFound.get(0));
    }

    @Test
    public void findAllBookingsByUserIdFutureSuccess() {
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        List<Booking> bookingFound = bookingRepository.findAllBookingsByUserIdFuture(
                booker.getId(), LocalDateTime.now(), params);
        assertEquals(booking, bookingFound.get(0));
    }

    @Test
    public void findAllBookingsByUserIdWaitingSuccess() {
        List<Booking> bookingFound = bookingRepository.findAllBookingsByUserIdWaiting(booker.getId(), params);
        assertEquals(booking, bookingFound.get(0));
    }

    @Test
    public void findAllBookingsByUserIdRejectedSuccess() {
        booking.setStatus(BookingStatus.REJECTED);
        List<Booking> bookingFound = bookingRepository.findAllBookingsByUserIdRejected(booker.getId(), params);
        assertEquals(booking, bookingFound.get(0));
    }

    @Test
    public void findAllBookingsByOwnerIdSuccess() {
        List<Booking> bookingFound = bookingRepository.findAllBookingsByOwnerId(owner.getId(), params);
        assertEquals(booking, bookingFound.get(0));
    }

    @Test
    public void findAllBookingsByOwnerIdCurrentSuccess() {
        booking.setStart(LocalDateTime.now().minusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(1));
        List<Booking> bookingFound = bookingRepository.findAllBookingsByOwnerIdCurrent(
                owner.getId(), LocalDateTime.now(), params);
        assertEquals(booking, bookingFound.get(0));
    }

    @Test
    public void findAllBookingsByOwnerIdPastSuccess() {
        booking.setStart(LocalDateTime.now().minusHours(2));
        booking.setEnd(LocalDateTime.now().minusHours(1));
        List<Booking> bookingFound = bookingRepository.findAllBookingsByOwnerIdPast(
                owner.getId(), LocalDateTime.now(), params);
        assertEquals(booking, bookingFound.get(0));
    }

    @Test
    public void findAllBookingsByOwnerIdFutureSuccess() {
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        List<Booking> bookingFound = bookingRepository.findAllBookingsByOwnerIdFuture(
                owner.getId(), LocalDateTime.now(), params);
        assertEquals(booking, bookingFound.get(0));
    }

    @Test
    public void findAllBookingsByOwnerIdWaitingSuccess() {
        List<Booking> bookingFound = bookingRepository.findAllBookingsByOwnerIdWaiting(owner.getId(), params);
        assertEquals(booking, bookingFound.get(0));
    }

    @Test
    public void findAllBookingsByOwnerIdRejectedSuccess() {
        booking.setStatus(BookingStatus.REJECTED);
        List<Booking> bookingFound = bookingRepository.findAllBookingsByOwnerIdRejected(owner.getId(), params);
        assertEquals(booking, bookingFound.get(0));
    }
}
