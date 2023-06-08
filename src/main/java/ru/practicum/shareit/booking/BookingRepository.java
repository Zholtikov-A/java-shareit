package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    @Query("select b from Booking as b " +
            "join b.booker as u  " +
            "where b.booker.id = :id  and :dateTime " +
            "between b.start  and b.end  " +
            "order by b.start desc")
    List<Booking> findAllCurrentBookingsByUser(Long id, LocalDateTime dateTime, Sort sort);

    @Query("select b from Booking as b " +
            "join b.booker as u  " +
            "where b.booker.id = :userId  and b.start > :dateTime " +
            "order by b.start desc")
    List<Booking> findAllFutureBookingsByUser(Long userId, LocalDateTime dateTime, Sort sort);

    @Query("select b from Booking as b " +
            "join b.booker as u  " +
            "where b.booker.id = :userId  and b.end < :dateTime " +
            "order by b.start desc")
    List<Booking> findAllPastBookingsByUser(Long userId, LocalDateTime dateTime, Sort sort);

    @Query("select b from Booking as b " +
            "join b.booker as u  " +
            "where b.booker.id = :userId  and b.status = 'REJECTED'" +
            "order by b.start desc")
    List<Booking> findAllRejectedBookingsByUser(Long userId, Sort sort);

    @Query("select b from Booking as b " +
            "join b.booker as u  " +
            "where b.booker.id = :userId  and b.status = 'WAITING'" +
            "order by b.start desc")
    List<Booking> findAllWaitingBookingsByUser(Long userId, Sort sort);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id in :userItems")
    List<Booking> findAllItemsBookings(List<Long> userItems, Sort sort);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id in :userItems " +
            "and :dateTime between b.start  and b.end ")
    List<Booking> findAllItemsCurrentBookings(List<Long> userItems, LocalDateTime dateTime, Sort sort);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id in :userItems " +
            "and b.end < :dateTime ")
    List<Booking> findAllItemsPastBookings(List<Long> userItems, LocalDateTime dateTime, Sort sort);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id in :userItems " +
            "and b.start > :dateTime ")
    List<Booking> findAllItemsFutureBookings(List<Long> userItems, LocalDateTime dateTime, Sort sort);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id in :userItems " +
            "and b.status = 'REJECTED' ")
    List<Booking> findAllItemsRejectedBookings(List<Long> userItems, Sort sort);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id in :userItems " +
            "and b.status = 'WAITING' ")
    List<Booking> findAllItemsWaitingBookings(List<Long> userItems, Sort sort);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id = :itemId and " +
            "b.status = 'APPROVED' ")
    List<Booking> findAllBookingsByItemId(Long itemId);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join b.booker as u " +
            "where i.id = :itemId " +
            "and u.id = :bookerId " +
            "and b.end < :end ")
    List<Booking> findAllByBookerIdAndItemIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime end);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id in :itemsId ")
    List<Booking> findAllBookingsByItemIdList(List<Long> itemsId);

}
