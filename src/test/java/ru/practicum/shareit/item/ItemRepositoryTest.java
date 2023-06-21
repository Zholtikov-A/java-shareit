package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles(profiles = {"ci,test"})
@DataJpaTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private TestEntityManager entityManager;

    private static final User owner = new User();
    private static final User user = new User();

    private static final ItemRequest itemRequest = new ItemRequest();
    private static final Item item = new Item();
    private static final Item anotherItem = new Item();

    private static final Pageable params = PageRequest.of(0, 4, Sort.by("id"));

    @BeforeAll
    static void prepare() {
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");

        user.setName("User");
        user.setEmail("user@mail.com");

        itemRequest.setDescription("Request for item");
        itemRequest.setRequester(user);

        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);

        anotherItem.setName("SearchItem");
        anotherItem.setDescription("SearchDescription");
        anotherItem.setAvailable(true);
        anotherItem.setOwner(owner);
        anotherItem.setRequest(itemRequest);
    }

    @BeforeEach
    void setUp() {
        userRepository.save(owner);
        userRepository.save(user);

        itemRequestRepository.save(itemRequest);

        itemRepository.save(item);
        itemRepository.save(anotherItem);
    }

    @Test
    void findItemsByTextIgnoreCase() throws Exception {
        String text = "archDescri";
        List<Item> items = itemRepository.findItemsByTextIgnoreCase(text, params);
        assertThat(items.size() == 1).isTrue();
        assertThat(items.get(0).getDescription()).isEqualTo(anotherItem.getDescription());
    }

    @Test
    void findAllByOwnerIdOrderByIdAscSuccessful() throws Exception {
        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(owner.getId(), params);
        assertThat(items.size() == 2).isTrue();
        assertThat(items.get(1).getOwner().getId()).isEqualTo(owner.getId());
    }

    @Test
    void findAllByRequestIdList() throws Exception {
        List<Long> requestId = List.of(itemRequest.getId());
        List<Item> items = itemRepository.findAllByRequestIdList(requestId);
        assertThat(items.size() == 1).isTrue();
        assertThat(items.get(0).getRequest().getId()).isEqualTo(anotherItem.getRequest().getId());
    }

}