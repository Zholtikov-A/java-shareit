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
            "where b.booker.id = ?1  and ?2 " +
            "between b.start  and b.end  " +
            "order by b.start desc")
    List<Booking> findAllCurrentBookingsByUser(Long id, LocalDateTime dateTime, Sort sort);

    @Query("select b from Booking as b " +
            "join b.booker as u  " +
            "where b.booker.id = ?1  and b.start > ?2 " +
            "order by b.start desc")
    List<Booking> findAllFutureBookingsByUser(Long userId, LocalDateTime dateTime, Sort sort);

    @Query("select b from Booking as b " +
            "join b.booker as u  " +
            "where b.booker.id = ?1  and b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findAllPastBookingsByUser(Long userId, LocalDateTime dateTime, Sort sort);

    @Query("select b from Booking as b " +
            "join b.booker as u  " +
            "where b.booker.id = ?1  and b.status = 'REJECTED'" +
            "order by b.start desc")
    List<Booking> findAllRejectedBookingsByUser(Long userId, Sort sort);

    @Query("select b from Booking as b " +
            "join b.booker as u  " +
            "where b.booker.id = ?1  and b.status = 'WAITING'" +
            "order by b.start desc")
    List<Booking> findAllWaitingBookingsByUser(Long userId, Sort sort);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id in :userItems")
    List<Booking> findAllItemsBookings(List<Long> userItems, Sort sort);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id in ?1 " +
            "and ?2 between b.start  and b.end ")
    List<Booking> findAllItemsCurrentBookings(List<Long> userItems, LocalDateTime dateTime, Sort sort);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id in ?1 " +
            "and b.end < ?2 ")
    List<Booking> findAllItemsPastBookings(List<Long> userItems, LocalDateTime dateTime, Sort sort);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id in ?1 " +
            "and b.start > ?2 ")
    List<Booking> findAllItemsFutureBookings(List<Long> userItems, LocalDateTime dateTime, Sort sort);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id in ?1 " +
            "and b.status = 'REJECTED' ")
    List<Booking> findAllItemsRejectedBookings(List<Long> userItems, Sort sort);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id in ?1 " +
            "and b.status = 'WAITING' ")
    List<Booking> findAllItemsWaitingBookings(List<Long> userItems, Sort sort);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id = ?1 and " +
            "b.status = 'APPROVED' ")
    List<Booking> findAllBookingsByItemId(Long itemId);

/*    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id = ?1 ")
    List<Booking> findAllBookingsByItemId(Long itemId);*/

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join b.booker as u " +
            "where i.id = ?1 " +
            "and u.id = ?2 " +
            "and b.end < ?3 ")
    List<Booking> findAllByBookerIdAndItemIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime end);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id in ( ?1 ) ")
    List<Booking> findAllBookingsByItemIdList(List<Long> itemsId);

}
