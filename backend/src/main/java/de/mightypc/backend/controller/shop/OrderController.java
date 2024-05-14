package de.mightypc.backend.controller.shop;

import de.mightypc.backend.model.shop.order.Item;
import de.mightypc.backend.model.shop.order.Order;
import de.mightypc.backend.model.shop.order.OrderStatusRequest;
import de.mightypc.backend.service.shop.OrderService;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/api/order/{userId}")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void placeOrder(@PathVariable String userId, @RequestBody List<Item> items) {
        orderService.placeOrder(userId, items);
    }

    @DeleteMapping("/{orderId}")
    public void removeOrder(@PathVariable String userId, @PathVariable String orderId) {
        orderService.removeOrder(userId, orderId);
    }

    @GetMapping("/{orderId}")
    public Order getOrderById(@PathVariable String userId, @PathVariable String orderId) {
        return orderService.getOrderByUserIdAndOrderId(userId, orderId);
    }

    @GetMapping
    public List<Order> getAllOrders(@PathVariable String userId) {
        return orderService.getAllOrdersByUserId(userId);
    }

    @PutMapping("/{orderId}")
    public void updateStatus(@PathVariable String userId, @PathVariable String orderId, @RequestBody OrderStatusRequest status) {
        orderService.updateStatus(userId, orderId, status);
    }

    @DeleteMapping("/all")
    public void deleteAll(@PathVariable String userId) {
        orderService.deleteAll(userId);
    }
}
