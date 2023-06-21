package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestServiceImplTest {

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    ItemRequestMapper itemRequestMapper;
    @Mock
    ItemMapper itemMapper;

    static UserDto requesterDto;
    static User requesterUser;
    static Optional<User> requesterUserOpt;
    static Optional<ItemRequest> requestOpt;
    static UserDto ownerDto;
    static ItemRequestDtoInput requestDtoInput;
    static ItemRequestDtoOutput requestDtoOutput;
    static ItemRequestDtoOutput requestDtoOutputWithItems;
    static ItemRequest requestModel;
    private static final Item item = new Item();
    final User userOwner = new User();
    static ItemDto itemDtoWithRequest;
    static ItemResponseDto itemResponseDto;

    @BeforeEach
    public void setUp() {
        LocalDateTime now = LocalDateTime.now();

        requesterDto = UserDto.builder()
                .id(1L)
                .name("requester")
                .email("requester.user@mail.com")
                .build();

        requesterUser = new User();
        requesterUser.setId(requesterDto.getId());
        requesterUser.setName(requesterDto.getName());
        requesterUser.setEmail(requesterDto.getEmail());

        requesterUserOpt = Optional.of(requesterUser);


        ownerDto = UserDto.builder()
                .id(2L)
                .name("owner")
                .email("owner.user@mail.com")
                .build();

        requestDtoInput = ItemRequestDtoInput.builder()
                .description("Request").build();

        requestDtoOutput = ItemRequestDtoOutput.builder()
                .id(1L)
                .description(requestDtoInput.getDescription())
                .created(now)
                .build();

        itemDtoWithRequest = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .requestId(1L)
                .build();

        itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .requestId(1L)
                .build();

        requestModel = new ItemRequest();
        requestModel.setId(requestDtoOutput.getId());
        requestModel.setRequester(requesterUser);
        requestModel.setCreated(now);
        requestModel.setDescription(requestDtoInput.getDescription());

        requestOpt = Optional.of(requestModel);

        userOwner.setId(1L);
        userOwner.setName("Owner");
        userOwner.setEmail("owner@mail.com");


        item.setId(1L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(userOwner);
        item.setRequest(requestModel);

        requestDtoOutputWithItems = ItemRequestDtoOutput.builder()
                .id(1L)
                .description(requestDtoInput.getDescription())
                .created(now)
                .items(List.of(itemResponseDto))
                .build();
    }

    @Test
    public void createRequestSuccess() {
        when(userRepository.findById(anyLong())).thenReturn(requesterUserOpt);
        when(itemRequestRepository.save(any())).thenReturn(requestModel);
        when(itemRequestMapper.toItemRequestDto(requestModel)).thenReturn(requestDtoOutput);

        ItemRequestDtoOutput output = itemRequestService.create(requesterDto.getId(), requestDtoInput);
        assertEquals(output.getDescription(), requestDtoOutput.getDescription());
    }

    @Test
    public void failCreateRequesterNotFound() {
        assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.create(requesterDto.getId(), requestDtoInput));
    }

    @Test
    public void findAllForRequesterSuccess() {
        when(userRepository.findById(anyLong())).thenReturn(requesterUserOpt);

        when(itemRequestRepository.findAllByRequesterId(anyLong(), any())).thenReturn(List.of(requestModel));
        when(itemRequestMapper.toItemRequestDtoList(anyList())).thenReturn(List.of(requestDtoOutput));
        when(itemRepository.findAllByRequestIdList(anyList())).thenReturn(List.of(item));
        when(itemMapper.toItemResponseDtoList(anyList())).thenReturn(List.of(itemResponseDto));

        List<ItemRequestDtoOutput> requests = itemRequestService.findUserRequests(requesterDto.getId(), 0, 3);
        assertEquals(requests, List.of(requestDtoOutputWithItems));
    }

    @Test
    public void findAllForRequesterSuccessWithNoItems() {
        when(userRepository.findById(anyLong())).thenReturn(requesterUserOpt);

        when(itemRequestRepository.findAllByRequesterId(anyLong(), any())).thenReturn(List.of(requestModel));
        when(itemRequestMapper.toItemRequestDtoList(anyList())).thenReturn(List.of(requestDtoOutput));
        when(itemRepository.findAllByRequestIdList(anyList())).thenReturn(List.of());

        List<ItemRequestDtoOutput> requests = itemRequestService.findUserRequests(requesterDto.getId(), 0, 3);
        assertEquals(requests, List.of(requestDtoOutput));
    }

    @Test
    public void findAllForRequesterFailUserNotFind() {
        assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.findUserRequests(requesterDto.getId(), 0, 3));
    }

    @Test
    public void findAllSuccess() {

        when(itemRequestRepository.findAllFromOthersWithParams(anyLong(), any())).thenReturn(List.of(requestModel));
        when(itemRequestMapper.toItemRequestDtoList(anyList())).thenReturn(List.of(requestDtoOutput));
        when(itemRepository.findAllByRequestIdList(anyList())).thenReturn(List.of(item));
        when(itemMapper.toItemResponseDtoList(anyList())).thenReturn(List.of(itemResponseDto));

        List<ItemRequestDtoOutput> requests = itemRequestService.findAll(requesterDto.getId(), 0, 3);
        assertEquals(requests, List.of(requestDtoOutputWithItems));
    }

    @Test
    public void findItemRequestByIdSuccess() {
        when(userRepository.findById(anyLong())).thenReturn(requesterUserOpt);
        when(itemRequestRepository.findById(anyLong())).thenReturn(requestOpt);
        when(itemRequestMapper.toItemRequestDto(any())).thenReturn(requestDtoOutput);

        when(itemRepository.findAllByRequestIdList(anyList())).thenReturn(List.of(item));
        when(itemMapper.toItemResponseDtoList(anyList())).thenReturn(List.of(itemResponseDto));

        ItemRequestDtoOutput request = itemRequestService.findItemRequestById(requesterDto.getId(), 1L);
        assertEquals(request, requestDtoOutputWithItems);
    }

    @Test
    public void findItemRequestByIdSuccessWithNoItems() {
        when(userRepository.findById(anyLong())).thenReturn(requesterUserOpt);
        when(itemRequestRepository.findById(anyLong())).thenReturn(requestOpt);
        when(itemRequestMapper.toItemRequestDto(any())).thenReturn(requestDtoOutput);

        when(itemRepository.findAllByRequestIdList(anyList())).thenReturn(List.of());


        ItemRequestDtoOutput request = itemRequestService.findItemRequestById(requesterDto.getId(), 1L);
        assertEquals(request, requestDtoOutput);
    }

    @Test
    public void findItemRequestByIdFailUserNotFound() {
        assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.findItemRequestById(requesterDto.getId(), 1L));
    }

    @Test
    public void findItemRequestByIdFailRequestNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(requesterUserOpt);
        assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.findItemRequestById(requesterDto.getId(), 1L));
    }

}
