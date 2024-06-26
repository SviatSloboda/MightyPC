package de.mightypc.backend.model.shop.user;

import de.mightypc.backend.model.pc.PC;
import de.mightypc.backend.model.shop.order.Item;
import de.mightypc.backend.model.shop.order.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.List;
import java.util.UUID;

@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String email;
    private String password;
    private List<Order> orders;
    private List<Item> basket;
    private List<PC> pcs;
    private Boolean isGoogle;
    private String role;
    private String dateOfAccountCreation;
    private String userPhoto;

    public User(String email, String password, List<Order> orders, List<Item> basket, List<PC> pcs, Boolean isGoogle, String role, String dateOfAccountCreation, String userImage) {
        this(UUID.randomUUID().toString(), password, email, orders, basket, pcs, isGoogle, role, dateOfAccountCreation, userImage);
    }
}
