package com.example.commercehub.order.presentation.dto;

import com.example.commercehub.order.domain.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record UpdateOrderDTO(
        String customerName,
        String customerEmail,
        OrderStatus orderStatus
) {}