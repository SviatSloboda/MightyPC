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
    private final UserService userService = new UserService(userRepository);
    private final BasketService basketService = new BasketService(userService, userRepository);
    private final OrderService orderService = new OrderService(userService, basketService, userRepository);

    private User user;

    @BeforeEach
    void setUp() {
        Item testItem = new Item("itemId", "pc", "destroyer", "something", new BigDecimal(666), Collections.emptyList());
        Order testOrder = new Order("orderId", new ArrayList<>(List.of(testItem)), new BigDecimal(666), OrderStatus.PENDING);

        user = new User("testId", "testEmail", new ArrayList<>(List.of(testOrder)), new ArrayList<>(), new ArrayList<>(), true, "user", "23.32", "link");
    }

    @Test
    void placeOrder() {
        List<Item> items = new ArrayList<>(List.of(new Item("new", "new", "lox", "lox", new BigDecimal(333), Collections.emptyList())));
        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        orderService.placeOrder("testId", items);

        verify(userRepository).findById("testId");
        verify(userRepository).save(user);
        assertEquals(2, user.getOrders().size());
    }

    @Test
    void placeOrder_shouldPlaceOrderProperly_whenOrdersListIsNull() {
        User specialUser = user.withId("specialId").withOrders(null);
        List<Item> items = new ArrayList<>(List.of(new Item("new", "new", "lox", "lox", new BigDecimal(333), Collections.emptyList())));
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
    void  getOrderByUserAndOrderId_whenThereIsNoSuchOrder_shouldOrderNotFoundException(){
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
