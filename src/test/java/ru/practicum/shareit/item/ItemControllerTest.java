package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private final User booker = new User();
    private final User owner = new User();
    private final Item item = new Item();
    private final ItemDto itemDto = new ItemDto(1L, "Item", "Description", true, null);

    private final ItemBookingCommentDto itemBookingCommentDto = new ItemBookingCommentDto(
            1L, "Item", "Description", true, null, null, null);
    private final ItemResponseDto itemResponseDto = new ItemResponseDto(
            1L, "Item", "Description", true, null);

    private final CommentDtoOutput commentDtoOutput = new CommentDtoOutput(
            1L, "Item comment", "User", LocalDateTime.now());

    private final CommentDtoInput commentDtoInput = new CommentDtoInput("Item comment");
    private final List<CommentDtoOutput> comments = new ArrayList<>();


    @BeforeEach
    public void setUp() throws Exception {

        itemDto.setId(1L);
        itemDto.setName("Item");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);

        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");

        booker.setId(2L);
        booker.setName("User");
        booker.setEmail("user@mail.com");

        item.setId(1L);
        item.setName("Item");
        item.setDescription("Item Description");
        item.setOwner(owner);
        item.setAvailable(true);

        itemResponseDto.setId(1L);
        itemResponseDto.setName("Item");
        itemResponseDto.setDescription("Item Description");
        itemResponseDto.setAvailable(true);

        commentDtoOutput.setId(1L);
        commentDtoOutput.setAuthorName("User");
        commentDtoOutput.setText("Comment text");
        commentDtoOutput.setCreated(LocalDateTime
                .of(2023, Month.JUNE, 6, 12, 0, 0));

        itemBookingCommentDto.setId(1L);
        itemBookingCommentDto.setName("Item");
        itemBookingCommentDto.setDescription("Item Description");
        itemBookingCommentDto.setAvailable(true);
        itemBookingCommentDto.setComments(comments);
    }

    @Test
    public void createSuccessful() throws Exception {
        when(itemService.create(anyLong(), any())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())// .isBadRequest() / .isNotFound() and other
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    public void updateSuccessful() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;
        String json = objectMapper.writeValueAsString(itemDto);

        when(itemService.update(any(), anyLong(), anyLong())).thenReturn(itemDto);

        itemDto.setName("ItemUpdate");

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }


    @Test
    public void findItemByIdSuccessful() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;

        when(itemService.findItemById(anyLong(), anyLong())).thenReturn(itemBookingCommentDto);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.comments", is(itemBookingCommentDto.getComments())));
    }

    @Test
    public void findAllSuccessful() throws Exception {
        Long userId = 1L;

        when(itemService.findOwnerItems(any(), anyInt(), anyInt()))
                .thenReturn(List.of(itemBookingCommentDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].comments", is(itemBookingCommentDto.getComments())));
    }

    @Test
    public void searchSuccessful() throws Exception {
        Long userId = 1L;

        when(itemService.search(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search?text=item")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    public void addCommentToItem() throws Exception {
        Long itemId = 1L;

        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(commentDtoOutput);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", booker.getId())
                        .content(objectMapper.writeValueAsString(commentDtoInput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoOutput.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDtoOutput.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDtoOutput.getAuthorName())))
                .andExpect(jsonPath("$.created").value("2023-06-06T12:00:00"));
    }

}
