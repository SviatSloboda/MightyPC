package de.mightypc.backend.model.shop;

import de.mightypc.backend.model.pc.PC;

import java.util.List;
import java.util.UUID;

public record User(
        String id,
        String email,
        List<PC> userPcs,
        List<Order> orders,
        Boolean isGoogle
) {
    public User(String email, List<PC> userPcs, List<Order> orders, Boolean isGoogle){
        this(UUID.randomUUID().toString(), email, userPcs, orders, isGoogle);
    }
}
