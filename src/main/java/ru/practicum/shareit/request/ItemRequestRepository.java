package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ItemRequestRepository extends PagingAndSortingRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequesterId(Long requesterId, Pageable pageable);

    @Query(value = "select ir" +
            " from ItemRequest as ir" +
            " where requester.id != :requesterId" +
            " order by ir.created")
    List<ItemRequest> findAllFromOthersWithParams(Long requesterId, Pageable pageable);


}
