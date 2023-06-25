package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.exception.ValidationExceptionCustom;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;


import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    ItemRequestRepository itemRequestRepository;

    ItemMapper itemMapper;
    BookingMapper bookingMapper;
    CommentMapper commentMapper;

    public ItemDto create(Long ownerId, @Valid ItemDto itemDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Not found: user id: " + ownerId));
        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new EntityNotFoundException("Not found: request id: " + itemDto.getRequestId()));
        }
        Item item = itemMapper.toItem(itemDto, owner, request);

        return itemMapper.toItemDto(itemRepository.save(item));

    }

    public ItemDto update(ItemDto itemDto, Long itemId, Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw (new EntityNotFoundException("Not found: owner id: " + ownerId));
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Not found: item's id " + itemId));
        itemDto.setId(itemId);

        if (item.getOwner() != null &&
                !item.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenOperationException("Item can be changed only by it's owner!");
        }

        if (itemDto.getName() != null &&
                !itemDto.getName().equals(item.getName())) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null &&
                !itemDto.getDescription().equals(item.getDescription())) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null &&
                !itemDto.getAvailable().equals(item.getAvailable())) {
            item.setAvailable(itemDto.getAvailable());
        }
        return itemMapper.toItemDto(itemRepository.save(item));
    }


    @Override
    public ItemBookingCommentDto findItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Not found: item's id " + itemId));
        if (!userRepository.existsById(userId)) {
            throw (new EntityNotFoundException("Not found: owner id: " + userId));
        }

        List<CommentDtoOutput> itemComments = commentMapper.commentDtoList(commentRepository.findAllByItemId(itemId));
        ItemBookingCommentDto itemDto = itemMapper.toItemDtoBookingComment(item);
        itemDto.setComments(itemComments);
        if (item.getOwner().getId().equals(userId)) {
            List<Booking> bookings = bookingRepository.findByItemId(itemId);
            if (!bookings.isEmpty()) {
                List<Booking> lastBookings = bookings.stream()
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getEnd).reversed())
                        .collect(Collectors.toList());
                if (!lastBookings.isEmpty()) {
                    itemDto.setLastBooking(bookingMapper.toBookingDtoForItemHost(lastBookings.get(0)));
                }

                List<Booking> nextBookings = bookings.stream()
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getStart))
                        .collect(Collectors.toList());
                if (!nextBookings.isEmpty()) {
                    itemDto.setNextBooking(bookingMapper.toBookingDtoForItemHost(nextBookings.get(0)));
                }
            }
        }
        return itemDto;
    }

    @Override
    public List<ItemBookingCommentDto> findOwnerItems(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw (new EntityNotFoundException("Not found: owner id: " + userId));
        }
        int page = from / size;
        Pageable params = PageRequest.of(page, size, Sort.by("id"));

        List<Item> userItems = itemRepository.findAllByOwnerIdOrderByIdAsc(userId, params);
        if (userItems.isEmpty()) {
            return new ArrayList<ItemBookingCommentDto>();
        }

        List<Long> itemsId = userItems.stream().map(Item::getId).collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findByItemIdList(itemsId);

        if (bookings.isEmpty()) {
            return itemMapper.toItemDtoBookingCommentList(userItems);
        }
        List<ItemBookingCommentDto> dtoItems = itemMapper.toItemDtoBookingCommentList(userItems);

        for (ItemBookingCommentDto item : dtoItems) {
            List<Booking> lastBookings = bookings.stream()
                    .filter(booking -> booking.getItem().getId().equals(item.getId()))
                    .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Booking::getEnd))
                    .collect(Collectors.toList());
            if (!lastBookings.isEmpty()) {
                item.setLastBooking(bookingMapper.toBookingDtoForItemHost(lastBookings.get(0)));
            }

            List<Booking> nextBookings = bookings.stream()
                    .filter(booking -> booking.getItem().getId().equals(item.getId()))
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Booking::getStart))
                    .collect(Collectors.toList());
            if (!nextBookings.isEmpty()) {
                item.setNextBooking(bookingMapper.toBookingDtoForItemHost(nextBookings.get(0)));
            }
        }
        return dtoItems;
    }

    public List<ItemDto> search(String text, Integer from, Integer size) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        int page = from / size;
        Pageable params = PageRequest.of(page, size, Sort.by("id"));
        List<Item> items = itemRepository.findItemsByTextIgnoreCase(text, params);
        return itemMapper.itemDtoList(items);
    }

    @Override
    public CommentDtoOutput addComment(Long userId, Long itemId, CommentDtoInput dtoInput) {
        if (dtoInput.getText().isBlank()) {
            throw new ValidationExceptionCustom("Comment can't be empty!");
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Not found: item's id " + itemId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Not found: user id: " + userId));
        List<Booking> booking = bookingRepository.findAllCompletedBookingsByBookerIdAndItemId(
                userId, itemId, LocalDateTime.now());

        if (booking.isEmpty()) {
            throw new ValidationExceptionCustom("User hasn't booked this item");
        }
        Comment comment = commentMapper.dtoToComment(dtoInput);
        comment.setItem(item);
        comment.setUser(user);
        comment.setCreated(LocalDateTime.now());
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

}
