package de.mightypc.backend.controller.shop;

import de.mightypc.backend.model.shop.order.Item;
import de.mightypc.backend.service.shop.BasketService;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/basket/{userId}")
public class BasketController {
    private final BasketService basketService;

    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @GetMapping
    public List<Item> getAllItemsOfUser(@PathVariable String userId) {
        return basketService.getAllItemsOfUser(userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@PathVariable String userId, @PathVariable String itemId) {
        basketService.deleteItem(userId, itemId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addItemToBasket(@PathVariable String userId, @RequestBody Item item) {
        basketService.saveItem(userId, item);
    }

    @GetMapping("/price")
    public BigDecimal getEntirePrice(@PathVariable String userId) {
        return basketService.getEntirePrice(userId);
    }

    @DeleteMapping("/all")
    public void clearBasket(@PathVariable String userId) {
        basketService.deleteAll(userId);
    }

}