package de.mightypc.backend.controller.shop;

import de.mightypc.backend.model.shop.order.Item;
import de.mightypc.backend.model.shop.order.Order;
import de.mightypc.backend.model.shop.order.OrderStatus;
import de.mightypc.backend.model.shop.user.User;
import de.mightypc.backend.repository.shop.UserRepository;
import de.mightypc.backend.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class OrderControllerTest {
    private final Item item1 = new Item(
            "itemId1",
            "pc",
            "testDescription",
            new BigDecimal("120"),
            "",
            ""
    );
    private final Item item2 = new Item(
            "itemId2",
            "workstation",
            "testDescription",
            new BigDecimal("1200"),
            "",
            ""
    );

    private final Order order1 = new Order(
            "orderId1",
            List.of(item1),
            new BigDecimal("120"),
            OrderStatus.PENDING,
            Collections.emptyList()
    );

    private final Order order2 = new Order(
            "orderId2",
            List.of(item1, item2),
            new BigDecimal("1320"),
            OrderStatus.PENDING,
            Collections.emptyList()
    );

    private final User user = new User(
            "user1",
            "testEmail",
            "testPassword",
            new ArrayList<>(List.of(order1, order2)),
            new ArrayList<>(List.of(item1, item2)),
            new ArrayList<>(),
            true,
            "user",
            "23.32",
            "link"
    );

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.save(user);
    }

    @DirtiesContext
    @WithMockUser
    @Test
    void placeOrder_shouldReturnStatusCreated() throws Exception {
        String jsonRequestBody = """
                [{
                    "id": "item1",
                    "type": "Electronics",
                    "name": "Laptop",
                    "description": "High-end gaming laptop",
                    "price": 1200,
                    "photos": ["url1", "url2"]
                }]
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/order/{userId}", "user1")
                        .contentType("application/json")
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @DirtiesContext
    @WithMockUser(roles = "ADMIN")
    @Test
    void removeOrder_shouldDeleteOrder() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/order/{userId}/{orderId}", "user1", "orderId1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DirtiesContext
    @WithMockUser
    @Test
    void getOrderById_shouldReturnOrderDetails() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/order/{userId}/{orderId}", "user1", "orderId1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("orderId1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderStatus").value("PENDING"));
    }

    @DirtiesContext
    @WithMockUser
    @Test
    void getAllOrders_shouldReturnOrders() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/order/{userId}", "user1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("orderId1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value("orderId2"));
    }

    @DirtiesContext
    @WithMockUser(roles = "ADMIN")
    @Test
    void updateStatus_shouldUpdateOrderStatus() throws Exception {
        String jsonRequestBody = """
                {
                    "orderStatus": "PAYED"
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.put("/api/order/{userId}/{orderId}", "user1", "orderId1")
                        .contentType("application/json")
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DirtiesContext
    @WithMockUser(roles = "ADMIN")
    @Test
    void deleteAll_shouldRemoveAllOrders() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/order/{userId}/all", "user1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}