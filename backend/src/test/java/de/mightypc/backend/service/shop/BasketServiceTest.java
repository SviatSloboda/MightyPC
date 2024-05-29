package de.mightypc.backend.service.shop;

import de.mightypc.backend.exception.shop.ItemNotFoundException;
import de.mightypc.backend.model.shop.order.Item;
import de.mightypc.backend.model.shop.user.User;
import de.mightypc.backend.repository.shop.UserRepository;
import de.mightypc.backend.service.shop.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BasketServiceTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserService userService = new UserService(userRepository, passwordEncoder);
    private final BasketService basketService = new BasketService(userService, userRepository);

    private User user;

    @BeforeEach
    void setUp() {
        Item testItem = new Item(
                "itemId",
                "destroyer",
                "pc",
                new BigDecimal(666),
                "",
                ""
        );

        user = new User(
                "testId",
                "testEmail",
                new ArrayList<>(),
                new ArrayList<>(List.of(testItem)),
                new ArrayList<>(),
                true,
                "user",
                "23.32",
                "link"
        );
    }

    @Test
    void getAllItemsOfUserByUserId() {
        // Arrange
        List<Item> expected = new ArrayList<>(List.of(new Item("itemId",
                "destroyer",
                "pc",
                new BigDecimal(666),
                "",
                ""
        )));

        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        // Act
        List<Item> actual = basketService.getAllItemsOfUserByUserId("testId");

        // Assert
        verify(userRepository).findById("testId");
        assertEquals(expected, actual);
    }

    @Test
    void saveItem() {
        // Arrange
        Item testItem = new Item("new", "something", "new", new BigDecimal(333), "", "");

        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        // Act
        basketService.saveItem("testId", testItem);

        // Assert
        verify(userRepository).save(user);
        verify(userRepository).findById("testId");
        assertEquals(2, user.getBasket().size());
    }

    @Test
    void saveItem_shouldSaveProperly_whenUserBasketIsNull() {
        // Arrange
        User specialUser = user.withId("specialId").withBasket(null);
        Item testItem = new Item("new", "something", "new", new BigDecimal(333), "", "");

        when(userRepository.findById("specialId")).thenReturn(Optional.of(specialUser));

        // Act
        basketService.saveItem("specialId", testItem);

        // Assert
        verify(userRepository).save(specialUser);
        verify(userRepository).findById("specialId");
        assertEquals(1, specialUser.getBasket().size());
    }

    @Test
    void getItemById_whenThereIsNoSuchItem_shouldThrowNoSuchItemException() {
        // Arrange
        when(userRepository.findById("testId")).thenReturn(Optional.of(user));
        user.setOrders(new ArrayList<>());

        // Act & Assert
        assertThrows(ItemNotFoundException.class,
                () -> basketService.deleteItemByUserIdAndItemId("testId", "orderId"));

    }

    @Test
    void deleteItem() {
        // Arrange
        when(userRepository.findById("testId")).thenReturn(Optional.of(user)).thenReturn(Optional.of(user));

        // Act
        basketService.deleteItemByUserIdAndItemId("testId", "itemId");

        // Assert
        verify(userRepository).findById("testId");
        verify(userRepository).save(user);
        assertTrue(user.getBasket().isEmpty());
    }

    @Test
    void deleteAll() {
        // Arrange
        when(userRepository.findById("testId")).thenReturn(Optional.of(user)).thenReturn(Optional.of(user));

        // Act
        basketService.deleteAll("testId");

        // Assert
        verify(userRepository).findById("testId");
        verify(userRepository).save(user);
        assertTrue(user.getBasket().isEmpty());
    }

    @Test
    void getEntirePriceOf_shouldCalculatePriceCorrectly() {
        // Arrange
        user.getBasket().add(new Item("new", "something", "new", new BigDecimal(333), "", ""));

        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        // Act
        BigDecimal actual = basketService.getEntirePriceOfBasketByUserId("testId");

        // Assert
        verify(userRepository).findById("testId");
        assertEquals(new BigDecimal(999), actual);
    }

    @Test
    void getItemById_shouldReturnItem_whenItemExistsInBasket() {
        // Arrange
        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        // Act
        Item actualItem = basketService.getItemById("testId", "itemId");

        // Assert
        verify(userRepository).findById("testId");
        assertEquals("itemId", actualItem.id());
        assertEquals("destroyer", actualItem.name());
        assertEquals(new BigDecimal(666), actualItem.price());
    }

    @Test
    void getItemById_shouldThrowItemNotFoundException_whenItemDoesNotExistInBasket() {
        // Arrange
        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(ItemNotFoundException.class, () -> basketService.getItemById("testId", "nonExistingItemId"));
    }
}
