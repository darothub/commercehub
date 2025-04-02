package com.example.commercehub.order.presentation.dto;

import com.example.commercehub.order.domain.OrderStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CreateOrderDTO(
        @NotBlank(message = "is required")
        String customerName,
        @Email(message = "invalid email")
        String customerEmail,
        double totalPrice,
        OrderStatus status
) {}