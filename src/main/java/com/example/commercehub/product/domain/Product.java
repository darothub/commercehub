package com.example.commercehub.product.domain;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Product {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;

    public Product() {}

    public void validatePrice() {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }
}
