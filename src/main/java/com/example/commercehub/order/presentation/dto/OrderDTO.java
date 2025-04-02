package com.example.commercehub.order.presentation.dto;

import com.example.commercehub.order.domain.OrderStatus;

import java.time.Instant;
import java.util.UUID;

public record OrderDTO(
        UUID id,
        String customerName,
        String customerEmail,
        double totalPrice,
        Instant orderDate,
        OrderStatus status
) {}