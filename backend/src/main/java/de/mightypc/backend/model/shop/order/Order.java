package de.mightypc.backend.model.shop.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    String id;
    List<Item> items;
    BigDecimal completePrice;
    OrderStatus orderStatus;

    public Order(List<Item> items, BigDecimal completePrice, OrderStatus orderStatus) {
        this(UUID.randomUUID().toString(), items, completePrice, orderStatus);
    }
}

