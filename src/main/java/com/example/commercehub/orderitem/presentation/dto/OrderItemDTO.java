package com.example.commercehub.orderitem.presentation.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemDTO(
        UUID  id,
        UUID orderId,
        UUID productId,
        Integer quantity,
        BigDecimal unitPrice
) {}

