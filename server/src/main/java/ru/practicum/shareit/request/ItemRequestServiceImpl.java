package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    ItemRepository itemRepository;
    UserRepository userRepository;
    ItemRequestRepository itemRequestRepository;
    ItemMapper itemMapper;
    ItemRequestMapper itemRequestMapper;

    @Override
    public ItemRequestDtoOutput create(Long requesterId, ItemRequestDtoInput itemRequestDtoInput) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new EntityNotFoundException("Not found: user id: " + requesterId));
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDtoInput, requester);
        return itemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDtoOutput> findUserRequests(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw (new EntityNotFoundException("Not found: owner id: " + userId));
        }
        int page = from / size;
        Pageable params = PageRequest.of(page, size, Sort.by("created"));
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterId(userId, params);
        return makeRequestsWithItems(requests);
    }

    @Override
    public List<ItemRequestDtoOutput> findAll(Long requesterId, Integer from, Integer size) {
        int page = from / size;
        Pageable params = PageRequest.of(page, size, Sort.by("created"));
        List<ItemRequest> requests = itemRequestRepository.findAllFromOthersWithParams(requesterId, params);
        return makeRequestsWithItems(requests);
    }

    @Override
    public ItemRequestDtoOutput findItemRequestById(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw (new EntityNotFoundException("Not found: owner id: " + userId));
        }
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Not found: request id: " + requestId));
        ItemRequestDtoOutput dtoRequest = itemRequestMapper.toItemRequestDto(itemRequest);

        List<Long> requestsId = new ArrayList<>();
        requestsId.add(requestId);
        List<Item> items = itemRepository.findAllByRequestIdList(requestsId);
        if (items.isEmpty()) {
            return dtoRequest;
        }
        dtoRequest.setItems(itemMapper.toItemResponseDtoList(items));
        return dtoRequest;
    }


    private List<ItemRequestDtoOutput> makeRequestsWithItems(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return new ArrayList<ItemRequestDtoOutput>();
        }
        List<Long> requestsId = requests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        List<ItemRequestDtoOutput> dtoRequestsWithoutItems = itemRequestMapper.toItemRequestDtoList(requests);

        List<Item> items = itemRepository.findAllByRequestIdList(requestsId);
        if (items.isEmpty()) {
            return dtoRequestsWithoutItems;
        }

        List<ItemResponseDto> dtoItems = itemMapper.toItemResponseDtoList(items);

        return dtoRequestsWithoutItems.stream()
                .peek(r -> r.setItems(dtoItems
                        .stream()
                        .filter(i -> i.getRequestId().equals(r.getId()))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
}