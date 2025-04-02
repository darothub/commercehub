package com.example.commercehub.order.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Order {

    private UUID id;
    private String customerName;
    private String customerEmail;
    private Instant orderDate;
    private OrderStatus status;
}
