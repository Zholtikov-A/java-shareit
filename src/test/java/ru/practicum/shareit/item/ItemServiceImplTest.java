package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.exception.ValidationExceptionCustom;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private static final User owner = new User();
    private static final User user = new User();

    private static final ItemRequest request = new ItemRequest();
    private static final ItemDto itemDto = new ItemDto(
            1L, "Item", "Description", true, null);

    private static final ItemDto itemDtoWithRequest = new ItemDto(
            1L, "Item", "Description", true, 1L);
    private static final ItemDto updatedItemDto = new ItemDto(
            1L, "UpdatedItem", "UpdatedDescription", false, null);
    private static final Item item = new Item();
    private static final Item updatedItem = new Item();

    private static final BookingDtoForItemOwner bookingDtoForItemOwner = BookingDtoForItemOwner.builder()
            .id(1L)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(3))
            .bookerId(2L)
            .build();
    private static final ItemBookingCommentDto itemBookingCommentDto = new ItemBookingCommentDto(
            1L, "Item", "Description", true,
            bookingDtoForItemOwner, bookingDtoForItemOwner, new ArrayList<CommentDtoOutput>());

    private static final Booking booking = new Booking();
    private static final Booking lastBooking = new Booking();
    private static final CommentDtoInput commentDtoInput = CommentDtoInput.builder()
            .text("comment")
            .build();

    private static final Comment comment = Comment.builder()
            .id(1L)
            .text(commentDtoInput.getText())
            .item(item)
            .user(user)
            .created(LocalDateTime.now())
            .build();

    private static final CommentDtoOutput commentDtoOutput = CommentDtoOutput.builder()
            .id(1L)
            .text(commentDtoInput.getText())
            .authorName(user.getName())
            .created(comment.getCreated())
            .build();

    List<Booking> bookings = List.of(booking, lastBooking);

    @BeforeAll
    static void prepare() {
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");

        user.setId(2L);
        user.setName("User");
        user.setEmail("user@mail.com");

        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);


        updatedItem.setId(updatedItemDto.getId());
        updatedItem.setName(updatedItemDto.getName());
        updatedItem.setDescription(updatedItemDto.getDescription());
        updatedItem.setAvailable(updatedItemDto.getAvailable());
        updatedItem.setOwner(owner);

        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(3));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.APPROVED);

        lastBooking.setId(2L);
        lastBooking.setStart(LocalDateTime.now().minusDays(3));
        lastBooking.setEnd(LocalDateTime.now().minusDays(1));
        lastBooking.setItem(item);
        lastBooking.setBooker(user);
        lastBooking.setStatus(BookingStatus.APPROVED);

        request.setId(1L);
        request.setRequester(user);
        request.setDescription("Request Description");
    }

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(
                itemRepository, userRepository, bookingRepository,
                commentRepository, itemRequestRepository, itemMapper, bookingMapper, commentMapper);
    }

    @Test
    public void createSuccessful() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));


        when(itemRepository.save(any()))
                .thenReturn(item);

        when(itemMapper.toItemDto(item))
                .thenReturn(itemDto);

        ItemDto returnedItemDto = itemService.create(owner.getId(), itemDto);

        assertNotNull(returnedItemDto);
        assertEquals(itemDto, returnedItemDto);
    }

    @Test
    public void createSuccessfulWithRequest() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));

        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));

        when(itemRepository.save(any()))
                .thenReturn(item);

        when(itemMapper.toItemDto(item))
                .thenReturn(itemDto);

        ItemDto returnedItemDto = itemService.create(owner.getId(), itemDtoWithRequest);

        assertNotNull(returnedItemDto);
        assertEquals(itemDto, returnedItemDto);
    }

    @Test
    public void createFailUserNotFound() {
        assertThrows(EntityNotFoundException.class,
                () -> itemService.create(owner.getId(), itemDto));
    }

    @Test
    public void createFailRequestNotFound() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));

        assertThrows(EntityNotFoundException.class,
                () -> itemService.create(owner.getId(), itemDtoWithRequest));
    }

    @Test
    public void createFailInCauseOfUserNotFound() {
        Long ownerId = 999L;
        doThrow(new EntityNotFoundException("Not found: user id: " + ownerId)).when(userRepository)
                .findById(ownerId);

        assertThatThrownBy(() -> itemService.create(ownerId, itemDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Not found: user id: " + ownerId);
        verify(itemRepository, times(0)).save(any());

    }

    @Test
    public void updateSuccessful() {

        when(userRepository
                        .existsById(anyLong()))
                .thenReturn(true);

        when(itemRepository
                        .findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(itemRepository
                        .save(any()))
                .thenReturn(updatedItem);

        when(itemMapper
                        .toItemDto(updatedItem))
                .thenReturn(updatedItemDto);

        ItemDto returnedItemDto = itemService.update(updatedItemDto, item.getId(), owner.getId());

        assertNotNull(returnedItemDto);
        assertEquals(updatedItemDto, returnedItemDto);
    }

    @Test
    public void updateSuccessfulWithSameItem() {

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(itemRepository.save(any()))
                .thenReturn(item);

        when(itemMapper.toItemDto(item))
                .thenReturn(itemDto);

        ItemDto returnedItemDto = itemService.update(itemDto, item.getId(), owner.getId());

        assertNotNull(returnedItemDto);
        assertEquals(itemDto, returnedItemDto);
    }

    @Test
    public void updateSuccessfulWithEmptyFields() {

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(itemRepository.save(any()))
                .thenReturn(updatedItem);

        when(itemMapper.toItemDto(updatedItem))
                .thenReturn(updatedItemDto);

        updatedItemDto.setName(null);
        updatedItemDto.setDescription(null);
        updatedItemDto.setAvailable(null);

        ItemDto returnedItemDto = itemService.update(updatedItemDto, item.getId(), owner.getId());

        assertNotNull(returnedItemDto);
        assertEquals(updatedItemDto, returnedItemDto);
    }


    @Test
    public void updateFailInCauseOfNotOwner() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(ForbiddenOperationException.class,
                () -> itemService.update(updatedItemDto, item.getId(), user.getId()));

    }


    @Test
    public void updateFailInCauseOfUserNotFound() {
        assertThrows(EntityNotFoundException.class,
                () -> itemService.update(updatedItemDto, item.getId(), user.getId()));
    }

    @Test
    public void updateFailInCauseOfItemNotFound() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        assertThrows(EntityNotFoundException.class,
                () -> itemService.update(updatedItemDto, item.getId(), user.getId()));
    }

    @Test
    public void findItemByIdByUserSuccessful() {

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(new ArrayList<>());

        when(commentMapper.commentDtoList(anyList()))
                .thenReturn(new ArrayList<>());

        when(itemMapper.toItemDtoBookingComment(item))
                .thenReturn(itemBookingCommentDto);

        ItemBookingCommentDto returnedItemDto = itemService.findItemById(user.getId(), item.getId());

        assertNotNull(returnedItemDto);
        assertEquals(itemBookingCommentDto, returnedItemDto);
    }

    @Test
    public void findItemByIdByOwnerSuccessful() {

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(bookingRepository.findByItemId(anyLong()))
                .thenReturn(bookings);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(new ArrayList<>());

        when(commentMapper.commentDtoList(anyList()))
                .thenReturn(new ArrayList<>());

        when(itemMapper.toItemDtoBookingComment(item))
                .thenReturn(itemBookingCommentDto);


        ItemBookingCommentDto returnedItemDto = itemService.findItemById(owner.getId(), item.getId());

        assertNotNull(returnedItemDto);
        assertEquals(itemBookingCommentDto, returnedItemDto);
    }

    @Test
    public void findItemByIdByOwnerSuccessfulWithoutBookings() {

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(bookingRepository.findByItemId(anyLong()))
                .thenReturn(List.of());

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(new ArrayList<>());

        when(commentMapper.commentDtoList(anyList()))
                .thenReturn(new ArrayList<>());

        when(itemMapper.toItemDtoBookingComment(item))
                .thenReturn(itemBookingCommentDto);

        ItemBookingCommentDto returnedItemDto = itemService.findItemById(owner.getId(), item.getId());

        assertNotNull(returnedItemDto);
    }

    @Test
    public void findItemByIdByOwnerSuccessfulWithoutNextBooking() {

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(bookingRepository.findByItemId(anyLong()))
                .thenReturn(List.of(lastBooking));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(new ArrayList<>());

        when(commentMapper.commentDtoList(anyList()))
                .thenReturn(new ArrayList<>());

        when(itemMapper.toItemDtoBookingComment(item))
                .thenReturn(itemBookingCommentDto);

        ItemBookingCommentDto returnedItemDto = itemService.findItemById(owner.getId(), item.getId());

        assertNotNull(returnedItemDto);
    }

    @Test
    public void findItemByIdFailItemNotFound() {

        assertThrows(EntityNotFoundException.class,
                () -> itemService.findItemById(user.getId(), item.getId()));
    }

    @Test
    public void findItemByIdFailUserNotFound() {

        when(itemRepository
                        .findById(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(EntityNotFoundException.class,
                () -> itemService.findItemById(user.getId(), item.getId()));
    }

    @Test
    public void findOwnerItemsSuccessful() {
        Integer from = 0;
        Integer size = 4;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong(), any()))
                .thenReturn(List.of(item));

        when(bookingRepository.findByItemIdList(anyList()))
                .thenReturn(bookings);

        when(itemMapper.toItemDtoBookingCommentList(anyList()))
                .thenReturn(List.of(itemBookingCommentDto));

        when(bookingMapper.toBookingDtoForItemHost(any()))
                .thenReturn(bookingDtoForItemOwner);

        List<ItemBookingCommentDto> returnedItemDto = itemService.findOwnerItems(owner.getId(), from, size);

        assertNotNull(returnedItemDto.get(0));
        assertEquals(itemBookingCommentDto, returnedItemDto.get(0));
    }

    @Test
    public void findOwnerItemsSuccessfulWithoutNextBooking() {
        Integer from = 0;
        Integer size = 4;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong(), any()))
                .thenReturn(List.of(item));

        when(bookingRepository.findByItemIdList(anyList()))
                .thenReturn(List.of(lastBooking));

        when(itemMapper.toItemDtoBookingCommentList(anyList()))
                .thenReturn(List.of(itemBookingCommentDto));

        when(bookingMapper.toBookingDtoForItemHost(any()))
                .thenReturn(bookingDtoForItemOwner);

        List<ItemBookingCommentDto> returnedItemDto = itemService.findOwnerItems(owner.getId(), from, size);

        assertNotNull(returnedItemDto.get(0));
    }

    @Test
    public void findOwnerItemsSuccessfulEmptyItemList() {
        Integer from = 0;
        Integer size = 4;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong(), any()))
                .thenReturn(List.of());

        List<ItemBookingCommentDto> returnedItemDto = itemService.findOwnerItems(owner.getId(), from, size);

        assertNotNull(returnedItemDto);
    }

    @Test
    public void findOwnerItemsSuccessfulEmptyBooking() {
        Integer from = 0;
        Integer size = 4;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong(), any()))
                .thenReturn(List.of(item));

        when(bookingRepository.findByItemIdList(anyList()))
                .thenReturn(List.of());

        when(itemMapper.toItemDtoBookingCommentList(anyList()))
                .thenReturn(List.of(itemBookingCommentDto));

        List<ItemBookingCommentDto> returnedItemDto = itemService.findOwnerItems(owner.getId(), from, size);

        assertNotNull(returnedItemDto.get(0));
    }

    @Test
    public void findOwnerItemsFailUserNotFound() {
        Integer from = 0;
        Integer size = 4;
        assertThrows(EntityNotFoundException.class,
                () -> itemService.findOwnerItems(owner.getId(), from, size));
    }

    @Test
    public void searchSuccessful() {
        String text = "text";
        Integer from = 0;
        Integer size = 4;

        when(itemRepository
                        .findItemsByTextIgnoreCase(anyString(), any()))
                .thenReturn(List.of(item));

        when(itemMapper
                        .itemDtoList(anyList()))
                .thenReturn(List.of(itemDto));

        List<ItemDto> returnedItemDto = itemService.search(text, from, size);

        assertNotNull(returnedItemDto.get(0));
        assertEquals(itemDto, returnedItemDto.get(0));
    }

    @Test
    public void searchSuccessfulEmptyText() {
        String text = "";
        Integer from = 0;
        Integer size = 4;

        List<ItemDto> returnedItemDto = itemService.search(text, from, size);

        assertNotNull(returnedItemDto);
    }

    @Test
    public void addCommentSuccessful() {

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(bookingRepository.findAllCompletedBookingsByBookerIdAndItemId(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        when(commentMapper.dtoToComment(commentDtoInput))
                .thenReturn(comment);

        when(commentRepository.save(comment))
                .thenReturn(comment);

        when(commentMapper.toCommentDto(comment))
                .thenReturn(commentDtoOutput);

        CommentDtoOutput returnedCommentDto = itemService.addComment(user.getId(), item.getId(), commentDtoInput);

        assertNotNull(returnedCommentDto);
        assertEquals(commentDtoOutput, returnedCommentDto);
    }


    @Test
    public void addCommentFailInCauseOfEmptyText() {
        commentDtoInput.setText("");
        assertThrows(ValidationExceptionCustom.class,
                () -> itemService.addComment(user.getId(), item.getId(), commentDtoInput));
    }

    @Test
    public void addCommentFailInCauseNoBooker() {

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));

        when(bookingRepository.findAllCompletedBookingsByBookerIdAndItemId(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of());

        assertThrows(ValidationExceptionCustom.class,
                () -> itemService.addComment(owner.getId(), item.getId(), commentDtoInput));
    }

    @Test
    public void addCommentFailInCauseOfNoUser() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(EntityNotFoundException.class,
                () -> itemService.addComment(owner.getId(), item.getId(), commentDtoInput));
    }


    @Test
    public void addCommentFailInCauseOfNoItem() {
        assertThrows(EntityNotFoundException.class,
                () -> itemService.addComment(owner.getId(), item.getId(), commentDtoInput));
    }
}
