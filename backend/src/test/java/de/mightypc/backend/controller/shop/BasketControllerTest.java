package de.mightypc.backend.controller.shop;

import de.mightypc.backend.model.shop.order.Item;
import de.mightypc.backend.model.shop.user.User;
import de.mightypc.backend.repository.shop.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
class BasketControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private final Item item1 = new Item(
            "itemId1",
            "pc",
            "pc",
            "testDescription",
            new BigDecimal("120"),
            new ArrayList<>()
    );

    private final Item item2 = new Item(
            "itemId2",
            "workstation",
            "workstation",
            "testDescription",
            new BigDecimal("1200"),
            new ArrayList<>()
    );

    private final User user = new User(
            "user1",
            "testEmail",
            new ArrayList<>(),
            new ArrayList<>(List.of(item1, item2)),
            new ArrayList<>(),
            true,
            "user",
            "23.32",
            "link"
    );

    @BeforeEach
    void setUp() {
        userRepository.save(user);
    }

    @DirtiesContext
    @Test
    void getAllItemsOfUser_shouldReturnItems() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/basket/{userId}", "user1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)));
    }

    @DirtiesContext
    @Test
    void addItemToBasket_shouldReturnStatusCreated() throws Exception {
        String jsonRequestBody = """
            {
                "id": "item1",
                "type": "Electronics",
                "name": "Laptop",
                "description": "High-end gaming laptop",
                "price": 1200,
                "photos": ["url1", "url2"]
            }
            """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/basket/{userId}", "user1")
                        .contentType("application/json")
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @DirtiesContext
    @Test
    void deleteItemById_shouldDeleteItem() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/basket/{userId}/{itemId}", "user1", "itemId1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DirtiesContext
    @Test
    void clearBasket_shouldClearAllItems() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/basket/{userId}/all", "user1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DirtiesContext
    @Test
    void getEntirePrice_shouldReturnTotalPrice() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/basket/{userId}/price", "user1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("1320"));
    }
}