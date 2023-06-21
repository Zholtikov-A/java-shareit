package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestRepositoryTest {
    @Autowired
    TestEntityManager entityManager;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    UserRepository userRepository;

    static ItemRequest request = new ItemRequest();
    static User requester;
    static User otherUser;
    static Pageable params = PageRequest.of(0, 1, Sort.by("created"));

    @BeforeEach
    void setUp() {
        requester = new User();
        requester.setName("User requester");
        requester.setEmail("requester.user@mail.com");
        userRepository.save(requester);

        otherUser = new User();
        otherUser.setName("Potential sharer");
        otherUser.setEmail("sharer@mail.com");
        otherUser = userRepository.save(otherUser);

        request = new ItemRequest();
        request.setDescription("Describe item 1");
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
        request = itemRequestRepository.save(request);
    }

    @Test
    public void findAllByRequesterIdSuccess() {
        List<ItemRequest> checkList = itemRequestRepository.findAllByRequesterId(requester.getId(), params);
        assertEquals(request, checkList.get(0));
    }

    @Test
    public void findAllFromOthersSuccess() {
        List<ItemRequest> checkList = itemRequestRepository.findAllFromOthersWithParams(otherUser.getId(), params);
        assertEquals(request, checkList.get(0));
    }
}