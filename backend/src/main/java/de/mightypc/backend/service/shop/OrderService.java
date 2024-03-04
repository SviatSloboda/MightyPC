package de.mightypc.backend.service.shop;

import de.mightypc.backend.exception.shop.OrderNotFoundException;
import de.mightypc.backend.model.shop.order.Item;
import de.mightypc.backend.model.shop.order.Order;
import de.mightypc.backend.model.shop.order.OrderStatus;
import de.mightypc.backend.model.shop.User;
import de.mightypc.backend.model.shop.order.OrderStatusRequest;
import de.mightypc.backend.repository.shop.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class OrderService {
    private final UserService userService;
    private final BasketService basketService;
    private final UserRepository userRepository;

    public OrderService(UserService userService, BasketService basketService, UserRepository userRepository) {
        this.userService = userService;
        this.basketService = basketService;
        this.userRepository = userRepository;
    }

    @Transactional
    public void placeOrder(String userId, List<Item> items) {
        User user = userService.getUserById(userId);

        List<Order> currOrders = user.getOrders();
        Order order = new Order(items, basketService.getEntirePrice(userId), OrderStatus.PENDING);

        if (currOrders.isEmpty()) {
            user.setOrders(new ArrayList<>(Collections.singletonList(order)));
        } else {
            user.getOrders().add(order);
        }

        userRepository.save(user);
    }

    @Transactional
    public void removeOrder(String userId, String orderId) {
        User user = userService.getUserById(userId);

        Order orderToRemove = getOrderById(userId, orderId);

        user.getOrders().remove(orderToRemove);

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Order getOrderById(String userId, String id) {
        User user = userService.getUserById(userId);

        return user.getOrders().stream()
                .filter(order -> order.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new OrderNotFoundException("There is no such order!"));
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders(String userId) {
        User user = userService.getUserById(userId);

        return user.getOrders();
    }

    @Transactional
    public void updateStatus(String userId, String orderId, OrderStatusRequest statusRequest) {
        User user = userService.getUserById(userId);

        Order order = this.getOrderById(userId,orderId);

        user.getOrders().remove(order);

        order.setOrderStatus(statusRequest.orderStatus());

        user.getOrders().add(order);

        userRepository.save(user);
    }

    @Transactional
    public void deleteAll(String userId) {
        User user = userService.getUserById(userId);

        user.setOrders(new ArrayList<>());

        userRepository.save(user);
    }
}

