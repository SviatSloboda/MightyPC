package de.mightypc.backend.service.shop;

import de.mightypc.backend.exception.shop.OrderNotFoundException;
import de.mightypc.backend.model.shop.order.Item;
import de.mightypc.backend.model.shop.order.Order;
import de.mightypc.backend.model.shop.order.OrderStatus;
import de.mightypc.backend.model.shop.user.User;
import de.mightypc.backend.model.shop.order.OrderStatusRequest;
import de.mightypc.backend.repository.shop.UserRepository;
import de.mightypc.backend.service.shop.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        List<String> photos = new ArrayList<>();

        for (Item item : items) {
            photos.add(item.photo());
        }

        Order order = new Order(items, basketService.getEntirePriceOfBasketByUser(user), OrderStatus.PENDING, photos);

        if (currOrders == null || currOrders.isEmpty()) {
            user.setOrders(new ArrayList<>(List.of(order)));
        } else {
            user.getOrders().add(order);
        }

        userRepository.save(user);
    }

    @Transactional
    public void removeOrder(String userId, String orderId) {
        User user = userService.getUserById(userId);

        Order orderToRemove = getOrderByUserAndOrderId(user, orderId);

        user.getOrders().remove(orderToRemove);

        userRepository.save(user);
    }

    private Order getOrderByUserAndOrderId(User user, String orderId) {
        return user.getOrders().stream()
                .filter(order -> order.getId().equals(orderId))
                .findAny()
                .orElseThrow(() -> new OrderNotFoundException("There is no such order!"));
    }

    @Transactional
    public Order getOrderByUserIdAndOrderId(String userId, String orderId) {
        User user = userService.getUserById(userId);

        return getOrderByUserAndOrderId(user, orderId);
    }

    public List<Order> getAllOrdersByUserId(String userId) {
        User user = userService.getUserById(userId);

        return user.getOrders();
    }

    @Transactional
    public void updateStatus(String userId, String orderId, OrderStatusRequest statusRequest) {
        User user = userService.getUserById(userId);

        Order order = getOrderByUserAndOrderId(user, orderId);

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
