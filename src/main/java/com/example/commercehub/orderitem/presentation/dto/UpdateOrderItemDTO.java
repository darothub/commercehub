package com.example.commercehub.orderitem.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateOrderItemDTO(
        @NotNull(message = "Item ID is required")
        UUID itemId,
        @NotNull(message = "Order ID is required")
        UUID orderId,
        @NotNull(message = "Product ID is required")
        UUID productId,
        @Min(value = 0, message = "Quantity must be at least 1")
        Integer quantity,
        BigDecimal unitPrice
) {}