package ru.practicum.shareit.request;

import javax.validation.Valid;
import java.util.List;

public interface ItemRequestService {

    ItemRequestDtoOutput create(Long requesterId, @Valid ItemRequestDtoInput itemRequestDtoInput);

    List<ItemRequestDtoOutput> findUserRequests(Long userId, Integer from, Integer size);

    List<ItemRequestDtoOutput> findAll(Long requesterId, Integer from, Integer size);


    ItemRequestDtoOutput findItemRequestById(Long userId, Long requestId);

}
