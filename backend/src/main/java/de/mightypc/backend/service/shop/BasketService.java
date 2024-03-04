package de.mightypc.backend.service.shop;

import de.mightypc.backend.exception.shop.ItemNotFoundException;
import de.mightypc.backend.model.shop.order.Item;
import de.mightypc.backend.model.shop.User;
import de.mightypc.backend.repository.shop.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class BasketService {
    private final UserService userService;
    private final UserRepository userRepository;

    public BasketService(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Item> getAllItemsOfUser(String userId) {
        User user = userService.getUserById(userId);

        return user.getBasket();
    }

    @Transactional
    public void saveItem(String userId, Item item) {
        User user = userService.getUserById(userId);

        if (user.getBasket() == null) {
            user.setBasket(new ArrayList<>(Collections.singletonList(item)));
        } else {
            List<Item> basket = user.getBasket();
            basket.add(item);
        }

        userRepository.save(user);
    }

    private Item getItemById(String userId, String itemId) {
        User user = userService.getUserById(userId);

        return user.getBasket().stream()
                .filter(item -> item.id().equals(itemId))
                .findAny()
                .orElseThrow(() -> new ItemNotFoundException("There is no such item!"));
    }

    @Transactional
    public void deleteItem(String userId, String itemId) {
        User user = userService.getUserById(userId);

        Item item = getItemById(userId, itemId);

        user.getBasket().remove(item);

        userRepository.save(user);
    }

    @Transactional
    public void deleteAll(String userId) {
        User user = userService.getUserById(userId);

        user.setBasket(new ArrayList<>());

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public BigDecimal getEntirePrice(String userId) {
        BigDecimal price = BigDecimal.ZERO;

        List<Item> items = getAllItemsOfUser(userId);

        if (items != null && !items.isEmpty()) {
            for (Item item : items) {
                price = price.add(item.price());
            }
        }

        return price;
    }
}