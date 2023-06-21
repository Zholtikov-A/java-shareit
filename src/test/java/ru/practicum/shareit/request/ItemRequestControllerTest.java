package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationExceptionCustom;
import ru.practicum.shareit.user.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    private final UserDto requester = UserDto.builder()
            .id(1L)
            .name("requester")
            .email("requester.user@mail.com")
            .build();

    private final ItemRequestDtoInput requestDtoInput = ItemRequestDtoInput.builder()
            .description("Request").build();

    private final ItemRequestDtoOutput requestDtoOutput = ItemRequestDtoOutput.builder()
            .id(1L)
            .description("Request")
            .created(LocalDateTime.now())
            .build();

    @Test
    public void createRequestSuccess() throws Exception {
        when(itemRequestService.create(anyLong(), any())).thenReturn(requestDtoOutput);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", requester.getId())
                        .content(mapper.writeValueAsString(requestDtoInput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDtoOutput.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDtoOutput.getDescription())));

    }

    @Test
    public void failCreateRequesterNotFound() throws Exception {
        when(itemRequestService.create(anyLong(), any())).thenThrow(new EntityNotFoundException("Not Found"));
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 999)
                        .content(mapper.writeValueAsString(requestDtoInput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void failCreateHeaderNotFound() throws Exception {
        when(itemRequestService.create(anyLong(), any())).thenThrow(new ValidationExceptionCustom("Bad Request"));
        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDtoInput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void failCreateEmptyDescription() throws Exception {
        requestDtoInput.setDescription("  ");
        when(itemRequestService.create(anyLong(), any())).thenThrow(new ValidationExceptionCustom("Empty description"));
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", requester.getId())
                        .content(mapper.writeValueAsString(requestDtoInput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void returnEmptyListForRequester() throws Exception {
        when(itemRequestService.findUserRequests(anyLong(), anyInt(), anyInt())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", requester.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void findUserRequestsWithZeroParams() throws Exception {
        when(itemRequestService.findUserRequests(anyLong(), anyInt(), anyInt())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests/?from=0&size=0")
                        .header("X-Sharer-User-Id", requester.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void failFindAllForRequesterSizeNegative() throws Exception {
        when(itemRequestService.findUserRequests(anyLong(), anyInt(), anyInt())).thenThrow(new ValidationExceptionCustom("Wrong params"));

        mockMvc.perform(get("/requests/?from=0&size=-1")
                        .header("X-Sharer-User-Id", requester.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void returnEmptyListForAllUsers() throws Exception {
        when(itemRequestService.findAll(anyLong(), anyInt(), anyInt())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", requester.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void findAllForAllUsersWithZeroParams() throws Exception {
        when(itemRequestService.findAll(anyLong(), anyInt(), anyInt())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests/all?from=0&size=0")
                        .header("X-Sharer-User-Id", requester.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void failFindAllForAllUsersSizeNegative() throws Exception {
        when(itemRequestService.findAll(anyLong(), anyInt(), anyInt())).thenThrow(new ValidationExceptionCustom("Wrong params"));

        mockMvc.perform(get("/requests/all?from=0&size=-1")
                        .header("X-Sharer-User-Id", requester.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void findByRequestIdSuccess() throws Exception {
        when(itemRequestService.findItemRequestById(anyLong(), anyLong())).thenReturn(requestDtoOutput);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", requester.getId())
                        .content(mapper.writeValueAsString(requestDtoInput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDtoOutput.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDtoOutput.getDescription())));
    }

    @Test
    public void failFindByIdNoRequest() throws Exception {
        when(itemRequestService.findItemRequestById(anyLong(), anyLong())).thenThrow(new EntityNotFoundException("Not Found"));

        mockMvc.perform(get("/requests/-9")
                        .header("X-Sharer-User-Id", requester.getId()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void failFindByIdUserNotFound() throws Exception {
        when(itemRequestService.findItemRequestById(anyLong(), anyLong())).thenThrow(new EntityNotFoundException("Not Found"));

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", -9))
                .andExpect(status().isNotFound());
    }

}