package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(Long ownerId);

    @Query(value = "SELECT i " +
            "FROM Item AS i " +
            "WHERE (lower(i.name) LIKE  lower(concat('%', :text,'%')) OR lower(i.description) LIKE lower(concat('%', :text,'%')) ) AND i.available=TRUE")
    List<Item> findItemsByTextIgnoreCase(String text, Pageable pageable);

    @Query("select i from Item as i " +
            "join i.owner as u  " +
            "where i.owner.id = :userId " +
            " group by i.id " +
            " order by i.owner.id desc")
    List<Item> findAllByOwnerIdOrderByIdAsc(Long userId, Pageable pageable);

    @Query("select i from Item as i " +
            " join i.request as r " +
            " where r.id in :requestId ")
    List<Item> findAllByRequestIdList(List<Long> requestId);

}
