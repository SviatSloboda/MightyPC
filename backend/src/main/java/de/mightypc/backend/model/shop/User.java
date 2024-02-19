package de.mightypc.backend.model.shop;

import de.mightypc.backend.model.pc.PC;

import java.util.List;

public record User(
        String id,
        List<PC> userPcs,
        List<Order> orders
) {
}
