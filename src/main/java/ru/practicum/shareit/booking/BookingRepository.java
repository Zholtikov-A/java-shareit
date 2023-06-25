package ru.practicum.shareit.booking;


import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends PagingAndSortingRepository<Booking, Long> {

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id = :itemId and " +
            "b.status = 'APPROVED' ")
    List<Booking> findByItemId(Long itemId);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id in(:itemIdList) and " +
            "b.status = 'APPROVED' ")
    List<Booking> findByItemIdList(List<Long> itemIdList);

    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.booker as booker" +
            " JOIN b.item as i" +
            " WHERE booker.id = :bookerId" +
            " AND i.id = :itemId" +
            " AND b.end < :now" +
            " ORDER BY b.start DESC")
    List<Booking> findAllCompletedBookingsByBookerIdAndItemId(Long bookerId, Long itemId, LocalDateTime now);

    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.booker as booker" +
            " JOIN b.item as i" +
            " WHERE booker.id = :userId" +
            " ORDER BY b.start DESC")
    List<Booking> findAllBookingsByUserId(Long userId, Pageable pageable);

    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.booker as booker" +
            " JOIN b.item as i" +
            " WHERE booker.id = :userId" +
            " AND (b.start < :now" +
            " AND b.end > :now)" +
            " ORDER BY b.start DESC")
    List<Booking> findAllBookingsByUserIdCurrent(Long userId, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.booker as booker" +
            " JOIN b.item as i" +
            " WHERE booker.id = :userId" +
            " AND b.end < :now" +
            " ORDER BY b.start DESC")
    List<Booking> findAllBookingsByUserIdPast(Long userId, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.booker as booker" +
            " JOIN b.item as i" +
            " WHERE booker.id = :userId" +
            " AND b.start > :now" +
            " ORDER BY b.start DESC")
    List<Booking> findAllBookingsByUserIdFuture(Long userId, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.booker as booker" +
            " JOIN b.item as i" +
            " WHERE booker.id = :userId" +
            " AND b.status = 'WAITING'" +
            " ORDER BY b.start DESC")
    List<Booking> findAllBookingsByUserIdWaiting(Long userId, Pageable pageable);

    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.booker as booker" +
            " JOIN b.item as i" +
            " WHERE booker.id = :userId" +
            " AND b.status = 'REJECTED'" +
            " ORDER BY b.start DESC")
    List<Booking> findAllBookingsByUserIdRejected(Long userId, Pageable pageable);

    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.item as i" +
            " JOIN i.owner as o" +
            " WHERE o.id = :userId" +
            " ORDER BY b.start DESC")
    List<Booking> findAllBookingsByOwnerId(Long userId, Pageable pageable);

    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.item as i" +
            " JOIN i.owner as o" +
            " WHERE o.id = :userId" +
            " AND (b.start < :now" +
            " AND b.end > :now)" +
            " ORDER BY b.start DESC")
    List<Booking> findAllBookingsByOwnerIdCurrent(Long userId, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.item as i" +
            " JOIN i.owner as o" +
            " WHERE o.id = :userId" +
            " AND b.end < :now" +
            " ORDER BY b.start DESC")
    List<Booking> findAllBookingsByOwnerIdPast(Long userId, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.item as i" +
            " JOIN i.owner as o" +
            " WHERE o.id = :userId" +
            " AND b.start > :now" +
            " ORDER BY b.start DESC")
    List<Booking> findAllBookingsByOwnerIdFuture(Long userId, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.item as i" +
            " JOIN i.owner as o" +
            " WHERE o.id = :userId" +
            " AND b.status = 'WAITING'" +
            " ORDER BY b.start DESC")
    List<Booking> findAllBookingsByOwnerIdWaiting(Long userId, Pageable pageable);

    @Query(value = "SELECT b" +
            " FROM Booking as b" +
            " JOIN b.item as i" +
            " JOIN i.owner as o" +
            " WHERE o.id = :userId" +
            " AND b.status = 'REJECTED'" +
            " ORDER BY b.start DESC")
    List<Booking> findAllBookingsByOwnerIdRejected(Long userId, Pageable pageable);

}