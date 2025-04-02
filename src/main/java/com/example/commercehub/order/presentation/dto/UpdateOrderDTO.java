package com.example.commercehub.order.presentation.dto;

import com.example.commercehub.order.domain.OrderStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UpdateOrderDTO(
        String customerName,
        @Email(message = "is invalid")
        String customerEmail,
        @Min(value = 1, message = "must be at least 1")
        BigDecimal totalPrice,
        OrderStatus orderStatus
) {}