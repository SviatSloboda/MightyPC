package de.mightypc.backend.service.shop;

import de.mightypc.backend.exception.shop.OrderNotFoundException;
import de.mightypc.backend.model.shop.order.Item;
import de.mightypc.backend.model.shop.order.Order;
import de.mightypc.backend.model.shop.order.OrderStatus;
import de.mightypc.backend.model.shop.order.OrderStatusRequest;
import de.mightypc.backend.model.shop.user.User;
import de.mightypc.backend.repository.shop.UserRepository;
import de.mightypc.backend.service.shop.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class OrderServiceTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserService userService = new UserService(userRepository, passwordEncoder);
    private final BasketService basketService = new BasketService(userService, userRepository);
    private final OrderService orderService = new OrderService(userService, basketService, userRepository);

    private User user;

    @BeforeEach
    void setUp() {
        Item testItem = new Item("itemId", "pc", "destroyer", new BigDecimal(666), "", "");
        Order testOrder = new Order("orderId", new ArrayList<>(List.of(testItem)), new BigDecimal(666), OrderStatus.PENDING, Collections.emptyList());

        user = new User("testId", "testEmail", new ArrayList<>(List.of(testOrder)), new ArrayList<>(), new ArrayList<>(), true, "user", "23.32", "link");
    }

    @Test
    void placeOrder() {
        List<Item> items = new ArrayList<>(List.of(new Item("new", "new", "something", new BigDecimal(333), "", "")));

        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        orderService.placeOrder("testId", items);

        verify(userRepository).findById("testId");
        verify(userRepository).save(user);
        assertEquals(2, user.getOrders().size());
    }

    @Test
    void placeOrder_shouldPlaceOrderProperly_whenOrdersListIsNull() {
        User specialUser = user.withId("specialId").withOrders(null);
        List<Item> items = new ArrayList<>(List.of(new Item("new", "new", "something", new BigDecimal(333), "", "")));
        when(userRepository.findById("specialId")).thenReturn(Optional.of(specialUser));

        orderService.placeOrder("specialId", items);

        verify(userRepository).findById("specialId");
        verify(userRepository).save(specialUser);
        assertEquals(1, user.getOrders().size());
    }

    @Test
    void removeOrder() {
        when(userRepository.findById("testId")).thenReturn(Optional.of(user)).thenReturn(Optional.of(user));

        orderService.removeOrder("testId", "orderId");

        verify(userRepository).findById("testId");
        verify(userRepository).save(user);
        assertTrue(user.getOrders().isEmpty());
    }

    @Test
    void getOrderByUserIdAndOrderId() {
        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        Order order = orderService.getOrderByUserIdAndOrderId("testId", "orderId");

        verify(userRepository).findById("testId");
        assertNotNull(order);
    }

    @Test
    void getAllOrders() {
        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        List<Order> orders = orderService.getAllOrdersByUserId("testId");

        verify(userRepository).findById("testId");
        assertEquals(1, orders.size());
    }

    @Test
    void getOrderByUserAndOrderId_whenThereIsNoSuchOrder_shouldOrderNotFoundException() {
        // Arrange
        when(userRepository.findById("testId")).thenReturn(Optional.of(user));
        user.setOrders(new ArrayList<>());

        // Act & Assert
        assertThrows(OrderNotFoundException.class,
                () -> orderService.getOrderByUserIdAndOrderId("testId", "orderId"));

    }

    @Test
    void updateStatus() {
        OrderStatusRequest statusRequest = new OrderStatusRequest(OrderStatus.COMPLETED);
        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        orderService.updateStatus("testId", "orderId", statusRequest);

        verify(userRepository).findById("testId");
        verify(userRepository).save(user);
        assertEquals(OrderStatus.COMPLETED, user.getOrders().get(0).getOrderStatus());
    }

    @Test
    void deleteAll() {
        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        orderService.deleteAll("testId");

        verify(userRepository).findById("testId");
        verify(userRepository).save(user);
        assertTrue(user.getOrders().isEmpty());
    }
}
