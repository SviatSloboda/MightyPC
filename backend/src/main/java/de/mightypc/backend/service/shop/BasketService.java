package de.mightypc.backend.service.shop;

import de.mightypc.backend.exception.shop.ItemNotFoundException;
import de.mightypc.backend.model.shop.order.Item;
import de.mightypc.backend.model.shop.user.User;
import de.mightypc.backend.repository.shop.UserRepository;
import de.mightypc.backend.service.shop.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class BasketService {
    private final UserService userService;
    private final UserRepository userRepository;

    public BasketService(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public List<Item> getAllItemsOfUserByUserId(String userId) {
        User user = userService.getUserById(userId);

        return user.getBasket();
    }

    @Transactional
    public void saveItem(String userId, Item item) {
        User user = userService.getUserById(userId);

        if (user.getBasket() == null || user.getBasket().isEmpty()) {
            user.setBasket(new ArrayList<>(List.of(item)));
        } else {
            List<Item> basket = user.getBasket();
            basket.add(item);
        }

        userRepository.save(user);
    }

    public Item getItemById(String userId, String itemId) {
        User user = userService.getUserById(userId);

        return getItemById(user, itemId);
    }

    public Item getItemById(User user, String itemId) {
        return user.getBasket().stream()
                .filter(item -> item.id().equals(itemId))
                .findAny()
                .orElseThrow(() -> new ItemNotFoundException("There is no such item!"));
    }

    private void deleteItem(User user, String itemId) {
        Item item = getItemById(user, itemId);

        user.getBasket().remove(item);

        userRepository.save(user);
    }

    public void deleteItemByUserIdAndItemId(String userId, String itemId){
        User user = userService.getUserById(userId);

        deleteItem(user, itemId);
    }

    @Transactional
    public void deleteAll(String userId) {
        User user = userService.getUserById(userId);

        user.setBasket(new ArrayList<>());

        userRepository.save(user);
    }

    public BigDecimal getEntirePriceOfBasketByUser(User user) {
        BigDecimal price = BigDecimal.ZERO;

        List<Item> items = user.getBasket();

        if (items != null && !items.isEmpty()) {
            for (Item item : items) {
                price = price.add(item.price());
            }
        }

        return price;
    }

    public BigDecimal getEntirePriceOfBasketByUserId(String userId) {
        User user = userService.getUserById(userId);

        return getEntirePriceOfBasketByUser(user);
    }
}
