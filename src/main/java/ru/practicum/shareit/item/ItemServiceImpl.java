package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.exception.ValidationExceptionCustom;
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

    ItemMapper itemMapper;
    BookingMapper bookingMapper;
    CommentMapper commentMapper;

    public ItemDto create(Long ownerId, @Valid ItemDto itemDto) {
        Optional<User> optionalOwner = userRepository.findById(ownerId);
        if (optionalOwner.isPresent()) {
            User owner = optionalOwner.get();
            Item item = itemMapper.toItem(itemDto, owner);
            return itemMapper.toItemDto(itemRepository.save(item));
        } else {
            throw new EntityNotFoundException("Not found: item owner's id " + ownerId);
        }
    }

    public ItemDto update(ItemDto itemDto, Long itemId, Long ownerId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isPresent()) {
            Optional<User> optionalOwner = userRepository.findById(ownerId);
            if (optionalOwner.isEmpty()) {
                throw new EntityNotFoundException("Not found: owner id: " + ownerId);
            }
            Item item = optionalItem.get();
            User owner = optionalOwner.get();

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
        } else {
            throw new EntityNotFoundException("Not found: item id " + itemDto.getId());
        }
    }


    @Override
    public ItemBookingCommentDto findItemById(Long userId, Long itemId) {

        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new EntityNotFoundException("Not found: item id " + itemId);
        }
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("Not found: owner id: " + userId);
        }
        Item item = optionalItem.get();
        List<CommentDtoOutput> itemComments = commentMapper.commentDtoList(commentRepository.findAllByItem(itemId));
        ItemBookingCommentDto itemDto = itemMapper.toItemDtoBookingComment(item);
        itemDto.setComments(itemComments);
        if (item.getOwner().getId().equals(userId)) {
            List<Booking> bookings = bookingRepository.findAllBookingsByItemId(itemId);
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
    public List<ItemBookingCommentDto> findOwnerItems(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("Not found: user id: " + userId);
        }

        List<Item> userItems = itemRepository.findAllByOwnerIdOrderByIdAsc(userId);
        if (userItems.isEmpty()) {
            return new ArrayList<ItemBookingCommentDto>();
        }

        List<Long> itemsId = userItems.stream().map(Item::getId).collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findAllBookingsByItemIdList(itemsId);

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
        //полностью через стрим сделать не получается, если до .get ничего не доходит - вылетает с ошибкой
        /*return dtoItems.stream()
                .peek(item -> item.setLastBooking(bookings.stream()
                        .filter(booking -> booking.getItem().getId().equals(item.getId()))
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                        .findFirst().map(bookingMapper::toBookingDtoForItemHost).get()))
                .peek(item -> item.setNextBooking(bookings.stream()
                        .filter(booking -> booking.getItem().getId().equals(item.getId()))
                        .filter(x -> x.getStart().isAfter(LocalDateTime.now()))
                        .min(Comparator.comparing(Booking::getStart))
                        .map(bookingMapper::toBookingDtoForItemHost).get()))
                .collect(Collectors.toList());*/
    }

    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.findItemsByTextIgnoreCase(text.toLowerCase());
        return itemMapper.itemDtoList(items);
    }

    @Override
    public CommentDtoOutput addComment(Long userId, Long itemId, CommentDtoInput dtoInput) {

        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new EntityNotFoundException("Not found: item id " + itemId);
        }
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("Not found: user id: " + userId);
        }
        Item item = optionalItem.get();
        User user = optionalUser.get();

        List<Booking> booking = bookingRepository.findAllByBookerIdAndItemIdAndEndBefore(itemId, userId, LocalDateTime.now());

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
